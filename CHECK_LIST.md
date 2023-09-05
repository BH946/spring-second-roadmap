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

* **요구사항 분석(대략적 기능)**
* **구체적인 요구사항 목록(상세한 기능)**
* **설계 시작**
  * 도메인 모델 분석(간략히)
  * 테이블 설계(DB)
  * 엔티티 설계(JPA)
  * **ERDCloud 툴을 사용한 설계 => 테이블, 엔티티 설계를 여기서 한번에**
* **코드 구현 (각 파트별 TDD도 함께)**
  * 도메인 구현 -> 엔티티를 의미하며, 모든 계층에서 사용
  * 레퍼지토리 구현 -> DB와 상호작용
  * 서비스 구현 -> 비지니스 로직 & 트랜잭션
    * `도메인 모델 패턴` : 서비스 계층은 단순히 엔티티에 필요한 요청을 위임하는 방식
    * `트랜잭션 스크립트 패턴` : 엔티티에는 비지니스 로직이 거의 없고 서비스 계층이 담당하는 방식
    * **참고로 `도메인 모델 패턴` 방식으로 진행 중**

  * 컨트롤러 구현 -> 웹 계층과 상호작용 (API 포함)

<br>

**네이밍**

- **Database**
  - 테이블명 형식으로 `ORDER 또는 order` 사용 **=> 대문자 or 소문자**
  - 컬럼명 형식으로 `order_id` 사용 **=> 스네이크 케이스**
  - 스프링에선 테이블 매핑 마지막에 전부 **"대문자"**로 바꿔주는것 같음
    - DB보니까 대문자로 전부 네이밍 되어있길래,,
- **JPA -> ORM(객체 관계 매핑)**
  - 엔티티명 형식으로 `OrderItem` 사용 **=> 파스칼 케이스**
  - 필드명 형식으로 `orderId` 사용 **=> 카멜 케이스**  
    - **스프링 부트는 자동으로 필드명을 `orderId -> order_id` 로 컬럼명 찾아서 매핑**
    - **엔티티명은 `OrderItem -> ORDERITEM` 처럼 "대문자"로 바꿔주는건 여전하고, 다른 규칙은 없으므로 필요시 직접 테이블명과 매핑**

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
  * 개발과정에선 **Getter, Setter**를 열어두고 나중에 리펙토링으로 **Setter** 들은 제거
    * 엔티티에서의 **비지니스 메서드** 구현은 **Setter** 제거 효과
    * **setter를 최대한 사용하지 않게끔 DTO 방식 권장**
* 엔티티 설계 때 **연관관계는 단방향 우선 개발(테스트)** 후 양방향 관계 추가
  * 양방향은 코드만으로 해결 가능해서 DB 설계에 아무런 영향을 끼치지 않음


- 즉시 or 지연 로딩 중에서 무조건 **"지연 로딩"** 으로 개발 => **N+1 문제** 때문

  - 코드상에서 `@XToOne` 은 기본이 **즉시 로딩**이므로 반드시 **지연로딩**으로 전부 변경

