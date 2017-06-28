package com.example.library;

import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
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

import org.json.JSONObject;

import com.cloudant.client.api.Database;
import com.cloudant.client.api.Search;

import javax.ws.rs.core.Response.Status;



@Path("/rentals")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RentalResource {
	
	private static Database dbRentals = DBManager.getDB("rentals");
	
	private static SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
	
	@GET
	public RentalShow[] getInformation(@Context UriInfo uriInfo) throws Exception, IOException {   
    	try {
			Collection<Rental> allRentals = dbRentals.getAllDocsRequestBuilder().includeDocs(true).build()
					.getResponse().getDocsAs(Rental.class);
			
			LinkedList<Rental> removeable = new LinkedList<Rental>();
			for(Rental temp : allRentals){
				if(temp.getId()==null){
					removeable.add(temp);
				}
			}
			allRentals.removeAll(removeable);
			
			RentalShow[] toShow = new RentalShow[allRentals.size()];
			Iterator<Rental> it = allRentals.iterator();
			int i=0;
			while(it.hasNext()){
				Rental current = it.next();
				
			    URI uribook = URI.create(uriInfo.getBaseUri().toString() + "books/"
						+ current.getBookid());
			    //System.out.println("uri book: "+uribook.toString());
			    URI uricustomer = URI.create(uriInfo.getBaseUri().toString() + "customers/"
						+ current.getCustomerid());
				//System.out.println("uri customer: "+uricustomer.toString());
				String startFormat = dt.format(current.getStart());
				String endFormat = dt.format(current.getEnd());
				RentalShow temp = new RentalShow(current.getId(),current.getBookid(),uricustomer.toString(),startFormat,endFormat);
				toShow[i]=temp;
				i++;
			}
			return toShow;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	@Path("/user/{customerid}")
	@GET
	public RentalShow[] getUserRentals(@PathParam("customerid") String customerid, @Context UriInfo uriInfo){
		Collection<Rental> userRentals = null;
		Search searchObject = dbRentals.search("RentalIdx/customerSearch");
		userRentals = searchObject.includeDocs(true).query(customerid, Rental.class);
		
		
		RentalShow[] toShow = new RentalShow[userRentals.size()];
		Iterator<Rental> it = userRentals.iterator();
		int i=0;
		while(it.hasNext()){
			Rental current = it.next();
			
		    URI uribook = URI.create(uriInfo.getBaseUri().toString() + "books/"
					+ current.getBookid());
		    //System.out.println("uri book: "+uribook.toString());
		    URI uricustomer = URI.create(uriInfo.getBaseUri().toString() + "customers/"
					+ current.getCustomerid());
			//System.out.println("uri customer: "+uricustomer.toString());
			String startFormat = dt.format(current.getStart());
			String endFormat = dt.format(current.getEnd());
			RentalShow temp = new RentalShow(current.getId(),current.getBookid(),uricustomer.toString(),startFormat,endFormat);
			toShow[i]=temp;
			i++;
		}
		return toShow;
	}
	
	
	@Path("/{id}")
	@GET
	public RentalShow showRental(@PathParam("id") String id, @Context UriInfo uriInfo){
		if(dbRentals.contains(id)){	
			Rental myrental= dbRentals.find(Rental.class, id);
		    URI uribook = URI.create(uriInfo.getBaseUri().toString() + "books/"
					+ myrental.getBookid());
			URI uricustomer = URI.create(uriInfo.getBaseUri().toString() + "customers/"
					+ myrental.getCustomerid());  
			String startFormat = dt.format(myrental.getStart());
			String endFormat = dt.format(myrental.getEnd());
		    RentalShow forshow = new RentalShow(id,myrental.getBookid(),uricustomer.toString(),startFormat,endFormat);
			return forshow;
		}	
		else{
			throw new WebApplicationException(Status.NOT_FOUND);
		}
	}
	
	
	//Creates a new rental
	@POST
	public Response createRental(Rental rental,  @Context UriInfo uriInfo){
		//if bookid and customerid exist
		Database bookdb = DBManager.getDB("books");
		Database customerdb = DBManager.getDB("customers");
		if(bookdb.contains(rental.getBookid()) && customerdb.contains(rental.getCustomerid())){
			String newID = UUID.randomUUID().toString();
			rental.setId(newID);
			dbRentals.save(rental);
			System.out.println("The rental "+rental.getId()+" has been created.");
			
			URI uri = URI.create(uriInfo.getBaseUri().toString() + "rentals/"
					+ rental.getId());
		    //System.out.println(uri.toString());
			return Response.created(uri).build();
		}else{
			throw new WebApplicationException(Status.NOT_FOUND);
		}
	}
	
	
	
	//Update rental
	@Path("/{id}")
	@PUT
	public Rental updateRental(@PathParam("id") String id, Rental rental,  @Context UriInfo uriInfo){
		if(dbRentals.contains(id)){
			if(rental.getId().equals(id)){
				Rental indb = dbRentals.find(Rental.class, id);
				//indb.setBookid(rental.getBookid());
				indb.setCustomerid(rental.getCustomerid());
				indb.setStart(rental.getStart());
				indb.setEnd(rental.getEnd());
				dbRentals.update(indb);
				System.out.println("The rental "+id+" has been updated.");
				return indb;
			} else{
				throw new WebApplicationException(Status.CONFLICT);
			}		
		}else{
			throw new WebApplicationException(Status.NOT_FOUND);	
		}
		
	}

	@Path("/user/{customerid}")
	@DELETE
	public void deleteUserAll(@PathParam("customerid") String customerid){
		Collection<Rental> userRentals = null;
		Search searchObject = dbRentals.search("RentalIdx/customerSearch");
		userRentals = searchObject.includeDocs(true).query(customerid, Rental.class);
		for(Rental temp : userRentals){
			dbRentals.remove(temp);
			System.out.println("Rental "+temp.getId()+" has been deleted.");
		}
	}
	
	
	@DELETE
	public void deleteAll(){
	   	try {
			Collection<Rental> allRentals = dbRentals.getAllDocsRequestBuilder().includeDocs(true).build()
					.getResponse().getDocsAs(Rental.class);
			for(Rental temp : allRentals){
				dbRentals.remove(temp);
				System.out.println("Rental "+temp.getId()+" has been deleted.");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Path("/{id}")
	@DELETE
	public void deleteRental(@PathParam("id") String id){
		if(dbRentals.contains(id)){
			Rental todelete = dbRentals.find(Rental.class, id);
			dbRentals.remove(todelete);
			System.out.println("Rental "+id+" has been deleted.");
		}	
		else{
			throw new WebApplicationException(Status.NOT_FOUND);
		}
	}
	
	
}
	