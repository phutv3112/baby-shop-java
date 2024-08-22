package com.phs.application.controller.shop;

import com.phs.application.entity.*;
import com.phs.application.exception.BadRequestException;
import com.phs.application.exception.InternalServerException;
import com.phs.application.exception.NotFoundException;
import com.phs.application.model.ProductDTO1;
import com.phs.application.model.ProductResponse1;
import com.phs.application.model.dto.*;
import com.phs.application.model.request.CreateOrderRequest;
import com.phs.application.model.request.CreateOrderRequestV2;
import com.phs.application.model.request.FilterProductRequest;
import com.phs.application.model.request.UpdateStatusOrderRequest;
import com.phs.application.model.response.ProductResponse;
import com.phs.application.security.CustomUserDetails;
import com.phs.application.service.*;
import com.phs.application.service.impl.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

import static com.phs.application.config.Contant.*;

@Controller
public class HomeController {

    @Autowired
    private ProductService productService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private PostService postService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderServiceImpl orderServiceImpl;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private PromotionService promotionService;

    @GetMapping
    public String homePage(Model model) {

        //Lấy 5 sản phẩm mới nhất
        List<ProductInfoDTO> newProducts = productService.getListNewProducts();
        model.addAttribute("newProducts", newProducts);

        //Lấy 5 sản phẩm bán chạy nhất
        List<ProductInfoDTO> bestSellerProducts = productService.getListBestSellProducts();
        model.addAttribute("bestSellerProducts", bestSellerProducts);

        //Lấy 5 sản phẩm có lượt xem nhiều
        List<ProductInfoDTO> viewProducts = productService.getListViewProducts();
        model.addAttribute("viewProducts", viewProducts);

        //Lấy danh sách nhãn hiệu
        List<Brand> brands = brandService.getListBrand();
        model.addAttribute("brands", brands);

        //Lấy 5 bài viết mới nhất
        List<Post> posts = postService.getLatesPost();
        model.addAttribute("posts", posts);

        return "shop/index";
    }

    @GetMapping("/{slug}/{id}")
    public String getProductDetail(Model model, @PathVariable String id) {

        //Lấy thông tin sản phẩm
        DetailProductInfoDTO product;
        try {
            product = productService.getDetailProductById(id);
        } catch (NotFoundException ex) {
            return "error/404";
        } catch (Exception ex) {
            return "error/500";
        }
        model.addAttribute("product", product);

        //Lấy sản phẩm liên quan
        List<ProductInfoDTO> relatedProducts = productService.getRelatedProducts(id);
        model.addAttribute("relatedProducts", relatedProducts);

        //Lấy danh sách nhãn hiệu
        List<Brand> brands = brandService.getListBrand();
        model.addAttribute("brands", brands);

        // Lấy size có sẵn
        List<Integer> availableSizes = productService.getListAvailableSize(id);
        model.addAttribute("availableSizes", availableSizes);
        if (!availableSizes.isEmpty()) {
            model.addAttribute("canBuy", true);
        } else {
            model.addAttribute("canBuy", false);
        }

        //Lấy danh sách size giầy
        model.addAttribute("sizeVn", SIZE_VN);
        model.addAttribute("sizeUs", SIZE_US);
        model.addAttribute("sizeCm", SIZE_CM);

        return "shop/detail";
    }

    @GetMapping("/dat-hang")
    public String getCartPage(Model model, @RequestParam String id, @RequestParam int size, @RequestParam int quantity) {

        //Lấy chi tiết sản phẩm
        DetailProductInfoDTO product;
        try {
            product = productService.getDetailProductById(id);
        } catch (NotFoundException ex) {
            System.out.println(ex);
            return "error/404";
        } catch (Exception ex) {
            return "error/500";
        }
        product.setQuantity(quantity);
        product.setPrice(product.getPrice() * quantity);
        long price = product.getPrice() / quantity;
        product.setPromotionPrice((price - product.getPromotionPrice()) * quantity);
        model.addAttribute("product", product);

        //Validate size
        if (size < 35 || size > 42) {
            return "error/404";
        }

        //Lấy danh sách size có sẵn
        List<Integer> availableSizes = productService.getListAvailableSize(id);
        model.addAttribute("availableSizes", availableSizes);
        boolean notFoundSize = true;
        for (Integer availableSize : availableSizes) {
            if (availableSize == size) {
                notFoundSize = false;
                break;
            }
        }
        model.addAttribute("notFoundSize", notFoundSize);

        //Lấy danh sách size
        model.addAttribute("sizeVn", SIZE_VN);
        model.addAttribute("sizeUs", SIZE_US);
        model.addAttribute("sizeCm", SIZE_CM);
        model.addAttribute("size", size);
        model.addAttribute("saveId", id);
        model.addAttribute("saveSize", size);
        return "shop/payment";
    }

