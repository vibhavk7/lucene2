

package ie.tcd.modified;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class IndexBuilder {
    private String dataPath;
    private static String INDEX_DIR = "../index";

    // Constructor
    public IndexBuilder(String path, Analyzer analyzer) {
        this.dataPath = path;
        System.out.println("DEBUG: Path to data - " + dataPath);
        try {
            parseAndCreateIndex(analyzer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parseAndCreateIndex(Analyzer analyzer) throws IOException {
        Directory directory = FSDirectory.open(Paths.get(INDEX_DIR));
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(directory, config);

        FileInputStream inputStream = new FileInputStream(dataPath);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        String currentTag = "ID";
        String previousTag = "";
        String tagContent = "";
        boolean newContent = true;
        boolean firstRun = true;

        ArrayList<Document> docsList = new ArrayList<>();
        Document doc = new Document();

        System.out.println("STARTING Parsing: " + dataPath);

        while ((line = bufferedReader.readLine()) != null) {
            if (isNewTag(line)) {
                previousTag = currentTag;
                currentTag = extractTag(line);

                if (!"ID".equals(previousTag)) {
                    doc.add(new TextField(previousTag, tagContent, Field.Store.YES));
                }
                if (isIdTag(line)) {
                    String docId = line.substring(3);
                    if (!firstRun) {
                        docsList.add(doc);
                    }
                    firstRun = false;
                    doc = new Document();
                    doc.add(new StringField(currentTag, docId, Field.Store.YES));
                    System.out.println("Indexed document with ID: " + docId);
                }
                newContent = true;
            } else {
                if (newContent) {
                    tagContent = line;
                    newContent = false;
                } else {
                    tagContent = tagContent + "\n" + line;
                }
            }
        }

        doc.add(new TextField(currentTag, tagContent, Field.Store.YES));
        docsList.add(doc);

        writer.addDocuments(docsList);
        writer.close();
        directory.close();
        System.out.println("FINISHED Parsing and Indexing");

        inputStream.close();
    }

    // Tag-related methods
    public String extractTag(String line) {
        if (isIdTag(line)) return "ID";
        if (isTitleTag(line)) return "Title";
        if (isAuthorTag(line)) return "Author";
        if (isBibTag(line)) return "Bibliography";
        return "Body";
    }

    public boolean isNewTag(String line) {
        return isIdTag(line) || isTitleTag(line) || isAuthorTag(line) || isBibTag(line) || isBodyTag(line);
    }

    public boolean isIdTag(String line) {
        return line.startsWith(".I");
    }

    
    public boolean isTitleTag(String line) {
        return line.startsWith(".T");
    }

    public boolean isAuthorTag(String line) {
        return line.startsWith(".A");
    }

    public boolean isBibTag(String line) {
        return line.startsWith(".B");
    }

    public boolean isBodyTag(String line) {
        return line.startsWith(".W");
    }
}
