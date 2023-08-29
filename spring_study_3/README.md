# Intro..

**자바 ORM 표준 JPA 프로그래밍 - 기본편**

* 인프런 강의 듣고 공부한 내용입니다.
* **유용한 단축키**
  
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

<br>

해당 프로젝트 폴더는 강의를 수강 후 강의에서 진행한 프로젝트를 직접 따라 작성했습니다.

따로 강의 자료(pdf)를 주시기 때문에 필요할때 해당 자료를 이용할 것이고,

이곳 README.md 파일에는 기억할 내용들만 간략히 정리하겠습니다.

* 자세한 코드가 궁금하다면, 올려둔 프로젝트에서 코드확인(커밋 위주로 보면 보기 편할 것)
* 정리하다 보니 좀 많이 정리하는 감이..
* **참고 : 필자는 `jdbc:h2:tcp://localhost/~/spring_study_dbh2/test+` 에 test.db 생성했다!**
  * 추가로 `jpashop.db` 도 이쪽 폴더에 관리

<br><br>

# 요약

**이번 강의에서는 JPA에 관한 모든것을 배우기 때문에 내용이 많다.  
따라서 꼭 기억하고 넘어가면 좋을 부분들만 먼저 소개를 하고, 이후에는 실습한 프로젝트를 소개한다.**

* 참고로 JPA의 자세한 것은 PDF 자료를 참고
* +쿼리언어 문법까지 정리 => 이건 따로 md파일이나 아니면 그냥 아래에 이어서 작성

<br>

**네이밍 규칙**

* 테이블명 `ORDER or order`, 컬럼명 `order_id` 형태로 하자.
* JPA에서 엔티티 `ORDER or order`, 필드명 `orderId` 형태로 하자.  
  (스프링 부트는 자동으로 `orderId -> order_id` 로 컬럼명 찾아서 매핑)

**추가 규칙**

+ ENUM클래스는 `@Enumerated(EnumType.STRING)` 로 반드시 `STRING`으로 할 것!
+ 엔티티 설계 때 단방향 우선 개발(테스트)후 양방향 추가. (양방향은 설계에 아무영향 안끼침)
+ `임베디드 타입(값 타입)` 과 `상속-Mapped Superclass` 이것 두개를 잘 활용.  
  + 중복해서 적는게 많이 줄어든다.  
  + 필자의 생각엔 `값 타입` 은 조금 적은 중복 때, `상속` 부분은 거희 모든 엔티티에 속하는 중복 때 사용하면 괜찮겠다고 판단이 됨.


**DB 테이블 구조를 만든이후 JPA 연관관게는??**

* JPA 연관관계는 "일대다 관계" 보다는 **"다대일 단(or양)방향"** 권장
  * 객체지향보단, DB쪽 느낌이 강하더라도 "다대일 단(or양)방향" 을 권장  

* 또한, "다대다" 는 그냥 사용금지. 꼭, 중간테이블을(엔티티로 승격) 만들어서 **"일대다", "다대일"**로 권장  
  * 중간에 만드는 테이블은 따로 독립적으로 id값이 있는 엔티티로 만드는걸 추천.  
    (예 : `Member <-> Product` => `Member->{Order}<-Product`)  

* 마지막 "일대일"은 ?? 둘중에 하나 주인으로 사용. 기본은 **"주 테이블 외래키 단방향"** 가져가겠음.  
  * 주 테이블 외래키 단방향 - 단점 : 값 없으면 외래 키에 null 허용  
  * 대상 테이블에 외래키 양방향 - 단점 : 무조건 즉시로딩 ..


**DB 상속관계 매핑은 ??**

* 상속 관계 기본 전략으론 **"조인 전략"**을 우선 쓰고, 테이블이 너무 단순하다면 "단일 테이블 전략"을 가져가자.  
* 매핑 정보 상속으로는 `Mapped Superclass` 를 사용, 권장하는편.

**JPA 설계시 "즉시 or 지연 로딩" ??**

* 무조건 **"지연 로딩"** 으로 개발할 것

**JPA 설계시 cascade 사용 유무 ??**

* 관계가 완전 종속일때만 사용!!. 편리.

**JPA 설계시 값 타입(엔티티 생명주기에 의존)의 경우 ??** 

* `값 타입` 은 엔티티 클래스에 `private Long id;` 와 같은 필드라고 생각하면 된다.
* 꼭 불변 객체로 만드는게 옳다. 엔티티나 자바 기본타입이 아니라 "임베디드 객체" 같은게 해당된다.
* 이때, "임베디드 객체" 같은건 값 비교할때 equals 메소드 반드시 오버라이드 필요하단점 참고로 기억.  
* 다음으로, "값 타입 컬렉션" 과 같이 컬렉션을 값 타입으로 쓰려고 한다면 "일대다" 엔티티로 만들어서
  "고아 true + cascade.all" 형태로 만들면 동일하게 사용할 수 있다.
* <결론>
  * 값 타입은 정말 값 타입이라 판단될 때 사용!!
  * 식별자가 필요하고, 지속해서 값 추적 및 변경해야한다면 그것은 값타입이 아닌 "엔티티"  
    * "임베디드 객체" 는 값 타입 하나. `@Embed...` 로  
    * "값 타입 컬렉션" 은 값 타입 하나 이상. 이건 그냥 `"일대다 고아+cascade" 엔티티` 로

<br><br>

## JPA 란?

**객체를 관계형 데이터베이스에 저장하는 과정**

* 객체 -> SQL로 변환 -> `SQL적용` (RDB=관계DB)
  * 개발자는 `SQL 매퍼`
  * **JPA - Java Persistence API 를 활용**
    * 자바 진영의 ORM(객체 관계 매핑) 기술 표준

<br>

**JPA는 앱과 JDBC 사이에서 동작**

**JPA 발전 과정 : EJB(엔티티 빈) -> 하이버네이트(오픈소스) -> JPA**

**JPA는 인터페이스의 모음으로써 Hibernate, EclipseLink, DataNucleus 인 3가지 구현체(=JPA2.1)**

* 하이버네이트에서 많이 파생

<br>

**데이터베이스 방언 : 각각의 DB가 제공하는 SQL문법과 함수는 조금씩 다름**

* JPA는 특정 DB에 종속 X
* JPA는 하이버네이트의 Dialect를 통해서 설정한 `MySQlL or Oracle or H2 등`  DB에 맞게 방언이 설정 된다.

