package com.phs.application.service.impl;

import com.phs.application.config.VNPayConfig;
import com.phs.application.entity.Order;
import com.phs.application.model.dto.PaymentDTO;
import com.phs.application.repository.OrderRepository;
import com.phs.application.repository.OrderRepositoryImpl;
import com.phs.application.utils.VNPayUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VNPayService {
    private final VNPayConfig vnpayConfig;
    private final OrderRepository orderRepository;

    public PaymentDTO.VNPayResponse createVnPayPayment(HttpServletRequest request) {

        long orderId=Integer.parseInt(request.getParameter("orderId"));
        Order order = orderRepository.findById(orderId).get();
        long amount =order.getTotalPrice() * 100L;
        String bankCode = request.getParameter("bankCode");
        Map<String, String> vnpParamsMap = vnpayConfig.getVNPayConfig(orderId);
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }
        vnpParamsMap.put("vnp_IpAddr","127.0.0.1");
        //build query url
        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnpayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnpayConfig.getVnp_PayUrl() + "?" + queryUrl;
        return PaymentDTO.VNPayResponse.builder()
                .paymentUrl(paymentUrl).build();
    }
}
