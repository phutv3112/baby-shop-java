package com.phs.application.repository;

import com.phs.application.model.dto.CommentDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentDTORepository extends JpaRepository<CommentDTO, Long> {
//    @Query(nativeQuery = true, value = "select * from shoes.comment Where product_id = ?1")
//    List<CommentDTO> getCommentByProductId( String productId);

    @Query(nativeQuery = true, value = "SELECT c.id, c.product_id, c.content, c.user_id, n.full_name, c.created_at " +
            "FROM shoes.comment c " +
            "JOIN shoes.users n ON c.user_id = n.id " +
            "WHERE c.product_id = ?1")
    List<CommentDTO> getCommentByProductId(String productId);

}
