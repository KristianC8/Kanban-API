# Kanban API — Referencia Completa

Documentación detallada de todos los endpoints de la API REST del proyecto Kanban.

**URL Base:** `http://localhost:8080`  
**Content-Type:** `application/json`

---

## Tabla de Contenidos

- [Health Check](#health-check)
- [Proyectos](#proyectos)
  - [Listar todos los proyectos](#listar-todos-los-proyectos)
  - [Obtener proyecto por ID](#obtener-proyecto-por-id)
  - [Crear proyecto](#crear-proyecto)
  - [Actualizar proyecto](#actualizar-proyecto)
  - [Eliminar proyecto](#eliminar-proyecto)
- [Tareas](#tareas)
  - [Listar tareas de un proyecto](#listar-tareas-de-un-proyecto)
  - [Crear tarea](#crear-tarea)
  - [Actualizar tarea completa](#actualizar-tarea-completa)
  - [Actualizar estado/posición de tarea](#actualizar-estadoposición-de-tarea)
  - [Eliminar tarea](#eliminar-tarea)
  - [Mover tarea entre columnas](#mover-tarea-entre-columnas)
  - [Reorganizar posiciones](#reorganizar-posiciones)
- [Errores](#errores)

---

## Health Check

### Verificar disponibilidad del servidor

Endpoint para comprobar que el servidor está activo y respondiendo.

```
GET /health
```

#### Respuesta exitosa

**Código:** `200 OK`

```
OK
```

---

## Proyectos

### Listar todos los proyectos

Obtiene la lista completa de todos los proyectos registrados, incluyendo sus tareas asociadas.

```
GET /kanban-app/proyectos
```

#### Respuesta exitosa

**Código:** `200 OK`

```json
[
  {
    "id": 1,
    "nombreProyecto": "Mi Proyecto",
    "descripciónProyecto": "Descripción del proyecto",
    "tareas": [
      {
        "id": 1,
        "titulo": "Configurar base de datos",
        "descripcion": "Instalar y configurar PostgreSQL",
        "estado": "done",
        "prioridad": "High",
        "fechaPendiente": "2026-05-20",
        "posicion": 1.0
      }
    ]
  }
]
```

---

### Obtener proyecto por ID

Obtiene un proyecto específico por su identificador.

```
GET /kanban-app/proyectos/{id}
```

#### Parámetros de ruta

| Parámetro | Tipo    | Requerido | Descripción              |
| --------- | ------- | --------- | ------------------------ |
| `id`      | Integer | Sí        | ID del proyecto          |

#### Respuesta exitosa

**Código:** `200 OK`

```json
{
  "id": 1,
  "nombreProyecto": "Mi Proyecto",
  "descripciónProyecto": "Descripción del proyecto",
  "tareas": [
    {
      "id": 1,
      "titulo": "Configurar base de datos",
      "descripcion": "Instalar y configurar PostgreSQL",
      "estado": "done",
      "prioridad": "High",
      "fechaPendiente": "2026-05-20",
      "posicion": 1.0
    }
  ]
}
```

#### Respuesta de error

**Código:** `404 Not Found`

```json
{
  "timestamp": "2026-05-14T19:00:00.000+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Proyecto con el id: 99, No existe",
  "path": "/kanban-app/proyectos/99"
}
```

---

### Crear proyecto

Crea un nuevo proyecto.

```
POST /kanban-app/proyectos
```

#### Cuerpo de la petición (Request Body)

```json
{
  "nombreProyecto": "Nuevo Proyecto",
  "descripciónProyecto": "Descripción del nuevo proyecto"
}
```

| Campo                 | Tipo   | Requerido | Descripción              |
| --------------------- | ------ | --------- | ------------------------ |
| `nombreProyecto`      | String | Sí        | Nombre del proyecto      |
| `descripciónProyecto` | String | No        | Descripción del proyecto |

#### Respuesta exitosa

**Código:** `200 OK`

```json
{
  "id": 2,
  "nombreProyecto": "Nuevo Proyecto",
  "descripciónProyecto": "Descripción del nuevo proyecto",
  "tareas": null
}
```

---

### Actualizar proyecto

Actualiza el nombre y/o la descripción de un proyecto existente.

```
PUT /kanban-app/proyectos/{id}
```

#### Parámetros de ruta

| Parámetro | Tipo    | Requerido | Descripción     |
| --------- | ------- | --------- | --------------- |
| `id`      | Integer | Sí        | ID del proyecto |

#### Cuerpo de la petición (Request Body)

```json
{
  "nombreProyecto": "Proyecto Actualizado",
  "descripciónProyecto": "Nueva descripción"
}
```

#### Respuesta exitosa

**Código:** `200 OK`

```json
{
  "id": 1,
  "nombreProyecto": "Proyecto Actualizado",
  "descripciónProyecto": "Nueva descripción",
  "tareas": [...]
}
```

#### Respuesta de error

**Código:** `404 Not Found` — si el proyecto no existe.

---

### Eliminar proyecto

Elimina un proyecto y todas sus tareas asociadas (cascade).

```
DELETE /kanban-app/proyectos/{id}
```

#### Parámetros de ruta

| Parámetro | Tipo    | Requerido | Descripción     |
| --------- | ------- | --------- | --------------- |
| `id`      | Integer | Sí        | ID del proyecto |

#### Respuesta exitosa

**Código:** `200 OK`

```json
{
  "eliminado": true
}
```

#### Respuesta de error

**Código:** `404 Not Found` — si el proyecto no existe.

---

## Tareas

### Listar tareas de un proyecto

Obtiene todas las tareas asociadas a un proyecto específico.

```
GET /kanban-app/tareas/{projectId}
```

#### Parámetros de ruta

| Parámetro   | Tipo    | Requerido | Descripción     |
| ----------- | ------- | --------- | --------------- |
| `projectId` | Integer | Sí        | ID del proyecto |

#### Respuesta exitosa

**Código:** `200 OK`

```json
[
  {
    "id": 1,
    "titulo": "Diseñar interfaz",
    "descripcion": "Crear mockups en Figma",
    "estado": "todo",
    "prioridad": "Medium",
    "fechaPendiente": "2026-06-01",
    "posicion": 1.0
  },
  {
    "id": 2,
    "titulo": "Implementar login",
    "descripcion": "Autenticación con JWT",
    "estado": "inProgress",
    "prioridad": "High",
    "fechaPendiente": "2026-05-25",
    "posicion": 1.0
  }
]
```

#### Respuesta de error

**Código:** `404 Not Found` — si no existen tareas para el proyecto.

---

### Crear tarea

Crea una nueva tarea y la asocia a un proyecto existente.

```
POST /kanban-app/tareas
```

#### Cuerpo de la petición (Request Body)

```json
{
  "titulo": "Nueva tarea",
  "descripcion": "Descripción de la tarea",
  "estado": "todo",
  "prioridad": "Medium",
  "fechaPendiente": "2026-06-15",
  "posicion": 1.0,
  "proyectoId": 1
}
```

| Campo            | Tipo    | Requerido | Descripción                                     |
| ---------------- | ------- | --------- | ----------------------------------------------- |
| `titulo`         | String  | Sí        | Título de la tarea                              |
| `descripcion`    | String  | No        | Descripción detallada                           |
| `estado`         | String  | Sí        | Estado inicial: `todo`, `inProgress`, `done`    |
| `prioridad`      | String  | Sí        | Prioridad: `Low`, `Medium`, `High`              |
| `fechaPendiente` | Date    | Sí        | Fecha límite (formato: `YYYY-MM-DD`)            |
| `posicion`       | Double  | Sí        | Posición ordinal dentro de la columna           |
| `proyectoId`     | Integer | Sí        | ID del proyecto al que pertenece la tarea       |

#### Respuesta exitosa

**Código:** `200 OK`

```json
{
  "id": 3,
  "titulo": "Nueva tarea",
  "descripcion": "Descripción de la tarea",
  "estado": "todo",
  "prioridad": "Medium",
  "fechaPendiente": "2026-06-15",
  "posicion": 1.0
}
```

#### Respuesta de error

**Código:** `404 Not Found` — si el proyecto no existe.

---

### Actualizar tarea completa

Actualiza todos los campos de una tarea existente. Al actualizar, se reajustan las posiciones en la columna de origen.

```
PUT /kanban-app/tareas/{id}
```

#### Parámetros de ruta

| Parámetro | Tipo    | Requerido | Descripción   |
| --------- | ------- | --------- | ------------- |
| `id`      | Integer | Sí        | ID de la tarea |

#### Cuerpo de la petición (Request Body)

```json
{
  "titulo": "Tarea actualizada",
  "descripcion": "Nueva descripción",
  "estado": "inProgress",
  "prioridad": "High",
  "fechaPendiente": "2026-06-20",
  "posicion": 2.0
}
```

| Campo            | Tipo      | Requerido | Descripción                                  |
| ---------------- | --------- | --------- | -------------------------------------------- |
| `titulo`         | String    | Sí        | Título de la tarea                           |
| `descripcion`    | String    | No        | Descripción detallada                        |
| `estado`         | String    | Sí        | Estado: `todo`, `inProgress`, `done`         |
| `prioridad`      | String    | Sí        | Prioridad: `Low`, `Medium`, `High`           |
| `fechaPendiente` | LocalDate | Sí        | Fecha límite (formato: `YYYY-MM-DD`)         |
| `posicion`       | Double    | Sí        | Nueva posición ordinal                       |

#### Respuesta exitosa

**Código:** `200 OK`

```json
{
  "id": 1,
  "titulo": "Tarea actualizada",
  "descripcion": "Nueva descripción",
  "estado": "inProgress",
  "prioridad": "High",
  "fechaPendiente": "2026-06-20",
  "posicion": 2.0
}
```

#### Respuesta de error

**Código:** `404 Not Found` — si la tarea no existe.

---

### Actualizar estado/posición de tarea

Actualización parcial del estado y/o posición de una tarea. Ideal para operaciones de drag & drop dentro del tablero.

```
PATCH /kanban-app/estado/tareas/{taskId}
```

#### Parámetros de ruta

| Parámetro | Tipo    | Requerido | Descripción    |
| --------- | ------- | --------- | -------------- |
| `taskId`  | Integer | Sí        | ID de la tarea |

#### Cuerpo de la petición (Request Body)

Se envía un objeto con los campos a actualizar. Solo se procesan `estado` y `posicion`.

```json
{
  "estado": "done",
  "posicion": 3.0
}
```

| Campo     | Tipo   | Requerido | Descripción                              |
| --------- | ------ | --------- | ---------------------------------------- |
| `estado`  | String | No        | Nuevo estado: `todo`, `inProgress`, `done` |
| `posicion`| Number | No        | Nueva posición ordinal                   |

> **Nota:** Al actualizar, se reajustan automáticamente las posiciones en la columna de origen.

#### Respuesta exitosa

**Código:** `200 OK`

```json
{
  "id": 1,
  "titulo": "Mi tarea",
  "descripcion": "Descripción",
  "estado": "done",
  "prioridad": "High",
  "fechaPendiente": "2026-06-01",
  "posicion": 3.0
}
```

#### Respuesta de error

- **`404 Not Found`** — si la tarea no existe.
- **`400 Bad Request`** — si el valor de `posicion` no es un número.

---

### Eliminar tarea

Elimina una tarea y reajusta las posiciones de las demás tareas en la misma columna.

```
DELETE /kanban-app/tareas/{id}
```

#### Parámetros de ruta

| Parámetro | Tipo    | Requerido | Descripción    |
| --------- | ------- | --------- | -------------- |
| `id`      | Integer | Sí        | ID de la tarea |

#### Respuesta exitosa

**Código:** `200 OK`

```json
{
  "eliminado": true
}
```

#### Respuesta de error

**Código:** `404 Not Found` — si la tarea no existe.

---

### Mover tarea entre columnas

Mueve una tarea de una columna a otra, ajustando automáticamente las posiciones en ambas columnas (origen y destino).

```
POST /kanban-app/mover
```

#### Cuerpo de la petición (Request Body)

```json
{
  "idTarea": 1,
  "nuevoEstado": "done",
  "nuevaPosicion": 2.0
}
```

| Campo           | Tipo    | Requerido | Descripción                                             |
| --------------- | ------- | --------- | ------------------------------------------------------- |
| `idTarea`       | Integer | Sí        | ID de la tarea a mover                                  |
| `nuevoEstado`   | String  | Sí        | Estado destino: `todo`, `inProgress`, `done`            |
| `nuevaPosicion` | Double  | No        | Posición en la columna destino (auto-calculada si es `null`) |

> **Nota:** Si `nuevaPosicion` es `null`, la tarea se colocará al final de la columna destino. Si la posición es `0` o negativa, se ejecuta automáticamente una reorganización de la columna.

#### Respuesta exitosa

**Código:** `200 OK`

```json
{
  "Moved": true
}
```

---

### Reorganizar posiciones

Reorganiza las posiciones de todas las tareas dentro de una columna, asignando enteros consecutivos comenzando desde 1.

```
POST /kanban-app/reorganizar
```

#### Cuerpo de la petición (Request Body)

```json
{
  "estado": "todo"
}
```

| Campo    | Tipo   | Requerido | Descripción                                  |
| -------- | ------ | --------- | -------------------------------------------- |
| `estado` | String | Sí        | Estado de la columna: `todo`, `inProgress`, `done` |

#### Respuesta exitosa

**Código:** `200 OK` (sin cuerpo)

---

## Errores

La API utiliza códigos HTTP estándar para indicar errores:

| Código | Significado       | Descripción                                          |
| ------ | ----------------- | ---------------------------------------------------- |
| `200`  | OK                | La solicitud fue exitosa                             |
| `400`  | Bad Request       | El cuerpo de la petición tiene datos inválidos       |
| `404`  | Not Found         | El recurso solicitado no fue encontrado              |
| `500`  | Internal Server Error | Error interno del servidor                      |

### Formato de error estándar (Spring Boot)

```json
{
  "timestamp": "2026-05-14T19:00:00.000+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Proyecto con el id: 99, No existe",
  "path": "/kanban-app/proyectos/99"
}
```
