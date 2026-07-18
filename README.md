# Encuestas OXXO - App Android (Kotlin + Jetpack Compose)

## Que trae este primer avance
- Proyecto Gradle listo para abrir en Android Studio (usa KSP, Compose BOM 2024.06, Kotlin 1.9.24)
- Room: entidades y DAOs para cuestionario/pregunta (cache) y encuesta/respuesta_detalle (cola offline con uuid)
- Retrofit: ApiService calcado a los 3 endpoints de `encuestas_web/api`
- SessionManager (DataStore): guarda token + permisos del usuario logueado, sobrevive a cerrar la app
- Login real: pega a `/api/login`, guarda la sesion, y manda a Encuesta o Historial segun el rol (mismo criterio que el panel web)
- Service Locator simple (`AppContainer`) en vez de Hilt

## Como probarlo
1. Abre la carpeta en Android Studio (Hedgehog o mas nuevo).
2. En `app/build.gradle.kts`, revisa `API_BASE_URL` -- por default apunta a
   `http://10.0.2.2/nps/api/`, que es como el EMULADOR de Android ve el
   `localhost` de tu PC. Si pruebas en celular fisico conectado a la
   misma red, cambialo por la IP de tu PC en la red local (ej. `http://192.168.1.50/nps/api/`).
3. Corre la app. Prueba login con alguno de los 3 usuarios que ya tienes en la BD
   (recuerda que la primera vez pide cambiar password -- esa pantalla
   todavia no esta armada, viene en el siguiente avance junto con Encuesta).

## Que sigue (proximo mensaje)
- Pantalla "cambiar password" (primer login / password restablecida)
- Selector negocio/region/plaza/tienda con los defaults
- Pantalla de encuesta: preguntas con estrellas 1-5 + comentario + enviar (guarda en Room con uuid)
- SincronizacionWorker (WorkManager): sube encuestas pendientes cuando hay señal
- Historial para ATI
- Gestionar preguntas / usuarios (ATI + webmaster) del lado movil, si los quieres tambien ahi (en el panel web ya existen)
