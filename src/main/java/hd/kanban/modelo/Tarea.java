package hd.kanban.modelo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.text.DateFormat;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Tarea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String titulo;
    private String descripcion;
    private String estado; // To Do, In Progress, Done
    private String prioridad; // Low, Medium, High
    private LocalDate fechaPendiente;
    private Integer posicion;

    @ManyToOne
    @JoinColumn(name = "proyecto_id", nullable = false)
    @JsonIgnore
//    @JsonBackReference
    private Proyecto proyecto;

}
