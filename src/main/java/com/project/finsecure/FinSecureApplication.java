package com.project.finsecure;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "FinSecure Smart Bank",
                description = "Backend REST APIs of FinSecure Bank",
                version = "v1.0",
                contact = @Contact(
                        name = "Sushobhan Mudi",
                        email = "sushobhan712702@gmail.com",
                        url = "https://github.com/sushobhan007"
                )
        ),
        externalDocs = @ExternalDocumentation(
                description = "FinSecure Smart Bank",
                url = "https://github.com/sushobhan007/FinSecure"
        )
)
public class FinSecureApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinSecureApplication.class, args);
    }

}
