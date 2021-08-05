# blog-project (블로그 프로젝트)

- 이 프로젝트는 자신의 블로그처럼 자신만의 글을 적고 사람들과 공유할 수 있습니다.
- 아직 부족한 부분이 많지만 처음부터 혼자서 구현해보았습니다.
- 아래 사이트에 들어가보실 수 있습니다.
- (참고) 페이스북 로그인이 동작하지 않습니다. -> 해결X

> http://ec2-3-37-254-101.ap-northeast-2.compute.amazonaws.com:8080/

<br/>
<br/>
<br/>

## 기술 스택

- Java 11 (openjdk Archive)
- MariaDB 10.5
- Spring Boot 2.4
- Spring Data JPA
- Spring Security
- Gradle 6.9
- Thymeleaf, Ajax, Bootstrap

<br/>
<br/>
<br/>

## 기능

- 회원가입,수정/로그인
	- Remember Me (소셜로그인은 동작X)
	- 회원수정시 소셜로그인한 사람은 닉네임만 변경 가능 (패스워드 변경 X)
- 게시판 CRUD
	- 조회수
- 소셜로그인 (구글, 로그인, 페이스북X)
	- 자동회원가입
	- 소셜로그인을 한 사람은 기본적으로 닉네임이 "소셜로그인"으로 설정되기 때문에 회원수정가서 따로 수정할 것을 권장합니다.
- 페이징/검색
- 댓글 등록/삭제
	- 자신이 작성한 댓글만 작성가능하도록 설정

<br/>
<br/>
<br/>

## 세부 설명

- DI는 스프링에서 권장하는 생성자 주입을 사용합니다.
- 먼저 회원가입이나 게시글이나 댓글의 CRUD는 REST API를 이용하여 구현하였습니다.
	- 데이터 타입은 Ajax를 이용하여 JSON으로 처리하였습니다.
	- Update와 Delete 같은 id값이 필요한 경우에는 `form 태그`에 `hidden`값을 주어 `id`값을 설정하였습니다.

### 로직 (예시)

```java
컨트롤러단
@PutMapping("/api/v1/board/{id}")
public Long update(@PathVariable Long id, @RequestBody BoardUpdateRequestDto boardUpdateRequestDto) {
    return boardService.update(id, boardUpdateRequestDto);
}

서비스단
@Transactional
public Long update(Long id, BoardUpdateRequestDto boardUpdateRequestDto) {
    Board board = boardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 id가 없습니다. id=" + id));
    board.update(boardUpdateRequestDto.getTitle(), boardUpdateRequestDto.getContent());
    return id;
}
```

- 기본적인 구조는 `Spring MVC` 패턴을 이용합니다.
	- 사용자에게 요청이 오면 `Controller`에서 사용자의 요청을 받고 그 요청주소에 맞는 메소드가 실행 (GET, POST...)
	- 사용자가 회원수정을 하려고 하면 그 회원에 대한 정보를 받아 `Model`에 담아 `View`에서는 ViewResolver에 의해 @Controller이면 html 파일을 리턴해주고, @RestController이면 JSON 데이터를 리턴해줍니다.
	- 주 로직은 `Service`에서 구현을 합니다.
- 데이터를 요청/응답할 때는 따로 `DTO` 클래스를 만들어 사용하고 있고, Service에서는 자동 커밋 및 데이터 정합성을 지키기 위해 `@Transactional`을 사용합니다.

<br/>
<br/>
<br/>

### 회원가입/수정, 게시글CRUD, 댓글 등록/삭제

- 위의 기능을 사용하기 위해 필요한 테이블은 총 3개입니다.
	- User, Board, Reply
	- User : Reply : Board -> 1 : N : 1
	- User : Board -> 1 : N

