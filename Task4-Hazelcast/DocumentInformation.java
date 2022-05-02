import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DocumentInformation implements Serializable {
    private String docKey;
    private int views = 0;
    private List<String> comments;

    public DocumentInformation(String docKey) {
        this.docKey = docKey;
        this.comments = new ArrayList<>();
    }

    public int getViews() {
        return views;
    }

    public String getDocKey() {
        return docKey;
    }

    public List<String> getComments() {
        return comments;
    }

    public void incView() {
        views++;
    }

    public void addComment(String comment) {
        comments.add(comment);
    }
}