<br>

**결론 : JPA 덕분에 DB지향이 아닌 객체 지향으로 설계가 가능**

<br><br>

## JPA 구동 방식

**그림과 Main코드를 함께 보면 이해하기 좋다.**

<img src=".\images\image-20230407202821013.png" alt="image-20230407202821013" style="zoom:80%;" />

```java
public class JpaMain {

    public static void main(String[] args) {
        // persistence.xml에 name을 "hello" 로 지정했으므로 "hello" 로 매핑
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try{
			// DATA 변경 로직 작성
            
            tx.commit();
        }catch (Exception e) {
            tx.rollback();
        }finally {
            em.close();
        }
        emf.close();
    }
}
```

<br>

**한가지 얘기를 하자면, 트랜잭션은 Spring Boot가 제공하는 어노테이션 사용하면 @Transaction으로 간편히 쓸 수 있는데 현재 코드는 원래 트랜잭션이 이런 로직이라고만 참고하자.**

<br><br>

## 영속성 관리

**영속성 컨텍스트(논리적 개념)는 `엔티티를 영구 저장하는 환경`  을 의미한다!!**

* 비영속(new/transient), 영속(managed), 준영속(detached), 삭제(removed)

<img src=".\images\image-20230407205137615.png" alt="image-20230407205137615" style="zoom:80%;" />



<br>

**`em.persist(엔티티)` 단계가 아니라, `tx.commit()` 시점에 SQL문 쿼리 모아둔거를 한꺼번에 전송한다.**

* 참고로 `persist` 를 한 경우엔 1차 캐시에 저장을 하기에 다음에 같은 엔티티를 찾을땐 DB에 접근안하고 바로 캐시에서 가져올 수 있다.

<img src=".\images\image-20230407205318584.png" alt="image-20230407205318584"  />

<img src=".\images\image-20230407205339481.png" alt="image-20230407205339481" style="zoom:80%;" />

<br>

**더티 체킹을 하므로, 데이터 변경이 간단하다.**

* 스냅샷에 기존 값을 기억해두기 때문에 변경했을때의 감지할 수 있다.

<img src=".\images\image-20230407205608368.png" alt="image-20230407205608368" style="zoom:80%;" />

<br>

**참고 : 앞전에도 언급했지만, 트랜잭션이 커밋할때 즉, 플러시가 발생할때 "변경감지, 기록한SQL들 전송" 등등 을 수행한다.**

<br><br>

# 프로젝트

## 프로젝트 생성

**참고로 이번 강의에서는 기존 `스프링 부트 페이지` 에서 한번에 프로젝트를 생성했던것과는 반면,  
여기서는 zero부터 시작해서 JPA를 설정한다. (즉, `스프링 부트 페이지` 에서 프로젝트 생성한게 아님)**

* 자바 8(=1.8) 이 아닌 11로 했다가 `createEntityMangerFactory`가 동작을 하지 않았었다ㅜㅜ
* Maven으로 설정 하였다.
  * version : 1.0.0
* H2 DB와 JPA 하이버네이트 버전은 꼭 지켜주자. (필자는 1.4.199 썻다가 에러가 너무 많아서 200으로 바꿈..)
  * H2 : 1.4.200
  * JPA 하이버네이트 : 5.4.13.Final
* JPA 설정위해 src/main/resources/META-INF/persistence.xml 경로로 파일 생성
  * h2 DB와 연결 세팅을 하게 된다.
  * 자주 사용하는 "create" 옵션 기억!!

* 프로젝트와 H2 DB의 이름은 jpashop으로 통일

<br><br>

## 1. 엔티티 매핑

**대략적인 개발 순서**

1. 요구사항 분석(대략적 기능)
   * 예 : 회원은 상품을 주문할 수 있다.
2. 기능 목록(상세한 기능)
   * 예 : 회원 기능 - 등록, 조회
3. 도메인 모델 분석(도메인들의 관계 분석)
   * 회원과 주문의 관계 : 회원은 여러 번 주문할 수 있다.(1:N 관계)
4. 테이블 설계(DB 설계!!)
5. **엔티티 설계와 매핑(이 부분이 우리가 할 것!!)** 

<br>

<img src=".\images\image-20230408135048828.png" alt="image-20230408135048828" style="zoom:80%;" />

* **`주문` 과 `상품` 의 관계는 "N:N"의 관계인데, 중간에 `ORDER_ITEM` 을 추가함으로 써 "1:N, N:1" 관계로 풀어줬다.**

<br>

<img src=".\images\image-20230408135215543.png" alt="image-20230408135215543" style="zoom:80%;" />

* **테이블 구조를 보고 엔티티를 설계한다.**
* 다만, 여기서는 **데이터(테이블) 중심 설계**를 하였다.
  * 예로 객체인 `member : Member` 로 기록하지 않고, `memberId: Long` 형태로 기록했다. 
  * 이는 코드 소개 이후 다음단계에서 **객체 중심 설계**를 통해서 해결해본다.

<br>

### 코드 간단 소개

**위 경로로 패키지를 만들고, `item, Member, Order, OrderItem, OrderStatus.java` 를 구현한다.**

<br>

#### Order.java

```java
@Entity // 엔티티임을 알림
@Table(name = "ORDERS") // 테이블명 ORDERS 랑 매핑
public class Member {
    @Id @GeneratedValue // @Id는 PK임을 알림, @GeneratedValue는 id 자동 생성
    @Column(name = "order_id") // ORDERS 테이블의 컬럼명 order_id 와 매핑
    private Long id;

    @Column(name = "member_id") // Member 테이블의 키값 즉, 외래키(FK) 등록
    private Long memberId;
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING) // enum 데이터 사용땐 꼭 "STRING"으로 옵션
    private OrderStatus status;
    
    // getter, setter 생성...   
}
```

* **나머지 엔티티도 유사해서 코드 생략하겠다.**
* 참고로 DB에는 order by 같이 order가 예약어로 걸려있기 때문에, 테이블 명을 ORDERS 처럼 다른 이름으로 하는것을 추천한다.
* **@Column(length=10)** 으로 **varchar(255) -> varchar(10)**으로 변경도 가능. 즉, 기존 테이블 만들때 하는 옵션들 다 가능하니까 필요할때 잘 찾아서 적용할 것.

