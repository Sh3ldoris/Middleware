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
        connectionId(connectionId), loggedIn(false) {}

    void isUserLoggedIn() const {
        std::cout << "User data cLogIn function" << std::endl;
    }

    void logOutUser() const {
        std::cout << "User data cLogOut function" << std::endl;
    }

    unsigned connectionId;
    bool loggedIn;
};

// Implementation of the Login service
class LoginHandler: public LoginIf {
    // Each connection gets assigned an id
    // That allows us to see how for each connection, a new handler is used
    std::shared_ptr<UserData> user_data;

public:
    LoginHandler(std::shared_ptr<UserData> user_data) :
        user_data(std::move(user_data)) {}

    // Implementation of logIn
    void logIn(const std::string& userName, const std::int32_t key) override {
        std::cout << "Login function" << std::endl;
    }

    // Implementation of logOut
    void logOut() override {
        std::cout << "Logout function" << std::endl;
    }
};

class SearchHandler : public SearchIf {
    // Each connection gets assigned an id
    // That allows us to see how for each connection, a new handler is used
    std::shared_ptr<UserData> user_data;
    std::map<std::string, std::function<Item()>> item_factory = {
        { "itemA", [this]() {
            Item item;

            item.__isset.itemA = true;

            {
                auto s = std::to_string(item.itemA.fieldA = rand());
                user_data->fetched.try_emplace("fieldA", std::set<std::string>()).first->second.emplace(s);
            }
            
            {
                std::size_t i = rand() & 0x7;
                std::stringstream ss;
                while (i--) {
                    auto num = (short)rand();
                    ss << num;
                    if (i) ss << ',';
                    item.itemA.fieldB.push_back(num);
                }
                user_data->fetched.try_emplace("fieldB", std::set<std::string>()).first->second.emplace(ss.str());
            }
            
            {
                auto s = std::to_string(item.itemA.fieldC = rand());
                user_data->fetched.try_emplace("fieldC", std::set<std::string>()).first->second.emplace(s);
            }
            
            return item;
        }},
        { "itemB", [this]() {
            Item item;
            item.__isset.itemB = true;
            {
                auto s = std::to_string(rand());
                user_data->fetched.try_emplace("fieldA", std::set<std::string>()).first->second.emplace(s);
                item.itemB.fieldA = s;
            }
            {
                std::size_t i = rand() & 0x7;
                while (i--) {
                    auto s = std::to_string(rand());
                    item.itemB.fieldB.emplace(s);
                }
                std::stringstream ss;
                for (auto &&s : item.itemB.fieldB) {
                    if (++i) ss << ',';
                    ss << s;
                }
                user_data->fetched.try_emplace("fieldB", std::set<std::string>()).first->second.emplace(ss.str());
            }
            {
                std::size_t i = rand() & 0x7;
                std::stringstream ss;
                if (i != 0)
                    item.itemB.__isset.fieldC = true;
                while (i--) {
                    auto s = std::to_string(rand());
                    ss << s;
                    if (i) ss << ',';
                    item.itemB.fieldC.push_back(s);
                }
                if (item.itemB.__isset.fieldC)
                    user_data->fetched.try_emplace("fieldC", std::set<std::string>()).first->second.emplace(ss.str());
            }
            return item;
        }},
        { "itemC", [this]() {
            Item item;
            item.__isset.itemC = true;
            auto s = std::to_string(item.itemC.fieldA = (rand() & 1) == 1);
            user_data->fetched.try_emplace("fieldA", std::set<std::string>()).first->second.emplace(s);
            return item;
        }},
    };

    struct Fetcher {
        Fetcher() = default;
        Fetcher(std::size_t i, std::size_t j, std::uint32_t limit, const std::string &query) :
            i(i), j(j), limit(limit), query(query) {}
        std::size_t i = 0, j;
        std::uint32_t limit;
        std::string query;
    };

    std::unique_ptr<Fetcher> fetcher = nullptr;

public:
    SearchHandler(std::shared_ptr<UserData> user_data) :
        user_data(std::move(user_data)) {}

    void search(SearchState& _return, const std::string& query, const int32_t limit) override {
        std::cout << "Search function" << std::endl;
    }

    void fetch(FetchResult& _return, const SearchState& state) override {
        std::cout << "Fetch function" << std::endl;
    }
};

class ReportsHandler : public ReportsIf {
    std::shared_ptr<UserData> user_data;

public:
    ReportsHandler(std::shared_ptr<UserData> user_data) :
        user_data(user_data) {}

    bool saveReport(const Report& report) override {
        user_data->logOutUser();
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
        auto user_data = std::make_shared<UserData>(assignId());

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
        shared_ptr<TServerTransport> serverTransport = std::make_shared<TServerSocket>(5000);
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