


- Repo Testing :
      where we already test the data and Query in DB So it's Integration Testing Not Unit test
	  usually used like H2-Momery DB to perform our testing 
	  
 - Common Annotation 
 
		@DataJpaTest → Used for testing JPA repositories with an in-memory database; focuses only on persistence layer.
		
	    @Test → Marks a method as a test case.

		@Order → Specifies the execution order of test methods.
		
		@Rollback → Controls whether a test transaction is rolled back after the test.

		@SpringBootTest → Loads the full Spring context; used for integration tests.

		@Autowired → Injects Spring beans (like your repository) into the test class.

		@BeforeEach → Runs a method before each test; useful for setting up test data.

		@AfterEach → Runs a method after each test; useful for cleanup.
  
		@BeforeAll → Runs once before all tests in the class; usually static.

		@AfterAll → Runs once after all tests in the class; usually static.
		
		@Transactional → Runs the test method in a transaction; can rollback changes.

		@Modifying → Used with @Query for update/delete operations in the repository.

		@Query → Defines a custom query (JPQL or native) in the repository.

		@Param → Binds method parameters to query parameters in @Query.
		
		
  Hint's :
     - in any Test class for Repo you must inject your Repo which you will Test
	         @Autowired
			   private BookRepository repo ;
			   
     - some time you need to test your function with existing DB so instead you write each Time code to save rows in DB
	    you can use for Example @BeforeEach   method which will run each time when you call any mehtod 
		   so it will insert the row and you method working direct into you test case or also in some cases 
		   you really need to save the data inside the method to used some paramter from your saved object 
		   insted you enter id = 5 No you used book.getId()
		   
		   
		   
		   
	- Some time you maybe need to save the Data which you insert in inside your method so Here we Used @Rollback( value=fale) 
	         so it will save the data .
		   
		   
		   
	 - in some cases you may need to run the test cases with speacific order so you can do it be @Order(1) , @Order(2) ..
	 
	 - Some Time in cases in your repo you need to make the function push or flush the data immediately in DB 
	    so   @Modifying(clearAutomatically = true) 
		
		
     - takecare about some Entity using @Where to usually add where clause to any query so in some cases 
	     when you test some cases you maybe feel conflict becuase some data not return this because using @Where 
		  we faced this senario in SoftDelete .
	 
     - some time data still in the cache and yo did'nt feel the update or deleted is done or not 
	     so you can  Testing with @DataJpaTest and TestEntityManager
		 
		 
		 Instead of relying only on repo.save() and repo.find...(), you can use TestEntityManager to have more control over the persistence context — specifically to flush and clear the EntityManager — and make assertions on the actual database state, not just what's in memory.
		 
		     entityManager.persistAndFlush(author);
			entityManager.persistAndFlush(book);

			repo.softDeleteBook(book.getId());
			entityManager.flush();
			entityManager.clear();



