package ie.tcd.modified;

class CranQuery {
    private int id;
    private String content;

    // Constructor
    public CranQuery(int id, String content) {
        this.id = id;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    
    public String getContent() {
        return content;
    }
}
