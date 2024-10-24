package hd.kanban.modelo;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.scheduling.config.Task;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Proyecto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nombreProyecto;
    private String descripciónProyecto;
    //private String creadorProyecto;

//    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL, orphanRemoval = true)
    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL)
    @ToString.Exclude
//    @JsonManagedReference
    private List<Tarea> tareas;
}