<br>

#### OrderStatus.java

```java
public enum OrderStatus {
    ORDER, CANCEL
}
```

<br>

#### JpaMain.java

```java
public class JpaMain {

    public static void main(String[] args) {
        // persistence.xml에 name을 "jpashop" 로 지정했었음.
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpashop");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try{
            Order order = em.find(Order.class, 1L); // DB에 있다고 가정
            Long memberId = order.getMemberId();

            Member member = em.find(Member.class, memberId);

            tx.commit();
        }catch (Exception e) {
            tx.rollback();
        }finally {
            em.close();
        }
        emf.close();
    }
}
```

* `persistence.xml` 에서 create로 설정해뒀기 때문에, 엔티티 매핑 테이블 있으면 자동 drop 및 다시 create table을 진행한다.
* 또한, `테이블 중심` 으로 엔티티 설계했기 때문에 객체 지향스럽지않다.
  * **order->member** 로 Order가 Member를 가지게해서 한번에 접근해줘야 하는데,
  * **order->memberId->member** 형태로 접근하고 있다.

<br><br>

## 2. 연관관계 매핑 기초

**연관관계를 매핑하는 이유는 앞전에 설계한 `데이터(테이블) 중심` 의 엔티티 설계를 `객체 지향 중심` 의 엔티티 설계로 발전시키기 위함이다.**

**반드시 단방향으로 먼저 개발을하고, 양방향의 필요 유무에 따라 추가한다.**

* `양방향` 을 추가할 때는 전혀 테이블에 영향을 끼치지 않기 때문에 `단방향` 을 우선 개발 하는것이다.
* 또한, `양방향` 의 경우 굳이 매핑을 추가하지 않아도 충분히 코드 구현이 가능해서 `단방향` 구현이 우선이다.
  * 물론, `양방향` 이 필요한 경우에는 매핑을 추가하는게 객체 지향적이고, JPQL에서도 잘 활용할 수 있다.

<br>

**테이블 구조는 이전과 같아서 생략하고, 객체 구조를 보여주겠다.**

<img src=".\images\image-20230408144647050.png" alt="image-20230408144647050" style="zoom:80%;" />

* **`memberId : Long -> member : Member` 로 참조 형태로 바뀜**

<br>

### 코드 간단 소개

**변경된 점은??**

* Member에 `orders`가 추가
* Order에 `memberId->member`, `orderItems` 추가
* OrderItem에 `itemId->item, orderId->order` 추가

* **개발과정**
  * "다대일" 먼저 **단방향 연관관계** 매핑후, 나머지 "일대다" 들 양방향 매핑 추가하자.
  * 아래 코드에서는 단방향 -> 양방향 개발순서 상관없이 코드를 한번에 보여주겠음.

<br>

#### Member.java

```java
@OneToMany(mappedBy = "member") // "일대다" 관계이며, 주인은 Order의 member
private List<Order> orders = new ArrayList<>();
```

* "단방향" 매핑 후 "양방향" 매핑을 위해 추가한 상태
  * "일대다 양방향 매핑 과정"

<br>

#### Order.java

```java
//    @Column(name = "member_id")
//    private Long memberId;
@ManyToOne // Order 엔티티가 연관관계 주인 ("다")
@JoinColumn(name="member_id") // 외래키
private Member member;

// 연관관계 주인은 OrderItem 엔티티 ("다")
@OneToMany(mappedBy = "order")
private List<OrderItem> orderItems = new ArrayList<>();

// 연관관계 편의 메소드
public void addOrderItem(OrderItem orderItem) {
    orderItems.add(orderItem);
    orderItem.setOrder(this);
}
```

* 기존 `Long memberId` 에서 `Member member` 로 변경
  * 연관관계의 주인이므로(주인은 "다") 외래키를 매핑한다.
  * "다대일 단방향 매핑 과정"
* `orderItems`의 경우 주인은 OrderItem 엔티티의 order 이므로 `mappedBy` 로 매핑
  * "일대다 양방향 매핑 과정"
* `연관관계 편의 메소드` 는 양방향 매핑을 하면 만드는것을 추천한다.
  * `order.addOrderItem(new OrderItem());` 처럼 간단히 사용할 수 있기 때문 (장점 많음)

<br>

#### OrderItem.java

```java
@ManyToOne
@JoinColumn(name = "order_id")
private Order order;

@ManyToOne
@JoinColumn(name = "item_id")
private Item item;
```

* 전부 "다대일 단방향 매핑 과정"

<br>

#### Item.java

**생략 **  
**Item은 양방향 매핑 안해서 수정할 거 없음.**

<br>

**이번엔 양방향 설계하지 않아도 충분히 코드 구현 가능하다는걸 보여주겠다.**

**우선, 양방향 설계후 연관관계 편의 메소드를 활용한 코드는**

```java
Order order = new Order();
order.addOrderItem(new OrderItem());
```

<br>

**그리고 양방향 설계하지 않고 그냥 코드로 바로 구현하는 것은**

```java
Order order = new Order();
em.persist(order);

OrderItem.orderItem = new OrderItem();
orderItem.setOrder(order); // 참고로 OrderItem이 주인
em.persist(orderItem);
```

<br><br>

## 3. 다양한 연관관계 매핑

**여러가지 연관관계를 보여주기 위해서 "다대다" 매핑도 추가하겠다.**

**"배송, 카테고리" 테이블을 추가**

<img src=".\images\image-20230408151428569.png" alt="image-20230408151428569"  />

<img src=".\images\image-20230408151536678.png" alt="image-20230408151536678"  />

<img src=".\images\image-20230408151606917.png" alt="image-20230408151606917"  />

* N:N 관계는 중간 테이블을 이용해서 1:N, N:1으로 풀어낸다.

* 실전에서는 @ManyToMany 사용 X
  * "다대다" 관계를 사용하는게 아니라 `Order, Item`을 풀어낸 `OrderItem 엔티티` 처럼 설계하는게 옳다.

<br>

### 코드 간단 소개

**Category, Delivery 테이블이 추가된 것이고, 이와 연관된 Item, Order.java도 수정되어 "일대일, 다대다" 관계를 나타낸다.**

<br>

#### Category.java

