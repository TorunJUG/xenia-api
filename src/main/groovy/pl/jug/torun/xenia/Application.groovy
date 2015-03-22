package pl.jug.torun.xenia

import org.h2.tools.Server
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.transaction.annotation.EnableTransactionManagement
import pl.jug.torun.xenia.meetup.TokenRequester

@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableTransactionManagement
class Application {

    
    static void main(String[] args) {
        Server webServer = Server.createWebServer("-webPort","8090").start();
        SpringApplication.run Application, args
    }
}
