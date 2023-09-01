# Intro..

**스프링 부트 - 핵심 원리와 활용**

* 인프런 강의듣고 공부한 내용입니다.

<br>

해당 프로젝트 폴더는 강의를 수강 후 강의에서 진행한 프로젝트를 직접 따라 작성했습니다.

따로 강의 자료(pdf)를 주시기 때문에 필요할때 해당 자료를 이용할 것이고,

이곳 README.md 파일에는 기억할 내용들만 간략히 정리하겠습니다.

* **프로젝트**
  * 웹 서버와 서블릿 컨테이너 - sever
  * 스프링 부트와 내장 톰캣 - embed

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
//서블릿
    implementation 'jakarta.servlet:jakarta.servlet-api:6.0.0' 
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

# 1. 웹 서버와 서블릿 컨테이너

**정적, 동적 콘텐츠 확인을 위해 `/src/main/webapp/index.html` 를 만들고, "서블릿" 하나 작성**

```java
/**
 * http://localhost:8080/test 
 */
@WebServlet(urlPatterns = "/test")
public class TestServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) 
        throws IOException {
        System.out.println("TestServlet.service"); 
        resp.getWriter().println("test");
    }
}
```

<br><br>

## 1-1. WAR 빌드와 배포

**위 코드를 실행하려면 WAR로 빌드 후 WAS(톰캣)에 배포해야 한다.**

**웹을 WAS로 실행 및 환경을 세팅하는 것이기 때문이다.**

* 실제로 WAS(톰캣) 만 `startup.sh` 로 실행해보면 웹이 정상 동작

<br>

**WAR 빌드**

* 해당 프로젝트에서 `gradlew build` 명령어 사용 -> WAR 빌드
* **JAR 파일**이 JVM 위에서 실행된다면, **WAR는** 웹 애플리케이션 서버(WAS) 위에서 실행된다.
* **WAR 구조**
  * WEB-INF
    * classes : 실행 클래스 모음 
    * lib : 라이브러리 모음
    * web.xml : 웹서버 배치 설정파일(생략 가능) 
  * index.html : 정적 리소스

<br>

**WAR 배포**

1. (bin폴더까지 접근) 톰캣 서버를 종료 `./shutdown.sh`
2. `톰캣폴더/webapps` 하위 모두 제거
3. 빌드된 `server-0.0.1-SNAPSHOT.war` 를 `/webapps` 하위에 복제 및 `ROOT.war` 로 이름변경
4. (bin폴더까지 접근) 톰캣 서버를 실행 `./startup.sh`

<br>

**MAC, 리눅스사용자**

* 톰캣폴더/bin 폴더
* 실행: ./startup.sh 
* 종료: ./shutdown.sh

<br>

**윈도우사용자**

* 톰캣폴더/bin 폴더 
* 실행: startup.bat 
* 종료: shutdown.bat

참고 : 진행이 잘 되지 않으면 `톰캣폴더/logs/catalina.out` 로그를 꼭 확인

**`localhost:8080` 접속 시 정상 동작 확인**