```java
// 상위 카테고리 라는개념
// 자식 입장에선 부모가 하나 - N:1
@ManyToOne
@JoinColumn(name = "parent_id")
private Category parent;

// 부모 입장에선 자식 여럿 - 1:N
@OneToMany(mappedBy = "parent")
private List<Category> child = new ArrayList<>();

@ManyToMany // 중간 테이블 생성
@JoinTable(name = "Category_item",
           joinColumns = @JoinColumn(name = "category_id"),
           inverseJoinColumns = @JoinColumn(name = "item_id")
          )
private List<Item> items = new ArrayList<>();
```

* 참고로 코드에 `id, name` 같은 자잘한 부분은 생략하고 나타내는중
* **items 부분은 Item과 "다대다" 관계이다 보니, 중간 테이블을 생성해서 풀어낸다.**

<br>

#### Item.java

```java
@ManyToMany(mappedBy = "items")
private List<Category> categories = new ArrayList<>();
```

* **"다대다" 관계에서 주인은 Category 이므로, 여기선 mappedby**

<br>

#### Delivery.java

```java
@OneToOne(mappedBy = "delivery")
private Order order;
```

* **"일대일" 관계는 주인을 아무나 선정하면 되는데, 여기선 Order가 주인이다.**

<br>

#### Order.java

```java
@OneToOne
@JoinColumn(name = "delivery_id")
private Delivery delivery;
```

* **"일대일" 관계의 주인**

<br><br>

## 4. 고급 매핑(상속관계)

**`상속-일반전략과 Mapped Superclass` 를 활용한다.**

* 중복해서 적는게 많이 줄어든다.

<br>

**요구사항 추가(가정)**

* 상품의 종류는 음반, 도서, 영화가 이쏙 이후 더 확장될 수 있다.
* 모든 데이터는 등록일과 수정일이 필수다.

<br>

<img src=".\images\image-20230408153403520.png" alt="image-20230408153403520"  />

<img src=".\images\image-20230408153437976.png" alt="image-20230408153437976"  />

<img src=".\images\image-20230408153523804.png" alt="image-20230408153523804"  />

* **Item에 상속 관계를 추가한것이고, 테이블의 모습은 SINGLE_TABLE 전략을 사용한 모습이다.**

<br>

### 코드 간단 소개

**Album, Book, Movie 테이블을 추가할것이고 Item과 상속관계로 나타낼거니까 Item은 추상클래스로 변경**

**그리고 BaseEntity.java 를 하나 만들어서 Mapped Superclass 전략 사용**

<br>

#### Album.java

```java
@Entity
//@DiscriminatorValue("DTYPE")
public class Album extends Item { // 부모인 item을 상속
    private String artist;
    private String etc;
```

* `@DiscriminatorValue("DTYPE")` 는 네이밍을 해서 자식임을 밝히고 구분할 수 있음.
* Book, Movie 도 유사해서 생략

<br>

#### Item.java

```java
// Item만 독립적으로 생성될 일이 없다고 가정하고, 추상 클래스로 만들겠음.
@Entity
@Inheritance(strategy = InheritanceType.JOINED) // JOIN방식 상속
@DiscriminatorColumn // 상속 테이블임을 표시하기 위함(기본값 : DTYPE)
public abstract class Item extends BaseEntity { ... }
```

* **앞전에 테이블 구조 사진에서는 SINGLE_TABLE 방식의 상속이였는데, 실제론 JOINED 방식을 많이 사용**

<br>

#### BaseEntity.java

```java
@MappedSuperclass
public abstract class BaseEntity {
    private String createdBy;
    private LocalDateTime createdDate;
    private String lastModifiedBy;
    private LocalDateTime lastModifiedDate;
```

* `@MappedSuperclass` 만 추가하면 바로 사용할 수 있다.
* **Category, Delivery, Member, Item에 BaseEntity를 상속받으면 위에 선언한 4개를 전부 테이블에 넣을 수 있다.**

<br>

#### JpaMain.java

```java
// Item을 추상 클래스로 만들었으니, Item 하위들로 persist
Book book = new Book();
book.setName("JPA");
book.setAuthor("사람22");
em.persist(book);
```

* 테스트 모습

<br>

**JOINED 방식 결과 모습**

<img src=".\images\image-20230408154642870.png" alt="image-20230408154642870"  />

* 해당되는 값들 생성하고, Item과 join 하는 방식

<br>

**SINGLE_TABLE 방식 결과 모습**

<img src=".\images\image-20230408154904919.png" alt="image-20230408154904919" style="zoom:;" />

* book,movie,album 테이블을 생성도 안하며 Item테이블 한곳에 전부 병합한 방식

<br><br>

## 5. 프록시와 연관관계 관리

**모든 연관관계를 `지연 로딩` 으로하며 `영속성 전이` 설정을 해본다.**

* `@XToOne` 은 기본이 **즉시 로딩**이므로 반드시 **지연로딩**으로 전부 변경

<br>

### 코드 간단 소개

**전부 LAZY로 변경하고, 영속성 전이를 설정한다.**

* Order -> Delivery를 cascade ALL
* Order -> OrderItem를 cascade ALL

<br>

#### Category.java

```java
@ManyToOne(fetch = FetchType.LAZY) // 기본이 즉시로딩
@JoinColumn(name = "parent_id")
private Category parent;

// 부모 입장에선 자식 여럿 - 1:N
@OneToMany(mappedBy = "parent") // 기본이 LAZY
private List<Category> child = new ArrayList<>();
```

* **@ManyToOne 부분만 LAZY로 설정 추가**
* 나머지들은 코드 생략

<br>

#### Order.java

```java
@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
@JoinColumn(name = "delivery_id")
private Delivery delivery;

@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
private List<OrderItem> orderItems = new ArrayList<>();
```

* Order -> Delivery를 cascade ALL
* Order -> OrderItem를 cascade ALL

<br><br>

## 6. 값 타입

**자바의 기본타입처럼 값 타입을 소개하는데, 자주 사용하는 "임베디드 타입" 을 기억하자.**

* 중복해서 적는게 많이 줄어든다.  
* **보통 설계할때부터 "값 타입" 으로 활용될거는 따로 빼서 설계**

* **값 타입은 정말 값 타입이라 판단될 때만 사용!!**
  * 식별자가 필요하고, 지속해서 값 추적 및 변경해야한다면 그것은 값타입이 아닌 **"엔티티"**  

<br>

