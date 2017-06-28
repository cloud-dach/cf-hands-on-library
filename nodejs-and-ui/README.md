# LibraryUI

This is the second part of the Library Application: [https://github.com/florae123/LibraryApp-user](https://github.com/florae123/LibraryApp-user).

This part consists of a Node.js server and the web UI.
It is built using HTML, CSS, Bootstrap, jQuery, and a Watson Text-To-Speech Service, a Watson Conversation Service as well as the App ID Service on Bluemix.

This first part is a backend server running on Java Liberty on Bluemix that connects to a Cloudant NoSQL Database.

  ![Architecture](./images/architecture-node.png)

## Deploy to Bluemix

1. Deploy the Java back end to Bluemix [https://github.com/florae123/library-server-java-user-adjusted](https://github.com/florae123/library-server-java-user-adjusted) and bind it to a Cloudant NoSQL Database.

2. Clone the app to your local environment from your terminal using the following command

    ```
    git clone https://github.com/florae123/libraryui-user-adjusted
    ```

3. You can find the URL of your java library server under **All Apps** on the Bluemix dashboard for the region you used.

    ![dashboard-click](./images/dashboard.png)

    Copy the URL.

    ![url](./images/java-url.png)

    Open the file **manifest.yml** and change the **"LIBRARY_URI"** to the URL of your java library server and add **"/api"** at the end: **https://[YOUR_LIBRARY_SERVER_URL]/api**

    Change the host name *LibraryUI-[myName]* to something unique. For example, you can replace [myName] with your name.

    ```
    applications:
    - name: LibraryUI
      host: libraryui-tsuedbro
      env:
        LIBRARY_URI: "https://library-server-tsuedbro.mybluemix.net/api"
      memory: 64M
      instances: 1
    ```

4. Log in to your Bluemix account using the Cloud Foundry CLI tool.

	```
	cf login
	```

5.  * Create a Node.JS Cloud Foundry App on Bluemix.
      Give it the same name (**LibraryUI**) and host name as defined in the **manifest.yml** file.

      ![](./images/nodejsapp.png)

    * Create a toolchain for this Cloud Foundry app:
      In the **Overview** section of the app, click **Enable** under **Continuous Delivery**.

      ![](./images/createtoolchain.png)

      Choose **repository type: new** to create a new git repository for your app.

      ![](./images/gitrepo.png)

      Click on the **Git** icon in your toolchain. You will be redirected to the GitLab repository.

      ![](./images/toolchaingit.png)

6. Create a Watson Text-To-Speech Service and connect it to the app LibraryUI.

7. Create a Watson Conversation Service and bind it to the app. Launch, and import a workspace using the file **conversation-workspace-user-adjusted.json**. Connect it to the app.

    * Select **Conversation** from the Bluemix Catalog in your Browser, make sure the *Free* pricing plan is selected and click **Create**. You will be directed to a view of the service.
    * To bind this service instance to the node.js application, open the **Connections** panel, and click **Create Connection**. Then select the LibraryUI applicaton and click **Connect**. Restage the application when prompted.
    * Open the **Manage** panel and click **Launch tool**.

        ![Launch](./images/launch-conv.png)

    * Under **Create workspace**, click **Import**.
    * Choose the file **conversation-workspace-user-adjusted.json** from your local copy of the LibraryUI directory, select **Everything (Intents, Entities, and Dialog)**, and click **Import**.

        ![import](./images/import-workspace-2.png)

    * Go **back to workspaces** and click **View Details** on the Libray Helper workspace.

        ![details](./images/workspace-id.png)

    * Copy the **Workspace ID** to the clipboard.
    * Replace it with the current value for *workspace_id_copy* in line 126 in **server.js**.

        ```
        //authenticate conversation service
        var workspace_id_copy = 'YOUR_WORKSPACE_ID';
        ```
8. Create an instance of the App ID Service on Bluemix:

    * Select the App ID Service from the Catalog.
    * Name your service instance and click **Create**.
    * You can keep the default configurations under *Identity Providers*, *Login Customization* and *Profiles*. Or you can adjust them as you choose, for example by uploading the image **views/images/bookshelf.jpg** in the login cumstomization.
    * Connect it to the app LibraryUI.

9. Push the application code to your git repository. *<your-url>* should be replaced by the url of the GitLab repository.

    ```
    git remote set-url origin <your-url>
    git add *
    git commit -m “first commit”
    git push origin master
    ```
    You may need to add an **SSH key** to your GitLab account.
    To locate an existing SSH key pair:
    ```
    cat ~/.ssh/id_rsa.pub
    ```
    To generate a new SSH key pair:
    ```
    ssh-keygen -t rsa -C "your.email@example.com" -b 4096
    cat ~/.ssh/id_rsa.pub
    ```
    Copy the entire key starting with ssh-rsa.
    Then add your public SSH key to GitLab. Navigate to the 'SSH Keys' tab in your 'Profile Settings'.
    Paste your key in the 'Key' section and give it a relevant 'Title'.
