package com.phs.application.model.mapper;

import com.phs.application.model.response.OrderDetailResponse;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class OrderDetailMapper implements RowMapper<OrderDetailResponse> {
    @Override
    public OrderDetailResponse mapRow(ResultSet resultSet, int i) throws SQLException {
        OrderDetailResponse order = new OrderDetailResponse();
        order.setReceiverAddress(resultSet.getString("receiver_address"));
        order.setNote(resultSet.getString("note"));
        order.setReceiverName(resultSet.getString("receiver_name"));
        order.setReceiverPhone(resultSet.getString("receiver_phone"));
        order.setPrices(resultSet.getLong("prices"));
        order.setQuantity(resultSet.getInt("quantity"));
//        order.setCreatedAt(resultSet.getTimestamp("created_at"));
        order.setBillCode(resultSet.getString("bill_code"));
        order.setProductId(resultSet.getString("product_id"));
        return order;
    }
}
