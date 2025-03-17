package org.example.expert.domain.todo.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.comment.entity.QComment;
import org.example.expert.domain.manager.entity.QManager;
import org.example.expert.domain.todo.dto.response.ProjectionTodoResponse;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class TodoRepositoryImpl implements TodoRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    QTodo todo = QTodo.todo;
    QUser user = QUser.user;
    QManager manager = QManager.manager;
    QComment comment = QComment.comment;

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {

        Todo result = jpaQueryFactory
                .selectFrom(todo)
                .leftJoin(todo.user, user).fetchJoin()
                .where(todo.id.eq(todoId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Page<ProjectionTodoResponse> findByTitleOrCreatedAtOrManagerNickname(String title, LocalDateTime startDate, LocalDateTime endDate, String managerNickname, Pageable pageable) {

        List<ProjectionTodoResponse> results = jpaQueryFactory.select(
                        Projections.constructor(ProjectionTodoResponse.class,
                                todo.title,
                                manager.countDistinct(),
                                comment.countDistinct())
                ).from(todo)
                .leftJoin(todo.managers, manager)
                .leftJoin(todo.comments, comment)
                .leftJoin(manager.user, user)
                .where(titleCheck(title),
                        DateCheck(startDate, endDate),
                        managerNicknameCheck(managerNickname))
                .groupBy(todo.id)
                .offset(pageable.getOffset()) // 페이지 시작 위치
                .limit(pageable.getPageSize()) // 페이지 크기
                .fetch();

        return new PageImpl<>(results, pageable, results.size());
    }

    private BooleanExpression titleCheck(String title) {
        return title != null ? todo.title.containsIgnoreCase(title) : null;
    }

    private BooleanExpression DateCheck(LocalDateTime startDate, LocalDateTime endDate) {
        return startDate != null & endDate!= null ? todo.createdAt.between(startDate, endDate) : null;
    }

    private BooleanExpression managerNicknameCheck(String managerNickname) {
        return managerNickname != null ? user.nickname.containsIgnoreCase(managerNickname) : null;
    }

}
