# Library application

This is a sample application running on IBM Bluemix. It allows you to store, delete and update books and customers, and register borrowed books.

It includes the Watson Conversation Service as well as the TextToSpeech Service.

To run the app, please create an instance of a Cloudant NoSQL Database, a Conversation Service, a TextToSpeech Service and an App ID service.

**Watch the video on YouTube for further information about this application. _Note:_ That the video refers to an older version of the demo app  [https://www.youtube.com/watch?v=NqF2wIMBqBw](https://www.youtube.com/watch?v=NqF2wIMBqBw).**

**Find the Java backend in this repository: [https://github.com/cloud-dach/cf-hands-on-LibraryServer](https://github.com/cloud-dach/cf-hands-on-LibraryServer).**

**Find the Node.js server and the web interface here: [https://github.com/cloud-dach/cf-hands-on-LibraryUI](https://github.com/cloud-dach/cf-hands-on-LibraryUI).**

These repositories will also provide you with step-by-step instructions on how to deploy the library app to Bluemix. There are two options to deploy the application to Bluemix:
  * You can deploy both cloud foundry apps, first the java app and then the node.js app.
  * You can also choose to deploy only the node.js app and connect it to an existing, running back end server with a connected database. In that case, your starting point is the *Libraryui-v2* repository.

**Watch the video on YouTube for further information about this application. Note that the video refers to an older version of the demo app [https://github.com/florae123/LibraryApp](https://github.com/florae123/LibraryApp): [https://www.youtube.com/watch?v=NqF2wIMBqBw](https://www.youtube.com/watch?v=NqF2wIMBqBw).**

![](./images/WebInterface.png)

## Architecture

A microservice-based architecture: The node.js server interacts with the Watson services and with the java server. It is secured by the App ID service, so the user is required to login with a facebook or google account. The database is accessed from the java server.

![](./images/app-architecture.png)
