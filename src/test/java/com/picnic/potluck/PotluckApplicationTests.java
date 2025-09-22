package com.picnic.potluck;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
class PotluckApplicationTests {

	@Container
	static PostgreSQLContainer<?> postgres =
			new PostgreSQLContainer<>("postgres:16.10")
					.withDatabaseName("potluck")
					.withUsername("admin")
					.withPassword("admin");

	@DynamicPropertySource
	static void registerProps(DynamicPropertyRegistry r) {
		r.add("spring.datasource.url", postgres::getJdbcUrl);
		r.add("spring.datasource.username", postgres::getUsername);
		r.add("spring.datasource.password", postgres::getPassword);
		r.add("spring.flyway.url", postgres::getJdbcUrl);
		r.add("spring.flyway.user", postgres::getUsername);
		r.add("spring.flyway.password", postgres::getPassword);
	}

	@Test
	void contextLoads() {
	}

}
