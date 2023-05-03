# Intro

**`Spring + Spring Boot + JPA` 를 활용한 개발 규칙**

* 현재 공부한 범위에 최선의 개발 방향을 기록
* 요구사항 분석부터 Spring 코드 구현 관련 내용까지
* **단축키**
  * `Alt + Insert` : getter, setter, constructor 등 자동 생성
  * `Ctrl + Alt + V` : 변수 선언부를 자동 작성
  * `Ctrl + Alt + M` : 코드 리팩토링하기 쉽게끔 함수 자동 생성
  * `Ctrl+T->extra method` : 코드 리팩토링하기 쉽게끔 드래그한 코드를 하나의 함수로 자동 생성
  * `Alt + Shift + Down/Up` : 코드 한줄을 위, 아래 자리 이동 가능
  * `Ctrl + D` : 코드 한줄 바로 아래에 복제
  * `Ctrl + Alt + Shift` : 멀티 커서 가능
  * `Shift + F6` : 변수명을 한번에 바꿀 때 사용
  * `Alt + 1 ` : 왼쪽 프로젝트 폴더 구조 열기
  * `Alt + F12` : 터미널 창 열기

<br><br>

# 체크리스트

**개발과정**

* 요구사항 분석(대략적 기능)
* 기능 목록(상세한 기능)
* 설계 시작
  * 도메인 모델 분석(간략히)
  * 테이블 설계(DB)
  * 엔티티 설계(JPA)
* 코드 구현 (각 파트별 TDD도 함께)
  * 도메인 구현 -> 엔티티를 의미하며, 모든 계층에서 사용
  * 레퍼지토리 구현 -> DB와 상호작용
  * 서비스 구현 -> 비지니스 로직 & 트랜잭션
    * `도메인 모델 패턴` : 서비스 계층은 단순히 엔티티에 필요한 요청을 위임하는 방식
    * `트랜잭션 스크립트 패턴` : 엔티티에는 비지니스 로직이 거의 없고 서비스 계층이 담당하는 방식
    * **참고로 `도메인 모델 패턴` 방식으로 진행 중**

  * 컨트롤러 구현 -> 웹 계층과 상호작용


<br>

**네이밍**

- **Database**
  - 테이블명 형식으로 `ORDER 또는 order` 사용 **=> 대문자 or 소문자**
  - 컬럼명 형식으로 `order_id` 사용 **=> 스네이크 케이스**
- **JPA -> ORM(객체 관계 매핑)**
  - 엔티티명 형식으로 `OrderItem` 사용 **=> 파스칼 케이스**
  - 필드명 형식으로 `orderId` 사용 **=> 카멜 케이스**  
    - **스프링 부트는 자동으로 필드명을 `orderId -> order_id` 로 컬럼명 찾아서 매핑**
    - **단, 엔티티명은 직접 테이블명과 매핑**

<br>

**테이블 설계 **

* **데이터(테이블) 중심** 설계
  * 보통 **PK**인 컬럼명을 **`테이블명_id`** 형태로 쓰지만, 나머지 컬럼명들은 `테이블명` 을 안쓰는 편

* **N:N** 관계는 **1:N, N:1** 로 풀기
  * N:N은 무한루프 때문 => 그림참고 : https://siyoon210.tistory.com/26

* **외래키가 있어야할 위치**
  *  **1:N, N:1** 의 경우 **N**에 사용
    * 성능 저하 때문(많은 JOIN문 발생) => 그림참고 : https://siyoon210.tistory.com/26

  * **1:1**의 경우 **상황에 따라** 사용 - 보통은 주 테이블에 외래키 사용
    * 주 테이블 외래키 단방향 - 단점 : 값 없으면 외래 키에 null 허용
    * 대상 테이블에 외래키 양방향 - 단점 : 무조건 즉시로딩

* **상속**의 경우 JOINED, SINGLE_TABLE 전략 중에서 **JOINED 전략**을 많이 사용
  * 부모 자식간에 join을 하므로 똑같이 테이블 생성

<br>

**엔티티 설계**

* **객체 중심 설계**
  * 보통 **PK**인 필드명을 바로 **`id`** 로 쓰고 **직접** 테이블의 컬럼명과 **매핑**을 함
  * **Getter는 오픈하되 Setter의 경우 필요한 경우에만 사용하길 권장**
    * 따라서 Setter의 경우 **비즈니스 메서드**를 별도로 만들 필요가 있음
    * **즉, 개발과정에선 Getter, Setter를 열어두고 나중에 리펙토링으로 Setter 들은 제거하길 바람**