![image](https://github.com/BH946/spring-second-roadmap/assets/80165014/4432625b-430a-4682-a22f-7d330343390f) 

<br>

**위 과정을 수동으로 매번 하기엔 너무 귀찮으므로 이를 자동화 해보자**

**톰캣 설정 - 인델리J 무료 버전**

* `build.gradle` 내용 추가 이후 `gradlew explodedWar` 명령어 실행

  * 명령어 실행시 WAR 파일 압축풀기
  * 굳이 명령어 실행 안하고 `build/exploded` 폴더만 만들어줘도 충분

  ```groovy
  //explodedWar 명령어로 WAR 압축풀기 위함
  task explodedWar(type: Copy) { 
      into "$buildDir/exploded" 
      with war
  }
  ```

* **Tomcat runner 플러그인 설치**

  * 메뉴에서 플러그인-market에서 직접 **Tomcat runner 플러그인 설치(꼭 재시작)**
  * 이 플러그인을 사용할 "톰캣서버"를 하나 더 준비 및 **폴더명 tomcat-runner로 변경**

* 메뉴 -> Run -> Edit Configurations 에서 **Tomcat Runner 선택**

  * 준비한 "톰캣서버" 인 tomcat-runner 디렉토리 지정 및 Modules 부분 추가
    * Context: `/` 입력
    * Doc.base: `build/exploded` 폴더 지정

* Before launch 아래있는 + 버튼을선택한다. 
  * Run Gradle task를 선택한다.
  * Gradle project: 현재 프로젝트를 선택한다. 
    * Tasks: explodedWar 를 입력한다.
    * **이렇게 하면 서버를 실행하기 전에 새로 빌드하면서 `gradlew explodedWar` 를 실행**

* 실행 모습

  ![image](https://github.com/BH946/spring-second-roadmap/assets/80165014/44e428e5-2f99-4b19-bd7d-ed580a892e59) 

<br><br>

## 1-2. 서블릿 컨테이너 초기화

**MyContainerInitV1.java - "서블릿 컨테이너"**

* **ServletContainerInitializer - onStartup() 오버라이드**로 코드 작성

<br>

**WAS(톰캣)은 초기화 기능을 제공** -> 옛날에는 `web.xml` 사용했지만 지금은 자바 코드!

* `resources/META-INF/services/jakarta.servlet.ServletContainerInitializer` 파일 생성

* **hello.container.MyContainerInitV1** 로 "등록"
* 이후 실행 해보면 "정상동작"

<br>

**서블릿 등록 2가지 방법**

* @WebServlet 애노테이션 -> 매우 간편
  * 앞서 TestServlet.java 에서 이미 해봄
* 프로그래밍 방식 -> 훨씬 유연
  * 이제 해보겠음

<br>

**MyContainerInitV2.java - "서블릿 컨테이너" & WAS에 등록(jakarta... 파일에)**

* **MyContainerInitV2** 를 실행하려면 **서블릿 컨테이너**에게 알려주어야 한다. 
* resources/META-INF/services/**jakarta.servlet.ServletContainerInitializer** 에  **hello.container.MyContainerInitV2** 로 "등록"

```java
/**
 * @HandlesTypes 로 AppInit 인터페이스 구현체들 전부 찾아 사용!!
 */
@HandlesTypes(AppInit.class) // "애플리케이션 초기화" 인터페이스 지정
public class MyContainerInitV2 implements ServletContainerInitializer {
    @Override
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws 
        ServletException {
        System.out.println("MyContainerInitV2.onStartup");
        System.out.println("MyContainerInitV2 c = " + c);
        System.out.println("MyContainerInitV2 container = " + ctx);
        
        // for문으로 인해 AppInit.class 구현체를 모두 접근
        for (Class<?> appInitClass : c) {
            try {
                //new AppInitV1Servlet()과 같은 코드
                AppInit appInit = (AppInit) 
                    appInitClass.getDeclaredConstructor().newInstance(); 
                appInit.onStartup(ctx);
            } catch (Exception e) {
                throw new RuntimeException(e); 
            }
        } 
    }
}
```

<br>

```java
public interface AppInit {
    void onStartup(ServletContext servletContext); 
}
```

<br>

```java
/**
 * http://localhost:8080/hello-servlet 
 * AppInit 구현체이기 때문에 자동으로 MyContainerInitV2 에 등록
 *
 * servletContext.addServlet() 서블릿 등록 - "HelloServlet.java이 최종 실행"
 */
public class AppInitV1Servlet implements AppInit {
    @Override
    public void onStartup(ServletContext servletContext) { 
        System.out.println("AppInitV1Servlet.onStartup");
        //순수 서블릿 코드 등록
        ServletRegistration.Dynamic helloServlet =
            servletContext.addServlet("helloServlet", new HelloServlet()); 
        helloServlet.addMapping("/hello-servlet");
    } 
}
```

<br>

```java
/**
 * 직접 jakarta... 파일에 등록해서 WAS가 바로 초기화 시켜 "서블릿" 동작하게 해도 되며,
 * "애플리케이션 초기화" 처럼 사용하기 위해서는 "서블릿 컨테이너 초기화" 를 도입해서 활용!
 */
public class HelloServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        System.out.println("HelloServlet.service");
        resp.getWriter().println("hello servlet!");
    }
}
```

<br>

**WAS(톰캣) -> "서블릿 컨테이너 초기화" -> "애플리케이션 초기화" - 실행**

* 아래 그림의 구조

![image](https://github.com/BH946/spring-second-roadmap/assets/80165014/ed123079-218f-4344-b1bb-692d6d00fb98) 

**초기화 순서**  

1. 서블릿 컨테이너 초기화 실행  
`resources/META-INF/services/jakarta.servlet.ServletContainerInitializer`
2. 애플리케이션 초기화 실행  
`@HandlesTypes(AppInit.class)`

<br>

**"편리함 및 의존성 줄임"**

* 애플리케이션 초기화는 특정 인터페이스만 구현가능
  * 기존엔 WAS에 "서블릿 컨테이너 추가 설정"
* "애플리케이션 초기화" 중 ServletContext ctx 가 필요없으면 의존 완전 제거가능

<br><br>

## 1-3. 스프링 컨테이너 등록

**`WAS + 스프링 통합` 을 위해 "서블릿 컨테이너 초기화 + 애플리케이션 초기화"를 활용**

* 아래 과정 필요
  * "스프링 컨테이너" 만들기
  * 스프링MVC 컨트롤러를 "빈"으로 등록
  * 디스패처 서블릿을 "서블릿 컨테이너" 에 등록

<br>

**"의존성 - 스프링 MVC 추가"**

`implementation 'org.springframework:spring-webmvc:6.0.4' `

**helloController.java 컨트롤러 생성 - @GetMapping... 및 @Bean 등록**

<br>

**AppInitV2Spring.java**

* **"애플리케이션 초기화" 로 "서블릿 컨테이너" 에 "스프링 컨테이너 등록"**
* `AnnotationConfigWebApplicationContext` 로 "스프링 컨테이너 생성"

* `DispatcherServlet` 로 "스프링MVC 디스패처 **서블릿** 생성" 및 **"스프링 컨테이너 연결"**
* `servletContext.addServlet` 로 "**서블릿 컨테이너**에 디스패처 **서블릿 연결**"
* `servlet.addMapping("/spring/*")` 로 뒤에 spring/* 부분으로 스프링 컨트롤러를 찾는다.

```java
*
 * http://localhost:8080/spring/hello-spring 
 * AppInit 구현체이기 때문에 자동으로 MyContainerInitV2 에 등록
