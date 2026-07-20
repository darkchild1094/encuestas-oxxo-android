# 📦 ÍNDICE DE ARCHIVOS PARA ANDROID STUDIO

**Generado:** 2026-07-18  
**Proyecto:** encuestas-oxxo-android  
**Total de archivos:** 12 (8 de código + 4 de documentación)

---

## 🔴 ARCHIVOS DE CÓDIGO LISTOS PARA COPIAR

### 1️⃣ NUEVO ARCHIVO - AppViewModelFactory.kt
- **Tamaño:** 1.5 KB
- **Ubicación en AS:** `app/src/main/java/mx/com/getic/encuestasoxxo/ui/AppViewModelFactory.kt`
- **Acción:** CREAR NUEVO
- **Descripción:** Factory pattern para crear ViewModels con dependencias
- **Soluciona:** Incongruencia #1 (sin ViewModelFactory)

**Pasos en AS:**
1. Click derecho en carpeta `ui/` 
2. New → Kotlin Class → `AppViewModelFactory`
3. Copia el contenido

---

### 2️⃣ REEMPLAZAR - NavGraph.kt
- **Tamaño:** 9.0 KB
- **Ubicación en AS:** `app/src/main/java/mx/com/getic/encuestasoxxo/ui/navigation/NavGraph.kt`
- **Acción:** REEMPLAZAR COMPLETAMENTE
- **Descripción:** Usa AppViewModelFactory para crear ViewModels
- **Soluciona:** Incongruencia #1 (sin factory)

**Cambios:**
- Import: `import mx.com.getic.encuestasoxxo.ui.AppViewModelFactory`
- Todas las líneas de `viewModel { ... }` ahora usan factory
- `HistorialViewModel` recibe `container.sessionManager`

---

### 3️⃣ REEMPLAZAR - EncuestaViewModel.kt
- **Tamaño:** 8.1 KB
- **Ubicación en AS:** `app/src/main/java/mx/com/getic/encuestasoxxo/ui/encuesta/EncuestaViewModel.kt`
- **Acción:** REEMPLAZAR COMPLETAMENTE
- **Descripción:** Observa cambios de sesión y muestra estado de cache
- **Soluciona:** Incongruencia #2 (sesion/sessionmanager), #6 (offline), #7 (pendientes)

**Cambios:**
- Nueva clase de estado: `EncuestaUiState` con campos:
  - `datosEnCache: Boolean = false`
  - `encuestasPendientes: Int = 0`
- Nuevo método: `reintentarSincronizacion()`
- Observa: `repository.contarPendientes()`
- Retorna: `PreguntasResult` (con flag `esCacheado`)

---

### 4️⃣ REEMPLAZAR - HistorialViewModel.kt
- **Tamaño:** 1.5 KB
- **Ubicación en AS:** `app/src/main/java/mx/com/getic/encuestasoxxo/ui/historial/HistorialViewModel.kt`
- **Acción:** REEMPLAZAR COMPLETAMENTE
- **Descripción:** Recibe SessionManager para acceso a datos de sesión
- **Soluciona:** Incongruencia #3 (HistorialViewModel incompleto)

**Cambios:**
- Ahora recibe: `sessionManager: SessionManager`
- Nuevo método: `reintentar()`

---

### 5️⃣ REEMPLAZAR - EncuestaRepository.kt
- **Tamaño:** 6.5 KB
- **Ubicación en AS:** `app/src/main/java/mx/com/getic/encuestasoxxo/data/repository/EncuestaRepository.kt`
- **Acción:** REEMPLAZAR COMPLETAMENTE
- **Descripción:** Retorna información de cache y cuenta pendientes
- **Soluciona:** Incongruencia #4 (UUID), #6 (offline), #7 (pendientes)

**Cambios:**
- Nueva clase: `PreguntasResult(cuestionario, preguntas, esCacheado: Boolean)`
- `obtenerPreguntas()` retorna `PreguntasResult?` en lugar de `Pair`
- Nuevo método: `contarPendientes(): Flow<Int>`
- Agregados logs con Timber en operaciones clave

---

