package com.phs.application.service.impl;

import com.phs.application.entity.*;
import com.phs.application.exception.BadRequestException;
import com.phs.application.exception.InternalServerException;
import com.phs.application.exception.NotFoundException;
import com.phs.application.model.dto.OrderDetailDTO;
import com.phs.application.model.dto.OrderInfoDTO;
import com.phs.application.model.request.CreateOrderRequest;
import com.phs.application.model.request.CreateOrderRequestV2;
import com.phs.application.model.request.UpdateDetailOrder;
import com.phs.application.model.request.UpdateStatusOrderRequest;
import com.phs.application.model.response.OrderDetailResponse;
import com.phs.application.model.response.OrderResponse;
import com.phs.application.repository.*;
import com.phs.application.service.OrderService;
import com.phs.application.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.phs.application.config.Contant.*;

@Controller
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductSizeRepository productSizeRepository;

    @Autowired
    private ProductSizeRepositoryImpl productSizeRepositoryImpl;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderRepositoryImpl orderRepositoryImpl;

    @Autowired
    private PromotionService promotionService;

    @Autowired
    private StatisticRepository statisticRepository;

    @Override
    public Page<Order> adminGetListOrders(String id, String name, String phone, String status, String product, int page) {
        page--;
        if (page < 0) {
            page = 0;
        }
        int limit = 10;
        Pageable pageable = PageRequest.of(page, limit, Sort.by("created_at").descending());
        return orderRepository.adminGetListOrder(id, name, phone, status, product, pageable);
    }

    @Override
    public Order createOrder(CreateOrderRequest createOrderRequest, long userId) {

        //Kiểm tra sản phẩm có tồn tại
        Optional<Product> product = productRepository.findById(createOrderRequest.getProductId());
        if (product.isEmpty()) {
            throw new NotFoundException("Sản phẩm không tồn tại!");
        }

        //Kiểm tra size có sẵn
        ProductSize productSize = productSizeRepository.checkProductAndSizeAvailable(createOrderRequest.getProductId(), createOrderRequest.getSize());
        if (productSize == null) {
            throw new BadRequestException("Size giày sản phẩm tạm hết, Vui lòng chọn sản phẩm khác!");
        }

        Order order = new Order();
        User user = new User();
        user.setId(userId);
        order.setCreatedBy(user);
        order.setQuantity(createOrderRequest.getQuantity());
        order.setBuyer(user);
        order.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        order.setReceiverAddress(createOrderRequest.getReceiverAddress());
        order.setReceiverName(createOrderRequest.getReceiverName());
        order.setReceiverPhone(createOrderRequest.getReceiverPhone());
        order.setNote(createOrderRequest.getNote());
        order.setSize(createOrderRequest.getSize());
        order.setPrice(createOrderRequest.getProductPrice());
        order.setTotalPrice(createOrderRequest.getProductPrice() - createOrderRequest.getTotalPrice());
        order.setStatus(ORDER_STATUS);
        order.setQuantity(createOrderRequest.getQuantity());
        order.setProduct(product.get());

        orderRepository.save(order);

        productSize.setQuantity(productSize.getQuantity() - createOrderRequest.getQuantity());
        productSizeRepository.save(productSize);

        return order;

    }

    @Override
    public void updateDetailOrder(UpdateDetailOrder updateDetailOrder, long id, long userId) {
        //Kiểm trả có đơn hàng
        Optional<Order> rs = orderRepository.findById(id);
        if (rs.isEmpty()) {
            throw new NotFoundException("Đơn hàng không tồn tại");
        }

        Order order = rs.get();
        //Kiểm tra trạng thái đơn hàng
        if (order.getStatus() != ORDER_STATUS) {
            throw new BadRequestException("Chỉ cập nhật đơn hàng ở trạng thái chờ lấy hàng");
        }

        //Kiểm tra size sản phẩm
        Optional<Product> product = productRepository.findById(updateDetailOrder.getProductId());
        if (product.isEmpty()) {
            throw new BadRequestException("Sản phẩm không tồn tại");
        }
        //Kiểm tra giá
        if (product.get().getSalePrice() != updateDetailOrder.getProductPrice()) {
            throw new BadRequestException("Giá sản phẩm thay đổi vui lòng đặt hàng lại");
        }

        ProductSize productSize = productSizeRepository.checkProductAndSizeAvailable(updateDetailOrder.getProductId(), updateDetailOrder.getSize());
        if (productSize == null) {
            throw new BadRequestException("Size giày sản phẩm tạm hết, Vui lòng chọn sản phẩm khác");
        }

        //Kiểm tra khuyến mại
        if (updateDetailOrder.getCouponCode() != "") {
            Promotion promotion = promotionService.checkPromotion(updateDetailOrder.getCouponCode());
            if (promotion == null) {
                throw new NotFoundException("Mã khuyến mãi không tồn tại hoặc chưa được kích hoạt");
            }
            long promotionPrice = promotionService.calculatePromotionPrice(updateDetailOrder.getProductPrice(), promotion);
            if (promotionPrice != updateDetailOrder.getTotalPrice()) {
                throw new BadRequestException("Tổng giá trị đơn hàng thay đổi. Vui lòng kiểm tra và đặt lại đơn hàng");
            }
            Order.UsedPromotion usedPromotion = new Order.UsedPromotion(updateDetailOrder.getCouponCode(), promotion.getDiscountType(), promotion.getDiscountValue(), promotion.getMaximumDiscountValue());
            order.setPromotion(usedPromotion);
        }

        order.setModifiedAt(new Timestamp(System.currentTimeMillis()));
        order.setProduct(product.get());
        order.setSize(updateDetailOrder.getSize());
        order.setPrice(updateDetailOrder.getProductPrice());
        order.setTotalPrice(updateDetailOrder.getTotalPrice());
        order.setQuantity(updateDetailOrder.getQuantity());


        order.setStatus(ORDER_STATUS);
        User user = new User();
        user.setId(userId);
        order.setModifiedBy(user);
        try {
            orderRepository.save(order);
        } catch (Exception e) {
            throw new InternalServerException("Lỗi khi cập nhật");
        }
    }


    @Override
    public Order findOrderById(long id) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isEmpty()) {
            throw new NotFoundException("Đơn hàng không tồn tại");
        }
        return order.get();
    }

    @Override
    public void updateStatusOrder(UpdateStatusOrderRequest updateStatusOrderRequest, long orderId, long userId) {
        Optional<Order> rs = orderRepository.findById(orderId);
        if (rs.isEmpty()) {
            throw new NotFoundException("Đơn hàng không tồn tại");
        }
        Order order = rs.get();
        //Kiểm tra trạng thái của đơn hàng
        boolean check = false;
        for (Integer status : LIST_ORDER_STATUS) {
            if (status == updateStatusOrderRequest.getStatus()) {
                check = true;
                break;
            }
        }
        if (!check) {
            throw new BadRequestException("Trạng thái đơn hàng không hợp lệ");
        }
        //Cập nhật trạng thái đơn hàng
        if (order.getStatus() == ORDER_STATUS) {
            //Đơn hàng ở trạng thái chờ lấy hàng
            if (updateStatusOrderRequest.getStatus() == ORDER_STATUS) {
                order.setReceiverPhone(updateStatusOrderRequest.getReceiverPhone());
                order.setReceiverName(updateStatusOrderRequest.getReceiverName());
                order.setReceiverAddress(updateStatusOrderRequest.getReceiverAddress());
                //Đơn hàng ở trạng thái đang vận chuyển
            } else if (updateStatusOrderRequest.getStatus() == DELIVERY_STATUS) {
                //Trừ đi một sản phẩm
                productSizeRepository.minusOneProductBySize(order.getProduct().getId(), order.getSize());
                //Đơn hàng ở trạng thái đã giao hàng
            } else if (updateStatusOrderRequest.getStatus() == COMPLETED_STATUS) {
                //Trừ đi một sản phẩm và cộng một sản phẩm vào sản phẩm đã bán và cộng tiền
                productSizeRepository.minusOneProductBySize(order.getProduct().getId(), order.getSize());
                productRepository.plusOneProductTotalSold(order.getProduct().getId());
                statistic(order.getTotalPrice(), order.getQuantity(), order);
            } else if (updateStatusOrderRequest.getStatus() != CANCELED_STATUS) {
                throw new BadRequestException("Không thế chuyển sang trạng thái này");
            }
            //Đơn hàng ở trạng thái đang giao hàng
        } else if (order.getStatus() == DELIVERY_STATUS) {
            //Đơn hàng ở trạng thái đã giao hàng
            if (updateStatusOrderRequest.getStatus() == COMPLETED_STATUS) {
                //Cộng một sản phẩm vào sản phẩm đã bán và cộng tiền
                productRepository.plusOneProductTotalSold(order.getProduct().getId());
                statistic(order.getTotalPrice(), order.getQuantity(), order);
                //Đơn hàng ở trạng thái đã hủy
            } else if (updateStatusOrderRequest.getStatus() == RETURNED_STATUS) {
                //Cộng lại một sản phẩm đã bị trừ
                productSizeRepository.plusOneProductBySize(order.getProduct().getId(), order.getSize());
                //Đơn hàng ở trạng thái đã trả hàng
            } else if (updateStatusOrderRequest.getStatus() == CANCELED_STATUS) {
                //Cộng lại một sản phẩm đã bị trừ
                productSizeRepository.plusOneProductBySize(order.getProduct().getId(), order.getSize());
            } else if (updateStatusOrderRequest.getStatus() != DELIVERY_STATUS) {
                throw new BadRequestException("Không thế chuyển sang trạng thái này");
            }
            //Đơn hàng ở trạng thái đã giao hàng
        } else if (order.getStatus() == COMPLETED_STATUS) {
            //Đơn hàng đang ở trạng thái đã hủy
            if (updateStatusOrderRequest.getStatus() == RETURNED_STATUS) {
                //Cộng một sản phẩm đã bị trừ và trừ đi một sản phẩm đã bán và trừ số tiền
                productSizeRepository.plusOneProductBySize(order.getProduct().getId(), order.getSize());
                productRepository.minusOneProductTotalSold(order.getProduct().getId());
                updateStatistic(order.getTotalPrice(), order.getQuantity(), order);
            } else if (updateStatusOrderRequest.getStatus() != COMPLETED_STATUS) {
                throw new BadRequestException("Không thế chuyển sang trạng thái này");
            }
        } else {
            if (order.getStatus() != updateStatusOrderRequest.getStatus()) {
                throw new BadRequestException("Không thế chuyển đơn hàng sang trạng thái này");
            }
        }

        User user = new User();
        user.setId(userId);
        order.setModifiedBy(user);
        order.setModifiedAt(new Timestamp(System.currentTimeMillis()));
        order.setNote(updateStatusOrderRequest.getNote());
        order.setStatus(updateStatusOrderRequest.getStatus());
        try {
            orderRepository.save(order);
        } catch (Exception e) {
            throw new InternalServerException("Lỗi khi cập nhật trạng thái");
        }
    }

