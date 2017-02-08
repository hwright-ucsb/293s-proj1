import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.document.Document;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import java.io.IOException;
import java.nio.file.*;
import java.util.Scanner;
import java.io.BufferedReader;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.*;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import java.io.StringReader;
import java.io.Reader;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.tartarus.snowball.ext.PorterStemmer;
import org.apache.lucene.search.similarities.*;
import java.util.HashMap;
import org.apache.lucene.search.*;
import org.apache.lucene.index.Term;

public class QueryString {


    public static void printOldAndNewQueries(String pair, String queryLine) {
        if(!queryLine.equals(pair)) {
            System.out.println("OLD: " + pair);
            System.out.println("NEW: " + queryLine);
        }
    }

    public static String removeStopWordsAndStem(String textFile) {
        CharArraySet stopWords = EnglishAnalyzer.getDefaultStopSet();
        StandardTokenizer tokenStream = new StandardTokenizer();
        Reader targetReader = new StringReader(textFile.trim());
        tokenStream.setReader(targetReader);

        StopFilter filter = new StopFilter(tokenStream, stopWords);

        StringBuilder sb = new StringBuilder();
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        try{
          filter.reset();
        }catch(IOException e){
          e.printStackTrace();
        }
        PorterStemmer stemmer = new PorterStemmer();
        
        boolean tok = false;

        try{
            tok = filter.incrementToken();
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
                tok = filter.incrementToken();
            } catch(IOException e){
                e.printStackTrace();
            }

        }
        return sb.toString();
    }

    public static void main(String[] args) throws IOException, ParseException {
        StandardAnalyzer analyzer = new StandardAnalyzer();
        FSDirectory index = FSDirectory.open(Paths.get("index"));

        // 2. query
        String simstring = args.length > 0 ? args[0] : "default";

        // the first arg specifies the default field to use
        // when no field is explicitly specified in the query.
        // Scanner sc = new Scanner(System.in);
        // System.out.println("\nWhich field would you like to query? (Searches title by default.)");
        // System.out.println("\tEnter 1 for DOC ID");
        // System.out.println("\tEnter 2 for BODY");
        // System.out.println("\tEnter anything else for TITLE");

        // int fieldCode = sc.nextInt();
        // sc.nextLine();
        int fieldCode = 3;

        // 3. search
        // int hitsPerPage = 10;
        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);

        String queries = null;
        Similarity simfn = null;
        QueryParser parser;

        if ("default".equals(simstring)) {
            simfn = new ClassicSimilarity();
        } else if ("bm25".equals(simstring)) {
            simfn = new BM25Similarity();
        } else if ("dfr".equals(simstring)) {
            simfn = new DFRSimilarity(new BasicModelP(), new AfterEffectL(), new NormalizationH2());
        } else if ("lm".equals(simstring)) {
            simfn = new LMDirichletSimilarity();
        }


        BufferedReader in = null;
        if (queries != null) {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(queries), "UTF-8"));
        } else {
            in = new BufferedReader(new InputStreamReader(new FileInputStream("test-data/title-queries.301-450"), "UTF-8"));
        }

        if(fieldCode==1){
            parser = new QueryParser("docID", analyzer);
        }
        else if(fieldCode==2){
            parser = new QueryParser("body", analyzer);
        } else if(fieldCode == 3) {
            parser = new QueryParser("content", analyzer);
        }
        else{
            parser = new QueryParser("title",analyzer);
        }

        long numQueries = 0;
        long averageTime = 0;
        

        while (true) {
            String line = in.readLine();

            if (line == null || line.length() == -1) {
                break;
            }

            line = line.trim();
            if (line.length() == 0) {
                break;
            }
            
            String[] pair = line.split(" ", 2);

            /*
                Stop Word Removal From Query
            */
            String queryLine = pair[1];//removeStopWordsAndStem(pair[1]);
            // printOldAndNewQueries(pair[1], queryLine);
            
            boolean doPhraseQuery = false;
            PhraseQuery phraseQuery;
            Query query;
            long startTime;
            long endTime;

            if(doPhraseQuery) {
                PhraseQuery.Builder builder = new PhraseQuery.Builder();
                String[] split = queryLine.split(" ");
                for(int i = 0; i < split.length; i++) {
                    builder.add(new Term("content", split[i]), i);
                }
                
                phraseQuery = builder.build();
                startTime = System.currentTimeMillis();
                doBatchSearch(in, searcher, pair[0], phraseQuery, simstring);
                endTime = System.currentTimeMillis();
            } else {
                query = parser.parse(queryLine);
                startTime = System.currentTimeMillis();
                doBatchSearch(in, searcher, pair[0], query, simstring);
                endTime = System.currentTimeMillis();
            }

            numQueries++;
            averageTime += (endTime - startTime);
        }
        averageTime /= numQueries;
        // System.out.println("Average time is : " + averageTime);
        // reader can only be closed when there
        // is no need to access the documents any more.
        reader.close();
    }

        /**
     * This function performs a top-1000 search for the query as a basic TREC run.
     */
    public static void doBatchSearch(BufferedReader in, IndexSearcher searcher, String qid, Query query, String runtag)  
            throws IOException {

        // Collect enough docs to show 5 pages
        TopDocs results = searcher.search(query, 1000);
        ScoreDoc[] hits = results.scoreDocs;
        HashMap<String, String> seen = new HashMap<String, String>(1000);
        int numTotalHits = results.totalHits;
        
        int start = 0;
        int end = Math.min(numTotalHits, 1000);

        for (int i = start; i < end; i++) {
            Document doc = searcher.doc(hits[i].doc);
            String docno = doc.get("docID");
            // There are duplicate document numbers in the FR collection, so only output a given
            // docno once.
            if (seen.containsKey(docno)) {
                continue;
            }
            seen.put(docno, docno);
            System.out.println(qid+" Q0 "+docno+" "+i+" "+hits[i].score+" "+runtag);
        }
    }
}
