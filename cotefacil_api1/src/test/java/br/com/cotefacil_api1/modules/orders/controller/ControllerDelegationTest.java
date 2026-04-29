package br.com.cotefacil_api1.modules.orders.controller;

import br.com.cotefacil_api1.modules.auth.controller.AuthController;
import br.com.cotefacil_api1.modules.auth.dto.AuthDTO;
import br.com.cotefacil_api1.modules.auth.service.AuthService;
import br.com.cotefacil_api1.modules.orders.client.OrderApiClient;
import br.com.cotefacil_api1.modules.orders.dto.OrderDTO;
import br.com.cotefacil_api1.modules.orders.dto.OrderItemDTO;
import br.com.cotefacil_api1.modules.orders.dto.PageRequestParams;
import br.com.cotefacil_api1.shared.web.responses.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ControllerDelegationTest {

    @Mock
    private AuthService authService;

    @Mock
    private OrderApiClient orderApiClient;

    @InjectMocks
    private AuthController authController;

    @InjectMocks
    private OrderController orderController;

    @Test
    void login_deveDelegarAoServico_quandoRequisicaoForValida() {
        Response expected = Response.success("ok");
        when(authService.login(any(AuthDTO.class))).thenReturn(expected);

        Response actual = authController.login(new AuthDTO("usuario", "senha"));

        assertSame(expected, actual);
        verify(authService).login(any(AuthDTO.class));
    }

    @Test
    void endpointsDePedidos_deveDelegarAoClient_quandoChamados() {
        ResponseEntity<Response> response = ResponseEntity.ok(Response.success("ok"));
        when(orderApiClient.findAllOrders(any(PageRequestParams.class), any())).thenReturn(response);
        when(orderApiClient.findOrderById(eq(1L), any())).thenReturn(response);
        when(orderApiClient.createOrder(any(OrderDTO.class), any())).thenReturn(response);
        when(orderApiClient.updateOrder(eq(1L), any(OrderDTO.class), any())).thenReturn(response);
        when(orderApiClient.deleteOrder(eq(1L), any())).thenReturn(response);
        when(orderApiClient.findAllOrdersItemByIdOrder(eq(1L), any())).thenReturn(response);
        when(orderApiClient.addItemsOrder(eq(1L), any(OrderItemDTO.class), any())).thenReturn(response);

        assertSame(response, orderController.findAllOrders(new PageRequestParams(), "Bearer token"));
        assertSame(response, orderController.findOrderById(1L, "Bearer token"));
        assertSame(response, orderController.createOrder(new OrderDTO(), "Bearer token"));
        assertSame(response, orderController.updateOrder(1L, new OrderDTO(), "Bearer token"));
        assertSame(response, orderController.deleteOrder(1L, "Bearer token"));
        assertSame(response, orderController.findAllOrdersItemByIdOrder(1L, "Bearer token"));
        assertSame(response, orderController.addItemsOrder(1L, new OrderItemDTO(), "Bearer token"));

        verify(orderApiClient).findAllOrders(any(PageRequestParams.class), any());
        verify(orderApiClient).findOrderById(eq(1L), any());
        verify(orderApiClient).createOrder(any(OrderDTO.class), any());
        verify(orderApiClient).updateOrder(eq(1L), any(OrderDTO.class), any());
        verify(orderApiClient).deleteOrder(eq(1L), any());
        verify(orderApiClient).findAllOrdersItemByIdOrder(eq(1L), any());
        verify(orderApiClient).addItemsOrder(eq(1L), any(OrderItemDTO.class), any());
    }
}
