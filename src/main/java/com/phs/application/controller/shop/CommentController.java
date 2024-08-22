package com.phs.application.controller.shop;

import com.phs.application.entity.Comment;
import com.phs.application.entity.User;
import com.phs.application.model.request.CreateCommentPostRequest;
import com.phs.application.model.request.CreateCommentProductRequest;
import com.phs.application.model.response.ResponseOK;
import com.phs.application.security.CustomUserDetails;
import com.phs.application.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/api/comments/post")
    public ResponseEntity<Object> createComment(@Valid @RequestBody CreateCommentPostRequest createCommentPostRequest) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Comment comment = commentService.createCommentPost(createCommentPostRequest, user.getId());
        return ResponseEntity.ok(comment);
    }

    @PostMapping("/api/comments/product")
    public ResponseEntity<Object> createComment(@Valid @RequestBody CreateCommentProductRequest createCommentProductRequest) {
        Comment comment = commentService.createCommentProduct(createCommentProductRequest, createCommentProductRequest.getUserId());
        ResponseOK response = new ResponseOK("200","SUCCCESS","Thêm bình luận thành công");
        return ResponseEntity.ok(response);
    }
}
