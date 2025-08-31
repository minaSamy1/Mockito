package com.example.spring_jpa.controller;


import com.example.spring_jpa.Services.BookServices;
import com.example.spring_jpa.entity.Book;
import com.example.spring_jpa.repository.BooksRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.internal.matchers.GreaterThan;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
//@ExtendWith(MockitoExtension.class)
public class TestController {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    public void addbook() throws Exception {

        MvcResult result = mockMvc.perform(post("/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Book(false, "xzc323", 21, "title1"))))
                .andExpect(status().isOk())
                .andReturn();

        String bookJson = result.getResponse().getContentAsString();
        Book savedBook = objectMapper.readValue(bookJson, Book.class);
//
//
//        Assertions.assertNotNull(savedBook);
    }


    @Test
    public void addbookBatch() throws Exception {

        List<Book> bookList = Arrays.asList(

                new Book(false, "xzc323", 21, "title1"),
                new Book(true, "2323dsa", 32, "title12")
        );
        MvcResult result = mockMvc.perform(post("/book/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookList)))
                .andExpect(status().isCreated())
                .andReturn();

    }

    @Test
    public void addbookBadRequest() throws Exception {
        mockMvc.perform(post("/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Book(true, "2323dsa", 0, "title"))))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void getBooks() throws Exception {

        MvcResult result = mockMvc.perform(get("/book")).andExpect(status().isOk()).andExpect(

                jsonPath("$.length()", greaterThan(0))

        ).andReturn();

        String resultjson = result.getResponse().getContentAsString();

        System.out.println(" result " + resultjson);


    }

    @Test
    public void getBooksemptyList() throws Exception {

        //     repo.deleteAll();

        mockMvc.perform(get("/book")).andExpect(status().isOk()).andExpect(

                jsonPath("$.length()", equalTo(1))

        );

    }

    @Test
    public void getbookSummary() throws Exception {


        MvcResult result = mockMvc.perform(get("/book/summary")).andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        System.out.println("JSon >>>>  " + json);
    }

    @Test
    public void getBookByAvailablity() throws Exception {


            MvcResult result = mockMvc.perform(get("/book/available")
                    .queryParam("flag", String.valueOf(true))
            ).andExpect(status().isOk()).andReturn();
            String json = result.getResponse().getContentAsString();
            System.out.println("JSon >>>>  " + json);
    }


    @Test
    public void getBookBytitle() throws Exception {

            MvcResult result = mockMvc.perform(  get("/book/search")
                                                 .queryParam("title", String.valueOf("title1"))
                                               ).andExpect(status().isOk()).
                     andExpect(jsonPath("$.length()", greaterThan(0))) .andReturn();
            String json = result.getResponse().getContentAsString();
            System.out.println("JSon >>>>  " + json);

    }

//
    @Test
    public void getBookBytitleNoData() throws Exception {

        MvcResult result = mockMvc.perform(  get("/book/search")
                        .queryParam("title", String.valueOf("title"))
                ).andExpect(status().isOk()).
                andExpect(jsonPath("$.length()").value(0)) .andReturn();
        String json = result.getResponse().getContentAsString();
        System.out.println("JSon >>>>  " + json);

    }



    @Test
    public void deleteBook() throws Exception {


        MvcResult result=      mockMvc.perform(
                         delete("/book")
                         .queryParam("id", String.valueOf(1l))
                 ).andExpect(status().isOk()).andReturn();
        String repsonseJson= result.getResponse().getContentAsString();
        System.out.println(" >>>>>>>> "+ repsonseJson);
    }
    }
