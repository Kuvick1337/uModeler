# uModeler
BPMN/S-BPM modelling tool for the Communication Engineering institute

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

## implemented features

* export as XML/SVG
* import from XML
* rework file menu to desired structure
* save to uLearn


## Todos

* add remote import from uLearn (can be done as soon as details about REST interfae are known)
* rework URLs in Init.js from localhost to a DNS name (e.g. umodeler.ce.jku.at)
* after save to uLearn the onError dialogue appears. What must the response look like to make it ok?

# Documentation

## Frontend 

### dialogues for saving/loading to uLearn

The modals are built using JS code in the file _Dialog.js_. This file contains objects for all dialogues used in the frontend. The _UlearnSaveDialog_ and _UlearnLoadDialog_ are custom extensions for the uLearn save/load functionality. The URLs called by these dialogues can be found in the file _Init.js_.

### menu structure

The _File_ menu structure has been modified to be more intuitive. If changes are to be made the menus itself are built in file _Menu.js_, where the menu actions (see file _Actions.js_) are added. The actions can either be edited directly or copied and then customized.

## Backend

### Introduction

The backend is a Java REST-Server powered by [Spring](https://spring.io/). The project is built using the build automation tool Gradle. The most important files are:
*  _build.gradle_: contains the whole project configuration including neccessary libraries and build options.
* _Application.java_: main entry point to start the Spring server at startup
* _ModelerConfiguration_: Spring configuration file which allows the frontend assets to be requested and delivered

### REST-Controllers

* _IndexController_: delivers the modeler application. Thymeleaf is used to simply refer to the _index.html_ file.
* _ExportController_: converts the model sent by the frontend to a SVG file and sends it back to the client to download.
* _SaveController_: converts the model sent by the frontend to a pure XML file and sends it back to the client to download.
* _uLearnController_: handels loading and storing to uLearn  TODO 
