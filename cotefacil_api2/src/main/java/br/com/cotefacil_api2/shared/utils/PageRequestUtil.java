package br.com.cotefacil_api2.shared.utils;

import br.com.cotefacil_api2.modules.orders.dto.PageRequestParams;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Map;

public final class PageRequestUtil {

    private PageRequestUtil() {
    }

    public static Pageable toPageable(PageRequestParams params, Map<String, String> orderMapping) {
        if (params == null) {
            params = new PageRequestParams();
        }
        int page = Math.max(params.resolvePage() - 1, 0);
        int size = params.resolveSize();
        String orderBy = resolveOrderBy(params.getOrderBy(), orderMapping);
        if (orderBy == null || orderBy.isBlank()) {
            return PageRequest.of(page, size);
        }
        Sort.Direction direction = Sort.Direction.fromString(params.resolveOrderDir());
        return PageRequest.of(page, size, Sort.by(direction, orderBy));
    }

    public static String resolveOrderBy(String orderBy, Map<String, String> orderMapping) {
        if (orderBy == null || orderBy.isBlank()) return null;
        if (orderMapping == null) return orderBy;
        return orderMapping.getOrDefault(orderBy, orderBy);
    }
}
