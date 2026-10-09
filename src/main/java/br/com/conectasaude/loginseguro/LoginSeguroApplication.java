package br.com.conectasaude.loginseguro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.data.mongo.config.annotation.web.http.EnableMongoHttpSession;

@SpringBootApplication
@EnableMongoHttpSession(maxInactiveIntervalInSeconds = 28800)
public class LoginSeguroApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoginSeguroApplication.class, args);
    }
}
