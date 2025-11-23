

# 🏡 Nearby – Backend

**API para la plataforma de búsqueda, gestión y reserva de alojamientos cerca de universidades.**

Este backend proporciona los servicios esenciales para el ecosistema *Nearby*, incluyendo autenticación, manejo de usuarios, alojamiento, mensajería, favoritos, reseñas, notificaciones y más. Está construido con **Spring Boot**, utiliza **PostgreSQL** como base de datos relacional y está preparado para ejecutarse en **Docker**.

---

## 🚀 Tecnologías

* **Java 17+**
* **Spring Boot**

  * Spring Web
  * Spring Data JPA
  * Spring Validation
  * (Opcional) Spring Security + JWT
* **PostgreSQL**
* **Docker / Docker Compose**
* **Gradle**
* **MapStruct (si usa mappers)**
* **Lombok**

---

## 📂 Estructura del Proyecto

```
src/main/java/com/edu/unimagdalena/nearby
│
├── entities/              # Entidades JPA del dominio
├── enums/                 # Enumeraciones de estado y tipos
├── repositories/          # Repositorios de acceso a datos
├── services/              # Lógica de negocio (pendiente)
├── controllers/           # Capas REST (pendiente)
└── NearbyApplication.java # Clase principal
```

---

## 🛢️ Configuración de Base de Datos

Las variables de entorno deben manejarse desde un archivo `.env` (no se sube al repositorio).
Ejemplo:

```
POSTGRES_DB=nearbydb
POSTGRES_USER=nearbyuser
POSTGRES_PASSWORD=secret
```

El proyecto utiliza un archivo `.env.example` con el formato esperado.

La configuración interna del backend se hace desde:

```
src/main/resources/application.properties
```

---

## 🐳 Ejecución con Docker

El proyecto incluye:

* `Dockerfile` para el backend
* `docker-compose.yml` para levantar:

  * Backend
  * Base de datos PostgreSQL

### **Levantar todo el sistema:**

```bash
docker-compose up --build
```

### **Detener los servicios:**

```bash
docker-compose down
```

---

## ▶️ Ejecución sin Docker

Solo backend:

```bash
./gradlew bootRun
```

---

## 🧪 Pruebas

Por implementar (carpeta `src/test/` reservada).

---

## 🔒 Autenticación (Futura Implementación)

El backend está preparado para manejar:

* Registro e inicio de sesión
* Tokens JWT
* Roles de usuario (estudiante, arrendador, admin)
* Renovación de tokens
* Autorización por endpoints

---

## 📝 Estado Actual del Proyecto

✔ Configuración inicial
✔ Estructura del dominio (entities + enums)
✔ Repositorios JPA
✔ Docker
✔ `.env.example`
✔ Clase principal del proyecto

Pendiente por subir:

* Servicios
* Controladores
* Seguridad
* Pruebas
* Documentación de endpoints (Swagger)

---

## 🤝 Contribuciones

Para contribuir:

```bash
git checkout -b feature/nombre-de-la-feature
```

Pull requests siempre bienvenidos.

---

## 📄 Licencia

MIT License (o la que elijas).
