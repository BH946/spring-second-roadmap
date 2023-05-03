# Intro..

**실전! 스프링 부트와 JPA 활용2 - API 개발과 성능 최적화**

* 인프런 강의 듣고 공부한 내용입니다.

* **유용한 단축키**
  * `Alt + Insert` : getter, setter, constructor 등 자동 생성
  * `Ctrl + Alt + V` : 변수 선언부를 자동 작성
  * `Ctrl + Alt + M` : 코드 리팩토링하기 쉽게끔 함수 자동 생성
  * `Ctrl+T->extra method` : 코드 리팩토링하기 쉽게끔 드래그한 코드를 하나의 함수로 자동 생성
  * `Ctrl + Shift + Down/Up` : 메소드 코드 통째로 위, 아래 자리 이동 가능
  * `Alt + Shift + Down/Up` : 코드 한줄을 위, 아래 자리 이동 가능
  * `Ctrl + D` : 코드 한줄 바로 아래에 복제
  * `Ctrl + Alt + Shift` : 멀티 커서 가능
  * `Shift + F6` : 변수명을 한번에 바꿀 때 사용
  * `Alt + 1 ` : 왼쪽 프로젝트 폴더 구조 열기
  * `Alt + F12` : 터미널 창 열기
  
* **생소했던 문법**

  ```java
  // stream ...
  List<OrderItem> orderItems = order.getOrderItems();
  orderItems.stream().forEach(o -> o.getItem().getName()); // Lazy 강제 초기화
  
  // stream ...
  List<MemberDto> result = findMembers.stream() // stream으로 풀기
          .map(m -> new MemberDto(m.getName())) // map함수 사용! (이때 DTO로 변환)
          .collect(Collectors.toList()); // 반환값 list로 변환!
  
  // getResultList()
  return em.createQuery("select i from Item i", Item.class)
                  .getResultList(); // 쿼리문에서 종종 보였음
  ```

<br>

해당 프로젝트 폴더는 강의를 수강 후 강의에서 진행한 프로젝트를 직접 따라 작성했습니다.

따로 강의 자료(pdf)를 주시기 때문에 필요할때 해당 자료를 이용할 것이고,

이곳 README.md 파일에는 기억할 내용들만 간략히 정리하겠습니다.

* 자세한 코드가 궁금하다면, 올려둔 프로젝트에서 코드확인(커밋 위주로 보면 보기 편할 것)
* 정리하다 보니 좀 많이 정리하는 감이..
* **참고 : 필자는 `~/spring_study_dbh2/jpashop` 경로로 db파일 옮겼다!!**

<br><br>

# API 개발 고급편 정리

**이번 강의에서는 OSIV와 성능 최적화 와 이것(=API 개발 고급 정리) 이 핵심이다!!**    

**따라서 전체적 흐름은 간략히 소개할 것이며, 자세한건 프로젝트에서 코드를 볼 것!!**

<br><br>

## 조회의 2가지 방법!!

* **엔티티 조회**
  * 엔티티를 조회 해서 그대로 반환: V1
    * 당연히 외부노출 하지 말라고 몇번이나 언급했음!! ( 이유도 이젠 생략하겠다 )
  * 엔티티를 조회 후 DTO로 변환: V2 
    * DTO로 변환해서 의존성 제거하는것도 몇번이나 언급!!( 내부까지 전부 관련된거 싹다 할 것!! )
  * 페치조인으로 쿼리 수 최적화: V3 
    * 실무에선 페이징 할일이 많기 때문에 페이징의 한계가 아쉬운 단계
    * 어디서 쓰는지 간단히 소개하자면?
      * 데이터 백만개면? 페이징 단위로 적당히 끊어서 api로 넘기거나 해야할거다.
      * 또한, 게시물 보여주는 경우 10~20개 연관 게시물을 빠르게 보여줘야하는데 이때도 페이징 단위로 빠르게 가져와야할거다.
  * 컬렉션 페이징과 한계돌파: V3.1
    * 컬렉션은 페치조인시 페이징이 불가능 
    * ToOne 관계는 페치 조인으로 쿼리 수 최적화, 페이징도 당연히 문제없음
    * 따라서 컬렉션은 페치조인 대신에 지연로딩을 유지하고, `hibernate.default_batch_fetch_size`, `@BatchSize` 로 최적화 (전역, 부분 이라는 차이점 있음)

* **DTO 직접 조회**
  * JPA에서 DTO를 직접 조회: V4
  * 컬렉션 조회 최적화 - 일대다 관계인 컬렉션은 IN 절을 활용해서 메모리에 미리 조회해서 최적화: V5 
  * 플랫 데이터 최적화 - JOIN 결과를 그대로 조회후 애플리케이션에서 원하는 모양으로 직접 변환: V6

<br><br>

## 권장 순서

1.  **엔티티 조회** 방식으로 우선접근
    1.  페치조인으로 쿼리 수를 최적화
    2.  컬렉션 최적화
        1.  페이징 필요O `hibernate.default_batch_fetch_size` , `@BatchSize` 로 최적화
        2.  페이징 필요X => 페치조인사용
