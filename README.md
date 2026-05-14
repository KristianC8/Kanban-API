# Kanban API

REST API para gestión de tableros Kanban construida con **Spring Boot 3.3**. Permite crear proyectos y administrar tareas organizadas en columnas (To Do, In Progress, Done) con soporte para drag & drop mediante posicionamiento numérico.

---

## Tabla de Contenidos

- [Tecnologías](#tecnologías)
- [Arquitectura](#arquitectura)
- [Requisitos Previos](#requisitos-previos)
- [Instalación y Configuración](#instalación-y-configuración)
- [Variables de Entorno](#variables-de-entorno)
- [Ejecutar la Aplicación](#ejecutar-la-aplicación)
- [Docker](#docker)
- [Referencia de la API](#referencia-de-la-api)
- [Modelos de Datos](#modelos-de-datos)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Contribuir](#contribuir)

---

## Tecnologías

| Tecnología               | Versión  |
| ------------------------ | -------- |
| Java                     | 21       |
| Spring Boot              | 3.3.4    |
| Spring Data JPA          | 3.3.x    |
| PostgreSQL               | 42.7.4   |
| Lombok                   | latest   |
| Maven                    | 3.9+     |
| Docker (opcional)        | 20+      |

---

## Arquitectura

El proyecto sigue una arquitectura en capas (Layered Architecture):

```
┌─────────────────────────────────────────┐
│           Controladores (REST)          │   ← Endpoints HTTP
├─────────────────────────────────────────┤
│         Servicios (Lógica de negocio)   │   ← Interfaces + Implementaciones
├─────────────────────────────────────────┤
│           Repositorios (JPA)            │   ← Acceso a datos
├─────────────────────────────────────────┤
│         PostgreSQL (Base de Datos)      │   ← Persistencia
└─────────────────────────────────────────┘
```

---

## Requisitos Previos

- **Java 21** o superior
- **Maven 3.9+** (o usar el wrapper `mvnw` incluido)
- **PostgreSQL** con una base de datos creada
- (Opcional) **Docker** para despliegue con contenedores

---

## Instalación y Configuración

1. **Clonar el repositorio**

   ```bash
   git clone https://github.com/KristianC8/Kanban-API.git
   cd Kanban-API
   ```

2. **Configurar las variables de entorno** (ver sección siguiente)

3. **Compilar el proyecto**

   ```bash
   ./mvnw clean package -DskipTests
   ```

---

## Variables de Entorno

La aplicación requiere las siguientes variables de entorno para conectarse a la base de datos:

| Variable        | Descripción                                  | Ejemplo                                           |
| --------------- | -------------------------------------------- | ------------------------------------------------- |
| `DB_URL`        | URL JDBC de conexión a PostgreSQL            | `jdbc:postgresql://localhost:5432/kanban_db`       |
| `DB_USERNAME`   | Usuario de la base de datos                  | `postgres`                                        |
| `DB_PASSWORD`   | Contraseña de la base de datos               | `mi_password`                                     |

### Configuración en el sistema

**Windows (PowerShell):**
```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/kanban_db"
$env:DB_USERNAME = "postgres"
$env:DB_PASSWORD = "mi_password"
```

**Linux / macOS:**
```bash
export DB_URL="jdbc:postgresql://localhost:5432/kanban_db"
export DB_USERNAME="postgres"
export DB_PASSWORD="mi_password"
```

> **Nota:** La aplicación usa `hibernate.ddl-auto=update`, por lo que las tablas se crean y actualizan automáticamente al iniciar.

---

## Ejecutar la Aplicación

```bash
./mvnw spring-boot:run
```

La API estará disponible en: `http://localhost:8080`

---

## Docker

### Construir la imagen

```bash
docker build -t kanban-api .
```

### Ejecutar el contenedor

```bash
docker run -d \
  -p 8080:8080 \
  -e DB_URL="jdbc:postgresql://host.docker.internal:5432/kanban_db" \
  -e DB_USERNAME="postgres" \
  -e DB_PASSWORD="mi_password" \
  --name kanban-api \
  kanban-api
```

---

## Referencia de la API

**URL Base:** `http://localhost:8080`

Para la documentación detallada de cada endpoint con ejemplos de request/response, consulta [docs/API.md](docs/API.md).

### Resumen de Endpoints

#### Health Check

| Método | Endpoint   | Descripción                    |
| ------ | ---------- | ------------------------------ |
| `GET`  | `/health`  | Verificar disponibilidad del servidor |

#### Proyectos

| Método   | Endpoint                     | Descripción                    |
| -------- | ---------------------------- | ------------------------------ |
| `GET`    | `/kanban-app/proyectos`      | Listar todos los proyectos     |
| `GET`    | `/kanban-app/proyectos/{id}` | Obtener un proyecto por ID     |
| `POST`   | `/kanban-app/proyectos`      | Crear un nuevo proyecto        |
| `PUT`    | `/kanban-app/proyectos/{id}` | Actualizar un proyecto         |
| `DELETE` | `/kanban-app/proyectos/{id}` | Eliminar un proyecto           |

#### Tareas

| Método   | Endpoint                                | Descripción                             |
| -------- | --------------------------------------- | --------------------------------------- |
| `GET`    | `/kanban-app/tareas/{projectId}`        | Listar tareas de un proyecto            |
| `POST`   | `/kanban-app/tareas`                    | Crear una nueva tarea                   |
| `PUT`    | `/kanban-app/tareas/{id}`               | Actualizar una tarea completa           |
| `PATCH`  | `/kanban-app/estado/tareas/{taskId}`    | Actualizar estado/posición de una tarea |
| `DELETE` | `/kanban-app/tareas/{id}`               | Eliminar una tarea                      |
| `POST`   | `/kanban-app/mover`                     | Mover tarea entre columnas              |
| `POST`   | `/kanban-app/reorganizar`               | Reorganizar posiciones en una columna   |

---

## Modelos de Datos

### Proyecto

| Campo                 | Tipo      | Descripción                          |
| --------------------- | --------- | ------------------------------------ |
| `id`                  | Integer   | Identificador único (auto-generado)  |
| `nombreProyecto`      | String    | Nombre del proyecto                  |
| `descripciónProyecto` | String    | Descripción del proyecto             |
| `tareas`              | List      | Lista de tareas asociadas            |

### Tarea

| Campo            | Tipo      | Descripción                                        |
| ---------------- | --------- | -------------------------------------------------- |
| `id`             | Integer   | Identificador único (auto-generado)                |
| `titulo`         | String    | Título de la tarea                                 |
| `descripcion`    | String    | Descripción de la tarea                            |
| `estado`         | String    | Estado de la tarea: `todo`, `inProgress`, `done`   |
| `prioridad`      | String    | Prioridad: `Low`, `Medium`, `High`                 |
| `fechaPendiente` | LocalDate | Fecha límite de la tarea (formato: `YYYY-MM-DD`)   |
| `posicion`       | Double    | Posición ordinal dentro de la columna              |

---

## Estructura del Proyecto

```
kanban/
├── src/
│   ├── main/
│   │   ├── java/hd/kanban/
│   │   │   ├── KanbanApplication.java          # Clase principal
│   │   │   ├── controlador/                    # Controladores REST
│   │   │   │   ├── ProyectoControlador.java
│   │   │   │   ├── TareaControlador.java
│   │   │   │   └── VerificarDisponibilidadControlador.java
│   │   │   ├── dto/                            # Data Transfer Objects
│   │   │   │   ├── EstadoTareaDTO.java
│   │   │   │   └── MoverTareaDTO.java
│   │   │   ├── excepcion/                      # Excepciones personalizadas
│   │   │   │   └── RecursoNoEncontradoExcepcion.java
│   │   │   ├── modelo/                         # Entidades JPA
│   │   │   │   ├── Proyecto.java
│   │   │   │   └── Tarea.java
│   │   │   ├── repositorio/                    # Repositorios JPA
│   │   │   │   ├── ProyectoRepositorio.java
│   │   │   │   └── TareaRepositorio.java
│   │   │   └── servicio/                       # Lógica de negocio
│   │   │       ├── IProyectoServicio.java
│   │   │       ├── ITareaServicio.java
│   │   │       ├── ProyectoServicio.java
│   │   │       └── TareaServicio.java
│   │   └── resources/
│   │       ├── application.properties          # Configuración
│   │       └── logback-spring.xml              # Configuración de logs
│   └── test/                                   # Tests
├── docs/                                       # Documentación detallada
│   ├── API.md                                  # Referencia completa de la API
│   └── ARCHITECTURE.md                         # Detalles de arquitectura
├── Dockerfile                                  # Build multi-stage
├── pom.xml                                     # Dependencias Maven
└── README.md                                   # Este archivo
```

---

## CORS

La API tiene configurado CORS para los siguientes orígenes:

- `http://localhost:5173` — Desarrollo local (Vite)
- `https://kanban-app-front.vercel.app/` — Producción (Vercel)

---

## Contribuir

1. Haz un fork del repositorio
2. Crea una rama para tu feature (`git checkout -b feature/nueva-funcionalidad`)
3. Haz commit de tus cambios (`git commit -m 'feat: agregar nueva funcionalidad'`)
4. Haz push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Abre un Pull Request

---

## Licencia

Este proyecto es de código abierto. Consulta el archivo de licencia para más detalles.
