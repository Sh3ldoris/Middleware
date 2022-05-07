# Task 3 - JMS

The assignment describes a message-based communication protocol that implements a simple trading system.
Each running instance communicates with other running instances and also with the bank.

### Application
The chosen messaging standard is JMS (Jakarta Message Service), using the ActiveMQ Artemis 2.19.1.

### Functionality
The application provides following functionality:
1. After its startup, create random unique names of goods, that the user (seller) provides, 
together with random price of each item. This list should be sent as a message via the 
Offers topic to the other running instances.
2. Present to the users (in any trivial form) a list of goods and prices, offered by the other running instances.
This list should be updated, whenever a new message from the Offers topic arrives, and any items no longer
offered by the sending instance should be removed from the list.
3. Allow the user to choose (again, using any trivial method) goods to buy from another instance.
The sale is realized as follows:
   * The buyer sends a message to the seller, containing the name of the requested item and the buyer's account number.
   * The seller sends a message to the buyer, containing the seller's account number, or indicating a refusal to sell (the item was already sold etc.).
   * The buyer sends a message to the Bank, requesting transfer of appropriate amount of money from the buyer's account to the seller's account.
   * The bank sends a message to the seller, notifying the seller of the money transfer from the buyer's account.
   * The seller removes the item from its list and sends a message to the buyer, confirming the finished sale.
   
    Note: The buyer does not have to offer the item bought for sale.
4. Keep proper account balances. For simplicity, newly created accounts can have fixed balance.
5. Support for account balance queries. Add a new user command to the client, to show the current balance.
6. Consider the buyer's account balance. Refuse transfer if sender has not enough money.
7. Considering that the buyer may transfer less money than the actual price of the required item (assume the price is fixed).
8. After a new client connects, it should see offers of all other clients without any user action on those clients.
### Messaging
Two different `Session` instances:
* The first for asynchronous message handling
* The second for synchronous (user-triggered) messages and waiting for their replies

For transferring messages were user `ObjectMessage` classes and as a content were used DTO class implementations.

## Running the application
To compile and run the application the ActiveMQ Artemis 2.19.1 library 
(https://activemq.apache.org/components/artemis/download/) has to be accessible at `$HOME` path
### Prerequisites
* Linux
* Java 1.8.0_322
* Maven 3.8

### ActiveMQ Artemis
To configure the message broker run the command `bash install`. Then you can start the broker by running the command
`bash activemq run`. When finished run `bash activemq stop`

### Application
Before building the application `mvn clean` is recommended.

To build application run the command `bash make`. After the application is build run the Bank instance by running
the command `bash bank`. In new window run the command `bash client [username]` to run the bank client instance.


