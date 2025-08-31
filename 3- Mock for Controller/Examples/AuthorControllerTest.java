package com.example.spring_jpa.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import com.example.spring_jpa.Services.AuthorServices;
import com.example.spring_jpa.entity.Author;
import com.example.spring_jpa.entity.Book;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;


import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
//@WebMvcTest(controllers = AuthorController.class, excludeAutoConfiguration = {
//        org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class,
//        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class
//})


public class AuthorControllerTest {

    private final String URL_PATH="/author";

@Autowired
private ObjectMapper objectMapper ;
    @MockitoBean
    private AuthorServices services;

    @Autowired
    private MockMvc mockMvc;
    @Test
    public void getAuthorTest() throws Exception {

        List<Author> authors = Arrays.asList(

                new Author("mina"), new Author("marco")
        );


        when(services.getAuthor()).thenReturn(authors);
       // System.out.println(mockMvc.perform(get(URL_PATH)).andReturn().getResponse().getContentAsString() );
        mockMvc.perform(get("/author")).andExpect(status().isOk()).
                andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("mina"));

        verify(services,times(1)).getAuthor();

    }

    @Test
    public void getAuthorEmptyTest() throws Exception {

        List<Author> authors = Arrays.asList(

                new Author("mina "), new Author("marco")
        );


        when(services.getAuthor()).thenReturn(Collections.emptyList());
        System.out.println(mockMvc.perform(get(URL_PATH)).andReturn().getResponse().getContentAsString() );
        mockMvc.perform(get("/author")).andExpect(status().isOk()) ;

    }

    @Test
    public void addAuthorTest() throws Exception {


        Author newAuthor= new Author( LocalDate.of(1994,06,5)  , "mina" ) ;
        Author savedAutor = new Author(1L, LocalDate.of(2994,06,5), "mina" ) ;

        when(services.addAuthor(newAuthor)).thenReturn(savedAutor) ;


         String url=URL_PATH ;
        mockMvc.perform(
                         post(url)
                        .contentType( MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAuthor))
                     )

                .andExpect(status().isOk()) ;


    }

    @Test
    public void addAuthorBadRequestValidationTest() throws Exception {


        Author newAuthor= new Author( LocalDate.of(2994,06,5)  , "mina" ) ;
        Author savedAutor = new Author(1L, LocalDate.of(2994,06,5), "mina" ) ;

        when(services.addAuthor(newAuthor)).thenReturn(savedAutor) ;
        String url=URL_PATH ;
        mockMvc.perform(post(url)
                .contentType( MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newAuthor)) ).andExpect(status().isBadRequest()) ;


    }

    @Test
    public void UpdateAuthorTest() throws Exception {


       // Author newAuthor= new Author( LocalDate.of(1994,06,5)  , "mina" ) ;
        Author updateAutor = new Author(1L, LocalDate.of(1994,06,5), "mina" ) ;

        when(services.updateAuthor(any())).thenReturn(updateAutor) ;


        String url=URL_PATH+"/update";
        mockMvc.perform(
                        post(url)
                                .contentType( MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateAutor))
                )

                .andExpect(status().isOk()) ;


    }



    @Test
    public void deleteAuthor() throws Exception {


       doNothing().when(services).deleteAuthor(1l);

        String url=URL_PATH ;
        mockMvc.perform(
                delete(url).
                        queryParam("id",String.valueOf(1l)
                        )



        ).andExpect(status().isOk());



    }


    @Test
    public void getBookOfAuthor() throws Exception {

        List<Book> bookList = Arrays.asList(

                new Book(true, "123123", 1L, 23, "book1"),
                new Book(true, "123zxczc", 2l, 43, "book2")
        );


        when(services.getBooksOfAuthor(anyLong())).thenReturn(bookList);

        String url=URL_PATH+"/{id}/books";
        MvcResult result=   mockMvc.perform(
                       get(url, 1l)


        ).andExpect(status().isOk()).andReturn();

        String response= result.getResponse().getContentAsString() ;
        List<Book> returnBooks= (List<Book>) objectMapper.readValue(response, new TypeReference<List<Book>>() {});


        System.out.println(returnBooks.size());


        Assertions.assertTrue(returnBooks.size()>0);


    }

    @Test
    public void getBookOfAuthorEmptyList() throws Exception {



        when(services.getBooksOfAuthor(anyLong())).thenReturn(Collections.emptyList());

        String url=URL_PATH+"/{id}/books";
        MvcResult result=   mockMvc.perform(
                get(url, 1l)


        ).andExpect(status().isOk()).andReturn();

        String response= result.getResponse().getContentAsString() ;
        List<Book> returnBooks= (List<Book>) objectMapper.readValue(response, new TypeReference<List<Book>>() {});


        System.out.println(returnBooks.size());


        Assertions.assertFalse(returnBooks.size()>0);


    }

}
