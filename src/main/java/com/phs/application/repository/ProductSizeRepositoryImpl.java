package com.phs.application.repository;

import com.phs.application.entity.ProductSize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ProductSizeRepositoryImpl {

    @Autowired
    @Qualifier("template")
    private NamedParameterJdbcTemplate template;

    public int update(ProductSize request) {
            String sql = "Update product_size set quantity = quantity - :quantity where product_id = :productId and size = :size";
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("quantity", request.getQuantity());
        param.addValue("productId", request.getProductId());
        param.addValue("size", request.getSize());
        return template.update(sql, param);
    }
}