    @PostMapping("/api/orders")
    public ResponseEntity<Object> createOrder(@Valid @RequestBody CreateOrderRequest createOrderRequest) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Order order = orderService.createOrder(createOrderRequest, user.getId());

        return ResponseEntity.ok(order.getId());
    }

    @PostMapping("/api/orders/v2")
    public ResponseEntity<Object> createOrderV2(@RequestBody CreateOrderRequestV2 createOrderRequest) {
//        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        List<Number> order = orderService.createOrderV2(createOrderRequest, createOrderRequest.getUserId());

        return ResponseEntity.ok(order);
    }

    @GetMapping("/api/orders")
    public ResponseEntity<Object> getOrder(@RequestParam(name = "status", required = false) Integer status, @RequestParam(name = "buyer", required = false) Long buyer) {
        return ResponseEntity.ok(orderServiceImpl.getSummary(buyer, status));
    }

    @GetMapping("/api/orders/status")
    public ResponseEntity<Object> getOrderByStatus(@RequestParam(name = "status", required = false) Integer status) {
        return ResponseEntity.ok(orderServiceImpl.getByStatus(status));
    }

    @GetMapping("/api/orders/detail")
    public ResponseEntity<Object> getOrderDetail(@RequestParam(name = "billCode") String billCode) {
        return ResponseEntity.ok(orderServiceImpl.getDetailByBillCode(billCode));
    }

