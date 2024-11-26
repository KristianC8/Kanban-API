package hd.kanban.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class MoverTareaDTO {
    private Integer idTarea;
    private String nuevoEstado;
    private Double nuevaPosicion;
}
