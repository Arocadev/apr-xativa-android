<div align="center">

# APR Xàtiva — App Android

**Aplicación Android para agentes de control de acceso vehicular**

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-purple?logo=kotlin)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-1.6-blue?logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Android](https://img.shields.io/badge/Android-8.0+-green?logo=android)](https://android.com)

</div>

---

## ¿Qué es APR Xàtiva?

APR Xàtiva es un sistema de control de acceso vehicular desarrollado como Trabajo de Fin de Grado (DAM) y adoptado por el Ajuntament de Xàtiva como propuesta técnica. Este repositorio contiene la app Android para los agentes de control — permite consultar matrículas en tiempo real y gestionar accesos desde el móvil.

🔧 **Backend:** [github.com/ArocaDev/apr-xativa-backend](https://github.com/ArocaDev/apr-xativa-backend)  
🌐 **Panel web:** [github.com/ArocaDev/apr-xativa-frontend](https://github.com/ArocaDev/apr-xativa-frontend)

---

## ✨ Funcionalidades

- **Login con JWT** y renovación automática de token
- **Consulta de matrícula** — resultado inmediato con indicador visual de acceso permitido/denegado
- **Historial de consultas** — registro local de los últimos accesos consultados
- **Acceso puntual para invitados** — flujo específico con límite mensual
- **Visibilidad de contraseña** en login
- **Multiidioma** — Valenciano, Español e Inglés
- **Gestión de errores de red** con mensajes claros al usuario
- **Compatibilidad** Android 8.0+

---

## 🛠️ Stack técnico

| Capa | Tecnología |
|------|-----------|
| Lenguaje | Kotlin 2.0 |
| UI | Jetpack Compose |
| Arquitectura | MVVM |
| HTTP | Retrofit 2 |
| Auth | JWT con interceptor automático |
| Navegación | Navigation Compose |
| Mínimo SDK | Android 8.0 (API 26) |

---

## 📁 Estructura del proyecto

```
apr-xativa-android/
├── app/src/main/java/com/arocadev/aprxativa/
│   ├── ui/
│   │   ├── login/
│   │   │   ├── LoginScreen.kt
│   │   │   └── LoginViewModel.kt
│   │   ├── scanner/
│   │   │   ├── ScannerScreen.kt
│   │   │   └── ScannerViewModel.kt
│   │   ├── resultado/
│   │   │   ├── ResultadoScreen.kt
│   │   │   └── ResultadoViewModel.kt
│   │   └── historial/
│   │       ├── HistorialScreen.kt
│   │       └── HistorialViewModel.kt
│   ├── data/
│   │   ├── api/
│   │   │   ├── ApiClient.kt
│   │   │   ├── AuthApi.kt
│   │   │   └── AccesoApi.kt
│   │   ├── model/
│   │   └── repository/
│   └── MainActivity.kt
├── assets/
│   └── .gitkeep
└── build.gradle.kts
```

---

## 🚀 Instalación

### Desde el código fuente

```bash
git clone https://github.com/ArocaDev/apr-xativa-android.git
```

1. Abre el proyecto en Android Studio
2. Edita `app/src/main/res/values/config.xml` con la URL del backend
3. Ejecuta en emulador o dispositivo físico

### APK directa

Descarga la última versión desde [Releases](https://github.com/ArocaDev/apr-xativa-android/releases).

---

## 🔑 Configuración

```xml
<!-- app/src/main/res/values/config.xml -->
<resources>
    <string name="api_base_url">http://tu-servidor:8080/</string>
</resources>
```

---

## 🌍 Idiomas soportados

| Idioma | Código |
|--------|--------|
| Valenciano | `ca` |
| Español | `es` |
| Inglés | `en` |

---

## 🗺️ Roadmap

- [x] Login con JWT y renovación automática
- [x] Consulta de matrícula con resultado visual
- [x] Historial de consultas
- [x] Acceso puntual para invitados
- [x] Visibilidad de contraseña
- [x] Multiidioma (Valenciano / Español / Inglés)
- [x] Gestión de errores de red
- [x] Capturas en emulador para README
- [x] APK en GitHub Releases

---

## 🏆 Reconocimiento

Proyecto calificado con **10/10** y adoptado por el **Ajuntament de Xàtiva** como propuesta técnica oficial.

---

## 👤 Autor

**Alejandro Rodríguez Calabuig**  
[github.com/ArocaDev](https://github.com/ArocaDev) · [LinkedIn](https://www.linkedin.com/in/alejandro-rodriguez-calabuig-a871a1230)

---

## 📄 Licencia

Proyecto académico — no licenciado para uso comercial.
