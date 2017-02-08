import java.io.IOException;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.store.Directory;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;
import java.nio.file.*;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.index.IndexWriterConfig;
import java.io.BufferedReader;
import java.io.FileReader;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.tartarus.snowball.ext.PorterStemmer;
import org.apache.lucene.search.similarities.*;
import java.util.HashMap;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import java.io.Reader;
import java.io.StringReader;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class Indexer {

	public static void printOldAndNewQueries(String pair, String queryLine) {
		if(!queryLine.equals(pair)) {
			System.out.println("OLD: " + pair);
			System.out.println("NEW: " + queryLine);
		}
	}

	public static String stemWords(String textFile) {
		CharArraySet stopWords = EnglishAnalyzer.getDefaultStopSet();
		StandardTokenizer tokenStream = new StandardTokenizer();
		Reader targetReader = new StringReader(textFile.trim());
		tokenStream.setReader(targetReader);

		StringBuilder sb = new StringBuilder();
		CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
		try{
		  tokenStream.reset();
		}catch(IOException e){
		  e.printStackTrace();
		}
		PorterStemmer stemmer = new PorterStemmer();
		
		boolean tok = false;

		try{
			tok = tokenStream.incrementToken();
		}catch(IOException e){
		  e.printStackTrace();
		}

		while (tok) {
			String term = charTermAttribute.toString();
			
			stemmer.setCurrent(term);
			stemmer.stem();
			String current = stemmer.getCurrent();

			sb.append(current + " ");

			
			try {
				tok = tokenStream.incrementToken();
			} catch(IOException e){
				e.printStackTrace();
			}

		}
		return sb.toString();
	}

	public static void main(String[] args) throws IOException {
		int NUM_FILES = args.length > 0 ? Integer.parseInt(args[0]) : 100;

		// 0. Specify the analyzer for tokenizing text.
		//    The same analyzer should be used for indexing and searching
		StandardAnalyzer analyzer = new StandardAnalyzer();

		// 1. create the index

		String fileName = "index";
		FSDirectory index = FSDirectory.open(Paths.get(fileName));

		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

		IndexWriter w = new IndexWriter(index, config);
		BufferedReader br = new BufferedReader(new FileReader("../../lines-trec45.txt"));
		String line;
		String[] stuff;
		while((line = br.readLine()) != null){
			stuff = line.split("\t");
			if(stuff.length == 1){
				addDoc(w,stuff[0],"","");
			}
			else if(stuff.length == 2){
				addDoc(w,stuff[0],stuff[1],"");
			}else{
				addDoc(w,stuff[0],stuff[1],stuff[2]); 
			}
		}

		br.close();
		w.close();
	}

	private static void addDoc(IndexWriter w, String docID, String title, String body) throws IOException {
		// 488473549 Jan 18 17:50 lines-trec45.txt.gz
		// This file has the same content as trec-disk4-5.xml except that
		// the XML data is  stripped to plaintext.
		// Each line is a document and the format is:
		// Up to the first tab  --> the docid,
		// between the first and second tab -->  the title,
		// after the second tab --> the body of the document
		String newTitle = title;//stemWords(title);
		String newBody = body;//stemWords(body);

		// printOldAndNewQueries(title, newTitle);
		// printOldAndNewQueries(newBody, body);

		Document doc = new Document();
		if(docID != null && newTitle != null && newBody != null) {
			doc.add(new TextField("content", docID + " " + newTitle + " " + newBody, Field.Store.YES));
			doc.add(new StringField("docID", docID, Field.Store.YES));
			doc.add(new TextField("title", newTitle, Field.Store.YES));
			doc.add(new TextField("body", newBody, Field.Store.YES));
			w.addDocument(doc);
		}
	}
}