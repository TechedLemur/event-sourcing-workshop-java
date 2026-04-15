package com.eventsourcing.workshop.controllers;

import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventsourcing.workshop.clients.EventClient;
import com.eventsourcing.workshop.events.HelloEvent;

@RestController
@RequestMapping("/example")
public class ExampleController {

    private static final String EXAMPLE_STREAM = "hello-event-stream";
    private static final Logger logger = Logger.getLogger(ExampleController.class.getName());

    private final EventClient eventClient;

    public ExampleController(EventClient eventClient) {
        this.eventClient = eventClient;
    }

    @PostMapping
    public void createHelloEvent() {
        eventClient.emit(EXAMPLE_STREAM, new HelloEvent("Hello, javaBin!").toStoreEvent("my-subject"));
    }

    @GetMapping
    public Collection<HelloEvent> getHelloEvents() {
        logger.info("Getting hello events");
        try {
            return eventClient.read(EXAMPLE_STREAM, HelloEvent.class).toList();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
