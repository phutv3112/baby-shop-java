package com.phs.application.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO1 {
    private long id;
    private String title;
    private String content;
    private String description;
    private String slug;
    private String thumbnail;
    private String createdAt;
    private String modifiedAt;
    private String createdBy;
    private String modifiedBy;
    private Timestamp publishedAt;
    private int status;
    private List<String> comments;

}
