# Intro..

**스프링 부트 - 핵심 원리와 활용**

* 인프런 강의듣고 공부한 내용입니다.

<br>

해당 프로젝트 폴더는 강의를 수강 후 강의에서 진행한 프로젝트를 직접 따라 작성했습니다.

따로 강의 자료(pdf)를 주시기 때문에 필요할때 해당 자료를 이용할 것이고,

이곳 README.md 파일에는 기억할 내용들만 간략히 정리하겠습니다.

* **프로젝트**
  * **자동 구성(Auto Configuration)** - autoconfig, memory-v1, project-v1, memory-v2, project-v2

<br><br>

#  프로젝트 환경설정 & 생성

**준비물**

* **Java 17 이상(스프링 3.0의 최소요구)**
* IDE: IntelliJ (이클립스도 가능합니다)
* **아파치 톰캣 10**

<br>

**프로젝트 생성**

* **Spring Boot: 3.x.x**
* **Packaging: Jar**
* **Java: 17 이상**
* **build.gradle**

```groovy
plugins {
    id 'java'
    id 'war'
}
group = 'hello'
version = '0.0.1-SNAPSHOT' 
sourceCompatibility = '17'
repositories {
mavenCentral() 
}
dependencies {
    // lombok, spring web, h2, jdbc
    implementation 'org.springframework.boot:spring-boot-starter-jdbc' 
    implementation 'org.springframework.boot:spring-boot-starter-web' 
    compileOnly 'org.projectlombok:lombok'
    runtimeOnly 'com.h2database:h2'
    annotationProcessor 'org.projectlombok:lombok'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
	//테스트에서 lombok 사용
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

# 자동 구성(Auto Configuration)

**@Transactinal 을 사용하려면 TransactionManager 가 스프링 빈으로 등록되어 있어야 한다.**

**이런것들을 매번 등록하는 건 귀찮은 일이기에 스프링은 "자동 구성" 을 제공하기 시작**

**DbConfig.java**

* @Configuration 또는 @Bean 을 "주석" 하면 "빈"에 등록 하지 않는다.
* 단, 내용은 없더라도 null은 아니다
  * **스프링 부트가 "자동 구성"으로 등록했기 때문**

```java
@Slf4j
@Configuration
public class DbConfig {
    @Bean
    public DataSource dataSource() { 
        log.info("dataSource 빈 등록");
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setJdbcUrl("jdbc:h2:mem:test"); // in memory 사용
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        return dataSource; 
    }
    @Bean
    public TransactionManager transactionManager() { 
        log.info("transactionManager 빈 등록");
        return new JdbcTransactionManager(dataSource()); 
    }
    @Bean
    public JdbcTemplate jdbcTemplate() { 
        log.info("jdbcTemplate 빈 등록");
        return new JdbcTemplate(dataSource()); // 엔티티 매니저처럼 사용
    }
}
```

<br>

## 1. 스프링 부트의 자동 구성

 **starter 라이브러리에도 포함되어 있는 `spring-boot-autoconfigure` 라이브러리를 사용**

**EX) JdbcTemplateAutoConfiguration.java**

* JdbcTemplate 스프링 빈 자동 등록해주는...
  * **@AutoConfiguration 자동 구성 사용(핵심!!)**
  * **@ConditionalXxx 시리즈가 핵심!! -> 조건에 따라 동작**

```java
@AutoConfiguration(after = DataSourceAutoConfiguration.class) 
@ConditionalOnClass({ DataSource.class, JdbcTemplate.class }) 
@ConditionalOnSingleCandidate(DataSource.class)
@EnableConfigurationProperties(JdbcProperties.class)
@Import({ DatabaseInitializationDependencyConfigurer.class, 
JdbcTemplateConfiguration.class,
NamedParameterJdbcTemplateConfiguration.class }) 
public class JdbcTemplateAutoConfiguration {}
```

<br>

**JdbcTemplateConfiguration.java**

* @ConditionalOnMissingBean 로 JdbcOperations 가 빈에 없을 때 동작을 의미
* 즉, 사용자가 등록한 빈을 더 우선으로 사용하기 위해

```java
@Configuration(proxyBeanMethods = false)
@ConditionalOnMissingBean(JdbcOperations.class) 
class JdbcTemplateConfiguration {
@Bean 
@Primary
    JdbcTemplate jdbcTemplate(DataSource dataSource, JdbcProperties properties) {}
```

<br><br>

## 2. 자동 구성 직접 만들기 - 별도의 패키지

**별도의 패키지에 만든 코드를 "스프링 빈" 을 통해서 기존 패키지에서 가져와 사용을 할 수 있다는 걸 알아야 한다.**

**EX) MemoryConfig.java**

```java
package hello.config; // 기존 hello 패키지
import memory.MemoryController; // 새로운 memory 패키지
import memory.MemoryFinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // 빈 등록
public class MemoryConfig {
    @Bean
    public MemoryController memoryController() {
        return new MemoryController(memoryFinder()); 
    }
    @Bean
    public MemoryFinder memoryFinder() {
        return new MemoryFinder();
    }
}
```

<br><br>

## 3. 자동 구성 직접 만들기 - @Conditional

**Condition 인터페이스의 matches 를 오버라이드**

* T/F 를 반환 용도

  * **MemoryConfig.java에 `@Conditional(MemoryCondition.class)` 추가해서 사용**

* **getEnvironment은** 개념이 중요한 개념이라 뒤에 따로 또 정리

  ```properties
  #VM Options
  #java -Dmemory=on -jar project.jar 
  -Dmemory=on
  
