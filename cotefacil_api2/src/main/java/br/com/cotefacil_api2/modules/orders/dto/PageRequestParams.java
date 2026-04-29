package br.com.cotefacil_api2.modules.orders.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Pagination parameters and filters")
public class PageRequestParams {

    @Builder.Default
    @Schema(description = "Current page", example = "1")
    private Integer page = 1;

    @Builder.Default
    @Schema(description = "Page size", example = "20")
    private Integer size = 20;

    @Schema(description = "Sorting field", example = "createdDate")
    private String orderBy;

    @Schema(description = "Ordering direction", example = "DESC")
    private String orderDir;

    @Builder.Default
    @Schema(description = "Additional filters")
    private Map<String, String> filters = new HashMap<>();

    public int resolvePage() {
        if (page == null || page < 1) return 1;
        return page;
    }

    public int resolveSize() {
        if (size == null || size < 1) return 20;
        return size;
    }

    public String resolveOrderDir() {
        if (orderDir == null || orderDir.isBlank()) return "DESC";
        return orderDir.trim().toUpperCase();
    }
}
