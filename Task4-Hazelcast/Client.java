import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

public class Client {
	// Reader for user input
	private LineNumberReader in = new LineNumberReader(new InputStreamReader(System.in));
	// Connection to the cluster
	private HazelcastInstance hazelcast;
	// The name of the user
	private String userName;
	// Do not keep any other state here - all data should be in the cluster

	/**
	 * Create a client for the specified user.
	 * @param userName user name used to identify the user
	 */
	public Client(String userName) {
		this.userName = userName;
		// Connect to the Hazelcast cluster
		ClientConfig config = new ClientConfig();
		hazelcast = HazelcastClient.newHazelcastClient(config);
	}

	/**
	 * Disconnect from the Hazelcast cluster.
	 */
	public void disconnect() {
		// Disconnect from the Hazelcast cluster
		hazelcast.shutdown();
	}

	/**
	 * Read a name of a document,
	 * select it as the current document of the user
	 * and show the document content.
	 */
	private void showCommand() throws IOException {
		System.out.println("Enter document name:");
		String documentName = in.readLine();

		// Currently, the document is generated directly on the client
		// Done TODO: change it, so that the document is generated in the cluster and cached
		IMap<String, Document> documentsMap = hazelcast.getMap("Documents");
		// Init the doc info
		loadDocInfo(documentName);
		// Load document
		Document document = documentsMap.executeOnKey(documentName, (data) -> {
			Document doc = data.getValue();

			// If there is no doc yet create one
			if (doc == null) {
				doc = DocumentGenerator.generateDocument(documentName);
				data.setValue(doc);
			}

			return doc;
		});

		// Done TODO: Set the current selected document for the user
		// Load user
		loadUser(userName);
		// Set current doc
		setSelectedDoc(userName, documentName);

		// Done TODO: Get the document (from the cache, or generated)
		// Done TODO: Increment the view count
		incrementViewCount(documentName);

		// Show the document content
		System.out.println("The document is:");
		System.out.println(document.getContent());
	}

	/**
	 * Show the next document in the list of favorites of the user.
	 * Select the next document, so that running this command repeatedly
	 * will cyclically show all favorite documents of the user.
	 */
	private void nextFavoriteCommand() {
		// TODO: Select the next document form the list of favorites
		// TODO: Increment the view count, get the document (from the cache, or generated) and show the document content
	}

	/**
	 * Add the current selected document name to the list of favorite documents of the user.
	 * If the list already contains the document name, do nothing.
	 */
	private void addFavoriteCommand() {
		// TODO: Add the name of the selected document to the list of favorites
		// System.out.printf("Added %s to favorites%n", selectedDocumentName);
	}
	/**
	 * Remove the current selected document name from the list of favorite documents of the user.
	 * If the list does not contain the document name, do nothing.
	 */
	private void removeFavoriteCommand(){
		// TODO: Remove the name of the selected document from the list of favorites
		// System.out.printf("Removed %s from favorites%n", selectedDocumentName);
	}
	/**
	 * Add the current selected document name to the list of favorite documents of the user.
	 * If the list already contains the document name, do nothing.
	 */
	private void listFavoritesCommand() {
		// TODO: Get the list of favorite documents of the user
		// Print the list of favorite documents
		// System.out.println("Your list of favorite documents:");
		// for(String favoriteDocumentName: favoriteList)
		//	System.out.println(favoriteDocumentName);
	}

	/**
	 * Show the view count and comments of the current selected document.
	 */
	private void infoCommand(){
		// TODO: Get the view count and list of comments of the selected document

		// Print the information
		// System.out.printf("Info about %s:%n", selectedDocumentName);
		// System.out.printf("Viewed %d times.%n", viewCount);
		// System.out.printf("Comments (%d):%n", comments.size());
		// for(String comment: comments)
		// 	System.out.println(comment);
	}
	/**
	 * Add a comment about the current selected document.
	 */
	private void commentCommand() throws IOException{
		System.out.println("Enter comment text:");
		String commentText = in.readLine();

		// TODO: Add the comment to the list of comments of the selected document
		// System.out.printf("Added a comment about %s.%n", selectedDocumentName);
	}

	/*
	 * Main interactive user loop
	 */
	public void run() throws IOException {
		loop:
		while (true) {
			System.out.println("\nAvailable commands (type and press enter):");
			System.out.println(" s - select and show document");
			System.out.println(" i - show document view count and comments");
			System.out.println(" c - add comment");
			System.out.println(" a - add to favorites");
			System.out.println(" r - remove from favorites");
			System.out.println(" n - show next favorite");
			System.out.println(" l - list all favorites");
			System.out.println(" q - quit");
			// read first character
			int c = in.read();
			// throw away rest of the buffered line
			while (in.ready())
				in.read();
			switch (c) {
				case 'q': // Quit the application
					break loop;
				case 's': // Select and show a document
					showCommand();
					break;
				case 'i': // Show view count and comments of the selected document
					infoCommand();
					break;
				case 'c': // Add a comment to the selected document
					commentCommand();
					break;
				case 'a': // Add the selected document to favorites
					addFavoriteCommand();
					break;
				case 'r': // Remove the selected document from favorites
					removeFavoriteCommand();
					break;
				case 'n': // Select and show the next document in the list of favorites
					nextFavoriteCommand();
					break;
				case 'l': // Show the list of favorite documents
					listFavoritesCommand();
					break;
				case '\n':
				default:
					break;
			}
		}
	}

	private void incrementViewCount(String docName) {
		IMap<String, DocumentsInformation> docInfoMap = hazelcast.getMap("DocumentsInfo");

		docInfoMap.executeOnKey(docName, (data) -> {
			DocumentsInformation docInfo = data.getValue();
			// Increment views
			docInfo.incView();
			// Save updated doc info
			data.setValue(docInfo);

			return docInfo;
		});
	}

	private DocumentsInformation loadDocInfo(String docName) {
		IMap<String, DocumentsInformation> docInfoMap = hazelcast.getMap("DocumentsInfo");

		return docInfoMap.executeOnKey(docName, (data) -> {
			DocumentsInformation docInfo = data.getValue();

			// If there is no doc info yet create new one
			if (docInfo == null) {
				docInfo = new DocumentsInformation(docName);
				data.setValue(docInfo);
			}

			return docInfo;
		});
	}

	private User loadUser(String userName) {
		IMap<String, User> usersMap = hazelcast.getMap("Users");

		return usersMap.executeOnKey(userName, (data) -> {
			User user = data.getValue();

			// If there is no doc info yet create new one
			if (user == null) {
				user = new User(userName);
				data.setValue(user);
			}

			return user;
		});
	}

	private void setSelectedDoc(String userName, String docName) {
		IMap<String, User> usersMap = hazelcast.getMap("Users");

		usersMap.executeOnKey(userName, (data) -> {
			User user = data.getValue();

			// Set selected doc name
			user.setSelectedDocument(docName);

			// Save updated user
			data.setValue(user);

			return user;
		});
	}

	/*
	 * Main method, creates a client instance and runs its loop
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Usage: ./client <userName>");
			return;
		}

		try {
			Client client = new Client(args[0]);
			try {
				client.run();
			}
			finally {
				client.disconnect();
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

}
