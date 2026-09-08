# Publicar en Play + seguir sirviendo APK de auto-actualización

Los dos canales usan **la misma upload key**. Si la firma cambia entre
versiones, Android rechaza la actualización ("app no instalada" / hay que
desinstalar). Genera la llave **una sola vez** y guárdala fuera del repo.

## 1. Generar la upload key (una vez)

```bash
keytool -genkeypair -v -keystore app/upload-keystore.jks \
  -alias upload -keyalg RSA -keysize 2048 -validity 10000
```

Copia `app/keystore.properties.example` a `app/keystore.properties` y pon
las contraseñas reales. Ambos (`*.jks` y `keystore.properties`) están en
`.gitignore`. **Respalda el .jks y las contraseñas** (gestor de
contraseñas / almacenamiento cifrado). Con Play App Signing se puede
resetear la upload key si se pierde, pero es un trámite de días.

## 2. Build para Play (Android App Bundle)

```bash
./gradlew :app:bundleRelease
# -> app/build/outputs/bundle/release/app-release.aab
```

Subir ese `.aab` al track **Prueba interna**. Play lo re-firma con la app
signing key y lo distribuye a los testers que agregues por correo.

## 3. Build del APK para el servidor de auto-actualización

```bash
./gradlew :app:assembleRelease
# -> app/build/outputs/apk/release/app-release.apk
```

Ese APK queda firmado con la **upload key** (misma que el bundle). Súbelo
a `https://fieldserviceplus.alwaysdata.net/nps/public/updates/app-release.apk`
y actualiza `config/version.json` en el backend:

```json
{
  "version_code": 11,
  "version_name": "1.11.0",
  "url": ".../updates/app-release.apk",
  "obligatoria": false,
  "novedades": "..."
}
```

`version_code` debe coincidir con `versionCode` de `app/build.gradle.kts`
y subir en cada release.

## 4. Cada versión nueva

1. Subir `versionCode` y `versionName` en `app/build.gradle.kts`.
2. `bundleRelease` -> subir `.aab` a Play (prueba interna).
3. `assembleRelease` -> subir `.apk` al servidor + editar `version.json`.

## Notas

- **targetSdk / compileSdk 35**: obligatorio para publicar en Play. Se
  activó junto con `android.suppressUnsupportedCompileSdk=35` (AGP 8.5 no
  lo reconoce oficialmente pero compila bien). Quitar ese flag al subir
  AGP a 8.6+.
- **Edge-to-edge**: Android 15 lo fuerza. Por ahora la app usa el opt-out
  temporal `android:windowOptOutEdgeToEdgeEnforcement` en el tema. Antes
  de que salga Android 16 hay que migrar a manejo de insets real y quitar
  el `window.statusBarColor` de `ui/theme/Theme.kt`.
- Sin `keystore.properties`, el `release` cae a **firma debug** (para que
  compile en otras máquinas / CI). Los binarios que se publican tienen
  que salir de una máquina con la llave.
- Data safety en Play: la app envía correo, foto de perfil, ubicación de
  tiendas y token de sesión. Hay que declararlo + URL de política de
  privacidad.
