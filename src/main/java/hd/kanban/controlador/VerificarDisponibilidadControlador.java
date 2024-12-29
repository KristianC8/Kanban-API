package hd.kanban.controlador;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
@CrossOrigin(origins = {"http://localhost:5173", "https://kanban-app-front.vercel.app/"})
public class VerificarDisponibilidadControlador {

    @GetMapping
    public ResponseEntity<String> checkHealth() {
        return ResponseEntity.ok("OK");
    }
}
