import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    private String userName;
    private String selectedDocument;
    private List<String> favoritesDocs;

    public User(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public void setFavoritesDocs(List<String> favoritesDocs) {
        this.favoritesDocs = favoritesDocs;
    }
}