- 옵션 중에서 **cascade** 사용 유무는 관계가 **완전 종속일때만** 사용 (연관된 데이터 연쇄적 변경 효과)

  - cascade는 영속성 전이를 하므로, 연관관계 매핑과는 전혀 관계 없음 
  - 단지 이를 사용하면 **생명주기를 같이** 하는것

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
      
  
  - **비지니스 로직(메서드) 사용 권장**
  
    - Service 파트가 아닌 Entity파트에서 비지니스 로직 구현이 가능할 것 같은 경우에는 Entity에서 개발을 적극 권장 => **장점 : 좀 더 객체 지향적인 코드**
  
      ```java
      // 간단히 엔티티에서 구현 가능하니까 서비스가 아닌 엔티티에서 로직 구현하는 것(객체지향적)
      // 재고 추가 함수 (비지니스 로직)
      public void addStock(int quantity) {
          this.stockQuantity += quantity;
      }
      // 업데이트의 경우에도 충분히 가능
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
* **"양방향" 연관관계 코드로 작성시 TIP**
  * 개인적인 생각일 뿐이다. 
  * **연관관계 편의 메서드와 mappedBy 를 세트로 항상 같이 작성**

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
  * 예로 전체 엔티티를 LAZY로 개발한 후 Order를 조회할 때?? 단, Order 엔 Member가 있음.
    * 이때, LAZY 강제 초기화를 안하면 Member를 null로 나타낸다는게 LAZY 문제라는것.
      * **해결방안**으로 **`order.getMember().getName()` 같은 코드를 추가해야 LAZY 강제 초기화**를 진행
      * **이 경우 Member를 select하는 쿼리를 추가로 전송하는 행동을 해준다.**
      * **단, 이 경우엔 `fetch join` (꼭 `distinct`도 사용 권장) 을 꼭 함께 사용해줘야 추가 전송하는 쿼리를 막고 1개의 쿼리만으로 모든걸 해결 할 수 있다. => 매우중요!!**
        * **TIP : `ToOne` 관계는 모두 페치조인, 컬렉션 엔티티 조회만 그대로 지연 로딩으로 조회한다.**

<br>

**JPQL 추가정보**

* **반환**

  * TypeQuery: 반환 타입이 명확할 때 사용
  * Query: 반환 타입이 명확하지 않을 때 사용
  * **Dto -> QueryDSL 사용시 패키지 명까지 없앨 수 있음 (우선 이걸로 자주 사용하자)**
    * `List<MemberDTO> result = em.createQuery("select new jpql.MemberDTO(m.username, m.age) from Member m", MemberDTO.class)`
  * **query.getResultList(): 결과가 하나 이상일 때, 리스트 반환**
  * query.getSingleResult(): 결과가 정확히 하나, 단일 객체 반환

* **쿼리문도 조건식 존재(case...then...else...end)**

  * `select case when m.age <= 10 then '학생요금' when m.age >= 60 then '경로요금'  else '일반요금' end from Member m`

* **페치징 API**

  * setFirstResult(int startPosition) : 조회 시작 위치 (0부터 시작)
  * setMaxResults(int maxResult) : 조회할 데이터 수

* **fetch join : 객체 그래프 유지할 때 사용시 효과적**

  * **여러 테이블을 조인해서 엔티티가 가진 모양이 아닌 전혀 다른 결과를 내야 하면, 페치 조인 보다는 일반 조인을 사용하고 필요한 데이터들만 조회해서 DTO로 반환하는 것이 효과적**
  * **T.\*, M.* 로 두 테이블 모두 조회 ("즉시 로딩")**
    * **`XToOne` 문제없음, `XToMany` 는 Distinct 함께 사용**
  * **페이징 API - `XToOne`는 바로 페치조인O, `XToMany`는 일반 select + BatchSize (페치조인X)**
    * **글로벌로 100정도 깔아두고 개발하자**
  * 페치 조인 대상에는 별칭X - 유일하게 연속으로 join 가져오는 경우에만 사용
  * 둘 이상의 컬렉션은 페치 조인X

* **경로 표현식 3가지 - 상태, 연관 필드(단일, 컬렉션)**

  * **일반 join 사용시 "묵시적 내부 조인" 이 아닌 "명시적 조인" 을 사용할 것**

    ```java
    select m.username -> 상태 필드 
      from Member m 
        join m.team t    -> 단일 값 연관 필드 
        join m.orders o -> 컬렉션 값 연관 필드 
    where t.name = '팀A'
    ```

* **엔티티 직접 사용**

  * **JPQL에서 엔티티를 직접 사용하면 SQL에서 해당 엔티티의 "기본 키" 값을 사용**
  * (JPQL) : select count**(m)** from Member m    
  * (SQL) : select count**(m.id)** as cnt from Member m

* **Named 쿼리**

  * **(참고) 실무에서는 Spring Data JPA 를 사용하는데 @Query("select...") 문법이 바로 "Named 쿼리"**
    * 지금은 쓰지말고, Spring Data JPA 를 공부하고 나면 ㄱㄱ

* **벌크 연산 - 여러 데이터 한번에 "수정, 삭제" 연산**

  * JPA 는 보통 실시간 연산에 치우쳐저 있는데, 대표적인 예가 "더티 체킹"
    * 100개 데이터가 변경되었으면 100개의 Update 쿼리가 나가게 되는 문제
    * **이런건 "벌크 연산" 으로 해결하자**
  * **올바른 사용법**
    * **벌크 연산을 먼저 실행**
    * **벌크 연산 수행 후 영속성 컨텍스트 초기화**

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

* **DTO와 DI(의존성 주입) 구분할 것**

  * **스프링에서 DI(Dependency Injection)**는 객체를 new로 직접 생성하는것이 아닌 **스프링 컨테이너(외부)에서 객체를 생성해서 주는 방식**을 의미
  * **DTO(Data Transfer Object)**는 **계층 간 데이터 교환을 하기 위해 사용하는 객체**로, 로직을 가지지 않는 순수한 데이터 객체
    * 메소드는 주로 getter, setter만 가짐

* **"캐시메모리" 사용으로 최적화 -> 서비스 계층에서 사용(트랜잭션 쪽)**

  * **[잘 정리한 사이트](https://adjh54.tistory.com/m/165) -> 사용법 다양함**
  * **게시물 삭제, 수정, 추가에 @CachePut사용, 조회에 @Cacheable 사용함으로써 간단한 최적화 가능**
  * **왜?? 사용하나??** 
    * CSR같은경우 서버에서 API로직으로 JSON같은 데이터 넘겨주면 Client에서 React였으면 Redux, React-Native였으면 AsyncStorage 등등으로 기록해서 사용하므로 "데이터존재" 하면 API호출을 따로 하지않을 것
    * 그러나, SSR같이 서버에서 구현할 경우 Thymeleaf에 React같은 Redux 같은기능이 없기때문에 서버단에서 완전히 해결해줘야 한다.
      * **이때, "캐시메모리"를 활용해서 해결이 가능하다는 것**
      * 서버 메모리에 저장하기 때문에 DB에 쿼리문 날릴필요 없기때문

* **DB의 Limit, offset 을 활용한 "페이징"을 더 간단히 하는법**

  * 단, **JPQL**에서는 Limit와 Offset 키워드는 사용 불가하고 **setFirstResult(), setMaxResults()** 를 사용해야 함.
  
  * 또다른 방법은 제공해주는 클래스 사용 -> **Pageable 클래스 활용**
  
  * **왜?? 사용하나??**
  
    * **(SSR 가정) 1000개 게시물을 1page에 10개씩 보여주는 구조를 만든다면??**
  
      * 캐시메모리 사용 시 오히려 캐시메모리 사용량과 갱신에 많은 오버헤드 우려
      * 대신 전체 게시물을 캐시메모리에 기록하는게 아니라 **"페이지별로" 캐싱**
  
    * 페이지별로 url(?page=1) 접근하면 해당 페이지별로 데이터를 가져올거고 이 데이터를 **@CachePut로 기록하고, @Cacheable로 조회**
  
      * 예로 `@CachePut(value = "posts", key = "#pageId")` 이런 형태
  
        * posts를 저장된 구간(키값)으로 보면되고, #pageId 를 파라미터로 들어온 pageId 속성값으로 매핑되며 해당 값을 키값으로 메모리에 기록
        * 이 때문에 pageId로 이 값을 바로 찾을수도 있음
  
      * **그럼 page를 하나하나 100개 전부 접근하면 결국 1000개 데이터가 전부 캐시메모리에 기록되고, 오버헤드가 우려되지 않는가???**
  
        * 오래된 캐시를 제거하는 등의 방법으로 해결
        * 또한, 애초에 그런 접근은 악의적인 접근으로써 따로 보안로직을 구현해야한다고 생각
  
      * **그럼 게시물이 삭제되거나 수정되면?? 특히 삭제되면 페이지별 데이터 10개 구성한것도 9개가 되고 갱신도 되어야할거고 그럴텐데 이건 어떻게 해결할건데??**
  
        * 위에서 pageId 별로 캐시메모리에 기록하기 때문에 해당 페이지만 수정하면 되는것(모든 페이지가 아니라)
  
        * 참고로 페이지별로 조회할때 `setFirstResult, setMaxResults` 를 pageId 를 이용해서 접근!
  
          ```java
          public List<Item> findAllWithPage(int pageId) {
              return em.createQuery("select i from Item i", Item.class)
                  .setFirstResult((pageId-1)*10)
                  .setMaxResults(((pageId-1)*10)+10)
                  .getResultList(); // 해당 페이지(pageId) 데이터 조회
          }
          ```
  
      * **마지막으로 캐시 메모리에 데이터도 제한이 가능한가??**
  
        * 가능하며, 아래 예시를 참고
  
          ```properties
          # application.properties
          spring.cache.cache-names=posts
          spring.cache.caffeine.spec=maximumSize=100 # 캐시 사이즈 설정 (예시로 최대 100개의 페이지를 캐시로 관리)
          ```

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
  * **`@RequiredArgsConstructor` : lombok의 어노테이션인데 이것도 `엔티티매니저` 주입을 제공해주므로, 이것과 `final` 조합으로 리포지토리, 서비스 둘다에서 활용하는것을 권장**
    * 예를 들면, @Repository 로 스프링 빈에 등록한 레퍼지토리를 다른 코드에서 사용하고 싶을때 "주입" 을 통해서 사용할 수 있다.
    * @Autowired를 생성자위에 선언하면 "생성자 주입" 으로 사용할 수 있고, 필드위에 선언하면 "필드주입" 으로 사용할 수 있다.
      * 여기서 "생성자 주입" 관점이 @RequiredArgsConstructor 로 대체 가능하다.
      * 자동으로 생성자 생성해서 제공해준다.
  * 보통 만드는 DB와 연관 메서드 : save(), findOne(), findAll(), findByName() 등등...
    * `EntityManager` 의 함수들은 잘 구현되어있어서 `find()` 함수를 쓸때 값 없을때 null로 잘 반환해줌
    * **우리가 직접 쿼리문 날려서 `find()` 함수를 구현할 때는 이 null 처리를 잘 해줘야한다는점**
      * **TIP : `getResultList()` 형태로 (즉, List) 값을 받으면 null처리가 매우 간단**

* **서비스**

  * @Service : 스프링빈으로 등록
  * @Transactional : 트랜잭션, 영속성 컨텍스트
    - readOnly=true : 데이터의 변경이 없는 읽기 전용 메서드에 사용, 영속성 컨텍스트를 플러시 하지 않으므로 약간의 성능 향상(읽기 전용에는 다 적용)
    - 데이터베이스 드라이버가 지원하면 DB에서 성능향상
    - **이와 같은 이유로 `@Transactional(readOnly = true)` 로 사용 및 쓰기모드 필요할때면 해당 메소드단에 `Transactional` 을 또 선언해서 쓰기모드까지 사용**
      - 기본값이 `readOnly=false` 
  * @Autowired
    - **앞전에 언급한것처럼 `@Autowired` 대신에 `@RequiredArgsConstructor` 와 `final ` 를 활용**

  - 보통 만드는 비지니스 로직 메서드 : join(), findMembers(), findOne() 등등...

* **컨트롤러**

  * @Controller : 스프링빈으로 등록, 핸들러 매핑 대상으로 기억

  * @RequestMapping, @GetMapping, @PostMapping 등등..  : HTTP 매핑

    * @RequestMapping을 클래스단에 @GetMapping, @PostMapping 을 메서드단에 사용 권장
      * @RequestMapping은 전역으로 경로 지정하기 좋기 때문
      * @RequestMapping 은 GET,POST,PUT... 등 전부 허용하기 때문
    * @PostConstruct : 테스트용 데이터를 코드 실행하자마자 바로 넣을 수 있음 (테스트하기 수월!)

  * @RestController : 뷰로 반환하는게 아닌 HTTP Body에 반환값 기입반환 **(API 만들시 적극 권장)**

    * @RestController는 @Contorller, **@ResponseBody** 등등을 포함하는 어노테이션

  * @PathVariable("userId"), @RequestParam => @PathVariable 방식 권장

    - @RequestParam : 기존 url 쿼리 파라미터 방식 : ?userId=userA
      - 단, POST-HTML Form 방식도 body를 쓰지만 쿼리 파라미터 형식으로 저장되기 때문에 @RequestParam 을 사용 가능
      - 또한, 생략도 가능한데 **본인은 넣는걸 권장** (이때 required=true)
      - 기본값 설정도 가능 (defaultValue = "-1")!!
        - 널 뿐만아니라 "/username=" 이렇게 "" 빈값으로 넘어온 데이터도 기본값을 설정해줌
      - Map, MultiValueMap 형태로 값을 받아올수도 있음
    - @PathVariable("itemId") : 최신 트랜드인 경로 변수 방식 : /mapping/userA
      - 중요한점은 @PathVariable 로 매핑한 userA가 따로 Model을 활용하지 않아도,
      - 백뿐만 아니라 프론트에서도 userA값을 사용가능하단 점이다.
      - (이부분은 추측이지만, 자동으로 변수를 추가해서 같이 프론트로 반환되는게 아닐까)
        - 스프링의 Model 클래스는 브라우저의 쿠키처럼 프론트에 같이 넘어가는 클래스
        - 이 때문에 데이터를 주고받기 수월하단 장점을 가짐.
      - 물론, 햇갈릴수도 있어서 그냥 **Model을 항상 데이터 보내는 용도로 사용**하고,
      - **@PathVariable을 url로 받은 값을 사용하는 목적**으로 활용하는게 젤 좋아보임.

  * @ModelAttribute("from")

    * model.addAttribute 에도 담기고, form서밋 때 html에 있는 form 데이터를 매핑해서 변수에도 자동으로 담아줘서 변수선언도 따로 할 필요 없음

    * 또한, @ModelAttribute 를 생략하고 바로 HelloData 가 와도 동일하게 가능

      ```java
      public String modelAttributeV1(@ModelAttribute HelloData helloData) {
          log.info(helloData.getUsername()); // 바로 변수 사용 가능!!
      }
      ```
      
    * **참고로 특별한 사용법이 있는데 전역으로 Model에 항상 적용법**

      * 물론 static으로 따로 구현해두는게 성능상 더 좋음

      ```java
      @ModelAttribute("regions") // regions 이름으로 Model에 넣음
      public Map<String, String> regions() {
          Map<String, String> regions = new LinkedHashMap<>();
          regions.put("SEOUL", "서울");
          regions.put("BUSAN", "부산");
          regions.put("JEJU", "제주"); 
      return regions;
      }
      ```

  * @RequestBody, @ResponseBody : HttpEntity 처럼 **HTTP 메시지 컨버터**가 **HTTP 메시지 바디**의 내용을 우리가 원하는 문자나 **객체 등으로 자동 변환**!!

    * 요청파라미터 @RequestParam, @ModelAttribute 랑은 전혀 관계없으니까 혼동 X

      * **왜냐하면, HTTP 메시지 Body 를 통해서 데이터가 넘어오는 경우이기 때문!!**

    * 요청오는건 RequestBody, 응답으론 ResponseBody

      ```java
      @ResponseBody
      @PostMapping("/request-body-json-v5")
      public HelloData requestBodyJsonV5(@RequestBody HelloData data) {
          log.info(data.getUsername());
          return data;
      }
      ```

      * @RequestBody를 활용할거면 꼭 Dto 형태 타입으로 인수 받을것(경험상)
        * ObjectMapper 필요없이 자동으로 바꿔줘서 편리
      * 요청으로 들어오는 json 데이터를 @RequestBody HelloData data 로 인해 Hellodata 객체로 바꾸고,
      * 반환 타입을 String이 아닌 HelloData로 하면 @ResponseBody 로 인해 return할때 응답body에 문자로 넣어준다 했는데 덕분에 json로 집어넣어준다.
      * 즉, json(요청)->객체->json(응답) 로 동작한다.

    * **(핵심!) 만약 @ResponseBody 가 없으면 뷰 리졸버가 실행 되어서 뷰를 찾아서 렌더링!!**

      * **참고로 @RestController 는 @ResponseBody 가지고있음!!**

  * 뷰 반환, 데이터 반환 정리

    * @Controller - View 반환
    * @Controller + @ResponseBody - Data 반환
    * @RestController - Data 반환

  * 쿠키 편리하게 조회 @CookieValue

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
  * **print대신 assert비교 테스트**
    * `Assertion`을 이용하자. 이걸 사용해서 `assertEquals`함수 사용시 두개 인자가 동일한지 봐준다
      * 안동일하면 오류, 동일하면 아무일도 없음
      * `Assertions.assertEquals(member, memberRepository.findOne(saveId));`
      * `Assertions.assertThat(member).isEqualeTo(result);` 이것도 위처럼 사용된다.
  * **예외 테스트**
    * try, catch보다 간편하게 `assertThrows`를 사용해서 일부러 예외를 터트려서 테스트 하는것이 있다.
    * 또는 try, catch대신 `@Test(expected = IllegalStateException.class)` 를 선언하면 알아서 해당 예외 터질 때 종료해줌
      * 만약 해당 예외가 안터지면 그다음 코드들이 계속 실행됨. 그 코드는 아래 형태로 작성
      * `Assertions.fail("예외가 발생해야 한다.");` 예외가 안터져서 오히려 에러라고 로그를 남겨줌
  * **HttpServletRequest, HttpServletResponse 관련 테스트**
    * 이를 흉내내주는 `MockHttpServletRequest, MockHttpServletResponse` 을 사용!

* **ETC**

  * @Slf4j : 로그를 바로 log로 사용 가능
  * @NotEmpty("에러 메세지 관련")
  * @Data -> 롬복
    * @Getter, @Setter, @ToString, @EqualsAndHashCode, @RequiredArgsConstructor 적용
  * @SpringBootApplication : 톰캣 내장(서버)
  * @Valid + @NotEmpty(message="회원 정보필수") 이런식으로 같이 사용
    * 예전꺼라서 의존성 추가 필수
    * `implementation 'org.springframework.boot:spring-boot-starter-validation'`
  * **@AllArgsConstructor : 생성자 대신 만들어줘서 필드만 선언**
    * **참고로 생성자 주입 방식인 `@RequiredArgsConstructor` 와 햇갈리지 말것**
  * @Value : application.properties에 선언한 변수 사용

<br>

**리팩토링**

* **AOP(공통 해결 관심사)**
  * **일반 상황**
    * 만들때 : @Aspect 사용 및 스프링 빈에 등록 필수
    * 사용할때 : @Around로 원하는 곳에 적용
    * 동작 : 프록시 객체 생성 -> 실제 객체 생성 의 흐름
  * **웹의 경우**
    * **서블릿 필터 보다는 스프링 인터셉터** 사용 권장 및 **ArgumentResolver** 활용 권장
      * **ArgumentResolver** 를 통해서 공통 작업할 때 컨트롤러를 더욱 편리하게 사용 가능
        * `HTTP 요청 -> WAS -> 필터 -> 서블릿 -> 인터셉터1 -> 인터셉터2 -> 컨트롤러`
          * 서블릿(=Dispatcher Servlet) 과 컨트롤러(핸들러) 사이에는 사실 핸들러 어댑터가 동작을 하며, 
          * 이때 "**ArgumentResolver**" 가 중간에 있어서 이를 거치고 "컨트롤러(핸들러)" 가 동작
          * **ArgumentResolver 덕분에 수많은 애노테이션으로 만든 데이터들을 컨트롤러(핸들러)로 정상 전달**
        * EX) @Login 애노테이션 만들어서 "멤버 정보" 객체 반환 이런게 가능!
        * 기존 : `@SessionAttribute(name = "loginMember", required = false) Member loginMember` 
        * **적용 : `@Login Member loginMember`** 
    * **"인터셉터" 는 "필터" 보다 더 많이 호출** 
      * **"로그인 인증" 을 예시로 구현 가능**
        * `preHandle` 에 "세션정보(쿠키)" 인증을 시도 및 실패시 다시 "로그인창" 으로 이동
        * 이를 다양한 **URL 패턴으로 적용 가능** - 적용URL, 미적용URL 구분도 간편
* **예외처리 - Spring Exception**
  * **html**
    * 자동으로 에러에 필요한 로직을 등록하므로 바로 활용가능
    * `ErrorPage, BasicErrorController` 자동 등록 및 `/error` 경로로 기본설정
    * `BasicErrorController` 는 `ErrorPage` 에서 등록한 `/error` 를 매핑해서 처리하는 컨트롤러
  * **API**
    * API는 html보다 예외처리가 세부적이므로 `ExceptionHandlerExceptionResolver` 로 해결
    * 즉, 자동 등록한 에러 로직을 사용하지 않고 `@ExceptionHandler, @RestConrollerAdvice` 사용
      * `@RestControllerAdvice` 를 통해서 컨트롤러를 "기존코드, 예외코드" 나눠서 분류 가능

<br>

**기능 확장법**

* 스프링은 `WebMvcConfigurer` 를 상속받아서 스프링빈에 등록후 기능을 확장한다.
  * @Bean 이나 @Configuration 선언
* 예로 인터셉터, ArgumentResolver 도 새로 구현한 후에는 이곳에 등록해서 확장해야한다.

<br>

**(중요)"메시지, 국제화 기능"**

* `application.properties` 에 `spring.messages.basename=message` 를 추가!!
  * 이후 `messages.properties` 에 공통 관리할 messages에 담을 내용을 세팅
* 여러개 추가할거면?? 
  * 예로 `errors.properties` 추가한다고 하면 `spring.messages.basename=message, errors` 이렇게 이어적으면 됨

<br>

**검증 - Validation (의존성 추가 필수)**

* **예외?**
  * 가장 좋은 예외는 **컴파일 예외**, 그리고 **애플리케이션 로딩 시점에 발생하는 예외**
  * 가장 나쁜 예외는 고객 서비스중에 발생하는 **런타임 예외**
  * 검증기 사용시 **앱 로딩 시점 예외**로 나타내 줄 수 있다는 장점!

* **검증이란** Form 데이터같은 것들이 POST 요청왔을때 원하는 "검증"을 진행하는 것  
  (ex: 0~9999 숫자범위"를 지정)

  * HTTP 요청 "Form데이터, URL파라미터" 는 "검증" 만으로 충분 - @ModelAttribute
  * HTTP 요청 "API" 는 "검증 + 예외처리" 까지 필요 - @RequestBody
    * 그럼 예외는?? String 타입에 int가 넘어왔다던지 이런쪽의 에러를 처리하는걸 "예외처리"
    * 또한, **API의 경우에는 여기서 사용한 메커니즘들을 활용하되 메시지는 꼭 API스펙에 맞춰 잘 반환**해주면 됨 (참고로 타임리프는 그냥 th문법으로 알아서 잘 가져옴)

* **"@Validated + {HTTP요청 + BindingResult} + 검증 애노테이션 + errors.properties" 사용**

  * @Valid, @Validated 둘다 사용가능하나 @Validated가 추가한 라이브러리 사용 지원

  * {HTTP요청(@ModelAttribute or @RequestBody) + BindingResult} 는 꼭 이순서대로 파라미터에 작성

  * **[검증 애노테이션 모음](https://docs.jboss.org/hibernate/validator/6.2/reference/en-US/ 
    html_single/#validator-defineconstraints-spec)** => DTO로 만든 도메인에 원하는 "검증" 작성!

  * BeanValidation의 메시지 찾는순서는 `errors.properties` 를 먼저 찾고 `검증 애노테이션의 message 속성` 사용

    ```properties
    NotBlank.item.itemName=상품 이름을 적어주세요.
    NotBlank={0} 공백X
    Range={0}, {2} ~ {1} 허용
    Max={0}, 최대 {1}
    ```

    * NotBlanck 보다 NotBlank.item.itemName 같이 세부 필드를 더 우선순위 높게 출력
    * FieldError, ObjectError 의 개념이 존재
      * FieldError 는 도메인에 "검증 애노테이션" 사용 + "bindingResult.hasErrors()" 필수
      * ObjectError 는 "직접 작성 - bindingResult.reject() 함수 권장"
        * `bindingResult.hasErrors()` 는 errors가 있는지 여부를 반환하고, 
        * `errors` 에는 "검증결과 에러" 들을 기록하며 이는 "검증 애노테이션"에 걸린 에러들을 의미

* **참고) id에는 적용하는가??? 언제써야 하는가???**

  *  id에 왜 검증을 넣냐고 볼 수도 있지만, POSTMAN같은 툴로 충분히 악의적 접근이 가능하기 때문에 "최종 검증은 서버에서 진행하는 것이 안전"
    * 예로 "등록 폼" 에서는 보통 id가 자동생성하므로 필요없기 때문에 @NotNull 같은걸 적용안해도 됨. 그러나 "수정 폼" 에서는 id가 필요하기에 "검증"을 해주는것이 안전
    * **즉, 필요한 경우가 있을때는 "서버에서 최종 검증" 을 하는것이 안전!**

* **타임리프**

  * th:field, th:errors, th:errorclass 를 주로 같이 활용

<br>

**예외처리 - @ExceptionHandle** 

* **웹 에러 처리의 경우에는 기존 "스프링 부트 기본제공" (BasicErrorController) 을 규칙에 맞게끔 사용하자**
* **API 에러 처리의 경우에는 @ExceptionHandler 와 @ControllerAdvice 를 조합해서 사용하자**
  * @ControllerAdvice 는 에러처리 로직을 분류하는 역할!

<br>

**외부 설정 및 프로필**

* `application.yml` 사용 추천 및 아래처럼 사용

```yaml
# default 프로필
my:
    datasource:
        url: local.db.com 
        username: local_user 
        password: local_pw 
        etc:
            maxConnection: 2 
            timeout: 60s
            options: LOCAL, CACHE 