  #Program arguments
  # -- 가 있으면 스프링이 환경 정보로 사용 
  #java -jar project.jar --memory=on 
  --memory=on
  
  #application.properties
  #application.properties에 있으면 환경 정보로 사용 
  memory=on
  ```

```java
@Slf4j
public class MemoryCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata 
                           metadata) {
        String memory = context.getEnvironment().getProperty("memory");
        log.info("memory={}", memory);
        return "on".equals(memory); 
    }
}
```

<br>

**스프링이 편리하게 제공하는 @ConditionalOnXxx**

* `@ConditionalOnProperty(name = "memory", havingValue = "on")` 사용하면 위에서 만든 코드와 동일하게 동작!
* `@ConditionalOnClass, @ConditionalOnMissingClass ` 클래스가 있는 경우 동작, 나머지는 그 반대
* `@ConditionalOnBean, @ConditionalOnMissingBean` 빈이 등록 되어 있는경우 동작, 나머지는 그반대
* `@ConditionalOnProperty` 환경정보가 있는 경우 동작
* `@ConditionalOnResource` 리소스가 있는 경우 동작
* `@ConditionalOnWebApplication, @ConditionalOnNotWebApplication` 웹 애플리케이션인 경우 동작
* `@ConditionalOnExpression` SpEL 표현식에 만족하는 경우 동작

<br><br>

## 4. 자동 구성 직접 만들기 - 순수 라이브러리

**순수 라이브러리 만들기 - memory-v1 프로젝트**

**`Memory.java, MemoryFinder.java, MemoryController.java` 로 구성하여 build 하자**

* 스프링부트 플러그인을 사용하게 되면 실행가능한 Jar을 만듦
* **여기서는 실행가능한 Jar가 아니라, 다른곳에 포함되어서 사용할 순수 라이브러리 Jar이 목적이므로 스프링 부트 플러그인을 사용X**
* 스프링 컨트롤러는 필요하므로 spring-boot-starter-web 라이브러리 사용
* 스프링부트 플러그인을 사용하지 않아서 라이브러리들의 버전을 직접 명시

<br>

**순수 라이브러리(memory-v1) 사용해보기 - project-v1 프로젝트**

* "스프링부트 플러그인 등등" 사용 및!! `implementation files('libs/memory-v2.jar')` 추가
* **src 디렉토리랑 동일 계층에 libs 디렉토리 추가 및 memory-v1에서 만든 jar 파일 복제**
* **memory관련 MemoryConfig.java 를 @Configuration 로 @Bean 등록 후 사용**
  * 스프링 부트 자동 구성을 사용한게 아니라서 직접 빈 등록

<br><br>

## 5. 자동 구성 직접 만들기 - 자동 라이브러리(@AutoConfiguration)

**자동 라이브러리 만들기 - memory-v2 프로젝트**

* **memory-v1 프로젝트 복제로 memory-v2 생성 및 settings.gradle 에 name을 v2로 수정**

* **MemoryAutoConfig.java**

```java
@AutoConfiguration
@ConditionalOnProperty(name = "memory", havingValue = "on") 
public class MemoryAutoConfig {
    @Bean
    public MemoryController memoryController() {
        return new MemoryController(memoryFinder()); 
    }
    @Bean
    public MemoryFinder memoryFinder() {
        return new MemoryFinder(); 
    }
}
```

<br>

* **파일 생성 : `src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` **
* **내용 작성 : `memory.MemoryAutoConfig` **
* 이렇게되면 스프링 부트가 시작 시점에 위 파일 내용을 읽어서 MemoryAutoConfig.java를 실행
* build 후 생성된 jar를 라이브러리로 사용하자
* **여기서는 실행가능한 Jar가 아니라, 다른곳에 포함되어서 사용할 순수 라이브러리 Jar이 목적이므로 스프링 부트 플러그인을 사용X**

<br>

**자동 라이브러리(memory-v2) 사용해보기 - project-v2 프로젝트**

* "스프링부트 플러그인 등등" 사용 및!! `implementation files('libs/memory-v2.jar')` 추가
* **src 디렉토리랑 동일 계층에 libs 디렉토리 추가 및 memory-v2에서 만든 jar 파일 복제**
* 순수 라이브러리와 다른점은 Bean 등록을 따로 또 할필요가 없다는 점!! **"자동구성됨"!!**

<br>

**테스트 할 때 VM옵션 주는 법!!**

* run -> edit configurations -> + -> Application
* Modify options -> Add VM Option
* VM Option : `-Dmemory=on`
* Main Class : `hello.ProjectV2Application`
* **실행**

![image](https://github.com/BH946/spring-second-roadmap/assets/80165014/b271462a-2778-4966-9934-224bc98f9eb7) 

<br><br>

## 6. 자동 구성 이해 - ImportSelector

**@Import 에설정정보를 추가하는 방법은 2가지**

* 정적인 방법: @Import (클래스)
* 동적인 방법: @Import ( ImportSelector ) - 코드로 프로그래밍해서 동적 선택 가능

<br>

**정적**

```java
@Configuration
@Import({AConfig.class, BConfig.class}) 
public class AppConfig {...}
```

<br>

**동적**

```java
package org.springframework.context.annotation; 
public interface ImportSelector {
    String[] selectImports(AnnotationMetadata importingClassMetadata); 
    //...
}
```

<br>

**ImportSelector 예제 - src/test 하위에 만듦**

**HelloBean.java**

```java
package hello.selector; // 이곳에 파일 전부
public class HelloBean {
}
```

<br>

**HelloConfig.java**

```java
@Configuration
public class HelloConfig {
    @Bean
    public HelloBean helloBean() {
        return new HelloBean(); 
    }
}
```

<br>

**HelloImportSelector.java**

* **ImportSelector 인터페이스 구현** 
* 이곳에 코드를 작성할 수 있으므로 **"동적"**

```java
public class HelloImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{"hello.selector.HelloConfig"}; 
    }
}
```

<br>

**ImportSelectorTest.java**

* 테스트용 파일

```java
public class ImportSelectorTest {
    @Test
    void staticConfig() {
        AnnotationConfigApplicationContext appContext =
            new AnnotationConfigApplicationContext(StaticConfig.class);
        HelloBean bean = appContext.getBean(HelloBean.class);
        assertThat(bean).isNotNull(); }
    
    @Test
    void selectorConfig() {
        AnnotationConfigApplicationContext appContext =
            new AnnotationConfigApplicationContext(SelectorConfig.class);
        HelloBean bean = appContext.getBean(HelloBean.class);
        assertThat(bean).isNotNull(); 
    }
    
    // 정적
    @Configuration
    @Import(HelloConfig.class) 
    public static class StaticConfig { 
    }
    // 동적
    @Configuration
    @Import(HelloImportSelector.class) 
    public static class SelectorConfig { 
    }
```

<br><br>

## 7. 스프링 부트의 자동 구성 최종 이해

**스프링부트 자동 구성이 동작하는 원리**

**@SpringBootApplication -> @EnableAutoConfiguration -> @Import(AutoConfigurationImportSelector.class)**

* `spring-boot-autoconfigure` 라이브러리에있는 `spring-boot-autoconfigure - org.springframework.boot.autoconfigure.AutoConfiguration.imports` 이 파일에 수많은 라이브러리들이 등록되어 있음
  * 마치 앞서 배운 memory.MemoryAutoConfig 가 등록되어 있는것 처럼
* **AutoConfigurationImportSelector 는 ImportSelector 의 구현체이므로 "동적 선택"**

<br><br>

# Folder Structure

생략..
