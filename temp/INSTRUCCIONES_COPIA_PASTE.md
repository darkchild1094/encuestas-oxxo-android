# 📋 INSTRUCCIONES PARA COPIAR ARCHIVOS CORREGIDOS EN ANDROID STUDIO

## RESUMEN DE CAMBIOS
- ✅ **3 archivos nuevos**
- ✅ **6 archivos modificados**
- ✅ **1 archivo de configuración actualizado**

---

## PASO 1: CREAR NUEVO ARCHIVO - AppViewModelFactory.kt

**Ruta:** `app/src/main/java/mx/com/getic/encuestasoxxo/ui/AppViewModelFactory.kt`

1. En Android Studio, click derecho en carpeta `ui` → New → Kotlin File/Class
2. Nombre: `AppViewModelFactory`
3. Pega el contenido de `AppViewModelFactory.kt`
4. Guarda (Ctrl+S)

---

## PASO 2: REEMPLAZAR - NavGraph.kt

**Ruta:** `app/src/main/java/mx/com/getic/encuestasoxxo/ui/navigation/NavGraph.kt`

1. Abre el archivo actual
2. Selecciona TODO (Ctrl+A)
3. Pega el contenido del archivo `NavGraph.kt`
4. Guarda (Ctrl+S)

**Cambios principales:**
- Importa `AppViewModelFactory`
- Usa factory para crear todos los ViewModels
- Ahora pasa `sessionManager` a `HistorialViewModel`

---

## PASO 3: REEMPLAZAR - EncuestaViewModel.kt

**Ruta:** `app/src/main/java/mx/com/getic/encuestasoxxo/ui/encuesta/EncuestaViewModel.kt`

1. Abre el archivo actual
2. Selecciona TODO (Ctrl+A)
3. Pega el contenido de `EncuestaViewModel.kt`
4. Guarda (Ctrl+S)

**Cambios principales:**
- Agregados campos en `EncuestaUiState`: `datosEnCache`, `encuestasPendientes`
- Nuevo método: `reintentarSincronizacion()`
- Observa cantidad de encuestas pendientes en `init`

---

## PASO 4: REEMPLAZAR - HistorialViewModel.kt

**Ruta:** `app/src/main/java/mx/com/getic/encuestasoxxo/ui/historial/HistorialViewModel.kt`

1. Abre el archivo actual
2. Selecciona TODO (Ctrl+A)
3. Pega el contenido de `HistorialViewModel.kt`
4. Guarda (Ctrl+S)

**Cambios principales:**
- Ahora recibe `SessionManager` como parámetro
- Simplificado pero más completo

---

## PASO 5: REEMPLAZAR - EncuestaRepository.kt

**Ruta:** `app/src/main/java/mx/com/getic/encuestasoxxo/data/repository/EncuestaRepository.kt`

1. Abre el archivo actual
2. Selecciona TODO (Ctrl+A)
3. Pega el contenido de `EncuestaRepository.kt`
4. Guarda (Ctrl+S)

**Cambios principales:**
- Nueva clase: `PreguntasResult` (incluye `esCacheado: Boolean`)
- Método `obtenerPreguntas()` retorna `PreguntasResult` en lugar de `Pair`
- Nuevo método: `contarPendientes(): Flow<Int>`
- Agregados logs con Timber

---

## PASO 6: REEMPLAZAR - EncuestaDao.kt

**Ruta:** `app/src/main/java/mx/com/getic/encuestasoxxo/data/local/dao/EncuestaDao.kt`

1. Abre el archivo actual
2. Selecciona TODO (Ctrl+A)
3. Pega el contenido de `EncuestaDao.kt`
4. Guarda (Ctrl+S)

**Cambios principales:**
- Nuevo método: `contarPendientes(): Flow<Int>` (Room Query)

---

## PASO 7: ACTUALIZAR - build.gradle.kts (app level)

**Ruta:** `app/build.gradle.kts`

1. Abre el archivo actual
2. Selecciona TODO (Ctrl+A)
3. Pega el contenido de `build.gradle.kts`
4. Guarda (Ctrl+S)

**Cambios principales:**
- Agregadas dependencias:
  - `com.jakewharton.timber:timber:5.0.1` (logging)
  - `org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1` (explícito)

**⚠️ IMPORTANTE:** Después de pegar, Android Studio te pedirá sincronizar Gradle
- Haz clic en "Sync Now" en la notificación azul

