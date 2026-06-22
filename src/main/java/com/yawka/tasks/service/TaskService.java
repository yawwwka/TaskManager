package com.yawka.tasks.service;

import com.yawka.tasks.dto.TaskCreateDto;
import com.yawka.tasks.dto.TaskResponseDto;
import com.yawka.tasks.entity.UserEntity;
import com.yawka.tasks.exception.InvalidPermission;
import com.yawka.tasks.exception.UserNotFoundException;
import com.yawka.tasks.mapper.TaskMapper;
import com.yawka.tasks.repository.TaskRepository;
import com.yawka.tasks.entity.TaskEntity;
import com.yawka.tasks.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserRepository userRepository;

    public List<TaskResponseDto> getAllTasks(String username) {
        UserEntity userEntity = getUserByUsername(username);

        return taskRepository.findByUser(userEntity).stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    public TaskResponseDto createTask(TaskCreateDto taskCreateDto, String username) {
        UserEntity userEntity = getUserByUsername(username);

        TaskEntity taskEntity = taskMapper.toEntity(taskCreateDto);
        taskEntity.setUser(userEntity);
        TaskEntity saved = taskRepository.save(taskEntity);

        return taskMapper.toDto(saved);
    }

    public TaskResponseDto getTaskById(Long id, String username) {
        UserEntity userEntity = getUserByUsername(username);
        TaskEntity taskEntity = getTaskByIdAndUser(id, userEntity);

        return taskMapper.toDto(taskEntity);
    }

    public TaskResponseDto updateTaskById(Long id, TaskCreateDto updateDto, String username) {
        UserEntity userEntity = getUserByUsername(username);
        TaskEntity existingTask = getTaskByIdAndUser(id, userEntity);

        existingTask.setTitle(updateDto.getTitle());
        existingTask.setDescription(updateDto.getDescription());
        existingTask.setStatus(updateDto.getStatus());

        TaskEntity updated = taskRepository.save(existingTask);
        return taskMapper.toDto(updated);
    }

    public void deleteTaskById(Long id, String username) {
        UserEntity userEntity = getUserByUsername(username);
        TaskEntity taskEntity = getTaskByIdAndUser(id, userEntity);
        taskRepository.delete(taskEntity);
    }

    private UserEntity getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));
    }

    private TaskEntity getTaskByIdAndUser(Long id, UserEntity user) {
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found: " + id));

        if (!task.getUser().getId().equals(user.getId())) {
            throw new InvalidPermission("You don't have permission to access this task");
        }

        return task;
    }
}
