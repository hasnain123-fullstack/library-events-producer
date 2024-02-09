package com.learnkafka.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.Controller.LibraryEventsController;
import com.learnkafka.Domain.LibraryEvent;
import com.learnkafka.producer.LibraryEventProducer;
import com.learnkafka.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LibraryEventsController.class)
class LibraryEventsControllerUnitTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    LibraryEventProducer libraryEventProducer;

    @Test
    void postLibraryEvent() throws Exception {

        var json = objectMapper.writeValueAsString(TestUtil.libraryEventRecord());
        when(libraryEventProducer.sendLibraryEventApproach3(isA(LibraryEvent.class)))
                .thenReturn(null);
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/libraryevent")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void postLibraryEvent_4xx() throws Exception {

        var json = objectMapper.writeValueAsString(TestUtil.libraryEventRecordWithInvalidBook());
        when(libraryEventProducer.sendLibraryEventApproach3(isA(LibraryEvent.class)))
                .thenReturn(null);
        var expectedMessage = "book.bookId - must not be null, book.bookName - must not be blank";

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/libraryevent")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(expectedMessage));
    }
}