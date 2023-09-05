package hello;

import hello.config.MyDataSourceConfigV3;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;



@Import(MyDataSourceConfigV3.class) // 해당 파일 컴포넌트 스캔
@SpringBootApplication(scanBasePackages = {"hello.datasource", "hello.pay"}) // 컴포넌트 스캔 범위
public class ExternalReadApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExternalReadApplication.class, args);
    }

}