Example :

  @DataJpaTest
 public class AuthorRepoTest {
    @Autowired
    private AuthorRepo repo;


    @BeforeEach
    public void initMethod() {

        Author outher = new Author("mina");
        Author saveed = repo.save(outher);
        System.out.println(" Init Testing Saving  the author ");
    }

    @Test
    @Order(1)
    public void testSaveAuthor() {


        Author outher = new Author("Sevo");
        Author saveed = repo.save(outher);


        Assertions.assertEquals("Sevo", saveed.getName());
        Assertions.assertNotNull(saveed);


    }

    @Test
    public void findAllTest() {
        System.out.println(" Testing Find All ");
        Assertions.assertEquals(1, repo.findAll().size());

    }

    @Test
    public void findByIdTest() {

        Author outher = new Author("Test");
        Author saveed = repo.save(outher);

        Optional<Author> o = repo.findById(saveed.getAuthorId());
        Assertions.assertEquals("Test", o.get().getName());
    }

    @Test
    public void findBynameFoundTest() {


        List<Author> authors = repo.findByname("mina");
        System.out.println(" List Size " + authors.size());
        Assertions.assertEquals(1, authors.size());
        Assertions.assertEquals("mina", authors.get(0).getName());
        // Assertions.assertEquals("mina" , authors.get(1).getName());
    }

    @Test
    public void findBynameNotFoundTest() {


        List<Author> authors = repo.findByname("");
        System.out.println(" List Size " + authors.size());
        Assertions.assertEquals(0, authors.size());
        Assertions.assertEquals(true, authors.isEmpty());

        //  Assertions.assertThrows(, authors.get(0).getName());
        // Assertions.assertEquals("mina" , authors.get(1).getName());
    }

    @Test
    public void deleteAuthorTest() {
        // we save author to used it's id instead of fiexd the authorid in delete
        Author outher = new Author("Marco");
        Author saveed = repo.save(outher);


        repo.deleteById(saveed.getAuthorId());

        Optional<Author> o = repo.findById(saveed.getAuthorId());

        Assertions.assertFalse(o.isPresent());  // value not exist
    }
	
	
	
	
 2- Example with more idea and native Query and Delete and pagable
 
  @DataJpaTest
public class BookRepoTest {

    @Autowired
    private BooksRepo repo;

    @BeforeEach
    @Test
    public void init() {
        Author author = new Author("Mina");
        Book b = new Book();
        b.setPages(200);
        b.setAvailable(true);
        b.setTitle("Test1");
        b.setIsbn("12313as2321");
        b.setAuthor(author);
        repo.save(b);
    }

    @Test
    public void FindAll() {


        List<Book> outhers = repo.findAll(); // we already saved data before  so we will get direct 
        System.out.println(" Size of Book " + outhers.size());
        System.out.println(" Reading Data  " + outhers.getFirst().getAuthor().getName());

        Assertions.assertEquals(1, outhers.size());
    }

    @Test
    public void findByTitleFoundTest() {


        Assertions.assertNotNull(repo.findByTitle("Test1"));


    }

    @Test
    public void findByTitleNotFoundTest() {

        Assertions.assertTrue(repo.findByTitle("Texst1").isEmpty());


    }

    @Test
    public void findByTitleAvaliableFoundTest() {

        Assertions.assertTrue(repo.findByavailable(true).size() > 0);


    }

    @Test
    public void findByAvaliableNotFoundTest() {

        Assertions.assertTrue(repo.findByavailable(false).size() > 0);


    }

    @Test
    public void findByAuthorIdTest() {
        Author author = new Author("Marco");
        Book b = new Book();
        b.setPages(200);
        b.setAvailable(true);
        b.setTitle("Test1");
        b.setIsbn("12313as2321");
        b.setAuthor(author);
        Book b2 = new Book();
        b2.setPages(32);
        b2.setAvailable(false);
        b2.setTitle("Test110");
        b2.setIsbn("12czxxz2321");
        b2.setAuthor(author);
        repo.save(b);
        repo.save(b2);
      List<Book> book=  repo.findBookbyAuthor(author.getAuthorId()) ;

        Assertions.assertTrue(book.size() > 0);


    }

    @Test
    public void getCustomBook()
    {


        List<BookCustom> bookCustoms = repo.getCutomeBooks();
        System.out.println( bookCustoms.getFirst().getFullDesc());
        Assertions.assertEquals("test1 mina",  bookCustoms.getFirst().getFullDesc().toLowerCase());
    }

    @Transactional
@Test
    public void softDeleteTest()
    {

        Author author = new Author("HEllpo");
        Book b = new Book();
        b.setPages(200);
        b.setAvailable(true);
        b.setTitle("bookDeteted");
        b.setIsbn("12313as2321");
        b.setAuthor(author);
        repo.save(b);
       Book findBook=repo.findById(b.getId()).get() ;

        System.out.println(findBook.getTitle() +"  "+ findBook.isDeleted());



        repo.softDeleteBook(b.getId());  // deleting the book by updating the flag

        // find by will return false because we did't return any book is deleted becausw we use @Where on Book Entity
        Assertions.assertFalse(repo.findById(b.getId()).isPresent());
        System.out.println(" >>>>>>>>>>>>>>>>>   "+ repo.bookIsDeleted(b.getId()));
        Assertions.assertTrue(repo.bookIsDeleted(b.getId()));


    }

@Test
    public void  pagableFindAll()
    {


        Page<Book> b = repo.findAll(PageRequest.of(0,2, Sort.by("title").ascending()));
       Assertions.assertTrue(b.getTotalElements()>0);
    }