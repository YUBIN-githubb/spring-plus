package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    //sort를 사용하여 대체
//    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u ORDER BY t.modifiedAt DESC")
//    Page<Todo> findAllByOrderByModifiedAtDesc(Pageable pageable);

    @Query("SELECT t FROM Todo t " +
            "LEFT JOIN t.user " +
            "WHERE t.id = :todoId")
    Optional<Todo> findByIdWithUser(@Param("todoId") Long todoId);

    // 날씨 검색
    @Query("select t from Todo t where t.weather = :weather")
    Page<Todo> findAllByWeather(@Param("weather") String weather, Pageable pageable);

    // 기본 검색
    Page<Todo> findAll(Pageable pageable);

    // 기간 검색
    @Query("select t from Todo t where t.modifiedAt between :startDate and :endDate")
    Page<Todo> findByModifiedAtBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    // 날씨 & 기간 검색
    @Query("select t from Todo t where (t.modifiedAt between :startDate and :endDate) and t.weather = :weather")
    Page<Todo> findByWeatherAndModifiedAtBetween(
            @Param("weather") String weather,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );
}
