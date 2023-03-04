package jpabook.jpashop;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JpashopApplication {

	public static void main(String[] args) {
		// lombok test => @Getter @Setter
//		Hello hello = new Hello();
//		hello.setData("hello");
//		System.out.println("data = " + hello.getData());

		SpringApplication.run(JpashopApplication.class, args);
	}
	
	// 하이버네이트 스프링빈에 등록(프록시 문제 해결 - 지연문제 자동 null)
	@Bean
	Hibernate5Module hibernate5Module() {
		return new Hibernate5Module();
	}

}