* 엔티티 설계 때 **연관관계는 단방향 우선 개발**(테스트)후 양방향 추가
  * 양방향은 코드만으로 해결 가능해서 DB 설계에 아무런 영향을 끼치지 않음


- 즉시 or 지연 로딩 중에서 무조건 **"지연 로딩"** 으로 개발 => **N+1 문제** 때문

  - 코드상에서 `@XToOne` 은 기본이 **즉시 로딩**이므로 반드시 **지연로딩**으로 전부 변경

- 옵션 중에서 **cascade** 사용 유무는 관계가 **완전 종속일때만** 사용 (연관된 데이터 연쇄적 변경 효과)

  - cascade는 영속성 전이를 하므로, 연관관계 매핑과는 전혀 관계 없음 
  - 단지 이를 사용하면 생명주기를 같이 하는것

- **추가정보**

  - **ENUM** 데이터 사용 시 `@Enumerated(EnumType.STRING)` 로 꼭 `STRING` 으로 옵션

  - **중복 코드를 줄이는 효과적인 방법들**

    - **`임베디드 타입(값 타입)` 과 `상속-Mapped Superclass`** 이것 두개를 잘 활용 - 중복 코드를 많이 줄임

      - `값 타입` 은 조금 적은 중복 때,`상속` 부분은 거희 모든 엔티티에 속하는 중복 때 사용하면 괜찮겠다고 판단된다

    - **설계할때 부터 "값 타입"으로 활용될거는 따로 빼서 설계**

      - `값 타입` 은 엔티티 클래스에 `private Long id;` 와 같은 필드라고 생각
      - 값 타입은 정말 값 타입이라 판단될 때만 사용 (엔티티와 혼동이되면 안되므로)
      - 식별자가 필요하면 "엔티티"를 의미하며, "임베디드 객체" 같은건 값 비교할때 equals 메소드 반드시 오버라이드 필요

      - "임베디드 객체" 는 값 타입 하나. `@Embed...` 로
      - "값 타입 컬렉션" 은 값 타입 하나 이상. 이건 그냥 `"일대다 고아+cascade" 엔티티` 로

  - **컬렉션(List같은,,,)은 필드에서 초기화 하자**

    - 코드간결, null 문제에서 안전

  - **의존성 주입(DI)은 스프링 필드 주입대신에 생성자 주입을 사용하자.**

    - 해석하자면 DI 중 @Autowired를 이용한 Field Injection보다는 **@RequiredArgsConstructor와 final**을 이용한 Constructor Injection을 사용하자라는 의미

  - **연관관계 메서드 사용 권장(양방향에 적극권장!!)**
  
    - **양방향**의 관계에 주로 사용되는 형태(아래는 예시)
  
      - (가정) Order와 OrderItem는 1:N이고 외래키 가진 주인은 OrderItem 인 상태이다.
      - (1) 이 상황에서 주인인 OrderItem에서는 Order를 바로 접근 가능
      - (2) Order가 OrderItem를 접근하는건 **양방향**이고, 조금 돌아가서 변경 가능
      - **(3) Member->Order를 Order->Member 접근처럼 간단히 사용하기 위해 연관관계 메서드를 사용**
  
      ```java
      // (1) OrderItem->Order 접근 예시 => 단반향
      orderItem.getOrder(); // orderItem에서 order정보 접근 모습
      
      // (2) Order->OrderItem 접근 예시 => 반대방향 (양방향)
      order.getOrderItems().add(orderItem); // order에 orderItem추가 목적
      orderItem.setOrder(order); // 두줄 필요
      
      // (3) 연관관계 편의 메서드
      // 즉, Order->OrderItem 접근(양방향)을 편의 메서드 만들어서 활용!
      // Order 클래스 내부
      public void addOrderItem(OrderItem orderItem) {
          orderItems.add(orderItem);
          orderItem.setOrder(this);
      }
      // main 함수 내부
      public void main~~(){
      	order.addOrderItem(orderItem); // 한줄로 order에 orderItem정보 추가
      }
      ```
      
  
  - **비지니스 로직 사용 권장**
  
    - Service 파트가 아닌 Entity파트에서 비지니스 로직 구현이 가능할 것 같은 경우에는 Entity에서 개발을 적극 권장 => **장점 : 좀 더 객체 지향적인 코드**
  
      ```java
      // 간단히 엔티티에서 구현 가능하니까 서비스가 아닌 엔티티에서 로직 구현하는 것(객체지향적)
      // 재고 추가 함수 (비지니스 로직)
      public void addStock(int quantity) {
          this.stockQuantity += quantity;
      }
      ```
  
  - **생성 메서드 사용 권장**
  
    - 목적 : 엔티티에있는 다양한 속성들을 이 생성메서드 하나로 간편히 다 적용 & 무분별한 엔티티 생성을 막기 위함
  
    - **`@NoArgsConstructor(access = AccessLevel.PROTECTED)` 를 사용!**
  
      - 기본 생성자의 접근 제어를 PROTECTED로 설정해놓게 되면 **무분별한 객체 생성에 ide 상에서 한번 더 체크할 수 있는 수단**
  
      ```java
      // 기본생성자 Public->Protected
      @NoArgsConstructor(access = AccessLevel.PROTECTED) 
      public class Order {
      	// ...
          
          //==생성 메서드==// => 수많은 정보들 저장하는걸 한번에 이 메서드에서 해결
              public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
              Order order = new Order();
              order.setMember(member);
              order.setDelivery(delivery);
              for(OrderItem orderItem : orderItems) {
                  order.addOrderItem(orderItem);
              }
              order.setStatus(OrderStatus.ORDER);
              order.setOrderDate(LocalDateTime.now());
              return order;
          }
      }
      // main 함수 내부
      Order order1 = Order.createOrder(member, delivery, orderItems);
      order1.setDelivery(delivery2); // 정상
      
      Order order2 = new Order(); 
      order2.setDelivery(delivery2); // ide상에서 에러 발생
      ```
  
  - **조회 로직 사용 권장**
  
    - 조회 로직의 경우에도 간단하므로 엔티티에서 개발을 권장
  
      ```java
      //==조회 로직==//
      /** 주문상품 전체 가격 조회 */
      public int getTotalPrice() {
          return getOrderPrice() * getCount(); // 가격 * 수량 = 주문상품 가격
      }
      ```

