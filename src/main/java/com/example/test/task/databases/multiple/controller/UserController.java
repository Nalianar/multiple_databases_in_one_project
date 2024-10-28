package com.example.test.task.databases.multiple.controller;

import com.example.test.task.databases.multiple.model.User;
import com.example.test.task.databases.multiple.service.UserAggregationService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserAggregationService userAggregationService;

    public UserController(UserAggregationService userAggregationService) {
        this.userAggregationService = userAggregationService;
    }

    @Operation(
            summary = "Get list of users",
            description = "Returns a list of users with the ability to filter by ID, username, first name and last name"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User list successfully retrieved",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(
            @Parameter(description = "Filter by ID") @RequestParam(required = false) String id,
            @Parameter(description = "Filter by username") @RequestParam(required = false) String username,
            @Parameter(description = "Filter by name") @RequestParam(required = false) String name,
            @Parameter(description = "Filter by surname") @RequestParam(required = false) String surname) {
        return userAggregationService.getAllUsers(id, username, name, surname);
    }
}
