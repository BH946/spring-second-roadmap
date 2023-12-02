# Intro

**`우테코 프리코스 4주간` 겪은 경험을 바탕으로 "코딩 컨벤션(Coding Convention)"을 정리**

* 구글 자바 컨벤션
* 깃 컨벤션
* 우테코 프리코스 등등...

<br><br>

# CHECK LIST



## 깃 커밋 컨벤션

* feat (feature)
  * ex) `git commit -m "feat: xxx 기능 구현"`
* fix (bug fix)
* docs (documentation)
  * 예로 README.md 작성
* style (formatting, missing semi colons, …)
* refactor
  * 리팩토링 - **Production Code 변경**
* test (when adding missing tests)
* chore (maintain)
  * 유지보수 관련 빌드, 패키지 매니저 등 설정 - **Production Code 변경X**

<br><br>

## 구글 자바 컨벤션

* **참고 문서 : [자세한것은 이곳에서](https://github.com/JunHoPark93/google-java-styleguide), [자동 적용법](https://blog.dglee.co.kr/22)**

* **구글 자바 컨벤션 설치 : [스타일가이드](https://blog.dglee.co.kr/22), [깃허브](https://github.com/google/styleguide)**
  
  * **Ctrl+Alt+L(자동 정렬) / Ctrl+Alt+I(임포트 정리)**
* 파일 코드 구조 : 라이센스(저작권) -> 패키지 구문 -> 임포트 구문 -> 하나의 최상위 클래스

* 와일드 카드(=*) 임포트 사용X : `import java.io.*`

* **열제한 : 100 / 들여쓰기 : +2 / 줄바꿈 들여쓰기 : +4**

* **K & R 스타일**(=Kernighan & Ritchie)

  ```java
  return new MyClass() {
    @Override public void method() {
      if (condition()) {
        try {
          something();
        } catch (ProblemException e) {
          recover();
        }
      } else if (otherCondition()) {
        somethingElse();
      } else {
        lastThing();
      }
    }
  };
  void doNothingElse() { // 권장
  }
  private enum Suit { CLUBS, HEARTS, SPADES, DIAMONDS } // enum은 허용
  ```

* C스타일 배열 선언문 금지

  * **`String[] args` OK**
  * `String args[]` NO

* 주석 예시

  ```java
  /*
   * This is          // And so           /* Or you can
   * okay.            // is this.          * even do this. */
   */
  ```

* long의 값을 가지는 정수 리터럴은 대문자 L : `3000l`  대신 `3000L` 사용

  * l과 1의 햇갈림을 방지

* 식별자 룰 - 패키지, 클래스, 함수, 상수 제외하고는 전부 유사

  * **패키지** 이름 전부 소문자(언더스코어 없음) : `com.example.deepspace`
  * **클래스** 이름은 UpperCamelCase(=Pascal Case) : `CharacterItem` 주로 "명사, 명사구"
  * **함수** 이름은 lowerCamelCase : `sendMessage` 주로 "동사, 동사구"
  * **상수** 이름은 `CONSTANT_CASE` 주로 "명사, 명사구"
  * 필드 이름은 lowerCamelCase : `computedValues` 주로 "명사, 명사구"
  * 파라미터 이름은 lowerCamelCase : 메서드 이름과 유사한건 피해주기
  * 지역변수 이름은 lowerCamelCase

* 참고) 우테코 컨벤션 특징

  * 블럭 들여쓰기 기본 4칸(스페이스4)
  * 줄바꿈 들여쓰기 기본 8칸(스페이스8)
  * 문자 유니코드, 열제한 120

<br><br>

## 프리코스 과정 중 컨벤션

* 깃 관리할 필요 없는 파일들이 존재
  * `.class` 파일은 java코드로 생성되어서 관리할 필요 X 
  * `.idea` 폴더는 IntelliJ IDEA 가 자동 생성하므로 관리할 필요 X
  * `.gitignore` 확인해보면 "out/, .idea" 선언 확인
* **서비스 구현을 요청받았을때 해결 과정**
  * README.md 작성 : **요구사항 분석 -> 기능 목록 <-> 구현**
  * 비지니스, UI 로직 분리는 기본
  * 참고) 프리코스 3주차 README.md 내용을 아래에서 확인
* 기능 목록을 처음부터 상세히 작성할 필요X -> 오히려 구현하면서 수정해주자(+예외처리)
* 값을 "하드 코딩 X" -> **상수(static final)** 권장!
* 변수 이름에 **자료형 사용X**
* 하나의 **함수는 하나의 기능**!! + **함수 길이 15라인** 이내로 분리 연습!!
  * main 함수도 포함