<br>

**엔티티 매핑**

* 연관관계에서 권장하는것은??

  * **N:1,1:N** 의 경우 "일대다 관계" 보다는 **"다대일 단(or양)방향"** 권장
    * **"다"** 쪽이 주인이 되는건 객체 지향보단 테이블 지향이 강하지만 그래도 이것을 권장
  * **1:1** 의 경우 **"주 테이블 외래키 단방향"** 권장
    * 주 테이블 외래키 단방향 - 단점 : 값 없으면 외래 키에 null 허용
    * 대상 테이블에 외래키 양방향 - 단점 : 무조건 즉시로딩

* **"다대다" 사용금지** (이미 앞에서 언급 했었음) => **"일대다", "다대일"**로 풀어서 사용

  * 중간 테이블도 "엔티티"로 만드는걸 권장

* **상속 매핑**은 **`일반적인 전략, Mapped Superclass`** 을 사용
  * **일반적인 전략의 경우** JOINED, SINGLE_TABLE 방식이 유명한데 보통 **JOINED 방식을** 선호
    * 혹시나 테이블이 너무 단순하다면 SINGLE_TABLE 을 사용
  
  * **Mapped Superclass 전략의 경우**
    * 상속 매핑으로 선언된 클래스를 상속받게 되면 해당 상속 내용을 전부 테이블에 넣을 수 있다.

<br>

**엔티티 조회 권장 순서**

1. **엔티티 조회** 방식으로 우선접근
   1. 페치조인으로 쿼리 수를 최적화
   2. 컬렉션 최적화
      1. 페이징 필요O `hibernate.default_batch_fetch_size` , `@BatchSize` 로 최적화
      2. 페이징 필요X => 페치조인사용
2. 엔티티 조회 방식으로 해결이 안되면 DTO 조회 방식 사용
3. DTO 조회 방식으로 해결이 안되면 NativeSQL or 스프링 JdbcTemplate 활용

<br>

**엔티티 조회 에러 해결(참고)**

* **엔티티를 외부에 노출하면서 발생하는 문제들이라서 사실상 이부분을 알필요는 없다.**

* **@JsonIgnore : 양방향 무한 반복의 문제를 해결**
  * **하지만 지양하고 DTO 방식으로 해결을 지향**

* **Hibernate5Module을 @Bean 등록 까지 해주면 Lazy 문제도 해결** 
  * **하지만 이 또한 지양하고 LAZY 강제 초기화로 다 해결**

<br>

**추가정보**

* 무조건 **지연로딩**을 사용하라고 했는데, **즉시로딩을 사용하고 싶을땐??**
  * **fetch join을 활용**

* **동적 쿼리**는 **Querydsl** 을 권장