---
# prod 프로필
spring:
	config:
		activate:
			on-profile: prod 
my:
    datasource:
        url: prod.db.com 
        username: prod_user 
        password: prod_pw 
        etc:
            maxConnection: 50 
            timeout: 10s 
            options: PROD, CACHE
```

* `@ConfigurationProperties` 를 통해 위 "외부 설정" 파일을 "자바 객체" 로 변경해서 "스프링 빈" 에 등록
* "실행 시점" 에 원하는 프로필 사용!!
  * IDE에서 Application 에 `--spring.profiles.active=prod` 하거나 (물론 값도 가능)
  * Jar 실행 : `java -.... app.jar`

<br><br>

# 참고용 정보

**스프링부트 플러그인 사용하면 "라이브러리 버전관리 자동화" - 물론 지원안하는건 직접 버전 등록**

**web 라이브러리 없으면 바로 종료되는데, 이때 `ApplicationRunner` 구현체로 자바코 드실행하는게 보통**

* ApplicationRunner 인터페이스를 사용하면 스프링은 빈 초기화가 모두 끝나고 애플리케이션 로딩이 완료되는 시점에 run(args) 메서드를 호출

```java
@Component
@RequiredArgsConstructor
public class OrderRunner implements ApplicationRunner { 
    private final OrderService orderService;
    
