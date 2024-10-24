package hd.kanban.servicio;

import hd.kanban.excepcion.RecursoNoEncontradoExcepcion;
import hd.kanban.modelo.Proyecto;
import hd.kanban.modelo.Tarea;
import hd.kanban.repositorio.TareaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TareaServicio implements ITareaServicio {

    @Autowired
    private TareaRepositorio tareaRepositorio;

    @Autowired
    private ProyectoServicio proyectoServicio;

    @Override
    public List<Tarea> ListarTareasPorProyecto(Integer proyectoId) {
        List<Tarea> tareas = tareaRepositorio.findByProyectoId(proyectoId);
        if (tareas == null || tareas.isEmpty())
            throw new RecursoNoEncontradoExcepcion("No se encontraron tareas para el proyecto con ID:" + proyectoId + ", No Existe");
        return tareas;
    }

    @Override
    public Tarea crearTarea(Tarea tarea, Integer proyectoId) {
        Proyecto proyecto = proyectoServicio.buscarProyectoPorId(proyectoId);
        if(proyecto == null)
            throw new RecursoNoEncontradoExcepcion("El proyecto con ID:" + proyectoId + ", No Existe");
        tarea.setProyecto(proyecto);
        return tareaRepositorio.save(tarea);
    }

    @Override
    public Tarea guardarTarea(Tarea tarea) {
        return  tareaRepositorio.save(tarea);
    }

    @Override
    public Tarea buscarTareaPorId(Integer tareaId) {
        return tareaRepositorio.findById(tareaId).orElse(null);
    }

    @Override
    public void eliminarTarea(Tarea tarea) {
        tareaRepositorio.delete(tarea);
    }
}