*/
    public class AppInitV2Spring implements AppInit {
        @Override
        public void onStartup(ServletContext servletContext) { 
            System.out.println("AppInitV2Spring.onStartup");
            //스프링 컨테이너 생성
            AnnotationConfigWebApplicationContext appContext = new 
                AnnotationConfigWebApplicationContext();
            appContext.register(HelloConfig.class);
            
            //스프링 MVC 디스패처 서블릿 생성, 스프링 컨테이너 연결
            DispatcherServlet dispatcher = new DispatcherServlet(appContext);
            
            //디스패처 서블릿을 서블릿 컨테이너에 등록 (이름 주의! dispatcherV2)
            ServletRegistration.Dynamic servlet =
                servletContext.addServlet("dispatcherV2", dispatcher);
            
            // /spring/* 요청이 디스패처 서블릿을 통하도록 설정
            servlet.addMapping("/spring/*");
        } 
    }
```

<br><br>

## 1-4. 스프링 MVC 지원 "서블릿 컨테이너 초기화"

**스프링 MVC는 "서블릿 컨테이너 초기화를 지원" 하므로 "애플리케이션 초기화" 만 사용하면 되며 이또한 `WebApplicationInitializer` 를 지원한다.**

**AppInitV3SpringMvc**

* `WebApplicationInitializer` 는 스프링이 이미 만들어둔 애플리케이션 초기화 인터페이스
* 이부분을 구현체로 사용했고, 나머지 코드는 V2와 동일

```java
/**
 * http://localhost:8080/hello-spring 
 * AppInit 구현체없이 자동으로 등록!!
 */
