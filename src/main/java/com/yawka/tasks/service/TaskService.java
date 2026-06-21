package com.yawka.tasks.service;

import com.yawka.tasks.dto.TaskCreateDto;
import com.yawka.tasks.dto.TaskResponseDto;
import com.yawka.tasks.entity.TaskStatus;
import com.yawka.tasks.mapper.TaskMapper;
import com.yawka.tasks.repository.TaskRepository;
import com.yawka.tasks.entity.TaskEntity;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public List<TaskResponseDto> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    public TaskResponseDto createTask(TaskCreateDto taskCreateDto) {
        TaskEntity entity = taskMapper.toEntity(taskCreateDto);
        TaskEntity saved = taskRepository.save(entity);

        return taskMapper.toDto(saved);
    }

    public TaskResponseDto getTaskById(Long id) {
        TaskEntity entity = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + id));

        return taskMapper.toDto(entity);
    }

    public TaskResponseDto updateTaskById(Long id, TaskCreateDto updateDto) {
        TaskEntity existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + id));

        existingTask.setTitle(updateDto.getTitle());
        existingTask.setDescription(updateDto.getDescription());
        existingTask.setStatus(updateDto.getStatus());

        TaskEntity updated = taskRepository.save(existingTask);
        return taskMapper.toDto(updated);
    }

    public void deleteTaskById(Long id) {
        if (!taskRepository.existsById(id))
            throw new EntityNotFoundException("No such entity");

        taskRepository.deleteById(id);
    }

    public List<TaskResponseDto> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status).stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }
}
