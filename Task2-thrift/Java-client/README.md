# Java Client

### Prerequisites
To compile and run the java client application the <b>Thrift</b> library has to be accessible at `$HOME` path.
System environment:
* Linux
* Java 1.8.0_322
* Maven 3.8

### Building the application
Before building the application `mvn clean` is recommended. 

To build application run the command `bash run-cmpl`. After the command runs there should 
be generated classes to communicate with the server. The generated sources should be on path `*/target/generated-sources/thrift/Task2`.

### Running the application
After the application is built successfully to run the application run the command `bash run-client`. 
The script runs the java application with 2 given arguments:
* Application username
* Search query for fetching items

Application is connecting on the server which should be started on `localhost` and port `5001`. To change this, 
modify the `ExampleClient` class