//    @Override
//    public void updateStatusOrderV2(UpdateStatusOrderRequest updateStatusOrderRequest, String billCode, long userId) {
//        List<Long> ls = orderRepository.findByBillCode(billCode);
//        if (ls.isEmpty()) {
//            throw new NotFoundException("Đơn hàng không tồn tại");
//        }
//        for (Long item : ls){
//            Optional<Order> rs = orderRepository.findById(item);
//            if (rs.isEmpty()) {
//                throw new NotFoundException("Đơn hàng không tồn tại");
//            }
//            Order order = rs.get();
//            //Kiểm tra trạng thái của đơn hàng
//            boolean check = false;
//            for (Integer status : LIST_ORDER_STATUS) {
//                if (status == updateStatusOrderRequest.getStatus()) {
//                    check = true;
//                    break;
//                }
//            }
//            if (!check) {
//                throw new BadRequestException("Trạng thái đơn hàng không hợp lệ");
//            }
//            //Cập nhật trạng thái đơn hàng
//            if (order.getStatus() == ORDER_STATUS) {
//                //Đơn hàng ở trạng thái chờ lấy hàng
//                if (updateStatusOrderRequest.getStatus() == ORDER_STATUS) {
//                    order.setReceiverPhone(updateStatusOrderRequest.getReceiverPhone());
//                    order.setReceiverName(updateStatusOrderRequest.getReceiverName());
//                    order.setReceiverAddress(updateStatusOrderRequest.getReceiverAddress());
//                    //Đơn hàng ở trạng thái đang vận chuyển
//                } else if (updateStatusOrderRequest.getStatus() == DELIVERY_STATUS) {
//                    //Trừ đi một sản phẩm
//                    productSizeRepository.minusOneProductBySize(order.getProduct().getId(), order.getSize());
//                    //Đơn hàng ở trạng thái đã giao hàng
//                } else if (updateStatusOrderRequest.getStatus() == COMPLETED_STATUS) {
//                    //Trừ đi một sản phẩm và cộng một sản phẩm vào sản phẩm đã bán và cộng tiền
//                    productSizeRepository.minusOneProductBySize(order.getProduct().getId(), order.getSize());
//                    productRepository.plusOneProductTotalSold(order.getProduct().getId());
//                    statistic(order.getTotalPrice(), order.getQuantity(), order);
//                } else if (updateStatusOrderRequest.getStatus() != CANCELED_STATUS) {
//                    throw new BadRequestException("Không thế chuyển sang trạng thái này");
//                }
//                //Đơn hàng ở trạng thái đang giao hàng
//            } else if (order.getStatus() == DELIVERY_STATUS) {
//                //Đơn hàng ở trạng thái đã giao hàng
//                if (updateStatusOrderRequest.getStatus() == COMPLETED_STATUS) {
//                    //Cộng một sản phẩm vào sản phẩm đã bán và cộng tiền
//                    productRepository.plusOneProductTotalSold(order.getProduct().getId());
//                    statistic(order.getTotalPrice(), order.getQuantity(), order);
//                    //Đơn hàng ở trạng thái đã hủy
//                } else if (updateStatusOrderRequest.getStatus() == RETURNED_STATUS) {
//                    //Cộng lại một sản phẩm đã bị trừ
//                    productSizeRepository.plusOneProductBySize(order.getProduct().getId(), order.getSize());
//                    //Đơn hàng ở trạng thái đã trả hàng
//                } else if (updateStatusOrderRequest.getStatus() == CANCELED_STATUS) {
//                    //Cộng lại một sản phẩm đã bị trừ
//                    productSizeRepository.plusOneProductBySize(order.getProduct().getId(), order.getSize());
//                } else if (updateStatusOrderRequest.getStatus() != DELIVERY_STATUS) {
//                    throw new BadRequestException("Không thế chuyển sang trạng thái này");
//                }
//                //Đơn hàng ở trạng thái đã giao hàng
//            } else if (order.getStatus() == COMPLETED_STATUS) {
//                //Đơn hàng đang ở trạng thái đã hủy
//                if (updateStatusOrderRequest.getStatus() == RETURNED_STATUS) {
//                    //Cộng một sản phẩm đã bị trừ và trừ đi một sản phẩm đã bán và trừ số tiền
//                    productSizeRepository.plusOneProductBySize(order.getProduct().getId(), order.getSize());
//                    productRepository.minusOneProductTotalSold(order.getProduct().getId());
//                    updateStatistic(order.getTotalPrice(), order.getQuantity(), order);
//                } else if (updateStatusOrderRequest.getStatus() != COMPLETED_STATUS) {
//                    throw new BadRequestException("Không thế chuyển sang trạng thái này");
//                }
//            } else {
//                if (order.getStatus() != updateStatusOrderRequest.getStatus()) {
//                    throw new BadRequestException("Không thế chuyển đơn hàng sang trạng thái này");
//                }
//            }
//
//            User user = new User();
//            user.setId(userId);
//            order.setModifiedBy(user);
//            order.setModifiedAt(new Timestamp(System.currentTimeMillis()));
//            order.setNote(updateStatusOrderRequest.getNote());
//            order.setStatus(updateStatusOrderRequest.getStatus());
//            try {
//                orderRepository.save(order);
//            } catch (Exception e) {
//                throw new InternalServerException("Lỗi khi cập nhật trạng thái");
//            }
//        }
//    }
@Override
public void updateStatusOrderV2(String billCode, int status) {
    // Tìm danh sách đơn hàng dựa trên mã hóa đơn
    List<Long> orderIds = orderRepository.findByBillCode(billCode);
    if (orderIds.isEmpty()) {
        throw new NotFoundException("Đơn hàng không tồn tại");
    }

    for (Long orderId : orderIds) {
        // Tìm đơn hàng theo ID
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new NotFoundException("Đơn hàng không tồn tại");
        }

        Order order = orderOpt.get();

        // Kiểm tra trạng thái hợp lệ
        boolean isValidStatus = LIST_ORDER_STATUS.contains(status);
        if (!isValidStatus) {
            throw new BadRequestException("Trạng thái đơn hàng không hợp lệ");
        }

        // Logic xử lý cập nhật trạng thái đơn hàng
        switch (order.getStatus()) {
            case ORDER_STATUS:
                if (status == DELIVERY_STATUS) {
                    productSizeRepository.minusOneProductBySize(order.getProduct().getId(), order.getSize());
                } else if (status == COMPLETED_STATUS) {
                    productSizeRepository.minusOneProductBySize(order.getProduct().getId(), order.getSize());
                    productRepository.plusOneProductTotalSold(order.getProduct().getId());
                    statistic(order.getTotalPrice(), order.getQuantity(), order);
                } else if (status != CANCELED_STATUS) {
                    throw new BadRequestException("Không thể chuyển sang trạng thái này");
                }
                break;

            case DELIVERY_STATUS:
                if (status == COMPLETED_STATUS) {
                    productRepository.plusOneProductTotalSold(order.getProduct().getId());
                    statistic(order.getTotalPrice(), order.getQuantity(), order);
                } else if (status == RETURNED_STATUS || status == CANCELED_STATUS) {
                    productSizeRepository.plusOneProductBySize(order.getProduct().getId(), order.getSize());
                } else if (status != DELIVERY_STATUS) {
                    throw new BadRequestException("Không thể chuyển sang trạng thái này");
                }
                break;

            case COMPLETED_STATUS:
                if (status == RETURNED_STATUS) {
                    productSizeRepository.plusOneProductBySize(order.getProduct().getId(), order.getSize());
                    productRepository.minusOneProductTotalSold(order.getProduct().getId());
                    updateStatistic(order.getTotalPrice(), order.getQuantity(), order);
                } else if (status != COMPLETED_STATUS) {
                    throw new BadRequestException("Không thể chuyển sang trạng thái này");
                }
                break;

            default:
                if (order.getStatus() != status) {
                    throw new BadRequestException("Không thể chuyển đơn hàng sang trạng thái này");
                }
                break;
        }

        // Cập nhật trạng thái đơn hàng
        order.setStatus(status);
        order.setModifiedAt(new Timestamp(System.currentTimeMillis()));

        // Lưu đơn hàng vào cơ sở dữ liệu
        try {
            orderRepository.save(order);
        } catch (Exception e) {
            throw new InternalServerException("Lỗi khi cập nhật trạng thái đơn hàng");
        }
    }
}



    @Override
    public List<OrderInfoDTO> getListOrderOfPersonByStatus(int status, long userId) {
        List<OrderInfoDTO> list = orderRepository.getListOrderOfPersonByStatus(status, userId);

        for (OrderInfoDTO dto : list) {
            for (int i = 0; i < SIZE_VN.size(); i++) {
                if (SIZE_VN.get(i) == dto.getSizeVn()) {
                    dto.setSizeUs(SIZE_US[i]);
                    dto.setSizeCm(SIZE_CM[i]);
                }
            }
        }
        return list;
    }

    @Override
    public OrderDetailDTO userGetDetailById(long id, long userId) {
        OrderDetailDTO order = orderRepository.userGetDetailById(id, userId);
        System.out.println(order.getQuantity());
        if (order == null) {
            return null;
        }

        if (order.getStatus() == ORDER_STATUS) {
            order.setStatusText("Chờ lấy hàng");
        } else if (order.getStatus() == DELIVERY_STATUS) {
            order.setStatusText("Đang giao hàng");
        } else if (order.getStatus() == COMPLETED_STATUS) {
            order.setStatusText("Đã giao hàng");
        } else if (order.getStatus() == CANCELED_STATUS) {
            order.setStatusText("Đơn hàng đã trả lại");
        } else if (order.getStatus() == RETURNED_STATUS) {
            order.setStatusText("Đơn hàng đã hủy");
        }

        for (int i = 0; i < SIZE_VN.size(); i++) {
            if (SIZE_VN.get(i) == order.getSizeVn()) {
                order.setSizeUs(SIZE_US[i]);
                order.setSizeCm(SIZE_CM[i]);
            }
        }

        return order;
    }

    @Override
    public void userCancelOrder(long id, long userId) {
        Optional<Order> rs = orderRepository.findById(id);
        if (rs.isEmpty()) {
            throw new NotFoundException("Đơn hàng không tồn tại");
        }
        Order order = rs.get();
        if (order.getBuyer().getId() != userId) {
            throw new BadRequestException("Bạn không phải chủ nhân đơn hàng");
        }
        if (order.getStatus() != ORDER_STATUS) {
            throw new BadRequestException("Trạng thái đơn hàng không phù hợp để hủy. Vui lòng liên hệ với shop để được hỗ trợ");
        }

        order.setStatus(CANCELED_STATUS);
        orderRepository.save(order);
    }

    @Override
    public long getCountOrder() {
        return orderRepository.countDistinctBillCodes();
    }

    @Transactional
    @Override
    public List<Number> createOrderV2(CreateOrderRequestV2 createOrderRequest, long userId) {
        //Kiểm tra sản phẩm có tồn tại
        for (ProductSize proId : createOrderRequest.getProducts()) {
            Optional<Product> product = productRepository.findById(proId.getProductId());
            if (product.isEmpty()) {
                throw new NotFoundException("Sản phẩm không tồn tại!");
            }
        }

//        //Kiểm tra size có sẵn
//        for (ProductSize proId : createOrderRequest.getProducts()) {
//            ProductSize productSize = productSizeRepository.checkProductAndSizeAvailable(proId.getProductId(), proId.getSize());
//            if (productSize == null) {
//                throw new BadRequestException("sản phẩm tạm hết, Vui lòng chọn sản phẩm khác!");
//            }
////            trừ số lượng sp
//            productSizeRepositoryImpl.update(productSize);
////             insert dữ liệu vào bảng map
//
//        }


        List<Number> res = new ArrayList<>();

        String uuid = UUID.randomUUID().toString();
        for (ProductSize proId : createOrderRequest.getProducts()) {
            ProductSize productSize = productSizeRepository.checkProductAndSizeAvailable(proId.getProductId(), proId.getSize());
            if (productSize == null) {
                throw new BadRequestException("sản phẩm tạm hết, Vui lòng chọn sản phẩm khác!");
            }
//            trừ số lượng sp
            productSizeRepositoryImpl.update(proId);
//             insert dữ liệu vào bảng map
            Order order = new Order();
            User user = new User();
            user.setId(userId);
            order.setCreatedBy(user);
            order.setQuantity(proId.getQuantity());
            order.setBuyer(user);
            order.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            order.setReceiverAddress(createOrderRequest.getReceiverAddress());
            order.setReceiverName(createOrderRequest.getReceiverName());
            order.setReceiverPhone(createOrderRequest.getReceiverPhone());
            order.setNote(createOrderRequest.getNote());
            order.setPrice(createOrderRequest.getProductPrice());
            order.setTotalPrice(createOrderRequest.getTotalPrice());
            order.setStatus(ORDER_STATUS);
            order.setProductIds(productSize.getProductId());
            order.setBillCode(uuid);
            order.setSize(proId.getSize());
            KeyHolder keyHolder = new GeneratedKeyHolder();
            orderRepositoryImpl.save(order, keyHolder);
            Number id = keyHolder.getKey();
            res.add(id);
        }

        return res;
    }

    public void statistic(long amount, int quantity, Order order) {
        Statistic statistic = statisticRepository.findByCreatedAT();
        if (statistic != null) {
            statistic.setOrder(order);
            statistic.setSales(statistic.getSales() + amount);
            statistic.setQuantity(statistic.getQuantity() + quantity);
            statistic.setProfit(statistic.getSales() - (statistic.getQuantity() * order.getProduct().getPrice()));
            statisticRepository.save(statistic);
        } else {
            Statistic statistic1 = new Statistic();
            statistic1.setOrder(order);
            statistic1.setSales(amount);
            statistic1.setQuantity(quantity);
            statistic1.setProfit(amount - (quantity * order.getProduct().getPrice()));
            statistic1.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            statisticRepository.save(statistic1);
        }
    }

    public void updateStatistic(long amount, int quantity, Order order) {
        Statistic statistic = statisticRepository.findByCreatedAT();
        if (statistic != null) {
            statistic.setOrder(order);
            statistic.setSales(statistic.getSales() - amount);
            statistic.setQuantity(statistic.getQuantity() - quantity);
            statistic.setProfit(statistic.getSales() - (statistic.getQuantity() * order.getProduct().getPrice()));
            statisticRepository.save(statistic);
        }
    }

    public List<OrderResponse> getSummary(Long userId, Integer status) {
        if (userId == null) {
            throw new BadRequestException("userId không được bỏ trống");
        }

        if (status == null) {
            throw new BadRequestException("status không được bỏ trống");
        }
        List<OrderResponse> orders = orderRepositoryImpl.getSummary(userId, status);
        return orders;
    }

    public List<OrderResponse> getByStatus( Integer status) {

        if (status == null) {
            throw new BadRequestException("status không được bỏ trống");
        }
        List<OrderResponse> orders = orderRepositoryImpl.getByStatus( status);
        return orders;
    }

    public List<OrderDetailResponse> getDetailByBillCode(String billCode) {
        if (billCode == null || billCode.isEmpty()) {
            throw new BadRequestException("userId không được bỏ trống");
        }
        List<OrderDetailResponse> orders = orderRepositoryImpl.getDetail(billCode);
        return orders;
    }
}
