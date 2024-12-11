package hd.kanban.repositorio;

import hd.kanban.modelo.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TareaRepositorio extends JpaRepository<Tarea, Integer> {
    List<Tarea> findByProyectoId(Integer proyectoId);

    // Obtener todas las tareas de un estado ordenadas por posición
    List<Tarea> findByEstadoOrderByPosicion(String estado);

    // Disminuir posiciones mayores en la columna de origen
    @Modifying
    @Query("UPDATE Tarea t SET t.posicion = t.posicion - 1 WHERE t.estado = :estado AND t.posicion > :posicion")
    void actualizarPosicionesColumnaOrigen(@Param("estado") String estado, @Param("posicion") double posicion);

    // Aumentar posiciones mayores en la columna de destino
    @Modifying
    @Query("UPDATE Tarea t SET t.posicion = t.posicion + 1 WHERE t.estado = :estado AND t.posicion >= :posicion AND t.id <> :id")
    void actualizarPosicionesColumnaDestino(@Param("estado") String estado, @Param("posicion") double posicion, @Param("id") Integer id);

    // Obtener la posición máxima en la columna de destino
    @Query("SELECT COALESCE(MAX(t.posicion), 0) + 1 FROM Tarea t WHERE t.estado = :estado")
    double obtenerNuevaPosicionFinal(@Param("estado") String estado);
}
