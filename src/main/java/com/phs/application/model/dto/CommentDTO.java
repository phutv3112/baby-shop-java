package com.phs.application.model.dto;

import com.phs.application.entity.Post;
import com.phs.application.entity.Product;
import com.phs.application.entity.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
public class CommentDTO {

    @Id
    private long id;

    @Column
    private String productId;

    private String content;

    private long userId;
    private String full_name;

//    private String product;

    private Timestamp createdAt;

}
