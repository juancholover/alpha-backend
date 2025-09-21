package upeu.edu.pe.security.domain.repositories.impl;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.security.domain.entities.User;
import upeu.edu.pe.security.domain.enums.UserRole;
import upeu.edu.pe.security.domain.enums.UserStatus;
import upeu.edu.pe.security.domain.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserRepositoryImpl implements UserRepository, PanacheRepositoryBase<User, Long> {

    @Override
    public List<User> getAllUsers() {
        return listAll();
    }

    @Override
    public List<User> findAllByStatus(UserStatus status) {
        return list("status", status);
    }

    @Override
    public List<User> findAllByRole(UserRole role) {
        return list("role", role);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return findByIdOptional(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return find("username", username).firstResultOptional();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    @Override
    public User saveUser(User user) {
        persist(user);
        return user;
    }

    @Override
    public void removeUserById(Long id) {
        delete("id", id);
    }

    @Override
    public boolean existsByUsername(String username) {
        return count("username = ?1", username) > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        return count("email = ?1", email) > 0;
    }

    @Override
    public long countByRole(UserRole role) {
        return count("role", role);
    }

    @Override
    public long countByStatus(UserStatus status) {
        return count("status", status);
    }
}