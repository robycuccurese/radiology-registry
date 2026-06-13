package it.cyberqual.radiology_registry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
public class RadiologyRegistryApplication {

	public static void main(String[] args) {
		SpringApplication.run(RadiologyRegistryApplication.class, args);
	}
}