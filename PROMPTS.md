# 🤖 Bitácora de Prompts - Rick and Morty App (Resumen en 3 Prompts)

Este documento resume la bitácora de interacción y los 3 prompts principales utilizados para desarrollar la aplicación **Rick and Morty** en Android (Kotlin, Jetpack Compose, Material 3, Firebase Auth & Cloud Firestore).

---

## 📋 Prompts

### Prompt 1: Autenticación, Arquitectura Base y RBAC (Auth + Firestore)
* **Objetivo:** Implementar el sistema de usuarios, autenticación, recuperación por código de 5 dígitos y control de acceso basado en roles (`ADMIN` vs `USUARIO`).
* **Prompt Enviado:**
  > Actúa como un desarrollador Senior Android especializado en Kotlin, Jetpack Compose y Firebase (Auth + Cloud Firestore). Necesito implementar la pantalla de inicio de sesión (`LoginScreen.kt`), su ViewModel y el repositorio de autenticación (`FirebaseAuthRepository.kt`) configurando: 1. Registro con rol `"USUARIO"` en la colección `"usuarios"`. 2. Detección de rol (ADMIN vs USUARIO) al iniciar sesión. 3. Recuperación de contraseña en 2 pasos mediante un código de verificación de 5 dígitos almacenado en `"codigos_recuperacion"` con expiración de 10 minutos y validación estricta de teclado numérico (`KeyboardType.Number`). 4. Guardia de navegación en `AppNav.kt` y `AppBottomBar.kt` para proteger rutas administrativas.
* **Resultado / Componentes Generados:**
  - `FirebaseAuthRepository.kt`: Lógica robusta de autenticación y transacciones en Firestore.
  - `LoginViewModel.kt` y `LoginScreen.kt`: Interfaz con diálogos modales en modo oscuro y validación de 5 dígitos.
  - `AppNav.kt` y `AppBottomBar.kt`: Restricción dinámica de pestañas y redirección automática de rutas.

---

### Prompt 2: Fidelidad Visual UI/UX y Modo Oscuro Cálido
* **Objetivo:** Diseñar la interfaz de usuario con la paleta de colores cálida oscura y tarjetas de personaje tipo póster vertical.
* **Prompt Enviado:**
  > Actúa como desarrollador Android UI Senior en Jetpack Compose y Material 3. Necesito adaptar la interfaz gráfica de `ListaScreen.kt`, `CharacterItemCard.kt` y `AppBottomBar.kt` en el paquete `com.galindo.rickandmorty` para replicar exactamente el diseño de referencia: fondo en modo oscuro cálido (`#171212`), tarjetas de personaje con contenedor marrón/gris oscuro cálido (`#3A3131`) y esquinas redondeadas (`RoundedCornerShape(20.dp)`), imagen póster vertical a la izquierda (`80.dp x 110.dp`, `RoundedCornerShape(12.dp)`, `ContentScale.Crop`), subtítulos con especie, género y estado, y barra de navegación inferior con indicador de selección en forma de píldora/cápsula en color marrón acento (`#5C4033`).
* **Resultado / Componentes Generados:**
  - `CharacterItemCard.kt`: Componente modular de tarjeta con póster vertical y diseño optimizado.
  - `ListaScreen.kt`: Catálogo principal inmersivo enfocado en la experiencia de consumo.
  - `AppBottomBar.kt`: Barra inferior personalizada con indicador de píldora seleccionado.

---

### Prompt 3: Panel de Administración CRUD, Restricción de Datos y Notificaciones FCM
* **Objetivo:** Construir el centro de mando para administradores con operaciones CRUD en Cloud Firestore, filtrado por origen y notificaciones automáticas.
* **Prompt Enviado:**
  > Actúa como un desarrollador Senior Android especializado en Kotlin, Jetpack Compose, Firebase Cloud Firestore y FCM. Necesito implementar: 1. Un centro de mando administrativo en `AdminScreen.kt` con estadísticas, botón de carga inicial (*Data Seeder* para poblar 5 personajes en Firestore) y un formulario CRUD completo (Crear, Editar y Eliminar). 2. Restricción de manipulación de datos mediante la bandera `esDeFirestore: Boolean` en el modelo `Item.kt`, asegurando que los botones de edición y eliminación solo estén disponibles para administradores en registros de Firebase. 3. Sistema de notificaciones automáticas mediante `NotificationCompat.Builder` en el canal `"cambios_personajes"` ante cada acción de publicar, actualizar o remover personajes.
* **Resultado / Componentes Generados:**
  - `AdminScreen.kt` y `AdminViewModel.kt`: Dashboard y formularios modales con `FilterChip` para estados y validación.
  - `ItemRepositoryImpl.kt`: Sincronización híbrida (API REST + Cloud Firestore) e integración con `NotificadorAndroid.kt`.
  - `NotificadorAndroid.kt`: Creación del canal de notificaciones locales ante mutaciones de datos.
