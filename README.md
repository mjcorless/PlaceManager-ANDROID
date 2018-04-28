# SER-423-Mobile-Systems
Android portion of SER 423 due 04/14/2018

Below is the assignment:
1. >The goal is to develop a multi-View Android app, which allows the user to manipulate (browse, view details, add, delete, and modify) geographic place descriptions. The app should use a SQLite database as its underlying model. The database should have an initial set of places which are a part of the app bundle (installation file). The app should also allow the user to select two of the registered places and to view the great circle distance and initial heading between the two selected places.
2. >Extend the app you developed and submitted in Week 5 (Exercises 1, 3, and 7) to include the features and capabilities explained in the points below. Note that the instructor does not distribute his solutions to problems and that you must begin your development of this assignment with your own Android App submission from Week5. 
>* The ability to synchronize with a JsonRPC server. The app should provide the capability to initialize and update its internal database by synchronizing with the instructor provided place server which you can download and run on your machine using the link on the page: http://pooh.poly.asu.edu/Mobile/Assigns/Assign5/assign5.html  You do NOT have to adhere to the specifications and criteria on that assignment page. Instead, provide the user with explicit capability (a Button or otherwise) to sync with the JsonRPC server. Additionally, be sure that any place description additions, removals, or updates that are performed by the user are pushed to the JsonRPC server so that the server remains consistent with your local database changes.
>* Add to your Android app a settings a capability that allows the user to specify the URL for the connecting to the JsonRPC server. You should provide initial values for this url that connect to the machine running the emulator (IP address 10.0.2.2) and assume a default port of 8080. When the user changes the URL setting, all further connections to the JsonRPC server for synchronization should occur to the new settings value. See Unit 11 for examples and other materials on Android settings menu.
>* Add to your Android app a Mapping view and controller (activity) which maps a place description. This Map view should appropriately set the geo position, zoom and tilt values. It should place a pin/locator on the map representing the place description. Your map view should also allow the user to specify a new place description by selecting a place on the map.