//package IRsystem;
import java.io.*;
import java.util.List;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.Reader;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.Iterator;

import java.io.BufferedReader;
import com.carrotsearch.hppc.IntIntScatterMap;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.Query;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.ParseException;

import org.apache.lucene.document.*;
import org.apache.lucene.search.IndexSearcher;

//import org.apache.commons.csv.CSVFormat;
//import org.apache.commons.csv.CSVParser;
//import org.apache.commons.csv.CSVRecord;

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
import org.apache.lucene.index.DirectoryReader;
import java.nio.charset.StandardCharsets;


import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.MatchAllDocsQuery ;


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



public class Searcher{

    //Ubicación del indice
    String indexPath ;
    String facetsPath;
    IndexSearcher searcher;
    TaxonomyReader taxoReader;
    FacetsCollector fc;
    int cuantos;
    FacetsConfig fconfig;

	private static final String english_words[]=new String[]{"i","me","my","myself","we","our","ours","ourselves","you","your","yours","yourself","yourselves","he","him","his","himself","she","her","hers", "herself","it","its","itself","they","them","their","theirs","themselves","what","which","who","whom","this","that","these","those","am","is", "are","was","were","be","been","being","have","has","had","having","do","does","did","doing","a","an","the","and","but","if","or","because", "as","until","while","of","at","by","for","with","about","against","between","into","through","during","before","after","above","below","to", "from","up","down","in","out","on","off","over","under","again","further","then","once","here","there","when","where","why","how","all","any", "both","each","few","more","most","other","some","such","no","nor","not","only","own","same","so","than","too","very","s","t","can","will","just", "don","should","now"};

	private static CharArraySet STOP_WORDS_SET=new CharArraySet(0, false);

    Searcher(){
        indexPath= "./index" ;
        facetsPath="./facet";
        fc= null;
        searcher=null;
        cuantos=5;

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

		System.out.println("CONSULTA: " + devolver + "\n");
		stream.end();
		stream.close();

		return devolver;
	}



    public TopDocs IndexBooleanQuery(String campo, Analyzer analyzer) throws IOException {

        //nueva forma para parsear la consulta
        QueryParser parser = new QueryParser(campo, analyzer);

        System.out.println("Introduzca las palabras que deban contener los documentos: ");
        TopDocs tdc=null;
        Scanner sc1 = new Scanner(System.in);
        String frase0 = sc1.nextLine();

        //frase0=DeleteStopWords(frase0);

        String[] todas = frase0.split(" ");




        BooleanQuery.Builder bqbuilder = new BooleanQuery.Builder();

        Query q1 = null;

        for(int i=0; i<todas.length; i++){

            //Query q1 = new TermQuery(new Term(campo, todas[i]));
            try{
                q1 = parser.parse(todas[i]);
            }catch(org.apache.lucene.queryparser.classic.ParseException e){}

            BooleanClause bc1 = new BooleanClause(q1, BooleanClause.Occur.MUST);
            bqbuilder.add(bc1);
        }

        BooleanQuery bq = bqbuilder.build();
        TopDocs tdocs = searcher.search(bq,cuantos);
        tdc = FacetsCollector.search(searcher,bq,10,fc);

        System.out.println("BooleanQuery - Hay " + tdocs.totalHits + " documentos que contengan esas palabras");

        System.out.println("¿Desea mostrar las categorías? (Facetas) si/no");
        String res =  sc1.nextLine();
        if(res.equals("si"))
            showFacets(tdc,true,q1);

        return tdocs;
    }



    public TopDocs IndexPhraseQuery(String campo, Analyzer analyzer) throws IOException{

        PhraseQuery.Builder pqbuilder = new PhraseQuery.Builder();

        ComplexPhraseQueryParser phraseparser = new ComplexPhraseQueryParser(campo, analyzer);
        TopDocs tdc = null;

        System.out.println("Introduzca la frase seguida: ");

        Scanner sc2 = new Scanner(System.in);
        String frase = sc2.nextLine();
        frase=frase.toLowerCase();
        String[] all = frase.split(" ");

        for(int i=0; i<all.length; i++){
            pqbuilder.add(new Term(campo, all[i]), i);
        }

        PhraseQuery pq = pqbuilder.build();

        TopDocs pqdocs = searcher.search(pq,cuantos);
        tdc = FacetsCollector.search(searcher,pq,10,fc);

        System.out.println("Coincidencias con la frase: " + pqdocs.totalHits+" docs");

        System.out.println("¿Desea mostrar las categorías? (Facetas) si/no");
        String res =  sc2.nextLine();
        if(res.equals("si"))
            showFacets(tdc,true, pq);
        return pqdocs;
    }



