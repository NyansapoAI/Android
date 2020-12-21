# nyansapo-api
This folder will hold the official code for Nyansapo API development 

# Database
The models for the mongodb database is saved in the models folder

# API
All api route codes will be saved in the api folder

# index.js
The server.js file will intialize the express server and listen for request

# app.js
The app.js file will receive request and route them to appropriate routes in the api folder

# Install Dependances
1. Nodemon: this is a developer dependancy that enables you to easily edit code and quickly run for testing
Command: npm install --save-dev nodemon

2. findit 
Command: npm install --save findit

3. fs
Command: npm install --save fs

4. Jsonwebtoken: this is used to generate security tokens for the API
Command: npm install --save jsonwebtoken

5. mongose: this package is used to connect to the MongoDB database
Command: npm install -save mongose

6. Morgan: this package handles logging for development
Command: npm install --save morgan

7. Mutter: This package handles file upload to the API
Command: npm install --save multer

8. Password hash: This package is for hashing of user passwords
Command: npm install --save password-hash

In case you are prompted to install any package use this format to install the package;

npm install --save package_name

The command to run the app is npm start

Email me at eai6@psu.edu if you encounter any issues
