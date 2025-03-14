package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.dto.response.ProjectionTodoResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TodoRepositoryCustom {
    Optional<Todo> findByIdWithUser(Long todoId);

    // 제목 검색
    Page<ProjectionTodoResponse> findByTitle(String title, Pageable pageable); // 메서드 이름 나중에 바꿔야 할듯

    // 생성일 기간 검색
    Page<ProjectionTodoResponse> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    // 매니저 이름 검색
    Page<ProjectionTodoResponse> findByManagerNickname(String managerNickname, Pageable pageable);

    // 제목 검색 & 생성일 기간 검색
    Page<ProjectionTodoResponse> findByTitleAndCreatedAtBetween(String title, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    // 제목 검색 & 매니저 이름 검색
    Page<ProjectionTodoResponse> findByTitleAndManagerNickname(String title, String managerNickname, Pageable pageable);

    // 제목 검색 & 생성일 기간 검색 & 매니저 이름 검색
    Page<ProjectionTodoResponse> findByTitleAndManagerNicknameAndCreatedAtBetween(String title, String managerNickname, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    // 아무 조건도 걸지 않은 경우
    Page<ProjectionTodoResponse> findAllProjection(Pageable pageable);
}
