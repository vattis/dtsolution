package com.example.dtsolution.member.domain.entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Entity
@Table(
    name = "member",
    uniqueConstraints = {
      @UniqueConstraint(name = "uk_member_username", columnNames = "username"),
      @UniqueConstraint(name = "uk_member_uuid", columnNames = "uuid")
    })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, updatable = false)
  private UUID uuid;

  @Column(nullable = false, length = 50)
  private String username;

  @Column(nullable = false, length = 60)
  private String password;

  @Column(nullable = false, length = 20)
  private String role;

  @PrePersist
  private void assignUuid() {
    if (this.uuid == null) {
      this.uuid = UUID.randomUUID();
    }
  }

  @Builder
  public Member(String username, String password, String role) {
    this.username = username;
    this.password = password;
    this.role = role;
  }
}
