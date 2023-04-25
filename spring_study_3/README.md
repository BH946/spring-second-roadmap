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

