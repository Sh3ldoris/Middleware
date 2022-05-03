import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private String userName;
    private String selectedDocument;
    private List<String> favoritesDocs;
    private int currentFavoriteIndex;

    public User(String userName) {
        this.userName = userName;
        this.favoritesDocs = new ArrayList<>();
        this.currentFavoriteIndex = 0;
    }

    public String getUserName() {
        return userName;
    }

    public String getSelectedDocument() {
        return selectedDocument;
    }

    public void setSelectedDocument(String selectedDocument) {
        this.selectedDocument = selectedDocument;
    }

    public List<String> getFavoritesDocs() {
        return favoritesDocs;
    }

    public void addFavoriteDoc(String docName) {
        if (!favoritesDocs.contains(docName)) {
            favoritesDocs.add(docName);
        }
    }

    public void removeFavoriteDoc(String docName) {
        this.favoritesDocs.remove(docName);
    }

    public String getNextFavoriteDoc() {
        if (this.favoritesDocs.size() == 0) {
            return null;
        }

        String favoriteDoc = this.favoritesDocs.get(this.currentFavoriteIndex);

        this.currentFavoriteIndex = (this.currentFavoriteIndex + 1) % this.favoritesDocs.size();

        return favoriteDoc;
    }

}
