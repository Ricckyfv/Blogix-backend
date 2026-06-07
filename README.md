# Blogix API — Backend Service

Este es el servicio backend de **Blogix** , un motor de blog moderno y seguro desarrollado con **Spring Boot 3**, **Java 21** y **PostgreSQL**. El proyecto está estructurado bajo principios de **Arquitectura Hexagonal**, lo que garantiza un desacoplamiento claro entre la lógica de dominio (casos de uso) y los detalles de infraestructura (base de datos, controladores REST y seguridad).

---

## 🔗 Enlaces del Proyecto

* 🚀 **Demo en Vivo (Frontend):** [https://blogix-frontend.vercel.app](https://blogix-frontend.vercel.app)
* 💻 **Repositorio del Frontend (Angular):** [https://github.com/Ricckyfv/Blogix-frontend](https://github.com/Ricckyfv/Blogix-frontend)

---

## 🛠️ Tecnologías y Herramientas

- **Java 21** (versión de soporte a largo plazo)
- **Spring Boot 3**
  - **Spring Data JPA** (Persistencia y accesos a datos)
  - **Spring Security** (Seguridad y autorización)
- **PostgreSQL** (Motor de base de datos relacional)
- **MapStruct** (Mapeos eficientes entre entidades y DTOs)
- **Lombok** (Reducción de código repetitivo/boilerplates)
- **Maven** (Gestor de dependencias)

---

## 📐 Arquitectura del Proyecto

El backend sigue un patrón de diseño **Hexagonal / Clean Architecture** estructurado de la siguiente forma dentro del paquete `com.ricardofernandezv.blog`:

```text
src/main/java/com/ricardofernandezv/blog/
├── domain/                  # Núcleo del Negocio (Core Domain)
│   ├── entities/            # Entidades JPA (User, Post, Category, Tag, PostLike, Comment)
│   └── dtos/                # Data Transfer Objects y Modelos de Petición/Respuesta
├── services/                # Puertos e Implementaciones de Lógica (Ports & Use Cases)
│   ├── [Service].java       # Interfaces que definen los puertos del dominio
│   └── impls/               # Adaptadores lógicos de los casos de uso
├── repositories/            # Adaptador de Salida: Base de Datos (Spring Data JPA)
├── controllers/             # Adaptador de Entrada: Controladores REST (API v1)
├── security/                # Seguridad: JWT Filters, UserDetails y configuración web
└── config/                  # Configuraciones generales (CORS, Beans, etc.)
```

---

## 🚀 Características Principales

1. **Autenticación Segura (JWT)**: Registro, login y recuperación de contraseñas mediante tokens firmados.
2. **Gestión de Artículos (Posts)**:
   - Flujo de estados: Borradores (`DRAFT`) y Publicados (`PUBLISHED`).
   - Cálculo automático del tiempo estimado de lectura en base al contenido.
   - Categorización única y etiquetado múltiple (Tags).
3. **Sistema de Reacciones (Likes)**:
   - Funcionalidad tipo switch/toggle (`POST /api/v1/posts/{id}/like`).
   - Restricción única compuesta (`post_id`, `user_id`) a nivel de base de datos para evitar inconsistencias.
4. **Sección de Discusión (Comentarios)**:
   - Creación y listado cronológico de comentarios asociados a un post.
   - **Validación Estricta de Borrado**: Solo el autor original del comentario o el creador del artículo tienen permisos para eliminar un comentario.

---

## ⚙️ Configuración y Configuración Local

### Requisitos previos

- **Java JDK 21** o superior.
- **PostgreSQL** corriendo de forma local o a través de Docker.

### Variables de entorno / Configuración

Edita o provee las siguientes propiedades en `src/main/resources/application.properties` (o mediante variables de entorno):

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/blogdb
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contrasena

# JWT Secret (mínimo 256 bits)
app.jwt.secret=tu_super_secreto_para_firmar_los_tokens_de_jwt_aqui
app.jwt.expiration-ms=86400000
```

### Ejecutar la aplicación

1. Clona el repositorio y ve al directorio `/blog-app-back`.
2. Compila el proyecto con Maven:
   ```bash
   ./mvnw clean compile
   ```
3. Ejecuta la aplicación de Spring Boot:
   ```bash
   ./mvnw spring-boot:run
   ```
4. El servidor arrancará por defecto en el puerto `8080` (o el puerto configurado en tus variables locales). Puedes validar la API accediendo a `http://localhost:8080/api/v1/posts`.

---

## 📂 Principales Endpoints de la API (v1)

| Método | Endpoint | Descripción | Requiere Auth |
|---|---|---|---|
| **POST** | `/api/v1/auth/register` | Registro de nuevos usuarios | No |
| **POST** | `/api/v1/auth/login` | Inicio de sesión (Retorna token JWT) | No |
| **GET** | `/api/v1/posts` | Lista posts publicados (filtrable por `categoryId` o `tagId`) | Opcional |
| **GET** | `/api/v1/posts/{id}` | Recupera un artículo detallado (con likesCount y likedByMe) | Opcional |
| **POST** | `/api/v1/posts` | Crea un nuevo post (permite subir imágenes base64 optimizadas) | **Sí** |
| **POST** | `/api/v1/posts/{id}/like` | Alterna el like del usuario actual en el post | **Sí** |
| **GET** | `/api/v1/posts/{id}/comments` | Obtiene la lista de comentarios de un post | No |
| **POST** | `/api/v1/posts/{id}/comments` | Publica un comentario en el post | **Sí** |
| **DELETE** | `/api/v1/posts/{id}/comments/{commentId}` | Elimina un comentario específico (Valida autorías) | **Sí** |
