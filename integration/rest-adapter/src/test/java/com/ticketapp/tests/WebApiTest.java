package com.ticketapp.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Scanner;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.ticketapp.TestContext;
import com.ticketapp.TicketappWebApplication;
import com.ticketapp.rest.domain.Ticket;

public class WebApiTest {

	private static ConfigurableApplicationContext applicationContext;

	@BeforeAll
	public static void startApplication() {
		applicationContext = SpringApplication.run(TicketappWebApplication.class,
				"--spring.main.allow-bean-definition-overriding=true",
				"--spring.jpa.defer-datasource-initialization=true");
	}

	@AfterAll
	public static void stopApplication() {
		if (applicationContext != null) {
			applicationContext.close();
		}
	}

	@Test
	public void testCreateTicket() {
		ResponseEntity<Ticket> response = createTicket();

		String path = response.getHeaders().getLocation().getPath();

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertTrue(path.startsWith("/api/v1/tickets"));
		Ticket entity = response.getBody();

		System.out.println("The ticket information: " + entity);
		System.out.println("The Location is " + response.getHeaders().getLocation());

		assertEquals(5, entity.getPriority());
	}

	private ResponseEntity<Ticket> createTicket() {
		HttpEntity<String> requestEntity = new HttpEntity<String>(read("test-data/ticket-sample.json"),
				TestContext.getHeaders());
		ResponseEntity<Ticket> response = new RestTemplate().postForEntity(TestContext.path("/api/v1/tickets"),
				requestEntity, Ticket.class);
		return response;
	}

	public static String read(String file) {
		String content = new Scanner(WebApiTest.class.getClassLoader().getResourceAsStream(file)).useDelimiter("\\Z")
				.next();
		System.out.println("read -> " + content);
		return content;
	}

}
