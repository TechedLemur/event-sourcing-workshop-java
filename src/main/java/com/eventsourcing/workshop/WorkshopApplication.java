package com.eventsourcing.workshop;

import java.util.concurrent.ExecutionException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.kurrent.dbclient.ConnectionStringParsingException;

@SpringBootApplication
public class WorkshopApplication {

	public static void main(String[] args)
			throws ConnectionStringParsingException, ExecutionException, InterruptedException, JsonProcessingException {
		SpringApplication.run(WorkshopApplication.class, args);

	}

}
