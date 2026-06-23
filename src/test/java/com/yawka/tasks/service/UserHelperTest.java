package com.yawka.tasks.service;

import com.yawka.tasks.entity.TaskEntity;
import com.yawka.tasks.entity.UserEntity;
import com.yawka.tasks.exception.InvalidPermission;
import com.yawka.tasks.exception.UserNotFoundException;
import com.yawka.tasks.repository.TaskRepository;
import com.yawka.tasks.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserHelperTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private UserHelper userHelper;

    @Test
    void shouldReturnUserWhenUsernameExists() {
        // given
        String username = "testuser";
        UserEntity user = new UserEntity();
        user.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // when
        UserEntity found = userHelper.getUserByUsername(username);

        // then
        assertThat(found).isNotNull();
        assertThat(found.getUsername()).isEqualTo(username);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // given
        String username = "nonexistent";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> userHelper.getUserByUsername(username))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found: " + username);
    }

    @Test
    void shouldReturnTaskWhenUserOwnsTask() {
        // given
        Long taskId = 1L;
        UserEntity user = new UserEntity();
        user.setId(1L);

        TaskEntity task = new TaskEntity();
        task.setId(taskId);
        task.setUser(user);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        // when
        TaskEntity found = userHelper.getTaskByIdAndUser(taskId, user);

        // then
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(taskId);
        assertThat(found.getUser()).isEqualTo(user);
    }

    @Test
    void shouldThrowExceptionWhenTaskNotFound() {
        // given
        Long taskId = 999L;
        UserEntity user = new UserEntity();
        user.setId(1L);

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> userHelper.getTaskByIdAndUser(taskId, user))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Task not found: " + taskId);
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotOwnTask() {
        // given
        Long taskId = 1L;
        UserEntity user = new UserEntity();
        user.setId(1L);

        UserEntity otherUser = new UserEntity();
        otherUser.setId(2L);

        TaskEntity task = new TaskEntity();
        task.setId(taskId);
        task.setUser(otherUser);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        // when/then
        assertThatThrownBy(() -> userHelper.getTaskByIdAndUser(taskId, user))
                .isInstanceOf(InvalidPermission.class)
                .hasMessageContaining("You don't have permission to access this task");
    }
}