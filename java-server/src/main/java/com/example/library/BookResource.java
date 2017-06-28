package com.example.library;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;



import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.Search;
//import com.cloudant.client.org.lightcouch.CouchDbException;
import com.cloudant.client.api.model.SearchResult;
import com.cloudant.client.api.views.Key;
import com.cloudant.client.api.views.ViewRequest;
import com.cloudant.client.api.views.ViewRequestBuilder;
import com.cloudant.client.api.views.ViewResponse;
import com.google.common.util.concurrent.RateLimiter;



// This class defines the RESTful API to fetch the database service information
// <basepath>/api/books



@Path("/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookResource {
	

	
	private static Database datab = null;
	
	private static Database initDatabase(){
		if(datab==null){
			try {
				datab = DBManager.getDB("books");
			} catch (Exception re) {
				re.printStackTrace();
			}
		}
		return datab;
	}
	

	
	@GET
	public Collection<Book> getInformation() throws Exception, IOException {
       
		initDatabase();
		
    	try {
			Collection<Book> allBooks = datab.getAllDocsRequestBuilder().includeDocs(true).build()
					.getResponse().getDocsAs(Book.class);
			LinkedList<Book> removeable = new LinkedList<Book>();
			for(Book temp : allBooks){
				//System.out.println("Bookid: "+temp.getId()+" Title: "+temp.getTitle());
				if(temp.getId()==null){
					removeable.add(temp);
				}
			}
			allBooks.removeAll(removeable);
			return allBooks;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * The mapfunction of view tagView:
	 * function (doc) {
  		var i;
  		for(i in doc.tags)
    		emit(doc.tags[i], doc._id)
		}
	 * @param tag
	 * @return
	 */
	@Path("/tag-search/{tag}")
	@GET
	public List<Book> getRelevantBooks(@PathParam("tag") String tag){
		initDatabase();
		List<Book> list = null;
		ViewRequestBuilder viewBuilder = datab.getViewRequestBuilder("BookIdx", "tagView");
		try {
			ViewRequest<String, String> request = viewBuilder.newRequest(Key.Type.STRING,String.class).reduce(false)
					  .keys(tag)
				 	  //.limit(10)
				 	  .includeDocs(true)
				 	  .build();
			list = request.getResponse().getDocsAs(Book.class);
			for(Book temp : list){
				System.out.println("Book "+temp.getTitle()+" has tag "+tag);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	@Path("/title/{title}")
	@GET
	public List<Book> getBooksByTitle(@PathParam("title") String title){
		initDatabase();
		List<Book> list = null;
		Search searchObject = datab.search("BookIdx/titleSearch");
		list = searchObject.includeDocs(true).query(title, Book.class);
		System.out.println(list);
		return list;
	}
	
	@Path("/author/{author}")
	@GET
	public List<Book> getBooksByAuthor(@PathParam("author") String author){
		initDatabase();
		List<Book> list = null;
		ViewRequestBuilder viewBuilder = datab.getViewRequestBuilder("BookIdx", "authorsView");
		try {
			ViewRequest<String, String> request = viewBuilder.newRequest(Key.Type.STRING,String.class).reduce(false)
					  .keys(author)
				 	  //.limit(10)
				 	  .includeDocs(true)
				 	  .build();
			list = request.getResponse().getDocsAs(Book.class);
			for(Book temp : list){
				System.out.println("Book "+temp.getTitle()+" has author "+author);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	
	
	
	@Path("/{id}")
	@GET
	public Book getBook(@PathParam("id") String id){
		initDatabase();
		if(datab.contains(id)){
			Book found = datab.find(Book.class, id);
			System.out.println("Bookid: "+found.getId()+" Title: "+found.getTitle());
			return found;
		}
		/**if(bookmap.containsKey(id)){
			return bookmap.get(id);
		}*/
		else{
			throw new WebApplicationException(Status.NOT_FOUND);
		}
	}
	

	
	//Delete all the books from the library
	@DELETE
	public void deleteAll(){
		//bookmap.clear();
		initDatabase();
	   	try {
			Collection<Book> allBooks = datab.getAllDocsRequestBuilder().includeDocs(true).build()
					.getResponse().getDocsAs(Book.class);
			// Allow one request per second
			RateLimiter throttle = RateLimiter.create(9.0);
			for(Book temp : allBooks){
				if(!(temp.getId()==null)){
					removeBook(temp, throttle);
				}
				//System.out.println("Book "+temp.getId()+" has been deleted.");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void removeBook(Book temp, RateLimiter throttle){
		throttle.acquire();
		datab.remove(temp);
	}
	
	//Delete book with given id from the library
	@Path("/{id}")
	@DELETE
	public void deleteOne(@PathParam("id") String id){
		/**if(bookmap.containsKey(id)){
			bookmap.remove(id);
			System.out.println("Book "+id+" has been deleted.");
		}*/	
		initDatabase();
		if(datab.contains(id)){
			Book todelete = datab.find(Book.class, id);
			datab.remove(todelete);
			System.out.println("Book "+id+" has been deleted.");
		}
		else{
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		
	}
	
	//Updates book in the library using a book object as input, only if already in library
	@Path("/{id}")
	@PUT
	public Book updateBook(@PathParam("id") String id, Book updated){
		initDatabase();
		if(datab.contains(id)){
			if(updated.getId().equals(id)){
				Book indb = datab.find(Book.class, id);
				indb.setTitle(updated.getTitle());
				indb.setAuthor(updated.getAuthor()); //Change back !!
				indb.setISBN(updated.getISBN());
				datab.update(indb);
				return indb;
			}else{
				throw new WebApplicationException(Status.CONFLICT);
			}
		}else{
			throw new WebApplicationException(Status.NOT_FOUND);
		}
	}

	
	//Creates a new book in library
	//@Path("/{id}")
	@POST
	public Response createBook(Book newBook,  @Context UriInfo uriInfo){
		String newID = UUID.randomUUID().toString();
		newBook.setId(newID);
		
		initDatabase();

		datab.save(newBook);
		
		System.out.println("The book "+newBook.getId()+" has been created.");
		URI uri = URI.create(uriInfo.getBaseUri().toString() + "books/"
				+ newBook.getId());
	   // System.out.println(uri.toString());
		return Response.created(
				uri).build();
	}
}