![캡처](https://user-images.githubusercontent.com/55525868/126988672-ff521533-74f5-4033-a231-e94ff8a04233.PNG)

```java
@Entity
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //sequence, auto_increment

    @Column(nullable = false, length = 50, unique = true)
    private String username; //아이디

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 30)
    private String email;

    @Column(nullable = false, length = 20)
    private String nickname; //닉네임

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column
    private String provider;

    @Column
    private String providerId;
}
```

- username : 회원ID (다른 회원과 겹치면 안되기 때문에 unique 값을 주었습니다.)
	- 소셜로그인을 위해 length를 넉넉하게 주었습니다.
- Role : 권한
	- 기본적으로 회원가입을 한 모든 사용자는 `Role.USER`을 갖고 이 사이트의 모든 컨텐츠를 이용할 수 있게 만들었습니다.
	- 따로 ADMIN 페이지를 구현하지 못했습니다.
- provider, providerId : 소셜로그인을 한 경우와 일반 회원가입을 한 경우를 구분하기 위해 추가하였습니다.
- 생성시간/수정시간은 JPA Auditing을 이용하여 테이블마다 자동화하였습니다.
- 프론트단에서 제약조건(ex. 아이디 4자 이상 등등)을 걸어두었습니다.

<br/>
<br/>
<br/>

```java
public class Board extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Lob
    private String content;

    @Column
    private int count; //조회수

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId")
    private User user;

    @OrderBy("id desc")
    @JsonIgnoreProperties({"board"})
    @OneToMany(mappedBy = "board", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<Reply> replyList;
}
```

- @ManyToOne(fetch = FetchType.EAGER)
	- 어떤 사용자가 게시글을 작성했는지 알기 위해 User 테이블을 조인해야 하는데 이 때 연관관계를 한 명의 사용자가 많은 게시글을 작성할 수 있으므로 @ManyToOne으로 설정합니다. (이 때 디폴트 FetchType은 Eager입니다.)
- @JoinColumn(name = "userId") : FK 컬럼명을 userId로 설정합니다.
- private List\<Reply> replyList;
	- @OrderBy("id desc") : 댓글 작성시 최근 댓글이 위로 올라오도록 설정합니다.
	- @JsonIgnoreProperties({"board"}) : Board를 조회하게 되면 Reply 객체를 조회하게 되는데 이 때 Reply 엔티티에는 또 Board 객체를 조회하게 됩니다. 이러면 무한 반복이 일어나기 때문에 한번만 조회하게 설정할 수 있게 @JsonIgnoreProperties를 사용합니다.
	- mappedBy : DB에는 하나의 row 데이터에는 하나의 값만 허용되기 때문에 `List`로 DB에 값을 저장할 수 없습니다. 그래서 실제로 조회만 할 수 있도록 mappedBy를 설정합니다.
	- Reply 테이블에는 외래키가 잡혀있어서 실제로 삭제가 동작안하는 문제가 발생하는데 이 때 `cascade = CascadeType.REMOVE)` 옵션을 주면 외래키가 있어도 삭제가 완료됩니다.

<br/>
<br/>
<br/>

```java
@Entity
public class Reply extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String content;

    @ManyToOne
    @JoinColumn(name = "boardId")
    private Board board;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

}
```

- 누가 댓글을 작성했는지와 어느 게시글에 작성했는지 알기 위해 FK 설정을 합니다. (boardId, userId )

<br/>
<br/>
<br/>

### 자신만 수정, 삭제 가능하도록 설정

- implementation 'org.thymeleaf.extras:thymeleaf-extras-springsecurity5' 의존성을 다운받으면
	- th:value="${#authentication.principal.email}" 이런식으로 사용자 정보를 뷰에 뿌려줄 수 있습니다.
- 본인이 작성한 게시글 혹은 댓글에 대한 수정, 삭제만 가능하도록 설정하였습니다.

```html
게시글 수정, 삭제 (DB에 있는 유저 id와 로그인한 유저 id 비교)
<span th:if="${board.user.id == #authentication.principal.id}">
    <a th:href="@{/board/{id}/update(id=${board.id})}" class="btn btn-warning" id="btn-update">수정</a>
    <button class="btn btn-danger" id="btn-delete">삭제</button>
</span>
```

```html
댓글 삭제 (DB에 있는 유저 id와 로그인한 유저 id 비교)
<span th:if="${reply.user.id == #authentication.principal.id}">
    <button th:onclick="|replyIndex.replyDelete('${board.id}', '${reply.id}')|" class="badge btn-danger" style="margin-left: 10px;">삭제</button>
</span>
```

<br/>
<br/>
<br/>

### 로그인

- 로그인은 Spring Security를 이용하여 시큐리티가 대신 로그인을 수행할 수 있도록 하고 유저 정보는 `UserDetails 인터페이스`를 상속받아 구현하였습니다.
	- `user-login.html`의 form 태그에 `action`과 `method`에 post로 설정합니다.
	- input 태그에는 `name` 값을 줍니다.
- 실제 로그인한 유저는 `UserDetails`를 상속받은 `PrincipalDetail` 클래스에 사용자 정보가 담겨 있고, 사용자 정보를 가져오는 `UserDetailsService 인터페이스`를 상속받은 `PrincipalDetailService` 클래스를 생성하였습니다.
- 회원 수정의 경우 변경 완료시킨 후 다시 회원 수정으로 들어갔을 때 바껴야하므로 이 때 스프링 시큐리티 세션을 이용하여 반영하였습니다.
	- @AuthenticationPrincipal에서 회원정보를 파라미터로 받고 회원수정 로직에서 setter를 통해 변경시켰습니다.

```java

//변경 - 더티체킹
@Transactional
public Long update(User user, @AuthenticationPrincipal PrincipalDetail principalDetail) {
    User userEntity = userRepository.findById(user.getId()).orElseThrow(() -> new IllegalArgumentException("해당 회원이 없습니다. id=" + user.getId()));
    userEntity.update(bCryptPasswordEncoder.encode(user.getPassword()), user.getNickname());
    principalDetail.setUser(userEntity); //시큐리티 세션 정보 변경
    return userEntity.getId();
}
```

<br/>
<br/>
<br/>

### 소셜로그인

- 소셜로그인은 `oauth2-client` 라이브러리를 이용하여 구현하였습니다.
- 소셜로그인을 할 때도 `UserDatils`를 구현한 `PrincipalDetail`의 사용자 정보를 가져오기 위해 `OAuth2User 인터페이스`를 상속받도록 합니다.
	- `DefaultOAuth2UserService 클래스`를 상속받은 `PrincipalOauth2UserService 클래스`가 `PrincipalDetail`를 반환해서 소셜로그인을 한 사람도 사용자 정보를 받을 수 있도록 합니다.
- `OAuth2UserInfo 인터페이스`를 만들어서 각각 구글, 페이스북, 네이버의 `attributes` 값을 받았습니다.

```java
if (userRequest.getClientRegistration().getRegistrationId().equals("google")) {
    oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
} else if (userRequest.getClientRegistration().getRegistrationId().equals("facebook")) {
    oAuth2UserInfo = new FacebookUserInfo(oAuth2User.getAttributes());
} else if (userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
    oAuth2UserInfo = new NaverUserInfo((Map) oAuth2User.getAttributes().get("response"));
}
```

<br/>

- 자동회원가입 로직

```java
if (userOptional.isPresent()) { //이미 소셜로그인이 되어있는 유저라면 email을 update해줍니다.
    user = userOptional.get();
    user.setEmail(oAuth2UserInfo.getEmail());
    userRepository.save(user);
} else { //소셜로그인 정보가 없는 유저라면 회원가입을 자동으로 시켜줍니다.
    user = User.builder()
            .username(oAuth2UserInfo.getProvider() + "_" + oAuth2UserInfo.getProviderId())
            .password(UUID.randomUUID().toString())
            .email(oAuth2UserInfo.getEmail())
            .nickname("소셜로그인")
            .role(Role.USER)
            .provider(oAuth2UserInfo.getProvider())
            .providerId(oAuth2UserInfo.getProviderId())
            .build();

    userRepository.save(user);
}
```

<br/>
<br/>
<br/>

### 페이징/검색

- 페이징은 `Pageable 인터페이스`를 이용하여 구현하였습니다.

```java
@RequiredArgsConstructor
@Controller
public class IndexController {

    private final BoardService boardService;

    @GetMapping("/")
    public String index(Model model,
                        @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                        @RequestParam(required = false, defaultValue = "") String search) {
        Page<Board> boards = boardService.findByTitleContainingOrContentContaining(search, search, pageable);
        int startPage = Math.max(1, boards.getPageable().getPageNumber() - 4);
        int endPage = Math.min(boards.getTotalPages(), boards.getPageable().getPageNumber() + 4);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("boards", boards);
        return "index";
    }
}
```

- @PageableDefault : 페이지의 사이즈, 정렬을 설정하기 위해 사용하였습니다.
- 검색같은 경우는 주소에 쿼리스트링으로 받아서 처리하는데 @RequestParam를 사용하였고, null값이 들어갈 수도 있으므로 속성값을 설정하였습니다.
- findByTitleContainingOrContentContaining : JPA에서 Containing을 사용하게 되면 `LIKE문`처럼 동작하게 됩니다.
- 페이지 목록에서 시작 페이지 번호(startPage)와 끝 페이지 번호(endPage)를 선언하여 모델에다가 넘겨주었습니다.
- thymeleaf에서 페이징 html은 아래와 같이 구현하였습니다.

```html
<nav aria-label="Page navigation example">
    <ul class="pagination">
        <li class="page-item" th:classappend="${1 == boards.pageable.pageNumber + 1} ? 'disabled' : '' ">
            <a class="page-link" th:href="@{/(page=${boards.pageable.pageNumber - 1}, search=${param.search})}">Previous</a>
        </li>
        <li class="page-item" th:classappend="${i == boards.pageable.pageNumber + 1} ? 'active' : '' " th:each="i : ${#numbers.sequence(startPage, endPage)}">
            <a class="page-link" th:href="@{/(page=${i - 1}, search=${param.search})}" th:text="${i}">1</a>
        </li>
        <li class="page-item" th:classappend="${boards.totalPages == boards.pageable.pageNumber + 1} ? 'disabled' : '' ">
            <a class="page-link" th:href="@{/(page=${boards.pageable.pageNumber + 1}, search=${param.search})}">Next</a>
        </li>
    </ul>
</nav>
```

- th:classappend를 이용하여 조건을 달아서 조건에 맞으면 클래스에 추가되도록 합니다.
- ${#numbers.sequence(startPage, endPage)}"을 이용하면 startPage부터 endPage까지 숫자범위를 설정합니다.
- 다른 페이지로 이동하게 되면 검색한 것이 초기화가 되는데 이 때 검색 파라미터를 페이지 이동할 때 까지 들고 갈 수 있도록 `th:href` url에 search 쿼리 파라미터를 추가하였습니다.
- 아래 코드는 검색 html입니다.

```html
<form class="d-flex" style="position: relative; top: 40px;" method="get" th:action="@{/}">
    <input class="form-control me-2" type="search" placeholder="Search" aria-label="Search"
           id="search" name="search" th:value="${param.search}">
    <button class="btn btn-outline-success" type="submit">Search</button>
</form>
```

- ${param.search} : param으로 접근하면 쿼리스트링의 주소를 받을 수 있도록 합니다.
