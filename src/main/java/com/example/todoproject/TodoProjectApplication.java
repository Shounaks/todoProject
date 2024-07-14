package com.example.todoproject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class TodoProjectApplication {
    private final TodoRepository todoRepository;

    public static void main(String[] args) {
        SpringApplication.run(TodoProjectApplication.class, args);
    }


    private Mono<ServerResponse> handleHeartBeat(ServerRequest serverRequest) {
        log.info("Received HeartBeat request");
        return ServerResponse.ok().bodyValue("Hello Java");
    }

    private Mono<ServerResponse> handleReturnAllTodoList(ServerRequest serverRequest) {
        log.info("Received ReturnAllTodoList request");
        return ServerResponse.ok().bodyValue(todoRepository.findAll());
    }

    @Bean
    public RouterFunction<ServerResponse> routerFunction() {
        return RouterFunctions
                .route(GET("/heartbeat"), this::handleHeartBeat)
                .andRoute(GET("/all"), this::handleReturnAllTodoList)
                .andRoute(POST("/add").and(accept(MediaType.APPLICATION_JSON)), this::addTodo)
                .andRoute(DELETE("/delete/{id}").and(accept(MediaType.APPLICATION_JSON)), this::deleteTodo);
    }

    private Mono<ServerResponse> addTodo(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(TodoEntity.class)
                .map(todoRepository::save)
                .flatMap(savedTodo -> ServerResponse.ok().bodyValue(savedTodo.getId()));
    }

    private Mono<ServerResponse> deleteTodo(ServerRequest serverRequest) {
        final Long id = validateAndTransformIntoLong(serverRequest.pathVariable("id"));
        todoRepository.deleteById(id);
        log.info("Deleted Todo with id {}", id);
        return ServerResponse.ok().bodyValue("SUCCESSFULLY DELETED ID: " + id);
    }

    private static Long validateAndTransformIntoLong(String id) {
        try {
            return Long.valueOf(id);
        } catch (Exception x) {
            return -1L;
        }
    }

}
