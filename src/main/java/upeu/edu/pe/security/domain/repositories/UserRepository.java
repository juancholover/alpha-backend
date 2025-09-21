package upeu.edu.pe.security.domain.repositories;

import upeu.edu.pe.security.domain.entities.User;
import upeu.edu.pe.security.domain.enums.UserRole;
import upeu.edu.pe.security.domain.enums.UserStatus;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> getAllUsers();
    List<User> findAllByStatus(UserStatus status);
    List<User> findAllByRole(UserRole role);
    Optional<User> getUserById(Long id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    User saveUser(User user);
    void removeUserById(Long id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    long countByRole(UserRole role);
    long countByStatus(UserStatus status);
}