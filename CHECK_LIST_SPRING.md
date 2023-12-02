# Intro

**`Spring + Spring Boot + JPA + Thymeleaf` 를 활용한 개발 규칙**

* 현재 공부한 범위에서 **최선의 개발 방향**을 기록
* **단축키**
  * `Alt + Insert` : getter, setter, constructor 등 자동 생성
  * **`Ctrl + Alt + O` : 사용안하는 import 자동 정리(제거 등)**
    * **`Ctrl + Alt + L` : 자동 정렬 -> Google Java Convention이나 Default 로 사용중**
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

# CHECK LIST



## 개발과정, 네이밍 TIP

**개발과정**

* **요구사항 분석(대략적 기능)**
* **기능 목록(상세한 기능)**
* **도메인 설계**
  * 도메인 모델 분석(간략히)
  * 테이블 설계(DB) + 엔티티 설계(JPA)
  * **`ERDCloud` 툴을 사용한 설계 => 테이블, 엔티티 설계를 여기서 한번에**
* **기능 구현 -> TDD 기법 활용 권장**
  * 도메인 구현 -> 엔티티를 의미하며, 모든 계층에서 사용
  * 레퍼지토리 구현 -> DB와 상호작용
  * 서비스 구현 -> 비지니스 로직 & 트랜잭션
    * `도메인 모델 패턴` : 서비스 계층은 단순히 엔티티에 필요한 요청을 위임하는 방식
    * `트랜잭션 스크립트 패턴` : 엔티티에는 비지니스 로직이 거의 없고 서비스 계층이 담당하는 방식
    * **참고로 본인은 `도메인 모델 패턴` 방식을 주로 사용**

  * 컨트롤러 구현 -> 웹 계층과 상호작용 (API 포함)

<br>

**네이밍 TIP**

- **Database**
  - 테이블명 형식으로 `ORDER 또는 order` 사용 **=> 대문자** or 소문자
  - 컬럼명 형식으로 `order_id` 사용 **=> 스네이크 케이스**
  - 스프링에서 테이블 매핑 마지막에 전부 **"대문자"**로 자동
    - 실제로 스프링 부트로 DB 테이블 자동 생성시 **"대문자" 이름 확인**
- **JPA -> ORM(객체 관계 매핑)**
  - 엔티티명 형식으로 `OrderItem` 사용 **=> 파스칼 케이스**
    - **스프링 부트는 엔티티명을 `OrderItem -> ORDERITEM` 처럼 "대문자"로 바꿔서 매핑**
  - 필드명 형식으로 `orderId` 사용 **=> 카멜 케이스**  
    - **스프링 부트는 필드명을 `orderId -> order_id` 로 컬럼명 찾아서 매핑**

<br><br>

## 테이블과 엔티티 설계&구현

**테이블 설계 **

* **데이터(테이블) 중심** 설계
  * 보통 **PK**인 컬럼명을 **`테이블명_id`** 형태로 쓰지만, 나머지 컬럼명들은 **`테이블명` 을 안붙이는 편**

