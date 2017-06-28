# Library application

This is a sample application running on IBM Bluemix. It allows you to store, delete and update books and customers and register borrowed books.

It includes the Watson Conversation Service as well as the TextToSpeech Service.

To run the app, please create an instance of a Cloudant NoSQL Database, a Conversation Service, a TextToSpeech Service and an App ID service.

**View a running sample of the app [here](https://libraryui-demo-1.mybluemix.net/).**

**Find the Java backend in this repository: [https://github.com/florae123/library-server-java-user-adjusted](https://github.com/florae123/library-server-java-user-adjusted).**

**Find the Node.js server and the web interface here: [https://github.com/florae123/Libraryui-user-adjusted](https://github.com/florae123/Libraryui-user-adjusted).**

You will also be provided with step-by-step instructions on how to deploy the library app to Bluemix.

**Watch the video on YouTube for further information about this application. Note that the video refers to an older version of the demo app [https://github.com/florae123/LibraryApp](https://github.com/florae123/LibraryApp): [https://www.youtube.com/watch?v=NqF2wIMBqBw](https://www.youtube.com/watch?v=NqF2wIMBqBw).**

![](./images/WebInterface.png)

## Architecture

A microservice-based architecture: The node.js server interacts with the Watson services and with the java server. It is secured by the App ID service, so the user is required to login with a facebook or google account. The database is accessed from the java server.

![](./images/app-architecture.png)
