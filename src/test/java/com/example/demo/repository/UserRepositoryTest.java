package com.example.demo.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.model.UserStatus;
import java.util.Optional;
import org.h2.engine.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest(showSql = true)
@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:test-application.properties")
@Sql("/sql/user-repository-test-data.sql")
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

//    @Test
//    void UserRepository_가_제대로_연걸되었다() {
//        // given
//        UserEntity userEntity = new UserEntity();
//        userEntity.setEmail("test@naver.com");
//        userEntity.setAddress("Seoul");
//        userEntity.setNickname("test");
//        userEntity.setStatus(UserStatus.ACTIVE);
//        userEntity.setCertificationCode("aaaaa-aaaaa-aaaaa-aaaaa-aaaaa");
//
//        // when
//        UserEntity result = userRepository.save(userEntity);
//
//        // then
//        assertThat(result.getId()).isNotNull();
//
//    }

    @Test
    void findByIdAndStatus_로_유저_데이터를_찾아올_수_있다() {
        // given
        // when
        Optional<UserEntity> result = userRepository.findByIdAndStatus(1, UserStatus.ACTIVE) ;

        // then
        assertThat(result.isPresent()).isTrue();
    }

    @Test
    void findByIdAndStatus_는_데이터가_없으면_Optional_empty_를_내려준다() {
        // given
        // when
        Optional<UserEntity> result = userRepository.findByIdAndStatus(1, UserStatus.PENDING) ;

        // then
        assertThat(result.isPresent()).isFalse();
        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    void findByEmailAndStatus_로_유저_데이터를_찾아올_수_있다() {
        // given
        // when
        Optional<UserEntity> result = userRepository.findByEmailAndStatus("test@naver.com", UserStatus.ACTIVE) ;

        // then
        assertThat(result.isPresent()).isTrue();
    }

    @Test
    void findByEmailAndStatus_는_데이터가_없으면_Optional_empty_를_내려준다() {
        // given
        // when
        Optional<UserEntity> result = userRepository.findByEmailAndStatus("test@naver.com", UserStatus.PENDING) ;

        // then
        assertThat(result.isPresent()).isFalse();
        assertThat(result.isEmpty()).isTrue();
    }
}