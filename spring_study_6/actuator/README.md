# Intro..

**스프링 부트 - 핵심 원리와 활용**

* 인프런 강의듣고 공부한 내용입니다.

<br>

해당 프로젝트 폴더는 강의를 수강 후 강의에서 진행한 프로젝트를 직접 따라 작성했습니다.

따로 강의 자료(pdf)를 주시기 때문에 필요할때 해당 자료를 이용할 것이고,

이곳 README.md 파일에는 기억할 내용들만 간략히 정리하겠습니다.

* **프로젝트**
  * **액츄에이터 - actuator**

<br><br>

#  프로젝트 환경설정 & 생성

**준비물**

* **Java 17 이상(스프링 3.0의 최소요구)**
* IDE: IntelliJ (이클립스도 가능합니다)

<br>

**프로젝트 생성**

* **Spring Boot: 3.x.x**
* **Packaging: Jar**
* **Java: 17 이상**
* **build.gradle**


```groovy
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.0.2'
    id 'io.spring.dependency-management' version '1.1.0'
}
group = 'hello'
version = '0.0.1-SNAPSHOT' 
sourceCompatibility = '17'
configurations { 
    compileOnly {
        extendsFrom annotationProcessor
    } 
}
repositories {
    mavenCentral() 
}
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa' 
    implementation 'org.springframework.boot:spring-boot-starter-web'
    // actuator 추가
    implementation 'org.springframework.boot:spring-boot-starter-actuator' 
    compileOnly 'org.projectlombok:lombok' 
    runtimeOnly 'com.h2database:h2'
    annotationProcessor 'org.projectlombok:lombok'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    //test lombok 사용
    testCompileOnly 'org.projectlombok:lombok'
    testAnnotationProcessor 'org.projectlombok:lombok' 
}
tasks.named('test') {
    useJUnitPlatform() 
}
```

<br>

**추가 설정**

* IntelliJ Gradle 대신에 자바직접실행
* 최근 IntelliJ 버전은 Gradle을통해서실행 하는것이기본설정이다. 이렇게 하면실행속도가느리다. 
* 다음과같이 변경하면 자바로바로실행해서 실행속도가더빠르다.
* Preferences -> Build, Execution, Deployment -> Build Tools -> Gradle 
  * Build and run using: Gradle -> IntelliJ IDEA
  * Run tests using: Gradle -> IntelliJ IDEA
  * 참고로 File -> Setting에서 검색해서 바로 찾아도 됨
* 그리고 설치한 `jdk11` 로 프로젝트, gradle 설정 해줘야 한다.
  * 위에서 접근한 Build Tools -> Gradle 에서 jdk11로 설정(java11)
  * File -> Setting에서 바로 Project Setting -> Project 검색해서 이곳도 jdk11로 설정(java11)
* **주의!!**
  * 단, Intellij IDEA 로 바꾸고 실행이 안되는 경우가 있는데 그런 경우에는 다시 Gradle로 돌려둘 것

<br>

**단축키 확인 법**

* File -> Settings -> keymap 에서 검색해서 확인

<br><br>

# 1. actuator(액츄에이터)

## 1-1. 엔드포인트

**라이브러리 추가하고 `http://localhost:8080/actuator` 접근해보면 바로 "앱 상태들을 제공"**

* `health` : 헬스 정보, `beans` : 스프링 컨테이너 등록된 빈, 등등...
  *  전체 엔드포인트는 다음공식 메뉴얼 참고 : [엔드포인트](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.endpoints)
* 엔드포인트 설정 - 활성화, 노출
  * 기본적으로 `shutdown` 을 제외하고는 거의다 **활성화** 상태
  * 따라서 "노출"까지 해야 `shutdown` 사용 가능

<br>

**application.yml**

* Post 로 shutdown 전송시 실제로 서버 다운
* exclude 로 제외도 당연히 가능

```yaml
management:
	endpoint:
		shutdown:
			enabled: true
	endpoints:
		web:
			exposure:
				include: "*"
				exclude: "env,beans"
```

<br>

**자주 사용하는 엔드포인트**

