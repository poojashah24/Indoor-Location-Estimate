# Indoor-Location-Estimate
Indoor location estimate consists of a crowd-sourced Android application, and a web server application. 
This system allows the collection of sensor values from phones and tablets, and streams the collected readings to the backend server application. This system is meant to be used as a building block in an indoor location estimation environment. 

The system consists of two applications - 
- LocationSensor - Android application that records location and sensor readings.
- LocationReceiverApp - A web application that records readings sent by the client Android app, and persists them to a database.

-----------------------------------------------------------------------------------------------------------------------------
# Installation of the server application

The server application should be deployed on a Tomcat server instance. It also needs connection to a MySQL database for storing the sensor values. The MySQL database configuration settings (url, dbname, username, password) are contained in the file DBConnector.java. Sensor readings will be persisted to this database. The schema can be found in the project report, as well as using the queries in the file QueryConstants.java.

The supporting JAR files required are included in the lib folder. There are 4 required libraries - 
- jackson-core-2.0.0.jar - for xml parsing
- json-20141113.jar - for json handling
- log4j-1.2.17.jar - for logging
- mysql-connector-java-5.1.34-bin.jar - for connecting to the MySQL database.

Each request sent by the Android application hits the servlet LocationUpdateServlet.java. This is the beginning of the workflow to save the sensor and location readings to the database. 

Integration with the National Weather Service is implemented within LocationUtils.java. This module requires a CSV-based listing of weather stations. The StationCSVCreator.java class creates the required CSV file, using as input an XML document containing a listing of all the weather stations (stations.xml). Both files (CSV and XML) are contained within the project.

-----------------------------------------------------------------------------------------------------------------------------
# Using the Android application - 
The Android application consists of a main application, and a home screen widget. The entry point for both is within the file MainActivity.java. 

## File Naming Conventions
Each user interface class ends in Activity, for e.g., PressureActivity is the user interface for the pressure widget. There are 4 background services, one per sensor. Each of the services end in Service, for e.g., PressureService is the background service for the pressure sensor. Threads that send the sensor readings to the web server app end in SenderThread, for e.g., PressureSenderThread sends pressure readings to the server application. Each sensor has an associated datasource to save and retrieve readings from the in-memory SQLite database, for e.g., PressureDataSource saves pressure readings to the in-memory SQLite database.

## Connecting to the server application
The URL of a server application instance needs to be configured within the Android app before sensor readings can be transmitted. This can be done by changing the URL in the Android app settings.

## In-memory SQLite datastore
The in-memory SQLite database stores all the sensor readings, and contains a table for each sensor. The schema for these tables is contained within the class SQLLiteHelper. Any update to the schema needs the db version to be incremented. This will ensure that the schema is upgraded the next time the application is installed.

## Home screen widget
The home screen widget will be displayed permanently in the notifications area. This widget contains button to scroll over a list of frequently used location, and a location can be clicked to transmit its details to the server application. Code for this widget is contained within MainActivity. The classes NextButtonListener, PrevButtonListener, and SelectLocationListener are listeners for the three controls within the widget. The listeners are included in MainActivity as they need access to the context.

-----------------------------------------------------------------------------------------------------------------------------
# Building the projects
AndroidStudio was used to develop the LocationSensor project. Importing the gradle file as an existing project into AndroidStudio is sufficient to create the development environment. The build/clean menus within AndroidStudio can be used to clean and compile the project. Run/Debug menus can be used to install, run and debug the application.

The Eclipse IDE was used to developed the LocationReceivedApp project. Any other IDE can also be used for development purposes. The four dependencies listed under the section "Installation of the server web application" should be added to the project classpath before compilation. The build/clean menus within Eclipse can be used to compile the project, and the generated WAR can be deployed to Tomcat from within Eclipse (using the Servers plugin) or as an external deployment.






