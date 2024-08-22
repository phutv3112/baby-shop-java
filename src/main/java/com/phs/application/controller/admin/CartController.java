package com.phs.application.controller.admin;

import com.phs.application.model.dto.CartResponse;
import com.phs.application.model.response.ResponseOK;
import com.phs.application.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/addOrUpdateCart")
    public ResponseEntity<ResponseOK> addOrUpdateCartItem(
            @RequestParam Long userId,
            @RequestParam String productId) {

        // Gọi dịch vụ để thêm hoặc cập nhật mục giỏ hàng và nhận phản hồi
        ResponseOK responseOK = cartService.addOrUpdateCartItem(userId, productId);

        // Trả về phản hồi với mã trạng thái HTTP 200 (OK) và đối tượng CartResponse
        return ResponseEntity.ok(responseOK);
    }

    @GetMapping("/getCartByUserId")
    public ResponseEntity<CartResponse> getCartByUserId(@RequestParam Long userId) {
        // Gọi dịch vụ để lấy giỏ hàng theo ID người dùng
        CartResponse cartResponse = cartService.getCartByUserId(userId);

        // Trả về phản hồi với mã trạng thái HTTP 200 (OK) và đối tượng CartResponse
        return ResponseEntity.ok(cartResponse);
    }
    @DeleteMapping("/remove")
    public ResponseEntity<ResponseOK> removeCartItem(@RequestParam Long userId, @RequestParam String productId) {
        ResponseOK responseOK = cartService.removeCartItem(userId, productId);
        return ResponseEntity.ok(responseOK);
    }
    @PutMapping("/decrease")
    public ResponseEntity<ResponseOK> decreaseCartItemQuantity(@RequestParam Long userId, @RequestParam String productId) {
        ResponseOK responseOK = cartService.decreaseCartItemQuantity(userId, productId);
        return ResponseEntity.ok(responseOK);
    }
    @DeleteMapping("/removeCartItemsByUserId")
    public ResponseEntity<ResponseOK> removeCartItemsByUserId(@RequestParam Long userId) {
        ResponseOK responseOK = cartService.removeCartItemsByUserId(userId);
        return ResponseEntity.ok(responseOK);
    }

}
