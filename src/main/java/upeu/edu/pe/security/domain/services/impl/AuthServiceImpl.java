package upeu.edu.pe.security.domain.services.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.security.application.dto.*;
import upeu.edu.pe.security.domain.entities.RefreshToken;
import upeu.edu.pe.security.domain.entities.User;
import upeu.edu.pe.security.domain.enums.UserStatus;
import upeu.edu.pe.security.domain.repositories.RefreshTokenRepository;
import upeu.edu.pe.security.domain.repositories.UserRepository;
import upeu.edu.pe.security.domain.services.AuthService;
import upeu.edu.pe.security.infrastructure.utils.JwtTokenGenerator;
import upeu.edu.pe.security.infrastructure.utils.JwtTokenValidator;
import upeu.edu.pe.security.infrastructure.utils.PasswordEncoder;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.time.LocalDateTime;

@ApplicationScoped
@Transactional
public class AuthServiceImpl implements AuthService {

    @Inject
    UserRepository userRepository;

    @Inject
    RefreshTokenRepository refreshTokenRepository;

    @Inject
    PasswordEncoder passwordEncoder;

    @Inject
    JwtTokenGenerator jwtTokenGenerator;

    @Inject
    JwtTokenValidator jwtTokenValidator;

    @Override
    public AuthResponseDto login(LoginRequestDto loginRequest) {
        // Buscar usuario por username o email
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .or(() -> userRepository.findByEmail(loginRequest.getUsername()))
                .orElseThrow(() -> new NotFoundException("Invalid username or password"));

        // Validar contraseña
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BusinessException("Invalid username or password");
        }

        // Validar estado del usuario
        if (user.getStatus() == UserStatus.INACTIVE || user.getStatus() == UserStatus.SUSPENDED) {
            throw new BusinessException("Account is " + user.getStatus().name().toLowerCase());
        }

        // Actualizar último login
        user.setLastLogin(LocalDateTime.now());
        userRepository.saveUser(user);

        // Generar tokens usando MicroProfile JWT
        String accessToken = jwtTokenGenerator.generateAccessToken(user);
        String refreshToken = createRefreshToken(user);

        // Crear respuesta
        AuthResponseDto.UserInfoDto userInfo = new AuthResponseDto.UserInfoDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                user.getStatus(),
                user.getLastLogin()
        );

        return new AuthResponseDto(
                accessToken,
                refreshToken,
                "Bearer",
                jwtTokenGenerator.getDuration(),
                userInfo
        );
    }

    @Override

    public AuthResponseDto register(RegisterRequestDto registerRequest) {
        try {
            System.out.println("=== INICIO REGISTRO ===");

            // Validar unicidad de username y email
            if (userRepository.existsByUsername(registerRequest.getUsername())) {
                throw new BusinessException("Username already exists");
            }

            if (userRepository.existsByEmail(registerRequest.getEmail())) {
                throw new BusinessException("Email already exists");
            }

            System.out.println("=== VALIDACIONES PASADAS ===");

            // Crear nuevo usuario
            User user = new User();
            user.setUsername(registerRequest.getUsername());
            user.setEmail(registerRequest.getEmail());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setFirstName(registerRequest.getFirstName());
            user.setLastName(registerRequest.getLastName());
            user.setPhone(registerRequest.getPhone());
            user.setRole(registerRequest.getRole());
            user.setStatus(UserStatus.ACTIVE);
            user.setLastLogin(LocalDateTime.now());

            System.out.println("=== USUARIO CREADO ===");

            User savedUser = userRepository.saveUser(user);
            System.out.println("=== USUARIO GUARDADO ID: " + savedUser.getId() + " ===");

            // Generar tokens - AQUÍ ESTÁ EL PROBLEMA
            System.out.println("=== GENERANDO ACCESS TOKEN ===");
            String accessToken = jwtTokenGenerator.generateAccessToken(savedUser);
            System.out.println("=== ACCESS TOKEN GENERADO ===");

            System.out.println("=== GENERANDO REFRESH TOKEN ===");
            String refreshToken = createRefreshToken(savedUser);
            System.out.println("=== REFRESH TOKEN GENERADO ===");

            // Crear respuesta
            AuthResponseDto.UserInfoDto userInfo = new AuthResponseDto.UserInfoDto(
                    savedUser.getId(),
                    savedUser.getUsername(),
                    savedUser.getEmail(),
                    savedUser.getFirstName(),
                    savedUser.getLastName(),
                    savedUser.getRole(),
                    savedUser.getStatus(),
                    savedUser.getLastLogin()
            );

            return new AuthResponseDto(
                    accessToken,
                    refreshToken,
                    "Bearer",
                    jwtTokenGenerator.getDuration(),
                    userInfo
            );

        } catch (Exception e) {
            System.out.println("=== ERROR EN REGISTRO: " + e.getClass().getSimpleName() + " - " + e.getMessage() + " ===");
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public TokenResponseDto refreshToken(RefreshTokenRequestDto refreshRequest) {
        // Validar refresh token con MicroProfile JWT
        if (!jwtTokenValidator.validateToken(refreshRequest.getRefreshToken())) {
            throw new BusinessException("Invalid or expired refresh token");
        }

        String username = jwtTokenValidator.getUsernameFromToken(refreshRequest.getRefreshToken());
        Long userId = jwtTokenValidator.getUserIdFromToken(refreshRequest.getRefreshToken());

        if (username == null || userId == null) {
            throw new BusinessException("Invalid refresh token claims");
        }

        // Buscar el refresh token en la base de datos
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshRequest.getRefreshToken())
                .orElseThrow(() -> new BusinessException("Refresh token not found"));

        // Validar que no esté revocado
        if (refreshToken.getIsRevoked() || refreshToken.isExpired()) {
            throw new BusinessException("Refresh token has been revoked or expired");
        }

        User user = refreshToken.getUser();

        // Generar nuevos tokens
        String newAccessToken = jwtTokenGenerator.generateAccessToken(user);
        String newRefreshToken = createRefreshToken(user);

        // Revocar el token anterior
        refreshToken.setIsRevoked(true);
        refreshTokenRepository.saveRefreshToken(refreshToken);

        return new TokenResponseDto(
                newAccessToken,
                newRefreshToken,
                "Bearer",
                jwtTokenGenerator.getDuration()
        );
    }

    @Override
    public void logout(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new BusinessException("Invalid refresh token"));

        refreshToken.setIsRevoked(true);
        refreshTokenRepository.saveRefreshToken(refreshToken);
    }

    @Override
    public void logoutAllDevices(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        refreshTokenRepository.revokeAllByUser(user);
    }

    private String createRefreshToken(User user) {
        // Generar refresh token usando MicroProfile JWT
        String tokenValue = jwtTokenGenerator.generateRefreshToken(user);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(tokenValue);
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(LocalDateTime.now().plusSeconds(jwtTokenGenerator.getRefreshDuration()));
        refreshToken.setIsRevoked(false);

        refreshTokenRepository.saveRefreshToken(refreshToken);

        return tokenValue;
    }
}