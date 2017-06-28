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

import com.cloudant.client.api.Database;
import com.cloudant.client.api.Search;

import javax.ws.rs.core.Response.Status;



@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResource {
	
	private static Database dbCustomers = null;
	
	private static Database initDatabasec(){
		if(dbCustomers==null){
			try {
				dbCustomers = DBManager.getDB("customers");
			} catch (Exception re) {
				re.printStackTrace();
			}
		}
		return dbCustomers;
	}
	
	
	@GET
	public Collection<Customer> getInformation() throws Exception, IOException {     
		initDatabasec();
    	try {
			Collection<Customer> allCustomers = dbCustomers.getAllDocsRequestBuilder().includeDocs(true).build()
					.getResponse().getDocsAs(Customer.class);
			/**for(Customer temp : allCustomers){
				System.out.println("CustomerId: "+temp.getId()+" Name: "+temp.getName());
			}*/
			return allCustomers;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	@Path("/user/{email}")
	@GET
	public List<Customer> getUser(@PathParam("email") String email){
		initDatabasec();
		List<Customer> list = null;
		Search searchObject = dbCustomers.search("CustomerIdx/emailSearch");
		list = searchObject.includeDocs(true).query(email, Customer.class);
		boolean isContained = false;
		Customer rightOne = null;
		int i = 0;
		while(!list.isEmpty() && i<list.size()){
			if(list.get(i).getEmail().equals(email)){
				isContained = true;
				rightOne = list.get(i);
				i++;
			} else {
				list.remove(i);
			}
		}
		return list;
		
	}
	
	@Path("/{id}")
	@GET
	public Customer getCustomer(@PathParam("id") String id){
		/**if(customers.containsKey(id)){
			return customers.get(id);*/
		dbCustomers = initDatabasec();
		System.out.println("Database for customers is: "+dbCustomers.toString());
		if(dbCustomers.contains(id)){
			Customer found = dbCustomers.find(Customer.class, id);
			System.out.println("Customerid: "+found.getId()+" Name: "+found.getName());
			return found;
		}	
		else{
			throw new WebApplicationException(Status.NOT_FOUND);
		}
	}
	
	@DELETE
	public void deleteAll(){
		initDatabasec();
	   	try {
			Collection<Customer> allCustomers = dbCustomers.getAllDocsRequestBuilder().includeDocs(true).build()
					.getResponse().getDocsAs(Customer.class);
			for(Customer temp : allCustomers){
				dbCustomers.remove(temp);
				System.out.println("Customer "+temp.getId()+" has been deleted.");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Path("/{id}")
	@DELETE
	public void deleteCustomer(@PathParam("id") String id){
		initDatabasec();
		if(dbCustomers.contains(id)){
			Customer todelete = dbCustomers.find(Customer.class, id);
			dbCustomers.remove(todelete);
			System.out.println("Customer "+id+" has been deleted.");
		}	
		else{
			throw new WebApplicationException(Status.NOT_FOUND);
		}
	}
	
	//Creates a new customer
	@POST
	public Response createCustomer(Customer customer,  @Context UriInfo uriInfo){
		String newID = UUID.randomUUID().toString();
		customer.setId(newID);
		initDatabasec();
		dbCustomers.save(customer);
		System.out.println("The customer "+customer.getId()+" has been created.");
		URI uri = URI.create(uriInfo.getBaseUri().toString() + "customers/"
				+ customer.getId());
	    System.out.println(uri.toString());
		return Response.created(
				uri).build();
	}
	
	//Update customer
	@Path("/{id}")
	@PUT
	public Customer updateCustomer(@PathParam("id") String id, Customer customer){
		initDatabasec();
		if(dbCustomers.contains(id)){
			if(customer.getId().equals(id)){
				Customer indb = dbCustomers.find(Customer.class, id);
				indb.setName(customer.getName());
				indb.setEmail(customer.getEmail());
				indb.setPassword(customer.getPassword());
				dbCustomers.update(indb);
				System.out.println("The customer "+id+" has been updated.");
				return indb;
			} else{
				throw new WebApplicationException(Status.CONFLICT);
			}		
		}else{
			throw new WebApplicationException(Status.NOT_FOUND);	
		}
	}
		
	
	
}