* beans : 스프링 컨테이너에 등록된 스프링빈을 보여준다.
* conditions : condition 을 통해서 빈을 등록할때 평가조건과 일치하거나 일치하지 않는 이유를  표시한다.
* configprops : @ConfigurationProperties 를 보여준다. 
* env : Environment 정보를보여준다.
* health : 애플리케이션 헬스 정보를 보여준다.
* httpexchanges : HTTP 호출 응답 정보를 보여준다. HttpExchangeRepository 를 구현한 빈을 별도로 등록해야 한다.
* info : 애플리케이션 정보를 보여준다.
* loggers : 애플리케이션 로거 설정을 보여주고 변경도 할 수 있다. 
* metrics : 애플리케이션의 메트릭 정보를 보여준다.
* mappings : @RequestMapping 정보를 보여준다. 
* threaddump : 쓰레드 덤프를 실행해서 보여준다.
* shutdown : 애플리케이션을 종료한다. 이 기능은 기본으로 **비활성화** 되어 있다.

<br><br>

## 1-2. 자세히 알아보자(엔드포인트)

**`health , info , loggers , httpexchanges, 보안, metric` 를 더 자세히 알아보자**

<br>

### health

**헬스 정보를 사용하면 애플리케이션에 문제가 발생했을때 문제를 빠르게 인지**

* `status: UP` 이면 정상

```properties
# 이 설정으로 자세한 헬스 정보 확인가능
management:
	endpoint:
		health:
			show-details: always
```