---

## PASO 8: REEMPLAZAR - EncuestasOxxoApp.kt

**Ruta:** `app/src/main/java/mx/com/getic/encuestasoxxo/EncuestasOxxoApp.kt`

1. Abre el archivo actual
2. Selecciona TODO (Ctrl+A)
3. Pega el contenido de `EncuestasOxxoApp.kt`
4. Guarda (Ctrl+S)

**Cambios principales:**
- Inicializa Timber en `onCreate()`
- Agrega import: `import timber.log.Timber`

---

## PASO 9: VERIFICACIÓN DE COMPILACIÓN

1. Build → Clean Project (Ctrl+Shift+F5)
2. Build → Rebuild Project (o presiona Ctrl+Shift+B)
3. Espera a que compile...

**¿Hay errores?** 
- ✅ Si ves errores de import no encontrado, presiona Alt+Enter para importar automáticamente
- ✅ Si ves "class not found", verifica que los archivos estén en las rutas correctas

---

## PASO 10: CAMBIOS ADICIONALES NECESARIOS (Solo si lo necesitas)

### Si usas OkHttp Interceptor para 401 (OPCIONAL - Avanzado)

Si quieres manejar automáticamente token expirado, crea:

**Nuevo archivo:** `app/src/main/java/mx/com/getic/encuestasoxxo/data/remote/AuthInterceptor.kt`

```kotlin
package mx.com.getic.encuestasoxxo.data.remote

import okhttp3.Interceptor
import okhttp3.Response
import mx.com.getic.encuestasoxxo.data.SessionManager
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class AuthInterceptor(private val sessionManager: SessionManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        
        // Si recibimos 401, la sesión expiró
        if (response.code == 401) {
            Timber.w("Token expirado (401), cerrando sesión")
            runBlocking {
                sessionManager.cerrarSesion()
            }
        }
        
        return response
    }
}
```

Luego actualiza `RetrofitClient.kt` para usarlo (requiere pasar `SessionManager`).

---

## CHECKLIST FINAL

Después de copiar todos los archivos, verifica:

- [ ] AppViewModelFactory.kt existe en `ui/`
- [ ] NavGraph.kt importa `AppViewModelFactory`
- [ ] EncuestaViewModel.kt tiene campos nuevos (`datosEnCache`, `encuestasPendientes`)
- [ ] HistorialViewModel.kt recibe `SessionManager` en constructor
- [ ] EncuestaRepository.kt retorna `PreguntasResult` (no Pair)
- [ ] EncuestaDao.kt tiene método `contarPendientes()`
- [ ] build.gradle.kts incluye Timber y coroutines
- [ ] EncuestasOxxoApp.kt inicializa Timber
- [ ] Gradle se sincroniza sin errores
- [ ] Proyecto compila sin errores

---

## PRÓXIMOS PASOS (Después de copiar)

1. **Actualiza EncuestaScreen.kt** para mostrar indicador de offline:
   ```kotlin
   if (estado.datosEnCache) {
       Surface(color = MaterialTheme.colorScheme.warningContainer) {
           Text("ℹ️ Modo offline")
       }
   }
   ```

2. **Agrega botón de reintentar sincronización** en alguna pantalla

3. **Revisa HistorialScreen.kt** para ver si necesita cambios (ahora puede acceder a SessionManager si lo necesita)

4. **Prueba login → encuesta → offline → online** para verificar que funciona

---

## TROUBLESHOOTING

### Error: "AppViewModelFactory not found"
→ Verifica que creaste el archivo en `ui/AppViewModelFactory.kt` (no en otro lado)

### Error: "timber.log.Timber not found"
→ Gradle no sincronizó. Haz clic en "Sync Now" después de actualizar build.gradle.kts

### Error: "PreguntasResult is not found"
→ Verifica que copiaste completo el EncuestaRepository.kt (incluye la data class)

### Error: "contarPendientes not found in EncuestaDao"
→ Verifica que agregaste el método @Query en EncuestaDao.kt

---

## CONTACTO PARA DUDAS

Si tienes dudas específicas de tu setup:
- Verifica las rutas de paquetes en tu proyecto (puede variar el prefijo `mx.com.getic`)
- Compara imports con tus archivos actuales
- Usa "Find and Replace" (Ctrl+H) si necesitas cambiar nombres de paquetes

