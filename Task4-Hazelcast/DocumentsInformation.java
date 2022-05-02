import java.util.List;

public class DocumentsInformation {
    private String docKey;
    private int views = 0;
    private List<String> comments;

    public DocumentsInformation(String docKey) {
        this.docKey = docKey;
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
