
package ie.tcd.modified;

class SearchResult {
    private static String Q0 = "Q0";
    private int queryId;
    private int docId;
    private int rank;
    private float score;
    private String runId;

    // Constructor
    public SearchResult(int queryId, int docId, int rank, float score, String runId) {
        this.queryId = queryId;
        this.docId = docId;
        this.rank = rank;
        this.score = score;
        this.runId = runId;
    }

    
    public String toTrecEvalFormat() {
        return queryId + "\t" + Q0 + "\t" + docId + "\t" + rank + "\t" + score + "\t" + runId;
    }
}