    @Override
    public void run(ApplicationArguments args) throws Exception { 
        orderService.order(1000);
    } 
}
```

**redirect vs forward**

* redirect 는 서버에서 응답을 통해 클라까지 응답이 나갔다가 클라가 redirect 경로를 보고 다시 해당 경로로 서버에 요청하는 형태
* forward 는 서버 내부에서 일어나는 호출이므로 클라가 전혀 인지하지 못함

**PRG Post/Redirect/Get** 

* **웹 브라우저의 새로고침은 마지막 서버에 전송한 데이터를 다시 전송한다.**  

* **따라서 POST 적용후 새로고침을 하면 계속 POST 보내는 문제가 발생하므로 이를 Redirect를 통해서 GET으로 요청하는 방식으로 해결할 수 있다.**

* **RedirectAttributes 추천**
  * Redirect 할때 Model처럼 파라미터를 추가해서 간편히 넘겨줄 수 있고, URL 인코딩 문제에서 자유롭다!
    * `"redirect:/basic/items/" + item.getId()` 는 인코딩 문제가 발생할 수 있는데,
    * `"redirect:/basic/items/{itemId}"` 는 인코딩 문제에서 자유롭다.

  * **특히 Status 정보를 파라미터로 넘김으로써 `th:if` 문법으로 "저장완료" 표시도 나타내는데 많이 사용한다.**


**jar vs war**

* **jar은 내부 톰캣 사용, war은 외부 서버 사용**으로 이해하면 됨
* 외부 톰캣에서 webapp 경로를 사용하는 편인데, 실제로 war은 사용가능하나 jar은 사용불가

**Content-Type 헤더 기반 Media Type 과 Accept 헤더 기반 Media Type**

* text/html, application/json 같은 content-type 을 의미
* **요청때나 응답할때나 body를 사용할때는 필수로 존재 및 서로 맞게 요청해야 함**

<br>

**타임리프 문법**

* 핵심 : 서버로 실행(뷰 템플릿 사용)하면 타임리프 문법들이 적용해서 동적으로 변경!
  * 스프링 부트는 "뷰 리졸버" 를 자동 등록하는데, 이때 설정파일에 등록한  `spring.mvc.view.prefix , spring.mvc.view.suffix` 정보를 사용해서 등록한다.
  * "뷰 리졸버" 에 필요한 "경로" 를 설정하는 부분인데 요즘 Thymeleaf 는 이것도 자동으로 등록해줘서 설정할 필요가 없다.
    * 혹시나 JSP 사용할 경우에는 이부분 기억해두자.
* 타임리프 사용 선언
  * `<html xmlns:th="http://www.thymeleaf.org">`
* 속성 변경

  * `th:href="@{/css/bootstrap.min.css}"`
  * `th:onclick="|location.href='@{/basic/items/add}'|"`
  * `<td th:text="${item.price}">10000</td>`
  * `th:value="${item.id}"`
  * `th:action`
  * ... 등등 매우 다양
* URL 링크표현식 - @{...}

  * `th:href="@{/css/bootstrap.min.css}"`

  * `th:href="@{/basic/items/{itemId}(itemId=${item.id})}"`
  * 심화) `th:href="@{/basic/items/{itemId}(itemId=${item.id}, query='test')}" `
    * 생성된 링크: `http://localhost:8080/basic/items/1?query=test`
  * 간편) `th:href="@{|/basic/items/${item.id}|}"`
    * 리터럴 대체문법 적용가능