public class AppInitV3SpringMvc implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext servletContext) throws 
        ServletException {
        System.out.println("AppInitV3SpringMvc.onStartup");
        
        //스프링 컨테이너 생성
        AnnotationConfigWebApplicationContext appContext = new 
            AnnotationConfigWebApplicationContext();
        appContext.register(HelloConfig.class);
        
        //스프링 MVC 디스패처 서블릿 생성, 스프링 컨테이너 연결
        DispatcherServlet dispatcher = new DispatcherServlet(appContext);
        
        //디스패처 서블릿을 서블릿 컨테이너에 등록 (이름 주의! dispatcherV3)
        ServletRegistration.Dynamic servlet =
            servletContext.addServlet("dispatcherV3", dispatcher);
        
        //모든 요청이 디스패처 서블릿을 통하도록 설정
        servlet.addMapping("/");
    } 
}
```

<br>

**현재 등록된 서블릿**

* / = dispatcherV3
* /spring/* = dispatcherV2 
* /hello-servlet = helloServlet 
* /test = TestServlet
* 이런 경우 우선순위는 더 구체적인 것이 먼저 실행

<br>

**여기까지 상태**

* 물론 실제로는 디스패처 1개, 스프링 컨테이너 1개만 구현해서 사용

![image](https://github.com/BH946/spring-second-roadmap/assets/80165014/7e0d964f-1994-40fd-9fe2-d182ae6397fe) 

<br>

**"어떻게 `WebApplicationInitializer` 하나로 초기화가 되었나?"**

![image](https://github.com/BH946/spring-second-roadmap/assets/80165014/c20c617b-0c00-4c1c-9bf1-cce4b142a3c3) 

<br>

**결론**

* 지금까지 알아본 내용은모두 서블릿 컨테이너 위에서 동작하는 방법
  * 따라서 항상 톰캣 같은 서블릿 컨테이너에 배포
* 현재는 스프링부트와 내장톰캣을 사용하면서 이런 부분이 바뀌기 시작했다.

<br><br>

# 2. 스프링 부트와 내장 톰캣

**`tomcat-embed-cor` 톰캣 라이브러리**

![image](https://github.com/BH946/spring-second-roadmap/assets/80165014/81f85071-572a-4491-93c9-9b5f38f91ff5) 

<br><br>

## 2-1. 내장 톰캣

**내장 톰캣은 "스프링 부트" 가 자동화 해서 다 설정해주므로 아래 내용들은 이해만 하자**

**내장 톰캣 "서블릿"**

```java
public class EmbedTomcatServletMain {
    // main 메소드
    public static void main(String[] args) throws LifecycleException { 
        System.out.println("EmbedTomcatServletMain.main");
        
        //톰캣 설정
        Tomcat tomcat = new Tomcat(); 
        Connector connector = new Connector(); 
        connector.setPort(8080);
        tomcat.setConnector(connector);
        
        //서블릿 등록
        Context context = tomcat.addContext("", "/");
        tomcat.addServlet("", "helloServlet", new HelloServlet()); 
        context.addServletMappingDecoded("/hello-servlet", "helloServlet"); 
        tomcat.start();
    } 
}
```

<br>

**내장 톰캣 "스프링"**

```java
// 서블릿 컨테이너 초기화와 거의 같은코드
public class EmbedTomcatSpringMain {
    public static void main(String[] args) throws LifecycleException { 
        System.out.println("EmbedTomcatSpringMain.main");
        
        //톰캣 설정
        Tomcat tomcat = new Tomcat(); 
        Connector connector = new Connector(); 
        connector.setPort(8080);
        tomcat.setConnector(connector);
        
        //스프링 컨테이너 생성
        AnnotationConfigWebApplicationContext appContext = new 
            AnnotationConfigWebApplicationContext();
        appContext.register(HelloConfig.class);
        
        //스프링 MVC 디스패처 서블릿 생성, 스프링 컨테이너 연결
        DispatcherServlet dispatcher = new DispatcherServlet(appContext);
        
        //디스패처 서블릿 등록
        Context context = tomcat.addContext("", "/"); 
        tomcat.addServlet("", "dispatcher", dispatcher); 
        context.addServletMappingDecoded("/", "dispatcher");
        tomcat.start();
    } 
}
```

<br>

**Jar 빌드, FatJar 빌드**

* 둘다 단점들이 있어서 자세한 정리는 하지않고, 뒤에 "스프링 부트" 가 이를 다 해결해준다고 기억

```groovy
// gradle
task buildJar(type: Jar) { 
    manifest {
        attributes 'Main-Class': 'hello.embed.EmbedTomcatSpringMain'
            }
    with jar 
}

task buildFatJar(type: Jar) { 
    manifest {
        attributes 'Main-Class': 'hello.embed.EmbedTomcatSpringMain'
}
    duplicatesStrategy = DuplicatesStrategy.WARN
    from { configurations.runtimeClasspath.collect { it.isDirectory() ? it : 
zipTree(it) } }
    with jar 
}
```

<br><br>

## 2-2. 나만의 부트 클래스

**"스프링 부트" 를 이해하기 위해 "나만의 부트" 를 만들어 보자**

**MySpringApplication.java**

* EmbedTomcatSpringMain.java 의 main 함수 코드를 알맞게 "복붙" 한 것일 뿐 - run() 함수제작

```java
/**
 * "나만의 부트 클래스" 만들기
 * 톰캣 -> 디스패처 서블릿 -> 스프링 컨테이너 -> Config(컨트롤러)
 */