2.  엔티티 조회 방식으로 해결이 안되면 DTO 조회 방식 사용
3.  DTO 조회 방식으로 해결이 안되면 NativeSQL or 스프링 JdbcTemplate 활용

<br>

**이러한 권장 순서의 근거는??**

* 엔티티 조회 방식은 페치 조인이나, `hibernate.default_batch_fetch_size` , `@BatchSize` 같이 코드를 거의 수정하지 않고, 옵션만 약간 변경해서, 다양한 성능 최적화를 시도할 수있다.   
  반면에 DTO를 직접조회 하는방식은 성능을 최적화 하거나 성능 최적화 방식을 변경할때 많은코드를 변경 해야한다
* 개발자는 성능 최적화와 코드 복잡도 사이에서 줄타기를 해야한다.   
  항상그런것은 아니지만, 보통 성능 최적화는 단순한 코드를 복잡한 코드로 몰고간다.
* 엔티티 조회 방식은 JPA가 많은 부분을 최적화 해주기 때문에, 단순한 코드를 유지하면서, 성능을 최적화 할 수있다.  
  반면에 DTO 조회방식은 SQL을 직접 다루는것과 유사하기 때문에, 둘 사이에 줄타기를 해야한다.
  * 강사님께서 기존에는 성능 최적화를 위해서 복잡하게 코드를 작성했다고 하는데 알고보니 `@batchsize` 기능 같은것을 직접 한땀한땀 코드를 수정해서 바꾼 그런 작업이였다고한다. 
  * 이때 엔티티 조회 방식은 JPA가 많은 부분을 최적화 해준다는것을 더 이해하게 되었다고 한다.

<br>

**엔티티 조회는 위에서 권장순서 자세히 설명을 했다. DTO 조회 방식의 선택지는??**

* DTO로 조회하는 방법도 각각 장단이 있다.   
  V4, V5, V6에서 단순하게 쿼리가 1번 실행된다고 V6이 항상 좋은방법인 것은 아니다.
* V4는 코드가단순하다. 특정 주문 한건만 조회하면 이 방식을 사용해도 성능이 잘 나온다.   
  * 예를들어서 조회한 Order 데이터가 1건이면 OrderItem을 찾기 위한 쿼리도 1번만 실행하면 된다.
* V5는 코드가 복잡하다. 여러주문을 한꺼번에 조회하는 경우에는 V4 대신에 이것을 최적화한 V5 방식을 사용해야 한다.   
  * 예를들어서 조회한 Order 데이터가 1000건인데, V4 방식을 그대로 사용하면, 쿼리가 총 1 + 1000번 실행된다. 여기서 1은 Order 를 조회한 쿼리고, 1000은 조회된 Order의 row 수다.   
    V5 방식으로 최적화하면 쿼리가 총 1 + 1번만실행된다. 상황에 따라 다르겠지만 운영환경에서 100배 이상의성능 차이가 날수있다.
* V6는 완전히 다른 접근방식이다.   
  쿼리 한번으로 최적화 되어서 상당히 좋아보이지만, Order를 기준으로 페이징이 불가능하다. 
  * 실무에서는 이정도 데이터면 수백이나, 수천건 단위로 페이징 처리가 꼭 필요하므로, 이경우 선택하기 어려운 방법이다.  
    그리고 데이터가 많으면 중복 전송이 증가해서 V5와 비교해서 성능 차이도미비하다.

<br><br>

## 페이징 불가능? 1+N? 컬렉션 최적화?

**어떤 문제들인지 하나하나 설명하겠다.**

* **페이징 불가능?**
  * order와 orderitem이 있고, orderitem이 데이터가 더 많다고 가정하자.
  * 이것을 join할 때 orderitem이 데이터가 더 많기 때문에 이것을 기준으로 order데이터가 추가로 복제된다.
  * 이때 order를 기준으로 페이징을 하면?? 불가능하다. order는 추가로 복제되었기 때문이다.
    * 당연히 orderitem을 기준으로 페이징 하는건 문제없다. 다만, 이 형태로 페이징은 매우 비추천한다.
* **1+N?**
  * order와 member, delivery가 있고, order 쿼리를 보냈는데 order 데이터가 4개(=N)를 결과로 받는다고 가정하자. + 전부 지연로딩으로 가정( 즉시로딩은 당연히 한번에 다 가져오니까 )
    * member, delivery는 지연로딩으로 인해 각각 4번씩 쿼리문을 날리게 된다.
    * 이 경우엔 1+4+4번 쿼리문이 날라가는 것이다.
    * 이를 페치조인으로 해결하는 것이다! => 쿼리 1번으로 조회 끝!!
    * 다만, ToOne 관계일때 가능하며 ToMany 관계는 좀 더 처리해야한다.
  * order와 orderitem이 있고, order 쿼리를 보냈는데 order 데이터가 2개를 결과로 받는다고 가정하자. + 전부 지연로딩 + 페치조인까지 했다고 가정.
    * 여기서 ToOne 관계였으면 문제없이 끝난다. 하지만, order는 orderitem와 ToMany 관계이다.
    * 여기서 부턴 컬렉션 최적화의 문제이다.
