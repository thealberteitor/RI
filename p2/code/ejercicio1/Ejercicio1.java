import java.io.IOException;


import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;


import java.io.*;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import java.util.*;

import java.io.IOException;

public class Ejercicio1{

	private static Map<String, Integer> sortByValue(Map<String, Integer> unsortMap) {
		// 1. Convert Map to List of Map
		List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

		// 2. Sort list with Collections.sort(), provide a custom Comparator
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1,
				Map.Entry<String, Integer> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		// 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for (Map.Entry<String, Integer> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}

	public static void Analyzer(Analyzer analyzer, String name, String contenido) throws IOException{
		System.out.println("*************** " + name + " ***************");
		TokenStream stream = analyzer.tokenStream(null, contenido);
		CharTermAttribute attr = stream.addAttribute(CharTermAttribute.class);
		stream.reset();
		
		Map<String,Integer> palabra_ocurrencias = new HashMap<String, Integer>();
		
		while(stream.incrementToken()){
			String palabra=attr.toString();
			//System.out.println(palabra);
			
			Integer value=palabra_ocurrencias.get(palabra);
			
			if(value==null)	palabra_ocurrencias.put(palabra,1);
			else			palabra_ocurrencias.put(palabra,value+1);
		}

		palabra_ocurrencias=sortByValue(palabra_ocurrencias);		

		int n_mostradas=0;

		for (Map.Entry<String, Integer> pair : palabra_ocurrencias.entrySet()) {
			System.out.print(pair.getKey() + " - " + pair.getValue() + "\t\t");
			n_mostradas++;
			if(n_mostradas>=20)
				break;
		}
		

		System.out.println("\nNúmero de tokens en el fichero: " + palabra_ocurrencias.size() + "\n");

		stream.end();
		stream.close();
	}

    public static void main(String[] args) throws IOException{
        if(args.length<1){
			System.out.println("Debe pasar un directorio a la ejecución del programa.");
			System.exit(0);
		}
		
		String english_words[]=new String[]{"a","about","above","across","after","again","against","all","almost","alone","along","already","also","although","always","among","an","and","another","any","anybody","anyone","anything","anywhere","are","area","areas","around","as","ask","asked","asking","asks","at","away","b","back","backed","backing","backs","be","became","because","become","becomes","been","before","began","behind","being","beings","best","better","between","big","both","but","by","c","came","can","cannot","case","cases","certain","certainly","clear","clearly","come","could","d","did","differ","different","differently","do","does","done","down","down","downed","downing","downs","during","e","each","early","either","end","ended","ending","ends","enough","even","evenly","ever","every","everybody","everyone","everything","everywhere","f","face","faces","fact","facts","far","felt","few","find","finds","first","for","four","from","full","fully","further","furthered","furthering","furthers","g","gave","general","generally","get","gets","give","given","gives","go","going","good","goods","got","great","greater","greatest","group","grouped","grouping","groups","h","had","has","have","having","he","her","here","herself","high","high","high","higher","highest","him","himself","his","how","however","i","if","important","in","interest","interested","interesting","interests","into","is","it","its","itself","j","just","k","keep","keeps","kind","knew","know","known","knows","l","large","largely","last","later","latest","least","less","let","lets","like","likely","long","longer","longest","m","made","make","making","man","many","may","me","member","members","men","might","more","most","mostly","mr","mrs","much","must","my","myself","n","necessary","need","needed","needing","needs","never","new","new","newer","newest","next","no","nobody","non","noone","not","nothing","now","nowhere","number","numbers","o","of","off","often","old","older","oldest","on","once","one","only","open","opened","opening","opens","or","order","ordered","ordering","orders","other","others","our","out","over","p","part","parted","parting","parts","per","perhaps","place","places","point","pointed","pointing","points","possible","present","presented","presenting","presents","problem","problems","put","puts","q","quite","r","rather","really","right","right","room","rooms","s","said","same","saw","say","says","second","seconds","see","seem","seemed","seeming","seems","sees","several","shall","she","should","show","showed","showing","shows","side","sides","since","small","smaller","smallest","so","some","somebody","someone","something","somewhere","state","states","still","still","such","sure","t","take","taken","than","that","the","their","them","then","there","therefore","these","they","thing","things","think","thinks","this","those","though","thought","thoughts","three","through","thus","to","today","together","too","took","toward","turn","turned","turning","turns","two","u","under","until","up","upon","us","use","used","uses","v","very","w","want","wanted","wanting","wants","was","way","ways","we","well","wells","went","were","what","when","where","whether","which","while","who","whole","whose","why","will","with","within","without","work","worked","working","works","would","x","y","year","years","yet","you","young","younger","youngest","your","yours","z"};	
		String spanish_words[]=new String[]{"a","á","al","algo","algunas","algunos","ante","antes","como","con","contra","cual","cuando","de","del","desde","donde","durante","e","el","él","ella","ellas","ellos","en","entre","era","erais","eran","eras","eres","es","esa","esas","ese","eso","esos","esta","estaba","estabais","estaban","estabas","estad","estada","estadas","estado","estados","estamos","estando","estar","estaremos","estará","estarán","estarás","estaré","estaréis","estaría","estaríais","estaríamos","estarían","estarías","estas","este","estemos","esto","estos","estoy","estuve","estuviera","estuvierais","estuvieran","estuvieras","estuvieron","estuviese","estuvieseis","estuviesen","estuvieses","estuvimos","estuviste","estuvisteis","estuviéramos","estuviésemos","estuvo","está","estábamos","estáis","están","estás","esté","estéis","estén","estés","fue","fuera","fuerais","fueran","fueras","fueron","fuese","fueseis","fuesen","fueses","fui","fuimos","fuiste","fuisteis","fuéramos","fuésemos","ha","habida","habidas","habido","habidos","habiendo","habremos","habrá","habrán","habrás","habré","habréis","habría","habríais","habríamos","habrían","habrías","habéis","había","habíais","habíamos","habían","habías","han","has","hasta","hay","haya","hayamos","hayan","hayas","hayáis","he","hemos","hube","hubiera","hubierais","hubieran","hubieras","hubieron","hubiese","hubieseis","hubiesen","hubieses","hubimos","hubiste","hubisteis","hubiéramos","hubiésemos","hubo","la","las","le","les","lo","los","me","mi","mis","mucho","muchos","muy","más","mí","mía","mías","mío","míos","nada","ni","no","nos","nosotras","nosotros","nuestra","nuestras","nuestro","nuestros","o","os","otra","otras","otro","otros","para","pero","poco","por","porque","que","quien","quienes","qué","se","sea","seamos","sean","seas","seremos","será","serán","serás","seré","seréis","sería","seríais","seríamos","serían","serías","seáis","sido","siendo","sin","sobre","sois","somos","son","soy","su","sus","suya","suyas","suyo","suyos","sí","también","tanto","te","tendremos","tendrá","tendrán","tendrás","tendré","tendréis","tendría","tendríais","tendríamos","tendrían","tendrías","tened","tenemos","tenga","tengamos","tengan","tengas","tengo","tengáis","tenida","tenidas","tenido","tenidos","teniendo","tenéis","tenía","teníais","teníamos","tenían","tenías","ti","tiene","tienen","tienes","todo","todos","tu","tus","tuve","tuviera","tuvierais","tuvieran","tuvieras","tuvieron","tuviese","tuvieseis","tuviesen","tuvieses","tuvimos","tuviste","tuvisteis","tuviéramos","tuviésemos","tuvo","tuya","tuyas","tuyo","tuyos","tú","un","una","uno","unos","vosotras","vosotros","vuestra","vuestras","vuestro","vuestros","y","ya","yo"};
		CharArraySet ENGLISH_STOP_WORDS_SET=new CharArraySet(0, false);
		CharArraySet SPANISH_STOP_WORDS_SET=new CharArraySet(0, false);
		CharArraySet EMPTY_WORDS_SET=new CharArraySet(0,false);		
		for(int i=0; i<english_words.length; i++)	ENGLISH_STOP_WORDS_SET.add(english_words[i]);
		for(int i=0; i<spanish_words.length; i++)	SPANISH_STOP_WORDS_SET.add(spanish_words[i]);
		
		
		//Leemos el directorio y los archivos que cuelgan de el
		File directory = new File(args[0]);
		String[] ficheros = directory.list();

		//Creamos una instancia de Tika con la configuracion por defecto
        Tika tika = new Tika();
        tika.setMaxStringLength(-1);

        Metadata metadata = new Metadata();

        System.out.println("\n\n\n");

        for(String f : ficheros){
			File file = new File("./"+args[0]+"/"+f);

			System.out.println("ARCHIVO PARSEADO: " + file.getName());

			//Parseamos
			tika.parse(file, metadata);
			String contenido = new String();

			try{
				contenido = tika.parseToString(file);
			}catch (Exception e){ 
				System.out.println("No se puede parsear...\n\n");
				continue;
			}
			
			
			Analyzer(new WhitespaceAnalyzer(), "WhitespaceAnalyzer", contenido);
			Analyzer(new StandardAnalyzer(), "StandardAnalyzer", contenido);
			Analyzer(new SimpleAnalyzer(), "SimpleAnalyzer", contenido);
			//Analyzer(new KeywordAnalyzer(), "KeywordAnalyzer", contenido);	//Convierte todo el texto en un único Token
			Analyzer(new StopAnalyzer(EMPTY_WORDS_SET), "StopAnalyzer EMPTY_WORDS_SET", contenido);
			Analyzer(new StopAnalyzer(SPANISH_STOP_WORDS_SET), "StopAnalyzer SPANISH_STOP_WORDS_SET", contenido);
			Analyzer(new SpanishAnalyzer(), "SpanishAnalyzer", contenido);
			System.out.println("\n\n");
        }
    }
    
}
