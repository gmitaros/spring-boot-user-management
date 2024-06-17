package com.myproject.authserver.controller;

import com.myproject.authserver.dto.UserDto;
import com.myproject.authserver.dto.UserUpdateDto;
import com.myproject.authserver.service.UserService;
import com.myproject.authserver.utils.DtoMapperUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auth/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Operations related to users management")
public class UsersController {

    private final UserService userService;

    /**
     * Fetch all users.
     *
     * @param active If true, fetch only active users; otherwise, fetch all users.
     * @param page   Page number for pagination.
     * @param size   Page size for pagination.
     * @return A page of UserDto containing the user details.
     */
    @Operation(summary = "Get all users", description = "Fetch all active or all users based on query parameter with pagination")
    @GetMapping()
    public ResponseEntity<Page<UserDto>> getAllUsers(
            @RequestParam(value = "active", defaultValue = "true") boolean active,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(userService.getAllUsers(active, pageable));
    }

    /**
     * Update the details of an existing user.
     *
     * @param id          User ID.
     * @param userDetails User details to be updated.
     * @return The updated UserDto.
     */
    @Operation(summary = "Update user", description = "Update the details of an existing user")
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserUpdateDto userDetails) {
        return ResponseEntity.ok(DtoMapperUtil.toUserDto(userService.updateUser(id, userDetails)));
    }

    /**
     * Soft delete a user by ID.
     *
     * @param id User ID.
     * @return HTTP status 202 Accepted.
     */
    @Operation(summary = "Delete user", description = "Soft delete a user by ID")
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.accepted().build();
    }

    /**
     * Soft delete multiple users by their IDs.
     *
     * @param ids List of user IDs.
     * @return HTTP status 202 Accepted.
     */
    @Operation(summary = "Delete multiple users", description = "Soft delete multiple users by their IDs")
    @DeleteMapping()
    public ResponseEntity<Void> deleteUsers(@RequestBody List<Long> ids) {
        userService.deleteUsers(ids);
        return ResponseEntity.accepted().build();
    }
}
