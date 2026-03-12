package com.example.dtsolution.member.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.dtsolution.member.domain.entity.Member;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
@DisplayName("MemberRepository 통합 테스트")
class MemberRepositoryTest {

  @Autowired private MemberRepository memberRepository;

  @BeforeEach
  void setUp() {
    memberRepository.save(
        Member.builder().username("user1").password("encoded").role("ROLE_USER").build());
  }

  @Test
  @DisplayName("존재하는 username 조회 시 Member 반환")
  void findByUsername_존재하는사용자_반환() {
    Optional<Member> result = memberRepository.findByUsername("user1");

    assertThat(result).isPresent();
    assertThat(result.get().getUsername()).isEqualTo("user1");
  }

  @Test
  @DisplayName("존재하지 않는 username 조회 시 빈 Optional 반환")
  void findByUsername_존재하지않는사용자_빈Optional반환() {
    Optional<Member> result = memberRepository.findByUsername("nobody");

    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("존재하는 username 확인 시 true 반환")
  void existsByUsername_존재하는사용자_true반환() {
    boolean exists = memberRepository.existsByUsername("user1");

    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("존재하지 않는 username 확인 시 false 반환")
  void existsByUsername_존재하지않는사용자_false반환() {
    boolean exists = memberRepository.existsByUsername("nobody");

    assertThat(exists).isFalse();
  }
}
