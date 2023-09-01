package hello.container;


import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.HandlesTypes;

import java.util.Set;

/**
 * @HandlesTypes 로 AppInit 인터페이스 구현체들 for문으로 전부 찾아 사용!!
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