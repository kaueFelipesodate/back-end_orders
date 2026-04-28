package br.com.cotefacil_api2.modules.orders.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Collections;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Parametros de paginacao e filtros")
public class PageRequestParams {

    @Builder.Default
    @Schema(description = "Pagina atual", example = "1")
    private Integer page = 1;

    @Builder.Default
    @Schema(description = "Tamanho da pagina", example = "20")
    private Integer size = 20;

    @Schema(description = "Campo de ordenacao", example = "createdDate")
    private String orderBy;

    @Schema(description = "Direcao de ordenacao", example = "DESC")
    private String orderDir;

    @Builder.Default
    @Schema(description = "Filtros adicionais")
    private Map<String, String> filters = Collections.emptyMap();

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