    public TopDocs IndexQueryParser(String campo , Analyzer analyzer) throws IOException {

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in , StandardCharsets.UTF_8));

        QueryParser parser = new QueryParser(campo, analyzer);

        System.out.println("Introduzca consulta -> ");

        String line = in.readLine();

        String new_line=DeleteStopWords(line);

        if(!new_line.equals(""))	line=new_line;

        Query query =null ;
        TopDocs docs = null;
        TopDocs tdc = null;

        try{
            query = parser.parse(line);
            docs = searcher.search(query, cuantos);
            tdc = FacetsCollector.search(searcher,query,10,fc);

        }catch (org.apache.lucene.queryparser.classic.ParseException e){
            System.out.println("Error en cadena consulta. ");
        }

        System.out.println(docs.totalHits + " documentos encontrados");

        System.out.println("¿Desea mostrar las categorías? (Facetas) si/no");
        String res =  in.readLine();
        if(res.equals("si"))
            showFacets(tdc,true,query);
        return docs;
    }

    public void showFacets(TopDocs tdc, boolean mostrarfacetas, Query q1){
        Facets facetas = null;
        DrillDownQuery ddq= new DrillDownQuery(fconfig,q1);
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in , StandardCharsets.UTF_8));
        String [] matrix =  new String[3*cuantos];
        int j=0;

        try{

            FacetsCollector fc1 = new FacetsCollector();
            TopDocs td2 = FacetsCollector.search(searcher,ddq,10,fc1);
            Facets fcCount2 = new FastTaxonomyFacetCounts(taxoReader,fconfig,fc1);
            List<FacetResult> allDims = fcCount2.getAllDims(100);

            if(mostrarfacetas){

                System.out.println("Categorias : " + allDims.size()+ ", mostrando las "+ cuantos + " más relevantes de cada una...\n");

                //Para cada faceta Director/Genero/Origen mostramos el valor de la etiqueta y su numero de ocurrencias
                for( FacetResult fr : allDims){
                    System.out.println("\n\nCATEGORIA -------------" + fr.dim+ "-------\n");
                    int cont=0;
                    //Almacenamos cada etiqueta en un vector de 3*cuantos casillas para guardar todas las que mostramos
                    for(LabelAndValue lv : fr.labelValues){
                        if(cont < cuantos){
                            matrix[j]=new String( "\n"+ fr.dim+ "::#->"+ lv.label + "");
                            System.out.println(lv.label + "::#-> "+ lv.value);
                        }else
                            break;
    						cont++;
                        j++;
                     }

                }
            }


            System.out.println("\n¿Desea filtrar la busqueda por categorias? (Facetas) si/no");
            String res =  in.readLine();

            if(res.equals("si")){
                td2= filterFacets(ddq,td2,cuantos, matrix);
                QueryShowDocuments(td2);

            }

        }catch(IOException e){

        }


    }

    public TopDocs filterFacets(DrillDownQuery ddq, TopDocs td2,int cuantos, String [] matrix){


        String res;
        FacetsCollector fc1 = new FacetsCollector();
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in , StandardCharsets.UTF_8));


        try{


            System.out.println("\n\n....Filtramos query[ " + ddq.toString()+ " ] a la que aplicaremos DrillDownQuery");
            System.out.println("Total hits = "+ td2.totalHits);
            System.out.println("\nFiltrar por: ");//hacer bien la matriz filx col 3x5

            for(int num=0 ; num < cuantos*3; num++){
                    System.out.println("\n[ "+ num +" ]"+ matrix[num]);

                }


            System.out.println("Introduzca el/los filtro/s");
            res =  in.readLine();
            String[] filtros = res.split(" ");
            int[] resu= new int[filtros.length];


            for(int i=0;i<resu.length;i++){
                resu[i]= Integer.parseInt(filtros[i]);
                int iend = matrix[resu[i]].indexOf(">"); //this finds the first occurrence of ">"
                String faceta = matrix[resu[i]].substring(iend+1,matrix[resu[i]].length());

                //cada dimension es de tipo AND con las otras, y las etiquetas pueden ser AND o OR
                //en este caso son AND
                //para que sean OR habria que introducirlas en la misma llamada a .add(facet,etiq1,etiq2,etiq3)
                if((resu[i]-1)<5)
                    ddq.add("Director",faceta);
                else if((resu[i]-1)< 10)
                    ddq.add("Genre",faceta);
                else if((resu[i]-1)<15)
                    ddq.add("Origin/Ethnicity",faceta);

            }

            System.out.println("\n\n....Nueva busqueda [ " + ddq.toString()+ " ]");

             fc1 = new FacetsCollector();
             td2 = FacetsCollector.search(searcher,ddq,10,fc1);
             Facets fcCount2 = new FastTaxonomyFacetCounts(taxoReader,fconfig,fc1);
             List<FacetResult> allDims = fcCount2.getAllDims(100);


             System.out.println("Total hits= "+ td2.totalHits);


         }catch(IOException e){

         }

         return td2;

     }

    public TopDocs IndexYearQuery(Analyzer analyzer){

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in , StandardCharsets.UTF_8));

        QueryParser parser = new QueryParser("Release Year", analyzer);
        //QueryParser parser = new QueryParser("Title", analyzer);
        System.out.println("Introduzca el año -> ");
        int year = 0;
        //  Scanner sc = new Scanner(System.in);
        //  String  year = in.readLine();
        //int year = sc.nextInt();
        try{
            year = Integer.parseInt(in.readLine());
        }catch(IOException e){}

        Query query = null ;
        TopDocs docs = null;
        TopDocs tdc = null;
        try{
            query = IntPoint.newExactQuery("Release Year", year) ;
            docs = searcher.search(query, cuantos);
            tdc = FacetsCollector.search(searcher,query,10,fc);

        }catch(IOException e){
            System.out.println("Error en cadena consulta. ");

        }

        System.out.println(docs.totalHits + " documentos encontrados");
        return docs;
    }

	public TopDocs IndexRangeYearQuery(Analyzer analyzer){
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in , StandardCharsets.UTF_8));

        QueryParser parser = new QueryParser("Release Year", analyzer);
        //QueryParser parser = new QueryParser("Title", analyzer);
        System.out.println("Introduzca el primer año -> ");
        int year1 = 0;
        try{
            year1 = Integer.parseInt(in.readLine());
        }catch(IOException e){}

        System.out.println("Introduzca el segundo año -> ");
        int year2 = 0;
        try{
            year2 = Integer.parseInt(in.readLine());
        }catch(IOException e){}

		Query query = null ;
		TopDocs docs = null;
		TopDocs tdc = null;
		try{
			query = IntPoint.newRangeQuery("Release Year", year1, year2) ;
			docs = searcher.search(query, cuantos);
			tdc = FacetsCollector.search(searcher,query,10,fc);

		}catch(IOException e){
			System.out.println("Error en cadena consulta. ");

		}

		System.out.println(docs.totalHits + " documentos encontrados");
		return docs;



	}


    public TopDocs IndexBooleanQueryDicGen(Analyzer analyzer) throws IOException{

        QueryParser parserGenre = new QueryParser("Genre", analyzer);
        QueryParser parserDirector = new QueryParser("Director", analyzer);


        //Genre
        System.out.println("Introduzca el género: ");
        Scanner sc1 = new Scanner(System.in);
        String genre = sc1.nextLine();


        //Director
        System.out.println("Introduzca el director: ");
        Scanner sc2 = new Scanner(System.in);
        String direct = sc2.nextLine();

        BooleanQuery.Builder bqbuilder = new BooleanQuery.Builder();

        Query q1=null, q2=null;
        try{
            q1 = parserGenre.parse(genre);
            q2 = parserDirector.parse(direct);

        }catch (org.apache.lucene.queryparser.classic.ParseException e){
            System.out.println("Error en cadena consulta. ");
        }

        BooleanClause bc1 = new BooleanClause(q1, BooleanClause.Occur.MUST);
        BooleanClause bc2 = new BooleanClause(q2, BooleanClause.Occur.MUST);

        bqbuilder.add(bc1);
        bqbuilder.add(bc2);

        BooleanQuery bq = bqbuilder.build();

        TopDocs tdocs = searcher.search(bq,cuantos);

        System.out.println("BooleanQuery - Hay " + tdocs.totalHits+" documentos que contengan esas palabras en Director y Genre");
        return tdocs;

    }


    public TopDocs IndexBooleanQueryMulti(Analyzer analyzer, String[] campos) throws IOException{

		int total=campos.length;
		QueryParser[] parser=new QueryParser[total];
		String[] values=new String[total];

		for(int i=0; i<total; i++){
			parser[i]=new QueryParser(campos[i], analyzer);
			System.out.println("Introduzca el " + campos[i] + ": ");
			Scanner sc1 = new Scanner(System.in);
			values[i] = sc1.nextLine();
		}

		BooleanQuery.Builder bqbuilder = new BooleanQuery.Builder();

		Query[] q=new Query[total];
		try{
			for(int i=0; i<total; i++){
				if(campos[i].equals("Release Year"))
					q[i] = IntPoint.newExactQuery("Release Year", Integer.parseInt(values[i]));
				else
					q[i]=parser[i].parse(values[i]);
			}

		}catch (org.apache.lucene.queryparser.classic.ParseException e){
			System.out.println("Error en cadena consulta. ");
		}

		BooleanClause[] bc=new BooleanClause[total];
		for(int i=0; i<total; i++){
			bc[i]=new BooleanClause(q[i], BooleanClause.Occur.MUST);
			bqbuilder.add(bc[i]);
		}

		BooleanQuery bq = bqbuilder.build();

		TopDocs tdocs = searcher.search(bq,cuantos);

		System.out.println("BooleanQuery - Hay " + tdocs.totalHits+" documentos que contengan esas palabras.");
		return tdocs;

    }



    public void QueryShowDocuments(TopDocs docs) throws IOException{

        ScoreDoc[] hits = docs.scoreDocs;

        for(int j=0; j<hits.length; j++){

            Document doc = searcher.doc(hits[j].doc);
            String name = doc.get("Title");
            String cuerpo  = doc.get("Plot"); //sustituir por lo que sea
            Integer id = doc.getField("Movie Number").numericValue().intValue();
            Integer year = doc.getField("Release Year").numericValue().intValue();
            String genero = doc.get("Genre");
            String director = doc.get("Director");
            String origin = doc.get("Origin/Ethnicity");


            if(cuerpo.length()>200){
            	cuerpo = cuerpo.substring(0, 200);
            	cuerpo += " [...]";
            }

            System.out.println("---------------------");
            System.out.println("Movie Number: " + id);
            System.out.println("Release Year: " + year);
            System.out.println("Title: " + name);
            System.out.println("Genre: " + genero);
            System.out.println("Director: " + director);
            System.out.println("Origin/Ethnicity: " + origin);




            System.out.println("Plot: " + cuerpo);
            System.out.println("---------------------\n");
        }


    }





    public  void indexSearch(Analyzer analyzer, Similarity similarity) {
        IndexReader reader = null;
        Directory dir = null;
        FSDirectory taxoDir = null;
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
            String mostrar = new String("NO");
            //int cuantos = 5; //luego meter unos 100, esto es prueba

            String salir = new String("si");
            TopDocs documentos = null;
            fc = new FacetsCollector();
            while(salir.equals("si") ){

            System.out.println("Introduzca el campo sobre el que realizar la busqueda: \n [0] -> Por defecto, sobre todos los campos \n [1] -> Introducir manualmente \n [2] -> Salir \n");
            Scanner sc = new Scanner(System.in);
            int valor = sc.nextInt();

            switch(valor){
                case 0:
                    System.out.println("Introduzca el tipo de  consulta que desea realizar: \n [0] -> AND-BooleanQuery \n [1] -> PhraseQuery \n [2] -> PalabraQuery \n");
                    sc = new Scanner(System.in);
                    valor = sc.nextInt();

                    switch(valor){
                        case 0:
                            documentos = IndexBooleanQuery("ALL", analyzer);
                            break;

                        case 1:
                            documentos =IndexPhraseQuery("ALL", analyzer);
                            break;

                        case 2:
                            documentos= IndexQueryParser("ALL", analyzer);
                            break;

                        default:
                            System.out.println("Error");
                            break;
                    }

                    break;

                case 1:
                	System.out.println("Campos disponibles: ( Release Year / Title / Director / Genre / Plot )");
                	System.out.println("¿Sobre cuántos campos quiere realizar la consulta? [0] Uno, [1] Varios-> ");
                	sc = new Scanner(System.in);
                    valor = sc.nextInt();

                	if(valor==0){
				        String line =  new String();
				        System.out.println("Introduzca el campo: -> ");
				        BufferedReader in = new BufferedReader(new InputStreamReader(System.in , StandardCharsets.UTF_8));
				        line = in.readLine();

				        if(!line.equals("Release Year") && !line.equals("Title") && !line.equals("Director") && !line.equals("Genre") && !line.equals("Plot")  ){
				            System.out.println("El campo no coincide con ninguno de los anteriores.");
				            break;
				        }
				        if(line.equals("Release Year")){
				        	System.out.println("Introduzca el tipo de  consulta que desea realizar: \n [0] -> Año exacto \n [1] -> Intervalo de años \n");
		                	sc = new Scanner(System.in);
		                	valor = sc.nextInt();

		                	switch(valor){
		                    	case 0:
		                        	documentos = IndexYearQuery(analyzer);
		                        	break;

		                    	case 1:
		                        	documentos = IndexRangeYearQuery(analyzer);
		                        	break;

		                    	default:
		                        	System.out.println("Error");
		                        	break;
		                	}
				        }else{

				                System.out.println("Introduzca el tipo de  consulta que desea realizar: \n [0] -> AND-BooleanQuery \n [1] -> PhraseQuery \n [2] -> PalabraQuery \n");
				                sc = new Scanner(System.in);
				                valor = sc.nextInt();

				                switch(valor){
				                    case 0:
				                        documentos = IndexBooleanQuery(line, analyzer);
				                        break;

				                    case 1:
				                        documentos =IndexPhraseQuery(line, analyzer);
				                        break;

				                    case 2:
				                        documentos= IndexQueryParser(line, analyzer);
				                        break;

				                    default:
				                        System.out.println("Error: Opcion no valida");
				                        break;
				                }
				            }
				    	break;
				    }else if(valor==1){
						System.out.println("Introduzca los campos separados por comas: (por ejemplo: Genre,Release Year,Director) -> ");
						BufferedReader in = new BufferedReader(new InputStreamReader(System.in , StandardCharsets.UTF_8));
				        String line = in.readLine();
						String[] campos = line.split(",");
						for(int i=0; i<campos.length; i++)	System.out.println(campos[i]);
						documentos =IndexBooleanQueryMulti(analyzer,campos);
						break;
				    }else{
				    	System.out.println("Demasiados campos.\n");
				    	break;
				    }
				case 2:
					System.out.println("Adiós!\n");
					return;
                default:
                    System.out.println("Error: Opcion no valida");
                    break;
            }


                ScoreDoc[] hits = documentos.scoreDocs;

                if( hits.length >0){
                    System.out.println("¿Desea mostrar los documentos relacionados?");
                    mostrar =  (new Scanner(System.in)).nextLine().toLowerCase();

                    if(mostrar.equals("si")){
                        QueryShowDocuments(documentos);
                    }
                }else
                    System.out.println("Lo siento, no hay resultados para mostrar");


                System.out.println("---------------------------------------------------------------------------------------------");

                System.out.println("¿Desea realizar otra consulta? Introduzca 'Si' para continuar, cualquier otra tecla para salir");
                salir =  (new Scanner(System.in)).nextLine().toLowerCase();

                System.out.println("---------------------------------------------------------------------------------------------");

            }
            reader.close();

        }catch(IOException e){
            try{
                reader.close();
            } catch(IOException e1){
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    //MAIN
    public static void main(String[] args){
        Searcher proIndex = new Searcher();
        Analyzer analyzer = new StandardAnalyzer();
        Similarity similarity = new ClassicSimilarity();
        proIndex.indexSearch(analyzer, similarity);
    }
}
