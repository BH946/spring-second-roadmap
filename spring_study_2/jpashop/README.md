# Intro..

**실전! 스프링 부트와 JPA 활용2 - API 개발과 성능 최적화**

* 인프런 강의 듣고 공부한 내용입니다.
* **유용한 단축키**
  * `Alt + Insert` : getter, setter, constructor 등 자동 생성
  * `Ctrl + Alt + V` : 변수 선언부를 자동 작성
  * `Ctrl + Alt + M` : 코드 리팩토링하기 쉽게끔 함수 자동 생성
  * `Ctrl + Shift + Down/Up` : 메소드 코드 통째로 위, 아래 자리 이동 가능
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

이거랑 아래 전체 흐름 정리하면 된다..







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

  <img src=".\images\image-20230308021524143.png" alt="image-20230308021524143" style="zoom:67%;" /> ==> =>                            <img src="C:\Users\KoBongHun\Desktop\Git\Study\Spring_Study\images\README\image-20230308021648305-16782094304791.png" alt="image-20230308021648305" style="zoom:67%;" /> 

  * 왼쪽 그림처럼 배열엔 `"count" : 4` 같은 데이터를 바로 못 집어넣는 형태이다.

  * **따라서 오른쪽 그림처럼 최상위는 객체로 감싸주고, 안에 "data"같은 키의 값부분에 왼쪽 그림의 배열 데이터들을 넣는게 일반적인 JSON 응답 구조이다.**

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

**조회에서 성능을 최적화 하는 좋은 방법들로만 소개하겠다.**

**현업에서 API 문제같은 경우 대부분 여기서 소개하는 부분들로 다 해결이 되었다고 한다.**

<br>

### v1/simple-orders : 엔티티 직접 노출

https://www.inflearn.com/notes/37668#s-24317

https://github.com/BH946/spring_second_roadmap/tree/main/spring_study_1/jpashop

활용편2 pdf

인텔리J 코드 - OrderSimpleApiController.java

<br>

### v2/simple-orders : 엔티티를 DTO로 변환



<br>

### v3/simple-orders : 엔티티를 DTO로 변환 - 페치 조인 최적화



<br>

### v4/simple-orders : JPA에서 DTO로 바로 조회



<br><br>

## API 개발 고급 - 컬렉션 조회 최적화

<br>

### v1/orders : 엔티티 직접 노출



<br>

### v2/orders : 엔티티를 DTO로 변환



<br>

### v3/orders : 엔티티를 DTO로 변환 - 페치 조인 최적화



<br>

### v3.1/orders : 엔티티를 DTO로 변환 - 페이징과 한계 돌파



<br>

### v4/orders : JPA에서 DTO 직접 조회



<br>

### v5/orders : JPA에서 DTO 직접 조회 - 컬렉션 조회 최적화



<br>

### v6/orders : JPA에서 DTO 직접 조회, 플랫 데이터 최적화





<br><br>

## API 개발 고급 - 실무 필수 최적화

<br>

### OSIV와 성능 최적화



<br><br>



양방향 해결 & 프록시 문제(LAZY 문제) 해결 한 상태에서 LAZY 강제 초기화만 안한 상태.  
첨고로 양방향 관계 문제 발생 -> @JsonIgnore => Order과 연관된 Delivery, OrderItem, Member 에 적용했음

<img src=".\images\image-20230304223646092.png" alt="image-20230304223646092"  /> 



엔티티의 실제값 구하게 해서 DB 접근으로 LAZY 강제 초기화 한 상태  
orderitems는 XXXXXXXX => 이부분은 v3에서 다루며, 패치 조인 필요

<img src="C:\Users\KoBongHun\Desktop\Git\Study\Spring_Study\images\README\image-20230304224539759.png" alt="image-20230304224539759"  /> 



V2는 V1내용들 단순히 DTO로 변환 ( 물론 V1, V2 둘다 1+N 문제 있음 )  
당연히 엔티티보호 및 원하는 내용들로 반환해쥬는 효과



V4 는 JPA방식으로 다른조회방식인데, 파일들도 따로 부뉴했댱 레퍼지토리를 새로 만들었윰.  
물론 api 호출은 컨트롤러에 그대로 v4 버전으로 만ㄷ



뒤부터... 앞의예제에서는 toOne(OneToOne, ManyToOne) 관계만 있었다. 이번에는컬렉션인일대다관계 
(OneToMany)를조회하고, 최적화하는 방법을 알아보자.

엔티티 노출 방법 V1~..~!~!~!~!!~!~!~!

JPA에서 DTO로 바로 조회 V4~...~!~!~!~!~!



최적화 권장

니마ㅓ리ㅏ너로ㅑ더파ㅣ;ㅏㅓㅜㅠㅗㅓㅏㄷ래패아 ㅟ



OSLI>?? 이후 정리해야함.ㅎ늘.,느.



```
List<OrderItem> orderItems = order.getOrderItems();
orderItems.stream().forEach(o -> o.getItem().getName()); // Lazy 강제 초기화
```

처럼 하는거랑, 또 다른 방식 stream이거 하는거 둘다 정리.

```
.collect(Collectors.toList());

.getResultList(); => 쿼리문에 보면 있윰
```







<br><br>

# 마무리 - 스프링 데이터 JPA, QueryDSL 소개

**마무리로 `스프링 데이터 JPA, QueryDSL` 을 간략히 소개하고 마치겠습니다.**

<br><br>

## 스프링 데이터 JPA





<br><br>

## QueryDSL



<br><br>

# 참고 자료

**각각 사이트에서 그림을 가져왔다. (그림들이 알기 쉽게 잘 되어 있음!)**

* **[일반적인 Spring MVC 처리과정](https://dncjf64.tistory.com/288)**
* **[Controller, RestController 비교](https://mangkyu.tistory.com/49)**

