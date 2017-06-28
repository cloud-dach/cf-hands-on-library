# Library server

This is the first part of the Library Application:
[https://github.com/florae123/LibraryApp-user](https://github.com/florae123/LibraryApp-user).

It is a java server for the Library app and requires a Cloudant NoSQL Database.

![architecture](./images/app-architecture-java.png)

## Prerequisites

The software needed for the following steps:
* Java SE 8 (JDK),
* Eclipse Neon (another option is Maven),
* Cloud Foundry Command Line Interface ([Download here](https://github.com/cloudfoundry/cli/releases)),
* Git tool ([Download here](https://git-scm.com/downloads))

Another requirement is an IBM Bluemix Account.

## Install the Cloud Foundry CLI

If the Cloud Foundry Command Line Interface is not yet installed on your machine, use the following commands to install it (for Ubuntu based Linux distributions):

```
# ...first add the Cloud Foundry Foundation public key and package repository to your system
wget -q -O - https://packages.cloudfoundry.org/debian/cli.cloudfoundry.org.key | sudo apt-key add -
echo "deb http://packages.cloudfoundry.org/debian stable main" | sudo tee /etc/apt/sources.list.d/cloudfoundry-cli.list
# ...then, update your local package index, then finally install the cf CLI
sudo apt-get update
sudo apt-get install cf-cli
```

## Deploy to Bluemix

1. Clone the app to your local environment from your terminal using the following command

      ```
      git clone https://github.com/florae123/library-server-java-user-adjusted.git
      ```

    and change into the newly created directory *library-server-java*.
2.  Execute full Maven build to create the target/name.war file:

      ```
      mvn clean install
      ```

      or use Eclipse to build the Maven project:
      * Launch Eclipse, Click **File > Import**. Then select **General > Existing Projects into Workspace**. Click **Next**.
          ![import](./images/eclipse-import-project.png)
      * Choose **Select root directory** and your downloaded directory *library-server-java*.
      * Right-click your *library-server-java* project folder in Eclipse and select **Run As > Maven install**. The *.war* file will be created in the *target* directory.
          ![maven](./images/eclipse-maven-install.png)

      Change into the directory *library-server-java* in your eclipse workspace directory. (This is where the target directory was created.)

3.  Verify your "Bluemix endpoint api", for example
    * US https://api.ng.bluemix.net or
    * EU-GB https://api.eu-gb.bluemix.net



     ```
     cf api https://api.ng.bluemix.net
     ```

4. Log in to your Bluemix account using the Cloud Foundry CLI tool. Provide your username and password when prompted.
      ```
      cf login
      ```

5. Push the app to Bluemix using the cf cli command
      ```
      cf push library-server -p target/library-server-java.war
      ```

      Instead of *library-server*, use your own unique name for the app.

      As soon as the application is running, test it by clicking on the url. (See [Test your running application](#test-your-running-application)).
6. Create an instance of the Cloudant NoSQL DB Service on Bluemix and connect it to your app.
See [Configure Databases](#configure-databases). When you are done, test the connection to the service. [Test your running application](#test-your-running-application).



## Configure Databases

Create an instance of a cloudant database on Bluemix, in the same space you were using for the application.
      ![catalog](./images/catalog-cloudant.png)
* Select **Cloudant NoSQL DB** in the Bluemix Catalog in your Browser, make sure the *Lite* pricing plan is selected and click **Create**. You will be directed to a view of the service.
* To bind this DB to the java application, open the **Connections** panel, and click **Create Connection**. Then select the java library server and click **Connect**. Restage the application when prompted.

    ![connect](./images/connect-cloudant.png)

    You do not need to integrate the service credentials of the Cloudant Service instance into your code to access your databases. By connecting the service to the cloud foundry application, the app's environment variables, the **VCAP_SERVICES**, will be updated. These VCAP_SERVICES are used in the java code to retrieve the service instance's credentials.

    ![VCAP_VARIABLES](./images/vcap.png)

* Go back to the **Manage** panel and click **Launch**.
    ![launch](./images/cloudant-launch.png)

The App requires three databases in your Cloudant service.

1. Create a database called **"books"**.

    ![books](./images/create-books-db.png)

    A book will be saved as a JSON in the following format:

      ```
    	{
    	  "_id": "...",
    	  "_rev": "...",
    	  "id": "id must be the same as _id",
    	  "isbn": "the book's isbn",
    	  "authors": [
    	    "the book's author"
    	  ],
    	  "title": "bookTitle",
    	  "tags": [
    	    "Keywords", "that match", "the book"
    	  ],
    	  "picture": "the book's cover",
    	  "about_the_book": "a description to be read by Text to Speech"
    	}
      ```

    There are some sample documents in this format in the *sample-books* folder in this directory. To include any of these books in your database:
    * Click on the database **books** and then click the **+** sign next to **All Documents**.
    * Select **New Doc**.
    * Delete the default *{ "id_": "..." }* and copy the text in one of the json files into the document.
    * Click **Create Document**.

    There is no **"_rev"** attribute included in the sample json data.
    This is because the *"_rev"* attribute has to be excluded from new data that is added to a database. The Cloudant service will automatically generate the attribute-value pair *"_rev"*.

    In order for the app to be able to properly search this database, create a new search index and two new views in "books".
    To do this:
    * Click the **+** sign next to **All Documents**
    * Select **New Doc**, delete the default *{ "id_": "..." }*
    * Copy the text from the file **cloudant-design-docs/cloudant-design-doc-books.json** into the document.
    * Click **Create Document**.

    **Alternatively, create the design document manually:** Click the **"+"** sign next to "Design Documents" on the "books" database view in your Cloudant dashboard.

    ![NewSearchIndex](./images/new-search-index.png)

    Click **"New Search Index"** and configure the search index like this:

    * **Design Document Name:**  BookIdx
    * **Index Name:**  titleSearch
    * **Code for the search index:**
        ```
        function (doc) {
          index("default", doc.title);
        }
        ```

    ![SearchIndex](./images/search-index.png)

    Then, click the **"+"** sign next to "BookIdx", then click **"New View"**.

    ![new-view](./images/new-view.png)

    Configure the view like this:

    * **Design Document Name:**  BookIdx
    * **View Index Name:**  tagView
    * **Code for the view:**
      ```
      function (doc) {
        var i;
        for(i in doc.tags)
           emit(doc.tags[i], doc._id);
      }
      ```

    ![View](./images/tag-view.png)

    Create another view an configure it like this:

    * **Design Document Name:**  BookIdx
    * **View Index Name:**  authorsView
    * **Code for the view:**
      ```
      function (doc) {
        var i;
        for(i in doc.authors)
           emit(doc.authors[i], doc._id);
      }
      ```

2. Create a database called **"customers"**. A customer will be saved as a JSON in the following format:

      ```
    	{
    	  "_id": "the customer's id",
        "_rev": "...",
    	  "name": "the customer's name",
    	  "email": "the customer's email address",
    	  "password": "the customer's password",
    	  "id": "must be the same as _id"
    	}
      ```
      Add a search index like this:
      * Click the **+** sign next to **All Documents**
      * Select **New Doc**, delete the default *{ "id_": "..." }*
      * Copy the text from the file **cloudant-design-docs/cloudant-design-doc-customers.json** into the document.
      * Click **Create Document**.

3. Create a database called **"rentals"**. Whenever a book is borrowed, it is registered in "rentals". A rental will be saved as a JSON in the following format:

      ```
      {
        "_id": "the rental's id",
        "_rev": "...",
        "id": "must be the same as _id",
        "bookid": "the id of the book borrowed",
        "customerid": "the id of the customer borrowing the book",
        "start": "Mar 3, 2017 12:00:00 AM",
        "end": "May 4, 2017 12:00:00 AM"
      }
      ```
      Add a search index like this:
      * Click the **+** sign next to **All Documents**
      * Select **New Doc**, delete the default *{ "id_": "..." }*
      * Copy the text from the file **cloudant-design-docs/cloudant-design-doc-rentals.json** into the document.
      * Click **Create Document**.

## Test your running application

* Open the server application you just deployed to Bluemix, e.g. running on    **https://libraryserver-myname.eu-gb.mybluemix.net**.

    The text **"... Library Backend Server is running ..."** should be displayed in your browser.

    ![test1](./images/test-app.png)

* To check the connection to the Cloudant Database, add **/api/books** to the url (e.g. **https://libraryserver-myname.eu-gb.mybluemix.net/api/books**). You should see the books that were added to your *books* database as a json array.

    ![test2](./images/test-app-cloudant.png)


## Build a toolchain
The following steps are only available to applications deployed in the US region.

1. Click on *Overview* for the library server app on Bluemix in your browser. Under **Continuous delivery**, click **Enable**.

    ![enable](./images/enable-con-delivery.png)

2. Name the toolchain and choose a new github repository to connect it to. Click **Create**.

    ![create](./images/create-toolchain.png)

3. Click the **Continuous delivery** icon.

    ![](./images/delivery.png)

4. For the first stage, the **Build Stage**, click **Configure Stage**. In the **JOBS** panel, change the **Builder Type** to **Maven**, the **Build Shell Command** to
    ```
    #!/bin/bash
    mvn clean install
    ```

    and click **save**.

    ![build-stage](./images/build-stage.png)

5. For the next stage, the **Deploy Stage**, also click **Configure Stage**. Change the **Deploy Script** to

    ```
    #!/bin/bash
    cf push "${CF_APP}" -p library-server-java.war

    # View logs
    # cf logs "${CF_APP}" --recent
    ```  
    and click **save**.

    ![deploy-stage](./images/deploy-stage.png)

6. Push the code into the new github repository and watch the automated deployment.
