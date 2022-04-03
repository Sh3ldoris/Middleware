// Standard library headers
#include <memory>
#include <functional>
#include <iostream>
#include <string>
#include <sstream>
#include <string_view>
#include <atomic>
#include <map>
#include <set>
#include <bits/stdc++.h>

// Thrift headers
#include <thrift/protocol/TProtocol.h>
#include <thrift/protocol/TBinaryProtocol.h>
#include <thrift/protocol/TMultiplexedProtocol.h>
#include <thrift/transport/TSocket.h>
#include <thrift/transport/TTransportUtils.h>
#include <thrift/server/TServer.h>
#include <thrift/server/TThreadedServer.h>
#include <thrift/processor/TMultiplexedProcessor.h>
#include <thrift/TProcessor.h>
#include <thrift/Thrift.h>

// Generated headers
#include "gen-cpp/Login.h"
#include "gen-cpp/Search.h"
#include "gen-cpp/Reports.h"
#include "gen-cpp/Task_types.h"

using namespace apache::thrift;
using namespace apache::thrift::transport;
using namespace apache::thrift::protocol;
using namespace apache::thrift::server;

struct SharedUserData {
    SharedUserData(unsigned connectionId) :
        connectionId(connectionId), isLoggedIn(false) {}

    unsigned connectionId;
    bool isLoggedIn;
};

// Implementation of the Login service
class LoginHandler: public LoginIf {
    // Each client will have his own shared_data
    std::shared_ptr<SharedUserData> user_data;

public:
    LoginHandler(std::shared_ptr<SharedUserData> user_data) :
        user_data(std::move(user_data)) {}

    // Implementation of logIn
    void logIn(const std::string& userName, const std::int32_t key) override {
        std::cout << "Login function" << std::endl;
        std::int32_t logKey = std::hash<std::string>{}(userName);
        if (user_data->isLoggedIn)
        {
            ProtocolException ex;
            ex.message= "Client is already logged in!";
            throw ex;
        }

        // Check for log key from the client (if does not match send it in InvalidKeyException)
        if (key != logKey)
        {
            InvalidKeyException invalidLoginEx;
            invalidLoginEx.invalidKey = key;
            invalidLoginEx.expectedKey = logKey;

            throw invalidLoginEx;
        } else {
            user_data->isLoggedIn = true;
        }
        
    }

    // Implementation of logOut
    void logOut() override {
        std::cout << "Logout function" << std::endl;
        if (!user_data->isLoggedIn)
        {
            ProtocolException ex;
            ex.message= "Client is not logged in!";
            throw ex;
        } 
        
        // If the client is logged in it will be logged out by setting shared var isLoggedIn to false;
        user_data->isLoggedIn = false;
    }
};

class SearchHandler : public SearchIf {
    // Each client will have his own shared_data
    std::shared_ptr<SharedUserData> user_data;
    int32_t initilizedLimit = 0;
    std::vector<std::string> initializedItems;

public:
    SearchHandler(std::shared_ptr<SharedUserData> user_data) :
        user_data(std::move(user_data)) {}

    // Client can fetch items only if it is search initializied and client is logged in
    void fetch(FetchResult& _return) override {
        std::cout << "Fetch function" << std::endl;
        // Check if client is logged in
        if (!user_data->isLoggedIn)
        {
            ProtocolException ex;
            ex.message= "Client is not logged in!";
            throw ex;
        }

        // Check for query and limit initialization
        if (initializedItems.size() == 0 || initilizedLimit < 0)
        {
            ProtocolException ex;
            ex.message= "Cannot fetch item(s) due to bad search initialization!";
            throw ex;
        }


        if (initilizedLimit == 0)
        {
            _return.status = FetchStatus::ENDED;
            return;
        }

        Item item;

        int index = rand() % initializedItems.size();
        std::string randomItemId = initializedItems.at(index);

        if (randomItemId.compare("ITEMC") == 0)
        {
            item.__isset.itemC = true;
            item.itemC.fieldX = true;
        } else if (randomItemId.compare("ITEMA") == 0) {
            item.__isset.itemA = true;
            item.itemA.fieldX = "No joo";
            item.itemA.fieldY.push_back(6);
            item.itemA.fieldZ = 99;
        } else if (randomItemId.compare("ITEMB") == 0) {
            item.__isset.itemB = true;
            item.itemB.fieldX = 99;
            std::string rs = "Random string";
            item.itemB.__isset.fieldY = true;
            item.itemB.fieldY.push_back(rs);
            item.itemB.fieldZ.insert(rs);
        }
            
        initilizedLimit--;
        _return.status = FetchStatus::ITEM;
        _return.__set_item(item);
    }

