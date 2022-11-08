package com.platform.energy;

import com.platform.energy.entities.ERole;
import com.platform.energy.entities.Role;
import com.platform.energy.entities.User;
import com.platform.energy.repo.IRoleRepository;
import com.platform.energy.repo.IUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;

import java.util.HashSet;
import java.util.TimeZone;

@SpringBootApplication
@Validated
public class Ds202230441ChichisanStefan1BackendApplication extends SpringBootServletInitializer {

	private static final Logger log = LoggerFactory.getLogger(Ds202230441ChichisanStefan1BackendApplication.class);

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Ds202230441ChichisanStefan1BackendApplication.class);
	}

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		SpringApplication.run(Ds202230441ChichisanStefan1BackendApplication.class, args);
	}

	@Bean
	public CommandLineRunner loadDataRole(IUserRepository iUserRepository, IRoleRepository iRoleRepository) {
		return (args -> {
			Role adminRole = iRoleRepository.save(new Role(1L, ERole.ADMIN));
			iRoleRepository.save(new Role(2L, ERole.CLIENT));

			iUserRepository.save(new User(1L, "Stefan", "stefandrei", "dilau", adminRole, new HashSet<>()));
		});
	}
}