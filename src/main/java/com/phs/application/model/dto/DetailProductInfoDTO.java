package com.phs.application.model.dto;

import com.phs.application.entity.Brand;
import com.phs.application.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class DetailProductInfoDTO {
    private String id;

    private String name;

    private String slug;

    private long price;

    private int views;
    private int quantity;

    private long totalSold;

    private ArrayList<String> productImages;

    private ArrayList<String> feedbackImages;

    private long promotionPrice;

    private String couponCode;

    private String description;

    private Brand brand;

    private List<CommentDTO> comments;
//    private List<Comment> commentsDto;
}