* **N:N** 관계는 **1:N, N:1** 로 풀기
  * N:N은 무한루프 때문 => [다른분 그림참고](https://siyoon210.tistory.com/26)

* **외래키가 있어야할 위치**
  *  **1:N, N:1** 의 경우 **N**에 사용
    * 성능 저하 때문(많은 JOIN문 발생) => [다른분 그림참고](https://siyoon210.tistory.com/26)

  * **1:1**의 경우 **상황에 따라** 사용 - 보통은 **주 테이블에 외래키** 사용
    * 주 테이블 외래키 단방향 - 단점 : 값 없으면 외래 키에 null 허용
    * 대상 테이블에 외래키 양방향 - 단점 : 무조건 즉시로딩

* **상속**의 경우 JOINED, SINGLE_TABLE 전략 중에서 **JOINED 전략**을 많이 사용
  * 부모 자식간에 join을 하므로 똑같이 테이블 생성
  * Index 덕분에 빠르게 join이 가능 (본인생각 검색해봐야함)

<br>

**엔티티 설계&구현**

* **객체 중심** 설계
  * 보통 **PK**인 필드명을 **`id`** 로 쓰고 **직접** 테이블의 컬럼명과 **매핑**을 선언함 - @Column(...)
  * 개발과정에선 **Getter, Setter**를 열어두고 나중에 리팩토링으로 **Setter** 들은 제거
    * 엔티티에서의 **비지니스 메서드** 구현은 **Setter** 제거 효과
    * **setter를 최대한 사용하지 않게끔 DTO 방식 권장**
* 엔티티 설계 때 **연관관계는 단방향 우선 개발(테스트)** 후 양방향 관계 추가
  * 양방향은 코드만으로 해결 가능해서 DB 설계에 아무런 영향을 끼치지 않음


- 즉시 or 지연 로딩 중에서 무조건 **"지연 로딩"** 으로 개발 => **N+1 문제** 때문

  - 코드상에서 `@XToOne` 은 기본이 **즉시 로딩**이므로 반드시 **지연로딩**으로 전부 변경
  - 단, **"즉시 로딩"**도 **N+1 문제** 발생시킬 수 있다는점을 알고가자
  - **"지연 로딩"**이 가능한 이유 : "프록시(가짜객체)"를 사용하기 때문

- 옵션 중에서 **cascade** 사용 유무는 관계가 **완전 종속일때만** 사용 (연관된 데이터 연쇄적 변경 효과)

  - cascade는 영속성 전이를 하므로, 연관관계 매핑과는 전혀 관계 없음 
  - 단지 이를 사용하면 **생명주기를 같이** 하는것

- **다양한 구현 TIP**

  - **ENUM** 데이터 사용 시 `@Enumerated(EnumType.STRING)` 로 꼭 `STRING` 으로 옵션 (이유 생략)
  - **중복 코드**를 줄이는 효과적인 방법들
    - **`임베디드 타입(값 타입)` 과 `상속-Mapped Superclass`** 이것 두개를 잘 활용 - 중복 코드를 많이 줄임
      - `값 타입` 은 조금 적은 중복 때,`상속` 부분은 거의 모든 엔티티에 속하는 중복 때 사용하면 괜찮겠다고 판단 (본인 생각임)
      
    - **설계할때 부터 "값 타입"으로 활용될거는 따로 빼서 설계**
      - `값 타입` 은 엔티티 클래스에 `private Long id;` 와 같은 필드라고 생각
      - 값 타입은 정말 값 타입이라 판단될 때만 사용 (엔티티와 혼동이되면 안되므로)
      - 식별자가 필요하면 "엔티티"를 의미하며, "임베디드 객체" 같은건 값 비교할때 equals 메소드 반드시 오버라이드 필요
      
      - "임베디드 객체" 는 값 타입 하나. `@Embed...` 로
      - "값 타입 컬렉션" 은 값 타입 하나 이상. 이건 그냥 `"일대다 고아+cascade" 엔티티` 로
  - **컬렉션(List같은,,,)은 필드**에서 초기화 하자
    - 코드간결, **null 문제에서 안전**
  - 의존성 주입(DI)은 필드 주입대신에 **생성자 주입을 사용**하자.
    - 해석하자면 DI 중 @Autowired를 이용한 Field Injection보다는 **@RequiredArgsConstructor와 final**을 이용한 Constructor Injection을 사용하자라는 의미
  - **엔티티 매핑**
    * 연관관계에서 권장하는것은??

      * **N:1,1:N** 의 경우 "일대다 관계" 보다는 **"다대일 단(or양)방향"** 권장
        * **"다"** 쪽이 주인이 되는건 객체 지향보단 테이블 지향이 강하지만 그래도 이것을 권장
      * **1:1** 의 경우 **"주 테이블 외래키 단방향"** 권장
        * 주 테이블 외래키 단방향 - 단점 : 값 없으면 외래 키에 null 허용
        * 대상 테이블에 외래키 양방향 - 단점 : 무조건 즉시로딩
    * **"다대다" 사용금지** => **"일대다", "다대일"**로 풀어서 사용
      * 중간 테이블도 "엔티티"로 만드는걸 권장
    * **"양방향" 연관관계 코드로 작성시 TIP**
      * **연관관계 편의 메서드 + mappedBy** 를 세트로 항상 같이 작성 (개인적인 생각)
    * **상속 매핑**은 **`일반적인 전략, Mapped Superclass`** 을 사용
      * **일반적인 전략** : JOINED, SINGLE_TABLE 방식이 유명한데 보통 **JOINED 방식을** 선호
        * 혹시나 테이블이 너무 단순하다면 SINGLE_TABLE 을 사용 -> 조인이 없어서 속도 빠름

      * **Mapped Superclass 전략**
        * 상속 매핑으로 선언된 클래스를 상속받게 되면 해당 상속 내용을 전부 테이블에 넣을 수 있다.

- **엔티티의 메서드 TIP : 연관관계, 비지니스, 생성, 조회 메서드 권장**

  - **연관관계 메서드** 사용 권장(양방향에 적극권장!!)

    - **양방향**의 관계에 주로 사용되는 형태(아래는 예시)

      - (가정) Order와 OrderItem는 1:N이고 외래키 가진 주인은 OrderItem 인 상태이다.
      - (1) 주인인 OrderItem가 Order를 접근하는건 **단방향**이므로 **바로** 접근 가능
      - (2) Order가 OrderItem를 접근하는건 **양방향**이므로 **조금 돌아가서** 접근 가능
      - **(3) Member->Order를 Order->Member 접근처럼 간단히 사용하기 위해 연관관계 메서드를 사용**

      ```java
      // (1) OrderItem->Order 접근 예시 => 단반향
      orderItem.getOrder(); // orderItem에서 order정보 접근 모습
      
      // (2) Order->OrderItem 접근 예시 => 반대방향 (양방향)
      order.getOrderItems().add(orderItem); // order에 orderItem추가 목적
      orderItem.setOrder(order); // 두줄 필요 (중요!!)
      
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
      

  - **비지니스 메서드** 사용 권장

    - Service 파트가 아닌 Entity파트에서 비지니스 로직 구현이 가능할 것 같은 경우에는 Entity에서 개발을 적극 권장(=**도메인 모델 패턴**) => **장점 : 좀 더 객체 지향적인 코드**

      ```java
      // 간단히 엔티티에서 구현 가능하면, 서비스가 아닌 엔티티에서 로직 구현(객체지향적)
      // 재고 추가 함수 (비지니스 로직)
      public void addStock(int quantity) {
          this.stockQuantity += quantity;
      }
      // update 로직의 경우도 충분히 가능
      ```

  - **생성 메서드** 사용 권장

    - 목적 : 엔티티에있는 다양한 속성들을 이 생성메서드 **하나로 간편**히 다 적용 & **무분별한 엔티티 생성을 막기** 위함

    - **`@NoArgsConstructor(access = AccessLevel.PROTECTED)` 를 사용!**

      - 기본 생성자의 접근 제어를 PROTECTED로 설정해놓게 되면 **무분별한 객체 생성에 ide 상에서 한번 더 체크할 수 있는 수단**
        - Protected 접근자는 반드시 **같은 패키지만 허용!!** - 상위, 하위 패키지 전부 불가

      - 외부에서 생성자 사용 위해서는 `public static Order createOrder(...)` 형태로 구현 필수!

      ```java
      // 기본생성자 Public->Protected
      @NoArgsConstructor(access = AccessLevel.PROTECTED) 
      public class Order {
      	// ...
          
          //==생성 메서드==// => 수많은 정보 한번에! 그리고 public static 필수
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
      // main 함수 내부 가정
      Order order1 = Order.createOrder(member, delivery, orderItems); // 외부 가능
      order1.setDelivery(delivery2); // 정상
      
      Order order2 = new Order(); // 외부에선 이게 안된다는것 (Protected:같은 패키지만!!)
      order2.setDelivery(delivery2); // ide상에서 에러 발생
      ```

  - **조회 메서드** 사용 권장

    - 조회 로직의 경우에도 간단하므로 엔티티에서 개발을 권장

      ```java
      //==조회 로직==//
      /** 주문상품 전체 가격 조회 */
      public int getTotalPrice() {
          return getOrderPrice() * getCount(); // 가격 * 수량 = 주문상품 가격
      }
      ```

<br><br>

## 중요한 엔티티의 조회

**엔티티 조회 권장 순서**

1. **엔티티 조회 방식**으로 우선접근 : **지연로딩!!**
   1. **일반 엔티티** 최적화 : 페치조인으로 쿼리 수를 최적화
   2. **컬렉션** 최적화
      1. 페이징 필요O : `hibernate.default_batch_fetch_size` , `@BatchSize` 로 최적화
      2. 페이징 필요X : 페치조인 사용
2. 엔티티 조회 방식으로 해결이 안되면 DTO 조회 방식 사용
3. DTO 조회 방식으로 해결이 안되면 NativeSQL or 스프링 JdbcTemplate 활용

<br>

**참고) 엔티티 조회 에러 해결**

* **엔티티를 외부에 노출하면서 발생하는 문제들이라서 사실상 이부분을 알 필요는 없다.**
* @JsonIgnore : **양방향 무한 반복의 문제**를 해결
  * 하지만 지양하고 **DTO** 방식으로 해결을 지향
* Hibernate5Module을 @Bean 등록 까지 해주면 **Lazy 문제**도 해결 
  * 하지만 이 또한 지양하고 **LAZY 강제 초기화**로 다 해결
  * 예로 전체 엔티티를 LAZY로 개발한 후 Order를 조회할 때?? 단, Order 엔 Member가 있음.
  * 이때, LAZY 강제 초기화를 안하면 Member를 null로 나타낸다는게 **LAZY 문제**라는것.
    * **해결방안**으로 **`order.getMember().getName()` 같은 코드를 추가해야 LAZY 강제 초기화**를 진행
    * 이 경우 Member를 select하는 **쿼리를 추가로 전송**하는 행동을 해준다. **이를 막으려면??**
    * **단, 이 경우엔 `fetch join` (`distinct`도 사용 권장) 을 꼭 함께 사용해줘야 추가 전송하는 쿼리를 막고 1개의 쿼리만으로 모든걸 해결 할 수 있다. => 매우중요!!**
      * **하이버네이트6 버전 부터는 자동으로 distinct를 적용해주고 있다고 함!!**

<br><br>

## JPQL(JPA) TIP - Fetch Join 중요

**JPQL TIP**

* **느낀점** 

  * JPQL 작성한 경우 DB 테이블의 필드명들로 쿼리문에 사용하는게 아니라,
  * Java 파일로 만든 엔티티 파일에 선언된 필드명으로 쿼리문에 작성해야 하는듯 하다.

* **반환 방식**

  * TypeQuery: 반환 타입이 명확할 때 사용
  * Query: 반환 타입이 명확하지 않을 때 사용
  * **Dto : QueryDSL 사용시 패키지 명까지 없앨 수 있음 (우선 이걸로 자주 사용하자)**
    * `List<MemberDTO> result = em.createQuery("select new jpql.MemberDTO(m.username, m.age) from Member m", MemberDTO.class)`
  * **query.getResultList(): 결과가 하나 이상일 때, 리스트 반환**
  * query.getSingleResult(): 결과가 정확히 하나, 단일 객체 반환

* **쿼리문도 조건식 존재(case...then...else...end)**

  * `select case when m.age <= 10 then '학생요금' when m.age >= 60 then '경로요금'  else '일반요금' end from Member m`

* **페치징 API**

  * `setFirstResult(int startPosition)` : 조회 시작 위치 (0부터 시작)
  * `setMaxResults(int maxResult)` : 조회할 데이터 수

* **fetch join** : 객체 그래프 유지할 때 사용시 효과적이고, **"즉시로딩"** 사용을 의미

  * 여러 테이블을 조인해서 엔티티가 가진 모양이 아닌 전혀 다른 결과를 내야 하면,   
    페치 조인 보다는 일반 조인을 사용하고 필요한 데이터들만 조회해서 DTO로 반환하는 것이 효과적
  * **fetch join**을 **일반 join**으로 따라하려면 T.\*, M.* 로 두 테이블 모두 조회 ("즉시 로딩")
    * **`XToOne` 문제없음, `XToMany` 는 Distinct 함께 사용(컬렉션이라 중복들이...)**
    * 근데, **하이버네이트6 부터 Distinct 자동 적용**해주는듯?! 아직 테스트 못해봤다.
  * **페이징 API - `XToOne`는 바로 페치조인O, `XToMany`는 일반 select + BatchSize (페치조인X)**
    * **글로벌로 100정도 깔아두고 개발하자**
  * **페치 조인 대상에는 별칭X** - 유일하게 연속으로 join 가져오는 경우에만 사용
  * **둘 이상의 컬렉션은 페치 조인X**

* **경로 표현식 3가지** - 상태, 연관 필드(단일 연관, 컬렉션 연관)

  * 경로 표현식 : .(점)을 찍어 객체 그래프를 탐색하는 것

  * **일반 join 사용시 "묵시적 내부 조인" 이 아닌 "명시적 조인" 을 사용할 것**

  * 묵시적 내부 조인은 자동 inner join을 진행 -> 묵시적 내부 조인 비권장!

    ```java
    select m.username -> 상태 필드 (묵시적 내부 조인X, 탐색X)
      from Member m 
        join m.team t    -> 단일 값 연관 필드 (묵시적 내부 조인O, 탐색O)
        join m.orders o -> 컬렉션 값 연관 필드 (묵시적 내부 조인O, 탐색X)
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
  * **@BatchSize 관련과 구분!! - 여러 데이터 한번에 "조회" 연산**

* **동적 쿼리**는 **Querydsl** 을 권장

  * 자세한 내용은 강의보고 적어두자@


<br>

**JPA TIP + 추가정보**

* **중요!! - 영속성 컨텍스트**

  * **FK오류 방지**
    * id를 @GeneratedValue 로 지정했기 때문에 em.persist를 해야 id를 생성 해준다.
    * 따라서 em.persist를 안한 엔티티를 외래키로 쓸때 id가 없어서 FK오류가 발생!!

  * **추가 Update 쿼리(더티체킹) 방지**
    * FK오류를 방지하면서 코드를 짰으면 사실 문제없을텐데, 그게아니라면??  
      => 추가 update 쿼리가 나갈 수 있다.
    * 예로 em.persist를 하지않은 엔티티를 먼저 외래키로 사용한 후에 persist한 경우  
      나중에 flush 할때 더티체킹(id가 생겼으니)으로 update 쿼리가 생성된다.
    * **따라서 그냥 FK오류 방지를 준수하면서 꼭 작성!**

  * **flush**
    * **em.createQuery** 같이 쿼리문 작성하는 것들이 **flush 동반**
    * **트랜잭션**은 당연히 **flush 동반**

  * **persist**
    * 우선 em(엔티티매니저) 사용하려면 **트랜잭션이 필수!!**(없으면 아래 에러)
    * em.save(=persist) : **(1)영속성 등록**, (2)쓰기지연 SQL저장소에 insert쿼리, (3)순차번호 구하기위해 select전송
      * 순차번호 구하는 **select 는 바로 전송**
      * 쓰기지연 SQL저장소의 insert 는 **flush 때 전송**

    * em.find : **(1)영속성 등록**, (2)찾는 id 인 값 select전송
      * **select 는 바로 전송** -> 당근 flush란건 아님. 다른것들 처럼 바로전송일 뿐

    * em.remove : **(1)영속성 해제**, (2)쓰기지연 SQL저장소에 delete쿼리, (3)삭제할 아이템 구하기 위해 select전송
      * 삭제할 아이템 구하는 **select 는 바로 전송**
      * 쓰기지연 SQL저장소의 delete 는 **flush 때 전송**

* **API에 제공할때 절대로 엔티티를 바로 나타내지 말것**

  * API 응답 스펙에 맞추어 별도의 **DTO를 반환** 권장

    * **setter를 최대한 사용하지않고, 파라미터를 줄여주는 효과**

    ```java
    // DTO 형태 예
    @Getter @Setter
    @RequiredArgsConstructor // 생성자
    public class UpdateItemDto {
        private final String name;
        private final int price;
        private final int stockQuantity;
    }
    ```

* **준영속 엔티티를 수정할 때 Merge방식보다는 Dirty Checking 방법 권장**

  * 변경 감지 기능(Dirty Checking)은 **Flush 할때 발생**

    * **flush 자동 호출은 3가지 경우 - commit 시점, query 날리는 시점, 강제 em.flush()**
      * flush는 1차캐시 지우지는 않음(내생각)
      * persist는 영속성 컨텍스트로.. clear() 는 비워줌.. commit은 1차캐시 지움
    * **참고로 "트랜잭션-@Transa..." 사용시 "로직 종료때 commit" 이라서 flush가 자동 호출됨!**
      * **또한, em관련 쿼리들 실행할때 자동 flush()**
      * **특히, persist 할때 자동 ID select도 따로 함.**

  * **영속성 컨텍스트를 사용하는 로직으로 작성해서 더티 체킹이 발생하도록 하자**

    * **update 예시**
    
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

* **JSON 반환시 꼭 마지막에 객체로 감싸서 반환**

  * 반드시 `{ 데이터 }` 형태로 만들기 위함

* **DTO와 DI(의존성 주입) 구분할 것**

  * **스프링에서 DI(Dependency Injection)**는 객체를 new로 직접 생성하는것이 아닌 **스프링 컨테이너(외부)에서 객체를 생성해서 주는 방식**을 의미
  * **DTO(Data Transfer Object)**는 **계층 간 데이터 교환을 하기 위해 사용하는 객체**로, 로직을 가지지 않는 순수한 데이터 객체
    * 메소드는 주로 getter, setter만 가짐

* **(최적화1)"캐시메모리" 사용 최적화 -> 서비스 계층에서 사용(트랜잭션 쪽)**

  * **[잘 정리한 사이트](https://adjh54.tistory.com/m/165) -> 사용법 다양함**
  * **게시물 삭제, 수정, 추가에 @CachePut사용, 조회에 @Cacheable 사용함으로써 간단한 최적화 가능**
  * **왜?? 사용하나??** 
    * CSR같은경우 서버에서 API로직으로 JSON같은 데이터 넘겨주면 Client에서 React였으면 Redux, React-Native였으면 AsyncStorage 등등으로 기록해서 사용하므로 "데이터존재" 하면 API호출을 따로 하지않을 것
    * 그러나, SSR같이 서버에서 구현할 경우 Thymeleaf에 React같은 Redux 같은기능이 없기때문에 서버단에서 완전히 해결해줘야 한다.
      * **이때, "캐시메모리"를 활용해서 해결이 가능하다는 것**
      * 서버 메모리에 저장하기 때문에 DB에 쿼리문 날릴필요 없기때문
  * 한마디로 성능개선을 위해 "캐시"를 사용하고, DEVOPS 정리한 md파일에서 자세히 확인!

* **(최적화2)DB의 Limit, offset 을 활용한 "페이징"을 더 간단히 하는법**

  * **JPQL**에서는 Limit와 Offset 키워드는 사용 불가하고 **setFirstResult(), setMaxResults()** 를 사용해야 함.

  * 또다른 방법은 제공해주는 클래스 사용 -> **Pageable 클래스 활용**

    ```java
    public List<Item> findAllWithPage(int pageId) {
        return em.createQuery("select i from Item i", Item.class)
            .setFirstResult((pageId-1)*10)
            .setMaxResults(((pageId-1)*10)+10)
            .getResultList(); // 해당 페이지(pageId) 데이터 조회
    }
    ```

  * **왜?? 사용하나??**

    * **(SSR 가정) 1000개 게시물을 1page에 10개씩 보여주는 구조를 만든다면??**

      * 캐시메모리 사용 시 오히려 캐시메모리 사용량과 갱신에 많은 오버헤드 우려
      * 대신 전체 게시물을 캐시메모리에 기록하는게 아니라 **"페이지별로" 캐싱**

    * 페이지별로 url(?page=1) 접근하면 해당 페이지별로 데이터를 가져올거고 이 데이터를 **@CachePut로 기록하고, @Cacheable로 조회**

      * 예로 `@CachePut(value = "posts", key = "#pageId")` 이런 형태

        * posts를 저장된 구간(키값)으로 보면되고, #pageId 를 파라미터로 들어온 pageId 속성값으로 매핑되며 해당 값을 키값으로 메모리에 기록
        * 이 때문에 pageId로 이 값을 바로 찾을수도 있음

      * **그럼 page를 하나하나 100개 전부 접근하면 결국 1000개 데이터가 전부 캐시메모리에 기록되고, 오버헤드가 우려되지 않는가???**

        * 캐시 크기 선언해서 해결

          ```properties
          # application.properties
          spring.cache.cache-names=posts
          spring.cache.caffeine.spec=maximumSize=100 # 캐시 사이즈 설정 (예시로 최대 100개의 페이지를 캐시로 관리)
          ```

      * **그럼 게시물이 삭제되거나 수정되면?? 특히 삭제되면 페이지별 데이터 10개 구성한것도 9개가 되고 갱신도 되어야할거고 그럴텐데 이건 어떻게 해결할건데??**

        * 사실 게시물 순서가 보장되어야 하는 경우에는 캐시 삭제 후 갱신된 데이터로 캐시

  * 한마디로 성능개선을 위해 "페이징"을 사용하고, DEVOPS 정리한 md파일에서 자세히 확인!

* **최적화 관련은 반드시 DEVOPS 정리한 md파일을 볼 것**

<br><br>

## 스프링 부트의 어토네이션들

**엔티티**

* @Entity : 스프링빈 자동 등록
* @Inheritance(strategy = InheritanceType.SINGLE_TABLE) : 상속을 **SINGLE_TABLE** 전략 사용선언
  * @DiscriminatorColumn(name = "dtype") : 상속의 **부모** 구분
  * @DiscriminatorValue("A") : 상속의 **자식** 구분
* @Getter @Setter : getter, setter 제공 -> lombok
* @Id @GeneratedValue : PK 할때 세트로 활용 -> @ID:PK, @GeneratedValue:자동숫자생성
* @Table(...) @Column(name = "member_id") : 테이블명, 컬럼명 매핑
* @ManyToOne(fetch=LAZY) : 지연로딩(LAZY), 다대일 관계 표시
  * @JoinColumn(name = "member_id") : 외래키 사용 - 주인이 할 일
  * @OneToMany(mappedBy = "member") : 주인은 Order라서 Order의 member를 의미
  * @JsonIgnore : 양방향 무한 반복의 문제를 해결 -> 안쓸거임.
    * **하지만 지양하고 DTO 방식으로 해결하는걸 지향한다.**
* @Embedded : JPA 내장 타입 쓸 때 사용
  * @Embeddable : JPA 내장 타입 선언

<br>

**리포지토리**

* @Repository : 스프링빈으로 등록, JPA 예외를 스프링 기반 예외로 예외 변환
* @PersistenceContext : 엔티티메니저( EntityManager ) 주입
* @PersistenceUnit : 엔티티메니터팩토리( EntityManagerFactory ) 주입
* **`@RequiredArgsConstructor` : lombok의 어노테이션인데 이것도 `엔티티매니저` 주입을 제공해주므로, 이것과 `final` 조합으로 리포지토리, 서비스 둘다에서 활용하는것을 권장**
  * 예를 들면, @Repository 로 스프링 빈에 등록한 레퍼지토리를 다른 코드에서 사용하고 싶을때 "주입" 을 통해서 사용할 수 있다.
  * @Autowired를 생성자위에 선언하면 "생성자 주입" 으로 사용할 수 있고, 필드위에 선언하면 "필드주입" 으로 사용할 수 있다.
    * 여기서 **"생성자 주입"** 관점이 **@RequiredArgsConstructor** 로 대체 가능하다.
    * 자동으로 생성자 생성해서 제공해준다.
* 보통 만드는 DB와 연관 메서드 : save(), findOne(), findAll(), findByName() 등등...
  * `EntityManager` 의 함수들은 잘 구현되어있어서 `find()` 함수를 쓸때 값 없을때 null로 잘 반환해줌
  * **우리가 직접 쿼리문 날려서 `find()` 함수를 구현할 때는 이 null 처리를 잘 해줘야한다는점**
    * **TIP : `getResultList()` 형태로 (즉, List) 값을 받으면 null처리가 매우 간단**

<br>

**서비스**

* @Service : 스프링빈으로 등록
* @Transactional : 트랜잭션, 영속성 컨텍스트
  - readOnly=true : 데이터의 변경이 없는 읽기 전용 메서드에 사용, **영속성 컨텍스트를 flush 하지 않으므로 약간의 성능 향상**(읽기 전용에는 다 적용)
  - 데이터베이스 드라이버가 지원하면 DB에서 성능향상
  - **이와 같은 이유로 `@Transactional(readOnly = true)` 로 사용 및 쓰기모드 필요할때면 해당 메소드단에 `Transactional` 을 또 선언해서 쓰기모드까지 사용**
    - 기본값이 `readOnly=false` 
* @Autowired : 필드 주입
  - **앞전에 언급한것처럼 `@Autowired` 대신에 `@RequiredArgsConstructor` 와 `final ` 를 활용**

- 보통 만드는 비지니스 로직 메서드 : join(), findMembers(), findOne() 등등...

<br>

**컨트롤러**

* @Controller : 스프링빈으로 등록, 핸들러 매핑 대상으로 기억

* @RequestMapping, @GetMapping, @PostMapping 등등..  : HTTP 매핑

  * **@RequestMapping을 클래스단**에 **@GetMapping, @PostMapping 을 메서드단**에 사용 권장
    * @RequestMapping은 **전역으로 경로** 지정하기 좋기 때문
    * @RequestMapping 은 GET,POST,PUT... 등 전부 허용하기 때문
  * @PostConstruct : 테스트용 데이터를 코드 실행하자마자 바로 넣을 수 있음 (테스트하기 수월!)  
    => 이름 그대로 생성자 생성된 후 시점으로 생각하자!

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
    - 백엔드뿐만 아니라 프론트에서도 userA값을 사용가능하단 점이다.
    - (이부분은 추측이지만, 자동으로 변수를 추가해서 같이 프론트로 반환되는게 아닐까)
      - 스프링의 Model 클래스는 브라우저의 쿠키처럼 프론트에 같이 넘어가는 클래스
      - 이 때문에 데이터를 주고받기 수월하단 장점을 가짐.
    - 물론, 햇갈릴수도 있어서 그냥 **Model을 항상 데이터 보내는 용도로 사용**하고,
    - **@PathVariable을 url로 받은 값을 사용하는 목적**으로 활용하는게 젤 좋아보임.

* @ModelAttribute("form")

  * model.addAttribute 에도 담기고, form서밋 때 html에 있는 form 데이터를 매핑해서 변수에도 자동으로 담아줘서 변수선언도 따로 할 필요 없음

  * 또한, @ModelAttribute 를 생략하고 바로 **HelloData** 가 와도 동일하게 가능

  * **단, 너무 생략하면 햇갈릴 수 있어서 조심하자**

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

  * **요청오는건 RequestBody, 응답으론 ResponseBody**

    ```java
    @ResponseBody
    @PostMapping("/request-body-json-v5")
    public HelloData requestBodyJsonV5(@RequestBody HelloData data) {
        log.info(data.getUsername());
        return data;
    }
    ```

    * **@RequestBody**를 활용할거면 꼭 Dto 형태 타입으로 인수 받을것(경험상)
      * **ObjectMapper 필요없이 자동**으로 바꿔줘서 편리
    * 요청으로 들어오는 json 데이터를 @RequestBody HelloData data 로 인해 Hellodata 객체로 바꾸고,
    * 반환 타입을 String이 아닌 HelloData로 하면 @ResponseBody 로 인해 return할때 응답body에 문자로 넣어준다 했는데 덕분에 json로 집어넣어준다.
    * 즉, json(요청)->객체->json(응답) 로 동작한다.

  * **(핵심!) 만약 @ResponseBody 가 없으면 뷰 리졸버가 실행 되어서 뷰를 찾아서 렌더링!!**

    * **참고로 @RestController 는 @ResponseBody 가지고있음!!**

* 뷰 반환, 데이터 반환 정리

  * **@Controller - View 반환**
  * @Controller + @ResponseBody - Data 반환
  * **@RestController - Data 반환**

* 쿠키 편리하게 조회 **@CookieValue**

<br>

**테스트 코드(TDD) -> 졸작에 테스트코드 반드시 참고!!**

* 참고) 졸작은 JUnit5, 우테코는 AssertJ 사용했음, **TDD는 테스트 주도 개발 방법론**

* **중요!! 컨트롤러 테스트할 때? -> MockMvc 활용 (perfom())**

  * MockMvc는 서버에 앱을 올리지않고 테스트용으로 시뮬레이션하여 MVC가 되도록 도와주는 클래스! -> WAS 역할을 톰캣 대신 해주는것!

    * **단위 테스트로써 컨트롤러 "만" 테스트** 할 것이기 때문에 DB에 쿼리 전송? 서비스나 레포지토리? 이런걸 신경쓰면 안된다!

      * 왜냐하면 서비스, 레퍼지토리 등등은 따로 `단위 테스트코드` 작성하기 때문!

    * 따라서 **MockMvc의 목적은 3가지**

      * request 잘 받는지 - **content()**
      * 서비스로 데이터 잘 전달 되는지 - **verify()**
        * times() 도 같이 많이 사용
        * 왜냐하면 여러번 서비스 불리는 경우 몇번 불리는지 times로 명시해줘야 TooMany 에러 방지

      * 서비스 반환값으로 response 잘 받는지 - **when, given 필수!**
        * when, given 으로 해당 서비스의 반환값을 직접 설정가능!
        * response(응답) 관련 검증 메소드는 매우 다양!
          * andExpect() - status(), view(), redirectedUrl(), model(), content(), jsonPath() 등등.. 응답 관련 메소드..
          * andDo() : 요청, 응답관련 전체 메시지 출력

    * **중요 어노테이션 - @WebMvcTest : 컨트롤러만 테스트, @MockBean : 가짜 객체 주입(실제 동작X)**

    * 예제

      ```java
      mockMvc.perform(
          post("/api/v1/members/register") // post or get
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
          .characterEncoding("UTF-8")
          .content(content)
      )
          .andExpect(status().isCreated()) // 예상 응답 status
          .andExpect(jsonPath("uid").value("12345")) // 응답 body 의 json
          .andDo(print()); // 전체 메시지 출력
      ```

  * **[참고1 - 메서드들 참고](https://velog.io/@doforme/Controller-%ED%85%8C%EC%8A%A4%ED%8A%B8-%EC%BD%94%EB%93%9C-%EC%9E%91%EC%84%B1%ED%95%98%EA%B8%B0), [참고2 - 메서드들도 참고](https://doongjun.tistory.com/71)**

  * **HttpServletRequest, HttpServletResponse 관련 테스트**

    * 이를 흉내내주는 `MockHttpServletRequest, MockHttpServletResponse` 을 사용!

* **@TestMethodOrder(MethodOrderer.OrderAnnotation.class)**

  * `@Order(1), @Order(2)` ... 로 테스트 코드 실행 순서 지정 가능
  * 주의 : Order 라는 클래스가 이미 import 중이라면 `@org.junit.jupiter.api.Order(1)`
  * 또한, `org.junit.jupiter.api` 패키지의 Order 를 사용한다는 점을 기억

* **`given, when, then` 예시**

  * given에 멤버 이름 설정
  * when에 서비스의 join함수 사용(회원가입 되는지 확인하는 것)
  * then에 결과를 보는것. 멤버이름이 잘 생겼는지 등등..(assert보통 씀!)

* **스프링 테스트 선언법**

  * Junit4 : @RunWith, @SpringBootTest 둘다 선언 - 클래스 단에
  * **Junit5** : @SpringBootTest 선언 - 클래스 단에
  * **이후 @Test** 를 메소드마다 선언하여 테스트!! - 메소드 단에
    * @RunWith(SpringRunner.class) : 스프링과 테스트 통합 -> **Junit4 이하만 사용**
    * @SpringBootTest :  스프링 컨테이너와 테스트를 함께 실행 (이게 없으면 @Autowired 다 실패)

* **@Transactional** : 반복가능한 테스트지원, 각각의 테스트를 실행할 때 마다 트랜잭션을 시작하고 테스트가 끝나면 트랜잭션을 강제로 롤백 (이 어노테이션은 테스트 케이스에서 사용될때만 기본값으로 롤백)

  * **롤백을 하기때문에 내부에서 굳이 영속성 컨텍스트 플러시를 안하는 특징**을 가짐
  * @Rollback(false) : **롤백 취소**
    * 롤백을 안하니까 flush까지 진행하므로 insert문 로그 확인가능
  * em.flush() 함수 사용 : **flush 진행**
    * 롤백은 건드리지않고 flush 진행하므로, 롤백은 그대로 진행하면서 insert문 로그 확인가능
  * **서비스 TDD 때 트랜잭션 선언안하면 서비스가 동작을 안하기 때문에 그냥 전역으로 써주고! 대신에 서비스 코드에 트랜잭션 있는건 꼭 확인**
    * 왜냐하면 TDD에 선언한 트랜잭션때문에 서비스에 트랜잭션이 없어도 동작하기 때문!

* `@AfterEach` 는 테스트 끝나면 실행되는 것인데, 이를 활용한다.

* `@BeforeEach` 는 테스트 시작전 실행되는 것인데, 이것도 활용할 수 있다.

  * **단!! 이 2개는 @Test 마다 수행된다는점을 꼭 인지!!**

* **print대신 assert비교 테스트**

  * `Assertion`을 이용하자. 이걸 사용해서 `assertEquals`함수 사용시 두개 인자가 동일한지 봐준다
    * 안동일하면 오류, 동일하면 아무일도 없음
    * `Assertions.assertEquals(member, memberRepository.findOne(saveId));`
    * `Assertions.assertThat(member).isEqualeTo(result);` 이것도 위처럼 사용된다.
    * `Assertions.assertInstanceOf(Character.class, character);` 어떤 객체인지 체크하기 좋음

* **예외 테스트**

  * (1)try, catch보다 간편하게 `assertThrows`를 사용해서 일부러 예외를 터트려서 테스트 하는것이 있다.

    * 아래 JUnit5 에 정리한 부분이 이 내용이다.

  * (2)또는 try, catch대신 `@Test(expected = IllegalStateException.class)` 를 선언하면 알아서 해당 예외 터질 때 종료해줌
    * 만약 해당 예외가 안터지면 그다음 코드들이 계속 실행됨. 그 코드는 아래 형태로 작성
    * `Assertions.fail("예외가 발생해야 한다.");` 예외가 안터져서 오히려 에러라고 로그를 남겨줌

  * **JUnit5**

    * `Assertions.assertNull, Assertions.assertInstanceOf, Assertions.assertEquals, Assertions.assertThrows` 등등 자주 사용

    * ```java
      // 특히 예외 처리 + 예외 메시지까지 확인법
      Throwable exception = Assertions.assertThrows(IllegalStateException.class, () -> {
          followService.join(findFollow); // 중복검증 예외 발생
      });
      Assertions.assertEquals("이미 팔로우 요청을 하셨습니다.", exception.getMessage());
      log.info("exception.getMessage() : {}", exception.getMessage());
      ```

  * **AssertJ -> Junit5에서 파생된것**

    * 

<br>

**ETC**

* @Slf4j : 로그를 바로 log로 사용 가능
  * **Log Level : error > warn > info(기본값) > debug > trace**
  * 보통 debug를 "개발서버용", info를 "운영서버용" 으로 사용
  * 기본값이 info라서 debug로 개발서버 프로필에 따로 설정 해주자
* @NotEmpty("에러 메세지 관련")
* @Data -> 롬복
  * @Getter, @Setter, @ToString, @EqualsAndHashCode, @RequiredArgsConstructor 적용
* @SpringBootApplication : 톰캣 내장(서버)
* @Valid + @NotEmpty(message="회원 정보필수") 이런식으로 같이 사용
  * 예전꺼라서 의존성 추가 필수
  * `implementation 'org.springframework.boot:spring-boot-starter-validation'`
* **@AllArgsConstructor : 생성자 대신 만들어줘서 필드만 선언**
  * **참고로 EM 생성자 주입 방식인 `@RequiredArgsConstructor` 와 햇갈리지 말것**
* @Value : application.properties에 선언한 변수 사용

<br><br>

## 리팩토링

**AOP(공통 해결 관심사)**

* **일반 상황**
  * 만들 때 : **@Aspect** 사용 및 스프링 빈에 등록 필수
  * 사용할 때 : **@Around**로 원하는 곳에 적용
  * 동작 : 프록시 객체 생성 -> 실제 객체 생성 의 흐름
* **웹의 경우**
  * **서블릿 필터 보다는 "스프링 인터셉터"** 사용 권장 및 **ArgumentResolver** 활용 권장
    * **ArgumentResolver** 를 통해서 공통 작업할 때 컨트롤러를 더욱 편리하게 사용 가능
      * `HTTP 요청 -> WAS -> 필터 -> 서블릿 -> 인터셉터1 -> 인터셉터2 -> 컨트롤러`
        * 서블릿(=Dispatcher Servlet) 과 컨트롤러(핸들러) 사이에는 사실 핸들러 어댑터가 동작을 하며, 
        * 이때 "**ArgumentResolver**" 가 중간에 있어서 이를 거치고 "**컨트롤러(핸들러)**" 가 동작
        * **ArgumentResolver 덕분에 수많은 애노테이션으로 만든 데이터들을 컨트롤러(핸들러)로 정상 전달**
      * EX) @Login 애노테이션 만들어서 "멤버 정보" 객체 반환 이런게 가능!
      * 기존 : `@SessionAttribute(name = "loginMember", required = false) Member loginMember` 
      * **적용 : `@Login Member loginMember`** 
  * **"인터셉터" 는 "필터" 보다 더 많이 호출** 
    * **"로그인 인증" 을 예시로 구현 가능**
      * `preHandle` 에 "세션정보(쿠키)" 인증을 시도 및 실패시 다시 "로그인창" 으로 이동
      * 이를 다양한 **URL 패턴으로 적용 가능** - 적용URL, 미적용URL 구분도 간편

<br>

**기능 확장법**

* 스프링은 `WebMvcConfigurer` 를 상속받아서 스프링빈에 등록 후 기능을 확장한다.
  * @Bean 이나 @Configuration 선언
* **예로 인터셉터, ArgumentResolver** 도 새로 구현한 후에는 이곳에 등록해서 확장해야한다.

<br>

**예외처리 - Spring Exception**

* **html**
  * **자동으로 에러에 필요한 로직을 등록**하므로 바로 활용가능
  * `ErrorPage, BasicErrorController` 자동 등록 및 `/error` 경로로 기본설정
  * `BasicErrorController` 는 `ErrorPage` 에서 등록한 `/error` 를 매핑해서 처리하는 컨트롤러
* **API**
  * API는 html보다 예외처리가 세부적이므로 `ExceptionHandlerExceptionResolver` 로 해결
  * 즉, **자동 등록한 에러 로직을 사용하지 않고** `@ExceptionHandler, @RestConrollerAdvice` 사용
    * **`@RestControllerAdvice` 를 통해서 컨트롤러를 "기존코드, 예외코드" 나눠서 분류 가능**

<br>

**검증 - Validation (의존성 추가 필수)**

* **검증과 예외 예시**
  
  * 예외처리 예시 => ex: String 타입에 int가 넘어온 에러를 처리
  * 검증 예시 => ex: 0~9999 숫자범위"를 지정

* **예외란**
  
  * 가장 좋은 예외는 **컴파일 예외**, 그리고 **애플리케이션 로딩 시점에 발생하는 예외**
  * 가장 나쁜 예외는 고객 서비스중에 발생하는 **런타임 예외**
  * 검증기 사용시 **앱 로딩 시점 예외로 나타내 줄 수 있다는 장점!**
  
* **검증이란** Form 데이터같은 것들이 POST 요청왔을때 원하는 "검증"을 진행하는 것  
  
  * HTTP 요청 **"Form데이터, URL파라미터" 는 "검증"** 만으로 충분 - @ModelAttribute
  * HTTP 요청 **"API" 는 "검증 + 예외처리"** 까지 필요 - @RequestBody
    * 또한, **API의 경우에는 여기서 사용한 메커니즘들을 활용하되 메시지는 꼭 API스펙에 맞춰 잘 반환**해주면 됨 (참고로 타임리프는 그냥 th문법으로 알아서 잘 가져옴)
  
* **"@Validated + {HTTP요청 + BindingResult} + 검증 애노테이션 + errors.properties" 사용**

  * **기존 엔티티인 Item 객체보단 DTO에 "검증" 사용하는 구조를 사용하겠다**

    * **참고) DTO는 컨트롤러에서 파라미터 받을때 원본 엔티티 대신 사용(안전위해),** 
    * **Model 에 담아서 html에 넘겨줄땐 당연히 DTO 아니여도 상관없음(안전하므로)**
      * 응?? 그런가?? 햇갈리네

  * @Valid, @Validated 둘다 사용가능하나 **@Validated**가 추가한 라이브러리 사용 지원

  * **{HTTP요청(@ModelAttribute or @RequestBody) + BindingResult}** 는 꼭 이순서대로 파라미터에 작성

  * **[검증 애노테이션 모음](https://docs.jboss.org/hibernate/validator/6.2/reference/en-US/ 
    html_single/#validator-defineconstraints-spec)** => DTO로 만든 도메인에 원하는 "검증" 작성!

  * @NotNull(message ="...") 이런식으로 메시지 바로 설정도 가능 / @Pattern(정규식)도 자주사용

  * **BeanValidation의 메시지 찾는순서**는 `errors.properties` 를 먼저 찾고 `검증 애노테이션의 message 속성` 사용

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

  * `th:field, th:errors, th:errorclass` 문법을 주로 같이 활용
    * `<div th:errors="*{id}"...` 등 이런형태로 바로 사용!! 에러시 해당 태그 출력!!
    * @ModelAttribute("item") 매우중요!!
      * 예로 th:object를 item 로 사용하면 반드시 Model에 "item"으로 담기게끔 해줘야함.
      * 그런데 AddItemDto form 이런건 자동으로 Model에 "AddItemDto"로 담기기 때문에 위 방식을 사용!!

<br>

**(중요)"메시지, 국제화 기능"**

* `application.properties` 에 `spring.messages.basename=message` 를 추가!!
  * 이후 `messages.properties` 에 공통 관리할 messages에 담을 내용을 세팅
* 여러개 추가할거면?? 
  * 예로 `errors.properties` 추가한다고 하면 `spring.messages.basename=message, errors` 이렇게 이어적으면 됨

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

# Thymeleaf 로 웹 개발 TIP 모음

**버전.. 환경설정 등 기초적인건 생략**

<br>

## Thymeleaf 문법

**타임리프 문법**

* 핵심 : 서버로 실행(뷰 템플릿 사용)하면 타임리프 문법들이 적용해서 동적으로 변경!
  * 스프링 부트는 "뷰 리졸버" 를 자동 등록하는데, 이때 설정파일에 등록한  `spring.mvc.view.prefix , spring.mvc.view.suffix` 정보를 사용해서 등록한다.
  * "뷰 리졸버" 에 필요한 "경로" 를 설정하는 부분인데 요즘 Thymeleaf 는 이것도 자동으로 등록해줘서 설정할 필요가 없다.
    * 혹시나 JSP 사용할 경우에는 이부분 기억해두자.
* **타임리프 사용 선언**
  * `<html xmlns:th="http://www.thymeleaf.org">`
* 속성 변경

  * `th:href="@{/css/bootstrap.min.css}"`
  * `th:onclick="|location.href='@{/basic/items/add}'|"`
  * `<td th:text="${item.price}">10000</td>`
  * `th:value="${item.id}"`
  * `th:action`
  * ... 등등 매우 다양
* **URL 링크표현식 - @{...}**
  * `th:href="@{/css/bootstrap.min.css}"`

  * `th:href="@{/basic/items/{itemId}(itemId=${item.id})}"`
  * 심화) `th:href="@{/basic/items/{itemId}(itemId=${item.id}, query='test')}" `
    * 생성된 링크: `http://localhost:8080/basic/items/1?query=test`
  * **간편) `th:href="@{|/basic/items/${item.id}|}"`**
    * **리터럴 대체문법 적용가능 => 이거 쓰자**
* **리터럴 대체 - |...|**
  * 타임리프에서 문자와 표현식 등은 분리되어 있기 때문에 더해서 사용해야 한다.
    * `<span th:text="'Welcome to our application, ' + ${user.name} + '!'">`
  * 다음과같이 리터럴 대체문법을 사용하면, 더하기 없이 편리하게 사용할 수 있다.
    * **`<span th:text="|Welcome to our application, ${user.name}!|">`**
    * **`th:onclick="|location.href='@{/basic/items/{itemId}/edit(itemId=${item.id})}'|"`**
* 변수표현식 - ${...}

  * `<td th:text="${item.price}">10000</td>`
* 반복출력 - th:each

  * `<tr th:each="item : ${items}">`
  * 컬렉션의 수만큼 `<tr>..</tr>` 이 하위 테그를 포함해서 생성된다.
* **조건문 - th:if or Default**
  * **th:if 문들은 false인 경우 아예 태그를 렌더링을 안해서 그럴경우 사용!!**
    * `<h2 th:if="${param?.status}" th:text="'저장 완료'"></h2>`
    * `<h2 th:unless="${param?.status}" th:text="'저장 실패'"></h2>`

  * **그외 + Default 경우**
    * `th:text="|B1 ~ B${(totalCount!=null) ? (totalCount/10+1) : '??'}F|"`
    * 또는 Default 활용 : `th:text="|B1 ~ B${(totalCount) ?: '??'}F|"`
      * Default는 자동으로 null을 잡아주지만, 
      * null/10 이런건 불가해서 이런경우는 위처럼 it-then-else 사용

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
* **블록 - `<th:block>`**
  * `<th:block>` 는 타임리프가 제공하는 유일한 자체 "태그"
  * **렌더링 할때는 아예 태그가 삭제**
* **fragment, JS**
  * fragment : 코드 재사용
  * JS : javascript 에서 사용 가능
* **`<input>과<label>` 에서 th:for로 id값 연결 하는 편 -> 보통 form 에 잘 구성**
  * 동적 id 인식 - `#ids.prev()`
* **`<table> <tr> <td> <th>` 형태로 데이터 잘 표현** 
* **`<div>` 로 데이터 잘 표현한 경우 `<th:block>` 같이 사용하면 깔끔**
* **젤 중요!! `th:field` 는 "검증"에 매우 유용해서 그냥 사용가능하면 무조건 사용**
  * value에 값 자동 넣어주며, id, name 자동 생성 및 체크박스도 자동체크 등 - **input**에 주 사용
  * `*{...}` : 선택 변수 식으로써 `th:object` 에서 선택한 객체에 접근 활용
  * **그냥 여러개 쓸땐 th:object로 관리쉽게 쓰고, th:field는 input이나 체크박스에 사용하자**

<br>

**타임리프 + 스프링 통합 문법**

* **th:object, th:field, *{itemName} 활용**
  * Form과 함께 Input, 체크박스, 라디오버튼, 셀렉트 박스에서 활용
  * TIP) addForm, editForm 이렇게 2개 따로 만드는게 개발하기 수월

