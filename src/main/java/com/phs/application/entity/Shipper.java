package com.phs.application.entity;

import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.sql.Timestamp;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "shippers")
@TypeDef(
        name ="json",
        typeClass = JsonStringType.class
)
public class Shipper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "username")
    private String username;
    @Column(name = "password",nullable = false)
    private String password;
    @Column(name = "phone")
    private String phone;
    @Column(name = "address")
    private String address;
    @Column(name = "created_at")
    private Timestamp createdAt;
    @Column(name = "modified_at")
    private Timestamp modifiedAt;
}
