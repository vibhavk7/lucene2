

package ie.tcd.modified;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class QueryProcessor {
    private static String INDEX_DIR = "../index";
    private static int MAX_DOCS = 50;
    private static String OUTPUT_DIR = "results/";

    private ArrayList<CranQuery> queryList = new ArrayList<>();
    private ArrayList<SearchResult> resultList = new ArrayList<>();
    private String analysisType;

    // Constructor
    public QueryProcessor(String queryFilePath, Analyzer analyzer, Similarity similarity, String analysisType) {
        try {
            loadQueries(queryFilePath);
            this.analysisType = analysisType;
            executeQueries(analyzer, similarity);
            saveResults();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadQueries(String filePath) throws IOException {
        FileInputStream fileStream = new FileInputStream(filePath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream));
        String line;
        int queryId = 1;
        StringBuilder queryContent = new StringBuilder();

        System.out.println("Loading Queries from: " + filePath);

        while ((line = reader.readLine()) != null) {
            if (line.startsWith(".I")) {
                if (queryContent.length() > 0) {
                    queryList.add(new CranQuery(queryId++, queryContent.toString()));
                    queryContent.setLength(0);
                }
            } else {
                queryContent.append(line).append("\n");
            }
        }

        queryList.add(new CranQuery(queryId, queryContent.toString()));
        fileStream.close();
    }

    private void executeQueries(Analyzer analyzer, Similarity similarity) throws IOException, ParseException {
        Directory directory = FSDirectory.open(Paths.get(INDEX_DIR));
        DirectoryReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        searcher.setSimilarity(similarity);
        QueryParser parser = new QueryParser("Body", analyzer);

        System.out.println("Executing Queries on the Index");

        for (CranQuery cranQuery : queryList) {
            Query query = parser.parse(cranQuery.getContent());
            ScoreDoc[] hits = searcher.search(query, MAX_DOCS).scoreDocs;

            for (int i = 0; i < hits.length; i++) {
                Document hitDoc = searcher.doc(hits[i].doc);
                resultList.add(new SearchResult(cranQuery.getId(), Integer.parseInt(hitDoc.get("ID")), i + 1, hits[i].score, analysisType));
            }
        }

        reader.close();
        directory.close();
    }

    
    private void saveResults() throws IOException {
        String filePath = OUTPUT_DIR + analysisType + ".test";
        java.io.FileWriter writer = new java.io.FileWriter(filePath);
        for (SearchResult result : resultList) {
            writer.write(result.toTrecEvalFormat() + "\n");
        }
        writer.close();
        System.out.println("Results saved to " + filePath);
    }
}
