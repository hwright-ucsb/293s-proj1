import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.BufferedReader;
import java.io.FileReader;

public class HelloLucene {
    public static void main(String[] args) throws IOException, ParseException {
        // 0. Specify the analyzer for tokenizing text.
        //    The same analyzer should be used for indexing and searching
        StandardAnalyzer analyzer = new StandardAnalyzer();

        // 1. create the index
        Directory index = new RAMDirectory();

        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        IndexWriter w = new IndexWriter(index, config);
        BufferedReader br = new BufferedReader(new FileReader("lines-trec45.txt"));
        String line;
        String[] stuff;
        for(int i=0; i<100; i++){
           line = br.readLine();
           stuff = line.split("\t");
           addDoc(w,stuff[0],stuff[1],stuff[2]);
        }

        br.close();
        w.close();

        // 2. query
        String querystr = args.length > 0 ? args[0] : "lucene";

        // the first arg specifies the default field to use
        // when no field is explicitly specified in the query.
        Query q = new QueryParser("body", analyzer).parse(querystr);

        // 3. search
        int hitsPerPage = 10;
        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs docs = searcher.search(q, hitsPerPage);
        ScoreDoc[] hits = docs.scoreDocs;

        // 4. display results
        System.out.println("Found " + hits.length + " hits.");
        for(int i=0;i<hits.length;++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            System.out.println((i + 1) + ". " + d.get("docId") + "\t" + d.get("title"));
        }

        // reader can only be closed when there
        // is no need to access the documents any more.
        reader.close();
    }

    private static void addDoc(IndexWriter w, String docID, String title, String body) throws IOException {
        // 488473549 Jan 18 17:50 lines-trec45.txt.gz
        // This file has the same content as trec-disk4-5.xml except that
        // the XML data is  stripped to plaintext.
        // Each line is a document and the format is:
        // Up to the first tab  --> the docid,
        // between the first and second tab -->  the title,
        // after the second tab --> the body of the document
        Document doc = new Document();
        doc.add(new TextField("docID", docID, Field.Store.YES));
        doc.add(new StringField("title", title, Field.Store.YES));
        doc.add(new StringField("body", body, Field.Store.YES));
        w.addDocument(doc);
    }
}