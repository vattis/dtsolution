package com.example.dtsolution.member.domain.dto;

import com.example.dtsolution.member.domain.entity.Member;
import java.util.UUID;

public record MemberResponse(UUID uuid, String username, String role) {
  public static MemberResponse from(Member member) {
    return new MemberResponse(member.getUuid(), member.getUsername(), member.getRole());
  }
}
