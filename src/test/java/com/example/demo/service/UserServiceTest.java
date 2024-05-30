package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.exception.CertificationCodeNotMatchedException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.UserStatus;
import com.example.demo.model.dto.UserCreateDto;
import com.example.demo.model.dto.UserUpdateDto;
import com.example.demo.repository.UserEntity;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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

    @MockBean
    private JavaMailSender javaMailSender;

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
        UserEntity result = userService.getById(2);
        //then
        assertThat(result.getNickname()).isEqualTo("test");

    }

    @Test
    void getById는_PENDING_상태인_유저를_찾아올_수_없다() {
        //given
        //when
        //then
        assertThatThrownBy(() -> {
            UserEntity result = userService.getById(3);
        }).isInstanceOf(ResourceNotFoundException.class);

    }

    @Test
    void userCreateDto_를_이용해_유저를_생성할_수_있다() {
        //given
        UserCreateDto userCreateDto = UserCreateDto.builder()
                .email("test3@naver.com")
                .address("gyeonggi-do")
                .nickname("test3")
                .build();

        BDDMockito.doNothing().when(javaMailSender).send(BDDMockito.any(SimpleMailMessage.class));

        //when
        UserEntity result = userService.create(userCreateDto);
        //then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(UserStatus.PENDING);
        //assertThat(result.getCertificationCode()).isEqualTo(""); //FIXME
    }

    @Test
    void userUdateDto_를_이용해_유저를_수정할_수_있다() {
        //given
        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .address("inchon")
                .nickname("test33")
                .build();


        //when
        userService.update(2, userUpdateDto);
        //then
        UserEntity result = userService.getById(2);
        assertThat(result.getAddress()).isEqualTo(userUpdateDto.getAddress());
        assertThat(result.getNickname()).isEqualTo(userUpdateDto.getNickname());
    }

    @Test
    void user_가_로그인_하면_마지막_로그인_시간이_변경된다() {
        //given
        //when
        userService.login(2);
        //then
        UserEntity result = userService.getById(2);
        assertThat(result.getLastLoginAt()).isGreaterThan(0);
//        assertThat(result.getLastLoginAt()).isEqualTo(""); //FIXME
    }

    @Test
    void PENDING_상태의_사용자는_인증_코드로_ACTIVE_할_수_있다() {
        //given
        //when
        userService.verifyEmail(3, "aaaa-aaaaa-aaaaa-aaaa-aaaabba");
        //then
        UserEntity result = userService.getById(3);
        assertThat(result.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void PENDING_상태의_사용자는_잘못된_인증_코드를_받으면_에러가_발생한다() {
        //given
        //when

        //then
        assertThatThrownBy(()->{
            userService.verifyEmail(3, "aaaa-aaaaa-aaaaa-aaaa-aaaabdddba ");
        }).isInstanceOf(CertificationCodeNotMatchedException.class);
    }

}