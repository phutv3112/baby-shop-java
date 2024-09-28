package com.phs.application.repository;

import com.phs.application.entity.Shipper;
import com.phs.application.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ShipperRepository extends JpaRepository<Shipper,Long> {
    Shipper findByUsername(String email);

    @Query(value = "SELECT * " +
            "FROM shippers s WHERE s.username LIKE CONCAT('%',?1,'%') " +
            "AND s.phone LIKE CONCAT('%',?2,'%') " ,nativeQuery = true)
    Page<Shipper> adminListShipperPages(String username, String phone, Pageable pageable);

}

