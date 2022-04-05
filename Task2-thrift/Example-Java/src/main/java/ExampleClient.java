import Task2.*;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.util.*;

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

            System.out.println("Initializing search!\n");

            // Init the search with the query and limit
            searchClient.initializeSearch("ITEMB;ITEMA;ITEMC", 10);

            System.out.println("Fetching items!\n");
            Summary summary = new Summary();

            // Fetch the items from the server and save them
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
                        if (item.isSetItemB()) {
                            processItemB(item.getItemB(), summary);
                            System.out.println("ItemB");
                            System.out.println(
                                    "FieldX: " + item.getItemB().fieldX
                                            + ", fieldY: " + item.getItemB().fieldY
                                            + ", fieldZ: " + item.getItemB().fieldZ
                            );
                        }

                        if (item.isSetItemA()) {
                            processItemA(item.getItemA(), summary);
                            System.out.println("ItemA");
                            System.out.println(
                                    "FieldX: " + item.getItemA().fieldX
                                            + ", fieldY: " + item.getItemA().fieldY
                                            + ", fieldZ: " + item.getItemA().fieldZ
                            );
                        }

                        if (item.isSetItemC()) {
                            processItemC(item.getItemC(), summary);
                            System.out.println("ItemC");
                            System.out.println("FieldX: " + item.getItemC().fieldX);
                        }
                        break;

                    default:
                        System.out.println("Unexpected status!");
                        break;
                }
            }

            TProtocol reportsProtocol = new TMultiplexedProtocol(muxProtocol, "Reports");
            Reports.Client reportsClient = new Reports.Client(reportsProtocol);

            System.out.println("\nSaving and verifying summary!");

            if (reportsClient.saveSummary(summary.itemsfields)) {
                System.out.println("Summary saved and verified successfully!\n");
            } else {
                System.out.println("Summary cannot be saved or verified!\n");
            }
            System.out.println("Logging out!\n");
            loginClient.logOut();

            System.out.println("Application ends!");
        } catch (Exception e) {
            System.out.println("Something went wrong!");
            e.printStackTrace();
        }
    }

    private static class Summary {
        private Map<String, Set<String>> itemsfields =  new TreeMap<>();

        public void addField(String fieldName, String fieldValue) {
            if (itemsfields.containsKey(fieldName))
                itemsfields.get(fieldName).add(fieldValue);
            else {
                Set<String> newSet = new TreeSet<>();
                newSet.add(fieldValue);
                itemsfields.put(fieldName, newSet);
            }
        }
    }

    private static void processItemA(ItemA item, Summary summary) {
        if (item.isSetFieldX()) {
            summary.addField("fieldX", item.getFieldX());
        }

        if (item.isSetFieldY()) {
            summary.addField("fieldY", processList(item.getFieldY()));
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
            summary.addField("fieldY", processList(item.getFieldY()));
        }

        if (item.isSetFieldZ()) {
            summary.addField("fieldZ", processSet(item.getFieldZ()));
        }
    }

    private static void processItemC(ItemC item, Summary summary) {
        if (item.isSetFieldX()) {
            summary.addField("fieldX", item.isFieldX() ? "true" : "false");
        }
    }

    private static <T extends Comparable<T>> String processSet(Set<T> set) {
        StringBuilder sBuilder = new StringBuilder();
        int i = 0;
        Set<T> orderedSet = new TreeSet<>(set);
        for (T value : orderedSet) {
            if (i++ != 0) {
                sBuilder.append(',');
            }
            sBuilder.append(value);
        }
        return sBuilder.toString();
    }

    private static <T extends Comparable<T>> String processList(List<T> list) {
        StringBuilder sBuilder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i != 0) {
                sBuilder.append(',');
            }
            sBuilder.append(list.get(i));
        }
        return sBuilder.toString();
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
