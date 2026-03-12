package com.example.dtsolution.member.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.example.dtsolution.auth.domain.dto.SignupRequest;
import com.example.dtsolution.global.exception.DuplicateUsernameException;
import com.example.dtsolution.global.exception.PasswordMismatchException;
import com.example.dtsolution.member.domain.dto.MemberResponse;
import com.example.dtsolution.member.domain.entity.Member;
import com.example.dtsolution.member.infrastructure.repository.MemberRepository;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberService 단위 테스트")
class MemberServiceTest {

  @Mock private MemberRepository memberRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @InjectMocks private MemberService memberService;

  @Test
  @DisplayName("존재하는 사용자 조회 시 MemberResponse 반환")
  void getMemberByUsername_성공() {
    // given
    Member member =
        Member.builder().username("user1").password("encoded").role("ROLE_USER").build();
    given(memberRepository.findByUsername("user1")).willReturn(Optional.of(member));

    // when
    MemberResponse response = memberService.getMemberByUsername("user1");

    // then
    assertThat(response.username()).isEqualTo("user1");
    assertThat(response.role()).isEqualTo("ROLE_USER");
  }

  @Test
  @DisplayName("존재하지 않는 사용자 조회 시 NoSuchElementException 발생")
  void getMemberByUsername_존재하지않는사용자_예외() {
    // given
    given(memberRepository.findByUsername("nobody")).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> memberService.getMemberByUsername("nobody"))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessage("사용자를 찾을 수 없습니다.");
  }

  @Test
  @DisplayName("정상 요청 시 회원 저장 성공")
  void signup_성공() {
    // given
    SignupRequest request = new SignupRequest("user1", "pass1234", "pass1234");
    given(memberRepository.existsByUsername("user1")).willReturn(false);
    given(passwordEncoder.encode("pass1234")).willReturn("encoded");

    // when
    memberService.signup(request);

    // then
    verify(memberRepository).save(any(Member.class));
  }

  @Test
  @DisplayName("비밀번호 불일치 시 PasswordMismatchException 발생 및 저장 미호출")
  void signup_비밀번호불일치_예외() {
    // given
    SignupRequest request = new SignupRequest("user1", "pass1234", "different");

    // when & then
    assertThatThrownBy(() -> memberService.signup(request))
        .isInstanceOf(PasswordMismatchException.class)
        .hasMessage("비밀번호가 일치하지 않습니다.");

    verify(memberRepository, never()).save(any());
  }

  @Test
  @DisplayName("중복 아이디 가입 시 DuplicateUsernameException 발생 및 저장 미호출")
  void signup_아이디중복_예외() {
    // given
    SignupRequest request = new SignupRequest("user1", "pass1234", "pass1234");
    given(memberRepository.existsByUsername("user1")).willReturn(true);

    // when & then
    assertThatThrownBy(() -> memberService.signup(request))
        .isInstanceOf(DuplicateUsernameException.class)
        .hasMessage("이미 사용 중인 아이디입니다.");

    verify(memberRepository, never()).save(any());
  }
}
