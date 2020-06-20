import java.io.*;
import java.util.List;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.Iterator;

import java.io.BufferedReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.Query;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.ParseException;

import org.apache.lucene.document.*;
import org.apache.lucene.search.IndexSearcher;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexReader;
import java.util.Scanner;
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


import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TotalHits;
import org.apache.lucene.index.DirectoryReader;
import java.nio.charset.StandardCharsets;


import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PhraseQuery;

import org.apache.lucene.search.TermQuery;
import org.apache.lucene.index.Term;


import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.DrillDownQuery;
import org.apache.lucene.facet.FacetsCollector;
import org.apache.lucene.facet.Facets;
import org.apache.lucene.facet.FacetResult;
import org.apache.lucene.facet.LabelAndValue;
import org.apache.lucene.facet.taxonomy.TaxonomyReader;
import org.apache.lucene.facet.taxonomy.FastTaxonomyFacetCounts;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyReader;

import org.apache.lucene.facet.taxonomy.TaxonomyFacets;
import org.apache.lucene.facet.taxonomy.IntTaxonomyFacets;


import org.apache.lucene.queryparser.complexPhrase.ComplexPhraseQueryParser;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.core.StopAnalyzer;



public class Searcher_interfaz{

    //Ubicación del indice
    String indexPath ;
    String facetsPath;
    IndexSearcher searcher;
    TaxonomyReader taxoReader;
    FacetsCollector fc;
    int cuantos;
    int cuantos_2;
    FacetsConfig fconfig;
    DrillDownQuery ddq;
    TopDocs td2;
    String[] matrix;
    String[] pa_mi_jeje;

	private static final String english_words[]=new String[]{"i","me","my","myself","we","our","ours","ourselves","you","your","yours","yourself","yourselves","he","him","his","himself","she","her","hers", "herself","it","its","itself","they","them","their","theirs","themselves","what","which","who","whom","this","that","these","those","am","is", "are","was","were","be","been","being","have","has","had","having","do","does","did","doing","a","an","the","and","but","if","or","because", "as","until","while","of","at","by","for","with","about","against","between","into","through","during","before","after","above","below","to", "from","up","down","in","out","on","off","over","under","again","further","then","once","here","there","when","where","why","how","all","any", "both","each","few","more","most","other","some","such","no","nor","not","only","own","same","so","than","too","very","s","t","can","will","just", "don","should","now"};

	private static CharArraySet STOP_WORDS_SET=new CharArraySet(0, false);
    
    Searcher_interfaz(){
        indexPath= "./index" ;
        facetsPath="./facet";
        fc= null;
        searcher=null;
        cuantos=10;
        cuantos_2=3;
        ddq=null;
        td2=null;

        for(int i=0; i<english_words.length; i++)	STOP_WORDS_SET.add(english_words[i]);        
    }


	public String DeleteStopWords(String contenido) throws IOException{		
		
		Analyzer analyzer= new StopAnalyzer(STOP_WORDS_SET);
		TokenStream stream=analyzer.tokenStream(null, contenido);
		CharTermAttribute attr = stream.addAttribute(CharTermAttribute.class);
		
		String devolver=new String();
		
		stream.reset();
		while(stream.incrementToken()){
			devolver+=attr.toString() + " ";
		}
		
		stream.end();
		stream.close();
		
		return devolver;
	}
	
	public String[] getPaMiJeje(){
		return pa_mi_jeje;	
	}

    public TopDocs IndexBooleanQuery(String campo, Analyzer analyzer, String consulta) throws IOException {

        //nueva forma para parsear la consulta
        QueryParser parser = new QueryParser(campo, analyzer);
        
        TopDocs tdc=null;
        
        String[] todas = consulta.split(" ");
        
        BooleanQuery.Builder bqbuilder = new BooleanQuery.Builder();

        Query q1 = null;

        for(int i=0; i<todas.length; i++){
            
            try{
                q1 = parser.parse(todas[i]);
            }catch(org.apache.lucene.queryparser.classic.ParseException e){}

            BooleanClause bc1 = new BooleanClause(q1, BooleanClause.Occur.MUST);
            bqbuilder.add(bc1);
        }

        BooleanQuery bq = bqbuilder.build();
        TopDocs tdocs = searcher.search(bq,cuantos);
        tdc = FacetsCollector.search(searcher,bq,10,fc);

        showFacets(tdc,true,q1);

        return tdocs;
    }



