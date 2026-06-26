# APR Xàtiva — Android

> App móvil nativa para gestión de accesos residenciales | Native Android app for residential access management

---

## 🇪🇸 Español

Aplicación móvil Android nativa del sistema APR Xàtiva, dirigida a los ciudadanos del Nucleo Antiguo. Permite gestionar vehículos, solicitar autorizaciones de acceso y consultar el estado de las solicitudes directamente desde el móvil. Desarrollada como parte del TFG del CFGS DAM en el IES Dr. Lluís Simarro, con nota de **9/10**.

> El proyecto fue cedido al Ayuntamiento de Xàtiva como base para una posible implantación real.

---

### ✨ Funcionalidades

- 🔐 **Autenticación** — login con DNI/NIE, registro con selector de tipología de acreditación
- 🚗 **Vehículos** — registro, activación y desactivación temporal sin eliminación
- 🎫 **Derechos de acceso** — permanentes para vehículo propio y puntuales para invitados (límite 5/mes)
- 📋 **Estado de solicitud** — polling automático cada 10 segundos con distinción de 3 estados: pendiente, rechazada y cuenta desactivada
- 🌍 **Multiidioma** — Valencià / Castellano / English con cambio dinámico sin reinicio
- 🌙 **Modo oscuro** — toggle desde la pantalla de opciones
- 🔔 **Notificaciones** — toggle para avisos de solicitudes y derechos
- 🔒 **Seguridad** — token JWT persistido en DataStore, logout cancela el polling inmediatamente

---

### 🛠️ Stack tecnológico

| Capa | Tecnología |
|------|-----------|
| Lenguaje | Kotlin |
| UI | Jetpack Compose |
| Arquitectura | MVVM + StateFlow |
| HTTP | Retrofit 2 + Gson |
| Persistencia | DataStore Preferences (token JWT) |

---

### 📁 Estructura del proyecto

```
app/src/main/java/com/apr/
├── ui/
│   ├── login/        # Login + ViewModel
│   ├── registro/     # Registro + ViewModel
│   ├── home/         # Pantalla principal + ViewModel
│   ├── vehiculos/    # Vehículos + ViewModel
│   ├── derechos/     # Derechos de acceso + ViewModel
│   ├── perfil/       # Perfil de usuario + ViewModel
│   └── opciones/     # Idioma, modo oscuro, notificaciones
├── data/
│   ├── api/          # Retrofit interfaces + modelos
│   └── datastore/    # Persistencia JWT
└── utils/            # Utilidades generales
```

---

### 🚀 Instalación

```bash
git clone https://github.com/ArocaDev/apr-xativa-android.git
```

Abre el proyecto en **Android Studio**, conecta un dispositivo o inicia un emulador y ejecuta la app. Asegúrate de que el backend está corriendo y ajusta la URL base en `data/api/`.

---

### 🔗 Repositorios del proyecto

| Componente | Repositorio |
|---|---|
| Backend API REST | [apr-xativa-backend](https://github.com/ArocaDev/apr-xativa-backend) |
| Panel web + Landing | [apr-xativa-web](https://github.com/ArocaDev/apr-xativa-web) |
| App móvil Android (este repo) | [apr-xativa-android](https://github.com/ArocaDev/apr-xativa-android) |

---

## 🌐 English

Native Android app of the APR Xàtiva system, designed for residents of the historic centre. It allows users to manage vehicles, request access authorisations and check the status of their requests directly from their phone. Developed as part of a final degree project (TFG) for the DAM Higher Vocational Course at IES Dr. Lluís Simarro, graded **9/10**.

> The project was handed over to Xàtiva City Council as a base for potential real-world implementation.

---

### ✨ Features

- 🔐 **Authentication** — login with national ID, registration with accreditation type selector
- 🚗 **Vehicles** — register, activate and temporarily deactivate without deletion
- 🎫 **Access rights** — permanent for own vehicle and one-time guest access (5/month limit)
- 📋 **Request status** — automatic polling every 10 seconds with 3 distinct states: pending, rejected and deactivated account
- 🌍 **Multilingual** — Valencian / Spanish / English with dynamic switching without restart
- 🌙 **Dark mode** — toggle from the options screen
- 🔔 **Notifications** — toggle for request and access right alerts
- 🔒 **Security** — JWT token persisted in DataStore, logout cancels polling immediately

---

### 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin |
| UI | Jetpack Compose |
| Architecture | MVVM + StateFlow |
| HTTP | Retrofit 2 + Gson |
| Persistence | DataStore Preferences (JWT token) |

---

### 🚀 Installation

```bash
git clone https://github.com/ArocaDev/apr-xativa-android.git
```

Open the project in **Android Studio**, connect a device or start an emulator and run the app. Make sure the backend is running and adjust the base URL in `data/api/`.

---

## 👤 Autor / Author

**Alejandro Rodríguez Calabuig** — [github.com/ArocaDev](https://github.com/ArocaDev) · [LinkedIn](https://linkedin.com/in/alejandro-rodriguez-calabuig-a871a1230)

---

## 📄 Licencia / License

Proyecto académico — cedido al Ayuntamiento de Xàtiva para posible uso institucional.  
Academic project — handed over to Xàtiva City Council for potential institutional use.
