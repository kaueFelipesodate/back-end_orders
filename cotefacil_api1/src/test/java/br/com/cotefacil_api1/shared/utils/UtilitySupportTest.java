package br.com.cotefacil_api1.shared.utils;

import br.com.cotefacil_api1.modules.orders.dto.PageRequestParams;
import br.com.cotefacil_api1.modules.orders.dto.PageResponse;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UtilitySupportTest {

    @Test
    void resolvePage_deveRetornarPadrao_quandoPaginaForInvalida() {
        PageRequestParams params = PageRequestParams.builder()
                .page(0)
                .size(-1)
                .orderDir(" asc ")
                .build();

        assertEquals(1, params.resolvePage());
        assertEquals(20, params.resolveSize());
        assertEquals("ASC", params.resolveOrderDir());
    }

    @Test
    void toPageable_deveAplicarOrdenacao_quandoOrderByForMapeado() {
        PageRequestParams params = PageRequestParams.builder()
                .page(2)
                .size(5)
                .orderBy("createdDate")
                .orderDir("desc")
                .build();

        Pageable pageable = PageRequestUtil.toPageable(params, Map.of("createdDate", "createdDate"));

        assertEquals(PageRequest.of(1, 5, Sort.by(Sort.Direction.DESC, "createdDate")), pageable);
    }

    @Test
    void fromPage_devePropagarMetadados_quandoPaginaForInformada() {
        PageRequestParams params = PageRequestParams.builder()
                .page(3)
                .size(2)
                .orderBy("customerName")
                .orderDir("asc")
                .filters(Map.of("status", "PENDING"))
                .build();

        PageResponse<String> response = PageResponse.fromPage(
                new PageImpl<>(List.of("a", "b"), PageRequest.of(2, 2), 7),
                params
        );

        assertEquals(List.of("a", "b"), response.getList());
        assertEquals(2, response.getSizePage());
        assertEquals(3, response.getCurrentPage());
        assertEquals(4, response.getTotalPages());
        assertEquals(7, response.getTotalItems());
        assertEquals("customerName", response.getOrderBy());
        assertEquals("ASC", response.getOrderDir());
        assertEquals(Map.of("status", "PENDING"), response.getFilters());
    }

    @Test
    void build_deveRetornarConjuncao_quandoFiltrosForemNulos() {
        @SuppressWarnings("unchecked")
        Root<Object> root = mock(Root.class);
        @SuppressWarnings("unchecked")
        CriteriaQuery<Object> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Predicate predicate = mock(Predicate.class);
        when(cb.conjunction()).thenReturn(predicate);

        Predicate actual = FilterSpecBuilder.build(null, null).toPredicate(root, query, cb);

        assertSame(predicate, actual);
        verify(cb).conjunction();
    }
}
