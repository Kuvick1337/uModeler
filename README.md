# uModeler
**_BPMN/S-BPM modelling tool for the JKU Communication Engineering institute_**

## local setup

* checkout repository
* open with IntelliJ IDEA (preferrably Ultimate Edition)
* update the Gradle project (syncs and installs all needed libraries)
* optional: set correct Java version (if the installed differs from project setup)
* start run configuration "Application" to start the spring server locally
* if the connection to uLearn fails due to SSL handshake errors, perform the following steps:
1) download the Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files (https://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html)
2) copy the JAR-Files to $JAVA_HOME/jre/lib/security
3) restart server

## run locally

* start run configuration "Application"
* jar is build and started on port 8080
* go to http://localhost:8080/ 

## build a JAR file

* select and run Gradle task _Umodeler [clean]_ (safety first)
* select and run Gradle task _Umodeler [build]_
* the compiled JAR file is in the directory _{project dir}/build/libs_

## implemented features

* export as XML/SVG
* import from XML
* rework file menu to desired structure
* save to uLearn


## Todos

* add remote import from uLearn
* rework URLs in Init.js from localhost to a DNS name (e.g. umodeler.ce.jku.at)
* rework URLS in _ULearnController.java_ to link to the production instance
* currently all submissions are listed, even the ones already closed -> error when submitting there
* add a favicon
* encrypt clients uLearn password (currently transferred only URL-encoded -> basically plain text)
* there seems to be an issue with the onError function of the mxXMLRequest, thus errors on save are not displayed
    - if the login fails, no error message is displayed
    - if the save to Ulearn fails  (e.g. submission timeframe already over), nothing is displayed

# Documentation

## Frontend 

### dialogues for saving/loading to uLearn

The modals are built using JS code in the file `Dialog.js`. This file contains objects for all dialogues used in the frontend. 
The URLs called by these dialogues can be found in the file `Init.js`.  
For the upload/download to uLearn the follwing dialogues are needed:

* `UlearnLoginSaveDialog`: opens a login modal, which fetches the users Bearer token and opens the submission selection modal
* `UlearnSaveDataDialog`: this modal allows the user to enter a filename and choose a workspace/submission for the file upload
* `UlearnLoadDialog`: **NOT YET IMPLEMENTED** - this modal should allow the user to choose a file and load it from uLearn into the editor

### menu structure

The _File_ menu structure has been modified to be more intuitive. 
If changes are to be made the menus itself are built in file `Menu.js`, where the menu actions (see file `Actions.js`) are added. 
The actions can either be edited directly or copied and then customized.

### modelling stuff

* **TODO Carina**

## Backend

### Introduction

The backend is a Java REST-Server powered by [Spring](https://spring.io/). The project is built using the build automation tool Gradle. The most important files are:
*  `build.gradle`: contains the whole project configuration including neccessary libraries and build options.
* `Application.java`: main entry point to start the Spring server at startup
* `ModelerConfiguration`: Spring configuration file which allows the frontend assets to be requested and delivered

### REST-Controllers

* `IndexController`: delivers the modeler application. Thymeleaf is used to simply refer to the _index.html_ file.
* `ExportController`: converts the model sent by the frontend to a SVG file and sends it back to the client to download.
* `SaveController`: converts the model sent by the frontend to a pure XML file and sends it back to the client to download.
* `UlearnController`: this controller offers REST endpoints for 1) logging into uLearn, 2) fetching submissionGroups and submissionSpecifications 
for a workspace and uploading the current model graph to a selected submissionSpecification
