package br.com.cotefacil_api1.modules.orders.dto;

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
@Schema(description = "Paged response to orders")
public class PageResponse<T> {

    @Schema(description = "List of items on the page")
    private List<T> list;
    @Schema(description = "Page size")
    private int sizePage;
    @Schema(description = "Current page")
    private int currentPage;
    @Schema(description = "Total number of pages")
    private int totalPages;
    @Schema(description = "Total number of items")
    private long totalItems;
    @Schema(description = "Sorting field")
    private String orderBy;
    @Schema(description = "Ordering direction")
    private String orderDir;
    @Schema(description = "Filters applied")
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