* 리터럴 대체 - |...|

  * 타임리프에서 문자와 표현식 등은 분리되어 있기 때문에 더해서 사용해야 한다.
    * `<span th:text="'Welcome to our application, ' + ${user.name} + '!'">`
  * 다음과같이 리터럴 대체문법을사 용하면, 더하기 없이 편리하게 사용할 수 있다.
    * `<span th:text="|Welcome to our application, ${user.name}!|">`
    * `th:onclick="|location.href='@{/basic/items/{itemId}/edit(itemId=${item.id})}'|"`
* 변수표현식 - ${...}

  * `<td th:text="${item.price}">10000</td>`
* 반복출력 - th:each

  * `<tr th:each="item : ${items}">`
  * 컬렉션의 수만큼 `<tr>..</tr>` 이 하위 테그를 포함해서 생성된다.
* 조건문 - th:if

  * `<h2 th:if="${param.status}" th:text="'저장 완료'"></h2>`
* 변수선언 - th:with
  * `th:with="first=${users[0]}"` -> frist 로 사용 가능
* text, utext, [[...]], [(...)]
  * text vs utext
    - th:text = Hello \<b>Spring!\</b>
    - th:utext = Hello **Spring!**

  * [[...]] vs [(...)] -> 속성이 아니라 컨텐츠 안에서 직접 출력!
    - [[...]] = Hello \<b>Spring!\</b>
    - [(...)] = Hello **Spring!**

