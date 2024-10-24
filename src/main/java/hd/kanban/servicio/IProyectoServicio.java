package hd.kanban.servicio;

import hd.kanban.modelo.Proyecto;

import java.util.List;

public interface IProyectoServicio {

    public List<Proyecto> listarProyectos();

    public Proyecto buscarProyectoPorId(Integer id);

    public Proyecto guardarProyecto(Proyecto proyecto);

    public void eliminarProyecto(Proyecto proyecto);


}
