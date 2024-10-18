package ie.tcd.modified;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;

public class MainApp {
    private static String DOCUMENTS_PATH = "../corpus/cran.all.1400";
    private static String QUERIES_PATH = "../corpus/cran.qry";

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please provide valid arguments.");
        } else {
            String analysisType;
            Analyzer analyzer;
            Similarity scoringModel;

            switch (args[0]) {
                case "standard":
                    analysisType = "StandardAnalyzer";
                    analyzer = new StandardAnalyzer();
                    break;
                case "whitespace":
                    analysisType = "WhitespaceAnalyzer";
                    analyzer = new WhitespaceAnalyzer();
                    break;
                case "english":
                    analysisType = "EnglishAnalyzer";
                    analyzer = new EnglishAnalyzer();
                    break;
                default:
                    analysisType = "EnglishAnalyzer";
                    analyzer = new EnglishAnalyzer();
                    break;
            }

            switch (args[1]) {
                case "vsm":
                    scoringModel = new ClassicSimilarity();
                    analysisType = analysisType + "_VSM";
                    break;
                case "bm25":
                    scoringModel = new BM25Similarity();
                    analysisType = analysisType + "_BM25";
                    break;
                default:
                    scoringModel = new BM25Similarity();
                    analysisType = analysisType + "_BM25";
                    break;
            }

            System.out.println("Starting Index Creation...");
            IndexBuilder indexBuilder = new IndexBuilder(DOCUMENTS_PATH, analyzer);
            System.out.println("Executing Queries...");
            QueryProcessor queryProcessor = new QueryProcessor(QUERIES_PATH, analyzer, scoringModel, analysisType);
        }
    }
}
