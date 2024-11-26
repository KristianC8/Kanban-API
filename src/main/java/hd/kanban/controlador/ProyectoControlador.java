package hd.kanban.controlador;

import hd.kanban.excepcion.RecursoNoEncontradoExcepcion;
import hd.kanban.modelo.Proyecto;
import hd.kanban.servicio.ProyectoServicio;
import hd.kanban.servicio.TareaServicio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
//http:localhost:8080/kanban-app/
@RequestMapping("kanban-app")
@CrossOrigin(value = "http://localhost:5173")
public class ProyectoControlador {
    private static final Logger logger = LoggerFactory.getLogger(ProyectoControlador.class);

    @Autowired
    private ProyectoServicio proyectoServicio;

//    @Autowired
//    private TareaServicio tareaServicio;

    @GetMapping("/proyectos")
    public List<Proyecto> obteberProyectos(){
        var proyectos = proyectoServicio.listarProyectos();
        proyectos.forEach((proyecto -> logger.info("Projects: " + proyecto.toString())));
        return proyectos;
    }

    @GetMapping("proyectos/{id}")
    public ResponseEntity<Proyecto> ObtenerProyectoPorId(@PathVariable Integer id){
//        tareaServicio.reorganizarPosiciones("todo");
//        tareaServicio.reorganizarPosiciones("inProgress");
//        tareaServicio.reorganizarPosiciones("done");
        Proyecto proyecto  =  proyectoServicio.buscarProyectoPorId(id);
        if(proyecto == null)
            throw new RecursoNoEncontradoExcepcion("Proyecto con el id: " + id +  ", No existe");
        logger.info("Project: " + proyecto.toString());
        return ResponseEntity.ok(proyecto);
    }

    @PostMapping("/proyectos")
    public Proyecto CrearProyecto(@RequestBody Proyecto proyecto){
        logger.info("Create project: " + proyecto);
        return proyectoServicio.guardarProyecto(proyecto);
    }

    @PutMapping("proyectos/{id}")
    public ResponseEntity<Proyecto> ActualizarProyecto(@PathVariable Integer id, @RequestBody Proyecto proyectoRecibido){
        Proyecto proyecto = proyectoServicio.buscarProyectoPorId(id);
        if(proyecto == null)
            throw new RecursoNoEncontradoExcepcion("El proyecto con el id:" + id + ", No Existe");
        proyecto.setNombreProyecto(proyectoRecibido.getNombreProyecto());
        proyecto.setDescripciónProyecto(proyectoRecibido.getDescripciónProyecto());
        proyectoServicio.guardarProyecto(proyecto);
        logger.info("Updated Project:" + proyecto);
        return ResponseEntity.ok(proyecto);
    }

    @DeleteMapping("/proyectos/{id}")
    public ResponseEntity<Map<String, Boolean>> Elminarproyecto(@PathVariable Integer id){
        Proyecto proyecto = proyectoServicio.buscarProyectoPorId(id);
        if(proyecto == null)
            throw new RecursoNoEncontradoExcepcion("El id:" + id + ", No Existe");
        proyectoServicio.eliminarProyecto(proyecto);
        Map<String, Boolean> respuesta = new HashMap<>();
        respuesta.put("eliminado", Boolean.TRUE);
        return ResponseEntity.ok(respuesta);
    }
}