### 6️⃣ REEMPLAZAR - EncuestaDao.kt
- **Tamaño:** 1.1 KB
- **Ubicación en AS:** `app/src/main/java/mx/com/getic/encuestasoxxo/data/local/dao/EncuestaDao.kt`
- **Acción:** REEMPLAZAR COMPLETAMENTE
- **Descripción:** Nuevo query para contar encuestas pendientes
- **Soluciona:** Incongruencia #7 (sin visibilidad de pendientes)

**Cambios:**
- Nuevo método: `contarPendientes(): Flow<Int>`
- Query: `SELECT COUNT(*) FROM encuesta WHERE sincronizado = 0`

---

### 7️⃣ REEMPLAZAR - build.gradle.kts (app level)
- **Tamaño:** 2.9 KB
- **Ubicación en AS:** `app/build.gradle.kts`
- **Acción:** REEMPLAZAR COMPLETAMENTE
- **Descripción:** Agrega dependencias de Timber y Coroutines explícitas
- **Soluciona:** Incongruencia #9 (sin logging)

**Cambios:**
- Nueva dependencia: `com.jakewharton.timber:timber:5.0.1`
- Nueva dependencia: `org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1`

**⚠️ IMPORTANTE:** 
Después de pegar, Android Studio mostrará "Sync Now" 
→ **HAZ CLIC EN "Sync Now"** y espera a que compile

---

### 8️⃣ REEMPLAZAR - EncuestasOxxoApp.kt
- **Tamaño:** 438 bytes
- **Ubicación en AS:** `app/src/main/java/mx/com/getic/encuestasoxxo/EncuestasOxxoApp.kt`
- **Acción:** REEMPLAZAR COMPLETAMENTE
- **Descripción:** Inicializa Timber para logging en debug
- **Soluciona:** Incongruencia #9 (sin logging)

**Cambios:**
- Import: `import timber.log.Timber`
- En `onCreate()`: inicializa Timber en modo DEBUG

---

## 📚 ARCHIVOS DE DOCUMENTACIÓN

### 📖 INSTRUCCIONES_COPIA_PASTE.md (7 KB)
**Guía paso-a-paso detallada** para copiar cada archivo en Android Studio
- Instrucciones exactas para cada archivo
- Checklist de compilación
- Troubleshooting con soluciones

**→ LEE ESTO PRIMERO para copiar los archivos**

---

### 📊 CAMBIOS_RESUMEN.txt (7.8 KB)
**Resumen visual** de todos los cambios
- Tabla de archivos a copiar
- Problemas que se arreglan
- Orden de copia recomendado
- Verificaciones finales

**→ REFERENCIA RÁPIDA mientras copias**

---

### 🔍 INCONGRUENCIAS_ANALISIS.md (8 KB)
**Análisis técnico** de cada incongruencia encontrada
- 12 problemas identificados (3 críticos, 4 moderados, 5 minor)
- Impacto de cada problema
- Código mostrando el problema

**→ PARA ENTENDER por qué se hacen estos cambios**

---

### 💡 SOLUCIONES_CODIGO.md (16 KB)
**Ejemplos de código** para cada solución
- ViewModelFactory completo
- Interceptor de auth
- Manejo de offline
- Enum para Roles
- Inicialización de Timber

**→ REFERENCIA TÉCNICA para soluciones alternativas**

---

### 📈 RESUMEN_VISUAL.txt (27 KB)
**Diagramas ASCII y tablas** mostrando:
- Flujo de datos actual vs corregido
- Incongruencias visualizadas
- Checklist de acciones
- Mapas conceptuales

**→ PARA ENTENDER visualmente la arquitectura**

---

## ✅ CHECKLIST DE COPIA EN ORDEN

