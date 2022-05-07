# C++ Server

### Prerequisites
To compile and run the C++ server application the <b>Thrift</b> library has to be accessible at `$HOME` path.
System environment:
* Linux
* GNU compiler 11.3.0
* `thrift` set in the `$PATH` (`export PATH=$PATH:$HOME/thrift/bin/`) and in the `$LD_LIBRARY_PATH` (`export LD_LIBRARY_PATH=$HOME/thrift/lib/`)
### Building the application
Before building the application `make clean` is recommended. 

To build application run the command `make`. After the command runs there should 
be generated classes to communicate with the client. The generated sources should be on path `*/gen-cpp`.

### Running the application
After the application is built successfully to run the application run the command `./server`.
