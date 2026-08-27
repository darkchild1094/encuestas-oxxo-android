# Añadir sección de Tiendas para Webmaster y ATI

Este plan describe la implementación de una nueva sección en la aplicación para que los usuarios con roles `WEBMASTER` y `ATI` puedan visualizar el catálogo de tiendas registradas.

## User Review Required

> [!IMPORTANT]
> La API actual requiere un `plaza_id` para obtener las tiendas. Para el rol `WEBMASTER` (que no tiene una plaza asignada por defecto), implementaremos un selector de Plaza en la pantalla de Tiendas para poder cargar la lista correspondiente.

## Proposed Changes

### [Componente] Navegación y Rutas

#### [MODIFY] [NavGraph.kt](file:///C:/Users/darkchild1094/AndroidStudioProjects/encuestas_android/app/src/main/java/mx/com/getic/encuestasoxxo/ui/navigation/NavGraph.kt)
- Añadir `Rutas.TIENDAS` al objeto `Rutas`.
- Actualizar `ConDrawer` para incluir el ítem "Tiendas" si el rol es `WEBMASTER` o `ATI`.
- Registrar la ruta en el `NavHost` conectándola a la nueva `TiendasScreen`.

### [Componente] UI de Tiendas

#### [NEW] [TiendasViewModel.kt](file:///C:/Users/darkchild1094/AndroidStudioProjects/encuestas_android/app/src/main/java/mx/com/getic/encuestasoxxo/ui/tiendas/TiendasViewModel.kt)
- Gestionar el estado de la lista de tiendas, carga y errores.
- Lógica para cargar plazas (para WEBMASTER) y tiendas por plaza.
- Si el usuario tiene `plazaId` (ATI), cargar automáticamente.

#### [NEW] [TiendasScreen.kt](file:///C:/Users/darkchild1094/AndroidStudioProjects/encuestas_android/app/src/main/java/mx/com/getic/encuestasoxxo/ui/tiendas/TiendasScreen.kt)
- Interfaz con lista de tiendas usando `ListItem`.
- Buscador por nombre o código de tienda.
- Selector de plaza (visible solo si el usuario no tiene una plaza fija).
- Diálogo de detalles al pulsar una tienda.

### [Componente] Infraestructura

#### [MODIFY] [AppViewModelFactory.kt](file:///C:/Users/darkchild1094/AndroidStudioProjects/encuestas_android/app/src/main/java/mx/com/getic/encuestasoxxo/ui/AppViewModelFactory.kt)
- Añadir soporte para instanciar `TiendasViewModel`.

## Verification Plan

### Automated Tests
- No se planean tests automáticos para este cambio de UI, se verificará mediante despliegue.

### Manual Verification
1. Iniciar sesión como `ATI`: verificar que aparezca el menú "Tiendas" y cargue las tiendas de su plaza.
2. Iniciar sesión como `WEBMASTER`: verificar que aparezca el menú "Tiendas", permita seleccionar una plaza y luego cargue sus tiendas.
3. Probar el buscador de tiendas.
4. Pulsar una tienda y verificar que el diálogo de detalles muestre la información correctamente.
