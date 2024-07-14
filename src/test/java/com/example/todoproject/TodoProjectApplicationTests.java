package com.example.todoproject;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TodoProjectApplicationTests {
    @Autowired
    private TestRestTemplate template;

    @Autowired
    TodoRepository todoRepository;

    @BeforeEach
    void setup() {
        todoRepository.deleteAll();
    }

    @Test
    void contextLoads() {
    }

    @Test
    void test_heartbeat() {
        // Given
        // When
        String actualHeartbeatResponse = template.getForEntity("/heartbeat", String.class).getBody();
        // Then
        Assertions.assertThat(actualHeartbeatResponse).isEqualTo("Hello Java");
    }

    @Test
    void test_todo_insertion() {
        // Given
        final TodoEntity todo = TodoEntity.builder()
                .id(1L)
                .title("DUMMY_TITLE")
                .description("DUMMY_DESC")
                .build();
        // When
        final var actualSavedId = template.postForEntity("/add", todo, Long.class).getBody();
        // Then
        Assertions.assertThat(actualSavedId).isGreaterThanOrEqualTo(1);
    }

    @Test
    void test_delete() {
        // Given - add a single Entity
        template.postForEntity("/add", TodoEntity.builder().id(1L).title("DUMMY_TITLE").description("DUMMY_DESC").build(), Long.class);
        // When
        final String actualDeleteResponse = template.getForEntity("/delete/1", String.class).getBody();
        // Then
        Assertions.assertThat(actualDeleteResponse).isNotBlank();
    }

    @Test
    void test_retrieve() {
        // Given
        // When
        List body = template.getForEntity("/all", List.class).getBody();
        // Then
        Assertions.assertThat(body).asInstanceOf(InstanceOfAssertFactories.LIST).isNotNull();
    }
}
