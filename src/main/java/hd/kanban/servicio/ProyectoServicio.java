package hd.kanban.servicio;

import hd.kanban.modelo.Proyecto;
import hd.kanban.repositorio.ProyectoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProyectoServicio implements IProyectoServicio{

    @Autowired
    private ProyectoRepositorio proyectoRepositorio;

    @Override
    public List<Proyecto> listarProyectos() {
        return proyectoRepositorio.findAll();
    }

    @Override
    public Proyecto buscarProyectoPorId(Integer id) {
        return proyectoRepositorio.findById(id).orElse(null);
    }

    @Override
    public Proyecto guardarProyecto(Proyecto proyecto) {
        return proyectoRepositorio.save(proyecto);
    }

    @Override
    public void eliminarProyecto(Proyecto proyecto) {
        proyectoRepositorio.delete(proyecto);
    }
}
