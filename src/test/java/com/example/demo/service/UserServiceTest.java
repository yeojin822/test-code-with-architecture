package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

@TestPropertySource("classpath:test-application.properties")

@SqlGroup({
        @Sql(value = "/sql/user-service-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD) ,
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
@SpringBootTest
class UserServiceTest {

    @Autowired
    UserService userService;

    @Test
    void getByEmail은_ACTIVE_상태인_유저를_찾아올_수_있다() {
        //given
        String email = "test@naver.com";
        //when
        UserEntity result = userService.getByEmail(email);
        //then
        assertThat(result.getNickname()).isEqualTo("test");

    }

    @Test
    void getByEmail은_PENDING_상태인_유저를_찾아올_수_없다() {
        //given
        String email = "test2@naver.com";

        //when
        //then
        assertThatThrownBy(() -> {
            UserEntity result = userService.getByEmail(email);
        }).isInstanceOf(ResourceNotFoundException.class);

    }

    @Test
    void getById는_ACTIVE_상태인_유저를_찾아올_수_있다() {
        //given
        //when
        UserEntity result = userService.getById(1);
        //then
        assertThat(result.getNickname()).isEqualTo("test");

    }

    @Test
    void getById는_PENDING_상태인_유저를_찾아올_수_없다() {
        //given
        //when
        //then
        assertThatThrownBy(() -> {
            UserEntity result = userService.getById(2);
        }).isInstanceOf(ResourceNotFoundException.class);

    }
}