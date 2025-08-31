package com.example.spring_jpa.services;


import com.example.spring_jpa.Services.AuthorServices;
import com.example.spring_jpa.entity.Author;
import com.example.spring_jpa.entity.Book;
import com.example.spring_jpa.repository.AuthorRepo;
import com.example.spring_jpa.repository.BooksRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
//@SpringBootTest
public class AuthorServicesTest {

    @InjectMocks
    private AuthorServices services ;

    @Mock
    private AuthorRepo repo ;
@Mock
    private BooksRepo bookRepo;


@DisplayName(" this method should return list of author")
    @Test
    public void getAuthorTest()
    {
        when(repo.findAll()).thenReturn(Arrays.asList(new Author("mina") , new Author("marco")) );
  List<Author> others = services.getAuthor() ;
        Assertions.assertEquals(   2 ,others.size());
        Assertions.assertFalse(others.isEmpty());


        verify( repo , times(1)).findAll() ;
    }
    @Test
    public void getAuthorTestFailed()
    {
        when(repo.findAll()).thenReturn(Collections.emptyList());
        List<Author> others = services.getAuthor() ;

        Assertions.assertTrue(others.isEmpty());


        verify( repo , times(1)).findAll() ;
    }

@Test
    public void FindBookByAuthor ()
{

    Author author = new Author("mina ") ;
    when(bookRepo.findByAuthor_authorId(anyLong())).thenReturn(
            Arrays.asList(
                    new Book(author,true , 1l ,"234asd431432",23,"Book1"),
                    new Book(author,true , 2l ,"bxvcbvd",200,"Book12")

            )

    );

    List<Book> books = services.getBooksOfAuthor(12L) ;
    System.out.println( " Book of Author who Id is 12 "+ books.size());
    Assertions.assertNotNull(books);
    Assertions.assertTrue(books.size()>0) ;
    verify(bookRepo,times(1)).findByAuthor_authorId(12L);


}


    @Test
    public void FindBookByAuthorFailed ()
    {

        Author author = new Author("mina ") ;
        when(bookRepo.findByAuthor_authorId(anyLong())).thenReturn(null);


        List<Book> books = services.getBooksOfAuthor(12L) ;

        Assertions.assertNull(books);

        verify(bookRepo,times(1)).findByAuthor_authorId(12L);


    }


    @Test
    public void deleteAuthor()
    {
        doNothing().when(repo).deleteById(anyLong()); /// mocking void method

        services.deleteAuthor(12L);    // calling it
        verify(repo,times(1)).deleteById(anyLong());  // making sure it's call
    }
    public void deleteAuthorFalid()
    {
       doThrow( new RuntimeException(" No Author")).when(repo).deleteById(anyLong());

       String exceptionMessage = Assertions.assertThrows(  RuntimeException.class ,  ()-> services.deleteAuthor(1L)).getMessage();

        Assertions.assertEquals(" No Author", exceptionMessage);
        verify(repo,times(1)).deleteById(anyLong());  // making sure it's call
    }


    @Test
    public void addAuthor()
    {

        Author author = new Author("mina ") ;
        List<Book> books=  Arrays.asList(
                new Book(true,"321dsa",1L,32,"book2"),
                new Book(true,"1654dsa",2L,12,"book4")


        );

        when(repo.save(any())).thenReturn( new Author(1l, LocalDate.of(1994,05,23),books,"mina") );

        Author savedAuthor =services.addAuthor(author);
        Assertions.assertNotNull(savedAuthor);

        verify(repo, times(1)).save(any());

    }


}
