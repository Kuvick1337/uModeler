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

## implemented features

* export as XML/SVG
* import from XML

## Todos

* add S-BPM set  
 -> start in file Sidebar.js
 -> menu entry for S-BPM already added (Sidebar.js:85)
 -> in method "addSbpmPalette" the items are added to the menu entry.
 -> it looks like mxGraph uses XML definitions (assets/stencils/bpmn.xml) and JS-objects to create the UI elements
 -> use "addBpmnPalette" as template for S-BPM elements
* add remote import/export (uLearn)
 -> can be done as soon as details about REST interfae are known
* rework URLs in Init.js from localhost to either a dns name (e.g. umodeler.ce.jku.at)
* rework file menu to desired structure (tbd with Dr. Frysak)
