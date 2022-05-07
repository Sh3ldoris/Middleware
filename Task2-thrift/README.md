# Task 2 - Apache Thrift

This task is about cross-language communication using remote procedure calls (RPC), using Apache Thrift. 
Implement a simple client-server application which will use given a communication protocol. 
The protocol is based on a given language-independent interface description in Thrift IDL. 
Then, extend the protocol while maintaining full run-time compatibility.

### Application
The main idea is that the client logs in to the server, then lets the server perform a search for some items. 
The result of the search is a list of items, which are returned to the client one by one. 
The client generates a summary based on the items and saves it on the server.

### Communication
The client and server should communicate like this:
1. <b>The client logs in</b>
   * Client calls method `logIn` of a `Login` service, with a user name an integer key. 
   * The server checks the validity of the key, and if the key does not match, throws an `InvalidKeyEception`, which will contain the correct key.
   * The client calls `logIn` again, now with the correct key.
2. <b>Searching for items</b>
    * client sets by sending string query to search service to initiate a search, specifying:
      * which types of items to search for
      * limit on maximum number of items to search for
    * The server supports searching for items of type `ItemA, ItemB, ItemC` and fetching them one by one.
    * The client repeatedly calls fetch to fetch the items
      * The returned FetchResult either contains an item from the result, or indicates why no item was returned
      * If the `state` field of `FetchResult` is `ENDED`, then no item was returned, because there are no more items (all were already fetched).
      * If the `state` field of `FetchResult` is `PENDING`, then no item was returned, because the server is still searching for items and does not currently have any items to return.
        * In this case, the client should wait for a bit and try again.
      * If the `state` field of `FetchResult` is `ITEMS`, then the item field contains an item.
4. <b>Creating a summary</b>
   * A "summary" in this case will be a multimap-like collection, represented as a map form a string to a set of strings.
     * The keys in the map will be the names of fields in the item types, such as `"fieldX"`, `"fieldY"`, etc.
     * The sets will contain all values of that field across all the items.
   * The client collects all field values from all the fetched items into the multimap.
     * The values of all the fields should be converted to strings. If the field is a collection, then the values should be comma-separated.
   * The client saves the summary on the server using saveSummary in Reports.
   * The server verifies that the summary is accurate and indicates that by a boolean return value. 

### Implementation
* [__Server__](Cpp-server) - C++ server
* [__Client__](Java-client) - Java client

