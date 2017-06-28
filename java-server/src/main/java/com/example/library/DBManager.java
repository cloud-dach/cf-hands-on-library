package com.example.library;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
//import com.cloudant.client.org.lightcouch.CouchDbException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.cloudant.*;


public class DBManager {
	
	private static CloudantClient cloudant = null;
	private static Database db = null;
	
	private static final String FILENAME = "C:\\dev\\src\\demo\\library-server-java\\vcap-env.json";
	
	private static String user;
	private static String password;
	

	public static void getCred(){
		String VCAP_SERVICES = System.getenv("VCAP_SERVICES");
		JSONObject vcap = new JSONObject();
		if (VCAP_SERVICES != null) {
			vcap = new JSONObject(VCAP_SERVICES);
			JSONArray cloudant = vcap.getJSONArray("cloudantNoSQLDB");
			JSONObject cred = cloudant.getJSONObject(0).getJSONObject("credentials");
			user = cred.getString("username");
			password = cred.getString("password");
		} else {
			try {
				String content = new String(Files.readAllBytes(Paths.get(FILENAME)));
				System.out.println(content);
				vcap = new JSONObject(content);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		JSONArray cloudant = vcap.getJSONArray("cloudantNoSQLDB");
		JSONObject cred = cloudant.getJSONObject(0).getJSONObject("credentials");
		user = cred.getString("username");
		password = cred.getString("password");
	}
	
	
	
	public static CloudantClient createClient(){
		getCred();
		try {
			System.out.println("Connecting to Cloudant : " + user);
			CloudantClient client = ClientBuilder.account(user)
					.username(user)
					.password(password)
					.build();
			return client;
		//} catch (CouchDbException e) {
		} catch (Exception e){
			throw new RuntimeException("Unable to connect to repository", e);
		}
	}
	
	public static void initClient() {
		if (cloudant == null) {
			cloudant = createClient();
			return;
		}else{
			return;
		}
	}
	
	public static Database getDB(String databaseName) {
		if (cloudant == null) {
			initClient();
		}

		try {
			db = cloudant.database(databaseName, false);
		} catch (Exception e) {
			throw new RuntimeException("DB Not found", e);
		}
		
		return db;
	}

}