**"임베디드 객체" 는 값 타입 하나. `@Embed...` 로**  

**"값 타입 컬렉션" 은 값 타입 하나 이상. 이건 그냥 `"일대다 고아+cascade" 엔티티` 로**

<img src=".\images\image-20230408155817695.png" alt="image-20230408155817695"  />

<br>

### 코드 간단 소개

**임베디드 타입만 소개할것이며, 이를위해 Address.java를 추가로 생성한다.**

**이 타입은 Delivery, Member에 적용할 것이다.**

<br>

#### Address.java

```java
// 임베디드 타입 => 값 타입
@Embeddable
public class Address {
    private String city;
    private String street;
    private String zipcode;
    
    // getter, setter
    
    // equals 오버라이딩
```

* **`@Embeddable` 사용**
* 임베디드 타입은 equals 오버라이딩이 필수다.
* 직접 구현하지말고 단축키 활용!!!@!@!@!@!

<br>

#### Delivery.java

```java
@Embedded
private Address address;
```

* **@Embedded 사용**
* Member.java 도 마찬가지이므로 코드 생략

<br><br>

# 객체지향 쿼리 언어

**이 파트는 따로 실습은 하지 않고 정리**

**아래 내용이 이해가 되지 않는다면 다시 복습하자(트랜잭션 예시)**

```java
public class JpaMain {

    public static void main(String[] args) {
        // 아까 name을 "hello" 로 지정했었음.
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        
        // DB변경은 반드시 트랜잭션 내에서! 아래 코드가 정석
        // 물론 현재 스프링 부트는 아래코드보다 간단
        tx.begin();
        try{
            // DB에 id 2L 데이터 있는 상태
            // 영속
            Member member = em.find(Member.class, 2L); // 1차캐시 비어서 DB접근(select)
            member.setName("AAAA"); // 데이터 "수정"

//            em.flush(); // 이걸 주석 해제하면 update쿼리 여기서 바로 전송해줌.
            em.detach(member); // 이거 때문에 준영속!! update쿼리 없어짐!!

            System.out.println("-------------");
            tx.commit(); // 준영속이라 이때 아무일도 발생X
        }catch (Exception e) {
            tx.rollback();
        }finally {
            em.close();
        }
        emf.close();
    }
}
```

<br>

**JPA는 다양한 쿼리 방법을 지원**

* JPA Criteria, **QueryDSL**은 자바 코드로 작성해서 JPQL형태로 빌드해줌.

- 강사님은 **JPQL로 대부분** 실무에서 처리되고,극히 일부만 **네이티브 SQL나 JDBC API** 등등 사용해서 해결했다고 한다
  - **JPQL(기본!), QueryDSL(동적쿼리에 강추!) 공부 추천**

<br>

**여기서 중요한 개념**

* JPQL은 **엔티티** 객체를 대상으로 쿼리
* SQL은 데이터베이스 **테이블**을 대상으로 쿼리

```java
// JPQL
// 검색 - 쿼리의 Member가 테이블이 아닌 "엔티티" 의미
String jpql = "select m From Member m where m.name like ‘%hello%'"; 
List<Member> result = em.createQuery(jpql, Member.class)
          .getResultList();
```

<br>

```java
// QueryDSL - Criteria 보다 단순(쉬움)
//JPQL - select m from Member m where m.age > 18 
JPAFactoryQuery query = new JPAQueryFactory(em);
QMember m = QMember.member;
List<Member> list = query.selectFrom(m) 
    .where(m.age.gt(18)) 
    .orderBy(m.name.desc()) 
    .fetch();
```

<br>

```java
// 네이티브 쿼리 (원래 기본 SQL)
// 단, 앞에서 db에 수정을 했다면 꼭 flush() 를 먼저 해주고 네이티브 쿼리로 "조회" 권장
String sql = “SELECT ID, AGE, TEAM_ID, NAME FROM MEMBER WHERE NAME = ‘kim’"; 
List<Member> resultList = em.createNativeQuery(sql, Member.class).getResultList();
```

<br><br>

## 1. JPQL

**참고) JPQL 은 "객체지향" 쿼리문 이지만 결국 SQL로 변환된다.**

<br><br>

## 2. 기본 문법과 기능

**SQL 과 문법이 유사**

* 단 "엔티티 이름" 사용하며 (테이블 X)
  * 별칭이 필수 `Member m` -> "m"이 별칭

```
select_문 :: = 
    select_절 
    from_절 
    [where_절] 
    [groupby_절] 
    [having_절] 
    [orderby_절]
update_문 :: = update_절 [where_절] 
delete_문 :: = delete_절 [where_절]
```

<br>

**TypeQuery, Query**

* TypeQuery: 반환 타입이 명확할 때 사용
* Query: 반환 타입이 명확하지 않을 때 사용

```java
// Member 로 타입이 명확
TypedQuery<Member> query = em.createQuery("SELECT m FROM Member m", Member.class); 
// m.username, m.age 로 string, int 임으로 타입이 명확하지 않음
Query query = em.createQuery("SELECT m.username, m.age from Member m");
```

<br>

**결과 조회 API**

* **query.getResultList(): 결과가 하나 이상일 때, 리스트 반환**
  * 결과가 없으면 빈 리스트 반환
* query.getSingleResult(): 결과가 정확히 하나, 단일 객체 반환
  * 결과가 없으면: javax.persistence.NoResultException
  * 둘 이상이면: javax.persistence.NonUniqueResultException

<br>

**파라미터 바인딩 - 이름 기준, 위치 기준**

```java
// 이름기준
SELECT m FROM Member m where m.username=:username 
query.setParameter("username", usernameParam); 
// 위치기준(비추)
SELECT m FROM Member m where m.username=?1 
query.setParameter(1, usernameParam); 
```

<br>

**프로젝션**

* SELECT 절에 조회할 대상을 지정하는 것
* SELECT **m** FROM Member m -> 엔티티 프로젝션
* SELECT **m.team** FROM Member m -> 엔티티 프로젝션
* SELECT **m.address** FROM Member m -> 임베디드 타입 프로젝션
* SELECT **m.username, m.age** FROM Member m -> 스칼라 타입 프로젝션

<br>

**프로젝션 - 여러 값 조회**

