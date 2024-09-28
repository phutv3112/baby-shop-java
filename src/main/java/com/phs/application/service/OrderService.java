package com.phs.application.service;

import com.phs.application.entity.Order;
import com.phs.application.model.dto.OrderDetailDTO;
import com.phs.application.model.dto.OrderInfoDTO;
import com.phs.application.model.request.CreateOrderRequest;
import com.phs.application.model.request.CreateOrderRequestV2;
import com.phs.application.model.request.UpdateDetailOrder;
import com.phs.application.model.request.UpdateStatusOrderRequest;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrderService {
    Page<Order> adminGetListOrders(String id, String name, String phone, String status, String product, int page);

    Order createOrder(CreateOrderRequest createOrderRequest, long userId);

    void updateDetailOrder(UpdateDetailOrder updateDetailOrder, long id, long userId);
//    void updateStatusOrderV2(UpdateStatusOrderRequest updateDetailOrder,String billCode, int status);
    void updateStatusOrderV2(String billCode, int status);

    Order findOrderById(long id);

    void updateStatusOrder(UpdateStatusOrderRequest updateStatusOrderRequest, long orderId, long userId);
    void updateStatusOrderByShipper(int status, Long shipperId, long orderId);
    List<OrderInfoDTO> getListOrderOfPersonByStatus(int status, long userId);
    List<Order> getListOrderOfShipperByStatus(int status, Long shipperId);
    OrderDetailDTO userGetDetailById(long id, long userId);

    void userCancelOrder(long id, long userId);

    //Đếm số lượng đơn hàng
    long getCountOrder();

    List<Number> createOrderV2(CreateOrderRequestV2 createOrderRequest, long id);

    List<Order> findOrdersByBillCode(String billCode);
    void changeOrderPaymentStatus(long orderId);
}