* **컬렉션 최적화?**
  * 일반적으로 join할 때 두 테이블의 데이터가 다르면, 작은 테이블은 추가로 복제가 된다.
  * 이때 ToMany 관계를 페치 조인을 하면 `distinct` 옵션으로 중복 제거를 한다. 
    * 이를 컬렉션 페치조인 이라고 한다. 컬렉션이기도 하면서 `distinct` 옵션을 적용하는 특징이다.
    * 다만, 컬렉션 페치조인은 1개만 사용할 수 있다.
      * 2개만 사용해도 일대다대다 관계가 되버리는데 데이터가 부정합하게 조회될 수 있다.
    * 또한, 페이징이 불가능하다.
      * 페치조인에 `distinct` 를 쓴다고 해서 DB에서 중복이 완벽히 제거가 되는게 아니라  
        앱단에서 다시 중복검사를 통해서 중복이 완벽히 제거가 되는것이기 때문에 페이징이 불가능하다.
      * 즉, DB에는 join으로 인해 다(=Many)의 개수만큼 데이터 생성되기 때문에 일(=One) 이 그만큼 다(=Many) 만큼 복제가 되어서 전체 데이터의 순서가 뒤틀리게 된다.
  * 컬렉션 최적화를 하면서 페이징도 가능하게 하고싶다면??
    * 컬렉션 페치조인을 사용하지 않고, 지연로딩을 사용해서 하이버네이트 옵션을 활용하라!!
      * `hibernate.default_batch_fetch_size , @BatchSize` : 각각 전역, 지역 size 설정이다.
      * 이 옵션을 사용하면 컬렉션이나, 프록시 객체를 한꺼번에 설정한 size 만큼 IN 쿼리로 조회한다.

<br><br>

# OSIV와 성능 최적화

**자세한건 아래 마지막 부분쯤에 정리한것 확인**

<img src=".\images\image-20230309023958076.png" alt="image-20230309023958076" style="zoom:80%;" /> 

<img src=".\images\image-20230309024031719.png" alt="image-20230309024031719" style="zoom: 80%;" /> 

<br><br>

# API 개발 과정

**'JPA 활용 1편' 에서는 간단히 웹을 만들고, DB를 연동해보는 그런 과정을 진행해보았다.**  

**'JPA 활용 2편' 에서는 API를 개발해보려고 한다.  
어찌보면 1편에서 개발한 Controller 부분들이 이번 API 개발하는 부분과 유사함을 느끼고 조금 햇갈릴 수 있을것이므로 의문점을 먼저 해결해보자.**

* `@Controller, @RestController` 의 동작 방식을 이해하면 된다.
  * @Controller - View 반환
  * @Controller + @ResponseBody - Data 반환
  * @RestController - Data 반환
* `@Controller + @ResponseBody = @RestController` 이다.
* 요즘 API스펙에서는 JSON으로 반환을 많이 하며, 예전에는 XML형태로 반환을 많이 했었다.

<br>

**< 일반적인 Spring MVC 처리과정 >**

<img src=".\images\image-20230307212046867.png" alt="image-20230307212046867"  /> 

<br>

**< Controller로 View 반환 >**

<img src=".\images\image-20230307212319945.png" alt="image-20230307212319945"  /> 

<br>

**< Controller로 Data 반환 - @ResponseBody 추가 사용 >**

<img src=".\images\image-20230307212147895.png" alt="image-20230307212147895"  /> 

<br>

**< RestController로 Data 반환 >**

<img src=".\images\image-20230307212413682.png" alt="image-20230307212413682"  /> 

<br>

**API를 개발하는 과정을 소개하려고 한다.**

1. API 개발 기본 - CRUD
2. API 개발 고급 - 지연 로딩(LAZY)과 조회 성능 최적화
3. API 개발 고급 - 컬렉션 조회 최적화
4. API 개발 고급 - 실무 필수 최적화

<br><br>

## API 개발 기본 - CRUD

**가장 기본이되는 CRUD 형태를 먼저 개발을 해보자.**

**`api` 패키지를 추가해서 구분지어주고, 하위에 `MemberApiController.java` 를 개발해주자.**

<br>

### 회원 등록 API

**등록 V1: 요청 값(RequestBody)으로 Member 엔티티를 직접 받는다.**

```java
@PostMapping("/api/v1/members")
public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
    Long id = memberService.join(member); // 회원 등록
    return new CreateMemberResponse(id);
}
```

* **`@RequestBody @Valid Member member`**

  * `@RequestBody` 의 역할은 Json얻은걸 Member 객체로 알아서 매핑

  * `@Valid` 하는 이유는 `@NotEmpty` 쓰려고!! 없이 하면 Null로 그냥 호출이 다 가니깐!

    ```java
    @NotEmpty(message = "회원 이름은 필수 입니다") // Null 대신 해당 메시지 출력
    ```

