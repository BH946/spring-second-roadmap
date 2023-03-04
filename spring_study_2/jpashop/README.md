## Intro..

**실전! 스프링 부트와 JPA 활용2 - API 개발과 성능 최적화**

* 인프런 강의 듣고 공부한 내용입니다.
* 단리ㅏㅓㄴ단츅키는 어디에 기록주ㅠㅇㅇ인가나난ㄴㄴㄴ alt+insert대박
* 알아뱌뱌어ㅏㅣㄹ우자ㅓㅇ루ㅏㅣㄴㅇㅇ

<br>

해당 프로젝트 폴더는 강의를 수강 후 강의에서 진행한 프로젝트를 직접 따라 작성했습니다.

따로 강의 자료(pdf)를 주시기 때문에 필요할때 해당 자료를 이용할 것이고,

이곳 README.md 파일에는 기억할 내용들만 간략히 정리하겠습니다.

* 자세한 코드가 궁금하다면, 올려둔 프로젝트에서 코드확인
* 정리하다 보니 좀 많이 정리하는 감이..

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

엔티티 노출 방법 V1~..

JPA에서 DTO로 바로 조회 V4~...



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

