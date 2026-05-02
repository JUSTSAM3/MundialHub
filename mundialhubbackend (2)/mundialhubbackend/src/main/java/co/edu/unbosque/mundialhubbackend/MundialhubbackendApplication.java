package co.edu.unbosque.mundialhubbackend;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MundialhubbackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MundialhubbackendApplication.class, args);
	}
	
	@Bean
	public ModelMapper getModelMapper() {
		return new ModelMapper();
	}

}
