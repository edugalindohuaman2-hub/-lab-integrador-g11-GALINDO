# Bitácora de Prompts e Interacción con IA

Este documento registra los prompts y respuestas generadas durante el desarrollo de la aplicación utilizando herramientas de Inteligencia Artificial, cumpliendo con la Sección 11 de la guía del laboratorio.

## Consulta 1
- **Prompt:** 
  > Actúa como un desarrollador Senior Android especializado en Testing con Kotlin, JUnit 4, MockK y Coroutines Test. Necesito crear la suite de pruebas unitarias para `ListaViewModelTest.kt` evaluando casos de éxito, errores de red y alternar favoritos.
- **Respuesta de la IA:** 
  > Proporcionó la estructura con `StandardTestDispatcher`, reglas de despacho de corrutinas (`Dispatchers.setMain`), mocks con `mockk()` y verificación con `coEvery` y `coVerify`.
- **Aplicación en el código:** 
  > Se implementó la suite de pruebas unitarias en `ListaViewModelTest.kt` asegurando la cobertura requerida por la rúbrica.

## Consulta 2
- **Prompt:** 
  > Actúa como desarrollador Android UI Senior en Jetpack Compose y Material 3. Necesito implementar un Sistema de Estado Global de Tema Sincronizado (State Hoisting) en `MainActivity` para que al presionar el botón Sol/Luna en CUALQUIER pantalla, absolutamente toda la aplicación cambie de modo de manera unificada, integrando además una campanita con `BadgedBox` e historial con navegación directa al detalle.
- **Respuesta de la IA:** 
  > Sugirió elevar la variable `esModoOscuro` al root (`MainActivity`), pasarla mediante el grafo de navegación (`AppNav`) a todas las pantallas, y envolver con `RickandmortyTheme(darkTheme = esModoOscuro)`.
- **Aplicación en el código:** 
  > Se aplicó la arquitectura de tema global y la navegación directa al detalle desde el historial de notificaciones.

## Consulta 3
- **Prompt:** 
  > Actúa como un desarrollador Senior Android especializado en Kotlin, Jetpack Compose y Firebase (Auth + Cloud Firestore + FCM). Necesito implementar un sistema de notificaciones locales automáticas al sincronizar o mutar registros mediante `NotificationCompat.Builder` en el canal `"cambios_personajes"`, configurando un `PendingIntent` con Deep Linking para abrir el detalle del personaje al tocar la notificación.
- **Respuesta de la IA:** 
  > Brindó la implementación de `NotificadorAndroid.kt` implementando una interfaz desacoplada `Notificador`, creando el canal de notificación con `NotificationManager` y construyendo alertas con `NotificationCompat.Builder` y `PendingIntent`.
- **Aplicación en el código:** 
  > Se configuró el servicio de notificaciones locales y permisos en tiempo real en Android 13+.
