# Intro..

**스프링 부트 - 핵심 원리와 활용**

* 인프런 강의듣고 공부한 내용입니다.

<br>

해당 프로젝트 폴더는 강의를 수강 후 강의에서 진행한 프로젝트를 직접 따라 작성했습니다.

따로 강의 자료(pdf)를 주시기 때문에 필요할때 해당 자료를 이용할 것이고,

이곳 README.md 파일에는 기억할 내용들만 간략히 정리하겠습니다.

* **프로젝트**
  * 외부 설정1 - external
  * (중요) 외부 설정2 - external-read

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
  * **web 애플리케이션 아니라는점!**


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
    implementation 'org.springframework.boot:spring-boot-starter' 
    compileOnly 'org.projectlombok:lombok'
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

# 1. 외부설정과 프로필1

**옛날 방식**

![image](https://github.com/BH946/spring-second-roadmap/assets/80165014/b1a8da9c-980c-4cfe-bef5-55d47b4fb3e6) 

<br>

**요즘 방식(외부 설정)**

![image](https://github.com/BH946/spring-second-roadmap/assets/80165014/420add7f-6286-4996-a770-d8429afcd5e8) 

<br>

**외부 설정 4가지 방법**

* OS 환경변수: "%JAVA_HOME" 같은 환경 변수 설정들 - **전체적으로 사용**
* 자바 시스템 속성: "Add VM Option", "java -옵션 명령어" 같은 것들 - **자바만 사용**
* 자바 커맨드 라인 인수: 실행시 main(args) 메서드에 파라미터 넘기듯
* 외부 파일(설정 데이터): 외부의 파일을 직접 읽어서 사용

![image](https://github.com/BH946/spring-second-roadmap/assets/80165014/38059d66-b9d5-4940-8ca5-0558bddf44f4) 

<br><br>

## 1-1. 외부 설정 - OS 환경변수

**조회방법**

* 윈도우 OS: set
* MAC, 리눅스 OS: printenv

<br>

**OsEnv.java - src/test 하위**

```java
@Slf4j
public class OsEnv {
    public static void main(String[] args) {
        Map<String, String> envMap = System.getenv(); // OS 환경변수
        for (String key : envMap.keySet()) {
            log.info("env {}={}", key, System.getenv(key));
        } 
    }
}
```

<br><br>

## 1-2. 외부 설정 - 자바 시스템 속성

**"Add VM Option", "java -옵션 명령어" 로 설정 적용**

**JavaSystemProperties.java  - src/test 하위**

```java
@Slf4j
public class JavaSystemProperties {
    public static void main(String[] args) {
        Properties properties = System.getProperties(); // 자바 시스템 속성
        for (Object key : properties.keySet()) {
            log.info("prop {}={}", key, 
                     System.getProperty(String.valueOf(key)));
        } 

        // url, ursername, password 받아와 보는 테스트
        String url = System.getProperty("url");
        String username = System.getProperty("username");
        String password = System.getProperty("password");
        log.info("url={}", url);
        log.info("username={}", username);
        log.info("password={}", password); 
    }
}
}
```

<br>

**방법 3가지**

* IDE에서 실행시 VM 옵션추가
* Jar 실행 : `java -Durl=devdb -Dusername=dev_user -Dpassword=dev_pw -jar app.jar`
* (비권장) 자바 코드 작성
  * 설정: `System.setProperty(propertyName, "propertyValue") `
  * 조회: `System.getProperty(propertyName)`

<br><br>

## 1-3. 외부 설정 - 자바 커맨드 라인 인수

**커맨드 라인 인수(Command line arguments)는 애플리케이션 실행 시점에 외부설정 값을 main(args) 메서드의 args 파라미터로 전달하는 방법**

* IDE에서 실행시 커맨드 라인 인수 추가 - Program arguments
* 명령어 : `java -jar app.jar dataA dataB`
* args 를 for문으로 읽어보면 dataA, dataB 가 정상 출력

<br>

**"스프링" 이 제공하는  `ApplicationArguments`**

* **스프링에서 제공하는 key=value 형태로 넘기는 TIP - 자바 표준 형식이 아님**

* `--` 를 `key=value` 로 인식해줘서 `--url=devdb --urername=dev_user ...` 이런식으로 사용
* `--username=userA --username=userB` 처럼 하나의 키에 여러값을 포함가능
  * 반환이 List로 된다는점!

<br>

**"스프링 부트"는  `ApplicationArguments` 를 "스프링 빈"으로 자동 등록**

* 바로 아래처럼 사용 가능
* 단, 값 가져오는 함수들이 제각각이라 이를 "추상화" 해서 쉽게 사용해보자 -> **스프링 통합**

```java
@Slf4j
@Component
public class CommandLineBean {
    private final ApplicationArguments arguments; 
    
    public CommandLineBean(ApplicationArguments arguments) {
        this.arguments = arguments; // 생성자 주입
    }
    
    @PostConstruct 
    public void init() {
        log.info("source {}", List.of(arguments.getSourceArgs())); 
        log.info("optionNames {}", arguments.getOptionNames()); 
        Set<String> optionNames = arguments.getOptionNames();
        for (String optionName : optionNames) {
            log.info("option args {}={}", optionName, 
                     arguments.getOptionValues(optionName));
        } 
    }
}
```

<br>

**스프링 통합**

* 외부 설정값이 어디에 위치하든 상관없이 일관성있고, 편리하게 key=value 형식의 외부 설정값을읽기 위해 "추상화"
  * **Environment 와 PropertySource**
* **결론은 Environment 로 전부 간단히 조회가능!!**
  * 심지어 getProperty 로 값을 읽으면? 커맨드라인, 자바시스템 방식이든 전부 똑같은 코드로 지원!! -> 추상화의 장점
* **우선순위 - 커맨드라인, 자바시스템 중복으로 적용할 수도 있으니 "우선순위" 가 필요**
  * 더 유연한 것이 우선권을 가짐
  * 범위가 넒은 것보다 좁은것이 우선권을 가짐
  * JVM보다 main에 arg로 오는게 더 좁으니 "커맨드라인 옵션인수" 가 더 우선순위

![image](https://github.com/BH946/spring-second-roadmap/assets/80165014/a83cf369-1ac6-4e3a-b2f8-fba79c99541a) 

<br>

**EnvironmentCheck.java - Environment 와 PropertySource**

```java
@Slf4j
@Component
public class EnvironmentCheck {
    private final Environment env;
    public EnvironmentCheck(Environment env) {
        this.env = env; 
    }
    
    @PostConstruct 
    public void init() {
        String url = env.getProperty("url");
        String username = env.getProperty("username");
        String password = env.getProperty("password");
        log.info("env url={}", url);
        log.info("env username={}", username);
        log.info("env password={}", password); 
    }
}
```

<br><br>

## 1-4. (중요!) 외부 설정 - 외부 파일, 내부 파일 분리&합체

**외부 설정은 "외부 파일, 내부 파일 분리, 내부 파일 합체" 로 총 3가지 방식이 있으며 "내부 파일 합체" 가 제일 최신**

**외부 파일**

* application.properties (yml가능) 을 자바와 동일 계층에 함께두고 실행시 자동 설정

![image](https://github.com/BH946/spring-second-roadmap/assets/80165014/e39fc766-321f-4be8-af22-d05a92db48c6) 

<br>

**내부 파일 분리**

* 프로젝트 내부에 application-dev.properties, application-prod.properties 파일 세팅
* 이후 실행할 때 "프로필" 사용 - 아래는 Jar 실행 예시
  * `java -Dspring.profiles.active=dev -jar external-0.0.1-SNAPSHOT.jar`
  * `java -jar external-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev`

![image](https://github.com/BH946/spring-second-roadmap/assets/80165014/04b178de-1e42-4c02-a6cb-d41de903a180) 

<br>

**내부 파일 합체**

* 두개로 나눈 설정 파일 조차 "하나로 통합" - 아래는 예시코드

```properties
spring.config.activate.on-profile=dev 
url=dev.db.com
username=dev_user 
password=dev_pw 
#---
spring.config.activate.on-profile=prod 
url=prod.db.com
username=prod_user 
password=prod_pw
```

![image](https://github.com/BH946/spring-second-roadmap/assets/80165014/f0e1aefa-2765-4ce7-82e4-1180fa717e93) 

<br>

**우선순위!!**

**우선순위는 위에서 아래로 적용된다. 아래가 더 우선순위가 높다.**

* 더 유연한 것이 우선권
* 범위가 넒은 것보다 좁은 것이 우선권

**application.properties**

* 실행할 때 따로 "프로필" 설정 없으면 "default" 프로필을 사용한다고 했다.
  * 아래 코드에서 1~3줄 부분
* 또한, "dev" 프로필 사용 및 url만 가진다고 가정하면, url만 덮어씌우고 username, password는 그대로 local 내용이 된다.
  * 우선순위는 위에서 아래로 적용

```properties
url=local.db.com 
username=local_user 
password=local_pw 
#---
spring.config.activate.on-profile=dev 
url=dev.db.com
username=dev_user 
password=dev_pw 
#---
spring.config.activate.on-profile=prod 
url=prod.db.com
username=prod_user 
password=prod_pw 
#---
url=hello.db.com
```

**자주 사용하는 우선순위**

* 설정데이터( application.properties ) 
* OS 환경변수
* 자바시스템속성 
* 커맨드라인 옵션 인수
* @TestPropertySource  (테스트에서 사용) 

**설정 데이터 우선순위**

* jar 내부 application.properties
* jar 내부프로필 적용파일 application-{profile}.properties
* jar 외부 application.properties
* jar 외부프로필 적용파일 application-{profile}.properties

<br><br>

# 2. 외부설정과 프로필2 - "사용"

**`@Configuration` 으로 빈에 등록하는 코드를 여러개 작성해볼 예정이라 `@Import, scanBasePackages` 를 사용**

```java
//@Import(MyDataSourceEnvConfig.class)
@Import(MyDataSourceValueConfig.class) // 주석으로 인해 얘만 컴포넌트 스캔
@SpringBootApplication(scanBasePackages = "hello.datasource") // 스캔 기본경로
public class ExternalReadApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExternalReadApplication.class, args);
    } 
}
```

<br>

**(참고) application.properties**

```properties
my.datasource.url=local.db.com
my.datasource.username=local_user 
my.datasource.password=local_pw
my.datasource.etc.max-connection=1 
my.datasource.etc.timeout=3500ms
my.datasource.etc.options=CACHE,ADMIN
```

<br><br>

## 2-1. Environment

**Environment 사용**

```java
@Slf4j
@Configuration
public class MyDataSourceEnvConfig {
    private final Environment env;
    public MyDataSourceEnvConfig(Environment env) {
        this.env = env; 
    }
    
    @Bean
    public MyDataSource myDataSource() {
        String url = env.getProperty("my.datasource.url");
        String username = env.getProperty("my.datasource.username");
        String password = env.getProperty("my.datasource.password");
        int maxConnection = env.getProperty("my.datasource.etc.max-connection",
                                            Integer.class);
        Duration timeout = env.getProperty("my.datasource.etc.timeout", 
                                           Duration.class);
        List<String> options = env.getProperty("my.datasource.etc.options", 
                                               List.class);
        return new MyDataSource(url, username, password, maxConnection,
                                timeout, options);
    } 
}
```

<br><br>

## 2-2. @Value

**@Value 사용 - 자동 타입변환 장점!**

```java
@Slf4j
@Configuration
public class MyDataSourceValueConfig {
    @Value("${my.datasource.url}") 
    private String url;
    @Value("${my.datasource.username}") 
    private String username;
    @Value("${my.datasource.password}") 
    private String password;
    @Value("${my.datasource.etc.max-connection}") 
    private int maxConnection;
    @Value("${my.datasource.etc.timeout}") 
    private Duration timeout;
    @Value("${my.datasource.etc.options}") 
    private List<String> options;
    
    // 방법 1
    @Bean
    public MyDataSource myDataSource1() {
        return new MyDataSource(url, username, password, maxConnection,
                                timeout, options);
    }
    
    // 방법 2 (내부에서 한번에)
    @Bean
    public MyDataSource myDataSource2(
        @Value("${my.datasource.url}") String url,
        @Value("${my.datasource.username}") String username, 
        @Value("${my.datasource.password}") String password,
        @Value("${my.datasource.etc.max-connection}") int maxConnection, 
        @Value("${my.datasource.etc.timeout}") Duration timeout,
        @Value("${my.datasource.etc.options}") List<String> options) { 
        return new MyDataSource(url, username, password, maxConnection,
                                timeout, options);
    } 
}
```

<br><br>

## (중요!) 2-3. @ConfigurationProperties

**스프링은 외부설정의 묶음 정보를 객체로 변환 하는 기능을 제공 -> `타입 안전한 설정 속성`**

* 생성자 주입방식 사용 가능 -> 즉, Setter 없이 가능
* "검증기" 사용 가능 -> @Validate
  * `implementation 'org.springframework.boot:spring-boot-starter-validation'`

<br>

**MyDataSourcePropertiesV3 - @ConfigurationProperties**

* @DefaultValue : 기본값 사용가능
* 참고 : **@ConstructorBinding**는 생성자 바인딩 시에 필수 였으나 **스프링 부트 3.0** 부터는 생성자가 **하나**일 때는 생략 가능

```java
@Getter
@ConfigurationProperties("my.datasource") 
@Validated
public class MyDataSourcePropertiesV3 {
    @NotEmpty
    private String url; 
    @NotEmpty
    private String username; 
    @NotEmpty
    private String password;
    
    private Etc etc;
    public MyDataSourcePropertiesV3(String url, String username, String 
                                    password, Etc etc) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.etc = etc;
    }
    
    @Getter
    public static class Etc {
        @Min(1) 
        @Max(999)
        private int maxConnection; 
        
        @DurationMin(seconds = 1)
        @DurationMax(seconds = 60) 
        private Duration timeout; 
        
        private List<String> options;
        
        public Etc(int maxConnection, Duration timeout, @DefaultValue("DEFAULT") List<String> options) {
            this.maxConnection = maxConnection;
            this.timeout = timeout;
            this.options = options;
        } 
    }
}
```

<br>

**MyDataSourceConfigV3 - @EnableConfigurationProperties**

* @EnableConfigurationProperties : MyDataSourcePropertiesV3 를 스프링 빈에 등록
* @Import(MyDataSourceConfigV3.class) 를 통해 이 파일 자체를 스프링 빈에 등록

```java
@Slf4j
@EnableConfigurationProperties(MyDataSourcePropertiesV3.class) 
public class MyDataSourceConfigV3 {
    private final MyDataSourcePropertiesV3 properties;
    public MyDataSourceConfigV3(MyDataSourcePropertiesV3 properties) {
        this.properties = properties; 
    }
    
    @Bean
    public MyDataSource dataSource() {
        return new MyDataSource(
            properties.getUrl(),
            properties.getUsername(),
            properties.getPassword(),
            properties.getEtc().getMaxConnection(),
            properties.getEtc().getTimeout(),
            properties.getEtc().getOptions()); 
    }
}
```

<br><br>

## (중요!) 2-4. 보충

### YAML 사용을 선호!

**`src/main/resources/application.yml` -> 계층 구조를 이루는것이 특징**

```yaml
my:
	datasource:
        url: local.db.com 
        username: local_user 
        password: local_pw 
        etc:
            max-connection: 1 
            timeout: 60s
            options: LOCAL, CACHE
```

<br>

**yml에 프로필까지!!**

* **`---` 로 구분!!**
* default, dev, prod 프로필 등록

```yaml
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
spring:
    config:
        activate:
		    on-profile: dev 
my:
    datasource:
        url: dev.db.com 
        username: dev_user 
        password: dev_pw 
        etc:
            maxConnection: 10 
            timeout: 60s 
            options: DEV, CACHE
---
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

<br><br>

### @Profile

**설정값이 다른 정도가 아니라 각 환경마다 서로 다른 빈 등록이 필요하다면??**

* 예를 들어, 결제 기능은 로컬 개발환경에서는 실제결제가 발생하면 문제....  
  따라서 가짜결제 기능이있는 스프링 빈을 등록하고, 운영 환경에서는 실제 결제기능 스프링빈을 등록이 필요!!

<br>

**DI 사용을 위해 "인터페이스"**

```java
public interface PayClient {
    void pay(int money); 
}
```

<br>

**LocalPayClient - 로컬**

```java
@Slf4j
public class LocalPayClient implements PayClient {
    @Override
    public void pay(int money) {
        log.info("로컬 결제 money={}", money);
    } 
}
```

<br>

**ProdPayClient - 운영**

```java
@Slf4j
public class ProdPayClient implements PayClient {
    @Override
    public void pay(int money) {
        log.info("운영 결제 money={}", money);
    } 
}
```

<br>

**OrderService**

* 상황에 따라 **LocalPayClient 또는 ProdPayClient 주입** 받는다

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    private final PayClient payClient; // LocalPayClient or ProdPayClient 
    
    public void order(int money) {
        payClient.pay(money);
    } 
}
```

<br>

**PayConfig**

* @Profile 은 @Conditional 로 구현되어 있어서 파라미터로 등록한 프로필이 활성화된 경우 **"빈 등록 진행"**

```java
@Slf4j
@Configuration
public class PayConfig {
    @Bean
    @Profile("default")
    public LocalPayClient localPayClient() { 
        log.info("LocalPayClient 빈 등록");
        return new LocalPayClient(); 
    }
    
    @Bean
    @Profile("prod")
    public ProdPayClient prodPayClient() { 
        log.info("ProdPayClient 빈 등록");
        return new ProdPayClient(); 
    }
}
```

<br>

**RunOrder**

* 현재 web 라이브러리가 없으므로 실행시 바로 종료
* **따라서 이런경우 ApplicationRunner 같은 구현체를 만들어 자바코드를 실행하는게 보통이다.**
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

**ExternalReadApplication 변경**

* hello.pay 하위가 컴포넌트 스캔되게끔 수정

```java
@Import(MyDataSourceConfigV3.class)
@SpringBootApplication(scanBasePackages = {"hello.datasource", "hello.pay"}) 
public class ExternalReadApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExternalReadApplication.class, args);
    }
}
```

<br>

**실행**

* prod 프로필 실행 : `--spring.profiles.active=prod`
* default 프로필 실행 : `프로필 없이 실행`

<br><br>

# Folder Structure

생략..
