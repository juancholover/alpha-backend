package upeu.edu.pe.security.domain.services;

import upeu.edu.pe.security.application.dto.PasswordChangeDto;
import upeu.edu.pe.security.application.dto.UserRequestDto;
import upeu.edu.pe.security.application.dto.UserResponseDto;
import upeu.edu.pe.security.application.dto.UserUpdateDto;
import upeu.edu.pe.security.domain.enums.UserRole;
import upeu.edu.pe.security.domain.enums.UserStatus;

import java.util.List;

public interface UserService {
    List<UserResponseDto> findAll();
    List<UserResponseDto> findAllByStatus(UserStatus status);
    List<UserResponseDto> findAllByRole(UserRole role);
    UserResponseDto findById(Long id);
    UserResponseDto findByUsername(String username);
    UserResponseDto create(UserRequestDto requestDto);
    UserResponseDto update(Long id, UserUpdateDto updateDto);
    void deleteById(Long id);
    void changePassword(Long id, PasswordChangeDto passwordChangeDto);
    void updateLastLogin(Long id);
    long countByRole(UserRole role);
    long countByStatus(UserStatus status);
}