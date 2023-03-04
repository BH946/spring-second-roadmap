## Intro..

**실전! 스프링 부트와 JPA 활용1 - 웹 애플리케이션개발**

* 인프런 강의 듣고 공부한 내용입니다.

<br>

해당 프로젝트 폴더는 강의를 수강 후 강의에서 진행한 프로젝트를 직접 따라 작성했습니다.

따로 강의 자료(pdf)를 주시기 때문에 필요할때 해당 자료를 이용할 것이고,

이곳 README.md 파일에는 기억할 내용들만 간략히 정리하겠습니다.

* 자세한 코드가 궁금하다면, 올려둔 프로젝트에서 코드확인

<br><br>

## 프로젝트 환경설정 & 생성

**준비물**

* Java 11
* IDE: IntelliJ (이클립스도 가능합니다)

<br>

**스프링 프로젝트 생성**

* **[프로젝트 생성하는 곳](https://start.spring.io)**
  * Project: Gradle - Groovy Project 
  * Spring Boot: 2.x.x
  * Language: Java
  * Packaging: Jar
  * Java: 11
  * Dependencies: spring web starter, thymeleaf, spring data jpa, h2 database, lombok
* 참고로  `2.7.1 (SNAPSHOT)` 이런 형태가 아닌 `2.7.0` 처럼 영어가 안 붙은걸 선택 권장
* 이후에 **gereater the project로 다운 -> IntelliJ로 폴더 오픈**

<br>

**추가 설정**

* **IntelliJ Gradle 대신에 자바가 직접 실행하게 만든다.**
  * 최근 IntelliJ 버전은 Gradle을 통해서 실행 하는것이 기본 설정이다. 이렇게 하면 실행속도가 느리다. 
  * 다음과 같이 변경하면 IntelliJ가 자바로 바로 실행해서 실행속도가 더 빠르다.
    * Preferences -> Build, Execution, Deployment -> Build Tools -> Gradle 
    * Build and run using: Gradle -> IntelliJ IDEA
    * Run tests using: Gradle -> IntelliJ IDEA

  * 참고로 File -> Setting에서 검색해서 바로 찾으면 간단.

* **그리고 설치한 `jdk11` 로 프로젝트, gradle 설정 해줘야 한다.(이미 되어있을 것이다)**
  * 위에서 접근한 Build Tools -> Gradle 에서 jdk11로 설정(java11)
  * File -> Setting에서 바로 Project Setting -> Project 검색해서 이곳도 jdk11로 설정(java11)

* **Junit5를 선호하면 상관 없지만, Junit4를 사용하기 위해선 `build.gradle` 파일 수정 - `dependencies내부`**

  ```
  //JUnit4 추가(junit5로 자동실행 되기 때문)
  testImplementation("org.junit.vintage:junit-vintage-engine") {
  	exclude group: "org.hamcrest", module: "hamcrest-core"
  }
  ```

* **lombok(롬복) 적용(plugin 설치가 필수)**

  * 물론 `dependencies` 에 추가한 상태이며 추가로 설정이 필요한 것.
    * Preferences -> plugin -> lombok 검색 실행 (재시작)
    * Preferences -> Annotation Processors 검색 -> Enable annotation processing 체크 (재시작)
  * 테스트 : @Getter, @Setter 확인

<br>

**단축키 확인 법**

* File -> Settings -> keymap 에서 검색해서 확인

<br>

**참고**

- 여기 `plugins {}` 에는 스프링부트 관련해서 있다.
  - 해당 내용에 있는 스프링부트 버전을 바꿔주기만 해도 자동으로 현재 설정된 스프링부트 버전에 맞게끔 사용 라이브러리들을 권장하는 버전들로 전부 바꿔주는 기능이 있다고 한다.
  - 다만, 외부 라이브러리같이 스프링부트가 기본적으로 완전 권장하는 라이브러리들이 아닌 이상은 직접 버전까지 같이 적어준다!(권장하는 라이브러리들은 버전을 적을 필요가 없었다)
- `External 라이브러리` 폴더 보면 엄청많이 라이브러리들을 끌어온걸 볼 수 있음
- `test코드` 에 아무 함수적어서 그냥 실행만 시켜보면? 프로젝트가 잘 구동되고 있는걸 알 수 있음

<br><br>

## H2 DB & 본섭, 테섭DB

**MySql 도 있고 다른 DB들도 있지만 H2는 간단히 테스트, 개발하기 좋다고 해서 이것을 사용하게되었다.**

<br>

### H2 DB 생성

<img src=".\images\image-20230212110546722.png" alt="image-20230212110546722" style="zoom:80%;" /> 

* **H2 DB를 생성하기 위해선 맨처음에 JDBC URL을 파일모드로 연결 해야한다.**
* **또한 크롬에 url란에 session값들도 있는데 이 또한 최초에는 건드리면 안됨(인증 위함)**

<br>

<img src=".\images\image-20230212110926194.png" alt="image-20230212110926194" style="zoom:80%;" /> 

* **최초 DB 생성 이후부터는 tcp로 네트워크 모드로 연결(접속)한다.**
* **이때 부터는 네트워크 모드로 어디서든 접근 가능(h2 서버만 구동해둔 상태라면)**

<br>

### H2 DB 설정(spring 연동)

**`application.propertices` 를 바로 사용해도 되지만, yml파일을 선호해서 기존 파일은 제거하고  ` application.yml` 파일을 만들어서 사용하겠다.**

* yml 파일은 들여쓰기가 띄어쓰기 2칸씩이므로 꼭 이를 지켜줄것

<br>

<img src=".\images\image-20230212111505705.png" alt="image-20230212111505705" style="zoom:80%;" /> 

* `datasource` 설정은 h2 db 접근 관련
* `spring.jpa.hibernate.ddl-auto: create` 옵션은 앱 실행 시점에 테이블 drom & 다시 생성
* `show_sql: true` 옵션은 System.out에 하이버네이트 실행 SQL을 남긴다.
  * 아래 logger를 통해 남기니까 이부분은 주석처리
* `org.hibernate.SQL` 옵션은 logger를 통해 하이버네이트 실행 SQL을 남긴다.
* `org.hibernate.type: trace` 옵션은 ??? 로 나오는 쿼리 파라미터 로그를 실제 적용한 값으로 남겨줌

<br>

### 테섭DB

**현재까지 설정 및 만든건 전부 `본섭DB` 로 볼 수 있다.**

**테스트는 케이스가 격리된 환경에서 실행하고, 끝나면 데이터를 초기화 하는것이 좋다. 그런면에서 메모리** 
**DB를 사용하는 것이 가장 이상적이다.**

<br>

**방법은 정말 간단하다.**

* `test/resources/application.yml` 파일을 만들어서 아무것도 작성 안해도 된다.
  * 참고로 sql 로그는 보기위해서 `loggin.leve: ...` 관련 코드는 주석처리 하지 않았다.
* 테스트 코드 실행시 이 위치의 설정 파일을 먼저 읽기 때문에 `datasource` 설정이 없으면 메모리 DB를 자동으로 사용한다.

<br><br>

## 도메인 분석 설계

### 요구사항 분석

**실제 완성될 웹 화면**

<img src=".\images\image-20230212121843695.png" alt="image-20230212121843695" style="zoom:80%;" /> 

<br>

**기능 목록**

* 회원기능 
  * 회원등록 
  * 회원조회
* 상품기능 
  * 상품등록 
  * 상품수정 
  * 상품조회
* 주문기능 
  * 상품주문 
  * 주문내역조회 
  * 주문취소
* 기타요구사항
  * 상품은재고 관리가필요하다
  * 상품의종류는도서, 음반, 영화가 있다. 
  * 상품을카테고리로 구분할수 있다.
  * 상품주문시 배송정보를입력할수있다.

<br>

### 도메인 모델과 테이블 설계(중요)

<img src=".\images\image-20230212122154055.png" alt="image-20230212122154055"  /> 

* **회원은 주문을 여러개 할 수 있고, 주문할 때 여러 상품을 선택할 수 있으므로 '주문과 상품'은 N:N 관계**
  * 하지만, N:N 관계는 비추천이기 때문에 '주문상품' 을 끼워 넣어서 1:N, N:1 관계로 풀어냈다.
* **카테고리와 상품(물품) 엔티티의 관계도 N:N 관계인데, 이것도 결국 1:N, N:1 관계로 풀어내야한다.**
  * 이 또한, 중간에 매핑 테이블을 연결해서 풀어낸다.

<br>

<img src=".\images\image-20230212143202478.png" alt="image-20230212143202478"  /> 

* **엔티티 분석을 나타낸 그림**
  * 참고로 Member -> Order로 참조를 실제로 할일은 없고 Order->Member 로 참조하는것으로 충분
  * 그러나 여기서는 일대다, 다대일의 양방향 연관관계를 설명하기 위해서 추가했음

<br>

<img src=".\images\image-20230212144252914.png" alt="image-20230212144252914"  />  

* **테이블 분석을 나타낸 그림**
  * PK는 따로 표시 안했지만, 자기자신 id값으로 가질 예정이고 FK를 잘 확인할 것
  * 또한 카테고리와 상품의 중간에 매핑 테이블을 추가한것을 잘 확인할 것
  * 상품 - 앨범, 도서, 영화 타입을통합해서 하나의테이블로 만들었다. DTYPE 컬럼으로타입을 구분한다.
  * Address, Album Book Movie 는 엔티티에 합쳐졌다는 점

<br>

**연관관계 매핑 분석(중요)**

* **`회원과 주문`은 1:N, N:1 양방향 관계(주인 선정이 필요!)**
  - '다' 에 속하는 주문 테이블을 주인으로 반드시 선정(따라서 ORDER테이블이 MEMBER 외래키 가짐)
* **`주문과 주문상품`은 1:N, N:1 양방향 관계(주인 선정 필요!)**
  - '다' 에 속하는 테이블이 주인으로 반드시 선정(따라서 ORDERITEM 테이블이 OREDR 외래키 가짐)
* **`주문과 배송`은 1:1 양방향 관계(주인이 필요!)**
  - 1:1은 외래키를 아무곳에나 둬도 되기 때문에 여기선 주문 테이블에 두었다!(따라서 주인은 주문 테이블)
* **`주문상품과 상품`은 N:1 단방향 관계(그림의 화살표 확인 및 주인 필요X)**
  - N:1 관계로 표시했는데, 해석해보면 당연히 주문상품을 통해서 무슨 상품인지 확인하지, 상품을 먼저 접근해서 주문상품을 알 이유는 전혀 없다.
  - 따라서 주문상품을 통해서 어떤 상품인지 접근하는 루트만 필요하므로 단방향 관계인 것이다.
  - 또한 '다' 가 외래키를 가진다 했으니, ORDERITEM이 ITEM 외래키 값을 가진다.
  - **여기서 주인 설정을 안하는 이유는?? 단방향이라 주인이 누군지 명확하기 때문(양방향만 필요함)**
* **`카테고리와 상품`은 N:N 양방향 관계!(실무에선 사용하지말라)**
* **참고**
  * 양방향 관계이면 주인 선정이 필요하다.
    * 단방향 관계는 당연히 주인 선정이 필요없고, 주인이 누군지 명확하다.
  * '다대일' 같이 '다' 가 속한 관계의 경우 '다' 부분이 외래키를 가진다.
  * 외래키가 있는 곳을 연관관계의 주인으로 정해라.
    * 양방향, 단방향 둘다 외래키는 설정하게 될텐데 양방향에서는 그곳을 주인으로 정하라.
  * '일대일' 의 경우 양방향 관계 일때 주인선정을 해야하는데, '일대일'이라서 아무곳에나 둬도 된다.
    * 그래도 실제로 동작을 생각해서 합리적인 곳에 둬야한다.
  * 마지막으로 '다대다' 의 관계의 경우 무조건 풀어서('일대다','다대일') 사용해야한다.
    * '다대다' 이상태로는 실무에서 절대 사용하면 안된다.

<br>

### 엔티티 클래스 개발

**'엔티티 분석' 그림을 보면서 개발해나가면 된다. FK 값들은 '테이블 분석'도 참고!**

**또한, 실무에서는 Getter는 오픈하되 Setter의 경우 필요한 경우에만 사용하길 권장!**

* **참고: 이론적으로 Getter, Setter 모두제공하지 않고, 꼭 필요한 별도의 메서드를 제공하는게 가장 이상적이다.** 
* 하지만 실무에서 엔티티의 데이터는 조회할 일이 너무 많으므로, Getter의 경우 모두 열어두는 것이 편리하다. 
* Getter는 아무리 호출해도 호출 하는것 만으로 어떤일이 발생하지는 않는다. 
* 하지만 Setter는 문제가다르다. Setter를 호출하면 데이터가 변한다. Setter를 막열어두면 가까운미래에 엔티티에 가도 대체 왜 변경되는지 추적하기 점점 힘들어진다. 
* 그래서 엔티티를 변경할때는 Setter 대신에 변경지점이 명확하도록 변경을 위한 비즈니스 메서드를 별도로 제공해야 한다.

<br>

**회원 엔티티(Member.java)**

```java
@Entity
@Getter @Setter
public class Member {
    @Id @GeneratedValue // PK(기본키) 설정
    @Column(name = "member_id") // PK 컬럼명 지정
    private Long id; // 엔티티 식별자
    
    private String name; 
    
    @Embedded // JPA 내장 타입으로 만들어진 Address 를 사용한다는 의미
    private Address address;
    
    @OneToMany(mappedBy = "member") // 주인이 아닌곳에 mappedBy = "member" 로 표현
	private List<Order> orders = new ArrayList<>();
}
```

* 참고: 엔티티의 식별자는 id 를사용하고 PK 컬럼명은 member_id 를 사용했다. 엔티티는 타입(여기서는 
  Member )이 있으므로 id 필드만으로 쉽게 구분할 수 있다. 테이블은 타입이 없으므로 구분이 어렵다. 
  그리고 테이블은 관례상 테이블명 + id 를많이사용한다. 참고로객체에서 id 대신에 memberId 를 
  사용해도 된다. 중요한것은 일관성이다.

<br>

**주소 타입(Address.java)**

```java
@Embeddable 
@Getter
public class Address {
    private String city;
    private String street;
    private String zipcode;
    
    protected Address() { 
    }
    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    } 
}
```

* @Embeddable 로 이를 표기해서 JPA 내장 타입을 선언
  * 사용할 때는 위에 Member를 보면 @Embedded 를 선언해서 사용
* 값 타입은 당연히 변경 불가능하게 설계해야해서 @Setter는 안쓰고, 생성자로 초기화 후 변경 불가능하게 만들자.
* 참고로 @Embeddable 특성상 자바 기본 생성자는 `public or protected` 로 설정해놔야 해서 `protected` 가 좀 더 안전하니까 이걸로 설정한 것

<br>

**주문 엔티티(Order.java)**

```java
@Entity
@Table(name = "orders") // 관례상 order이름 그대로 사용하지말고 바꾸라함
@Getter @Setter
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id") // PK 컬럼명 order_id, id를 식별자로 사용
    private Long id;

    // 다대일&양방향 => 주인 "다"인 Order
    @ManyToOne(fetch = FetchType.LAZY) // 무조건 LAZY 사용할 것
    @JoinColumn(name = "member_id") // FK(외래키)로 사용 - 매핑
    private Member member; // 주문 회원

    // cascade : 영속성 전이
    // 일대다&양방향 => 주인 OrderItem
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();
	
    // delivery 위와 유사해서 생략... //
    private LocalDateTime orderDate; // Date보다 이 타입 추천

    // enum 데이터 방식
    @Enumerated(EnumType.STRING) // 타입 꼭 STRING을 추천
    private OrderStatus status; // 주문상태 [ORDER, CANCEL]
```

* 관례상 order 이름그대로 쓰면 안되므로 @Table을 통해 테이블명 변경(orders로)
* 여기수 중요한점은 반드시 LAZY로 사용할 것
* @JoinColumn을 통해서 외래키(FK) 지정

* Enum 데이터 방식도 확인(@Enumerated) => 꼭 STRING을 추천

  ```java
  public enum OrderStatus {
      ORDER, CANCEL
  }
  ```

<br>

**주문상품 엔티티(OrderItem.java)**

* 그림에 맞게 쭉 계속 생성

<br>

**상품 엔티티(Item.java)**

* 상속관계 매핑으로 구성!!!

  * Book, Album, Movie.java 엔티티 만들기
  * Item.java 다 상속받기

* @Inheritance 어노테이션 이용해서 SINGLE_TABLE 전략을 사용할거임

  * 한 테이블에 다 때려박는 전략이다.

* 한테이블에 모았어도, 실제 저장할땐 뭐가 상속구조인지 이런건 알아야 스프링이 저장할거다.

  - @DiscriminatorColumn
    - 부모에 선언

  - @DiscriminatorValue
    - 자식에 선언

* 카테고리와 "다대다" 관계가 있는데 실무에서는 사용X

  ```java
  @ManyToMany(mappedBy = "items")
  private List<Category> categories = new ArrayList<Category>();
  ```


<br>

**배송 엔티티(Delivery.java)**

* 여기도 Enum 데이터 방식 확인(@Enumerated)

  ```java
  public enum DeliveryStatus {
      READY, COMP 
  }
  ```

<br>

**카테고리 엔티티(Category.java)**

```java
@ManyToMany
@JoinTable(name = "category_item",
            joinColumns = @JoinColumn(name = "category_id"), 
            inverseJoinColumns = @JoinColumn(name = "item_id"))
private List<Item> items = new ArrayList<>();
```

* 이런 형태로 중간에 매핑 테이블을 임의로 만들어서 사용한 것
* `@ManyToMany` 는편리한 것 같지만, 중간 테이블( CATEGORY_ITEM )에 컬럼을추가할 수없고, 세밀하게 
  쿼리를 실행하기 어렵기때문에 실무에서 사용하기에는 한계가 있다. 
* 해결 방안으로 중간 엔티티( CategoryItem )를 만들고 `@ManyToOne , @OneToMany` 로 매핑해서 사용하자. 
* 정리하면 "다대다" 매핑을 ''일대다, 다대일'' 매핑으로 풀어내서 사용하자.

<br>

### 엔티티 설계시 주의점

**참고로 테이블 이름은 OrderItem- > "order_item" 처럼 소문자 및 "_" 로 구분해서 지정하겠다.**

**"다대다" 관계는 "일대다, 다대일" 매핑으로 풀어서 사용하자. (자세한건 위에 카테고리 엔티티 내용 참고)**

**엔티티에는 가급적 Setter를 사용하지말자**

* 나중에 리펙토링으로 Setter 들은 제거하길 바람

<br>

<img src=".\images\image-20230212165258592.png" alt="image-20230212165258592"  /> <img src=".\images\image-20230212165326225.png" alt="image-20230212165326225"  />

**컬렉션은 필드에서 초기화 하자.(생성자로 따로 하지말고)**

* 왼쪽이 생성자를 통한것이고, 오른쪽이 필드에서 바로 초기화하는 모습
* null 문제에서 안전하다고 한다.(정확히 이해하지는 못했음)
* 코드도 간결해진다고 한다.(이것은 당연)

<br>

<img src="C:\Users\KoBongHun\Desktop\Git\Study\Spring_Study\spring_second_roadmap\spring_study_1\jpashop\images\image-20230212165545520.png" alt="image-20230212165545520" style="zoom:80%;" /> 

* 스프링부트에 `SpringPhysicalNamingStrategy`가 있어서 이렇게 테이블, 컬럼명이 자동으로 생성된다.
* `orderDate`로 필드 선언만 했는데, 해당 필드명으로 테이블에 컬럼 이름이 `order_date`로 자동 설정

<br>

**모든 연관관계는 지연로딩으로 설정(LAZY)!**

* 즉시로딩(EAGER)은 예측이어렵고, 어떤 SQL이 실행될지 추적하기 어렵다. 특히 JPQL을 실행할 때 **N+1 문제**가 자주 발생한다.
* N+1 문제란? 
* **@XToOne(OneToOne, ManyToOne) 관계는 기본이 즉시로딩이므로 직접 지연로딩으로 설정해야 한다.**

<br>

### 추가) 엔티티에 추가한 로직들

**참고: 스프링필드 주입대신에 생성자 주입을 사용하자.**

<br>

**연관관계 메서드**

<img src=".\images\image-20230212170213514.png" alt="image-20230212170213514"  /> 

<img src=".\images\image-20230212170231822.png" alt="image-20230212170231822"  /> 

* 맨위 코드를 아래 코드처럼 짜면 한줄로 줄여진다는 것
* 다른 엔티티들에도 있으니까 프로젝트에 코드 참고

<br>

**비지니스 로직**

- 서비스가 아닌 엔티티 안에서 비지니스 로직 구현이 가능한것 같은 경우에는 엔티티에서 개발하는걸 추천
  => 이것이 좀 더 객체지향적인 것.
- 자세한 코드들은 프로젝트에서 참고

<br>

**생성 메서드**

- 즉, 엔티티에있는 다양한 속성들을 이 생성메서드 하나로 간편히 다 적용하기 위해서!

  ```java
  @NoArgsConstructor(access = AccessLevel.PROTECTED)
  public class OrderItem {
  ```

  - 이런 형태로 protected도 생성자에 적용해줘야 유지보수하기 좋다.

    <img src=".\images\image-20230212175555385.png" alt="image-20230212175555385" style="zoom:80%;" /> 

    생성 메서드로 생성하길 원하는데 위처럼 new로 엔티티 바로 생성하는걸 막을 수 있음

<br>

**조회 로직**

- 예로 전체 주문 가격 조회 함수 개발!

<br><br>

## 애플리케이션 구현 준비

**구현 요구사항**

<img src=".\images\image-20230212170703968.png" alt="image-20230212170703968" style="zoom:80%;" /> 

<img src=".\images\image-20230212170627197.png" alt="image-20230212170627197"  /> 

* **레퍼지토리**는 DB와 직접 연관이니까 DB와 상호작용하는 코드들을 구현
* **서비스**는 비지니스 로직과 트랜잭션안에서 데이터 변경 등등 코드들을 구현
* **도메인**은 엔티티 계층!! 모두가 사용!!
* **컨트롤러**는 웹 계층에서 활용!!! 웹 계층과 상호작용!!!
* 보통 본인은 이렇게 기본적으로 생각하고 개발을 하는 편이다.

<br><br>

## 회원 도메인 개발

**구현 기능** 

* 회원 등록 
* 회원 목록 조회

<br>

**순서**

* 회원 엔티티 코드 다시보기 
* 회원 리포지토리 개발 
* 회원 서비스 개발 
* 회원 기능 테스트 

<br>

### 회원 리포지토리 개발

* @Repository : 스프링빈으로 등록, JPA 예외를 스프링 기반예외로 예외 변환 
* @PersistenceContext : 엔티티메니저( EntityManager ) 주입
* @PersistenceUnit : 엔티티메니터팩토리( EntityManagerFactory ) 주입 
* save(), findOne(), findAll(), findByName()
  * DB와 연관 메서드
  * **sql 은 테이블에 쿼리하는 개념**
  * **jpql 은 엔티티 객체를 대상으로 쿼리하는 개념**

<br>

### 회원 서비스 개발

* @Service : 스프링빈으로 등록

* @Transactional : 트랜잭션, 영속성 컨텍스트

  * readOnly=true : 데이터의 변경이 없는 읽기 전용 메서드에 사용, 영속성 컨텍스트를 플러시 하지 않으므로 약간의 성능 향상(읽기 전용에는 다 적용)
  * 데이터 베이스 드라이버가 지원하면 DB에서 성능향상

* @Autowired

  * 생성자 Injection 많이 사용, 생성자가 하나면 생략 가능 
  * **하지만, 스프링 필드 주입 대신에 생성자 주입을 사용하자.**
    * **따라서 `@Autowired` 대신에  `@RequiredArgsConstructor` 를 활용**

* 회원가입, 회원 전체 조회, 회원 한명 조회 - join(), findMembers(), findOne()

  * 회원가입에는 중복 회원 검증이 필수!!

    ```java
    private void validateDuplicateMember(Member member) { 
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다."); 
        }
    }
    ```

<br>

### 회원 기능 테스트

**CTRL+SHIFT+T로 테스트코드!!! 간단히 작성!!**

* **live template을 설정에서 검색해서 macro - tdd 도 만들어 뒀다!(간단한 테스트 로직 작성해줌)**

<br>

**테스트 요구사항** 

* 회원가입을 성공해야 한다.
* 회원가입 할때같은 이름이 있으면 예외가 발생 해야한다

<br>

```java
@RunWith(SpringRunner.class) 
@SpringBootTest
@Transactional
public class MemberServiceTest {
    // 회원가입() 생략
    
    @Test(expected = IllegalStateException.class) 
    public void 중복_회원_예외() throws Exception {
        //Given
        Member member1 = new Member(); 
        member1.setName("kim");
        Member member2 = new Member(); 
        member2.setName("kim");
        //When
        memberService.join(member1);
        memberService.join(member2); //예외가 발생해야 한다.
        
        // 상단에 (expected = IllegalStateException.class) 를 선언한 덕분에
        // 아래 주석 코드 2줄이 위의 memberService.join(member2); 한줄만 써도 그대로 적용
        // try { memberService.join(member2); }
        // catch(IllegalStateException e) { return ; }
        
        //Then
        fail("예외가 발생해야 한다.");
    }
}
```

- 회원 가입 테스트는 간단해서 생략
- 스프링부트 테스트를 위해 : @RunWith, @SpringBootTest
  - @RunWith(SpringRunner.class) : 스프링과 테스트 통합
  - @SpringBootTest : 스프링 부트 띄우고 테스트(이게없으면 @Autowired 다 실패)
- 데이터 변경과 롤백을 위해 : @Transactional
  - @Transactional : 반복가능한 테스트지원, 각각의 테스트를 실행할 때 마다 트랜잭션을 시작하고 테스트가 끝나면 트랜잭션을 강제로 롤백 (이 어노테이션이 테스트 케이스에서 사용될때 만 롤백)
- **중요한 개념**
  - 회원가입 테스트 진행해보면 로그에 insert문이 없는걸 볼 수 있음.
  - 이것은 스프링부트가 트랜잭션을 커밋할때 db에 insert하는 특징 때문.
  - 따라서 현재상황에서는 스프링부트가 트랜잭션에 롤백을 해버린다.
    - 따라서 롤백을 할거면 굳이 db에 insert까지 할 필요는 없으니까 로그에 insert문이 없는것.
  - **insert문이 찍히는걸 보고싶다면??**
    - @Rollback(false) 를 추가해주기!
  - **insert문이 찍히면서 롤백까지 하고싶으면??**
    - @Autowired EntityManager em을 하나 추가로 선언하고 **em.flush()** 함수를 사용하면 된다.
    - 왜냐하면 위에서 insert문이 안찍힌 정확한 이유는 **영속성 컨텍스트 플러시를 안한것이기 때문**이라고 한다. => 이해 안된다면, 마지막에 더티체크 같은 이야기가 나올텐데 그부분을 참고

<br><br>

## 상품 도메인 개발

<br>

### 상품 엔티티 개발(비지니스 로직 추가)

**구현 기능** 

* 상품 등록 
* 상품 목록 조회 
* 상품 수정

**순서**

* 상품 엔티티 개발(비즈니스 로직 추가) 
* 상품 리포지토리 개발
* 상품 서비스 개발 
* 상품 기능 테스트
  * 참고 : 여기서는 테스트 코드 생략(이전과 비슷해서)

<br>

### 상품 엔티티 개발(비지니스 로직 추가)

**엔티티 안에서 비지니스 로직 구현이 가능한것 같은 경우에는 엔티티에서 개발하는걸 추천  
=> 이것이 좀 더 객체지향적인 것**

* 재고 증가, 감소 함수를 만들 수 있다.

<br>

### 상품 리포지토리 개발

* save()
  * id 가없으면 신규생성으로 보고 persist() 실행
  * id 가있으면 이미 데이터베이스에 저장된 엔티티를 수정한다고 보고, merge() 를 실행, 자세한 내용은 맨 마지막에 설명(지금은 업데이트 한다고만 이해)
* findOne, findAll 

<br>

### 상품 서비스 개발

**상품서비스는 상품 리포지토리에 단순히 위임만 하는 클래스**

* save, find

<br><br>

## 주문 도메인 개발

**제일 중요한 부분이다.**

**구현 기능** 

* 상품 주문 
* 주문 내역 조회 
* 주문 취소

**순서**

* 주문 엔티티, 주문상품 엔티티 개발
* 주문 리포지토리 개발
* 주문 서비스 개발
* 주문 기능 테스트
* 주문 검색 기능 개발

<br>

### 주문, 주문상품 엔티티 개발

- **`주문` 엔티티에 생성 메서드를 개발해준다!**
  - 즉, 엔티티에있는 다양한 속성들을 이 생성메서드 하나로 간편히 다 적용하기 위해서!
- **`주문` 엔티티에 이번엔 비지니스 로직!!**
  - 주문취소 함수!
    - 참고로 주문취소가 되면 주문상태는 당연히 취소긴 한데 주문했던 상품들도 상태를 취소로 만들어줘야한다.
    - 여기선 그래도 주문 엔티티로 상품들 취소한걸 다 접근 가능할테니 위 내용은 생략.
    - 대신에 취소했으니 남은 재고수량은 원복해준다.
- **`주문` 엔티티에 이번엔 조회 로직 개발!**
  - 전체 주문 가격 조회 함수 개발!
    - 참고로 여기랑 주문상품 엔티티에서도 수정해줘야한다.
    - 상품 수량과 가격을 곱한값이 주문가격이기 때문이다.
- **`주문상품` 엔티티에도 생성 메서드 개발!**
  - 설명은 생략
- **`주문상품` 엔티티에 비지니스 로직 개발!**
  - 재고수량 원복 함수개발!
- **`주문상품` 엔티티에 조회 로직 개발!**
  - 상품 수량과 가격을 곱한 가격 반환 함수개발!

<br>

### 주문 리포지토리 개발

**생략**

<br>

### 주문 서비스 개발

**생략**

* **참고: 주문 서비스의 주문과 주문취소 메서드를보면 비즈니스로직 대부분이 엔티티에 있다.** 
* 서비스 계층은 단순히 엔티티에 필요한 요청을 위임하는 역할을 한다. 이처럼 엔티티가 비즈니스 로직을 가지고 객체지향의 특성을 적극활용 하는것을 `도메인 모델 패턴` 
  * 우리가 현재 적용한 패턴
* 반대로 엔티티에는 비즈니스 로직이 거의 없고서비스 계층에서 대부분의 비즈니스로직을 처리하는 것을 `트랜잭션 스크립트 패턴`
  * 이것은 일반적인 SQL을 썻을때 패턴
* **참고로, 해당 개발부분의 문맥에 맞춰서 적절한 패턴적용을 해주면 된다고함.**

<br>

### 주문 기능 테스트

**생략**

* private으로 클래스내에 그냥 함수 이쁘게 만드는 단축키는 CTRL+ALT+M

<br>

### 주문 검색 기능 개발(중요)

**JPA에서 "동적 쿼리"를 어떻게 해결하는지가 중요하다.**

* **동적쿼리 첫번째 무식한방법**
  - 매우 복잡하며 양이 많은 코드(옛날 방식)
  - 그냥 jpql 을 문자열로 조건에따라 계속 붙여나가는 것
* **동적쿼리 두번째 방법**
  - JPA Criteria 방법 - 위의 무식한방법을 좀 더 편하게 해준다고 함.
  - 이것도 실무에서 쓰는게 아니라 그냥 편안히 확인
    - 치명적인 단점존재..
      - 이걸보면 무슨 쿼리문인지 도저히 알기 어려움
      - 즉, 유지보수가 멘붕
* **동적쿼리 세번째 방법 추천(Querydsl)**
  * 이방법을 제일 추천한다고 한다.
  * Querydsl 강의가 따로 있으므로 추후에 공부하고 적용시켜 보자.
* 참고로 2번째 방법으로 프로젝트에는 코드 적용되어있다.

<br><br>

## 웹 계층 개발

### 홈 화면과 레이아웃

<img src=".\images\image-20230212182944110.png" alt="image-20230212182944110" style="zoom:80%;" />

<br>

```java
@Controller
@Slf4j // => 로그확인 위해
public class HomeController {
    @RequestMapping("/") // 당연히 @GetMapping 을 사용해도 된다는점 참고
    public String home() {
        log.info("home controller"); // @Slf4j 통해서 로그 사용
        return "home";
    }
}
```

* **log 사용은 @Slf4j 어노테이션 활용**
* **웹 관련해서는 [MVC](https://github.com/BH946/spring_first_roadmap/tree/main/spring_study_1) 게시글 참고**
  * **여기서는 못보던 내용들만 정리**

<br>

**Bootstrap 에서 css들 다운**

- 전부 복사해서 staic 위치에 css, js전부 붙여넣기
- 혹시 바로 적용안되면 강제로 다 빌드 또는 Synchorinze
- 참고로 css 한개 임의로 더 만들어서 적용했음

<br>

### 회원 등록, 목록 조회

**회원 등록**

<img src=".\images\image-20230212183029533.png" alt="image-20230212183029533" style="zoom:80%;" /> 

* `MemberForm.java` 만들어서 Form 전용으로 타입 사용(이런 방식을 추천. 엔티티 바로 사용이 아닌)
  * **실제로 개발하다보면 화면의 form에서 컨트롤러로 넘어오는 데이터와 도메인에서 요구하는 데이터가 다를수도 있어서 차라리 이런식으로 개발하는걸 추천**
  * @NotEmpty("에러 메세지 관련") 도 이용
* `MemberController.java` 생성해서 @Get, @Post
  * MemberForm 타입 가져다 쓸때 @Vaild 어노테이션 선언해주면 @NotEmpty를 확인 가능
  * 원래 에러뜰때 코드실행도안하고 팅기는데, BindingResult result를 매개변수에 추가해주면 위의 인자 덕분에 내부 코드를 실행한다.
    * 이때 return을 다시 html로 보내고, html에서 \#fileds.hasErros 를 통해서 에러감지한 result를 사용!!
    * @NotEmpty("에러 메세지 관련")을 name에 적용했었는데 name값이 "에러 메세지 관련" 으로 바뀌면서 해당 name 값을 출력해서 메시지를 보여줄 수 있었던 것.

<br>

**목록 조회**

<img src=".\images\image-20230212183105872.png" alt="image-20230212183105872" style="zoom:80%;" /> 

* `<tr th:each="member : ${members}">` 같은 타임리프 문법을 활용해서 출력
* ?문법도 지원합니다(널세이프) => 널이면 더이상 출력안한다는 것
* 참고로 여기선 Member.java인 엔티티 타입을 바로 사용했음
  * **API만들땐 절때 지금처럼 엔티티를 바로 가져와 출력하면 안된다. 즉, 외부에 바로 반환하지말것.**
  * 다행인건 템플릿엔진은 서버사이드에서 진행되기 때문에 가져다 사용해도 상관은없는것!
  * 물론 권장하는건 엔티티를 바로 사용하기보단 가공해서 사용하는걸 권장

<br>

### 상품 등록, 목록 조회, 수정

**상품 등록**

<img src=".\images\image-20230212183249151.png" alt="image-20230212183249151" style="zoom:80%;" /> 

<br>

**목록 조회**

<img src=".\images\image-20230212183327898.png" alt="image-20230212183327898" style="zoom:80%;" /> 

<br>

**상품 수정(상품 등록 폼과 동일)**

<img src=".\images\image-20230212183412094.png" alt="image-20230212183412094" style="zoom:80%;" />

* **중요한 부분**
* 버튼연결 url을 `items/{itemId}/edit` 으로 이동하게 했으니 상품 컨트롤러에서 이부분 잘 매핑 해야함.
* @PathVariable("itemId")
  * @PathVariable 과 @RequestParam어노테이션은 url파라미터로 전달받은 value를 메서드의 파라미터로 받을 수 있게 해주는 어노테이션이다.
*  @ModelAttribute("from")
  * model.addAttribute 에도 담기고, form서밋 때 html에 있는 form 데이터를 매핑해서 변수에 담아줄거임

<br>

### 상품 주문, 주문 목록(검색, 취소)

**상품 주문**

<img src=".\images\image-20230212183431941.png" alt="image-20230212183431941" style="zoom:80%;" /> 

* @RequestParam 은 form 서밋 방식
  * 예를들어 form에 select태그들 name속성이름으로 값 매핑해서 가져와줌!

<br>

**주문 목록(검색, 취소)**

<img src=".\images\image-20230212183457331.png" alt="image-20230212183457331" style="zoom:80%;" /> 

* 자바스크립트 코드도 호출해서 사용
* `<script>` 문으로 작성했으며 post 방식으로 cancel을 전달했음

<br><br>

## 웹 계층에서 사용한 th문법

**공문에 타임리프 메뉴얼 다 있으니까 참고!!**

-  th문법으로 다른 html 가져오는것 사용했음(fragments - header.html, footer.html, bodyHeader.html)
  - 해당 th문법은 replace, fragment 사용
    - 연결될 html에서 fragment로 이름 지정
    - 연결할 html에서 replace로 위에 이름지정한 이름으로 연결

- form태그에 th:object="${memberForm}" 는 memberForm을 사용하겠다는 것
- "*{name}" 같이 앞에 *을 붙인건 위에 선언한 object에 name값 가져온다는 것!
- th:field="*{city}" 이것은 id=city, name=city하는것과 동일한 역할을 한다.
  - 따라서 이 문법은 memberForm.city값을 가져옴 + id=city, name=city 까지 자동 설정
- ${#fileds.hasErros('name')} 을 통해서 에러 감지 가능(컨트롤러에서 if(result.hasErrors()){} 형태로 코드짜서 에러가 BindingResult result에 담겨있는데, 이걸 html에서 fileds가 result값 가져와서 사용가능
- `<tr th:each="member : ${members}">` 같이 for each같은 문법도 존재

<br><br>

## 중요 개념 : 변경 감지와 병합(merge)

**정말 중요한 개념이라서 완벽히 이해해야 한다.**



<img src=".\images\image-20230212190058075.png" alt="image-20230212190058075"  /> 

**준영속 엔티티?**

* **영속성 컨텍스트가 더는 관리 하지 않는 엔티티를 말한다.**
  * **즉, JPA는 준영속 엔티티를 관리 안하기 때문에 db 업데이트 쿼리를 안쏨**
* 여기서는 itemService.saveItem(book) 에서 수정을 시도하는 Book 객체다. 
* Book 객체는 이미 DB 에한번 저장되어서 식별자가 존재한다. 
* 이렇게 임의로 만들어낸 엔티티도 기존 식별자를 가지고 있으면 준영속 엔티티로 볼 수 있다.
* 다만 이 준영속 엔티티가 saveItem(book)을 타고 들어가보면 merge함수 사용해서 해결한 상태임!

<br>

**준영속 엔티티를 수정하는 2가지방법** 

* 변경 감지( dirty checking ) 기능 사용

  <img src=".\images\image-20230212190554364.png" alt="image-20230212190554364"  /> 

  * Flush:영속성 컨텍스트의 변경 내용을 DB 에 반영하는 것
  * 플러싱 할때 더티 체킹이 발생!!
    * JPA가 트랜잭션 커밋시점에 값이 바뀐지점을 찾아서 db에 update쿼리 쏴주고 트랜잭션 커밋

* 병합( merge ) 사용

  <img src=".\images\image-20230212190628958.png" alt="image-20230212190628958"  /> 

  * 아까 준영속 엔티티였던 book 코드에서 save함수 타고 들어가보면 여기까지 오게되는데, 여기서 merge함수를 통해서 더티체크 그림 봤듯이 그 코드를 진행해주는 느낌으로 영속성 엔티티로 변경해준것!!

  * 물론 merge는 조금 차이가 있는데 이건 ppt 확인 & 여기서는 간략히만 소개

    <img src=".\images\image-20230212190741656.png" alt="image-20230212190741656"  /> 

    * 이것이 merge함수와 완전 동일한 코드라고 이해하면 된다. 즉, 저렇게 영속성 엔티티로 완전 바꾼 값을 그대로 반환함!

    <img src=".\images\image-20230212190817632.png" alt="image-20230212190817632"  /> 

    * 따라서 이곳에 merge(item)의 item은 당연히 아직 준영속 엔티티고 이 merge가 반환한 Item merge 이 변수가 영속성 엔티티로 봐야하기 때문에 뒤에 더 update할게 있으면 이 변수를 활용해줘야한다.

<br>

**merge 사용시 꼭꼭 주의할점!!**

<img src=".\images\image-20230212190925612.png" alt="image-20230212190925612"  />

- 예로 지금 수정하는 상황인데, 기존에 상품의 가격이 만원 db에 저장되어있는상황이고 수정할때 상품 가격은 수정못하게 하려고 저렇게 주석을 해놨다면??
- merge의 경우 모든!! 모~든 속성을 다 바꿔버리기 때문에 db에 저렇게 가격이 null 이 들어간다.
- 매우매우 위험.
- 따라서 강사님은 더티체크를 추천

<br>

**결론적으로 최종 추천 코드**

```java
/**
* 영속성 엔티티로 만들었기 때문에 영속성 컨텍스트가 자동 변경
*/
//    @Transactional
//    public void updateItem(Long id, String name, int price, int stockQuantity){
//        Item item = itemRepository.findOne(id);
//        item.setName(name);
//        item.setPrice(price);
//        item.setStockQuantity(stockQuantity);
//    }

// 아래가 권장 코드
@Transactional
public void updateItem(Long id, UpdateItemDto itemDto){
    Item item = itemRepository.findOne(id);
    item.change(itemDto.getName(), itemDto.getPrice(), itemDto.getStockQuantity());
}
```

* 위의 코드도 준영속 엔티티가 아닌 영속성 엔티티로 설정한 잘 만든 코드인데, 좀 더 다듬을 수 있다.

* 파라미터가 많은걸 Dto 방식으로 바꾸는것과 setter를 최대한 사용하지 않게끔!!

  ```java
  @Getter @Setter
  @RequiredArgsConstructor // 생성자
  public class UpdateItemDto {
      private final String name;
      private final int price;
      private final int stockQuantity;
  
  //    UpdateItemDto(String name, int price, int stockQuantity) {
  //        this.name = name;
  //        this.price = price;
  //        this.stockQuantity = stockQuantity;
  //    }
  }
  ```

  * Dto 구조

    

  ```java
  // updateItem에 사용(setter를 남발하지 않기 위함)
  public void change(String name, int price, int stockQuantity) {
      this.name = name;
      this.price = price;
      this.stockQuantity = stockQuantity;
  }
  ```

  * change 함수의 구조(Item 엔티티에 정의)

<br><br>

## 마무리

**다음 활용편2 에서는 API 개발을 공부할 예정이다.**

