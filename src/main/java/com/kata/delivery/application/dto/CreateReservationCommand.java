package com.kata.delivery.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Command DTO for creating a reservation (CQRS pattern)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateReservationCommand {

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @NotBlank(message = "Customer email is required")
    @Email(message = "Invalid email format")
    private String customerEmail;

    @NotNull(message = "Time slot ID is required")
    private Long timeSlotId;
}
