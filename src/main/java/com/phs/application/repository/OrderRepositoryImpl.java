package com.phs.application.repository;

import com.phs.application.entity.Order;
import com.phs.application.model.mapper.OrderDetailMapper;
import com.phs.application.model.mapper.OrderMapper;
import com.phs.application.model.response.OrderDetailResponse;
import com.phs.application.model.response.OrderResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderRepositoryImpl {

    @Autowired
    @Qualifier("template")
    private NamedParameterJdbcTemplate template;

    public int save(Order request, KeyHolder keyHolder) {
        String sql = "INSERT INTO orders(created_at, note, price, promotion, quantity, receiver_address, receiver_name,\n" +
                "                   receiver_phone, status, total_price, buyer, created_by,product_id,bill_code,`size`)\n" +
                "VALUES (:created_at, :note, :price, :promotion, :quantity, :receiver_address, :receiver_name,\n" +
                "        :receiver_phone, :status, :total_price, :buyer, :created_by,:product_id,:bill_code,:size)\n";
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("created_at", request.getCreatedAt());
        param.addValue("note", request.getNote());
        param.addValue("price", request.getPrice());
        param.addValue("promotion", request.getPromotion());
        param.addValue("quantity", request.getQuantity());
        param.addValue("receiver_address", request.getReceiverAddress());
        param.addValue("receiver_name", request.getReceiverName());
        param.addValue("receiver_phone", request.getReceiverPhone());
        param.addValue("status", request.getStatus());
        param.addValue("total_price", request.getTotalPrice());
        param.addValue("buyer", request.getBuyer().getId());
        param.addValue("created_by", request.getCreatedBy().getId());
        param.addValue("product_id", request.getProductIds());
        param.addValue("bill_code", request.getBillCode());
        param.addValue("size", request.getSize());

        return template.update(sql, param, keyHolder);
    }

    public int[] saveBatch(List<Order> request) {
        String sql = "INSERT INTO orders(created_at, note, price, promotion, quantity, receiver_address, receiver_name,\n" +
                "                   receiver_phone, status, total_price, buyer, created_by,product_id)\n" +
                "VALUES (:createdAt, :note, :price, :promotion, :quantity, :receiverAddress, :receiverName,\n" +
                "        :receiverPhone, :status, :totalPrice, :buyer, :createdBy,:productIds)\n";

        SqlParameterSource[] sqlParamsList = new SqlParameterSource[request.size()];

        for (int i = 0; i < request.size(); i++) {
            sqlParamsList[i] = new BeanPropertySqlParameterSource(request.get(i));
        }

        int[] result = template.batchUpdate(sql, sqlParamsList);

        return result;
    }

    public List<OrderResponse> getSummary(Long userId, Integer status) {
        StringBuilder sql = new StringBuilder("select total_price,\n" +
                "       bill_code,\n" +
                "       full_name,\n" +
                "       sum(o.quantity) as quantity, " +
                "       CASE\n" +
                "           WHEN o.status = 1 THEN 'chờ lấy hàng '\n" +
                "           WHEN o.status = 2 THEN 'đang giao hàng '\n" +
                "           WHEN o.status = 3 THEN 'Đã giao hàng '\n" +
                "           WHEN o.status = 4 THEN 'Đơn hàng bị trả lại '\n" +
                "           WHEN o.status = 5 THEN 'Đơn hàng bị hủy '\n" +
                "           END  as status\n" +
                "from orders o\n" +
                "         left join users u ON o.buyer = u.id\n" +
                "where 1= 1 ");
        MapSqlParameterSource map = new MapSqlParameterSource();
        if (userId != null) {
            sql.append(" AND o.buyer = :buyer\n ");
            map.addValue("buyer", userId);
        }
        if (status != null) {
            sql.append(" AND o.status = :status\n ");
            map.addValue("status", status);
        }
        sql.append(" group by bill_code, total_price");

        return template.query(sql.toString(), map, new OrderMapper());
    }

    public List<OrderResponse> getByStatus( Integer status) {
        StringBuilder sql = new StringBuilder("select total_price,\n" +
                "       receiver_name    as full_name,\n" +
                "       bill_code,\n" +
                "       sum(o.quantity)  as quantity,\n" +
                "       CASE\n" +
                "           WHEN o.status = 1 THEN 'chờ lấy hàng '\n" +
                "           WHEN o.status = 2 THEN 'đang giao hàng '\n" +
                "           WHEN o.status = 3 THEN 'Đã giao hàng '\n" +
                "           WHEN o.status = 4 THEN 'Đơn hàng bị trả lại '\n" +
                "           WHEN o.status = 5 THEN 'Đơn hàng bị hủy '\n" +
                "           END          as status\n" +
                "from orders o\n" +
                "         left join users u ON o.buyer = u.id\n" +
                "where 1 = 1\n" +
                "\n");
        MapSqlParameterSource map = new MapSqlParameterSource();
        if (status != null) {
            sql.append(" AND o.status = :status\n ");
            map.addValue("status", status);
        }
        sql.append(" group by bill_code, total_price, receiver_name, receiver_phone, receiver_address");

        return template.query(sql.toString(), map, new OrderMapper());
    }

    public List<OrderDetailResponse> getDetail(String billCode) {
        StringBuilder sql = new StringBuilder("select receiver_address, receiver_name,product_id, receiver_phone, quantity, product.price * orders.quantity as prices, bill_code , note\n" +
                "from orders\n" +
                "         inner join product ON orders.product_id = product.id\n" +
                "where bill_code = :billCode");
        MapSqlParameterSource map = new MapSqlParameterSource();

        map.addValue("billCode", billCode);
        return template.query(sql.toString(), map, new OrderDetailMapper());
    }
}