* **(중요)"메시지, 국제화 기능"**
  * `application.properties` 에 `spring.messages.basename=messages` 를 추가!!
* 여러개 추가할거면?? 예로 errors.properties 추가한다고 하면 message, errors 이렇게 이어적으면 됨
  * 이후 `messages.properties` 를 추가해서 messages에 담을 내용을 세팅
* 타임리프로 사용 예시 : `<h2 th:text="#{page.addItem}">상품 등록</h2>` `
* **(참고) nullsafe**
  * `th:if="${errors?.containsKey('globalError')}"` 에서 ?를 통해 null 로 나타나므로 if는 false로 반환
  * ?가 없으면 null.containesKey... 로 에러
* "컨트롤러"에서 그냥 @GET 으로 페이지 로딩할때 item을 빈값이라도 선언해둬서 Model에 담아 반환하는걸 권장
  * **검증 실패 때 forward로 "자원 재활용"이 됨.**
  * HTML 코드도 더 깔끔 -  if문으로 null인지 확인할 필요없이 그냥 item을 타임리프 문법으로 사용하면 되기때문

<br><br>

## (2) Thymeleaf TIP

**Page 생성 기본과정**

* \<div> + Container 를 활용해서 전체적인 "페이지 틀" 을 먼저 작성
  * 단, container와 flex 같이 선언하지 말기

* fragment로 head, header, footer, scripts, modal 등등 작성 (scripts, modal은 관리편리)
* 데이터 간단히 표현 : `table, td, tr 등등` 또는 `div로 잘 구현`
  * `th:each` + `<th:block>` 도 적절히 함께 활용
* FORM 데이터 : `label, input, 체크박스 등등` 권장
  * th:field(name,id,value자동) 와 *{...} 랑 th:object(데이터 관리쉽게) 함께 사용 권장
  * `th:errors` 등등 도 함께 사용
* 문법 잘 활용
  * |...| : `<span th:text="|Welcome to our application, ${user.name}!|">`
  * @{} : 간편) -`th:href="@{|/basic/items/${item.id}|}"`

<br>

**css**

* 부트스트랩, J쿼리 기본 import + basic.css(전체 CSS) 정도는 깔고가면 편리
* **자주 사용하는 hover 의 주의점** : 태그의 style이 제일 우선순위 높으므로 이쪽에 color, background-color 등이 있으면 지워주고 따로 \<style> 태그나 css에서 진행할것
  * 특히 .page-link:hover 과 .page-link.active 처럼 hover은 ":", active는 "." 사용하는 등의 자잘한 실수를 유발할 수 있으므로 꼼꼼히 살펴볼 것

<br>

**PRG 패턴 적용(위에 PRG 정리한 내용 참고) - 무한 POST 방지**

* 추가로 forward 사용가능한 건 forward 형태로!
* Redirect, Forward 개념 인지하고 사용!!

**자원 재활용(forward) : 폼... 분리 가능한건 분리해서 작성 권장 - addForm, editForm**

* 이거때문에 굉장히 많은 삽질 ㅜㅜ
  * GET에 꼭 빈값이라도 엔티티 Model에 삽입 - th:object 함께 사용
  * POST에는 `@Validated @ModelAttribute("item") AddItemDto form, BindingResult bindingResult, RedirectAttributes redirectAttributes)` 이런식으로 파라미터 거진 필수
  * 장점은 검증실패시 html로 바로 return보내게 로직 작성시 서버단에서만 동작하는 forward 이기 때문에 자원 재활용!!

**검증 : 위에서 따로 정리했기때문에 꼭 참고**

* 참고) id에는 @NotBlank, pw에는 @Pattern(정규식="^[0-9]+") 등 추천!
  * 특히 pw는 클라에서도 따로 검증하더라도 Postman 같은걸로 접근 가능하기때문에 한번더 "검증"

<br>

**Model, @ModelAttribute("item"), forward, RedirectAttributes, UpdateItemDto, @PathVariable, @RequestParam**

* API 말고 여기 웹 개발 컨트롤러에서 주로 사용한 것들인데 꼭 동작을 전부 이해해둬야 함
* **Model** - **(1)html로 return**때 데이터 자주 넘겨줬음(forward로 볼 수 있음)
  * **(2)return을 forward:/ 형태로 동일 컨트롤러내에서는 다른 곳으로 요청가능(서버내에서)**
  * **(3)return을 html이 아닌 redirect로** 넘길시 새롭게 웹브라우저가 다시요청 하는것이므로 Model 값 사라져서 이땐 사용안하고 **RedirectAttributes 를 사용**
    * RedirectAttributes 로 데이터 넘겨줄수 있기 때문
  * **@ModelAttribute("item") UpdateItemDto form** - Post때 주로 사용하며 Form데이터 자동 매핑 후 Model에 item 이름으로 기록! 
    * **UpdateItemDto** 만 사용시 @ModelAttribute 과 동일하되 Model에 UpdateItemDto 이름으로 기록
    * form 데이터 넘길땐 UpdateItemDto 관련 데이터를 전부 서버로 form에서 넘겨줘야하며 이때 View에 안보여줄 데이터는 \<input> + hidden 으로 넘겨주면 간단하다.
* @PathVariable - URL 뒤의 값 바로 사용 (자주 사용)
  * @RequestParam - 쿼리 파라미터값 가져올때 자주사용 (기본값 설정도 가끔사용)

<br>

**application.yml**

* "프로필" 사용 - 개발용, 배포용 따로설정
  * gradlew clean build
  * "외부설정" 사용 -> 경로 등등
  * thymeleaf 캐시 사용X -> 실시간 reload
  * "메시지-국제화" 사용(messages.properties) -> thymeleaf와 연동 최고
  * 포트 8080(개발용), 포트 80(배포용) -> http는 80포트 기본값
  * 로그레벨 설정

```yaml
# default 프로필 -> 개발모드로 사용
spring:
  # thymeleaf 캐시 사용 X로 실시간 reload
  thymeleaf:
    cache: false
    prefix: file:src/main/resources/templates/ # thymeleaf 경로지정

  # H2 DB
  datasource:
    url: jdbc:h2:tcp://localhost/~/secret-art-typing-gallery/secret
    username: sa
    password:
      driver-class-name: org.h2.Driver

  jpa:
    hibernate:
      ddl-auto: create # DB 초기화 사용
    properties:
      hibernate:
#        show_sql: true
        format_sql: true

  # 메시지 - thymeleaf 연동
  messages:
    basename: messages

server:
  port: 8080
#  address: 223.39.212.163
  servlet:
    session:
      timeout: 30m # session timeout 30min

logging.level: # DB 쿼리 로그
  org.hibernate.SQL: debug
  org.hibernate.type: trace

my: # 외부설정 값
  datasource:
    imgPath: C:/images-spring/

logging:
  level:
    com.dau.secretarttypinggallery.controller: debug # 컨트롤러에서만 로그확인
--- # 프로필 구분자
# prod 프로필 -> 배포모드로 사용
spring:
  config:
    activate:
      on-profile: prod

  # thymeleaf 캐시 사용!!
  thymeleaf:
    cache: true
    prefix: classpath:/templates/ # 리눅스용(배포환경) 경로지정

  # MYSQL DB
  datasource:
    url: jdbc:mysql://localhost:3306/secret?useSSL=false&useUnicode=true&serverTimezone=Asia/Seoul
    username: secretUser
    password: 1234
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    hibernate:
#      ddl-auto: none # DB 초기화 사용 X
      ddl-auto: create # DB 초기화 사용

  # 메시지 - thymeleaf 연동
  messages:
    basename: messages

server:
  port: 8080 # 포트 포워딩으로 http(80) -> 8080 설정하면 "URL에 포트없이 바로 접속가능"
  servlet:
    session:
      timeout: 30m # session timeout 30min

