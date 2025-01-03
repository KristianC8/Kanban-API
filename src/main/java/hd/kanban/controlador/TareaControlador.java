package hd.kanban.controlador;

import hd.kanban.dto.EstadoTareaDTO;
import hd.kanban.dto.MoverTareaDTO;
import hd.kanban.excepcion.RecursoNoEncontradoExcepcion;
import hd.kanban.modelo.Tarea;
import hd.kanban.servicio.TareaServicio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("kanban-app")
@CrossOrigin(origins = {"http://localhost:5173", "https://kanban-app-front.vercel.app/"})
public class TareaControlador {
    private static final Logger logger = LoggerFactory.getLogger(TareaControlador.class);

    @Autowired
    private TareaServicio tareaServicio;

    public static class TareaRequest {
        public String titulo;
        public String descripcion;
        public String estado; // To Do, In Progress, Done
        public String prioridad; // Low, Medium, High
        public Date fechaPendiente;
        public Double posicion;
        public Integer proyectoId;
    }

    @GetMapping("/tareas/{projectId}")
    public List<Tarea> obtenerTareas(@PathVariable Integer projectId) {
        return tareaServicio.ListarTareasPorProyecto(projectId);
    }

    @PostMapping("/tareas")
    public ResponseEntity<Tarea> crearTarea(@RequestBody TareaRequest tareaRequest) {
        Tarea tarea = new Tarea();
        tarea.setTitulo(tareaRequest.titulo);
        tarea.setDescripcion(tareaRequest.descripcion);
        tarea.setEstado(tareaRequest.estado);
        tarea.setPrioridad(tareaRequest.prioridad);
        tarea.setFechaPendiente(tareaRequest.fechaPendiente.toLocalDate());
        tarea.setPosicion(tareaRequest.posicion);
        Tarea nuevaTarea = tareaServicio.crearTarea(tarea, tareaRequest.proyectoId);
        return ResponseEntity.ok(nuevaTarea);
    }

    @PutMapping("/tareas/{id}")
    public ResponseEntity<Tarea> ActualizarTarea(@PathVariable Integer id, @RequestBody Tarea tareaRecibida){
        Tarea tarea = tareaServicio.buscarTareaPorId(id);
        if(tarea == null)
            throw new RecursoNoEncontradoExcepcion("La tarea con id:" + id + ", No Existe");
        tareaServicio.actualizarPosicionesOrigen(tarea.getEstado(), tarea.getPosicion());
        tarea.setTitulo(tareaRecibida.getTitulo());
        tarea.setDescripcion(tareaRecibida.getDescripcion());
        tarea.setEstado(tareaRecibida.getEstado());
        tarea.setPrioridad(tareaRecibida.getPrioridad());
        tarea.setFechaPendiente(tareaRecibida.getFechaPendiente());
        tarea.setPosicion(tareaRecibida.getPosicion());
        tareaServicio.guardarTarea(tarea);
        return ResponseEntity.ok(tarea);
    }

//    @PutMapping("/estado/tareas/{id}")
//    public ResponseEntity<Tarea> ActualizarEstadoTarea(@PathVariable Integer id, @RequestBody EstadoTareaDTO estadoTarea){
//        Tarea tarea = tareaServicio.buscarTareaPorId(id);
//        if(tarea == null)
//            throw new RecursoNoEncontradoExcepcion("La tarea con id:" + id + ", No Existe");
//        tarea.setEstado(estadoTarea.getEstado());
//        tareaServicio.guardarTarea(tarea);
//        return ResponseEntity.ok(tarea);
//    }

    @PatchMapping("/estado/tareas/{taskId}")
    public ResponseEntity<?> ActualizarEstadoTarea(
            @PathVariable Integer taskId,
            @RequestBody Map<String, Object> updates
    ){
        Tarea tarea = tareaServicio.buscarTareaPorId(taskId);
        if(tarea == null)
            throw new RecursoNoEncontradoExcepcion("La tarea con id:" + taskId + ", No Existe");
        // 2. Actualizar los campos especificados
//        if (updates.containsKey("posicion")) {
//            tarea.setPosicion((Double) updates.get("posicion"));
//        }
        tareaServicio.actualizarPosicionesOrigen(tarea.getEstado(), tarea.getPosicion());
        if (updates.containsKey("posicion")) {
            Object posicionObj = updates.get("posicion");
            if (posicionObj instanceof Number) {
                tarea.setPosicion(((Number) posicionObj).doubleValue());
            } else {
                throw new IllegalArgumentException("El valor de 'posicion' debe ser un número.");
            }
        }
        if (updates.containsKey("estado")) {
            tarea.setEstado((String) updates.get("estado"));
        }
        // 3. Guardar los cambios
        tareaServicio.guardarTarea(tarea);
        return ResponseEntity.ok(tarea);
    }

    @DeleteMapping("/tareas/{id}")
    public ResponseEntity<Map<String, Boolean>> eliminarTarea(@PathVariable Integer id){
        Tarea tarea = tareaServicio.buscarTareaPorId(id);
        if(tarea == null)
            throw new RecursoNoEncontradoExcepcion("La tarea con id:" + id + ", No Existe");
        tareaServicio.eliminarTarea(tarea);
        Map<String, Boolean> respuesta = new HashMap<>();
        respuesta.put("eliminado", Boolean.TRUE);
        return ResponseEntity.ok(respuesta);
    }

    // Mover tarea entre columnas
    @PostMapping("/mover")
    public ResponseEntity<Map<String, Boolean>> moverTarea(
            @RequestBody MoverTareaDTO mover) {
        tareaServicio.moverTarea(mover.getIdTarea(), mover.getNuevoEstado(), mover.getNuevaPosicion());
//        return ResponseEntity.ok().build();
        Map<String, Boolean> respuesta = new HashMap<>();
        respuesta.put("Moved", Boolean.TRUE);
        return ResponseEntity.ok(respuesta);
    }

    // Reorganizar posiciones en una columna
    @PostMapping("/reorganizar")
    public ResponseEntity<Void> reorganizar(@RequestBody EstadoTareaDTO estado) {
        tareaServicio.reorganizarPosiciones(estado.getEstado());
        return ResponseEntity.ok().build();
    }

}
