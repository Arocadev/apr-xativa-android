<div align="center">

# APR Xàtiva — Android

**App mòbil per a residents de les Àrees de Prioritat Residencial de Xàtiva**  
*Mobile app for residents of Xàtiva's Residential Priority Areas*

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-purple?logo=kotlin)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-Material3-blue?logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Android](https://img.shields.io/badge/Android-API_26+-green?logo=android)](https://android.com)
[![Retrofit](https://img.shields.io/badge/Retrofit-2.9-orange)](https://square.github.io/retrofit)

</div>

---

## ¿Qué es APR Xàtiva?

Sistema de gestión de Áreas de Prioridad Residencial para el Ayuntamiento de Xàtiva. Esta app permite a los residentes gestionar sus vehículos, solicitar autorizaciones de acceso y consultar sus derechos activos directamente desde el móvil.

> Proyecto TFG del CFGS DAM en el IES Dr. Lluís Simarro — nota **10/10**. Cedido al Ayuntamiento de Xàtiva como base para una posible implantación real.

---

## ✨ Funcionalidades principales

### 🔐 Autenticación
- Login con DNI + contraseña
- **Refresh token** — renovación automática del JWT sin re-login
- Logout con revocación del token en el servidor
- Persistencia de sesión con **DataStore Preferences**
- Splash screen con estado de sesión

### 📋 Solicitudes de autorización
- Flujo completo de registro → solicitud → aprobación
- **Polling automático** cada 10 segundos del estado de solicitud
- 3 estados visuales diferenciados:
  - 🟡 **PENDIENTE** — con subida de documentación
  - 🔴 **RECHAZADA** — con opción de reenviar documentación
  - ⚪ **DESACTIVADO** — cuenta sin solicitud activa
- Subida de documentación (PDF, imagen) directamente desde el móvil

### 🚗 Vehículos
- Alta de vehículo con matrícula y tipo de acreditación (LIBRE / ACREDITADO)
- Baja y reactivación de vehículos
- **Pull to refresh** para recargar la lista
- Validación de formato de matrícula

### 🎫 Derechos de acceso
- Creación de derechos **permanentes** (vinculados a vehículo propio)
- Creación de derechos **puntuales para invitados** (matrícula + fecha)
- DatePicker con restricción de fechas válidas
- **Pull to refresh** para recargar los derechos activos
- Límite de 5 invitaciones por mes

### ⚙️ Opciones
- 🌍 **Multiidioma** — Valencià / Castellano / English
- 🌙 **Modo oscuro** — toggle en ajustes
- Información de versión y contacto del Ayuntamiento

### 👤 Perfil
- Datos del usuario (DNI, nombre, tipo)
- Cambio de contraseña con validación

---

## 🛠️ Stack tecnológico

| Capa | Tecnología |
|------|-----------|
| Lenguaje | Kotlin 2.0 |
| UI | Jetpack Compose + Material 3 |
| Arquitectura | MVVM + StateFlow |
| HTTP | Retrofit 2.9 + OkHttp |
| Auth | JWT + Refresh token (AuthInterceptor) |
| Persistencia | DataStore Preferences |
| Navegación | Navigation Compose |
| Async | Coroutines + ViewModelScope |

---

## 📁 Estructura del proyecto

```
app/src/main/java/com/example/aprxtiva/
├── api/
│   ├── ApiService.kt
│   ├── AuthInterceptor.kt
│   └── RetrofitClient.kt
├── entities/
├── repository/
│   ├── AuthRepository.kt
│   ├── DerechoRepository.kt
│   ├── DocumentoRepository.kt
│   ├── SolicitudRepository.kt
│   └── VehiculoRepository.kt
├── ui/
│   ├── screens/
│   │   ├── SplashScreen.kt
│   │   ├── LoginScreen.kt
│   │   ├── RegisterScreen.kt
│   │   ├── HomeScreen.kt
│   │   ├── VehiculosScreen.kt
│   │   ├── DerechosScreen.kt
│   │   ├── PerfilScreen.kt
│   │   ├── OpcionesScreen.kt
│   │   └── GuiaScreen.kt
│   └── theme/
├── utils/
│   ├── IdiomaManager.kt
│   └── TokenManager.kt
├── viewmodel/
│   ├── AuthViewModel.kt
│   ├── DerechoViewModel.kt
│   ├── EstadoUI.kt
│   ├── SolicitudViewModel.kt
│   └── VehiculoViewModel.kt
└── MainActivity.kt
```

---

## 🚀 Instalación

### Des d'APK
Descarrega l'última versió des de [GitHub Releases](https://github.com/ArocaDev/apr-xativa-android/releases).

### Des d'Android Studio

```bash
git clone https://github.com/ArocaDev/apr-xativa-android.git
```

Obre el projecte a Android Studio i edita la `BASE_URL` a `RetrofitClient.kt`:

```kotlin
// Emulador
private const val BASE_URL = "http://10.0.2.2:8080/"

// Dispositiu físic (IP local del servidor)
private const val BASE_URL = "http://192.168.1.XXX:8080/"
```

Executa en dispositiu o emulador amb API 26+.

---

## 📱 Requisits

- Android 8.0 (API 26) o superior
- Backend APR Xàtiva en marcha i accessible per xarxa local o internet

---

## 🗺️ Roadmap

- [ ] APK en GitHub Releases
- [ ] Desplegament backend en Railway per a demo sense xarxa local
- [ ] Biometria (empremta / Face ID) per a login ràpid
- [ ] Push notifications per a canvis d'estat de sol·licitud

---

## 🔗 Repositoris del projecte

| Component | Repositori |
|---|---|
| Backend API REST | [apr-xativa-backend](https://github.com/ArocaDev/apr-xativa-backend) |
| Panel web + Landing | [apr-xativa-web](https://github.com/ArocaDev/apr-xativa-web) |
| App mòbil Android (aquest repo) | [apr-xativa-android](https://github.com/ArocaDev/apr-xativa-android) |

---

## 👤 Autor

**Alejandro Rodríguez Calabuig**  
[github.com/ArocaDev](https://github.com/ArocaDev) · [LinkedIn](https://linkedin.com/in/alejandro-rodriguez-calabuig-a871a1230)

---

## 📄 Llicència

Projecte acadèmic — cedit a l'Ajuntament de Xàtiva per a possible ús institucional.