* **문제점**

  * 엔티티에 프레젠테이션 계층을 위한 로직이 추가된다.
  * 엔티티에 API 검증을 위한 로직이 들어간다. (@NotEmpty 등등)
  * 실무에서는 회원 엔티티를 위한 API가 다양하게 만들어지는데, 한 엔티티에 각각의 API를
    위한 모든 요청 요구사항을 담기는 어렵다.
  * 엔티티가 변경되면 API 스펙이 변한다.

* **결론**
  * API 요청 스펙에 맞추어 별도의 DTO를 파라미터로 받아서 해결한다.

* **포스트 맨으로 동작 확인**

  * name을 hello로 저장하기 위해 Body에 아래 사진처럼 담아서 POST 방식으로 전송하면,
  * DB에 생성되고 id값 반환을 받게 된다.

  <img src=".\images\image-20230307224000506.png" alt="image-20230307224000506"  /> 

<br>

**등록 V2: 요청 값(RequestBody)으로 Member 엔티티 대신에 별도의 DTO를 받는다.**

```java
@PostMapping("/api/v2/members")
public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
    Member member = new Member();
    member.setName(request.getName());

    Long id = memberService.join(member);
    return new CreateMemberResponse(id);
}
```

* DTO 역할을 하는것은 별도로 만든 `CreateMemberRequest` 클래스 이다.

  * ```java
    @Data
    static class CreateMemberRequest {
        @NotEmpty // 이렇게 쓸 수 있다는것도 큰 장점
        private String name;
    }
    ```

    * 외부에서 만들어도 되고, 여기선 안에 만든거라 간단히 선언!
    * `@Data 또는 @Getter` 가 없으면 에러가 뜰테니 참고

* 엔티티와 프레젠테이션 계층을 위한 로직을 분리할 수있다.

* 엔티티와 API 스펙을 명확하게 분리할 수 있다. 

* 엔티티가 변해도 API 스펙이 변하지 않는다.

* **결론**

  * **API설계 시 실무에서는 엔티티를 외부에 노출시키면 안된다.**

* **DTO로 개발시 안전성 보장!! (아래 사진 확인)**

  <img src=".\images\image-20230307224355564.png" alt="image-20230307224355564"  /> 

  * 이처럼 엔티티 필드명 "name" -> "username" 으로 맘대로 바꿨을때 위 로직에서 컴파일 에러(빨간글자)!!
  * 따라서 유지보수가 훨씬 편해진다.

<br>

### 회원 수정 API

**회원수정 API인 아래 `updateMemberV2` 함수는 회원 정보를 부분 업데이트한다.** 

* 여기서 PUT 방식을 사용했는데, PUT은 전체 업데이트를 할때 사용하는 것이 맞다. 
* **부분 업데이트**를 하려면 PATCH를 사용하거나 **POST를 사용하는것이 REST 스타일에 맞다.**

```java
// @PutMapping("/api/v2/members/{id}") // 전체 수정은 PUT 사용
@PostMapping("/api/v2/members/{id}") // 부분 수정은 POST 사용
public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id, @RequestBody @Valid UpdateMemberRequest request) {
    memberService.update(id, request.getName()); // update함수 새로 개발(변경 감지를 사용해서 데이터를 수정)
    Member findMember = memberService.findOne(id); // update된 값으로 member 엔티티 가져옴
    return new UpdateMemberResponse(findMember.getId(), findMember.getName());
}
```

* `UpdateMemberRequest` 는 DTO용으로 따로 만든 클래스이다.

* 서비스 계층에서 update함수를 새로 개발하였다.

  ```java
  /**
  	* 회원 수정
  */
  @Transactional
  public void update(Long id, String name) {
      Member member = memberRepository.findOne(id);
      member.setName(name); // Flush:영속성 컨텍스트의 변경 내용을 DB 에 반영하는 것
  }
  ```

  * JPA가 트랜잭션 커밋시점에 값이 바뀐지점을 찾아서 db에 update쿼리 쏴주고 트랜잭션 커밋

* 반환할 때 `UpdateMemberResponse` 클래스 만들어서 반환하고 싶은 값들로 구성

  ```java
  @Data
  @AllArgsConstructor // 생성자 대신 만들어 줄 테니까 필드만 선언
  static class UpdateMemberResponse {
      private Long id;
      private String name;
  }
  ```

<br>

**포스트맨 테스트**

<img src=".\images\image-20230308020050790.png" alt="image-20230308020050790" style="zoom:80%;" /> 

<img src=".\images\image-20230308020123766.png" alt="image-20230308020123766" style="zoom:80%;" /> 

<br>

### 회원 조회 API

**조회 V1: 응답 값으로 엔티티를 직접 외부에 노출한다.**

```java
@GetMapping("/api/v1/members")
public List<Member> membersV1() {
    return memberService.findMembers();
}
```

* 문제점
  * 엔티티에 프레젠테이션 계층을 위한 로직이 추가된다.
  * 기본적으로 엔티티의 모든 값이 노출된다.
  * 응답 스펙을 맞추기 위해 로직이 추가된다. (@JsonIgnore, 별도의 뷰 로직 등등)
  * 실무에서는 같은 엔티티에 대해 API가 용도에 따라 다양하게 만들어지는데, 한 엔티티에 각각의
    API를 위한 프레젠테이션 응답 로직을 담기는 어렵다.
  * 엔티티가 변경되면 API 스펙이 변한다.
  * 추가로 컬렉션을 직접 반환하면 항후 API 스펙을 변경하기 어렵다.(별도의 Result 클래스
    생성으로 해결)
