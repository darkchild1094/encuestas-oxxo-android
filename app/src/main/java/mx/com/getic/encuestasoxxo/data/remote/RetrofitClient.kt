package mx.com.getic.encuestasoxxo.data.remote

import mx.com.getic.encuestasoxxo.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    val api: ApiService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        val cliente = OkHttpClient.Builder()
            .addInterceptor(logging)
            // Timeouts explicitos: sin esto, OkHttp usa 10s por
            // default para las 3 fases. Se sube un poco el de
            // lectura/escritura pensando en señal debil de campo
            // (el escenario real de uso: dentro de una tienda con
            // datos moviles regulares), sin dejar que una conexion
            // realmente caida se quede colgada para siempre.
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            // Reintenta automaticamente si la conexion se cae a medio
            // request (comun con datos moviles cambiando de torre/red)
            .retryOnConnectionFailure(true)
            .build()

        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(cliente)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
