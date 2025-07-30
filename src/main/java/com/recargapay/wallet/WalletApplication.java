package com.recargapay.wallet;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "Digital Wallet API – RecargaPay Technical Challenge",
                version = "0.0.1",
                description = "API for digital wallet operations built for the RecargaPay backend challenge"

        ))
public class WalletApplication {

    public static void main(String[] args) {
        SpringApplication.run(WalletApplication.class, args);
    }

}