* 테스트는 "작은 단위 테스트" 부터 진행!
* **3항 연산자는 사용X** -> 가독성이 떨어지기 때문!
* **else 사용X** 연습!! -> 부정문 등등 여러 방법으로 조건문 생각하길
* 연관성 있는 상수는 **Java Enum 사용** -> static final 대신 enum 연습!!
* 값의 변경을 막을 변수들은 꼭 **final 키워드** 사용!!
* 인스턴스 변수의 접근 제어자는 private으로 구현(항상 하는거긴 함)
* 객체는 객체스럽게 사용! -> getter만 하지말고 "가능한 비지니스 로직"은 작성권장(=**도메인 모델 패턴**)
* 불 필요한 필드(인스턴스 변수)는 제거
* **테스트를 위한 코드는 구현 코드에서 구현하지 말자**
  * 테스트를 위해 접근 제어자를 바꾸는 경우
  * **테스트 코드에서만 사용되는 메서드**
* **[메서드 시그니처를 수정하여 테스트하기 좋은 메서드로 만들기](https://tecoble.techcourse.co.kr/post/2020-05-07-appropriate_method_for_test_by_parameter/)**
* **private 함수를 테스트 하고 싶다면 클래스(객체) 분리를 고려**
  * 가독성의 이유만으로 분리한 private 함수의 경우 public 함수로 검증 가능하다고 여겨질 수 있다. (public 함수가 private 함수를 사용하고 있는 경우 테스트 범위에 포함됨)
  * 하지만 가독성 이상의 역할을 하는 경우, 테스트하기 쉽게 구현하기 위해서는 해당 역할을 수행하는 다른 객체를 만들 타이밍이 아닐지 고민해 볼 수 있다. 
  * **따라서 너무 많은 역할을 하고 있는 함수나 객체를 어떻게 의미 있는 단위로 분할할지에 초점을 맞춰 볼 필요가 있다.**

<br>

**Enum 추가 정리 - 자주 사용해보자!**

**예시 코드**

```java
// enum 도 객체!
public enum Menu {
    양송이수프(6000, "애피타이저"), 타파스(5500, "애피타이저"), 시저샐러드(8000, "애피타이저"),
    티본스테이크(55000, "메인"), 바비큐립(54000, "메인"), 해산물파스타(35000, "메인"), 크리스마스파스타(25000, "메인"),
    초코케이크(15000, "디저트"), 아이스크림(5000, "디저트"),
    제로콜라(3000, "음료"), 레드와인(60000, "음료"), 샴페인(25000, "음료");
    private final int price;
    private final String type;
    Menu(int price, String type) {
        this.price = price;
        this.type = type;
    }

    public int getPrice() {
        return this.price;
    }
    public String getType() {
        return this.type;
    }
}

public enum Direction { EAST, SOUTH, WEST, NORTH } // 값 : 0, 1, 2, 3 가짐 (참고용 TEST)
```

<br>

**출력 테스트**

```java
System.out.println(Menu.valueOf("샴페인")); // 샴페인
System.out.println(Menu.샴페인); // 샴페인
System.out.println(Menu.샴페인.getType()); // 음료
System.out.println(Menu.샴페인.getPrice()); // 25000
System.out.println(Menu.샴페인.name()); // 샴페인
System.out.println(Menu.샴페인.ordinal()); // 11 -> 11번째 순서의미
for(Direction dir : Direction.values()) { // 배열로 반환하는 메소드
    System.out.println(dir.compareTo(Direction.WEST)); // -2 -1 0 1
}
```

<br>

**기본 제공 메소드**

* `Menu.샴페인` 처럼 바로 해당 Enum 객체에 접근도 가능하며,

* Enum에서 제공하는 `valueOf` 메소드를 통해서 "String" 으로도 접근 가능

* name() : 이름반환

* ordinal() : 순서 반환

* values() : 배열로 Enum 객체 전부 반환

<br>

**`-2 -1 0 1` 의 의미는?**

* compareTo 특징과 Enum 특징 때문
  * `public enum Direction { EAST, SOUTH, WEST, NORTH } // 값 0, 1, 2, 3 가짐`
  * compareTo 는 두 값을 뺀 결과가 나옴
    * 음수면 "왼쪽"
    * 양수면 "오른쪽"
    * 0이면 "동일"
* 계산 과정
  * 0 - 2 = -2
  * 1 - 2 = -1
  * 2 - 2 = 0
  * 3 - 2 = 1

<br><br>

# 프리코스 3주차 기능구현 과정

**과제이름 로또 - 기능 목록 작성 후 규칙에 맞춰서 구현**

<br>

## 기능 목록

### 1. 입력 기능

* [x] 로또 구입 금액을 입력 - InputLotto#purchaseMoney(), readMoney()
  * [x] 사용자가 잘못된 값을 입력하면 예외 발생 - IllegalArgumentException
* [x] 당첨 번호를 입력 - InputLotto#answerNumber(), readAnswer()
  * [x] 사용자가 잘못된 값을 입력하면 예외 발생 - IllegalArgumentException
  * [x] 숫자 범위 예외 처리용 함수 - InputLotto#exceptNumberRange()
* [x] 보너스 번호를 입력 - InputLotto#bonusNumber(), readBonus()
  * [x] 사용자가 잘못된 값을 입력하면 예외 발생 - IllegalArgumentException
  * [x] 숫자 범위 예외 처리용 함수 - InputLotto#exceptNumberRange()

<br>

### 2. 로또 기능

* [x] 1개의 로또 발행 - LottoGenerator#createLotto()
  * [x] X개의 로또 기록 - LottoGenerator#createLottos()
* [x] 사용자 로또 번호와 당첨 번호 비교 - Referee#compareToLottos(), compareToLotto()
  * [x] 수익률 계산 - Referee#calculateIncome()
* [x] 기존 로또 클래스에 getter 추가 - Lotto#getNumbers()

<br>

### 3. 출력 기능

* [x] 로또 수량 출력 - OutputLotto#counts()
* [x] 로또 번호 출력(오름차순) - OutputLotto#numbers()
* [x] 당첨 내역 출력 - OutputLotto#winningHistory()
* [x] 수익률 출력 - OutputLotto#incomeRate()

<br>

### 4. 추가 예외 처리

* [x] 숫자 범위 예외 처리용 함수 - InputLotto#exceptNumberRange()
* [x] 로또 숫자 중복 검증 함수 - Lotto#validateDuplication()

<br><br>

## 요구사항 분석

### 1. 입출력 요구 사항

#### 1-1. 입력

* **로또 구입 금액 입력**
  * 구입 금액은 **1000원 단위**
  * 1000원으로 나누어 떨어지지 않으면 **예외 처리**
* **당첨 번호를 입력**
  * 번호는 **쉼표(,)**를 기준으로 **구분**
* **보너스 번호를 입력**

<br>

#### 1-2. 출력

* **로또 수량과 번호를 출력**
  * 번호는 오름차순 정렬
* **당첨 내역을 출력**
  * 쉼표(,)로 1,000원 단위 구분 : `3개 일치 (5,000원) - 1개`
  * 보너스 볼 일치할 경우 : `5개 일치, 보너스 볼 일치 (30,000,000원) - 0개`
* **수익률을 출력**
  * 소수점 둘째 자리에서 반올림 : `총 수익률은 62.5%입니다.`
* **예외 상황 에러 문구 출력**
  * "[ERROR]" 로 시작 : `[ERROR] 로또 번호는 1부터 45 사이의 숫자여야 합니다.`

<br>

### 2. 로또 기능 분석

* **1개의 로또 발행** 
  * 중복되지 않는 6개의 숫자
  * 범위 : 1~45
  * 가격 : 1000원
* **사용자 로또 번호와 당첨 번호를 비교**
  * 당첨 내역
  * 수익률
* 잘못된 값 입력시 `IllegalArgumentException` 에러 발생 후 다시 입력 받기
  * "[ERROR]" 로 시작하는 에러메시지 출력 필수

<br>

### 3. 테스트 예제

* **입력**

  * `구입금액을 입력해 주세요.`
  * 로또 구입 금액 : `14000`
    * 14000 / 1000 을 통해 14개의 로또가 발행되어야 함
    * 만약 14001 이라면? 예외 발생
  * 담청 번호 : `1,2,3,4,5,6`
  * 보너스 번호 : `7`

* **출력**

  * 구매수량 출력 : `8개를 구매했습니다.`

  * 로또 번호 출력(오름차순)

    * `[8, 21, 23, 41, 42, 43]`
    * `[3, 5, 11, 16, 32, 38]`
    * 등등... 8개..

  * 당첨 내역을 출력

    * `3개 일치 (5,000원) - 1개`
    * `4개 일치 (50,000원) - 0개`
    * `5개 일치 (1,500,000원) - 0개`
    * `5개 일치, 보너스 볼 일치 (30,000,000원) - 0개`
    * `6개 일치 (2,000,000,000원) - 0개`

  * 수익률 출력(소수점 둘째 자리 반올림) : `총 수익률은 62.5%입니다.`

  * 예외 상황 : `[ERROR] 로또 번호는 1부터 45 사이의 숫자여야 합니다.`

<br><br>