* 결론
  * API 응답 스펙에 맞추어 별도의 DTO를 반환한다.

<br>

**조회 V2: 응답 값으로 엔티티가 아닌 별도의 DTO를 반환한다.**

```java
@GetMapping("/api/v2/members")
public Result membersV2() {
    List<Member> findMembers = memberService.findMembers();
    //엔티티 -> DTO 변환
    List<MemberDto> result = findMembers.stream() // stream으로 풀기
        .map(m -> new MemberDto(m.getName())) // map함수 사용! (이때 DTO로 변환)
        .collect(Collectors.toList()); // 반환값 list로 변환!
    return new Result(result); // 오브젝트로 배열 감싸서 반환
}
```

* **DTO는?** 

  ```java
  @Data
  @AllArgsConstructor // 생성자 자동! 필드만 선언!
  static class MemberDto {
      private String name;
  }
  ```

  * 참고로 생성자 주입 방식인 `@RequiredArgsConstructor` 와 햇갈리지 말것

* **반환값이 배열로 반환이 되기 때문에 마지막엔 객체로 감싸서 반환해주는게 필요하다.**

  <img src=".\images\image-20230308021524143.png" alt="image-20230308021524143" style="zoom:67%;" /> ==> =>               

  * 그림처럼 배열엔 `"count" : 4` 같은 데이터를 바로 못 집어넣는 형태이다.

  * **따라서 최상위는 객체로 감싸주고, 안에 "data"같은 키의 값 부분에 왼쪽 그림의 배열 데이터들을 넣는게 일반적인 JSON 응답 구조이다.**

    ```java
    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int count; // 이처럼 객체로 감싸면 다른 변수도 쉽게 삽입 가능
        private T data;
    }
    ```

<br><br>

## API 개발 고급 들어가기전 샘플 데이터 생성

샘플 데이터를 삽입하는 자세한 코드는 프로젝트 코드에서 확인할 것!

* `InitDb.java` 파일 입니다.

  * 앱실행시 총 주문 2개 자동 insert
  * userA
    * JPA1 BOOK
    * JPA2 BOOK
  * userB
    * SPRING1 BOOK
    * SPRING2 BOOK

* 여기서 사용된 중요 메소드는 `@PostConstruct, @PreDestroy` 이다.

  * 앱 실행시점에 해당 메소드 실행
  * 앱 종료시점에 해당 메소드 실행

* 또 중요한점은 `@PostConstruct` 가 선언된 함수에서 바로 서비스 메소드를 구현하지 말고 따로 구현하라는 것. (즉, 트랜잭션 들어가는 내용의미 = 데이터 변경)

  ```java
  @PostConstruct // 앱 실행 시점에 해당 메소드 실행(참고 : @PreDestroy 는 종료시점)
  public void init() {
      initService.dbInit1(); // 따로 서비스 함수 구현할 것
      initService.dbInit2();
  }
  
  @Component // 순서는 Component 가 먼저 스프링 빈에 등록되고 @PostConstruct 부분을 실행
  @Transactional // 트랜잭션 같은거 할 때 문제 있을 수 있기 때문에 따로 구현하라고 함
  @RequiredArgsConstructor
  static class InitService {
      private final EntityManager em;
      public void dbInit1() { ... 생략 }
  ```

  * 스크링 빈의 라이프사이클 특성상 트랜잭션 같은거 할 때 문제 없으려면 따로 구현이 필요
  * 실행 순서는 Component 가 먼저 스프링 빈에 등록되고 @PostConstruct 부분을 실행

<br>

**데이터들이 삽입된 모습**

<img src=".\images\image-20230308023310542.png" alt="image-20230308023310542" style="zoom: 80%;" /> 

<br><br>

## API 개발 고급 - 지연 로딩(LAZY)과 조회 성능 최적화

**API 개발 고급 부분에서는 조회만 다룰것이다 (보통 조회에서 주로 최적화를 한다고 했던것 같다..?)**  

* **주문 + 배송정보 + 회원을 조회하는 API를 만들자**
* **여기선 xToOne(M:1, 1:1) 에서의 최적화를 주로 다룰 것이다@!@!@!@**
  * **참고로 order->member, order->delivery 등등.. 가 xToOne관계였었음 (예전에 했었음 기억!!)**

**조회에서 성능을 최적화 하는 좋은 방법들로만 소개하겠다.**

**현업에서 API 문제같은 경우 대부분 여기서 소개하는 부분들로 다 해결이 되었다고 한다.**

<br>

### v1/simple-orders : 엔티티 직접 노출

**`api/OrderSimpleApiController.java` 파일에 만들것이다!!**

**간단히 `orderRepository.findAllByString(..)` 을 통해서 모든 데이터를 조회하면 많은 문제들이 나타남.**

