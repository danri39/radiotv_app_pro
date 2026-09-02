package br.com.drs.radiotv_app_pro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RadiotvAppProApplication {

    public static void main(String[] args) {
        SpringApplication.run(RadiotvAppProApplication.class, args);
    }

}
