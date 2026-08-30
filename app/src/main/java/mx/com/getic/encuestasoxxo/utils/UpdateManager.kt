package mx.com.getic.encuestasoxxo.utils

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import mx.com.getic.encuestasoxxo.BuildConfig
import mx.com.getic.encuestasoxxo.data.remote.ApiService
import java.io.File

class UpdateManager(private val context: Context, private val api: ApiService) {

    companion object {
        private const val TAG = "UpdateManager"
    }

    suspend fun checarYDescargar(onUpdateAvailable: (versionName: String, url: String, obligatoria: Boolean, novedades: String) -> Unit) {
        try {
            val response = api.verificarActualizacion()
            val currentVersion = BuildConfig.VERSION_CODE
            
            Log.d(TAG, "Versión actual: $currentVersion, Versión servidor: ${response.version_code}")
            
            if (response.version_code > currentVersion) {
                onUpdateAvailable(response.version_name, response.url, response.obligatoria, response.novedades ?: "")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error verificando actualización: ${e.message}")
        }
    }

    fun descargarEInstalar(url: String) {
        // Usamos una ruta segura y garantizada para DownloadManager
        val destinationFile = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "update.apk")
        if (destinationFile.exists()) destinationFile.delete()

        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle("Actualizando Pulso TI")
            .setDescription("Descargando actualización...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationUri(Uri.fromFile(destinationFile))
            .setMimeType("application/vnd.android.package-archive")
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = downloadManager.enqueue(request)

        Toast.makeText(context, "Iniciando descarga...", Toast.LENGTH_SHORT).show()

        val onComplete = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
                if (id == downloadId) {
                    val query = DownloadManager.Query().setFilterById(downloadId)
                    val cursor = downloadManager.query(query)
                    
                    if (cursor.moveToFirst()) {
                        val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                        val status = cursor.getInt(statusIndex)
                        
                        if (status == DownloadManager.STATUS_SUCCESSFUL) {
                            if (destinationFile.exists() && destinationFile.length() > 0) {
                                Log.d(TAG, "Descarga completada: ${destinationFile.length()} bytes")
                                instalarApk(destinationFile)
                            } else {
                                Log.e(TAG, "Archivo no encontrado o vacío")
                                Toast.makeText(context, "Error: Archivo de actualización inválido", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            val reasonIndex = cursor.getColumnIndex(DownloadManager.COLUMN_REASON)
                            val reason = cursor.getInt(reasonIndex)
                            Log.e(TAG, "Falla en DownloadManager. Razón: $reason")
                            
                            val msg = when(reason) {
                                404 -> "Archivo no encontrado en el servidor (404)"
                                403 -> "Acceso denegado al servidor (403)"
                                else -> "Error de red o servidor ($reason)"
                            }
                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                        }
                    }
                    cursor.close()
                    try { context.unregisterReceiver(this) } catch (e: Exception) {}
                }
            }
        }

        ContextCompat.registerReceiver(
            context,
            onComplete,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
            ContextCompat.RECEIVER_EXPORTED
        )
    }

    private fun instalarApk(file: File) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }

            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Error al lanzar instalador: ${e.message}")
            Toast.makeText(context, "Error al abrir el instalador", Toast.LENGTH_LONG).show()
        }
    }
}
