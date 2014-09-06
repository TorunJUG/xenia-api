package pl.jug.torun.xenia

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableTransactionManagement
class Application {

    static void main(String[] args) {
        SpringApplication.run Application, args
    }
}