* **API에 제공할때 절대로 엔티티를 바로 나타내지 말것**

  * **API 응답 스펙에 맞추어 별도의 DTO를 반환 권장**

* **준영속 엔티티를 수정할 때 Merge방식보다는 Dirty Checking 방법 권장**

  * 변경 감지 기능은 Flush 할때 발생

  * 따라서 **영속성 컨텍스트를 사용하는 로직으로 작성해서 더티 체킹이 발생하도록 하자**

    ```java
    // entity part
    // 준속성 엔티티 -> 영속성 엔티티 에 사용
    public void change(String name, int price, int stockQuantity) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }
    
    // service part
    @Transactional
    public void updateItem(Long id, UpdateItemDto itemDto){
        Item item = itemRepository.findOne(id); // 영속성 엔티티
        item.change(itemDto.getName(), itemDto.getPrice(), itemDto.getStockQuantity()); // 준속성 엔티티(itemDto) -> 영속성 엔티티
    }
    
    // controller part
    public String updateItem(@PathVariable Long itemId, @ModelAttribute("form") BookForm form){
        UpdateItemDto itemDto = new UpdateItemDto(form.getName(), form.getPrice(), form.getStockQuantity());
        itemService.updateItem(itemId, itemDto);
        return "redirect:/items"; // 위에서 "상품 목록" 매핑한 부분으로 이동
    ```

* **setter를 최대한 사용하지 않게끔 DTO 방식 권장**

  * 장점 : 엔티티 계층에서 setter를 사용하지 않게끔 하는 효과와 파라미터 많은걸 줄여주는 효과

  ```java
  // DTO 형태
  @Getter @Setter
  @RequiredArgsConstructor // 생성자
  public class UpdateItemDto {
      private final String name;
      private final int price;
      private final int stockQuantity;
  }
  ```

* **JSON 반환시 꼭 마지막에 객체로 감싸서 반환해줄것**

<br>

**스프링 부트의 다양한 어노테이션**

* **엔티티**
  * @Entity : 스프링빈으로 등록
  * @Inheritance(strategy = InheritanceType.SINGLE_TABLE) : 상속을 SINGLE_TABLE 전략 사용선언
    * @DiscriminatorColumn(name = "dtype") : 상속의 부모 구분
    * @DiscriminatorValue("A") : 상속의 자식 구분
  * @Getter @Setter : getter, setter 제공
  * @Id @GeneratedValue : PK 할때 세트로 활용
  * @Table(...) @Column(name = "member_id") : 테이블명, 컬럼명 매핑
  * @ManyToOne(fetch=LAZY) : 지연로딩(LAZY), 다대일 관계 표시
    * @OneToMany(mappedBy = "member") : 주인은 Order라서 Order의 member표시
    * @JoinColumn(name = "member_id") : 외래키 사용 - 주인이 할 일
    * @JsonIgnore : 양방향 무한 반복의 문제를 해결
      * **하지만 지양하고 DTO 방식으로 해결하는걸 지향한다.**
  * @Embedded : JPA 내장 타입 쓸 때 사용
    * @Embeddable : JPA 내장 타입 선언

* **리포지토리**

  * @Repository : 스프링빈으로 등록, JPA 예외를 스프링 기반예외로 예외 변환
  * @PersistenceContext : 엔티티메니저( EntityManager ) 주입
  * @PersistenceUnit : 엔티티메니터팩토리( EntityManagerFactory ) 주입
  * 보통 만드는 DB와 연관 메서드 : save(), findOne(), findAll(), findByName() 등등...

* **서비스**

  * @Service : 스프링빈으로 등록
  * @Transactional : 트랜잭션, 영속성 컨텍스트
    - readOnly=true : 데이터의 변경이 없는 읽기 전용 메서드에 사용, 영속성 컨텍스트를 플러시 하지 않으므로 약간의 성능 향상(읽기 전용에는 다 적용)
    - 데이터베이스 드라이버가 지원하면 DB에서 성능향상
  * @Autowired
    - **앞전에 언급한것처럼 `@Autowired` 대신에 `@RequiredArgsConstructor` 와 `final ` 를 활용**

  - 보통 만드는 비지니스 로직 메서드 : join(), findMembers(), findOne() 등등...

