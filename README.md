# Red Patrimonial

MVP móvil de la Red de Patrimonio Histórico de la Arquidiócesis de Santiago (DSY1105, Duoc UC).
Todos los datos son sintéticos.

El repositorio tiene dos partes:

- `app/`: aplicación Android en Kotlin con Jetpack Compose, MVVM, Navigation, Room y lectura de QR con CameraX + ML Kit.
- `backend/`: tres microservicios Spring Boot 3.5 con MySQL. En la Prueba 2 la app **no** se conecta al backend; se conectará en la Prueba 3 con Retrofit.

## Ejecutar la app

1. Abrir la carpeta raíz en Android Studio y ejecutar la configuración `app` (minSdk 26).
2. La primera vez, Room crea la base `red_patrimonial_db` y la llena con el catálogo de prueba (`database/seed`).
3. Para probar el QR en el emulador, escribir el código a mano en la pantalla QR (por ejemplo `redpatrimonial://recinto/3` o solo `3`). El código de cada recinto se muestra en el panel de su encargado.

### Usuarios de prueba (contraseña `1234`)

| Correo | Rol | Recinto |
|---|---|---|
| docente@demo.cl | Docente | — |
| encargado@demo.cl | Encargado | Iglesia de San Francisco |
| encargado2@demo.cl | Encargado | Basílica de la Merced |
| guia@demo.cl | Guía voluntario | — |
| guia2@demo.cl | Guía voluntario | — |
| admin@demo.cl | Administrador de red | — |

Los visitantes usan la app sin iniciar sesión.

## Estructura de la app

```
com.example.redpatrimonial
├── model/        Entidades Room y enums
├── dao/          Consultas Room
├── database/     AppDatabase, DatabaseProvider y datos de prueba (seed/)
├── repository/   Capa entre ViewModels y DAOs
├── viewmodel/    Un ViewModel por funcionalidad, más SesionViewModel
├── view/         Pantallas Compose (encargado/, admin/, docente/, guia/)
├── navigation/   Routes, AppNavigation y un grafo por persona
├── ui/           Tema y componentes compartidos
└── util/         Cálculo de tiempos, validaciones y códigos QR
```

### Reparto del trabajo

| | Persona A: exploración y recinto | Persona B: acceso, rutas, visitas y QR |
|---|---|---|
| Pantallas | Mapa, Buscador, Detalle, Eventos, Detalle evento, Favoritos, panel y formularios del encargado, panel admin, Registrar nodo, Moderar | Inicio, Login, Rutas, Detalle ruta, Ruta personalizada, QR, docente, guía, Solicitudes del recinto, Rutas oficiales |
| Tablas Room | recintos, eventos, favoritos, historial_estado, aperturas_especiales | usuarios, rutas, paradas_ruta, salidas_pedagogicas, solicitudes_guia, disponibilidad_guia, visitas_qr |
| Navegación | `navigation/NavExploracion.kt` | `navigation/NavRutasVisitas.kt` |
| Backend | `ms-recintos` | `ms-usuarios`, `ms-rutas` |

Archivos compartidos (se cambian solo con un PR que el otro aprueba): `Routes.kt`, `AppNavigation.kt`, `AppDatabase.kt`, `DatabaseProvider.kt`, `ui/`, `util/Validaciones.kt`, Gradle y Manifest.

## Ejecutar el backend

Requisitos: Java 21 o superior y MySQL en `localhost:3306`. Cada servicio crea su propia base de datos y carga datos de prueba al primer arranque.

```bash
cd backend/ms-recintos && ./mvnw spring-boot:run   # puerto 8081
cd backend/ms-usuarios && ./mvnw spring-boot:run   # puerto 8082
cd backend/ms-rutas    && ./mvnw spring-boot:run   # puerto 8083
```

El usuario y la clave de MySQL se configuran con las variables `DB_USER` y `DB_PASS` (por defecto `root` sin clave).
La documentación Swagger de cada servicio queda en `http://localhost:<puerto>/swagger-ui.html`.

Pruebas (usan H2 en memoria, no necesitan MySQL): `./mvnw test` dentro de cada servicio, o desde `backend/` para los tres.

| Servicio | Recursos principales |
|---|---|
| ms-recintos | `/api/recintos` (filtros, ficha, estado, historial, horarios, aperturas, multimedia), `/api/eventos` (cercanos, publicar, moderar), `/api/comunas`, `/api/tipos-recinto` |
| ms-usuarios | `/api/auth/login`, `/api/usuarios`, `/api/roles`, `/api/docentes`, `/api/guias` |
| ms-rutas | `/api/rutas`, `/api/salidas`, `/api/solicitudes`, `/api/asignaciones`, `/api/guias/{id}/disponibilidad`, `/api/guias/{id}/agenda` |

### Pendiente para la Prueba 3

- Conectar la app a los microservicios con Retrofit, usando Room como caché.
- Mapa real y ubicación GPS del usuario (hoy se simula en la Plaza de Armas).
- Autenticación con token entre app y backend, y llamadas entre microservicios (por ejemplo, que `ms-rutas` consulte las coordenadas de `ms-recintos`).
- APK firmado en modo release.
