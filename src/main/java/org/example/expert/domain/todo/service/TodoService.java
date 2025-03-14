package org.example.expert.domain.todo.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.ProjectionTodoResponse;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.todo.repository.TodoRepositoryImpl;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor

// @Transactional에 (readOnly = true)가 붙으면 읽기전용 트랜잭션으로 사용하겠다는 의미이다.
// 따라서 SELECT 쿼리만 실행 가능하고 JPA가 따로 변경감지를 하지 않는다.
// (readOnly = true)는 데이터 변경이 없는 메서드에서 실수로 save() 같은 DB를 변경하는 쿼리를 실행하는 걸
// 방지할 때 사용하도록 하자.
public class TodoService {

    private final TodoRepository todoRepository;
    private final WeatherClient weatherClient;
    private final TodoRepositoryImpl todoRepositoryImpl;

    @Transactional
    public TodoSaveResponse saveTodo(AuthUser authUser, TodoSaveRequest todoSaveRequest) {

        // 유저 정보 받아오기
        User user = User.fromAuthUser(authUser);

        // 날씨 정보 받아오기
        String weather = weatherClient.getTodayWeather();

        // 새로운 Todo 생성
        Todo newTodo = new Todo(
                todoSaveRequest.getTitle(),
                todoSaveRequest.getContents(),
                weather,
                user
        );

        // DB에 저장
        Todo savedTodo = todoRepository.save(newTodo);

        return new TodoSaveResponse(
                savedTodo.getId(),
                savedTodo.getTitle(),
                savedTodo.getContents(),
                weather,
                new UserResponse(user.getId(), user.getEmail())
        );
    }

    @Transactional(readOnly = true)
    public Page<TodoResponse> getTodos(int page, int size, String weather, LocalDateTime startDate, LocalDateTime endDate) {
        Page<Todo> todos;
        Sort modifiedSort = Sort.by("modifiedAt").descending(); // 기본적으로 수정일 기준 내림차순 정렬


        if(weather != null && (startDate == null && endDate == null)) { // 날씨 검색만 있는 경우

            Pageable pageable = PageRequest.of(page - 1, size, modifiedSort);
            todos = todoRepository.findAllByWeather(weather, pageable);

        }else if(weather == null && (startDate != null && endDate != null)) { // 기간 검색만 있는 경우

            PageRequest pageable = PageRequest.of(page - 1, size, modifiedSort);
            todos = todoRepository.findByModifiedAtBetween(startDate, endDate, pageable);

        } else if (weather != null && (startDate != null && endDate != null)) { // 날씨검색, 날짜검색 둘 다 있는 경우

            PageRequest pageable = PageRequest.of(page - 1, size, modifiedSort);
            todos = todoRepository.findByWeatherAndModifiedAtBetween(weather, startDate, endDate, pageable);

        } else { // 아무 조건도 걸지 않은 경우
            Pageable pageable = PageRequest.of(page - 1, size, modifiedSort);
            todos = todoRepository.findAll(pageable);
        }

        return todos.map(todo -> new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(todo.getUser().getId(), todo.getUser().getEmail()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        ));
    }

    @Transactional(readOnly = true)
    public TodoResponse getTodo(long todoId) {

        Optional<Todo> foundTodo = todoRepository.findByIdWithUser(todoId); // QueryDSL을 적용한 메서드
        Todo todo = foundTodo.get();

        User user = todo.getUser();

        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(user.getId(), user.getEmail()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        );
    }

    public Page<ProjectionTodoResponse> getTodoByQueryDSL(int page, int size, String title, LocalDateTime startDate, LocalDateTime endDate, String managerNickname) {
        // 기본적으로 생성일 기준으로 최신순으로 정렬
        Sort createdSort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(page - 1, size, createdSort);
        Page<ProjectionTodoResponse> todos;

        // 제목 + 생성일 기간 + 매니저이름
        if ((startDate == null && endDate == null) &&title != null && managerNickname == null) {

            todos = todoRepository.findByTitle(title, pageable);

        } else if ((startDate != null && endDate != null) &&title == null && managerNickname == null) {

            todos = todoRepository.findByCreatedAtBetween(startDate, endDate, pageable);

        } else if (managerNickname != null && (startDate == null && endDate == null) &&title == null ) {

            todos = todoRepository.findByManagerNickname(managerNickname, pageable);

        } else if (startDate != null && endDate != null && managerNickname == null) {

            todos = todoRepository.findByTitleAndCreatedAtBetween(title, startDate, endDate, pageable);

        } else if (startDate == null && endDate == null && title != null && managerNickname != null) {

            todos = todoRepository.findByTitleAndManagerNickname(title, managerNickname, pageable);

        } else if(title != null && startDate != null && endDate != null && managerNickname != null) {

            todos = todoRepository.findByTitleAndManagerNicknameAndCreatedAtBetween(title, managerNickname, startDate, endDate, pageable);

        } else {
            todos = todoRepository.findAllProjection(pageable);
        }

        return todos;

    }
}
