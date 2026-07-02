# MapaGeo

Aplicación Android que muestra lugares turísticos sobre un mapa de Google Maps, permitiendo filtrar por categoría, subcategoría y radio de búsqueda alrededor de una posición seleccionada.

## Descripción

MapaGeo consume una API REST para obtener categorías, subcategorías y lugares turísticos, mostrándolos como marcadores dinámicos sobre un `GoogleMap` en modo satélite. El usuario puede mover el mapa, ajustar el radio de búsqueda con un slider y filtrar los resultados por categoría/subcategoría.

## Funcionalidades

- Visualización de mapa satelital con Google Maps SDK.
- Círculo indicador del radio de búsqueda.
- Marcadores de lugares turísticos dentro del radio seleccionado.
- Filtro por categoría y subcategoría (con opción "Todos").
- Actualización automática de subcategorías según la categoría elegida.
- Actualización de resultados al mover el mapa o cambiar el radio.
- Visualización de latitud/longitud actuales.

## Tecnologías

- Java (Android)
- Google Maps SDK for Android
- Volley (peticiones HTTP)
- Material Components (Slider, Spinner)

## Requisitos

- Android Studio
- SDK de Android configurado
- API Key de Google Maps válida en `AndroidManifest.xml`
- Conexión a internet (emulador o dispositivo físico)

## Configuración

1. Clona el repositorio.
2. Abre el proyecto en Android Studio.
3. Agrega tu API Key de Google Maps en `AndroidManifest.xml`:
   ```xml
   <meta-data
       android:name="com.google.android.geo.API_KEY"
       android:value="TU_API_KEY_AQUI" />
   ```
4. Ejecuta la app en un emulador o dispositivo físico con Google Play Services.

## Captura de pantalla

<img width="466" height="853" alt="image" src="https://github.com/user-attachments/assets/307bc1cb-f32d-4835-b102-5ebe31b65f76" />



## Estructura del proyecto

```
app/
 └── src/main/java/com/example/mapageo/
      └── MainActivity.java
 └── src/main/res/
      └── layout/activity_main.xml
```
