// import Task2.*;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ExampleClient {

    private static String userName = "";
    private static String query = "";
    private static int loginKey = 0;
    private static int searchLimit = 5;

    public static void main(String args[]) {
        if (args.length == 2) {
            userName = args[0];
            query = args[1];
        } else {
            System.out.println("Insert username and initial query! Exiting!");
            System.exit(1);
        }

        // Connect to server by TCP socket
        try (TTransport transport = new TSocket("localhost", 5001)) {
            // The socket transport is already buffered
            // Use a binary protocol to serialize data
            TProtocol muxProtocol = new TBinaryProtocol(transport);
            // Use a multiplexed protocol to select a service by name
            TProtocol loginProtocol = new TMultiplexedProtocol(muxProtocol, "Login");

            Login.Client loginClient = new Login.Client(loginProtocol);

            // Open the connection
            transport.open();

            System.out.println("Trying to login!");
            login(userName, loginKey, loginClient);
            System.out.println("Successfully logged in!\n");


            TProtocol searchProtocol = new TMultiplexedProtocol(muxProtocol, "Search");
            Search.Client searchClient = new Search.Client(searchProtocol);

            System.out.println("Initializing search!");
            searchClient.initializeSearch("ITEMB", 5);

            FetchResult result = searchClient.fetch();
            System.out.println(result.status + ", ItemB: " + result.item.isSetItemB());
            if (result.item.isSetItemB()) {
                System.out.println(
                        "FieldX: " + result.item.getItemB().fieldX
                        + ", fieldY size: " + result.item.getItemB().fieldY
                        + ", fieldZ size: " + result.item.getItemB().fieldZ.size()
                );
            }
/*
            System.out.println("Fetching items!");
            Summary summary = new Summary();
            fetching: while (true) {
                FetchResult result = searchClient.fetch();

                switch (result.status)
                {
                    case ENDED:
                        System.out.println("Fetching items ended!");
                        break fetching;

                    case PENDING:
                        System.out.println("Pending for items!");
                        Thread.sleep(100);
                        break;

                    case ITEM:
                        Item item = result.getItem();
                        if (item.isSetItemA()) {
                            System.out.println("ItemA fetched.");
                            processItemA(item.getItemA(), summary);
                        } else if (item.isSetItemB()) {
                            System.out.println("ItemB fetched.");
                            processItemB(item.getItemB(), summary);
                        } else if (item.isSetItemC()) {
                            System.out.println("ItemC fetched.");
                            processItemC(item.getItemC(), summary);
                        }
                        break;

                    default:
                        System.out.println("Unexpected status!");
                        break;
                }
            }

            TProtocol reportsProtocol = new TMultiplexedProtocol(muxProtocol, "Reports");
            Reports.Client reportsClient = new Reports.Client(reportsProtocol);

            System.out.println("Saving and verifying summary!");

            if (reportsClient.saveSummary(summary.itemsfields)) {
                System.out.println("Summary saved and verified successfully!");
            } else {
                System.out.println("Summary cannot be saved or verified!");
            }
*/
            System.out.println("Logging out!");
            loginClient.logOut();

            System.out.println("Application ends!");
        } catch (Exception e) {
            System.out.println("Something went wrong!");
            e.printStackTrace();
        }
    }

    private static class Summary {
        private Map<String, Set<String>> itemsfields = new HashMap<>();

        public void addField(String fieldName, String fieldValue) {
            if (itemsfields.containsKey(fieldName))
                itemsfields.get(fieldName).add(fieldValue);
            else {
                Set<String> newSet = new HashSet<>();
                newSet.add(fieldValue);
                itemsfields.put(fieldName, newSet);
            }
        };
    }

    private static void processItemA(ItemA item, Summary summary) {
        if (item.isSetFieldX()) {
            summary.addField("fieldX", item.getFieldX());
        }

        if (item.isSetFieldY()) {
            StringBuilder sBuilder = new StringBuilder();
            for (int i = 0; i < item.getFieldYSize(); i++) {
                if (i != 0) {
                    sBuilder.append(',');
                }
                sBuilder.append(item.getFieldY().get(i));
            }
            summary.addField("fieldY", sBuilder.toString());
        }

        if (item.isSetFieldZ()) {
            summary.addField("fieldZ", String.valueOf(item.getFieldZ()));
        }
    }

    private static void processItemB(ItemB item, Summary summary) {
        if (item.isSetFieldX()) {
            summary.addField("fieldX", String.valueOf(item.getFieldX()));
        }

        if (item.isSetFieldY()) {
            StringBuilder sBuilder = new StringBuilder();
            for (int i = 0; i < item.getFieldYSize(); i++) {
                if (i != 0) {
                    sBuilder.append(',');
                }
                sBuilder.append(String.valueOf(item.getFieldY().get(i)));
            }
            summary.addField("fieldY", sBuilder.toString());
        }

        if (item.isSetFieldZ()) {
            StringBuilder sBuilder = new StringBuilder();
            int i = 0;
            for (String value : item.getFieldZ()) {
                if (i++ != 0) {
                    sBuilder.append(',');
                }
                sBuilder.append(value);
            }
            summary.addField("fieldZ", sBuilder.toString());
        }
    }

    private static void processItemC(ItemC item, Summary summary) {
        if (item.isSetFieldX()) {
            summary.addField("fieldX", String.valueOf(item.isFieldX() ? "true" : "false"));
        }
    }

    private static void login(String name, int key, Login.Client client) throws TException {
        while (true) {
            try {
                client.logIn(name, key);
                break;
            } catch (InvalidKeyException e) {
                key = e.expectedKey;
            }
        }
    }
}