my: # 외부설정 값
  datasource:
    imgPath: /var/www/images-spring/

logging:
  level:
    com.dau.secretarttypinggallery.controller: info # 배포는 기본값(=info) 사용
```

<br>

**타입 컨버터**

* DB에는 LocalDateTime(원하는..) 형태로 저장 후 HTML 출력때 원하는 "타입 컨버터" 사용
* ` @DateTimeFormat(pattern = "yy.MM.dd.HH:mm"), @NumberFormat(pattern = "###,###")` 등등 사용 가능
* 타임리프에 적용법 : `th:field=*{{...}}`\

* **(보충) 타입 컨버터**

  * **(1) 웹 - `@Requestparam, @ModelAttribute, @PathVariable` 스프링이 기본 지원**

    * 예로 `@PathVariable Long itemId` 는 자동으로 String->Long 타입변환

    * "확장 가능" 하고, "**애노테이션**"을 제공

      * **@DateTimeFormat**예시 : DB엔 LocalDateTime타입, Thymeleaf는 지정한 pattern 사용
      * "타임리프 사용법" - `th:field, ${{...}}`
        * **예로) `th:field="*{{date1}}"` 이런식으로 사용**

      ```java
      @Data
      static class Form {
          @NumberFormat(pattern = "###,###") // 타입 컨버터
          private Integer number;
      
          @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
          private LocalDateTime localDateTime; 
          // db엔 LocalDateTime 형태로 저장
          // Thymeleaf에선 지정한 "패턴"으로 출력
      }
      ```


  * **(2) HTTP API (@ResponseBody 등) - 의 경우 지원하지 않는다(HttpMessageConverter 는 "컨버전 서비스 적용 불가")**
    * **이 경우에는 `Jackson 같은` 라이브러리에서 포맷터를 찾아 사용**
    * JSON->객체,  객체->JSON 등등 쉽게 타입 변환 가능

<br>

**예외처리(HTML 페이지 기본제공 사용)**

* 웹 에러 처리의 경우에는 기존 "스프링 부트 기본제공" (BasicErrorController) 을 규칙에 맞게끔 사용
  * `/error` 경로의 html을 기본 접근
* **뷰선택 우선순위(BasicErrorController 가 제공하는 기능)**
  * **1. 뷰템플릿**
    * resources/templates/error/500.html
    * html resources/templates/error/5xx.html
  * **2. 정적리소스( static , public ) resources/**
    * static/error/400.html
    * resources/static/error/404.html 
    * resources/static/error/4xx.html
  * **3. 적용대상이없을 때뷰이름( error )**
    * resources/templates/error.html

<br>

**정적파일 전부 캐싱 + gzip압축 (속도개선)**

* gzip 압축 -> 보통 이미지나 동영상은 이미 압축되어 있는 상태가 대부분이라 HTML,CSS,JS 만 압축!!
* 단, 개발할땐 캐싱 주석해둘것. -> 캐싱때문에 이미지 바꿔도 적용안된걸로 착각할 위험이 있음

<br><br>

# 참고 개념

사용한 로그인 구현 방식도 정리했음..

<br>

## 스프링 라이브러리관련

**Spring과 JUnit 버전별 테스트 코드**

* **(1) Spring 2.xx + JUnit4(Test Code)**

  * `build.gradle	`

  ```groovy
  //JUnit4 추가(junit5로 자동실행 되기 때문) - 의존성 추가
  testImplementation("org.junit.vintage:junit-vintage-engine") {
      exclude group: "org.hamcrest", module: "hamcrest-core"
  }
  ```

  * `TestCode.java`

  ```java
  @RunWith(SpringRunner.class) // SpringRunner : Junit4
  @SpringBootTest // 스프링과 통합 테스트 - "빈" 등 스프링 사용시 필수
  public class ItemServiceTest { 
      @Test // 기본 테스트(필수)
      public void 조회() {} 
  }
  ```

* **(2) Spring 3.xx + JUni5(Test Code)**

  * H2 사용시 반드시 버전 업그레이드 + 자바 17이상

  * RunWith, JUnit4 등록이 없어졌다고 보면 됨.

  * `build.gradle`

  ```groovy
  // JUnit5 자동 사용!
  // 테스트 코드에서 lombok 사용하는 꿀 팁! -> 아래 의존성 추가
  testCompileOnly 'org.projectlombok:lombok'
  testAnnotationProcessor 'org.projectlombok:lombok'
  ```

  * `TestCode.java`

  * 참고 - 왜 @Transactional 이런건 바로 사용 가능한가?
    * @Transactional 은 "빈에 반드시 TransactionManager 가 필요" 
    * 스프링 부트는 자동으로 TransactionManager 등등 을 "빈에 등록" -> "자동 구성"

  ```java
  @SpringBootTest // 스프링과 통합 테스트 - "빈" 등 스프링 사용시 필수
  public class ItemServiceTest { 
      @Test // 기본 테스트(필수)
      public void 조회() {} 
  }
  ```

<br>

**`스프링부트 플러그인` 사용하면 "라이브러리 버전관리 자동화"**

* 물론 지원안하는건 직접 버전 등록 해야함

<br>

**web 라이브러리 없으면 바로 종료되기 때문에, 이때 `ApplicationRunner` 구현체로 자바코드 실행하는게 보통**

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

<br>

**jar vs war**

* **jar은 내부 톰캣 사용, war은 외부 서버 사용**으로 이해하면 됨
* 외부 톰캣에서 webapp 경로를 사용하는 편인데, 실제로 war은 사용가능하나 jar은 사용불가

<br><br>

## HTTP 중요지식

**redirect vs forward**

* redirect 는 서버에서 응답을 통해 클라까지 응답이 나갔다가 클라가 redirect 경로를 보고 다시 해당 경로로 서버에 요청하는 형태
* forward 는 서버 내부에서 일어나는 호출이므로 클라가 전혀 인지하지 못함
  * 따라서 URL은 처음 호출한 URL 그대로이며(클라는 인지못해서), 동일 Web Container인 페이지로만 이동 가능!
  * 단, request와 response는 공유

<br>

**PRG Post/Redirect/Get - POST를 무한한 재요청 문제 해결 패턴** 

* **웹 브라우저의 새로고침은 마지막 서버에 전송한 데이터를 다시 전송한다.**  
* **따라서 POST 적용후 새로고침을 하면 계속 POST 보내는 문제가 발생하므로 이를 Redirect를 통해서 GET으로 요청하는 방식으로 해결할 수 있다.**
  * Redirect를 사용해야지만 POST 보내는 URL을 벗어나기 때문!!

* **RedirectAttributes 추천**
  * Redirect 할때 Model처럼 파라미터를 추가해서 간편히 넘겨줄 수 있고, URL 인코딩 문제에서 자유롭다!
    * `"redirect:/basic/items/" + item.getId()` 는 인코딩 문제가 발생할 수 있는데,
    * `"redirect:/basic/items/{itemId}"` 는 인코딩 문제에서 자유롭다.
  * **특히 Status 정보를 파라미터로 넘김으로써 `th:if` 문법으로 "저장완료" 표시도 나타내는데 많이 사용한다.**
  * **따라서 Redirect 할때는 RedirectAttributes.addAttribute() 추천, html 반환 할때는 Model.addAttribute() 추천**
    * 리다이렉트가 발생하면 원래 요청은 끊어지고, 새로운 HTTP GET 요청이 시작된다.(브라우저에게 이 URL로 리다이렉트해!) 때문에 리다이렉트 실행 이전에 수행된 모델 데이터는 소멸한다. 따라서 리다이렉트로 모델을 전달하는 것은 의미 없다.**[출처]** [[스프링\] RedirectAttributes](https://blog.naver.com/allkanet72/220964699929)|**작성자** [페얼프인](https://blog.naver.com/allkanet72)
    * 즉, 리다이렉트시 새로운 HTTP GET 요청을 하므로 기존에 RedirectAttributes에 담아둔 데이터를 사용할 수 없음
      * 단, `redirectAttributes.addAttribute` 는 파라미터로 전송되므로 **@RequestParam(defaultValue = "")** 등으로 사용가능!!
      * `redirectAttributes.addFlashAttribute` 의 경우 불가능!! 파라미터 전송이 아니기 때문이며 세션에 저장해서 딱 한번 HTML에만 전송될 뿐!!

<br>

**Content-Type 헤더 기반 Media Type 과 Accept 헤더 기반 Media Type**

* text/html, application/json 같은 content-type 을 의미
* **요청때나 응답할때나 body를 사용할때는 필수로 존재 및 서로 맞게 요청해야 함**

<br><br>

## "로그인 구현 방식"의 기본 지식

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

<br><br>

## "파일 업로드, 다운로드" 기본 지식

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

<br><br>

## 배포

**포트생략법**

* 배포할 때 `http://localhost:8080` 이 아닌 `http://localhost` 로 접속법(포트생략법)
* http는 80포트를 기본값으로 사용 / 개발할 때 사용한 포트는 8080포트
* 따라서 **"포트포워딩"** 을 사용해서 **80포트 접속시 -> 8080포트로 변경**해주면 끝
* 예시
  * 등록 : `sudo iptables -t nat -A PREROUTING -i eth0 -p tcp --dport 80 -j REDIRECT --to-port 8080`
  * 조회 : `iptables -nL PREROUTING -t nat --line-numbers`
  * 삭제 : `iptables -t nat -D PREROUTING {number}`
