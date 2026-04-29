package br.com.cotefacil_api2.modules.orders.dto;

import br.com.cotefacil_api2.modules.orders.model.OrderItem;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Item de pedido utilizado pela API 2")
public class OrderItemDTO {

    @Schema(description = "Identifier of item.", example = "1")
    private Long id;

    @Schema(description = "Product name", example = "Produto A")
    @NotBlank(message = "Product name is required.")
    @Size(min = 2, max = 150, message = "Product name must be between 2 and 150 characters.")
    private String productName;

    @Schema(description = "Quantity", example = "2")
    @NotNull(message = "Quantity is required.")
    @Min(value = 1, message = "Quantity must be at least 1.")
    private Integer quantity;

    @Schema(description = "Unit price", example = "10.50")
    @NotNull(message = "Unit price is required.")
    @DecimalMin(value = "0.01", message = "Unit price must be greater than zero.")
    @Digits(
            integer = 17,
            fraction = 2,
            message = "Unit price must have up to 17 integer digits and 2 decimal places."
    )
    private BigDecimal unitPrice;

    @Schema(description = "Item subtotal", example = "21.00")
    @DecimalMin(value = "0.00", message = "Subtotal cannot be negative.")
    @NotNull(message = "Subtotal is required.")
    @Digits(
            integer = 17,
            fraction = 2,
            message = "Subtotal must have up to 17 integer digits and 2 decimal places."
    )
    private BigDecimal subtotal = BigDecimal.ZERO;

    public static OrderItemDTO toDTO(OrderItem entity) {
        if (entity == null) return null;

        OrderItemDTO dto = new OrderItemDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    public OrderItem toEntity() {
        OrderItem entity = new OrderItem();
        BeanUtils.copyProperties(this, entity, "order", "id");
        if (entity.getSubtotal() == null || entity.getSubtotal().compareTo(BigDecimal.ZERO) == 0) {
            if (entity.getQuantity() != null && entity.getUnitPrice() != null) {
                entity.setSubtotal(entity.getUnitPrice().multiply(BigDecimal.valueOf(entity.getQuantity())));
            }
        }
        return entity;
    }
}
