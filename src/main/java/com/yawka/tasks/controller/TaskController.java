package com.yawka.tasks.controller;

import com.yawka.tasks.dto.TaskCreateDto;
import com.yawka.tasks.dto.TaskResponseDto;
import com.yawka.tasks.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController()
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> getAllTasks(Authentication authentication) {
        return ResponseEntity.ok(taskService.getAllTasks(authentication.getName()));
    }

    @PostMapping("")
    public ResponseEntity<TaskResponseDto> createTask(@Valid @RequestBody TaskCreateDto task, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.createTask(task, authentication.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(taskService.getTaskById(id, authentication.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDto> updateTaskById(@Valid @PathVariable Long id,
                                                     @RequestBody TaskCreateDto task,
                                                          Authentication authentication) {
        return ResponseEntity.ok(taskService.updateTaskById(id, task, authentication.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskById(@PathVariable Long id, Authentication authentication) {
        taskService.deleteTaskById(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
