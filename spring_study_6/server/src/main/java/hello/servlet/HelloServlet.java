package hello.servlet;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

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