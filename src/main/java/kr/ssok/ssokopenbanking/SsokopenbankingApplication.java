package kr.ssok.ssokopenbanking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SsokopenbankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(SsokopenbankingApplication.class, args);
	}

}
