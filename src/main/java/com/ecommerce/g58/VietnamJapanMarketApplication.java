package com.ecommerce.g58;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class VietnamJapanMarketApplication {

	private static final Logger logger = LoggerFactory.getLogger(VietnamJapanMarketApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(VietnamJapanMarketApplication.class, args);
		logger.info("Application has started successfully!");  // Test log message
	}
}
