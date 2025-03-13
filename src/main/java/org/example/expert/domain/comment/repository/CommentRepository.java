package org.example.expert.domain.comment.repository;

import org.example.expert.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // JPQL의 join fetch 사용해서 user 정보를 즉시 로드 -> N+1 문제 해결
    @Query("select c from Comment c join fetch c.user where c.todo.id = :todoId")
    List<Comment> findCommentWithUserById(@Param("todoId") Long todoId);
}
