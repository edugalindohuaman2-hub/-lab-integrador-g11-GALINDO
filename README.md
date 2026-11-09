# 🚀 Rick and Morty - Android App (Jetpack Compose & M3)

## 📌 Datos Generales
- **Proyecto:** Rick and Morty Mobile App (`com.galindo.rickandmorty`)
- **Tema Asignado:** Consumo de la API de Rick and Morty con filtro `status=alive`, persistencia local offline-first con Room, autenticación Firebase Auth & Firestore, y control de roles (Admin / Usuario).
- **Desarrollador:** Desarrollador Único

---

## 🛠️ Requisitos de Compilación
- **JDK:** 17 o superior
- **Min SDK:** 24 (Android 7.0+)
- **Target SDK:** 34 (Android 14)
- **IDE:** Android Studio Ladybug (o superior)
- **Gradle:** 8.x / AGP 9.x

---

## 🚀 Guía de Configuración de Firebase
Para habilitar la autenticación y el control de roles (RBAC):

1. **Crear proyecto en Firebase:**
   - Registra tu app en [Firebase Console](https://console.firebase.google.com/) con el nombre de paquete `com.galindo.rickandmorty`.
   - Descarga el archivo de configuración `google-services.json` y colócalo en la carpeta `app/` del proyecto.
2. **Habilitar Authentication:**
   - Activa el proveedor **Email/Password** en la sección **Authentication** -> **Sign-in method**.
3. **Configurar Cloud Firestore:**
   - Crea una base de datos en modo de producción o prueba.
   - Crea la colección `usuarios` donde el ID del documento sea el **UID** del usuario en Firebase Auth y contenga el campo:
     - `rol` (String): `"ADMIN"` (para administradores) o `"USUARIO"` (para usuarios regulares).

---

## 📂 Estructura de Paquetes (Arquitectura MVVM)
```text
com.galindo.rickandmorty/
│
├── data/
│   ├── auth/          # Repositorio de autenticación (Firebase Auth & Firestore)
│   ├── local/         # Room Database, DAOs e Entities (Room como fuente de verdad)
│   ├── mapper/        # Mapeadores entre DTO, Entity y Modelo de Dominio
│   ├── remote/        # Retrofit ApiService, RetrofitClient y DTOs
│   └── repository/    # Implementación del Repositorio principal de ítems
│
├── domain/
│   └── model/         # Modelos de dominio (Item, Sesion, Rol)
│
├── notification/      # Sistema de notificaciones locales (NotificadorAndroid)
│
└── ui/
    ├── admin/         # Panel de administración (AdminScreen, AdminViewModel)
    ├── components/    # Componentes reutilizables (Cargando, ErrorView)
    ├── detalle/       # Pantalla de detalle (DetalleScreen, DetalleViewModel)
    ├── favoritos/     # Pantalla de favoritos (FavoritosScreen, FavoritosViewModel)
    ├── lista/         # Pantalla de catálogo (ListaScreen, ListaViewModel)
    ├── login/         # Pantalla de inicio de sesión (LoginScreen, LoginViewModel)
    ├── navigation/    # Grafo de navegación (AppNav)
    └── theme/         # Tema Material 3 (Color, Theme, Type)
```

---

## 📸 Guía de Capturas de Evidencia Requeridas (8 Secciones)

| # | Pantalla / Evidencia | Descripción |
| :-: | :--- | :--- |
| **1** | **Login (Auth & Validación)** | Pantalla de inicio de sesión con Firebase Auth y manejo de errores. |
| **2** | **Lista con Campanita y Tema** | Catálogo principal con selector de tema Sol/Luna y campanita de notificaciones con badge. |
| **3** | **Detalle del Personaje** | Pantalla de detalle consumiendo el endpoint `/character/{id}`. |
| **4** | **Favoritos** | Lista filtrada de elementos marcados como favoritos (persistencia en Room). |
| **5** | **Panel de Administración** | Dashboard con métricas de caché y botón Data Seeder. |
| **6** | **Notificación Push / Local (FCM)** | Alerta desplegada en el canal `"cambios_personajes"` tras mutaciones en Firestore. |
| **7** | **Consola de Firebase** | Estructura de colecciones `usuarios` (`rol`) y `personajes`. |
| **8** | **Pruebas Unitarias** | Ejecución exitosa de `ListaViewModelTest` en JUnit 4 / MockK. |
