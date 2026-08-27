package mx.com.getic.encuestasoxxo.domain

import android.util.Log
import mx.com.getic.encuestasoxxo.data.local.AppDatabase
import mx.com.getic.encuestasoxxo.data.local.entities.*
import mx.com.getic.encuestasoxxo.data.remote.ApiService
import mx.com.getic.encuestasoxxo.data.remote.dto.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class GeneralSyncManager(
    private val db: AppDatabase,
    private val api: ApiService
) {
    companion object {
        private const val TAG = "GeneralSyncManager"
    }

    suspend fun sincronizarTodo(token: String, plazaIdUsuario: Int? = null) = coroutineScope {
        Log.d(TAG, "Iniciando sincronización completa...")
        val authHeader = "Bearer $token"

        try {
            // 1. Negocios
            val negociosDto = api.negocios(authHeader)
            db.catalogoDao().borrarNegocios()
            db.catalogoDao().guardarNegocios(negociosDto.map { 
                NegocioEntity(it.id, it.nombre, it.es_default) 
            })

            // 2. Roles
            try {
                val rolesDto = api.obtenerRoles(authHeader)
                db.catalogoDao().borrarRoles()
                db.catalogoDao().guardarRoles(rolesDto.map { 
                    RolEntity(it.id, it.nombre) 
                })
            } catch (e: Exception) {
                 Log.w(TAG, "No se pudieron sincronizar roles")
            }

            // 3. Usuarios (si es admin)
            try {
                val usuariosDto = api.obtenerUsuarios(authHeader)
                db.usuarioDao().borrarUsuarios()
                db.usuarioDao().guardarUsuarios(usuariosDto.map { u ->
                    UsuarioEntity(
                        id = u.id,
                        correo = u.correo,
                        nombreCompleto = u.nombre_completo,
                        fotoPerfil = u.foto_perfil,
                        genero = u.genero,
                        plazaId = u.plaza_id,
                        plazaNombre = u.plaza_nombre,
                        rol = u.rol,
                        gestionaPreguntas = u.gestiona_preguntas,
                        gestionaUsuarios = u.gestiona_usuarios,
                        esEncuestable = u.es_encuestable,
                        veResultadosTiendas = u.ve_resultados_tiendas
                    )
                })
            } catch (e: Exception) {
                Log.w(TAG, "No se pudieron sincronizar usuarios (posible falta de permisos)")
            }

            // 4. Regiones, Plazas y Tiendas
            if (plazaIdUsuario != null) {
                sincronizarRamaPlaza(authHeader, plazaIdUsuario)
            } else {
                negociosDto.map { negocio ->
                    async {
                        val regiones = try { api.regiones(authHeader, negocio.id) } catch(e: Exception) { emptyList() }
                        db.catalogoDao().guardarRegiones(regiones.map { 
                            RegionEntity(it.id, negocio.id, it.nombre, it.cr, it.es_default) 
                        })
                        
                        regiones.map { region ->
                            async {
                                val plazas = try { api.plazas(authHeader, region.id) } catch(e: Exception) { emptyList() }
                                db.catalogoDao().guardarPlazas(plazas.map { 
                                    PlazaEntity(it.id, region.id, it.nombre, it.cr, it.es_default) 
                                })
                                
                                plazas.map { plaza ->
                                    async {
                                        sincronizarRamaPlaza(authHeader, plaza.id)
                                    }
                                }.awaitAll()
                            }
                        }.awaitAll()
                    }
                }.awaitAll()
            }

            Log.d(TAG, "Sincronización completa finalizada con éxito")
        } catch (e: Exception) {
            Log.e(TAG, "Error durante la sincronización: ${e.message}")
            throw e
        }
    }

    private suspend fun sincronizarRamaPlaza(authHeader: String, plazaId: Int) {
        // Tiendas
        try {
            Log.d(TAG, "Descargando tiendas para plaza $plazaId...")
            val tiendas = api.tiendas(authHeader, plazaId)
            Log.d(TAG, "API devolvió ${tiendas.size} tiendas para plaza $plazaId")
            
            db.tiendaDao().borrarDe(plazaId)
            db.tiendaDao().guardar(tiendas.map { t ->
                TiendaEntity(
                    id = t.id,
                    plazaId = plazaId,
                    nombre = t.nombre,
                    codigo = t.codigo,
                    direccion = t.direccion,
                    latitud = t.latitud,
                    longitud = t.longitud,
                    atiUsuarioId = t.ati_usuario_id,
                    atiNombre = t.ati_nombre,
                    atiFoto = t.ati_foto,
                    atiGenero = t.ati_genero
                )
            })
        } catch (e: Exception) {
            Log.w(TAG, "Error sincronizando tiendas para plaza $plazaId")
        }

        // Cuestionario y Preguntas
        try {
            val response = api.obtenerCuestionario(authHeader, plazaId)
            val cuestDto = response.cuestionario
            if (cuestDto != null) {
                db.cuestionarioDao().guardar(CuestionarioEntity(
                    id = cuestDto.id,
                    plazaId = plazaId,
                    nombre = cuestDto.nombre
                ))
                
                db.cuestionarioDao().borrarPreguntasDe(cuestDto.id)
                db.cuestionarioDao().guardarPreguntas(response.preguntas.map { p ->
                    PreguntaEntity(p.id, cuestDto.id, p.texto, p.orden)
                })
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error sincronizando cuestionario para plaza $plazaId: ${e.message}")
        }
        
        // ATIs disponibles
        try {
            val atis = api.atisDisponibles(authHeader, plazaId)
            db.atiDao().borrarDe(plazaId)
            db.atiDao().guardar(atis.map { a ->
                AtiEntity(plazaId, a.id, a.nombre_completo, a.foto_perfil, a.genero)
            })
        } catch (e: Exception) {
            Log.w(TAG, "Error sincronizando ATIs para plaza $plazaId: ${e.message}")
        }
    }
}
