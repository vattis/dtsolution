package com.example.dtsolution.member.application;

import com.example.dtsolution.auth.domain.dto.SignupRequest;
import com.example.dtsolution.global.exception.DuplicateUsernameException;
import com.example.dtsolution.global.exception.PasswordMismatchException;
import com.example.dtsolution.member.domain.dto.MemberResponse;
import com.example.dtsolution.member.domain.entity.Member;
import com.example.dtsolution.member.infrastructure.repository.MemberRepository;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional(readOnly = true)
  public MemberResponse getMemberByUsername(String username) {
    Member member =
        memberRepository
            .findByUsername(username)
            .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
    return MemberResponse.from(member);
  }

  @Transactional
  public void signup(SignupRequest request) {
    if (!request.password().equals(request.confirmPassword())) {
      throw new PasswordMismatchException("비밀번호가 일치하지 않습니다.");
    }
    if (memberRepository.existsByUsername(request.username())) {
      throw new DuplicateUsernameException("이미 사용 중인 아이디입니다.");
    }

    Member member =
        Member.builder()
            .username(request.username())
            .password(passwordEncoder.encode(request.password()))
            .role("ROLE_USER")
            .build();

    memberRepository.save(member);
  }
}
