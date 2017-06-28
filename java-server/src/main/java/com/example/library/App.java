
/**
 *  Copyright 2014 Michel Jaczynski
 *  
 *  Visit my blog for more explanation and tips: 
 *  http://www.mycloudtips.com/
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.example.library;



import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;



@ApplicationPath("/api")
public class App extends Application {

	
	public App() {
		// TODO Auto-generated constructor stub
		
	}
	
	
	
	@Override
	public Set<Object> getSingletons() {
		Set<Object> singletons = new HashSet<Object>();
		// this is the application resource, declared as a singleton
		singletons.add(new DBManager());
		singletons.add(new BookResource());
		singletons.add(new CustomerResource());
		singletons.add(new RentalResource());
		return singletons;
	}
}
