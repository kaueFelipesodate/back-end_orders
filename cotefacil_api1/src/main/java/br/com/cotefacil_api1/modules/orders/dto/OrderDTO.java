package br.com.cotefacil_api1.modules.orders.dto;

import br.com.cotefacil_api1.modules.orders.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request transmitted via API 1")
public class OrderDTO {

    @Schema(description = "Identifier of order.", example = "1")
    private Long id;

    @Schema(description = "Customer name", example = "Cliente Teste")
    @NotBlank(message = "Customer name is required.")
    @Size(min = 3, max = 150, message = "Customer name must be between 3 and 150 characters.")
    private String customerName;

    @Schema(description = "Customer email", example = "cliente@teste.com")
    @NotBlank(message = "Customer email is required.")
    @Email(message = "Customer email must be valid.")
    @Size(max = 150, message = "Customer email must have at most 150 characters.")
    private String customerEmail;

    @Schema(description = "Order date", example = "02/12/2025 10:30:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss", locale = "pt-BR", timezone = "Brazil/East")
    @PastOrPresent(message = "Order date cannot be in the future.")
    private LocalDateTime orderDate;

    @Schema(description = "Order status", example = "PENDING")
    @NotNull(message = "Order status is required.")
    private OrderStatus status;

    @Schema(description = "Order items")
    @Valid
    private List<OrderItemDTO> items = new ArrayList<>();

    @Schema(description = "Total order value", example = "20.50")
    @NotNull(message = "Total amount is required.")
    @DecimalMin(value = "0.00", message = "Total amount cannot be negative.")
    @Digits(
            integer = 17,
            fraction = 2,
            message = "Total amount must have up to 17 integer digits and 2 decimal places."
    )
    private BigDecimal totalAmount = BigDecimal.ZERO;
}
