package com.game.gamescore;

import com.game.gamescore.controller.HTTPServerController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.io.IOException;

@EnableSwagger2
@SpringBootApplication
public class GamescoreApplication {

    public static void main(String[] args) throws IOException {
        HTTPServerController localServer = new HTTPServerController();
        localServer.startServer();

        SpringApplication.run(GamescoreApplication.class, args);
    }

    @Bean
    public Docket productApi() {
        return new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors.basePackage("com.game.gamescore")).build();
    }

}
