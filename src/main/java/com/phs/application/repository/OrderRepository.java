package com.phs.application.repository;

import com.phs.application.entity.Order;
import com.phs.application.model.dto.OrderDetailDTO;
import com.phs.application.model.dto.OrderInfoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query(value = "SELECT * FROM orders " +
            "WHERE id LIKE CONCAT('%',?1,'%') " +
            "AND receiver_name LIKE CONCAT('%',?2,'%') " +
            "AND receiver_phone LIKE CONCAT('%',?3,'%') " +
            "AND status LIKE CONCAT('%',?4,'%') " +
            "AND product_id LIKE CONCAT('%',?5,'%')", nativeQuery = true)
    Page<Order> adminGetListOrder(String id, String name, String phone, String status, String product, Pageable pageable);

    @Query(nativeQuery = true, name = "getListOrderOfPersonByStatus")
    List<OrderInfoDTO> getListOrderOfPersonByStatus(int status, long userId);

    @Query(value = "SELECT * FROM orders " +
            "WHERE status = ?1 " +
            "AND (shipper_id = ?2 OR (?2 IS NULL AND shipper_id IS NULL))" , nativeQuery = true)
    List<Order>getListOrderOfShipperByStatus(int status, Long shipperId);

    @Query(nativeQuery = true, name = "userGetDetailById")
    OrderDetailDTO userGetDetailById(long id, long userId);

    @Query(nativeQuery = true, value = "SELECT id FROM orders WHERE bill_code = ?1")
    List<Long> findByBillCode(String billCode);
    int countByProductId(String id);

    @Query("SELECT COUNT(DISTINCT o.billCode) FROM Order o")
    long countDistinctBillCodes();

    @Query(value = "SELECT * FROM orders WHERE bill_code LIKE CONCAT('%',?1,'%')", nativeQuery = true)
    List<Order> findOrdersByBillCode(String billCode);

    @Modifying
    @Query(value = "UPDATE orders SET shipper_id = ?1 WHERE id = ?2", nativeQuery = true)
    void updateShipperIdForOrder(Long shipperId, long orderId);
}
