# 🚀 Rick and Morty - Android App (Jetpack Compose & M3)

## 📌 Datos Generales
- **Proyecto:** Rick and Morty Mobile App
- **Tema Asignado:** Consumo de la API de Rick and Morty con filtro `status=alive`, persistencia local offline-first con Room, autenticación Firebase Auth & Firestore, y control de roles (Admin / Usuario).
- **Integrantes:** 
  - [Tu Nombre / Integrante 1]
  - [Integrante 2]

---

## 🛠️ Instrucciones de Compilación y Ejecución
1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/tu-usuario/rickandmorty.git
   ```
2. **Abrir en Android Studio:**
   - Selecciona **Open** y navega hasta la carpeta raíz del proyecto.
3. **Sincronizar Gradle:**
   - Asegúrate de tener instalado Android Studio con soporte para Kotlin 2.2.10 y AGP. Haz clic en **Sync Project with Gradle Files**.
4. **Ejecutar la App:**
   - Conecta un dispositivo físico o inicia un emulador Android (API 24 o superior).
   - Haz clic en el botón **Run 'app'** (`Shift + F10`).

---

## 🔥 Configuración de Firebase Firestore (Roles de Usuario)
Para probar los roles de **Admin** y **Usuario**, configura tu base de datos en [Firebase Console](https://console.firebase.google.com/):

1. **Crear proyecto en Firebase** o utilizar el proyecto configurado (`rickandmorty-874ac`).
2. **Habilitar Authentication (Email/Password):**
   - Ve a **Authentication** -> **Sign-in method** y habilita **Email/Password**.
   - Crea usuarios de prueba (ej. `admin@tecsup.edu.pe` y `usuario@tecsup.edu.pe`).
3. **Crear la colección `usuarios` en Firestore:**
   - Ve a **Cloud Firestore** -> **Start collection**.
   - ID de la colección: `usuarios`.
   - ID del documento: Ingresa el **UID** del usuario autenticado en Firebase Auth (puedes copiarlo desde la pestaña Authentication).
   - Campos del documento:
     - `rol` (String): `"admin"` (para el administrador) o `"usuario"` (para usuarios regulares).
4. **Colocar el archivo `google-services.json`:**
   - Asegúrate de que el archivo `google-services.json` esté ubicado en la carpeta `app/`.

---

## 📂 Estructura de Paquetes (Arquitectura MVVM)
```text
com.tecsup.rickandmorty/
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