* **첫번째로 무한반복 문제( 양방향 관계 때문 )**
  * jackson이 Order엔티티에 Member, Delivery 있으니까 접근해서 출력할텐데
  * 각각 접근하면 또 Order내용 있어서  Order에 접근하면.... 무한 반복...
  * `@JsonIgnore` **을 통해서 양방향 관계 있을땐 한쪽에는 이걸 선언해줘야 해결!**
    * 실제로 코드 작성했을땐 Member, Delivery, OrderItem 엔티티들에 각각 @JsonIgnore 적용했음
* **두번째 ByteBuddy... 에러 발생( 지연로딩 때문 )**
  * `@ManyToOne(fetch=LAZY)` 선언처럼 지연로딩의 효과는 즉시 DB를 접근하는게 아니고, 사용(필요)하게 될 때 DB에 접근하는 효과가 있었다.
    - 하지만 초기값을 null을 넣어둘수는 없으므로 `ByteBuddyInterceptor()` 라는 클래스 객체를 넣어둔다!!!
    - 이것은 프록시 객체이며, 위에서 언급했었던 "사용(필요)하게 될 때 DB에 쿼리문 날리는 것"을 프록시 초기화라고 한다. => **프록시 기술 관련!**
  * 다만 JSON으로 또 나타낼때 jackson라이브러리는 프록시 기술관련을 모를뿐더러 `ByteBuddyInterceptor()` 라는 클래스도 모르기에 에러를 출력하는것이다.
  * **여기서 해결방안이 있는데, 지연로딩일때는 그냥 아무것도 뿌리지 말라고하는 명령이 있다!! (NULL!!)**
    * **Hibernate5Module을 @Bean 등록해주면 끝이다.**
      * 하이버네이트 하이버 모듈 설치(버전 빼도 알아서 스프링부트가 최적화된 버전 설치해주는것 기억)
    * 효과 : 알아서 초기화 된 프록시 객체만 노출, 초기화 되지 않은 프록시 객체는 노출 안함(NULL)
    * **또한, 하이버네이트 옵션에 강제로 LAZY 로딩 하는 옵션도 있어서 데이터를 다 뽑아 볼수도있다. ( 알 필요 없음)**
      - 물론 이 옵션의 존재만 알도록! 쓸일 없음.
      - 지연로딩을 통해서 애초에 필요없는 쿼리들은 안 쏘아보낸건데 강제로 LAZY 로딩은 이 쿼리들을 다 쏘아 보내는거다.
      - 정말 테스트로 보려는것 말고는 쓸일이 없다는 것
      - 애초에 엔티티 노출하지 말라했기 때문에 정말로 이부분들은 참고만 할 것

<br>

**양방향 해결 & 프록시 문제(LAZY 문제) 해결 한 상태에서 LAZY 강제 초기화만 안한 상태** 

<img src=".\images\image-20230304223646092.png" alt="image-20230304223646092"  /> 

<br>

**엔티티의 실제 값을 구하게 해서 DB 접근으로 LAZY 강제 초기화 한 상태  
orderitems가 null => 이부분은 v3에서 다루며, 패치 조인 필요**

* 참고로 여기서 사용한 LAZY 강제 초기화 방법은 여러번 소개했던 방법

<img src=".\images\image-20230410140105746.png" alt="image-20230410140105746"  />

<br>

### v2/simple-orders : 엔티티를 DTO로 변환

**V2는 V1내용들 단순히 DTO로 변환 ( 물론 V1, V2 둘다 1+N 문제 있음 )  
당연히 V2는 엔티티 보호 및 원하는 내용들로 반환해주는 효과들이 있음.**

**`N+1 문제`가 발생하는것이 핵심..!! => 위에서 정리한 N+1 관련 내용 참고**

<br>

### v3/simple-orders : 엔티티를 DTO로 변환 - 페치 조인 최적화

**`N+1 문제` 를 해결하는 `페치 조인` 방법을 사용해본다.**

* jpql의 `join fecth` 문법을 이용!!

  ```java
  // 페치조인으로 order -> member , order -> delivery 는이미 조회된 상태이므로 지연로딩X
  public List<Order> findAllWithMemberDelivery() {
      return em.createQuery(
          "select o from Order o" +
          " join fetch o.member m" +
          " join fetch o.delivery d", Order.class)
          .getResultList();
  }
  ```

* 쿼리문 1번만 날라간다 (실무에서 정말 많이 사용)

  * SQL 문법인 inner join 이 사용된다.
  
  <img src=".\images\image-20230308191402946.png" alt="image-20230308191402946" style="zoom:80%;" /> 

<br>

### v4/simple-orders : JPA에서 DTO로 바로 조회

**V4. JPA에서 DTO로 바로 조회** 

* 쿼리 1번 호출

