package com.phs.application.model.mapper;

import com.phs.application.entity.Order;
import com.phs.application.model.response.OrderResponse;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderMapper implements RowMapper<OrderResponse> {
    @Override
    public OrderResponse mapRow(ResultSet resultSet, int i) throws SQLException {
        OrderResponse order = new OrderResponse();
        order.setStatus(resultSet.getString("status"));
        order.setFullName(resultSet.getString("full_name"));
        order.setTotalPrice(resultSet.getLong("total_price"));
        order.setQuantity(resultSet.getInt("quantity"));
//        order.setCreatedAt(resultSet.getTimestamp("created_at"));
        order.setBillCode(resultSet.getString("bill_code"));
        return order;
    }
}
