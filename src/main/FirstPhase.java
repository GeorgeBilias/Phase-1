package main;


import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.Field;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.index.IndexWriterConfig;


public class FirstPhase {
    public static void main(String[] args) {
        try {
            List<MyDoc> docs = TXTParsing.parse("src/main/documents.txt"); //parse the documents
            //System.out.println(parsedDocs);

            String indexLocation = ("src/main/index"); //define were to store the index 

            Directory dir = FSDirectory.open(Paths.get(indexLocation));// define which analyzer to use for the normalization of documents
            Analyzer analyzer = new EnglishAnalyzer(); //define Analyzer

            // define which similarity to use
            Similarity similarity = new ClassicSimilarity();

            // configure IndexWriter
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setSimilarity(similarity); // set the similarity

            // Create a new index in the directory, removing any
            // previously indexed documents:
            iwc.setOpenMode(OpenMode.CREATE);

            // create the IndexWriter with the configuration as above 
            IndexWriter indexWriter = new IndexWriter(dir, iwc);


            for (MyDoc doc : docs){
                indexDoc(indexWriter, doc); //index the documents
            }

            indexWriter.close(); //close the index



        } catch (IOException e) {
            e.printStackTrace(); //catch the error
        } catch (Exception e) {
            e.printStackTrace(); //catch the error
        }
    }

    private static void indexDoc(IndexWriter indexWriter, MyDoc mydoc){
        
        try {
            
            // make a new, empty document
            Document doc = new Document();
            
            // create the fields of the document and add them to the document (I NEED TO FIND THE CORRRECT FIELD TYPE FOR ID, TITLE AND CONTENT)
            StoredField id = new StoredField("id", mydoc.getID());
            doc.add(id);
            StoredField title = new StoredField("title", mydoc.getTitle());
            doc.add(title);
            StoredField content = new StoredField("content", mydoc.getContent());
            doc.add(content);
            String fullSearchableText = mydoc.getID() + " " + mydoc.getTitle() + " " + mydoc.getContent();            
            TextField contents = new TextField("contents", fullSearchableText, Field.Store.NO);
            doc.add(contents);
            
            if (indexWriter.getConfig().getOpenMode() == OpenMode.CREATE) {
                // New index, so we just add the document (no old document can be there):
                // System.out.println("adding " + mydoc);
                indexWriter.addDocument(doc);
            } 
        } catch(Exception e){
            e.printStackTrace();
        }
        
    }
}