* SELECT **m.username, m.age** FROM Member m
  * Query 타입으로 조회
  * Object[] 타입으로 조회
  * **new 명령어로 조회 (단순 값을 DTO로 바로 조회)**
    * SELECT **new jpabook.jpql.UserDTO(m.username, m.age)** FROM Member m
    * 패키지 명을 포함한 전체 클래스 명 입력
    * 순서와 타입이 일치하는 생성자 필요

```java
// Object[]
List<Object[]> resultList = em.createQuery("select m.username, m.age from Member m")
    .getResultList();

Object[] result = resultList.get(0);
System.out.println("username = " + result[0]);
System.out.println("age = " + result[1]);

// Dto -> QueryDSL 사용시 패키지 명까지 없앨 수 있음 (우선 이걸로 자주 사용하자)
List<MemberDTO> result = em.createQuery("select new jpql.MemberDTO(m.username, m.age) from Member m", MemberDTO.class)
```

<br>

**서브쿼리 SQL 처럼 지원**

* JPA는 WHERE, HAVING 절에서만 서브 쿼리 사용 가능
* SELECT 절도 가능(하이버네이트에서 지원)
* **FROM 절의 서브 쿼리는 현재 JPQL에서 불가능**
  * **하이버네이트6 부터 지원!**
  * EX) select m from (select mm from Member mm) as m

<br>

**조건식 - CASE 식도 SQL 처럼 지원**

* 기본 CASE 식

  ```java
  select
      case when m.age <= 10 then '학생요금'
      	when m.age >= 60 then '경로요금' 
  	    else '일반요금'
  	end
  from Member m
  ```

* 단순 CASE 식

  ```java
  select
      case t.name
  	    when '팀A' then '인센티브110%' 
      	when '팀B' then '인센티브120%' 
  	    else '인센티브105%'
  	end 
  from Team t
  ```

<br>

**JPQL 기본 함수**

* CONCAT
* SUBSTRING
* TRIM
* LOWER, UPPER
* LENGTH
* LOCATE
* ABS, SQRT, MOD
* SIZE, INDEX(JPA 용도)

<br><br>

## 3. 페치 조인

**페이징 API**

* setFirstResult(int startPosition) : 조회 시작 위치 (0부터 시작)
* setMaxResults(int maxResult) : 조회할 데이터 수

```java
//페이징  쿼리
String jpql = "select m from Member m order by m.name desc"; 
List<Member> resultList = em.createQuery(jpql, Member.class)
    .setFirstResult(0) 
    .setMaxResults(10) 
    .getResultList();
```

<br>

**조인**

* 내부 조인: SELECT m FROM Member m **[INNER] JOIN** m.team t
  * (참고 SQL) : SELECT m.* from Member m **[INNER] JOIN** Team t **ON** m.id=t.id
  * **보통 관련있는 필드인 "외래키"와 조인**
* 외부 조인: SELECT m FROM Member m **LEFT [OUTER] JOIN** m.team t
  * (참고 SQL) : SELECT m.* from Member m **LEFT [OUTER] JOIN** Team t **ON** m.id=t.id
  * **보통 관련있는 필드인 "외래키"와 조인**
* 세타 조인: select count(m) from Member m, Team t **where** m.username = t.name
  * SQL 문과 동일
  * **"관련 필드 없어도" 조인**
* **추가 문법 : [OUTER]조인 + on**
  * 조인 대상 필터링 (join + **조건**까지)
    * EX) 회원과 팀을 조인하면서, 팀 이름이 A인 팀만 조인
    * SELECT m, t FROM Member m **LEFT JOIN** m.team t **on** t.name = 'A' 
    * (참고 SQL) : SELECT m.\*, t.* FROM Member m **LEFT JOIN** Team t **ON** m.TEAM_ID=t.id **and t.name='A'**
  * 연관관계 없는 엔티티 외부 조인 (**세타 조인**처럼 사용)
    * EX) 회원의 이름과 팀의 이름이 같은 대상 외부 조인 
    * SELECT m, t FROM Member m **LEFT JOIN** Team t **on** m.username = t.name
    * (참고 SQL) : SELECT m.\*, t.* FROM Member m **LEFT JOIN** Team t **ON** m.username = t.name

<br>

**join vs fetch join**

* join
  * 일반 조인은 연관된 엔티티를 함께 조회 X
  * **SELECT 절에 지정한 엔티티만 조회**
    * 예로 SELECT **t.*** from ...
* fetch join
  * 연관된 엔티티도 함께 조회 **("즉시로딩")**
  * "지연로딩"을 쓰다가 여기서 "즉시로딩"으로 SQL 한번에 조회하는 개념으로 사용
  * (JPQL) : select **t** from Team t **join fetch** t.members where t.name = ‘팀A'
  * (SQL) : SELECT **T.\*, M.*** FROM TEAM T **INNER JOIN** MEMBER M **ON** T.ID=M.TEAM_ID WHERE T.NAME = '팀A'
    * T.\*, M.* 로 두 테이블 모두 조회 하는 것
    * 따라서 **객체 그래프를 유지할 때 사용**하면 효과적

<br>

**fetch join 한계**

* 페치 조인 대상에는 별칭을 주지 않는다. (조건을 안쓰는걸 권장)
  * 유일하게 쓰는 경우가 연속으로 join으로 가져오는 그런경우에 쓴다고 기억
* 둘 이상의 컬렉션은 페치 조인 할 수 없다.
* 컬렉션을 페치 조인하면 페이징 API(setFirstResult, setMaxResults)를 사용할 수 없다.
  * 왜?? "데이터 뻥튀기" 때문 -> 따라서 **XToOne 은 사용가능**
  * **컬렉션인 XToMany 는 "BatchSize" 개념을 사용해서 해결가능!**
    * fetch join 을 사용하는게 아니라 그냥 select t From Team t; + 페이징 API 를 쓴다.
    * 단, fetch join을 안씀에 따라 N+1 문제가 있으니까 이것을 "BatchSize" 로 해결
    * BatchSize 를 100을 둔다고 가정시 총 150 개 데이터가 있을 경우 처음 100개 그다음 50개 인쿼리문을 보낸다고 한다. 그럼 쿼리문 자체는 훨씬 줄어드는것!
      * 먼저 Team 쿼리가 나오면서 페이징에 등록한만큼 데이터를 가져오고,
      * Team에 연관된 Member들을 가져와야하는데 이때 Member를 가져오는 쿼리가 나오면서 where member.Team_id in ( ?. ? ) 이부분에 ?. ? 가 팀A, 팀B 를 의미하며,
      * BatchSize 100이니까 "팀" 데이터가 150개면 ?가 100개 그다음쿼리에선 50개가 붙어서 나간다.
    * **@BatchSize 나 글로벌 사용법이 있는데 강사님은 보통 "글로벌로 100정도" 로 깔아두고 개발한다고 한다. => 따라하자@**