    // Client can initialize search without logging in!
    // After initializing search the query and the search limit will be stored in the server
    void initializeSearch(const std::string& query, const int32_t limit) override {
        std::cout << "Search function" << std::endl;

        if (limit < 1 || query.compare("") == 0)
        {
            ProtocolException ex;
            ex.message= "Cannot initalize search due to wrong LIMIT or QUERY!";
            throw ex;
        }
        
        initilizedLimit = limit;

        std::string itemId = "";
        for (auto x : query) 
        {
            if (x == ';')
            {
                initializedItems.push_back(itemId);
                itemId = "";
            }
            else {
                itemId = itemId + x;
            }
        }
        initializedItems.push_back(itemId);
    }
};

class ReportsHandler : public ReportsIf {
    // Each client will have his own shared_data
    std::shared_ptr<SharedUserData> user_data;

public:
    ReportsHandler(std::shared_ptr<SharedUserData> user_data) :
        user_data(user_data) {}

    bool saveSummary(const Summary& summary) override {
        return true;
    }
};

// This factory creates a new handler for each conection
class PerConnectionProcessorFactory: public TProcessorFactory{
    // We assign each handler an id
    std::atomic<unsigned> connectionIdCounter;

public:
    // Constructor
    PerConnectionProcessorFactory(): connectionIdCounter(0) {}

    // The counter is incremented for each connection
    unsigned assignId() {
        return ++connectionIdCounter;
    }

    // This metod is called for each connection
    virtual std::shared_ptr<TProcessor> getProcessor(const TConnectionInfo& connInfo) override {
        // Assign a new id to this connection
        auto user_data = std::make_shared<SharedUserData>(assignId());

        // Create Login handler and processor
        shared_ptr<LoginHandler> loginHandler = std::make_shared<LoginHandler>(user_data);
        shared_ptr<TProcessor> loginProcessor = std::make_shared<LoginProcessor>(loginHandler);

        // Create Search handler and processor
        shared_ptr<SearchHandler> searchHandler = std::make_shared<SearchHandler>(user_data);
        shared_ptr<TProcessor> searchProcessor = std::make_shared<SearchProcessor>(searchHandler);

        // Create Report handler and processor
        shared_ptr<ReportsHandler> reportsHandler = std::make_shared<ReportsHandler>(user_data);
        shared_ptr<TProcessor> reportsProcessor = std::make_shared<ReportsProcessor>(reportsHandler);

        // Add the processor to a multiplexed processor
        // This allows extending this server by adding more services
        shared_ptr<TMultiplexedProcessor> muxProcessor = std::make_shared<TMultiplexedProcessor>();
        muxProcessor->registerProcessor("Login", loginProcessor);
        muxProcessor->registerProcessor("Search", searchProcessor);
        muxProcessor->registerProcessor("Reports", reportsProcessor);
        // Use the multiplexed processor
        return muxProcessor;
    }
};

int main(){
    
    try{
        // Accept connections on a TCP socket
        shared_ptr<TServerTransport> serverTransport = std::make_shared<TServerSocket>(5001);
        // Use buffering
        shared_ptr<TTransportFactory> transportFactory = std::make_shared<TBufferedTransportFactory>();
        // Use a binary protocol to serialize data
        shared_ptr<TProtocolFactory> protocolFactory = std::make_shared<TBinaryProtocolFactory>();
        // Use a processor factory to create a processor per connection
        shared_ptr<TProcessorFactory> processorFactory = std::make_shared<PerConnectionProcessorFactory>();

        // Start the server
        TThreadedServer server(processorFactory, serverTransport, transportFactory, protocolFactory);
        server.serve();
    }
    catch (TException& tx) {
        std::cerr << "ERROR: " << tx.what() << std::endl;
    }

}