package upeu.edu.pe.security.domain.services.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.security.application.dto.PasswordChangeDto;
import upeu.edu.pe.security.application.dto.UserRequestDto;
import upeu.edu.pe.security.application.dto.UserResponseDto;
import upeu.edu.pe.security.application.dto.UserUpdateDto;
import upeu.edu.pe.security.application.mapper.UserMapper;
import upeu.edu.pe.security.domain.entities.User;
import upeu.edu.pe.security.domain.enums.UserRole;
import upeu.edu.pe.security.domain.enums.UserStatus;
import upeu.edu.pe.security.domain.repositories.UserRepository;
import upeu.edu.pe.security.domain.services.UserService;
import upeu.edu.pe.security.infrastructure.utils.PasswordEncoder;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
@Transactional
public class UserServiceImpl implements UserService {

    @Inject
    UserRepository userRepository;

    @Inject
    UserMapper userMapper;

    @Inject
    PasswordEncoder passwordEncoder;

    @Override
    public List<UserResponseDto> findAll() {
        List<User> users = userRepository.getAllUsers();
        return userMapper.toResponseDtoList(users);
    }

    @Override
    public List<UserResponseDto> findAllByStatus(UserStatus status) {
        List<User> users = userRepository.findAllByStatus(status);
        return userMapper.toResponseDtoList(users);
    }

    @Override
    public List<UserResponseDto> findAllByRole(UserRole role) {
        List<User> users = userRepository.findAllByRole(role);
        return userMapper.toResponseDtoList(users);
    }

    @Override
    public UserResponseDto findById(Long id) {
        User user = userRepository.getUserById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
        return userMapper.toResponseDto(user);
    }

    @Override
    public UserResponseDto findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found with username: " + username));
        return userMapper.toResponseDto(user);
    }

    @Override
    public UserResponseDto create(UserRequestDto requestDto) {
        // Validate unique constraints
        if (userRepository.existsByUsername(requestDto.getUsername())) {
            throw new BusinessException("Username '" + requestDto.getUsername() + "' already exists");
        }

        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new BusinessException("Email '" + requestDto.getEmail() + "' already exists");
        }

        User user = userMapper.toEntity(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));

        User savedUser = userRepository.saveUser(user);
        return userMapper.toResponseDto(savedUser);
    }

    @Override
    public UserResponseDto update(Long id, UserUpdateDto updateDto) {
        User existingUser = userRepository.getUserById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        // Validate email uniqueness if being updated
        if (updateDto.getEmail() != null && !updateDto.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmail(updateDto.getEmail())) {
                throw new BusinessException("Email '" + updateDto.getEmail() + "' already exists");
            }
        }

        userMapper.updateEntityFromDto(updateDto, existingUser);
        User updatedUser = userRepository.saveUser(existingUser);
        return userMapper.toResponseDto(updatedUser);
    }

    @Override
    public void deleteById(Long id) {
        if (!userRepository.getUserById(id).isPresent()) {
            throw new NotFoundException("User not found with id: " + id);
        }
        userRepository.removeUserById(id);
    }

    @Override
    public void changePassword(Long id, PasswordChangeDto passwordChangeDto) {
        User user = userRepository.getUserById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        if (!passwordEncoder.matches(passwordChangeDto.getCurrentPassword(), user.getPassword())) {
            throw new BusinessException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(passwordChangeDto.getNewPassword()));
        userRepository.saveUser(user);
    }

    @Override
    public void updateLastLogin(Long id) {
        User user = userRepository.getUserById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        user.setLastLogin(LocalDateTime.now());
        userRepository.saveUser(user);
    }

    @Override
    public long countByRole(UserRole role) {
        return userRepository.countByRole(role);
    }

    @Override
    public long countByStatus(UserStatus status) {
        return userRepository.countByStatus(status);
    }
}