* select 절에서 원하는 데이터만 선택해서 조회!!

  ```java
  @GetMapping("/api/v4/simple-orders")
  public List<OrderSimpleQueryDto> ordersV4() {
      return orderSimpleQueryRepository.findOrderDtos();
  }
  ```

  * `orderSimpleQueryRepository.findOrderDtos()` 를 사용!!! (레퍼지토리도 새로 만든것이다)

    * 왜냐면 기존 OrderRepository에는 순수한 엔티티에만 접근하는 코드들을 놔두려고 하는 편이라 함.

    ```java
    // repository 패키지에 order 패키지 만들어서 OrderSimpleQueryRepository.java 생성 추천!
    public List<OrderSimpleQueryDto> findOrderDtos() {
        return em.createQuery(
            "select new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
            " from Order o" +
            " join o.member m" +
            " join o.delivery d", OrderSimpleQueryDto.class)
            .getResultList();
    }
    ```

    * select 문에서 객체 생성해서 가져오는것 마냥 jpql을 짤 수 있다!! (querydsl쓰면 더 편하다고 함!!)
    * 여기서 `OrderSimpleQueryDto` 도 만들어 사용해준다.
      * MVC 패턴을 유지하고 있으므로 `service->repository->db` 의 구조이다.
      * 따라서 `repository -> controller` 가 아니기 때문에 기존 controller 파일에 DTO를 static class로 만들면 MVC 패턴에 위배된다는점 참고하자!

* SELECT 절에서 원하는 데이터를 직접 선택 하므로 DB -> 애플리케이션 네트웍 용량 최적화(생각보다 
  미비)

* 리포지토리 재사용성 떨어짐, API 스펙에 맞춘 코드가 리포지토리에 들어가는 단점

<br><br>

## API 개발 고급 - 컬렉션 조회 최적화

**이번엔 `OneToMany(=XtoMany)` 를 조회를 최적화 해보자!!**

* 해당하는 엔티티들은 Order 기준으로 OrderItem, Item 이 있다.

**참고로 컬렉션 관련해서도 초반에 정리했었다!!**

<br>

### v1/orders : 엔티티 직접 노출

**`OrderApiController.java` 만들어서 작성!!**

**참고로 이전에 한 하이버네이트 모듈 사용 중이므로 지연로딩(LAZY) 값들은 null로 띄어줄거임.**  
**또한, LAZY 강제 초기화도 이전처럼 할거임.**

**하지만!! 엔티티 노출은 안좋다고 했습니다~~ DTO로 하자**

<br>

### v2/orders : 엔티티를 DTO로 변환

**DTO로 변환하는건 문제없는데 `Order->OrderItem->Item` 처럼 엔티티가 구조를 가진다면???**

* 흔히들 Order까지만 DTO를 형성해서 실수한다고함!! 

* 귀찮더라도 OrderItem까지 해서 의존성을 완전히 끊어줘야한다. ( 아래 주석 잘 확인 )

  ```java
  @Getter
  static class OrderDto {
      private Long orderId;
  	// ... 생략
      // private List<OrderItem> orderItems; 이 코드처럼 실수 하지 말라는 것!
      private List<OrderItemDto> orderItems;
      public OrderDto(Order order) {
          orderId = order.getId();
  		// ... 생략
          orderItems = order.getOrderItems().stream()
              .map(o -> new OrderItemDto(o))
              .collect(toList());
      }
  }
  ```

<br>

**즉, DTO 클래스에 멤버의 타입같이 내부 데이터들도 DTO 적용 안되어있으면 반드시 해야한다.**

**또한, 지연로딩으로 너무 많은 SQL 실행** 

* SQL 실행 수
  * `order`  1번
  * `member, address`  N번(order 조회 수 만큼) 
  * `orderItem`  N번(order 조회 수 만큼) 
  * `item`  N번(orderItem 조회 수 만큼)
* 참고: 지연로딩은 영속성 컨텍스트에 있으면 영속성 컨텍스트에 있는 엔티티를 사용하고 없으면 SQL을 
  실행한다.   
  따라서 같은 영속성 컨텍스트에서 이미 로딩한 회원 엔티티를 추가로 조회하면 SQL을 실행하지 않는다.

<br>

### v3/orders : 엔티티를 DTO로 변환 - 페치 조인 최적화

**V3. 엔티티 DTO + fecth join 사용**

* 레퍼지토리에서 `findAllWithItem()` 에서 jpql인 `join fetch` 구문 사용할 것
* 이전과 다른점은 `distinct` 를 사용해서 중복 제거한다는 것!! (자세한 내용은 컬렉션 관련해서 위에서 확인)
  * 이전에는 XToOne 관계 였기 때문에 복제 걱정은 없었음!!

<br>

### v3.1/orders : 엔티티를 DTO로 변환 - 페이징과 한계 돌파

**V3.1 엔티티 DTO + fetch join (XToOne 만 해당) + 페이징 한계 돌파( hibernate 옵션 이용 )**

* XToOne 관계만 모두 페치 조인으로 최적화 - 페이징에 전혀 문제 없어서!!

* 컬렉션 관계는 `hibernate.default_batch_fetch_size, @BatchSize`로 최적화 - 즉, fetch join을 사용하는게 아님

  * 여기선 지연로딩(LAZY) 조회를 사용하는 것!!

  * 이 옵션을 사용하면 컬렉션이나, 프록시 객체를 한꺼번에 설정한 size 만큼 IN 쿼리로 조회한다. 

    ```yaml
    spring:
      jpa:
        properties:
          hibernate:
            default_batch_fetch_size: 1000
    ```

    * size는 100~1000 을 추천하며, 주변 환경들의 성능에 따라서 선택하자!

