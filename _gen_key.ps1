# Genera la upload key para Play App Signing.
# Ejecutar desde la raiz del proyecto:
#   powershell -NoProfile -ExecutionPolicy Bypass -File .\_gen_key.ps1
# Te pedira la contrasena del keystore (dos veces). GUARDALA en un gestor
# de contrasenas junto con una copia de app\upload-keystore.jks.

$ErrorActionPreference = "Stop"
$keytool = "C:\Program Files\Android\Android Studio\jbr\bin\keytool.exe"
$ks = "app\upload-keystore.jks"

if (-not (Test-Path "app\build.gradle.kts")) {
    Write-Host "ERROR: ejecuta esto desde la raiz del proyecto (encuestas_android)." -ForegroundColor Red
    exit 1
}
if (Test-Path $ks) {
    Write-Host "Ya existe $ks. Si quieres regenerarlo, borralo primero:" -ForegroundColor Yellow
    Write-Host "  Remove-Item $ks" -ForegroundColor Yellow
    exit 1
}

& $keytool -genkeypair -v -keystore $ks -alias upload -keyalg RSA -keysize 2048 -validity 10000 -dname "CN=Raul Huerta Aguilar, OU=kernel94, O=kernel94, L=Ciudad Valles, ST=San Luis Potosi, C=MX"

if (Test-Path $ks) {
    $size = (Get-Item $ks).Length
    Write-Host ""
    Write-Host "OK -> $ks creado ($size bytes)." -ForegroundColor Green
    Write-Host "Respalda ese archivo + la contrasena FUERA del repo." -ForegroundColor Green
} else {
    Write-Host "FALLO: no se creo el keystore." -ForegroundColor Red
    exit 1
}
