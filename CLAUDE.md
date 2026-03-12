# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
./gradlew build

# Run application
./gradlew bootRun

# Run tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.example.dtsolution.SomeServiceTest"

# Clean build
./gradlew clean build
```

## Tech Stack

- **Spring Boot 4.0.3** + Java 17 + Gradle
- **Spring Web MVC** — REST API (`/api/v1/...`)
- **Spring Data JPA** — ORM (MySQL 8.0)
- **Spring Validation** — Bean Validation (`@Valid`, `@NotNull`, etc.)
- **Lombok** — boilerplate reduction
- Main package: `com.example.dtsolution`

## Package Structure (Domain Module Architecture)

도메인별 모듈로 구성. 새 도메인 추가 시 `{domain}/` 하위에 동일 구조로 생성.

```
com.example.dtsolution/
├── auth/
│   ├── config/          # SecurityConfig 등 인증 관련 설정
│   └── domain/dto/      # 인증 관련 DTO (SignupRequest 등)
├── member/
│   ├── application/     # MemberService, CustomUserDetailsService
│   ├── domain/
│   │   ├── entity/      # Member 등 JPA Entity
│   │   └── dto/         # MemberResponse 등 응답 DTO
│   ├── infrastructure/
│   │   └── repository/  # MemberRepository (JpaRepository 확장)
│   └── presentation/    # AuthController 등
└── {도메인명}/           # 새 도메인은 위 구조 그대로 추가
    ├── application/
    ├── domain/
    │   ├── entity/
    │   └── dto/
    ├── infrastructure/
    │   └── repository/
    └── presentation/
```

**패키지 네이밍 규칙:**
- Entity: `com.example.dtsolution.{domain}.domain.entity`
- Repository: `com.example.dtsolution.{domain}.infrastructure.repository`
- Service: `com.example.dtsolution.{domain}.application`
- DTO: `com.example.dtsolution.{domain}.domain.dto`
- Controller: `com.example.dtsolution.{domain}.presentation`
- Auth 설정: `com.example.dtsolution.auth.config`

## Database (application.yml)

`application.properties` → `application.yml`로 전환 사용:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/{DB명}
    username: root
    password: {비밀번호}
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
```

## API Convention

- 엔드포인트 prefix: `/api/v1/{resource}`
- 표준 응답 형식:
```json
{ "success": true, "data": { ... } }
{ "success": false, "error": { "code": "ERROR_CODE", "message": "..." } }
```

## Entity & CRUD 패턴

엔티티 생성 시 기본 구조:
- `@Entity`, `@Table`, `@Id`, `@GeneratedValue` 사용
- BaseEntity (`createdAt`, `updatedAt`) 공통 상속
- Request/Response DTO 분리 (record 또는 class)
- Service에서 트랜잭션 관리 (`@Transactional`)

## 예외 처리

`global/exception/GlobalExceptionHandler`에서 `@RestControllerAdvice`로 처리:
- `MethodArgumentNotValidException` → 400
- `EntityNotFoundException` → 404
- `DuplicateException` → 409
- `Exception` → 500

## 페이징 / 검색

- `Pageable` 파라미터로 page, size, sort 처리
- 검색 조건은 AND 조합, QueryDSL 또는 JPA Specification 사용

## 테스트

- 단위 테스트: `@ExtendWith(MockitoExtension.class)`, `@Mock` / `@InjectMocks`
- 통합 테스트: `@SpringBootTest` + `MockMvc`
