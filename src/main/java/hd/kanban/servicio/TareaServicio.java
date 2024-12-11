package hd.kanban.servicio;

import hd.kanban.excepcion.RecursoNoEncontradoExcepcion;
import hd.kanban.modelo.Proyecto;
import hd.kanban.modelo.Tarea;
import hd.kanban.repositorio.TareaRepositorio;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TareaServicio implements ITareaServicio {

    @Autowired
    private TareaRepositorio tareaRepositorio;

    @Autowired
    private ProyectoServicio proyectoServicio;

    //Umbral para reorganizar cuando detectamos más de 3 decimales
    //private static final int MAX_DECIMALES_PERMITIDOS = 2;

    @Override
    @Transactional
    public List<Tarea> ListarTareasPorProyecto(Integer proyectoId) {
        List<Tarea> tareas = tareaRepositorio.findByProyectoId(proyectoId);
        if (tareas == null || tareas.isEmpty())
            throw new RecursoNoEncontradoExcepcion("No se encontraron tareas para el proyecto con ID:" + proyectoId + ", No Existe");
        return tareas;
    }

    @Override
    @Transactional
    public Tarea crearTarea(Tarea tarea, Integer proyectoId) {
        Proyecto proyecto = proyectoServicio.buscarProyectoPorId(proyectoId);
        if(proyecto == null)
            throw new RecursoNoEncontradoExcepcion("El proyecto con ID:" + proyectoId + ", No Existe");
        tarea.setProyecto(proyecto);
        return tareaRepositorio.save(tarea);
    }

    @Override
    @Transactional
    public Tarea guardarTarea(Tarea tarea) {
        return  tareaRepositorio.save(tarea);
    }

    @Override
    @Transactional
    public Tarea buscarTareaPorId(Integer tareaId) {
        return tareaRepositorio.findById(tareaId).orElse(null);
    }

    @Override
    @Transactional
    public void eliminarTarea(Tarea tarea) {
        tareaRepositorio.actualizarPosicionesColumnaOrigen(tarea.getEstado(), tarea.getPosicion());
        tareaRepositorio.delete(tarea);
    }

    @Override
    @Transactional
    public void moverTarea(Integer idTarea, String nuevoEstado, Double nuevaPosicion) {
        Tarea tarea = tareaRepositorio.findById(idTarea)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        // Actualizar posiciones en la columna de origen
        tareaRepositorio.actualizarPosicionesColumnaOrigen(tarea.getEstado(), tarea.getPosicion());

        // Calcular nueva posición en la columna de destino si no se proporciona
        if (nuevaPosicion == null) {
            nuevaPosicion = tareaRepositorio.obtenerNuevaPosicionFinal(nuevoEstado);
        }

        String estadoActual = tarea.getEstado();
        // Actualizar el estado y la posición de la tarea
        tarea.setEstado(nuevoEstado);
        tarea.setPosicion(nuevaPosicion);
        tareaRepositorio.save(tarea);
        tareaRepositorio.actualizarPosicionesColumnaDestino(nuevoEstado, nuevaPosicion, tarea.getId());

        if (nuevaPosicion == 0 || nuevaPosicion < 0) {
            reorganizarPosiciones(nuevoEstado); // Reorganizar la columna actual
        }
    }

    @Override
    @Transactional
    public void reorganizarPosiciones(String estado) {
        List<Tarea> tareas = tareaRepositorio.findByEstadoOrderByPosicion(estado);

        // Reorganizar posiciones asignando enteros consecutivos
        for (int i = 0; i < tareas.size(); i++) {
            tareas.get(i).setPosicion((double) (i + 1));
        }

        tareaRepositorio.saveAll(tareas);
    }

    @Override
    @Transactional
    public void actualizarPosicionesOrigen(String estado, Double posicion){
        tareaRepositorio.actualizarPosicionesColumnaOrigen(estado, posicion);
    }

    @Override
    @Transactional
    public void actualizarPosicionesDestino(String estado, Double posicion, Integer id) {
        tareaRepositorio.actualizarPosicionesColumnaDestino(estado, posicion, id);
    }

//    private boolean necesitaReorganizacion(String estado) {
//        List<Tarea> tareas = tareaRepositorio.findByEstadoOrderByPosicion(estado);
//        for (Tarea tarea : tareas) {
//            if (tieneDemasiadosDecimales(tarea.getPosicion())) {
//                return true;  // Se detectaron demasiados decimales, es necesario reorganizar
//            }
//        }
//        return false;  // No es necesario reorganizar
//    }

//    private boolean tieneDemasiadosDecimales(double posicion) {
//        String[] partes = String.valueOf(posicion).split("\\.");
//        if (partes.length > 1) {
//            String decimales = partes[1];
//            return decimales.length() >= MAX_DECIMALES_PERMITIDOS;
//        }
//        return false;
//    }

}