* 편의 객체 제공 - param, session 등
  * `param.title` 같이 파라미터 바로 접근 가능
* 비교연산 - HTML 엔티티 주의!! 
  * \> : gt 로 표기

* Elvis 연산자 - `"${data}? : _"`
  * data 있으면 true조건 실행

* No-Operation : "_" 
  * 마치 타임리프 실행안한것처럼 동작

* 타임리프 파서 주석 : `<!--/* [[${data}]] */-->`
  * 참고로 `/*사이에서 여러줄 가능*/`
  * 렌더링때 삭제되는 것

* 블록 - `<th:block>`
  * `<th:block>` 는 타임리프가 제공하는 유일한 자체 "태그"
  * **렌더링 할때는 아예 태그가 삭제**
* fragment, JS
  * fragment : 코드 재사용
  * JS : javascript 에서 사용 가능
* `<input>과<label>` 에서 th:for로 id값 연결 하는 편
  * 동적 id 인식 - `#ids.prev()`

<br>

**타임리프 + 스프링 통합 문법**

* **th:object, th:field, *{itemName} 활용**
* **체크박스, 라디오버튼, 셀렉트 박스에서 활용**
* **(중요)"메시지, 국제화 기능"**
  * `application.properties` 에 `spring.messages.basename=message` 를 추가!!
