//package IRsystem;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.logging.Level;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;

import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.FacetField;
import org.apache.lucene.search.IndexSearcher;

//import org.apache.tika.language.LanguageIdentifier;



public class IndiceSimple {
	private String indexPath = "./index";
	private String facetPath = "./facet";
	private boolean create = true;
	private IndexWriter writer;
	private Analyzer analyzer;
	private Similarity similarity;
	private DirectoryTaxonomyWriter taxoWriter;
	//private static final String SAMPLE_CSV_FILE_PATH = "../prueba/wiki_movie_plots_deduped_CHIQUITO.csv";
	private static final String SAMPLE_CSV_FILE_PATH = "../wiki_movie/wiki_movie_plots_deduped.csv";
	private static final String english_words[]=new String[]{"i","me","my","myself","we","our","ours","ourselves","you","your","yours","yourself","yourselves","he","him","his","himself","she","her","hers","herself","it","its","itself","they","them","their","theirs","themselves","what","which","who","whom","this","that","these","those","am","is","are","was","were","be","been","being","have","has","had","having","do","does","did","doing","a","an","the","and","but","if","or","because","as","until","while","of","at","by","for","with","about","against","between","into","through","during","before","after","above","below","to","from","up","down","in","out","on","off","over","under","again","further","then","once","here","there","when","where","why","how","all","any","both","each","few","more","most","other","some","such","no","nor","not","only","own","same","so","than","too","very","s","t","can","will","just","don","should","now"};


	public IndexSearcher searcherAns, searcherTags;
	public FacetsConfig fconfig;

	IndiceSimple(){
		analyzer = new StandardAnalyzer();
		similarity = new ClassicSimilarity();
	}

	IndiceSimple(Analyzer ana, Similarity simi){
		analyzer=ana;
		similarity=simi;
	}

	public void configurarIndice() throws IOException {
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

		iwc.setSimilarity(similarity);
		// Crear un nuevo indice cada vez que se ejecute
		iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

		// Localizacion del indice
		Directory dir = FSDirectory.open(Paths.get(indexPath));
		FSDirectory taxoDir = FSDirectory.open(Paths.get(facetPath));
		fconfig = new FacetsConfig();
		// Creamos el indice y facetas
		writer = new IndexWriter(dir, iwc);
		taxoWriter= new DirectoryTaxonomyWriter(taxoDir);
	}


	public String DeleteStopWords(String contenido) throws IOException{
		CharArraySet STOP_WORDS_SET=new CharArraySet(0, false);
		STOP_WORDS_SET.add("https");
		STOP_WORDS_SET.add("en.wikipedia.org");
		STOP_WORDS_SET.add("wiki");
		for(int i=0; i<english_words.length; i++)	STOP_WORDS_SET.add(english_words[i]);

		TokenStream stream=new StopFilter(analyzer.tokenStream(null, contenido), STOP_WORDS_SET);
		CharTermAttribute attr = stream.addAttribute(CharTermAttribute.class);

		String devolver=new String();

		stream.reset();
		while(stream.incrementToken()){
			devolver+=attr.toString() + " ";
		}

		//System.out.println(devolver + "\n");
		stream.end();
		stream.close();

		return devolver;
	}


	public void indexarDocumentos(){
		try{
			Reader reader = Files.newBufferedReader(Paths.get(SAMPLE_CSV_FILE_PATH));
			CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);

			Iterator<CSVRecord> csv_it = csvParser.iterator();
			CSVRecord first=csv_it.next();

			String[] campos= new String[first.size()];

			for(int i=0; i<first.size(); i++){
				campos[i]=first.get(i);
			}

			for (CSVRecord csvRecord : csvParser) {
				Document doc = new Document();

				String plot=DeleteStopWords(csvRecord.get(7));

				long movie_number=csvRecord.getRecordNumber();
				movie_number-=1;

				//System.out.println("Indexing... -Movie Number - " + movie_number);

				String todo_todito=new String();
				for(int i=0; i<8; i++){
					todo_todito+=csvRecord.get(i) + " ";
				}
				/*	0 Release Year
					1 Title
					2 Origin/Ethnicity
					3 Director
					4 Cast
					5 Genre
					6 Wiki Page
					7 Plot			*/
			  	fconfig.setMultiValued("Director",true);
				fconfig.setMultiValued("Genre",true);

				doc.add(new LongPoint("Movie Number", movie_number));
				doc.add(new StoredField("Movie Number", movie_number));
				doc.add(new IntPoint(campos[0], Integer.parseInt(csvRecord.get(0))));
				doc.add(new StoredField(campos[0], Integer.parseInt(csvRecord.get(0))));
				doc.add(new TextField(campos[1], csvRecord.get(1), Field.Store.YES));
				doc.add(new TextField(campos[2], csvRecord.get(2), Field.Store.YES));
				doc.add(new TextField(campos[3], csvRecord.get(3), Field.Store.YES));
				doc.add(new TextField(campos[4], csvRecord.get(4), Field.Store.YES));
				doc.add(new TextField(campos[5], csvRecord.get(5), Field.Store.YES));
				doc.add(new StringField(campos[6], csvRecord.get(6), Field.Store.YES));
				doc.add(new TextField(campos[7], csvRecord.get(7), Field.Store.YES));

				doc.add(new TextField("ALL", todo_todito, Field.Store.YES));
				
				//doc.add(new FacetField("Release Year", csvRecord.get(0)));
		     	doc.add(new FacetField(campos[3], csvRecord.get(3)));
				doc.add(new FacetField(campos[5], csvRecord.get(5)));
				doc.add(new FacetField(campos[2], csvRecord.get(2)));


				try {
				//	writer.addDocument(doc);
					writer.addDocument(fconfig.build(taxoWriter,doc));
				} catch (IOException ex) {
					Logger.getLogger(IndiceSimple.class.getName()).log(Level.SEVERE, null, ex);
				}
			}

		}catch(IOException ex){
			Logger.getLogger(IndiceSimple.class.getName()).log(Level.SEVERE, null, ex);
		}

	}


	public void close(){
		try{
			writer.commit();
			writer.close();
			taxoWriter.close();
		} catch (IOException e) {
			System.out.println("Error closing the index.");
		}
	}



	public static void main (String[] args) {
		System.out.println("Creando índice..............");
		// Analizador a utilizar
		Analyzer analyzer = new StandardAnalyzer();
		// Medida de Similitud (modelo de recuperacion) por defecto BM25
		Similarity similarity = new ClassicSimilarity();
		// Llamados al constructor con los parametros
		IndiceSimple baseline = new IndiceSimple(analyzer, similarity);

		// Creamos el indice
		try{
			baseline.configurarIndice();
		}catch(IOException ex){
			Logger.getLogger(IndiceSimple.class.getName()).log(Level.SEVERE, null, ex);
		}
		// Insertar los documentos
		baseline.indexarDocumentos();
		// Cerramos el indice
		baseline.close();
		System.out.println("Hecho!\n\n");
	}


}
