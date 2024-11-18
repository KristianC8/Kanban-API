package hd.kanban.controlador;

import hd.kanban.dto.EstadoTareaDTO;
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
@CrossOrigin(origins = {"http://localhost:5173", "http://192.168.1.36:5173", "http://192.168.1.41:5173"})
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
        Tarea nuevaTarea = tareaServicio.crearTarea(tarea, tareaRequest.proyectoId);
        return ResponseEntity.ok(nuevaTarea);
    }

    @PutMapping("/tareas/{id}")
    public ResponseEntity<Tarea> ActualizarTarea(@PathVariable Integer id, @RequestBody Tarea tareaRecibida){
        Tarea tarea = tareaServicio.buscarTareaPorId(id);
        if(tarea == null)
            throw new RecursoNoEncontradoExcepcion("La tarea con id:" + id + ", No Existe");
        tarea.setTitulo(tareaRecibida.getTitulo());
        tarea.setDescripcion(tareaRecibida.getDescripcion());
        tarea.setEstado(tareaRecibida.getEstado());
        tarea.setPrioridad(tareaRecibida.getPrioridad());
        tarea.setFechaPendiente(tareaRecibida.getFechaPendiente());
        tareaServicio.guardarTarea(tarea);
        return ResponseEntity.ok(tarea);
    }

    @PutMapping("/estado/tareas/{id}")
    public ResponseEntity<Tarea> ActualizarEstadoTarea(@PathVariable Integer id, @RequestBody EstadoTareaDTO estadoTarea){
        Tarea tarea = tareaServicio.buscarTareaPorId(id);
        if(tarea == null)
            throw new RecursoNoEncontradoExcepcion("La tarea con id:" + id + ", No Existe");
        tarea.setEstado(estadoTarea.getEstado());
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

}
