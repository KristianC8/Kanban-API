# Kanban API — Arquitectura

Documentación técnica de la arquitectura interna del proyecto Kanban API.

---

## Índice

- [Visión General](#visión-general)
- [Patrón Arquitectónico](#patrón-arquitectónico)
- [Diagrama de Capas](#diagrama-de-capas)
- [Diagrama Entidad-Relación](#diagrama-entidad-relación)
- [Capa de Controladores](#capa-de-controladores)
- [Capa de Servicios](#capa-de-servicios)
- [Capa de Repositorios](#capa-de-repositorios)
- [Capa de Modelos (Entidades)](#capa-de-modelos-entidades)
- [DTOs](#dtos)
- [Manejo de Excepciones](#manejo-de-excepciones)
- [Sistema de Posicionamiento](#sistema-de-posicionamiento)
- [Flujo de una Petición](#flujo-de-una-petición)
- [Configuración y Despliegue](#configuración-y-despliegue)

---

## Visión General

Kanban API es una API REST construida con **Spring Boot 3.3** que implementa la lógica backend para un tablero Kanban. El proyecto sigue una arquitectura en capas clásica de Spring, con separación clara de responsabilidades entre controladores, servicios y repositorios.

---

## Patrón Arquitectónico

Se utiliza una **Arquitectura en Capas (Layered Architecture)** con los siguientes principios:

- **Separación de responsabilidades**: cada capa tiene una función específica.
- **Inversión de dependencias**: los servicios se definen mediante interfaces (`IProyectoServicio`, `ITareaServicio`) y se inyectan mediante `@Autowired`.
- **Transaccionalidad**: las operaciones de negocio están protegidas con `@Transactional`.

---

## Diagrama de Capas

```
┌──────────────────────────────────────────────────────────────────┐
│                     Cliente (Frontend)                           │
│              React App (localhost:5173 / Vercel)                  │
└──────────────────────────┬───────────────────────────────────────┘
                           │ HTTP (JSON)
┌──────────────────────────▼───────────────────────────────────────┐
│                   Controladores (@RestController)                │
│                                                                  │
│  ProyectoControlador    TareaControlador    HealthControlador    │
│  /kanban-app/proyectos  /kanban-app/tareas  /health              │
└──────────────────────────┬───────────────────────────────────────┘
                           │ Inyección de dependencias
┌──────────────────────────▼───────────────────────────────────────┐
│                     Servicios (@Service)                          │
│                                                                  │
│  IProyectoServicio ←── ProyectoServicio                          │
│  ITareaServicio    ←── TareaServicio                             │
└──────────────────────────┬───────────────────────────────────────┘
                           │ Spring Data JPA
┌──────────────────────────▼───────────────────────────────────────┐
│                  Repositorios (JpaRepository)                    │
│                                                                  │
│  ProyectoRepositorio          TareaRepositorio                   │
│  (CRUD estándar)              (CRUD + queries personalizadas)    │
└──────────────────────────┬───────────────────────────────────────┘
                           │ Hibernate / JDBC
┌──────────────────────────▼───────────────────────────────────────┐
│                     PostgreSQL Database                           │
│                                                                  │
│  Tabla: proyectos              Tabla: tareas                     │
└──────────────────────────────────────────────────────────────────┘
```

---

## Diagrama Entidad-Relación

```
┌───────────────────────┐        ┌───────────────────────────┐
│      PROYECTOS        │        │          TAREAS            │
├───────────────────────┤        ├───────────────────────────┤
│ id (PK, SERIAL)       │───┐    │ id (PK, SERIAL)           │
│ nombre_proyecto       │   │    │ titulo                    │
│ descripción_proyecto  │   │    │ descripcion               │
│                       │   │    │ estado                    │
│                       │   │    │ prioridad                 │
│                       │   │    │ fecha_pendiente           │
│                       │   │    │ posicion                  │
│                       │   └──► │ proyecto_id (FK, NOT NULL)│
└───────────────────────┘        └───────────────────────────┘
         1                                    N
```

**Relación:** Un `Proyecto` tiene muchas `Tareas` (1:N). La relación se gestiona bidireccionalmente:
- En `Proyecto`: `@OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL)`
- En `Tarea`: `@ManyToOne` con `@JoinColumn(name = "proyecto_id", nullable = false)` y `@JsonIgnore` para evitar referencias circulares en la serialización JSON.

---

## Capa de Controladores

Los controladores reciben las peticiones HTTP y delegan la lógica a los servicios.

### `ProyectoControlador`

| Responsabilidad                                 | Ruta                            |
| ----------------------------------------------- | ------------------------------- |
| Listar proyectos                                | `GET /kanban-app/proyectos`     |
| Obtener proyecto por ID                         | `GET /kanban-app/proyectos/{id}`|
| Crear proyecto                                  | `POST /kanban-app/proyectos`    |
| Actualizar proyecto                             | `PUT /kanban-app/proyectos/{id}`|
| Eliminar proyecto (con cascade a sus tareas)    | `DELETE /kanban-app/proyectos/{id}` |

### `TareaControlador`

| Responsabilidad                                 | Ruta                                    |
| ----------------------------------------------- | --------------------------------------- |
| Listar tareas por proyecto                      | `GET /kanban-app/tareas/{projectId}`    |
| Crear tarea                                     | `POST /kanban-app/tareas`               |
| Actualizar tarea completa                       | `PUT /kanban-app/tareas/{id}`           |
| Actualizar estado/posición (parcial)            | `PATCH /kanban-app/estado/tareas/{taskId}` |
| Eliminar tarea                                  | `DELETE /kanban-app/tareas/{id}`        |
| Mover tarea entre columnas                      | `POST /kanban-app/mover`                |
| Reorganizar posiciones de una columna           | `POST /kanban-app/reorganizar`          |

### `VerificarDisponibilidadControlador`

Health check simple en `GET /health` que retorna `"OK"`.

### Clase interna `TareaRequest`

Dentro de `TareaControlador` existe una clase interna `TareaRequest` que actúa como DTO para la creación de tareas, incluyendo el `proyectoId` que no forma parte de la entidad `Tarea`.

---

## Capa de Servicios

Los servicios contienen la lógica de negocio y están definidos mediante interfaces para facilitar el testing y la extensibilidad.

### `IProyectoServicio` → `ProyectoServicio`

Operaciones CRUD estándar delegadas al repositorio:
- `listarProyectos()` — Retorna todos los proyectos
- `buscarProyectoPorId(Integer id)` — Busca por ID, retorna `null` si no existe
- `guardarProyecto(Proyecto proyecto)` — Crea o actualiza
- `eliminarProyecto(Proyecto proyecto)` — Elimina un proyecto

### `ITareaServicio` → `TareaServicio`

Lógica más compleja que incluye el sistema de posicionamiento:
- `ListarTareasPorProyecto(Integer proyectoId)` — Lista tareas, lanza excepción si no hay resultados
- `crearTarea(Tarea tarea, Integer proyectoId)` — Asocia la tarea al proyecto
- `guardarTarea(Tarea tarea)` — Persistencia directa
- `buscarTareaPorId(Integer tareaId)` — Busca por ID
- `eliminarTarea(Tarea tarea)` — Elimina y reajusta posiciones
- `moverTarea(...)` — Mueve entre columnas con reajuste de posiciones
- `reorganizarPosiciones(String estado)` — Normaliza las posiciones a enteros consecutivos
- `actualizarPosicionesOrigen(...)` — Reajusta posiciones en la columna de origen
- `actualizarPosicionesDestino(...)` — Reajusta posiciones en la columna de destino

---

## Capa de Repositorios

### `ProyectoRepositorio`

Extiende `JpaRepository<Proyecto, Integer>`. Solo usa métodos CRUD heredados.

### `TareaRepositorio`

Extiende `JpaRepository<Tarea, Integer>` e incluye queries personalizadas:

| Método                               | Tipo    | Descripción                                        |
| ------------------------------------- | ------- | -------------------------------------------------- |
| `findByProyectoId(Integer)`           | Derived | Busca tareas por ID de proyecto                    |
| `findByEstadoOrderByPosicion(String)` | Derived | Tareas por estado, ordenadas por posición           |
| `actualizarPosicionesColumnaOrigen()` | `@Query`| Decrementa posiciones > X en una columna            |
| `actualizarPosicionesColumnaDestino()`| `@Query`| Incrementa posiciones >= X en columna destino       |
| `obtenerNuevaPosicionFinal(String)`   | `@Query`| Obtiene MAX(posición) + 1 en una columna            |

---

## Capa de Modelos (Entidades)

### `Proyecto`

```java
@Entity
@Table(name = "proyectos")
public class Proyecto {
    @Id @GeneratedValue(strategy = IDENTITY)
    private Integer id;
    private String nombreProyecto;
    private String descripciónProyecto;

    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL)
    private List<Tarea> tareas;
}
```

### `Tarea`

```java
@Entity
@Table(name = "tareas")
public class Tarea {
    @Id @GeneratedValue(strategy = IDENTITY)
    private Integer id;
    private String titulo;
    private String descripcion;
    private String estado;         // "todo", "inProgress", "done"
    private String prioridad;      // "Low", "Medium", "High"
    private LocalDate fechaPendiente;
    private Double posicion;

    @ManyToOne
    @JoinColumn(name = "proyecto_id", nullable = false)
    @JsonIgnore
    private Proyecto proyecto;
}
```

---

## DTOs

### `EstadoTareaDTO`

Transporta el estado de una columna para reorganización:

```java
public class EstadoTareaDTO {
    private String estado;  // "todo", "inProgress", "done"
}
```

### `MoverTareaDTO`

Transporta los datos necesarios para mover una tarea entre columnas:

```java
public class MoverTareaDTO {
    private Integer idTarea;
    private String nuevoEstado;
    private Double nuevaPosicion;
}
```

---

## Manejo de Excepciones

### `RecursoNoEncontradoExcepcion`

Excepción personalizada anotada con `@ResponseStatus(HttpStatus.NOT_FOUND)`. Al ser lanzada, Spring automáticamente retorna un `404` con el mensaje proporcionado.

```java
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class RecursoNoEncontradoExcepcion extends RuntimeException {
    public RecursoNoEncontradoExcepcion(String mensaje) {
        super(mensaje);
    }
}
```

Se utiliza en los siguientes escenarios:
- Proyecto no encontrado por ID
- Tarea no encontrada por ID
- Proyecto inexistente al crear una tarea
- Lista vacía de tareas para un proyecto

---

## Sistema de Posicionamiento

El sistema de posicionamiento permite implementar drag & drop en el frontend. Cada tarea tiene un campo `posicion` (`Double`) que determina su orden dentro de una columna (estado).

### Operaciones de reposicionamiento

1. **Al eliminar una tarea:** Las posiciones superiores en la misma columna se decrementan en 1.

2. **Al mover una tarea entre columnas:**
   - Se decrementan las posiciones en la **columna de origen** (tareas con posición mayor).
   - Se incrementan las posiciones en la **columna de destino** (tareas con posición >= la nueva).
   - Si la nueva posición es `0` o negativa, se ejecuta una reorganización automática.

3. **Reorganización:** Asigna enteros consecutivos (1, 2, 3...) a todas las tareas de una columna, ordenadas por su posición actual. Esto normaliza las posiciones y previene problemas de precisión decimal.

### Flujo de mover tarea

```
1. Recibir: idTarea, nuevoEstado, nuevaPosicion
2. Obtener tarea actual de BD
3. Decrementar posiciones en columna ORIGEN (posiciones > actual)
4. Si nuevaPosicion == null → calcular posición final
5. Actualizar estado y posición de la tarea
6. Incrementar posiciones en columna DESTINO (posiciones >= nueva)
7. Si nuevaPosicion <= 0 → reorganizar columna destino
```

---

## Flujo de una Petición

Ejemplo: **Crear una tarea**

```
1. [Cliente]      POST /kanban-app/tareas  { titulo, estado, ..., proyectoId }
2. [Controlador]  TareaControlador.crearTarea() recibe TareaRequest
3. [Controlador]  Construye objeto Tarea desde TareaRequest
4. [Servicio]     TareaServicio.crearTarea(tarea, proyectoId)
5. [Servicio]     Busca Proyecto por ID via ProyectoServicio
6. [Servicio]     Asocia tarea.setProyecto(proyecto)
7. [Repositorio]  TareaRepositorio.save(tarea)
8. [Hibernate]    INSERT INTO tareas (...) VALUES (...)
9. [Controlador]  Retorna ResponseEntity<Tarea> con 200 OK
```

---

## Configuración y Despliegue

### Propiedades principales (`application.properties`)

| Propiedad                                           | Valor                            | Descripción                           |
| --------------------------------------------------- | -------------------------------- | ------------------------------------- |
| `spring.datasource.url`                             | `${DB_URL}`                      | URL JDBC (variable de entorno)        |
| `spring.datasource.username`                        | `${DB_USERNAME}`                 | Usuario BD (variable de entorno)      |
| `spring.datasource.password`                        | `${DB_PASSWORD}`                 | Contraseña BD (variable de entorno)   |
| `spring.jpa.hibernate.ddl-auto`                     | `update`                         | Auto-crear/actualizar esquema         |
| `spring.jpa.properties.hibernate.dialect`           | `PostgreSQLDialect`              | Dialecto SQL de Hibernate             |
| `spring.datasource.hikari.ssl`                      | `true`                           | Conexión SSL habilitada               |
| `spring.jpa.show-sql`                               | `true`                           | Mostrar queries SQL en logs           |
| `spring.jackson.time-zone`                          | `America/Bogota`                 | Zona horaria para JSON                |

### Logging (`logback-spring.xml`)

Configuración personalizada de logging con formato:

```
[thread] LEVEL: logger - message
```

Nivel de log root: `INFO`

### Docker

Build multi-stage:
1. **Etapa builder:** `maven:3.9.4-eclipse-temurin-21` compila el proyecto.
2. **Etapa runtime:** `eclipse-temurin:21-jdk-jammy` ejecuta el JAR resultante.

Puerto expuesto: `8080`