* **컨트롤러**

  * @Controller : 스프링빈으로 등록, 핸들러 매핑 대상으로 기억

  * @RequestMapping, @GetMapping, @PostMapping 등등..  : HTTP 매핑

    * @RequestMapping을 클래스단에 @GetMapping, @PostMapping 을 메서드단에 사용 권장

  * @RestController : 뷰로 반환하는게 아닌 HTTP Body에 반환값 기입반환 **(API 만들시 적극 권장)**

    * @RestController는 @Contorller, **@ResponseBody** 등등을 포함하는 어노테이션

  * @PathVariable("userId"), @RequestParam => @PathVariable 방식 권장

    - @RequestParam : 기존 url 쿼리 파라미터 방식 : ?userId=userA
    - @PathVariable("itemId") : 최신 트랜드인 경로 변수 방식 : /mapping/userA

  * @ModelAttribute("from")

    * model.addAttribute 에도 담기고, form서밋 때 html에 있는 form 데이터를 매핑해서 변수에도 자동으로 담아줘서 변수선언도 따로 할 필요 없음

      ```java
      public String modelAttributeV1(@ModelAttribute HelloData helloData) {
          log.info(helloData.getUsername()); // 바로 변수 사용 가능!!
      }
      ```

  * @RequestBody, @ResponseBody : HttpEntity 처럼 HTTP 메시지 **컨버터**가 HTTP 메시지 바디의 내용을 우리가 원하는 문자나 객체 등으로 자동 변환!!

    * 요청파라미터 @RequestParam, @modelAttribute 랑은 전혀 관계없으니까 혼동 X

    * 요청오는건 RequestBody, 응답으론 ResponseBody

      ```java
      @ResponseBody
      @PostMapping("/request-body-json-v5")
      public HelloData requestBodyJsonV5(@RequestBody HelloData data) {
          log.info(data.getUsername());
          return data;
      }
      ```

      * 요청으로 들어오는 json 데이터를 @RequestBody HelloData data 로 인해 Hellodata 객체로 바꾸고,
      * 반환 타입을 String이 아닌 HelloData로 하면 @ResponseBody 로 인해 return할때 응답body에 문자로 넣어준다 했는데 덕분에 json로 집어넣어준다.
      * 즉, json(요청)->객체->json(응답) 로 동작한다.

  * 뷰 반환, 데이터 반환 정리

    * @Controller - View 반환
    * @Controller + @ResponseBody - Data 반환
    * @RestController - Data 반환

* **TDD**

  * @Test : 기본 테스트이고 @SpringBootTest는 스프링 통합 테스트
  * **`given, when, then`**
    * given에 멤버 이름 설정
    * when에 서비스의 join함수 사용(회원가입 되는지 확인하는 것)
    * then에 결과를 보는것. 멤버이름이 잘 생겼는지 등등..(assert보통 씀!)
  * @RunWith(SpringRunner.class) : 스프링과 테스트 통합
  * @SpringBootTest :  스프링 컨테이너와 테스트를 함께 실행 (이게 없으면 @Autowired 다 실패)

  * @Transactional : 반복가능한 테스트지원, 각각의 테스트를 실행할 때 마다 트랜잭션을 시작하고 테스트가 끝나면 트랜잭션을 강제로 롤백 (이 어노테이션은 테스트 케이스에서 사용될때만 기본값으로 롤백)
    * 롤백을 하기때문에 내부에서 굳이 영속성 컨텍스트 플러시를 안하는 특징을 가짐
    * @Rollback(false) : 롤백 취소
      * 롤백을 안하니까 flush까지 진행하므로 insert문 로그 확인가능
    * em.flush() 함수 사용 : flush 진행
      * 롤백은 건드리지않고 flush 진행하므로, 롤백은 그대로 진행하면서 insert문 로그 확인가능
  * `@AfterEach` 는 테스트 끝나면 실행되는 것인데, 이를 활용한다.
  * `@BeforeEach` 는 테스트 시작전 실행되는 것인데, 이것도 활용할 수 있다.
  * print대신 assert비교
    * `Assertion`을 이용하자. 이걸 사용해서 `assertEquals`함수 사용시 두개 인자가 동일한지 봐준다
      * 안동일하면 오류, 동일하면 아무일도 없음
      * `Assertions.assertThat(member).isEqualeTo(result);` 이것도 위처럼 사용된다.
  * 예외 테스트
    * try, catch보다 간편하게 `assertThrows`를 사용해서 일부러 예외를 터트려서 테스트 하는것이 있다.

* **ETC**
  * @Slf4j : 로그를 바로 log로 사용 가능
  * @NotEmpty("에러 메세지 관련")
  * @Data -> 롬복
    * @Getter, @Setter, @ToString, @EqualsAndHashCode, @RequiredArgsConstructor 적용
  * @SpringBootApplication : 톰캣 내장(서버)
