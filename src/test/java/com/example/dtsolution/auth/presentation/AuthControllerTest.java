package com.example.dtsolution.auth.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.example.dtsolution.auth.domain.dto.SignupRequest;
import com.example.dtsolution.global.exception.DuplicateUsernameException;
import com.example.dtsolution.global.exception.PasswordMismatchException;
import com.example.dtsolution.member.application.MemberService;
import com.example.dtsolution.member.domain.dto.MemberResponse;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("AuthController 통합 테스트")
class AuthControllerTest {

  @Autowired private MockMvc mockMvc;
  @MockitoBean private MemberService memberService;

  @Test
  @DisplayName("비로그인 상태에서 메인 페이지 접근 가능")
  void 메인페이지_비로그인_접근가능() throws Exception {
    mockMvc.perform(get("/")).andExpect(status().isOk()).andExpect(view().name("home"));
  }

  @Test
  @DisplayName("로그인 상태에서 메인 페이지 접근 시 사용자 정보 모델에 포함")
  void 메인페이지_로그인_사용자정보표시() throws Exception {
    MemberResponse response = new MemberResponse(UUID.randomUUID(), "user1", "ROLE_USER");
    given(memberService.getMemberByUsername(anyString())).willReturn(response);

    mockMvc
        .perform(get("/").with(user("user1").roles("USER")))
        .andExpect(status().isOk())
        .andExpect(view().name("home"))
        .andExpect(model().attributeExists("member"));
  }

  @Test
  @DisplayName("GET /login 요청 시 login 뷰 반환")
  void 로그인페이지_접근() throws Exception {
    mockMvc.perform(get("/login")).andExpect(status().isOk()).andExpect(view().name("login"));
  }

  @Test
  @DisplayName("GET /signup 요청 시 signup 뷰 반환")
  void 회원가입페이지_접근() throws Exception {
    mockMvc.perform(get("/signup")).andExpect(status().isOk()).andExpect(view().name("signup"));
  }

  @Test
  @DisplayName("정상 요청 시 회원가입 성공 후 /login 으로 리다이렉트")
  void 회원가입_성공_로그인페이지로리다이렉트() throws Exception {
    doNothing().when(memberService).signup(any(SignupRequest.class));

    mockMvc
        .perform(
            post("/signup")
                .with(csrf())
                .param("username", "newuser")
                .param("password", "pass1234")
                .param("confirmPassword", "pass1234"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/login"));
  }

  @Test
  @DisplayName("빈 아이디 입력 시 username 필드 유효성 오류")
  void 회원가입_빈아이디_유효성오류() throws Exception {
    mockMvc
        .perform(
            post("/signup")
                .with(csrf())
                .param("username", "")
                .param("password", "pass1234")
                .param("confirmPassword", "pass1234"))
        .andExpect(status().isOk())
        .andExpect(view().name("signup"))
        .andExpect(model().attributeHasFieldErrors("signupRequest", "username"));
  }

  @Test
  @DisplayName("비밀번호 불일치 시 confirmPassword 필드 오류")
  void 회원가입_비밀번호불일치_필드오류() throws Exception {
    doThrow(new PasswordMismatchException("비밀번호가 일치하지 않습니다."))
        .when(memberService)
        .signup(any(SignupRequest.class));

    mockMvc
        .perform(
            post("/signup")
                .with(csrf())
                .param("username", "user1")
                .param("password", "pass1234")
                .param("confirmPassword", "different"))
        .andExpect(status().isOk())
        .andExpect(view().name("signup"))
        .andExpect(model().attributeHasFieldErrors("signupRequest", "confirmPassword"));
  }

  @Test
  @DisplayName("중복 아이디 가입 시 username 필드 오류")
  void 회원가입_아이디중복_필드오류() throws Exception {
    doThrow(new DuplicateUsernameException("이미 사용 중인 아이디입니다."))
        .when(memberService)
        .signup(any(SignupRequest.class));

    mockMvc
        .perform(
            post("/signup")
                .with(csrf())
                .param("username", "user1")
                .param("password", "pass1234")
                .param("confirmPassword", "pass1234"))
        .andExpect(status().isOk())
        .andExpect(view().name("signup"))
        .andExpect(model().attributeHasFieldErrors("signupRequest", "username"));
  }

  @Test
  @DisplayName("비로그인 상태에서 보호된 페이지 접근 시 /login 으로 리다이렉트")
  void 보호된페이지_비로그인_로그인으로리다이렉트() throws Exception {
    mockMvc
        .perform(get("/protected"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/login"));
  }
}
