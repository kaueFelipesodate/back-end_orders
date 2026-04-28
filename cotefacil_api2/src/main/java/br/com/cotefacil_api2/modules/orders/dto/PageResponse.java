package br.com.cotefacil_api2.modules.orders.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta paginada de pedidos")
public class PageResponse<T> {

    @Schema(description = "Lista de itens da pagina")
    private List<T> list;
    @Schema(description = "Tamanho da pagina")
    private int sizePage;
    @Schema(description = "Pagina atual")
    private int currentPage;
    @Schema(description = "Total de paginas")
    private int totalPages;
    @Schema(description = "Total de itens")
    private long totalItems;
    @Schema(description = "Campo de ordenacao")
    private String orderBy;
    @Schema(description = "Direcao de ordenacao")
    private String orderDir;
    @Schema(description = "Filtros aplicados")
    private Map<String, String> filters;

    public static <T> PageResponse<T> fromPage(Page<T> page, PageRequestParams params) {
        return PageResponse.<T>builder()
                .list(page.getContent())
                .sizePage(page.getSize())
                .currentPage(page.getNumber() + 1)
                .totalPages(page.getTotalPages())
                .totalItems(page.getTotalElements())
                .orderBy(params != null ? params.getOrderBy() : null)
                .orderDir(params != null ? params.resolveOrderDir() : null)
                .filters(params != null ? params.getFilters() : null)
                .build();
    }
}
