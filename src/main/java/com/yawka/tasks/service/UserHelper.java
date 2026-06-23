package com.yawka.tasks.service;

import com.yawka.tasks.entity.TaskEntity;
import com.yawka.tasks.entity.UserEntity;
import com.yawka.tasks.exception.InvalidPermission;
import com.yawka.tasks.exception.UserNotFoundException;
import com.yawka.tasks.repository.TaskRepository;
import com.yawka.tasks.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserHelper {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public UserEntity getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));
    }

    public TaskEntity getTaskByIdAndUser(Long id, UserEntity user) {
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found: " + id));

        if (!task.getUser().getId().equals(user.getId())) {
            throw new InvalidPermission("You don't have permission to access this task");
        }

        return task;
    }

}
