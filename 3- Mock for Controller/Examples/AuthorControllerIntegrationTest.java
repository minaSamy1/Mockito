package com.example.spring_jpa.controller;

import com.example.spring_jpa.Services.AuthorServices;
import com.example.spring_jpa.entity.Author;
import com.example.spring_jpa.entity.Book;
import com.example.spring_jpa.repository.AuthorRepo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.greaterThan;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
//@WebMvcTest(controllers = AuthorController.class, excludeAutoConfiguration = {
//        org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class,
//        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class
//})


public class AuthorControllerIntegrationTest {

    private final String URL_PATH = "/author";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthorRepo repo;

    @BeforeEach
    public void initMethod() {
        repo.deleteAll();
//        Author outher = new Author("mina");
//        Author saveed = repo.save(outher);
//        System.out.println(" Init Testing Saving  the author ");
    }

    @Test
    public void addAuthorTest() throws Exception {


        Author newAuthor = new Author(LocalDate.of(1994, 06, 5), "mina");
        Author savedAutor = new Author(1L, LocalDate.of(2994, 06, 5), "mina");


        String url = URL_PATH;
        MvcResult result = mockMvc.perform(
                        post(url)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(newAuthor))
                )
                .andExpect(status().isOk()).andReturn();

        String response = result.getResponse().getContentAsString();
        System.out.println(" Api Response " + response);


    }

    @Test
    public void getAuthorTest() throws Exception {
        Author outher = new Author("mina");
        Author saveed = repo.save(outher);
        System.out.println(" Init Testing Saving  the author ");

        // System.out.println(mockMvc.perform(get(URL_PATH)).andReturn().getResponse().getContentAsString() );
        mockMvc.perform(get("/author")).andExpect(status().isOk()).
                andExpect(jsonPath("$.length()", greaterThan(0)))
                .andExpect(jsonPath("$[0].name").value("mina"));


    }

    @Test
    public void getAuthorEmptyTest() throws Exception {


        System.out.println(mockMvc.perform(get(URL_PATH)).andReturn().getResponse().getContentAsString());
        mockMvc.perform(get("/author")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));

    }
//

    //
    @Test
    public void addAuthorBadRequestValidationTest() throws Exception {


        Author newAuthor = new Author(LocalDate.of(2994, 06, 5), "mina");


        String url = URL_PATH;
        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newAuthor))).andExpect(status().isBadRequest());


    }

    //
    @Test
    public void UpdateAuthorTest() throws Exception {


        // Author newAuthor= new Author( LocalDate.of(1994,06,5)  , "mina" ) ;
        Author updateAutor = new Author(1L, LocalDate.of(1994, 06, 5), "mina");


        String url = URL_PATH + "/update";
        mockMvc.perform(
                        post(url)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateAutor))
                )

                .andExpect(status().isOk());


    }

    //
//
//
    @Test
    public void deleteAuthor() throws Exception {


        String url = URL_PATH;
        mockMvc.perform(
                delete(url).
                        queryParam("id", String.valueOf(1l)
                        )


        ).andExpect(status().isOk());


    }

    //
//
    @Test
    public void getBookOfAuthor() throws Exception {

//        List<Book> bookList = Arrays.asList(
//
//                new Book(true, "123123", 1L, 23, "book1"),
//                new Book(true, "123zxczc", 2l, 43, "book2")
//        );
//
//
//        when(services.getBooksOfAuthor(anyLong())).thenReturn(bookList);

        String url = URL_PATH + "/{id}/books";
        MvcResult result = mockMvc.perform(
                get(url, 1l)


        ).andExpect(status().isOk()).andReturn();

        String response = result.getResponse().getContentAsString();
        List<Book> returnBooks = (List<Book>) objectMapper.readValue(response, new TypeReference<List<Book>>() {
        });


        System.out.println(returnBooks.size());


        Assertions.assertTrue(returnBooks.size() > 0);


    }

    //
    @Test
    public void getBookOfAuthorEmptyList() throws Exception {


        String url = URL_PATH + "/{id}/books";
        MvcResult result = mockMvc.perform(
                get(url, 1l)


        ).andExpect(status().isOk()).andReturn();

        String response = result.getResponse().getContentAsString();
        List<Book> returnBooks = (List<Book>) objectMapper.readValue(response, new TypeReference<List<Book>>() {
        });


        System.out.println(returnBooks.size());


        Assertions.assertFalse(returnBooks.size() > 0);


    }

}
