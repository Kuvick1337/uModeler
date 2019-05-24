# uModeler
BPMN/S-BPM modelling tool for the Communication Engineering institute

## local setup

* checkout repository
* open with IntelliJ IDEA (preferrably Ultimate Edition)
* update the Gradle project (syncs and installs all needed libraries)
* optional: set correct Java version (if the installed differs from project setup)

## run locally

* start run configuration "Application"
* jar is build and started on port 8080
* go to http://localhost:8080/ 

## Todos

* add S-BPM set  
 -> start in file Sidebar.js
 -> menu entry for S-BPM already added (Sidebar.js:85)
 -> in method "addSbpmPalette" the items are added to the menu entry.
 -> it looks like mxGraph uses XML definitions (assets/stencils/bpmn.xml) and JS-objects to create the UI elements
 -> use "addBpmnPalette" as template for S-BPM elements
* add remote import/export (uLearn)
* add local import/export  
 -> https://jgraph.github.io/mxgraph/docs/js-api/files/editor/mxEditor-js.html
 -> maybe an example: https://github.com/jgraph/mxgraph/blob/master/java/examples/com/mxgraph/examples/web/resources/export.html
 -> in Dialog.js werden die Dialoge zusammengebaut (innere Klasse ExportDialog)
 -> import via OpenDialogue should work, but doesn't ??
* add export as pictures
 -> XML needs to be converted in binary data (picture, e.g. png) 
 -> see Java backend classes mxCodec, mxGraphModel, mxCellRenderer and mxGraph (!!!)
* add Java Backend as module to the project (better separation than as subfolder)