public class MySpringApplication {
    
    public static void run(Class configClass, String[] args) { 
        System.out.println("MySpringBootApplication.run args=" + 
                           List.of(args));
        //톰캣 설정
        Tomcat tomcat = new Tomcat(); 
        Connector connector = new Connector(); 
        connector.setPort(8080);
        tomcat.setConnector(connector);
        //스프링 컨테이너 생성
        AnnotationConfigWebApplicationContext appContext = new 
            AnnotationConfigWebApplicationContext();
        appContext.register(configClass);
        //스프링 MVC 디스패처 서블릿 생성, 스프링 컨테이너 연결
        DispatcherServlet dispatcher = new DispatcherServlet(appContext);
        //디스패처 서블릿 등록
        Context context = tomcat.addContext("", "/"); 
        tomcat.addServlet("", "dispatcher", dispatcher); 
        context.addServletMappingDecoded("/", "dispatcher");
        try {
            tomcat.start();
        } catch (LifecycleException e) {
            throw new RuntimeException(e); 
        }
    }
}
```

<br>

**@MySpringBootApplication - interface**

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME) 
@Documented
@ComponentScan // 컴포넌트 스캔 사용!!
public @interface MySpringBootApplication { 
}
```

<br>

**MySpringBootMain**

* 해당 애노테이션이 붙은 클래스의 현재패키지 부터 그 하위패키지를 컴포넌트 스캔의 대상
  * 즉, hello 패키지 하위 전부 "컴포넌트 스캔 대상"
  * 따라서 @RestController로 등록한 Controller도 자동 사용
  * HelloConfig.java 에 @Configuration 를 주석해야 이 파일도 스캔 대상
* 아까만든 run() 함수 사용

```java
@MySpringBootApplication
public class MySpringBootMain { // package hello; 선언되어 있.음.
    public static void main(String[] args) {
        System.out.println("MySpringBootMain.main"); 
        MySpringApplication.run(MySpringBootMain.class, args);
    } 
}
```

<br>

**지금까지 만든것을 라이브러리로 만들어서 배포한다면? 그것이 바로 "스프링부트"**

**일반적인 "스프링 부트" 와 매우 유사**

```java
@SpringBootApplication
public class BootApplication {
    public static void main(String[] args) {
        SpringApplication.run(BootApplication.class, args); 
    }
}
```

<br><br>

## 2-3. 스프링 부트 Jar 분석

**이번엔 "스프링 부트" 로 프로젝트 Jar 형식으로 만들었다고 가정하자**

**빌드**

* **jar 빌드 : ./gradlew clean build**
* **윈도우OS : gradlew clean build**

<br>

**jar 분석위해 압축 풀기** 

* `jar -xvf boot-0.0.1-SNAPSHOT.jar`
* (참고) :  boot-0.0.1-SNAPSHOT-plain.jar 파일은 무시 - 우리가 개발한 코드만 있는 순수한 jar

<br>

**FatJar 구조가 아닌 "실행 가능 Jar"을 "스프링 부트"는 제공**

* **jar 내부에 jar를 포함할 수 있는 특별한 구조의 jar**
* **FatJar 기존 문제**
  * 문제: 어떤 라이브러리가 포함 되어있는지 확인하기 어렵다.
  * 해결: jar 내부에 jar를 포함하기 때문에 어떤 라이브러리가 포함되어 있는지 쉽게 확인할 수 있다. 
  * 문제: 파일명 중복을 해결할 수 없다.
  * 해결: jar 내부에 jar를 포함하기 때문에 a.jar , b.jar 내부에 같은 경로의 파일이 있어도 둘다 
    인식할 수 있다.

<br>

**실행 과정 정리**

1. java -jar xxx.jar

2. MANIFEST.MF 인식

   * 매니피스트 먼저 인식하는건 "자바 표준 방식"

3. JarLauncher.main() 실행 

  * BOOT-INF/classes/ 인식 

  * BOOT-INF/lib/ 인식
  * "스프링 부트" 가 우선 arLauncher 를 메인으로 사용하기 위해 "제일 처음 실행"
  * 왜냐하면 다른 작업들을 할게 많기 때문에 arLauncher 가 BootApplication.main() 을 실행