<br>

**쿼리 호출 수가 `1 + N` -> `1 + 1` 로 최적화 된다.**

<br>

### v4/orders : JPA에서 DTO 직접 조회

**자세한것은 코드들이 길어서 프로젝트 코드를 확인!!**

**쿼리 호출 수는 `1 + N`**

<br>

### v5/orders : JPA에서 DTO 직접 조회 - 컬렉션 조회 최적화

**자세한것은 코드들이 길어서 프로젝트 코드를 확인!!**

**쿼리 호출 수는 `1 + 1`**

<br>

### v6/orders : JPA에서 DTO 직접 조회, 플랫 데이터 최적화

**자세한것은 코드들이 길어서 프로젝트 코드를 확인!!**

**쿼리 호출 수는 `1`**

<br><br>

## API 개발 고급 - 실무 필수 최적화

**중요한 개념을 소개**

* Open Session In View : 하이버네이트 (옛날 것)
* Open EntityManager In View : JPA (새로 출시된것)   
  (관례상 OSIV라 한다)

<br>

### OSIV와 성능 최적화

**service 패키지에 query패키지 만들어서 안에 `OrderQueyrService.java` 생성 작성 (쿼리 계층)**

<img src=".\images\image-20230309023958076.png" alt="image-20230309023958076" style="zoom:80%;" /> 

**`spring.jpa.open-in-view` : true 기본값**

**트랜잭션 시작처럼 최초 데이터베이스 커넥션 시작 시점 ~ API 응답이 끝날 때 까지  
영속성 컨텍스트, 데이터베이스 커넥션 유지**

* 장점 : 덕분에 View Template, ,API 컨트롤러에서 지연 로딩(LAZY) 가 가능했던 것
* 단점 : 실시간 앱 등등 에서는 커넥션 낭비로 인해 커넥션이 모자랄 수 있다.

<br>

<img src=".\images\image-20230309024031719.png" alt="image-20230309024031719" style="zoom: 80%;" /> 

**`spring.jpa.open-in-view` : false (OSIV 종료)**

트랜잭션 종료할 때 영속성 컨텍스트를 닫고, 데이터베이스 커넥션도 반환

* 장점 : 커넥션 리소스 낭비하지 않음
* 단점 : 지연로딩(LAZY) 이 문제
* 해결방안 ?? 아래 3가지 외에도 여러가지 존재
  * OSIV를 그냥 킨다
  * 페치조인을 사용한다.
  * 트랜잭션 내에 작성한다. (이부분이 실습해주신 내용)

<br>

<img src=".\images\image-20230309025312037.png" alt="image-20230309025312037"  /> 

* `open-in-view : false` 로 설정해서 OSIV를 꺼보는것
* 지연로딩에러가 바로 뜨게되므로 해결 방안 중에서 트랜잭션 내에 작성하는것으로 해결하자.
  * 이를 `커멘드와 쿼리 분리` 라고 한다. 아래와 같이 분리한다.
  * **OrderService => 강사님은 이 방식을 추천**
    * OrderService : 핵심 비지니스 로직
    * OrderQueryServiec : 화면이나 API에 맞춘 서비스 (주로 읽기 전용 트랜잭션 사용)

<br><br>

# 마무리 - 스프링 데이터 JPA, QueryDSL 소개

**마무리로 `스프링 데이터 JPA, QueryDSL` 을 간략히 소개하고 마치겠습니다.**

<br><br>

## 스프링 데이터 JPA

**기존 만든 save(), findOne() findAll() 등등 자주쓰는 함수들 만들때 구조가 비슷비슷함.**

**이것을 자동화해서 미리 만들어 제공해주는게 스프링 데이터 JAP!!!!**

- `MemberRepository` 를 인터페이스로 만들었을 뿐인데, 스프링 데이터 JPA가 알아서 구현해서 `private final MemberRepository memberRepository;` 이런식으로 바로 가져다 쓸 수 있음!!
  - 생각보다 간단한 메소드들이 실무에서 사용하게 돼서 매우 매우 유용하다고 함
- 특히 `findByName` 처럼일반화하기 어려운기능도 메서드 이름으로정확한 JPQL 쿼리를실행한다.
  - `select m from Member m where m.name = :name`

<br><br>

## QueryDSL

**세팅은 귀찮았지만 사용할땐 직관적이고 좋음**

- 자바코드라서 sql문 부분 쿼리 잘못적으면 컴파일 오류(빨간글자)를 알려줌!\

**Querydsl은 JPA로애플리케이션을개발할때 선택이 아닌필수라 생각한다고 한다.**

<br><br>

# 참고 자료

**각각 사이트에서 그림을 가져왔다. (그림들이 알기 쉽게 잘 되어 있음!)**

* **[일반적인 Spring MVC 처리과정](https://dncjf64.tistory.com/288)**
* **[Controller, RestController 비교](https://mangkyu.tistory.com/49)**

