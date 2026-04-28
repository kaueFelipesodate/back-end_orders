package br.com.cotefacil_api2.modules.orders.dto;

import br.com.cotefacil_api2.modules.orders.enums.OrderStatus;
import br.com.cotefacil_api2.modules.orders.model.Order;
import br.com.cotefacil_api2.modules.orders.model.OrderItem;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Order used by API 2.")
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
    private LocalDateTime orderDate;

    @Schema(description = "Order status", example = "PENDING")
    @NotNull(message = "Order status is required.")
    private OrderStatus status;

    @Schema(description = "Order items")
    @Valid
    private List<OrderItemDTO> items = new ArrayList<>();

    @Schema(description = "Total order value", example = "20.50")
    @DecimalMin(value = "0.00", message = "Total amount cannot be negative.")
    private BigDecimal totalAmount = BigDecimal.ZERO;

    public static OrderDTO toDTO(Order entity) {
        if (entity == null) return null;

        OrderDTO dto = new OrderDTO();
        BeanUtils.copyProperties(entity, dto);

        List<OrderItem> safeItems = entity.getItems() == null ? new ArrayList<>() : entity.getItems();
        dto.setItems(safeItems
                .stream()
                .map(OrderItemDTO::toDTO)
                .collect(Collectors.toCollection(ArrayList::new))
        );

        return dto;
    }

    public Order toEntity() {
        Order entity = new Order();
        applyToEntity(entity);
        entity.setId(null);
        entity.getItems().forEach(i -> i.setId(null));
        return entity;
    }

    public void loadFromEntity(Order entity) {
        applyToEntity(entity);
    }

    private void applyToEntity(Order entity) {
        if (entity == null) return;

        BeanUtils.copyProperties(this, entity, "id", "items");
        if (entity.getOrderDate() == null) {
            entity.setOrderDate(LocalDateTime.now());
        }

        List<OrderItemDTO> safeItems = this.getItems() != null ? this.getItems() : new ArrayList<>();
        List<OrderItem> mappedItems = safeItems
                .stream()
                .map(itemDTO -> {
                    OrderItem item = itemDTO.toEntity();
                    item.setOrder(entity);
                    return item;
                })
                .collect(Collectors.toCollection(ArrayList::new));

        entity.getItems().clear();
        entity.getItems().addAll(mappedItems);

        if (entity.getTotalAmount() == null || entity.getTotalAmount().compareTo(BigDecimal.ZERO) == 0) {
            entity.setTotalAmount(mappedItems.stream()
                    .map(item -> item.getSubtotal() != null ? item.getSubtotal() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
        }
    }
}
