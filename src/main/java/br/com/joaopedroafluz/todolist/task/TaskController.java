package br.com.joaopedroafluz.todolist.task;

import br.com.joaopedroafluz.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @GetMapping
    public List<TaskModel> findAll(HttpServletRequest request) {
        final var userId = (UUID) request.getAttribute("userId");

        return this.taskRepository.findAllByUserId(userId);
    }

    @PostMapping
    public ResponseEntity create(@RequestBody TaskModel task, HttpServletRequest request) {
        final var userId = (UUID)  request.getAttribute("userId");

        task.setUserId(userId);

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

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable UUID id, @RequestBody TaskModel taskModel, HttpServletRequest request) {
        final var userId = (UUID)  request.getAttribute("userId");

        taskModel.setUserId(userId);
        taskModel.setId(id);

        final var task = this.taskRepository.findById(id);

        if (task.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Tarefa não encontrada");
        }

        if (task.get().getStartAt().isAfter(task.get().getEndAt()) || task.get().getStartAt().isEqual(task.get().getEndAt())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("O fim da tarefa deve ser após o início da tarefa");
        }

        final var now = LocalDateTime.now();

        if (now.isAfter(task.get().getStartAt()) || now.isAfter(task.get().getEndAt())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("As datas e horas de início e do fim da tarefa devem ser maior que a data e hora atual");
        }



        Utils.copyNonNullProperties(taskModel, task.get());

        var taskUpdated = this.taskRepository.save(task.get());

        return ResponseEntity.status(HttpStatus.OK).body(taskUpdated);
    }


}
