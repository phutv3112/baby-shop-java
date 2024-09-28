package com.phs.application.controller.shop;

import com.phs.application.model.dto.PaymentDTO;
import com.phs.application.model.response.ResponseObject;
import com.phs.application.service.OrderService;
import com.phs.application.service.impl.VNPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final VNPayService paymentService;
    @Autowired
    private OrderService orderService;
    @GetMapping("/vn-pay")
    public ResponseObject<PaymentDTO.VNPayResponse> pay(HttpServletRequest request) {
        return new ResponseObject<PaymentDTO.VNPayResponse>(HttpStatus.OK.toString(), "Success", paymentService.createVnPayPayment(request));
    }
    @GetMapping("/vn-pay-callback")
    public String payCallbackHandler(HttpServletRequest  request) {
        long orderId=Long.parseLong(request.getParameter("vnp_OrderInfo"));
        orderService.changeOrderPaymentStatus(orderId);
        return "Thanh toán thành công";
    }
}