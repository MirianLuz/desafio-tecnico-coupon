package desafio.tecnico.coupon_api.utils;

import jakarta.servlet.Servlet;
import org.h2.tools.Server;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import java.sql.SQLException;

@Configuration
public class H2ConsoleConfig {

    @Bean
    public ServletRegistrationBean<Servlet> h2ConsoleServletRegistration() {
        try {
            Class<?> servletClass = Class.forName("org.h2.server.web.JakartaWebServlet");
            Servlet h2Servlet = (Servlet) servletClass.getDeclaredConstructor().newInstance();

            return new ServletRegistrationBean<>(h2Servlet, "/h2-console/*");
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível carregar o Servlet do H2. Verifique a dependência do H2 no pom.xml.", e);
        }
    }

    @EventListener(ContextRefreshedEvent.class)
    public void startH2WebServer() throws SQLException {
        Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082").start();
    }
}