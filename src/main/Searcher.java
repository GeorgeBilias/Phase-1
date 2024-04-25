package main;

// tested for lucene 7.7.3 and jdk13
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.FSDirectory;

/**
 *
 * @author Tonia Kyriakopoulou
 */
public class Searcher {
    
    public Searcher(){
        try{
            String indexLocation = ("src/main/index"); //define where the index is stored
            String field = "contents"; //define which field will be searched            
            
            //Access the index using indexReaderFSDirectory.open(Paths.get(index))
            IndexReader indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexLocation))); //IndexReader is an abstract class, providing an interface for accessing an index.
            IndexSearcher indexSearcher = new IndexSearcher(indexReader); //Creates a searcher searching the provided index, Implements search over a single IndexReader.
            indexSearcher.setSimilarity(new ClassicSimilarity());
            
            //Search the index using indexSearcher
            search(indexSearcher, field);
            
            //Close indexReader
            indexReader.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * Searches the index given a specific user query.
     */
    private void search(IndexSearcher indexSearcher, String field){
        try{
            // define which analyzer to use for the normalization of user's query
            Analyzer analyzer = new EnglishAnalyzer();
            
            // create a query parser on the field "contents"
            QueryParser parser = new QueryParser(field, analyzer);              
            
            // read user's query from stdin
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enter query or 'q' to quit: ");
            System.out.print(">>");
            String line = br.readLine();
            while(line!=null && !line.equals("") && !line.equalsIgnoreCase("q")){
                // parse the query according to QueryParser
                Query query = parser.parse(line);
                System.out.println("Searching for: " + query.toString(field));
                
                // search the index using the indexSearcher
                TopDocs results = indexSearcher.search(query, 50); // search the index using the indexSearcher, FIND TOP 100 DOCUMENTS
                ScoreDoc[] hits = results.scoreDocs; // get the results of the search
                long numTotalHits = results.totalHits; // get the total number of matching documents
                System.out.println(numTotalHits + " total matching documents"); // display the total number of matching documents


                //display results
                for(int i=0; i<hits.length; i++){
                    Document hitDoc = indexSearcher.doc(hits[i].doc);
                    System.out.println("\tScore "+hits[i].score +"\tid="+hitDoc.get("id")); //display the document's id
                } // for each document
                
                System.out.println("Enter query or 'q' to quit: ");
                System.out.print(">>");
                line = br.readLine();
            }
        } catch(Exception e){
            e.printStackTrace(); // handle exceptions
        }
    }

    /**
     * Initialize a Searcher
     */
    public static void main(String[] args){
        Searcher searcher = new Searcher(); // create a SearcherDemo
    }
}