4. BootApplication.main() 실행

5. 참고

   * 실행가능 Jar가 아니라, IDE에서 직접 실행할 때는 BootApplication.main() 을 바로 실행
   * IDE가 필요한 라이브러리를 모두 인식할 수 있게 도와주기 때문에 JarLauncher 가 필요X

<br><br>

# 3. 스프링 부트 스타터와 라이브러리 관리

## 3-1. 라이브러리 직접 관리

**수많은 라이브러리를 수동으로 지정하는 예시**

* 너무 비효율 WHY???
  * **"버전 관리 및 호완 버전 등등"을 항상 기억하는건 너무 어렵다**

```groovy
dependencies {
    //1. 라이브러리 직접 지정 
    //스프링 웹 MVC
    implementation 'org.springframework:spring-webmvc:6.0.4'
    //내장 톰캣
    implementation 'org.apache.tomcat.embed:tomcat-embed-core:10.1.5'
    //JSON 처리
    implementation 'com.fasterxml.jackson.core:jackson-databind:2.14.1'
    //스프링 부트 관련
    implementation 'org.springframework.boot:spring-boot:3.0.2'
    implementation 'org.springframework.boot:spring-boot-autoconfigure:3.0.2'
    //LOG 관련
    implementation 'ch.qos.logback:logback-classic:1.4.5'
    implementation 'org.apache.logging.log4j:log4j-to-slf4j:2.19.0'
    implementation 'org.slf4j:jul-to-slf4j:2.0.6' 
    //YML 관련
    implementation 'org.yaml:snakeyaml:1.33' 
}
```

<br><br>

## 3-2. 스프링 부트 라이브러리 버전관리

**`io.spring.dependency-management` 플러그인을 사용해야 "자동 제공!!"**

* **버전을 자동 관리 및 호환 버전도 알아서 찾아줌**
* **단, 스프링 부트가 관리하지 않는 라이브러리는 직접 버전 적어주기**
* [버전정보 bom](https://github.com/spring-projects/spring-boot/blob/main/spring-boot-project/spring-boot-dependencies/build.gradle)

```groovy
plugins {
    id 'org.springframework.boot' version '3.0.2'
    id 'io.spring.dependency-management' version '1.1.0' //추가
    id 'java'
}

dependencies {
    //2. 스프링 부트 라이브러리 버전 관리 
    //스프링 웹, MVC
    implementation 'org.springframework:spring-webmvc' 
    //내장 톰캣
    implementation 'org.apache.tomcat.embed:tomcat-embed-core' 
    //JSON 처리
    implementation 'com.fasterxml.jackson.core:jackson-databind' 
    //스프링 부트 관련
    implementation 'org.springframework.boot:spring-boot'
    implementation 'org.springframework.boot:spring-boot-autoconfigure' 
    //LOG 관련
    implementation 'ch.qos.logback:logback-classic'
    implementation 'org.apache.logging.log4j:log4j-to-slf4j'
    implementation 'org.slf4j:jul-to-slf4j'
}
```

<br><br>

## 3-3. 스프링 부트 스타터

**마지막으로 관련 라이브러리를 모아서 제공하는 "스프리 부트 스타터"**

```groovy
dependencies {
    //3. 스프링 부트 스타터
    implementation 'org.springframework.boot:spring-boot-starter-web' 
}
```

<br>

**스프링부트 스타터 - 자주사용하는것 위주**

* spring-boot-starter : 핵심 스타터, 자동구성, 로깅, YAML
* spring-boot-starter-jdbc : JDBC, HikariCP 커넥션풀
* spring-boot-starter-data-jpa : 스프링 데이터 JPA, 하이버네이트
* spring-boot-starter-data-mongodb : 스프링데이터 몽고
* spring-boot-starter-data-redis : 스프링 데이터 Redis, Lettuce 클라이언트
* spring-boot-starter-thymeleaf : 타임리프뷰와웹 MVC
* spring-boot-starter-web : 웹 구축을위한스타터, RESTful, 스프링 MVC, 내장톰캣
* spring-boot-starter-validation : 자바빈 검증기(하이버네이트 Validator)
* spring-boot-starter-batch : 스프링배치를 위한스타터

<br><br>

# Folder Structure

생략..
