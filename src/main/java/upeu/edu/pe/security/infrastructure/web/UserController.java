// src/main/java/upeu/edu/pe/security/infrastructure/web/UserController.java
package upeu.edu.pe.security.infrastructure.web;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.security.application.dto.PasswordChangeDto;
import upeu.edu.pe.security.application.dto.UserRequestDto;
import upeu.edu.pe.security.application.dto.UserResponseDto;
import upeu.edu.pe.security.application.dto.UserUpdateDto;
import upeu.edu.pe.security.domain.services.UserService; // Using interface instead of implementation
import upeu.edu.pe.security.domain.enums.UserRole;
import upeu.edu.pe.security.domain.enums.UserStatus;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/api/v1/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Users", description = "User management operations")
public class UserController {

    @Inject
    UserService userService; // Changed from UserServiceImpl to UserService interface

    @GET
    @Operation(summary = "Get all users", description = "Retrieve all users with optional filtering")
    @APIResponse(responseCode = "200", description = "Users retrieved successfully")
    public Response getAllUsers(
            @QueryParam("status") UserStatus status,
            @QueryParam("role") UserRole role) {

        List<UserResponseDto> users;
        if (status != null) {
            users = userService.findAllByStatus(status);
        } else if (role != null) {
            users = userService.findAllByRole(role);
        } else {
            users = userService.findAll();
        }

        return Response.ok(ApiResponse.success("Users retrieved successfully", users)).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve a specific user by their ID")
    @APIResponse(responseCode = "200", description = "User found")
    @APIResponse(responseCode = "404", description = "User not found")
    public Response getUserById(@Parameter(description = "User ID") @PathParam("id") Long id) {
        UserResponseDto user = userService.findById(id);
        return Response.ok(ApiResponse.success("User retrieved successfully", user)).build();
    }

    @GET
    @Path("/username/{username}")
    @Operation(summary = "Get user by username", description = "Retrieve a specific user by their username")
    @APIResponse(responseCode = "200", description = "User found")
    @APIResponse(responseCode = "404", description = "User not found")
    public Response getUserByUsername(@Parameter(description = "Username") @PathParam("username") String username) {
        UserResponseDto user = userService.findByUsername(username);
        return Response.ok(ApiResponse.success("User retrieved successfully", user)).build();
    }

    @POST
    @Operation(summary = "Create user", description = "Create a new user")
    @APIResponse(responseCode = "201", description = "User created successfully")
    @APIResponse(responseCode = "400", description = "Invalid input data")
    @APIResponse(responseCode = "409", description = "Username or email already exists")
    public Response createUser(@Valid UserRequestDto requestDto) {
        UserResponseDto user = userService.create(requestDto);
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("User created successfully", user))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update user", description = "Update an existing user")
    @APIResponse(responseCode = "200", description = "User updated successfully")
    @APIResponse(responseCode = "404", description = "User not found")
    @APIResponse(responseCode = "409", description = "Email already exists")
    public Response updateUser(
            @Parameter(description = "User ID") @PathParam("id") Long id,
            @Valid UserUpdateDto updateDto) {
        UserResponseDto user = userService.update(id, updateDto);
        return Response.ok(ApiResponse.success("User updated successfully", user)).build();
    }

    @PUT
    @Path("/{id}/password")
    @Operation(summary = "Change user password", description = "Change password for a specific user")
    @APIResponse(responseCode = "200", description = "Password changed successfully")
    @APIResponse(responseCode = "400", description = "Invalid current password")
    @APIResponse(responseCode = "404", description = "User not found")
    public Response changePassword(
            @Parameter(description = "User ID") @PathParam("id") Long id,
            @Valid PasswordChangeDto passwordChangeDto) {
        userService.changePassword(id, passwordChangeDto);
        return Response.ok(ApiResponse.success("Password changed successfully")).build();
    }

    @PUT
    @Path("/{id}/last-login")
    @Operation(summary = "Update last login", description = "Update the last login timestamp for a user")
    @APIResponse(responseCode = "200", description = "Last login updated successfully")
    @APIResponse(responseCode = "404", description = "User not found")
    public Response updateLastLogin(@Parameter(description = "User ID") @PathParam("id") Long id) {
        userService.updateLastLogin(id);
        return Response.ok(ApiResponse.success("Last login updated successfully")).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete user", description = "Delete a user by their ID")
    @APIResponse(responseCode = "200", description = "User deleted successfully")
    @APIResponse(responseCode = "404", description = "User not found")
    public Response deleteUser(@Parameter(description = "User ID") @PathParam("id") Long id) {
        userService.deleteById(id);
        return Response.ok(ApiResponse.success("User deleted successfully")).build();
    }

    @GET
    @Path("/stats")
    @Operation(summary = "Get user statistics", description = "Get user count statistics by role and status")
    @APIResponse(responseCode = "200", description = "Statistics retrieved successfully")
    public Response getUserStats() {
        Map<String, Object> stats = new HashMap<>();

        // Count by role
        Map<String, Long> roleStats = new HashMap<>();
        roleStats.put("ADMIN", userService.countByRole(UserRole.ADMIN));
        roleStats.put("MANAGER", userService.countByRole(UserRole.MANAGER));
        roleStats.put("USER", userService.countByRole(UserRole.USER));

        // Count by status
        Map<String, Long> statusStats = new HashMap<>();
        statusStats.put("ACTIVE", userService.countByStatus(UserStatus.ACTIVE));
        statusStats.put("INACTIVE", userService.countByStatus(UserStatus.INACTIVE));
        statusStats.put("SUSPENDED", userService.countByStatus(UserStatus.SUSPENDED));
        statusStats.put("PENDING_VERIFICATION", userService.countByStatus(UserStatus.PENDING_VERIFICATION));

        stats.put("byRole", roleStats);
        stats.put("byStatus", statusStats);

        return Response.ok(ApiResponse.success("User statistics retrieved successfully", stats)).build();
    }
}