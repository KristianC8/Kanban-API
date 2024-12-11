package hd.kanban.servicio;

import hd.kanban.modelo.Tarea;

import java.util.List;

public interface ITareaServicio {

    public List<Tarea> ListarTareasPorProyecto(Integer proyectoId);

    public  Tarea crearTarea(Tarea tarea, Integer proyectoId);

    public  Tarea guardarTarea(Tarea tarea);

    public Tarea buscarTareaPorId(Integer tareaId);

    public void  eliminarTarea(Tarea tarea);

    public void moverTarea(Integer idTarea, String nuevoEstado, Double nuevaPosicion);

    public void reorganizarPosiciones(String estado);

    public void actualizarPosicionesOrigen(String estado, Double posicion);

    public void actualizarPosicionesDestino(String estado, Double posicion, Integer id );
}