```
PASO 1: Lee CAMBIOS_RESUMEN.txt (este archivo)
PASO 2: Lee INSTRUCCIONES_COPIA_PASTE.md (instrucciones detalladas)

PASO 3: Copia archivos EN ESTE ORDEN:
   ☐ build.gradle.kts        (PRIMERO - agrega dependencias)
   ☐ AppViewModelFactory.kt   (nuevo archivo)
   ☐ EncuestaDao.kt           (DAO)
   ☐ EncuestaRepository.kt    (Repository)
   ☐ EncuestasOxxoApp.kt      (App)
   ☐ EncuestaViewModel.kt     (ViewModel)
   ☐ HistorialViewModel.kt    (ViewModel)
   ☐ NavGraph.kt              (ÚLTIMO - importa todo)

PASO 4: Build → Clean Project
PASO 5: Build → Rebuild Project
PASO 6: Verifica que no haya errores rojos

PASO 7: (OPCIONAL) Lee INCONGRUENCIAS_ANALISIS.md para entender qué se arregló
```

---

## 📋 TABLA DE CAMBIOS RÁPIDA

| Archivo | Tipo | Cambio | Ubicación |
|---------|------|--------|-----------|
| AppViewModelFactory.kt | ✅ NUEVO | Factory para ViewModels | `ui/` |
| NavGraph.kt | 🔄 REPLACE | Usa factory | `ui/navigation/` |
| EncuestaViewModel.kt | 🔄 REPLACE | Agrega cache + pendientes | `ui/encuesta/` |
| HistorialViewModel.kt | 🔄 REPLACE | Recibe SessionManager | `ui/historial/` |
| EncuestaRepository.kt | 🔄 REPLACE | Retorna PreguntasResult | `data/repository/` |
| EncuestaDao.kt | 🔄 REPLACE | Agrega contarPendientes() | `data/local/dao/` |
| build.gradle.kts | 🔄 REPLACE | Agrega Timber | `app/` |
| EncuestasOxxoApp.kt | 🔄 REPLACE | Inicializa Timber | `` |

---

## 🎯 PROBLEMAS RESUELTOS

| # | Problema | Archivo | Solución |
|---|----------|---------|----------|
| 1 | Sin ViewModelFactory | AppViewModelFactory.kt + NavGraph.kt | Factory pattern |
| 2 | Sesion vs SessionManager | EncuestaViewModel.kt | Observa SessionManager |
| 3 | HistorialViewModel incompleto | HistorialViewModel.kt | Recibe SessionManager |
| 4 | UUID redundante en respuestas | EncuestaRepository.kt | Documentado (no hay cambio) |
| 5 | Sin manejo de 401 | — | Pendiente (opcional) |
| 6 | Sin indicador offline | EncuestaViewModel.kt + EncuestaRepository.kt | Flag esCacheado |
| 7 | Sin estado de sincronización | EncuestaViewModel.kt + EncuestaRepository.kt | Flow<Int> contarPendientes |
| 8 | Sin timeout en sesionActualBloqueante | — | Pendiente (lower priority) |
| 9 | Sin logging | EncuestaRepository.kt + build.gradle.kts | Timber |
| 10 | Magic strings de rutas | — | Pendiente (refactor) |

---

## ⚡ TIEMPO ESTIMADO

- **Copiar archivos:** 10-15 minutos
- **Compilar:** 2-3 minutos (depende de tu máquina)
- **Verificar:** 5 minutos
- **TOTAL:** ~20-25 minutos

---

## 🚀 DESPUÉS DE COPIAR

1. **Prueba login**
   ```
   Usuario: (tu usuario)
   Password: (tu password)
   ```

2. **Prueba encuesta sin internet**
   ```
   Activar modo avión
   Selecciona tienda → Debería mostrar "ℹ️ Modo offline"
   Contesta preguntas → Debería funcionar
   Envía → Se queda pendiente
   ```

3. **Prueba con internet de vuelta**
   ```
   Desactiva modo avión
   Debería sincronizar automáticamente
   O haz clic en botón "Reintentar Sincronización"
   ```

---

## 📞 CONTACTO TÉCNICO

Si tienes dudas:

1. **Import no encontrado:**
   - Presiona `Alt+Enter` para auto-import
   - O verifica ruta de paquetes (puede variar tu proyecto)

2. **Error de compilación:**
   - Consulta TROUBLESHOOTING en INSTRUCCIONES_COPIA_PASTE.md

3. **Gradle no sincroniza:**
   - Haz clic en "Sync Now" (notificación azul)
   - Espera a que compile

---

**¡Listo para copiar!** 🚀