    public TopDocs IndexPhraseQuery(String campo, Analyzer analyzer, String consulta) throws IOException{

        PhraseQuery.Builder pqbuilder = new PhraseQuery.Builder();
        
        ComplexPhraseQueryParser phraseparser = new ComplexPhraseQueryParser(campo, analyzer);
        
        TopDocs tdc = null;
        
        consulta=consulta.toLowerCase();
        
        String[] all = consulta.split(" ");

        for(int i=0; i<all.length; i++){
            pqbuilder.add(new Term(campo, all[i]), i);
        }
        
        PhraseQuery pq = pqbuilder.build();

        TopDocs pqdocs = searcher.search(pq,cuantos);
        
        tdc = FacetsCollector.search(searcher,pq,10,fc);

        showFacets(tdc,true, pq);
        
        return pqdocs;
    }



    public TopDocs IndexQueryParser(String campo , Analyzer analyzer, String consulta) throws IOException {

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in , StandardCharsets.UTF_8));

        QueryParser parser = new QueryParser(campo, analyzer);
        
        String nueva_consulta=DeleteStopWords(consulta);
	
		if(nueva_consulta.isEmpty())
			nueva_consulta=consulta;


        Query query =null ;
        TopDocs docs = null;
        TopDocs tdc = null;
    
	    try{
	        query = parser.parse(nueva_consulta);
	        docs = searcher.search(query, cuantos);
	        tdc = FacetsCollector.search(searcher,query,10,fc);

	    }catch (org.apache.lucene.queryparser.classic.ParseException e){
	        System.out.println("Error en cadena consulta. ");
	    }
	    
	    showFacets(tdc,true,query);
        return docs;
    }
    
    public void showFacets(TopDocs tdc, boolean mostrarfacetas, Query q1){
        Facets facetas = null;
        ddq= new DrillDownQuery(fconfig,q1);
        matrix = new String[3*cuantos_2];
        pa_mi_jeje = new String[3*cuantos_2];
        int j=0;

        try{

            FacetsCollector fc1 = new FacetsCollector();
            td2 = FacetsCollector.search(searcher,ddq,10,fc1);
            Facets fcCount2 = new FastTaxonomyFacetCounts(taxoReader,fconfig,fc1);
            List<FacetResult> allDims = fcCount2.getAllDims(100);

            if(mostrarfacetas){

                //Para cada faceta Director/Genero/Origen mostramos el valor de la etiqueta y su numero de ocurrencias
                for( FacetResult fr : allDims){
                    int cont=0;
                    //Almacenamos cada etiqueta en un vector de 3*cuantos_2 casillas para guardar todas las que mostramos
                    for(LabelAndValue lv : fr.labelValues){
                        if(cont < cuantos_2){
                            matrix[j]=new String( fr.dim+ "::#->"+ lv.label + "");
                            pa_mi_jeje[j]=new String(lv.label + " ("+ lv.value+")");
                        }else
                            break;
    						cont++;
                        j++;
                     }
                }
            }

        }catch(IOException e){

        }
    }
    
    public String filterSearchByFacets(String escogidas){

		String[] split_escogidas=escogidas.split(",");
		int[] resu=new int[split_escogidas.length];
		
		for(int i=0; i<split_escogidas.length; i++){
			resu[i]=Integer.parseInt(split_escogidas[i]);
			//System.out.println("#"+resu[i]+"#");
		}

        FacetsCollector fc1 = new FacetsCollector();
        
		IndexReader reader = null;
		Directory dir = null;
		FSDirectory taxoDir = null;
	
		Analyzer analyzer = new StandardAnalyzer();
		Similarity similarity = new ClassicSimilarity();
		
		DrillDownQuery ddq2=ddq.clone();


        try{
        	
            /*for(int num=0 ; num < matrix.length; num++){
            	System.out.println("\n[ "+ num +" ]"+ matrix[num]);
            }*/

			//Directorio donde se encuentra el índice
			dir = FSDirectory.open(Paths.get(indexPath));
			taxoDir = FSDirectory.open(Paths.get(facetsPath));

			//lo abrimos para lectura
			reader = DirectoryReader.open(dir);
			fconfig = new FacetsConfig();

			//Creamos el objeto IndexSearcher usando el IndexReader anterior
			searcher = new IndexSearcher(reader);
			taxoReader = new DirectoryTaxonomyReader(taxoDir);
			searcher.setSimilarity(similarity);

			TopDocs documentos = null;
			fc = new FacetsCollector();
			

            for(int i=0;i<resu.length;i++){
                //resu[i]= Integer.parseInt(filtros[i]);
                /*int iend = matrix[resu[i]].indexOf(">"); //this finds the first occurrence of ">"
                String faceta = matrix[resu[i]].substring(iend+1,matrix[resu[i]].length());

                //cada dimension es de tipo AND con las otras, y las etiquetas pueden ser AND o OR
                //en este caso son AND
                //para que sean OR habria que introducirlas en la misma llamada a .add(facet,etiq1,etiq2,etiq3)
                
                if((resu[i])<3)
                    ddq2.add("Director",faceta);
                else if((resu[i])<6)
                    ddq2.add("Genre",faceta);
                else if((resu[i])<9)
                    ddq2.add("Origin/Ethnicity",faceta);
                */    
                //facet_n[i]= Integer.parseInt(filtros[i]);
                int iend = matrix[resu[i]].indexOf(">"); //this finds the first occurrence of ">"
                int istart = matrix[resu[i]].indexOf(":");
                String faceta = matrix[resu[i]].substring(iend+1,matrix[resu[i]].length());
                String categoria = matrix[resu[i]].substring(0,istart);


                ddq2.add(categoria,faceta);

            }

			System.out.println("\n\n....Nueva busqueda [ " + ddq2.toString()+ " ]\n\n");

			fc1 = new FacetsCollector();
			td2 = FacetsCollector.search(searcher,ddq2,10,fc1);
			
			
			Facets fcCount2 = new FastTaxonomyFacetCounts(taxoReader,fconfig,fc1);
			List<FacetResult> allDims = fcCount2.getAllDims(100);
			
			
			String resultado = QueryShowDocuments(td2);

			reader.close();

			if(!resultado.isEmpty())	return resultado;
			else						return "No se han encontrado resultados :(";
			
		}catch(IOException e){
			try{
				reader.close();
			} catch(IOException e1){
				e1.printStackTrace();
			}
			e.printStackTrace();
		}

		return "nada";

     }
    


    public String QueryShowDocuments(TopDocs docs) throws IOException{
    	String resultado=new String();
		resultado+=docs.totalHits +  " documentos encontrados.\n\n";
        ScoreDoc[] hits = docs.scoreDocs;

        for(int j=0; j<hits.length; j++){
            
            Document doc = searcher.doc(hits[j].doc);
            String name = doc.get("Title");
            String cuerpo  = doc.get("Plot"); //sustituir por lo que sea
            Integer id = doc.getField("Movie Number").numericValue().intValue();
            Integer year = doc.getField("Release Year").numericValue().intValue();
            String director = doc.get("Director");
            String genero = doc.get("Genre");
            String origin = doc.get("Origin/Ethnicity");

            if(cuerpo.length()>200){
            	cuerpo = cuerpo.substring(0, 200);
            	cuerpo += " [...]";
            }
            
            resultado+="---------------------\n";
            //resultado+="Movie Number: " + id + "\n";
            //resultado+="Release Year: " + year + "\n";
            resultado+="Title: " + name + "\n";
            resultado+="Director: " + director + "\n";
            resultado+="Genre: " + genero + "\n";
            resultado+="Origin/Ethnicity: " + origin + "\n";
            //resultado+="Plot: " + cuerpo + "\n";
            

            resultado+="---------------------\n\n";
        }
        
        return resultado;
    }


	public String indexSearchInterfaz(String consulta, String type){
		IndexReader reader = null;
		Directory dir = null;
		FSDirectory taxoDir = null;
		
		Analyzer analyzer = new StandardAnalyzer();
		Similarity similarity = new ClassicSimilarity();
		
		try{
			//Directorio donde se encuentra el índice
			dir = FSDirectory.open(Paths.get(indexPath));
			taxoDir = FSDirectory.open(Paths.get(facetsPath));

			//lo abrimos para lectura
			reader = DirectoryReader.open(dir);
			fconfig = new FacetsConfig();

			//Creamos el objeto IndexSearcher usando el IndexReader anterior
			searcher = new IndexSearcher(reader);
			taxoReader = new DirectoryTaxonomyReader(taxoDir);
			searcher.setSimilarity(similarity);

			TopDocs documentos = null;
			fc = new FacetsCollector();
			
			switch(type){
				case "AND-BooleanQuery":
					documentos = IndexBooleanQuery("ALL", analyzer, consulta);
					break;
				case "PhraseQuery":
					documentos = IndexPhraseQuery("ALL", analyzer, consulta);
					break;
				case "PalabraQuery":
					documentos = IndexQueryParser("ALL", analyzer, consulta);
					break;
				default:
					return "Que ha pasao aqui\n";
			}
			
			String resultado = QueryShowDocuments(documentos);

			reader.close();

			if(!resultado.isEmpty())	return resultado;
			else						return "No se han encontrado resultados :(";
			
		}catch(IOException e){
            try{
                reader.close();
            } catch(IOException e1){
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        
        return "nada";
	}



    public static void main(String[] args){
        Searcher_interfaz proIndex = new Searcher_interfaz();
        Analyzer analyzer = new StandardAnalyzer();
        Similarity similarity = new ClassicSimilarity();
        String result= proIndex.indexSearchInterfaz("hola", "AND-BooleanQuery");
        System.out.println(result);
        
        String prueba="0,3,";
        
        String result2= proIndex.filterSearchByFacets(prueba);
        
        System.out.println(result2);
        
    }
}