* 여러개 추가할거면?? 예로 errors.properties 추가한다고 하면 message, errors 이렇게 이어적으면 됨
  * 이후 `messages.properties` 를 추가해서 messages에 담을 내용을 세팅
* 타임리프로 사용 예시 : `<h2 th:text="#{page.addItem}">상품 등록</h2>` `
* **(참고) nullsafe**
  * `th:if="${errors?.containsKey('globalError')}"` 에서 ?를 통해 null 로 나타나므로 if는 false로 반환
  * ?가 없으면 null.containesKey... 로 에러
* "컨트롤러"에서 그냥 @GET 으로 페이지 로딩할때 item을 빈값이라도 선언해둬서 Model에 담아 반환하는걸 권장
  * **검증 실패 때 forward로 "자원 재활용"이 됨.**
  * HTML 코드도 더 깔끔 -  if문으로 null인지 확인할 필요없이 그냥 item을 타임리프 문법으로 사용하면 되기때문

<br>

**로그인 구현 방식**

* 컨트롤러 - ArgumentResolver 활용해서 @Login으로 바로 Member 객체 가져오기

  ```java
  @GetMapping("/")
  public String homeLoginV3ArgumentResolver(@Login Member loginMember, Model model) {
      //세션에 회원 데이터가 없으면 home
      if (loginMember == null) {
          return "home";
      }
      //세션이 유지되면 로그인으로 이동
      model.addAttribute("member", loginMember);
      return "loginHome";
  }
  ```

* 인터셉터 - URL 접근시 전부 회원인증 여부 체크(공통관리)를 인터셉터로 확인

  ```java
  @Slf4j
  public class LoginCheckInterceptor implements HandlerInterceptor {
  
      @Override
      public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
  
          String requestURI = request.getRequestURI();
  
          log.info("인증 체크 인터셉터 실행 {}", requestURI);
  
          HttpSession session = request.getSession(); // false가 나을듯?
  
          if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {
              log.info("미인증 사용자 요청");
              //로그인으로 redirect
              response.sendRedirect("/login?redirectURL=" + requestURI);
              return false;
          }
  
          return true;
      }
  }
  ```

* WebMvcOnfigurer 적용 - 인터셉터와 ArgumentResolver 를 설정해야 적용이 됨

  ```java
  @Configuration
  public class WebConfig implements WebMvcConfigurer {
  
      @Override
      public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
          resolvers.add(new LoginMemberArgumentResolver());
      }
  
      @Override
      public void addInterceptors(InterceptorRegistry registry) {
          registry.addInterceptor(new LoginCheckInterceptor())
                  .order(2)
                  .addPathPatterns("/**") // 인증O
                  .excludePathPatterns("/", "/members/add", "/login", "/logout",
                          "/css/**", "/*.ico", "/error"); // 인증X
      }
  }
  ```

* 타임아웃은 글로벌 설정으로 이미 1800(30분)으로 적용되어 있는것같아서 수정할 필요 없어보임.
  그래도 설정이 눈에 보이게끔 server.servlet.session.timeout=1800 // 분단위. 이렇게 놔둬.

* 쿠키 id는 SessionConst.java 만들어서 상수로 등록 권장. 자주쓰니까

<br>

**타입 컨버터 (참고)**

* 웹 - `@Requestparam, @ModelAttribute, @PathVariable` 스프링이 기본 지원

  * "타임리프" 도 지원 - `th:field, ${{...}}`

* 단, **HTTP API (@ResponseBody 등)**의 경우 지원하지 않는다(**HttpMessageConverter 는 "컨버전 서비스 적용 불가"**)
  * **이 경우에는 Jackson 같은 라이브러리에서 포맷터를 찾아 사용**

* 물론 "확장 가능" 하고, 아래는 애노테이션 제공

  ```java
  @Data
  static class Form {
      @NumberFormat(pattern = "###,###")
      private Integer number;
  
      @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
      private LocalDateTime localDateTime;
  }
  ```


<br>

**"파일 업로드, 다운로드" 기본 지식**

* **파일은 "스토리지"** 저장, 경로와 이름 등 정보**(EX: Item)는 "DB"** 저장
* **Item** - uploadFileName, storeFileName 는 필수 저장
  * uploadFileName(업로드 파일명), storeFileName(서버에 저장된 파일명) 둘 다 DB에 기록해놔야 함
  * 업로드 파일명들은 사람마다 중복될 수 있으며, 서버 파일명은 중복되면 안돼서 UUID 같은걸로 지정하기에 "둘 다 기록"
* **ItemForm** - Item의 Dto 용으로 만들어서 Form 데이터를 받는 도메인을 만들어줘야 함
  * 여기선 `MultipartFile` 타입을 사용해 데이터 받을거라 Item 에선 할 수 없기에 만들어줌
* **FileStore.java**
  * "스토리지" 에 저장하는 로직을 작성해서 "컨트롤러" 에서 사용
* **컨트롤러에서..**
  * `@GetMapping("/images/{filename}")` : \<img> 태그로 **이미지를 조회**할 때 사용
    * UrlResource 로 이미지 파일을 읽어서 @ResponseBody 로 이미지 바이너리를 반환
    * 경로에 "file:" 을 넣어야 내부저장소 경로를 접근하는 것 (스토리지에 파일 있으니까!)
      * 이 부분을 통해 **"경로 설정" 을 꼭 해줘야 정상 접근**
  * `@GetMapping("/attach/{itemId}")` : **파일을 다운로드** 할때 실행
    * "/attach/{itemId}" - \<a> 태그 "href" 활용해 "파일명" 을 눌러서 접근하게 한 URL 경로
      * 파일 다운로드시 권한 체크같은 복잡한 상황까지 가정해서 이미지 id 를 요청하도록 함
    * 파일 다운이 되려면 반환할때 **"헤더" 가 필수**
    * 파일 다운로드시에는 고객이 업로드한 파일 이름으로 다운로드 하는게 좋다. 
      * Content-Disposition 해더에 `attachment; filename="업로드 파일명"`
