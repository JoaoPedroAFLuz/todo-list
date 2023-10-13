package br.com.joaopedroafluz.todolist.task;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping
    public ResponseEntity create(@RequestBody TaskModel task, HttpServletRequest request) {
        final var userId = request.getAttribute("userId");

        task.setUserId((UUID) userId);

        if (task.getStartAt().isAfter(task.getEndAt()) || task.getStartAt().isEqual(task.getEndAt())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("O fim da tarefa deve ser após o início da tarefa");
        }

        final var now = LocalDateTime.now();

        if (now.isAfter(task.getStartAt()) || now.isAfter(task.getEndAt())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("As datas e horas de início e do fim da tarefa devem ser maior que a data e hora atual");
        }

        var taskCreated = this.taskRepository.save(task);

        return ResponseEntity.status(HttpStatus.CREATED).body(taskCreated);
    }

}