* **여러 테이블을 조인해서 엔티티가 가진 모양이 아닌 전혀 다른 결과를 내야 하면, 페치 조인 보다는 일반 조인을 사용하고 필요한 데이터들만 조회해서 DTO로 반환하는 것이 효과적**

<br>

**fetch join 일반, 컬렉션**

* 페치 조인 ::= [ LEFT [OUTER] | INNER ] JOIN FETCH 조인경로

* **"지연로딩, 즉시로딩" 둘다 N + 1 문제 발생할 수 있다.**

  * **XToOne** - X : 1 관계 및 LAZY(지연로딩) 이라 가정

    * Member, Team 엔티티로 관계 있다고 가정 
      * (최악의 상황 가정) 회원1 - 팀A, 회원2 - 팀B, 회원3 - 팀C
      * **"회원" 을 조회 가정(XToOne)**
    * `일반 조인` 은 M.* 로 SELECT 동작함으로써 회원1을 조회하면 팀A 때문에 join쿼리 1번전송   
      -> 회원2를 조회하면 팀A면 추가 쿼리 없지만 팀B라서 또 추가 join쿼리 전송...   
      -> 회원3까지 진행시 총 3번의 쿼리를 전송   
      **-> "N+1" 문제... (1번의 쿼리에 N번의 추가쿼리)**
    * `페치 조인` 은 M.\*, T.* 로 SELECT 동작함으로써 회원1,2,3 팀A,B,C를 1번의 join쿼리에 모두 조회하기 때문에 총 쿼리가 1번 전송  
      **-> "N+1" 문제 해결!**

  * **XToMany** - X : N 관계 및 LAZY(지연로딩) 이라 가정

    * Member, Team 과 관계 있고, 앞의 XToOne 에서와 반대로 "조인" 하는것

      * (최악의 상황 가정) 회원1 - 팀A, 회원2 - 팀A, 회원3 - 팀B
      * **"팀" 을 조회 가정(XToMany)**

    * `일반 조인` 은 T.* 로 SELECT 함으로써 XToOne에서 본것과 마찬가지로 문제

    * `컬렉션 페치 조인` 은 T.\*, M.* 로 SELECT 함으로써 XToOne 처럼 문제 해결!

      * **단!! Distinct(중복제거) 사용하지 않으면 "데이터 뻥튀기 문제 발생"**

      * 아래쪽 그림을 참고) **DB 입장**에서 `팀A - 회원1,2 ` 는 `데이터 1개 - 2개` 인데 join 하면 **데이터 1개가 아니라 2개가 될 수 밖에 없다. ("DB 데이터 뻥튀기")**

      * 이때, fetch join 때문에 {팀A-회원1, 팀A-회원2} 이어야 할 데이터가   
        {팀A-회원1,2 , 팀A-회원1,2} 로 "동일한 데이터가 뻥튀기로 보인다"

        * 데이터 중복제거 필요하면, Distinct 로 간단히 해결

        * 단!! DB에서 Distinct 는 정확히 동일한 튜플만 중복제거 되며,

        * JPQL 에서는 엔티티 주소를 확인해서 중복제거를 제공한다.(아래그림 참고)

          ![image](https://github.com/BH946/spring-first-roadmap/assets/80165014/170bd550-4f66-4823-ad9e-e7d35d3e378b) 

<br>

**그림참고 (페치 조인, 컬렉션 페치 조인)**

![image](https://github.com/BH946/spring-first-roadmap/assets/80165014/fee4bf06-9d05-4d7d-96db-5d4857847daa) 

<br>

![image](https://github.com/BH946/spring-first-roadmap/assets/80165014/6c6d80ce-e603-4c05-a877-68dfa6555bd4) 

<br><br>

## 4. 경로 표현식

**경로 표현식 총 3가지**

* **상태 필드**(state ﬁeld): 단순히 값을 저장하기 위한 필드 (ex: m.username)
* **연관 필드**(association ﬁeld): 연관관계를 위한 필드
  * 단일 값 연관 필드: @ManyTo**One,** @OneTo**One**, 대상이 엔티티(ex: m.team)
    * "즉시 로딩" 이 기본값이므로 꼭 "지연 로딩" + "join fetch" 사용 권장
  * 컬렉션 값 연관 필드: @OneTo**Many**, @ManyTo**Many**, 대상이 컬렉션(ex: m.orders)
    * 컬렉션은 "Object" 객체로 생각하면 되며, "지연 로딩" 이 기본값

```java
select m.username -> 상태 필드 
  from Member m 
    join m.team t    -> 단일 값 연관 필드 
    join m.orders o -> 컬렉션 값 연관 필드 
where t.name = '팀A'
```

<br>

**경로 표현식 특징**

* 상태 필드(state ﬁeld): 경로 탐색의 끝, 탐색X
  * `m.username.` 처럼 뒤에 '.' 을 붙여서 탐색이 불가하다는 것
* 단일 값 연관 경로: **묵시적 내부 조인(inner join) 발생**, 탐색O
* 컬렉션 값 연관 경로: **묵시적 내부 조인(inner join) 발생**, 탐색X
  * FROM 절에서 명시적 조인을 통해 별칭을 얻으면 별칭을 통해 탐색 가능
  * **"묵시적 내부 조인" 으로 인해 N+1 문제 직면할 위험이 있으므로 "비추천"**

<br><br>

## 5. 다형성 쿼리

**조회 대상을 특정 자식으로 한정 - type**

* 예) Item 중에 Book, Movie를 조회해라
* [JPQL] : select i from Item i where type(i) IN (Book, Movie)
* [SQL] : select i from i where i.DTYPE in (‘B’, ‘M’)

<br>

**예) 부모인 Item과 자식 Book이 있다. - treat**

* [JPQL] : select i from Item i where treat(i as Book).author = ‘kim’

* [SQL] : select i.* from Item i where i.DTYPE = ‘B’ and i.author = ‘kim’

<br><br>

## 6. 엔티티 직접 사용

**JPQL에서 엔티티를 직접 사용하면 SQL에서 해당 엔티티의 "기본 키" 값을 사용**

* [JPQL]
  * select count**(m.id)** from Member m //엔티티의 아이디를 사용 
  * select count**(m)** from Member m    //엔티티를 직접 사용
* \[SQL](JPQL 둘다 같은 다음 SQL 실행) 
  * select count**(m.id)** as cnt from Member m

* **따라서 파라미터로 전달할 때도 활용 - "기본키"**

  ```java
  // 엔티티를 파라미터로 전달 방법
  String jpql = “select m from Member m where m = :member”; 
  List resultList = em.createQuery(jpql)
                      .setParameter("member", member) 
                      .getResultList();
  // 식별자를 직접 전달 방법
  String jpql = “select m from Member m where m.id = :memberId”; 
  List resultList = em.createQuery(jpql)
                      .setParameter("memberId", memberId) 
                      .getResultList();
  // 실행된 SQL 동일
  select m.* from Member m where m.id=?
  ```

* **"외래키"도 동일**

  ```java
  // 엔티티를 파라미터로 전달 방법
  Team team = em.find(Team.class, 1L);
  String qlString = “select m from Member m where m.team = :team”; 
  List resultList = em.createQuery(qlString) 
                      .setParameter("team", team) 
                      .getResultList();
  // 식별자를 직접 전달 방법
  String qlString = “select m from Member m where m.team.id = :teamId”; 
  List resultList = em.createQuery(qlString)
                      .setParameter("teamId", teamId) 
                      .getResultList();
  // 실행된 SQL 동일
  select m.* from Member m where m.team_id=?
  ```

<br><br>

## 7. Named 쿼리

**Named 쿼리 - 정적 쿼리**

* 미리 정의해서 이름을 부여해두고 사용하는 JPQL
* 정적 쿼리
* 어노테이션, XML에 정의
* 애플리케이션 로딩 시점에 초기화 후 재사용
* **애플리케이션 로딩 시점에 쿼리를 검증 -> 장점**

<br>

**Named 쿼리 - 어노테이션**

```java
@Entity
@NamedQuery(
        name = "Member.findByUsername",
        query="select m from Member m where m.username = :username") 
public class Member {
    ... 
}
List<Member> resultList =
  em.createNamedQuery("Member.findByUsername", Member.class) 
        .setParameter("username", "회원1")
        .getResultList();
```

<br>

**(참고) 실무에서는 Spring Data JPA 를 사용하는데 @Query("select...") 문법이 바로 "Named 쿼리"**

<br><br>

## 8. (중요) 벌크 연산

**벌크 연산 : 하나의 데이터가 아닌 여러 데이터를 한번에 수정하거나 삭제하는 연산**

* JPA 는 보통 실시간 연산에 치우쳐저 있는데, 대표적인 예가 "더티 체킹"
  * 100개 데이터가 변경되었으면 100개의 Update 쿼리가 나가게 되는 문제
  * 이런건 "벌크 연산" 으로 해결하자

<br>

**쿼리 한 번으로 여러 테이블 로우 변경(엔티티)**

* executeUpdate()의 결과는 영향받은 엔티티 수 반환
* UPDATE, DELETE 지원
* INSERT(insert into .. select, 하이버네이트 지원)

```java
String qlString = "update Product p " +
                  "set p.price = p.price * 1.1 " + 
                  "where p.stockAmount < :stockAmount";
int resultCount = em.createQuery(qlString)
                    .setParameter("stockAmount", 10) 
                    .executeUpdate();
```

<br>

**주의 및 사용법**

* 벌크 연산은 영속성 컨텍스트를 무시하고 데이터베이스에 직접 쿼리 하므로 "주의"
* **올바른 사용법**
  * **벌크 연산을 먼저 실행**
  * **벌크 연산 수행 후 영속성 컨텍스트 초기화**
    * 혹시나 이전에 값이 "영속성 컨텍스트" 에 있는 상태로 "벌크 연산" 수행해서
    * 아직도 "영속성 컨텍스트" 에 남아있을 가능성을 배제하기 위해

<br>

**flush 자동 호출은 3가지 경우 - commit 시점, query 날리는 시점, 강제 em.flush()**

```java
Member member1 = new Member();
member1.setUsername("회원1");
member1.setAge(0);
member1.setTeam(teamA);
em.persist(member1); // 영속성

Member member2 = new Member();
member2.setUsername("회원2");
member2.setAge(0);
member2.setTeam(teamA);
em.persist(member2); // 영속성

Member member3 = new Member();
member3.setUsername("회원3");
member3.setAge(0);
member3.setTeam(teamB);
em.persist(member3); // 영속성

// flush 자동 호출 commit, query, 강제 em.flush()
int resultCount = em.createQuery("update Member m set m.age = 20")
    .executeUpdate();

System.out.println(member1.getAge()); // 0
System.out.println(member2.getAge()); // 0
System.out.println(member3.getAge()); // 0
// 단 DB에는 20
// 위 코드가 이해가 안되면 다시 영속성 컨텍스트 복습하자
```

<br>

* **현재 print에서 0이 나온이유??**

  * flush를 한다고 해서 영속성 컨텍스트에 데이터가 사라지는게 아니기 때문이며,
  * 애초에 이전 member1 데이터 객체 주소를 그대로 사용중이기 때문

* **DB는 20인 이유??**

  * 쿼리 날리는 시점에 flush가 발생해 "영속성 컨텍스트" 의 member1,2,3 을 DB에 insert
  * 이후에 update 쿼리문을 DB에 "직접" 날려서 age를 20으로 변경한 것

* 그래서 아까 "올바른 사용법" 에서 **벌크 연산 수행 후 영속성 컨텍스트 초기화** 를 언급

  * **따라서 아래코드처럼 사용**

    ```java
    em.clear(); // 초기화 (em:영속성 컨텍스트)
    // 아직 member1은 이전의 데이터 값을 기록해둔 변수 주소 그대로이기 때문에
    // DB에서 새로 가져오는 작업도 필수 (아래 find)
    Member findMember = em.find(Member.class, member1.getId()); 
    
    System.out.println(findMember.getAge()); // 20
    ```

    