//    @PostMapping("/api/update/status")
//    public ResponseEntity<Object> updateStatus(@RequestBody UpdateStatusOrderRequest updateStatusOrderRequest, @RequestParam(name = "billCode") String billCode, @RequestParam(name = "userId") long userId) {
//        orderServiceImpl.updateStatusOrderV2(updateStatusOrderRequest, billCode, userId);
//        return ResponseEntity.ok("Cập nhật trạng thái thành công");
//    }
@PostMapping("/api/update/status")
public ResponseEntity<Object> updateStatus(
        @RequestParam(name = "billCode") String billCode,
        @RequestParam(name = "status") int status) {
    try {
        orderServiceImpl.updateStatusOrderV2(billCode, status);
        return ResponseEntity.ok("Cập nhật trạng thái thành công");
    } catch (NotFoundException | BadRequestException | InternalServerException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}




    //    @GetMapping("/products")
//    public ResponseEntity<Object> getListBestSellProducts(){
//        List<ProductInfoDTO> productInfoDTOS = productService.getListBestSellProducts();
//        return ResponseEntity.ok(productInfoDTOS);
//    }
    @GetMapping("/products")
    public ResponseEntity<ProductResponse> getListBestSellProducts() {
        try {
            List<ProductInfoDTO> productInfoDTOS = productService.getListBestSellProducts();
            // Trả về danh sách sản phẩm và trạng thái thành công
            ProductResponse response = new ProductResponse(productInfoDTOS, "OK", "SUCCSESS");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Trả về lỗi nếu có vấn đề
            ProductResponse errorResponse = new ProductResponse(null, "INTERNAL_SERVER_ERROR", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    @GetMapping("api/product/{product_id}")
    public ResponseEntity<ProductDTO1> getProductById(@PathVariable String product_id) {

            Product productInfoDTO = productService.getProductById(product_id);
            if (productInfoDTO != null) {
                ProductDTO1 productDTO = new ProductDTO1(productInfoDTO.getId(), productInfoDTO.getName(), String.valueOf(productInfoDTO.getPrice()));
                return ResponseEntity.ok(productDTO);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
    }



    @GetMapping("/san-pham")
    public String getProductShopPages(Model model) {

        //Lấy danh sách nhãn hiệu
        List<Brand> brands = brandService.getListBrand();
        model.addAttribute("brands", brands);
        List<Long> brandIds = new ArrayList<>();
        for (Brand brand : brands) {
            brandIds.add(brand.getId());
        }
        model.addAttribute("brandIds", brandIds);

        //Lấy danh sách danh mục
        List<Category> categories = categoryService.getListCategories();
        model.addAttribute("categories", categories);
        List<Long> categoryIds = new ArrayList<>();
        for (Category category : categories) {
            categoryIds.add(category.getId());
        }
        model.addAttribute("categoryIds", categoryIds);

        //Danh sách size của sản phẩm
        model.addAttribute("sizeVn", SIZE_VN);

        //Lấy danh sách sản phẩm
        FilterProductRequest req = new FilterProductRequest(brandIds, categoryIds, new ArrayList<>(), (long) 0, Long.MAX_VALUE, 1);
        PageableDTO result = productService.filterProduct(req);
        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("currentPage", result.getCurrentPage());
        model.addAttribute("listProduct", result.getItems());

        return "shop/product";
    }

    @PostMapping("/api/san-pham/loc")
    public ResponseEntity<?> filterProduct(@RequestBody FilterProductRequest req) {
        // Validate
        if (req.getMinPrice() == null) {
            req.setMinPrice((long) 0);
        } else {
            if (req.getMinPrice() < 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mức giá phải lớn hơn 0");
            }
        }
        if (req.getMaxPrice() == null) {
            req.setMaxPrice(Long.MAX_VALUE);
        } else {
            if (req.getMaxPrice() < 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mức giá phải lớn hơn 0");
            }
        }

        PageableDTO result = productService.filterProduct(req);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/tim-kiem")
    public String searchProduct(Model model, @RequestParam(required = false) String keyword, @RequestParam(required = false) Integer page) {

        PageableDTO result = productService.searchProductByKeyword(keyword, page);

        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("currentPage", result.getCurrentPage());
        model.addAttribute("listProduct", result.getItems());
        model.addAttribute("keyword", keyword);
        if (((List<?>) result.getItems()).isEmpty()) {
            model.addAttribute("hasResult", false);
        } else {
            model.addAttribute("hasResult", true);
        }

        return "shop/search";
    }

    @GetMapping("/api/tim-kiem/{keyword}/{page}")
    public ResponseEntity<PageableDTO> searchProduct(
            @PathVariable String keyword,
            @PathVariable Integer page) {

        PageableDTO result = productService.searchProductByKeyword(keyword, page);

        return ResponseEntity.ok(result);
    }


    @GetMapping("/api/check-hidden-promotion")
    public ResponseEntity<Object> checkPromotion(@RequestParam String code) {
        if (code == null || code == "") {
            throw new BadRequestException("Mã code trống");
        }

        Promotion promotion = promotionService.checkPromotion(code);
        if (promotion == null) {
            throw new BadRequestException("Mã code không hợp lệ");
        }
        CheckPromotion checkPromotion = new CheckPromotion();
        checkPromotion.setDiscountType(promotion.getDiscountType());
        checkPromotion.setDiscountValue(promotion.getDiscountValue());
        checkPromotion.setMaximumDiscountValue(promotion.getMaximumDiscountValue());
        return ResponseEntity.ok(checkPromotion);
    }

    @GetMapping("/chitiet/{slug}/{id}")
    public ResponseEntity<?> getProductDetail1(@PathVariable String id) {
        try {
            DetailProductInfoDTO product = productService.getDetailProductById(id);

            Map<String, Object> response = new HashMap<>();
            response.put("product", product);

            // Lấy size có sẵn
            List<Integer> availableSizes = productService.getListAvailableSize(id);
            response.put("availableSizes", availableSizes);

            // Đặt thuộc tính "canBuy" dựa trên size có sẵn
            response.put("canBuy", !availableSizes.isEmpty());

            // Lấy danh sách size giầy
            response.put("sizeVn", SIZE_VN);
            response.put("sizeUs", SIZE_US);
            response.put("sizeCm", SIZE_CM);

            return ResponseEntity.ok(response);

        } catch (NotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "Product not found"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "Internal server error"));
        }
    }

    @GetMapping("lien-he")
    public String contact() {
        return "shop/lien-he";
    }

    @GetMapping("huong-dan")
    public String buyGuide() {
        return "shop/buy-guide";
    }

    @GetMapping("doi-hang")
    public String doiHang() {
        return "shop/doi-hang";
    }

}
