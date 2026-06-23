package com.yawka.tasks.service;

import com.yawka.tasks.dto.TaskCreateDto;
import com.yawka.tasks.dto.TaskResponseDto;
import com.yawka.tasks.entity.UserEntity;
import com.yawka.tasks.mapper.TaskMapper;
import com.yawka.tasks.repository.TaskRepository;
import com.yawka.tasks.entity.TaskEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserHelper userHelper;


    public List<TaskResponseDto> getAllTasks(String username) {
        UserEntity userEntity = userHelper.getUserByUsername(username);

        return taskRepository.findByUser(userEntity).stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    public TaskResponseDto createTask(TaskCreateDto taskCreateDto, String username) {
        UserEntity userEntity = userHelper.getUserByUsername(username);
        TaskEntity taskEntity = taskMapper.toEntity(taskCreateDto);
        taskEntity.setUser(userEntity);
        TaskEntity saved = taskRepository.save(taskEntity);

        return taskMapper.toDto(saved);
    }

    public TaskResponseDto getTaskById(Long id, String username) {
        UserEntity userEntity = userHelper.getUserByUsername(username);
        TaskEntity taskEntity = userHelper.getTaskByIdAndUser(id, userEntity);

        return taskMapper.toDto(taskEntity);
    }

    public TaskResponseDto updateTaskById(Long id, TaskCreateDto updateDto, String username) {
        UserEntity userEntity = userHelper.getUserByUsername(username);
        TaskEntity existingTask = userHelper.getTaskByIdAndUser(id, userEntity);

        existingTask.setTitle(updateDto.getTitle());
        existingTask.setDescription(updateDto.getDescription());
        existingTask.setStatus(updateDto.getStatus());

        TaskEntity updated = taskRepository.save(existingTask);
        return taskMapper.toDto(updated);
    }

    public void deleteTaskById(Long id, String username) {
        UserEntity userEntity = userHelper.getUserByUsername(username);
        TaskEntity taskEntity = userHelper.getTaskByIdAndUser(id, userEntity);
        taskRepository.delete(taskEntity);
    }
}
