package org.example.expert.domain.todo.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class TodoRepositoryImpl implements TodoRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
        QTodo todo = QTodo.todo;
        QUser user = QUser.user;

        Todo result = jpaQueryFactory
                .selectFrom(todo)
                .leftJoin(todo.user, user).fetchJoin()
                .where(todo.id.eq(todoId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Page<ProjectionTodoResponse> findByTitle(String title, Pageable pageable) {
        QTodo todo = QTodo.todo;
        QManager manager = QManager.manager;
        QComment comment = QComment.comment;

        List<ProjectionTodoResponse> results = jpaQueryFactory.select(
                        Projections.constructor(ProjectionTodoResponse.class, // constructor 뭐임?
                                todo.title,
                                manager.countDistinct(), // count랑 countDistinct 차이 뭐임?
                                comment.countDistinct())
                ).from(todo)
                .leftJoin(todo.managers, manager)
                .leftJoin(todo.comments, comment)
                .where(todo.title.containsIgnoreCase(title)) // 부분 검색 적용 containsIgnoreCase 뭐임?
                .groupBy(todo.id)
                .offset(pageable.getOffset()) // 페이지 시작 위치
                .limit(pageable.getPageSize()) // 페이지 크기
                .fetch();// 전체 개수와 함께 가져오기

        return new PageImpl<>(results, pageable, results.size());

    }

    @Override
    public Page<ProjectionTodoResponse> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        QTodo todo = QTodo.todo;
        QManager manager = QManager.manager;
        QComment comment = QComment.comment;

        List<ProjectionTodoResponse> results = jpaQueryFactory.select(
                        Projections.constructor(ProjectionTodoResponse.class, // constructor 뭐임?
                                todo.title,
                                manager.countDistinct(), // count랑 countDistinct 차이 뭐임?
                                comment.countDistinct())
                ).from(todo)
                .leftJoin(todo.managers, manager)
                .leftJoin(todo.comments, comment)
                .where(todo.createdAt.between(startDate, endDate)) // 생성일 기간 검색
                .groupBy(todo.id)
                .offset(pageable.getOffset()) // 페이지 시작 위치
                .limit(pageable.getPageSize()) // 페이지 크기
                .fetch();// 전체 개수와 함께 가져오기

        return new PageImpl<>(results, pageable, results.size());
    }

    @Override
    public Page<ProjectionTodoResponse> findByManagerNickname(String managerNickname, Pageable pageable) {
        QTodo todo = QTodo.todo;
        QManager manager = QManager.manager;
        QComment comment = QComment.comment;
        QUser user = QUser.user;

        List<ProjectionTodoResponse> results = jpaQueryFactory.select(
                        Projections.constructor(ProjectionTodoResponse.class, // constructor 뭐임?
                                todo.title,
                                manager.countDistinct(), // count랑 countDistinct 차이 뭐임?
                                comment.countDistinct())
                ).from(todo)
                .leftJoin(todo.managers, manager)
                .leftJoin(todo.comments, comment)
                .leftJoin(manager.user, user)
                .where(user.nickname.containsIgnoreCase(managerNickname)) // 매니저 이름 검색
                .groupBy(todo.id)
                .offset(pageable.getOffset()) // 페이지 시작 위치
                .limit(pageable.getPageSize()) // 페이지 크기
                .fetch();// 전체 개수와 함께 가져오기

        return new PageImpl<>(results, pageable, results.size());
    }

    @Override
    public Page<ProjectionTodoResponse> findByTitleAndCreatedAtBetween(String title, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        QTodo todo = QTodo.todo;
        QManager manager = QManager.manager;
        QComment comment = QComment.comment;

        List<ProjectionTodoResponse> results = jpaQueryFactory.select(
                        Projections.constructor(ProjectionTodoResponse.class, // constructor 뭐임?
                                todo.title,
                                manager.countDistinct(), // count랑 countDistinct 차이 뭐임?
                                comment.countDistinct())
                ).from(todo)
                .leftJoin(todo.managers, manager)
                .leftJoin(todo.comments, comment)
                .where(todo.createdAt.between(startDate, endDate).and(todo.title.containsIgnoreCase(title))) // 생성일 기간 + 제목 검색
                .groupBy(todo.id)
                .offset(pageable.getOffset()) // 페이지 시작 위치
                .limit(pageable.getPageSize()) // 페이지 크기
                .fetch();// 전체 개수와 함께 가져오기

        return new PageImpl<>(results, pageable, results.size());
    }

    @Override
    public Page<ProjectionTodoResponse> findByTitleAndManagerNickname(String title, String managerNickname, Pageable pageable) {
        QTodo todo = QTodo.todo;
        QManager manager = QManager.manager;
        QComment comment = QComment.comment;
        QUser user = QUser.user;

        List<ProjectionTodoResponse> results = jpaQueryFactory.select(
                        Projections.constructor(ProjectionTodoResponse.class, // constructor 뭐임?
                                todo.title,
                                manager.countDistinct(), // count랑 countDistinct 차이 뭐임?
                                comment.countDistinct())
                ).from(todo)
                .leftJoin(todo.managers, manager)
                .leftJoin(todo.comments, comment)
                .leftJoin(manager.user, user)
                .where(todo.title.containsIgnoreCase(title).and(user.nickname.containsIgnoreCase(managerNickname))) // 제목 + 매니저 이름 검색
                .groupBy(todo.id)
                .offset(pageable.getOffset()) // 페이지 시작 위치
                .limit(pageable.getPageSize()) // 페이지 크기
                .fetch();// 전체 개수와 함께 가져오기

        return new PageImpl<>(results, pageable, results.size());
    }

    @Override
    public Page<ProjectionTodoResponse> findByTitleAndManagerNicknameAndCreatedAtBetween(String title, String managerNickname, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        QTodo todo = QTodo.todo;
        QManager manager = QManager.manager;
        QComment comment = QComment.comment;
        QUser user = QUser.user;

        List<ProjectionTodoResponse> results = jpaQueryFactory.select(
                        Projections.constructor(ProjectionTodoResponse.class, // constructor 뭐임?
                                todo.title,
                                manager.countDistinct(), // count랑 countDistinct 차이 뭐임?
                                comment.countDistinct())
                ).from(todo)
                .leftJoin(todo.managers, manager)
                .leftJoin(todo.comments, comment)
                .leftJoin(manager.user, user)
                .where(todo.createdAt.between(startDate, endDate).and(todo.title.containsIgnoreCase(title)).and(user.nickname.containsIgnoreCase(managerNickname))) // 제목 + 기간 + 매니저 이름 검색
                .groupBy(todo.id)
                .offset(pageable.getOffset()) // 페이지 시작 위치
                .limit(pageable.getPageSize()) // 페이지 크기
                .fetch();// 전체 개수와 함께 가져오기

        return new PageImpl<>(results, pageable, results.size());
    }

    @Override
    public Page<ProjectionTodoResponse> findAllProjection(Pageable pageable) {
        QTodo todo = QTodo.todo;
        QManager manager = QManager.manager;
        QComment comment = QComment.comment;

        List<ProjectionTodoResponse> results = jpaQueryFactory.select(
                        Projections.constructor(ProjectionTodoResponse.class, // constructor 뭐임?
                                todo.title,
                                manager.countDistinct(), // count랑 countDistinct 차이 뭐임?
                                comment.countDistinct())
                ).from(todo)
                .leftJoin(todo.managers, manager)
                .leftJoin(todo.comments, comment)
                .groupBy(todo.id)
                .offset(pageable.getOffset()) // 페이지 시작 위치
                .limit(pageable.getPageSize()) // 페이지 크기
                .fetch();// 전체 개수와 함께 가져오기

        return new PageImpl<>(results, pageable, results.size());
    }
}
