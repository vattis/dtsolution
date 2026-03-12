# DTSolution — 로그인/로그아웃/회원가입

Spring Boot 기반 회원 인증 시스템 구현 과제

---

## 기술 스택

| 분류 | 기술 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 4.0.3 |
| Security | Spring Security 7.x |
| ORM | Spring Data JPA + Hibernate 7 |
| DB | MySQL 8.0 |
| Template | Thymeleaf + Bootstrap 5.3.3 |
| Build | Gradle |

---

## 주요 기능

- 회원가입 — 아이디 중복 확인, 비밀번호 확인, Bean Validation
- 로그인 — Spring Security Form Login, BCrypt 비밀번호 검증
- 로그아웃 — 세션 삭제 후 로그인 페이지 이동
- 비로그인 사용자도 메인 페이지 접근 가능
- 보호된 페이지 접근 시 로그인 페이지로 리다이렉트


---

## 실행 방법

### 1. 환경 변수 설정

프로젝트 루트에 `.env` 파일 생성:

DB_PASSWORD=your_mysql_password



### 2. MySQL DB



### 3. 애플리케이션 실행

./gradlew bootRun
테이블은 ddl-auto: update 설정으로 자동 생성됩니다.

접속: http://localhost:8080

ERD

┌──────────────────────────────┐
│            member            │
├──────────────┬───────────────┤
│ id           │ BIGINT PK AI  │
│ uuid         │ BINARY(16) UQ │
│ username     │ VARCHAR(50) UQ│
│ password     │ VARCHAR(60)   │
│ role         │ VARCHAR(20)   │
└──────────────┴───────────────┘
id — 내부 PK
uuid — 외부 식별자, 가입 시 자동 생성
password — BCrypt 해시 저장 (60자 고정)
role — ROLE_USER
API 흐름
Method	URL	설명
GET	/	메인 페이지 (비로그인 허용)
GET	/login	로그인 페이지
POST	/login	로그인 처리 (Spring Security)
POST	/logout	로그아웃
GET	/signup	회원가입 페이지
POST	/signup	회원가입 처리
테스트

./gradlew test
테스트	방식	대상
MemberServiceTest:	  Mockito 단위 테스트	
MemberRepositoryTest:	@DataJpaTest (H2)	findByUsername, existsByUsername
AuthControllerTest:	  @SpringBootTest + MockMvc	전체 인증 흐름