![image](https://github.com/BH946/spring-second-roadmap/assets/80165014/90a2252c-0b92-4b26-8caa-fe6b28b6e03e) 

<br>

### info

**애플리케이션의 기본 정보를 노출**

* `java, os, env, build, git` 정보를 볼 수 있고 `env, java, os` 는 기본이 "비활성화"
  * java, os 는 자바버전, os 환경 등의 정보
  * env 는 "외부설정" 파일에 info...로 등록한 설정내용들을 접근가능
  * build 는 build.gradle에 `springBoot { buildInfo() }` 추가 후 정보 확인 가능
  * git 은 build.gradle에 `id "com.gorylenko.gradle-git-properties" version "2.4.1"` 추가
    * 단, git 이 등록되어 있어야하며 
    * 애플리케이션을 배포할때 가끔 기대와 전혀 다르게 동작할때가 있는데,(특정기능이빠져있다던가) 확인해보면 다른 커밋이나 다른 브랜치의 내용이 배포된 경우가 종종 있다. 
    * 이럴 때큰 도움

<br>

### loggers

**실시간으로 서버를 껏다켜지 않고 "로그 레벨" 을 변경할 수 있는 장점**

**LogController.java - 단순히 로그 남기는 컨트롤러 예시**

```java
@GetMapping("/log") 
public String log() {
    log.trace("trace log");
    log.debug("debug log");
    log.info("info log");
    log.warn("warn log");
    log.error("error log");
    return "ok"; 
}
```

<br>

**aaplication.yml - 위 컨트롤러 레벨을 debug로 설정**

* `http://localhost:8080/actuator/loggers` 접근시 DEBUG 로 잘 바껴있음
* **이때 POST+JSON 형태로 `"configuredLevel": "TRACE"` 전송시 TRACE 레벨로 바뀜!!**
  * 물론 서버를 재부팅하면 다시 DEBUG가 된다.

```yaml
logging:
	level:
		hello.controller: debug
```

<br>

### httpexchanges

**HTTP 요청과 응답의 과거 기록을 확인하고 싶다면 httpexchanges 엔드포인트를 사용**

**InMemoryHttpExchangeRepository 추가**

```java
@SpringBootApplication
public class ActuatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(ActuatorApplication.class, args);
    }
    // 추가
    @Bean
    public InMemoryHttpExchangeRepository httpExchangeRepository() {
        return new InMemoryHttpExchangeRepository(); 
    }
}
```

* 참고로 이 기능은 매우 단순하고 기능에 제한이 많기 때문에 개발단계에서만 사용하고, 실제운영 서비스에서는 모니터링 툴이나 **핀포인트**, Zipkin 같은 다른기술을 사용하는것이 좋다.

<br>

### 보안

**내부망에서만 액츄에이터에 접근할 수 있게하는게 안전**

* 8080 포트를 외부에서 들어올 수 있게 서버에서 세팅 해놨다면, 내부에서만(예로 해당 서버 IP만허용) 사용할 수 있는 9292 포트로 바꾸면 훨씬 보안상 안전
* 9292 포트를 내부망이라 가정하고 바꿔보자
* `management.server.port=9292`
  * `http://localhost:9292/actuator` 정상동작
  * `http://localhost:8080/actuator` 실패

<br>

**metric 은 아래에서부터 알아보자**

<br><br>

# 2. 마이크로미터, 프로메테우스, 그라파나

## 2-1. 마이크로미터

**수많은 모니터링이 존재하는데, 모니터링 툴을 바꾸려고 할때 엄청나게 많은 코드를 또 수정해야하는 수고를 가질 수도있다.**

**이를 해결해주는게 "마이크로미터"**

**마이크로미터 전체 그림**

![image](https://github.com/BH946/spring-second-roadmap/assets/80165014/424d1842-38ea-4402-bb23-397b9bac2592) 

<br><br>

## 2-2. 메트릭

**기본 메트릭 확인 : `http://localhost:8080/actuator/metrics`**

* 자바 메모리 사용량 : `http://localhost:8080/actuator/metrics/jvm.memory.used`
* HTTP 요청수 : `http://localhost:8080/actuator/metrics/http.server.requests`
* 등등 매우 다양하게 제공 중

<br>

**액츄에이터를 통해서 수많은 메트릭이 자동으로 만들어지는 것을 확인**

**그런데 이러한 메트릭들을 어딘가에 지속해서 보관해야 과거의 데이터들도 확인할 수 있을것**

**따라서 메트릭을 지속적으로 수집하고 보관할 데이터베이스가 필요**

**그리고 이러한 메트릭들을 그래프를 통해서 한눈에 쉽게 확인할 수 있는 대시보드도 필요**

<br><br>

## 2-3. 프로메테우스

**설치생략**

**프로메테우스는 메트릭을 수집 및 DB에 저장**

**전체 구조**

![image](https://github.com/BH946/spring-second-roadmap/assets/80165014/67aab22e-f622-47ea-b63d-474e7a112c5c) 

<br>

### 앱과 연동하는 법

**프로메테우스는 포멧이 달라서 /actuator/metrics 내용을 바로 이해 못해서 "마이크로미터" 활용**

* **build.gradle 에 `implementation 'io.micrometer:micrometer-registry-prometheus'` 추가**

  * **micrometer Prometheus 구현체 확인 : http://localhost:8080/actuator/prometheus** 

* **프로메테우스 폴더의 prometheus.yml 파일 수정 -> 수집 설정**

  * job_name : 수집하는 이름 - 임의의 이름을 사용
  * metrics_path : 수집할 경로
  * scrape_interval : 수집할 주기
    * 10s~1m 을 권장
  * targets : 수집할 서버의 IP, PORT

  ```properties
  #추가
  - job_name: "spring-actuator" 
  	metrics_path: '/actuator/prometheus' 
  	scrape_interval: 1s
  	static_configs:
  		- targets: ['localhost:8080']
  ```

* **연동 확인**

  * http://localhost:9090/config
  * http://localhost:9090/targets

<br>

### 기본 기능들

**PDF에서 확인**

<br>

### 메트릭 - 게이지, 카운터

* **게이지(Gauge)**
  * 임의로 오르내릴 수 있는값
  * 예) CPU 사용량, 메모리사용량, 사용중인커넥션
* **카운터(Counter)**
  * 단순하게 증가하는 단일누적 값 
  * 예) HTTP 요청수, 로그발생수
  * **`increase(...)` 를 사용시 "시간 단위 요청 그래프" 확인 가능**
    * "일반 그래프"로는 확인이 너무 어렵기 때문에 이런 함수를 함께 사용

<br>

**프로메테우스는 대시보드를 한눈에 보기 너무 어렵다. 이를 "그라파나"를 사용해서 해결하자**

<br><br>

## 2-4. 그라파나 

**설치생략**

**그라파나는 프로메테우스가 DB라고 하면, 이 DB 데이터를 불러서 쉽게 보는 대시보드 역할**

**PDF 를 통해서 다 따라해볼것**

<br>

### 공유 대시보드 활용

**그라파나 대시보드 세팅이 귀찮은 작업인데 이미 오픈소스로 사람들이 만든 대시보드를 그대로 가져다 사용할 수 있다.**

* 추천 : ID: 11378
* 추천 : ID: 4701

<br><br>

## 2-5. 메트릭을 통한 문제 확인

**실무에서 주로 많이 발생하는 다음 4가지 대표적인 예시를 확인**

* CPU 사용량 초과 
* JVM 메모리 사용량 초과 
* 커넥션 풀 고갈 
* 에러 로그 급증

<br>

### 1) CPU 사용량 초과

**CPU에 부하주는 코드 작성 후 사용 가정**

![image](https://github.com/BH946/spring-second-roadmap/assets/80165014/97be3c5a-1b76-4247-83cd-2ea9216eff9f) 

<br>

### 2) JVM 메모리 사용량 초과

**메모리 사용을 누적하는 코드 사용**

* `java.lang.OutOfMemoryError: Java heap space` 에러 발생

![image](https://github.com/BH946/spring-second-roadmap/assets/80165014/cd1dd70c-994c-4274-86b4-393caf9e643a) 

<br>

### 3) 커넥션 풀 고갈

**커넥션을 닫지 않는 코드 사용**

* DB 커넥션을 획득하기 위해 대기하던 톰캣 쓰레드가 30초이상 DB 커넥션을 획득하지 못하면 다음과 같은 예외가 발생하면서 커넥션 획득을 포기
* `Connection is not available, request timed out after 30004ms.`

![image](https://github.com/BH946/spring-second-roadmap/assets/80165014/548dc7d1-6e98-4bee-8306-ed8fe9254783) 

<br>

### 4) 에러 로그 급증

**에러로그 남기는 코드 사용**

![image](https://github.com/BH946/spring-second-roadmap/assets/80165014/3462f53e-f318-476d-b0ad-3f1826288d6e) 

<br><br>

# 3. 모니터링 메트릭 활용 - 비지니스 추가

**테스트 비지니스로 컨트롤러 GET 형태로 order, cancel, stock 함수를 만들었다고 가정**

**이후 마이크로미터를 사용해서 메트릭을 직접 등록하겠다.**

* **마이크로미터 핵심 기능** : Counter, Gauge, Timer, Tags

* **MeterRegistry** : 마이크로미터 기능을 제공하는 핵심 컴포넌트
  * 스프링을 통해서 주입 받아서 사용

* **참고** : 카운터와 게이지를 구분하는 것은 값이 감소하는가를 기준으로 나누면 된다.

<br>

**OrderController - 비지니스 로직 테스트 컨트롤러**

```java
@Slf4j
@RestController
public class OrderController {
    private final OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService; 
    }

    @GetMapping("/order") 
    public String order() {
        log.info("order"); 
        orderService.order();
        return "order"; 
    }
    @GetMapping("/cancel") 
    public String cancel() {
        log.info("cancel"); 
        orderService.cancel();
        return "cancel"; 
    }
    @GetMapping("/stock") 
    public int stock() {
        log.info("stock");
        return orderService.getStock().get(); 
    }
}
```

<br>

**OrderSerivce - 인터페이스**

```java
public interface OrderService {
    void order(); 
    void cancel();
    AtomicInteger getStock(); 
}
```

<br><br>

## 3-1. @Counted - V1, V2

**OrderServiceV1**

```java
@Slf4j
public class OrderServiceV1 implements OrderService {
    private final MeterRegistry registry;
    private AtomicInteger stock = new AtomicInteger(100); 
    public OrderServiceV1(MeterRegistry registry) {
        this.registry = registry; 
    }
    
    @Override
    public void order() {
        log.info("주문");
        stock.decrementAndGet();
        Counter.builder("my.order")
            .tag("class", this.getClass().getName()) 
            .tag("method", "order")
            .description("order")
            .register(registry).increment();
    }
    @Override
    public void cancel() {
        log.info("취소");
        stock.incrementAndGet();
        Counter.builder("my.order")
            .tag("class", this.getClass().getName()) 
            .tag("method", "cancel")
            .description("order")
            .register(registry).increment();
    }
    @Override
    public AtomicInteger getStock() {
        return stock; 
    }
}
```

**OrderConfigV1**

```java
@Configuration
public class OrderConfigV1 {
    @Bean
    OrderService orderService(MeterRegistry registry) {
        return new OrderServiceV1(registry); 
    }
}
```

**ActuatorApplication - 수정**

* 스프링이 빈에 자동 등록하는 MeterRegistry 을 사용

```java
//@Import(OrderConfigV0.class) 
@Import(OrderConfigV1.class)
@SpringBootApplication(scanBasePackages = "hello.controller") 
public class ActuatorApplication {}
```

<br>

**OrderServiceV2 - @Counted**

```java
@Slf4j
public class OrderServiceV2 implements OrderService { 
    private AtomicInteger stock = new AtomicInteger(100); 
    
    @Counted("my.order")
    @Override
    public void order() { 
        log.info("주문"); 
        stock.decrementAndGet();
    }
    @Counted("my.order") 
    @Override
    public void cancel() { 
        log.info("취소"); 
        stock.incrementAndGet();
    }
    @Override
    public AtomicInteger getStock() {
        return stock; 
    }
}
```

**OrderConfigV2**

* **CountedAspect** 를 빈으로 반드시 등록해야 @Counted 관련 AOP 동작

```java
@Configuration
public class OrderConfigV2 {
    @Bean
    public OrderService orderService() {
        return new OrderServiceV2(); 
    }
    @Bean
    public CountedAspect countedAspect(MeterRegistry registry) {
        return new CountedAspect(registry); 
    }
}
```

**ActuatorApplication - 변경**

```java
//@Import(OrderConfigV1.class) 
@Import(OrderConfigV2.class)
@SpringBootApplication(scanBasePackages = "hello.controller") 
public class ActuatorApplication {}
```

<br>

**그라파나 대시보드에 등록!!**

![image](https://github.com/BH946/spring-second-roadmap/assets/80165014/dee548c6-bf15-465f-8f77-07786e8c3e48) 

<br><br>

## 3-2. @Timed - V3, V4

**OrderServiceV3**

```java
@Slf4j
public class OrderServiceV3 implements OrderService {
    private final MeterRegistry registry;
    private AtomicInteger stock = new AtomicInteger(100); 
    public OrderServiceV3(MeterRegistry registry) {
        this.registry = registry; 
    }
    
    @Override
    public void order() {
        Timer timer = Timer.builder("my.order")
            .tag("class", this.getClass().getName()) 
            .tag("method", "order")
            .description("order") 
            .register(registry);
        timer.record(() -> { 
            log.info("주문"); 
            stock.decrementAndGet();
            sleep(500); 
        });
    }
    @Override
    public void cancel() {
        Timer timer = Timer.builder("my.order")
            .tag("class", this.getClass().getName())
            .tag("method", "cancel") 
            .description("order") 
            .register(registry);
        timer.record(() -> { 
            log.info("취소"); 
            stock.incrementAndGet();
            sleep(200); 
        });
    }
    private static void sleep(int l) {
        try {
            Thread.sleep(l + new Random().nextInt(200));
        } catch (InterruptedException e) {
            throw new RuntimeException(e); 
        }
    }
    @Override
    public AtomicInteger getStock() {
        return stock; 
    }
}
```

**OrderConfigV3**

```java
@Configuration
public class OrderConfigV3 {
    @Bean
    OrderService orderService(MeterRegistry registry) {
        return new OrderServiceV3(registry); 
    }
}
```

**ActuatorApplication - 변경**

```java
//@Import(OrderConfigV1.class) 
//@Import(OrderConfigV2.class) 
@Import(OrderConfigV3.class)
@SpringBootApplication(scanBasePackages = "hello.controller") 
public class ActuatorApplication {}
```

<br>

**OrderServiceV4 - @Timed**

```java
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger; 
@Timed("my.order")
@Slf4j
public class OrderServiceV4 implements OrderService { 
    private AtomicInteger stock = new AtomicInteger(100); 
    
    @Override
    public void order() { 
        log.info("주문"); 
        stock.decrementAndGet();
        sleep(500); 
    }
    @Override
    public void cancel() { 
        log.info("취소"); 
        stock.incrementAndGet();
        sleep(200); 
    }
    private static void sleep(int l) {
        try {
            Thread.sleep(l + new Random().nextInt(200));
        } catch (InterruptedException e) {
            throw new RuntimeException(e); 
        }
    }
    @Override
    public AtomicInteger getStock() {
        return stock; 
    }
}
```

**OrderConfigV4**

* **TimedAspect** 를 사용해야 @Timed 에 AOP 가 적용

```java
@Configuration
public class OrderConfigV4 {
    @Bean
    OrderService orderService() {
        return new OrderServiceV4(); 
    }
    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry); 
    }
}
```

**ActuatorApplication - 변경**

```java
//@Import(OrderConfigV1.class) 
//@Import(OrderConfigV2.class) 
//@Import(OrderConfigV3.class) 
@Import(OrderConfigV4.class)
@SpringBootApplication(scanBasePackages = "hello.controller") 
public class ActuatorApplication {}
```

<br>

**그라파나 대시보드 등록!!**

![image](https://github.com/BH946/spring-second-roadmap/assets/80165014/ce8ed7f6-1988-491b-bf8b-aa4991d0ce09) 

<br><br>

## 3-3. 게이지 - V1, V2

**StockConfigV1**

* 앞에서 빈에 OrderService 는 등록중이고, MeterRegistry 는 스프링이 자동으로 빈에 등록하므로 아래처럼 바로 파라미터로 사용 가능
* my.stock 이라는이름으로 게이지를등록

```java
@Configuration
public class StockConfigV1 {
    @Bean
    public MyStockMetric myStockMetric(OrderService orderService, MeterRegistry 
                                       registry) {
        return new MyStockMetric(orderService, registry); 
    }

    @Slf4j
    static class MyStockMetric {
        private OrderService orderService; 
        private MeterRegistry registry;
        public MyStockMetric(OrderService orderService, MeterRegistry registry) 		{
            this.orderService = orderService; 
            this.registry = registry;
        }
        
        @PostConstruct 
        public void init() {
            Gauge.builder("my.stock", orderService, service -> { 
                log.info("stock gauge call");
                return service.getStock().get(); 
            }).register(registry);
        } 
    }
}
```

**ActuatorApplication - 변경**

```java
package hello;
//@Import(OrderConfigV4.class)
@Import({OrderConfigV4.class, StockConfigV1.class})
@SpringBootApplication(scanBasePackages = "hello.controller") 
public class ActuatorApplication {}
```

<br>

**StockConfigV2 - MeterBinder(regitstry 관련 타입)**

```java
@Slf4j
@Configuration
public class StockConfigV2 {
    @Bean
    public MeterBinder stockSize(OrderService orderService) {
        return registry -> Gauge.builder("my.stock", orderService, service -> {
            log.info("stock gauge call");
            return service.getStock().get(); 
        }).register(registry);
    } 
}
```

**ActuatorApplication - 변경**

```java
//@Import({OrderConfigV4.class, StockConfigV1.class}) 
@Import({OrderConfigV4.class, StockConfigV2.class})
@SpringBootApplication(scanBasePackages = "hello.controller") 
public class ActuatorApplication {}
```

<br>

**그라파나 대시보드에 등록!!**

![image](https://github.com/BH946/spring-second-roadmap/assets/80165014/3c7f1e2d-59b3-4de4-a8e9-10402daf54b1) 

<br><br>

## 3-4. 실무 모니터링 환경 구성 팁

**모니터링 3단계** 

* 대시보드
* 애플리케이션 추적 - 핀포인트 
* 로그

<br>

**대시보드**

* 전체를 한눈에 볼 수 있는 가장 높은 뷰 

* 제품 : 마이크로미터, 프로메테우스, 그라파나 등등 
* 모니터링 대상
  * 시스템 메트릭(CPU, 메모리)
  * 애플리케이션 메트릭(톰캣 쓰레드 풀, DB 커넥션 풀, 애플리케이션 호출수) 
  * 비즈니스 메트릭(주문수, 취소수)

<br>

**애플리케이션 추적**

* 주로 각각의 HTTP 요청을 추적, 일부는 마이크로서비스 환경에서 분산 추적
* 제품 : **핀포인트(오픈소스)**, 스카우트(오픈소스), 와탭(상용), 제니퍼(상용)

<br>

**로그**

* 가장 자세한 추적, 원하는데로 커스텀 가능
* 같은 HTTP 요청을 묶어서 확인할 수 있는 방법이 중요, MDC 적용 
* 파일로직접 로그를 남기는경우
  * 일반로그와 에러 로그는 파일을 구분해서 남기자 
  * 에러로그만 확인해서 문제를 바로 정리할 수있음
* 클라우드에 로그를 저장하는 경우 
  * 검색이 잘 되도록 구분

<br>

**알람**

* 모니터링 툴에서 일정이상 수치가 넘어가면, 슬랙, 문자등을 연동 
* 알람은 2가지 종류로 꼭 구분해서 관리 : 경고, 심각
* 경고는 하루 1번정도 사람이 직접 확인해도 되는 수준(사람이 들어가서확인) 
* 심각은 즉시 확인해야 함, 슬랙알림(앱을 통해알림을 받도록), 문자, 전화
* 예시
  * 디스크사용량 70% 경고 
  * 디스크사용량 80% 심각 
  * CPU 사용량 40% 경고 
  * CPU 사용량 50% 심각

<br><br>

# Folder Structure

생략..
