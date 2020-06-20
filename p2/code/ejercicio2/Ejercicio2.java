import java.io.*;
import java.util.*;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;
import org.apache.lucene.analysis.ngram.NGramTokenFilter;
import org.apache.lucene.analysis.commongrams.CommonGramsFilter;
import org.apache.lucene.analysis.synonym.SynonymFilter;


public class Ejercicio2{

	public static void PrintTokens(TokenStream stream, String text) throws IOException{
		System.out.println("************* " + text + " *************");
		CharTermAttribute attr = stream.addAttribute(CharTermAttribute.class);
		
		stream.reset();
		while(stream.incrementToken()){
			System.out.print(attr.toString() + " ");
		}
		
		System.out.println("\n");
		stream.end();
		stream.close();
	}


	public static void main(String[] args) throws IOException{
		String spanish_words[]=new String[]{"a","á","al","algo","algunas","algunos","ante","antes","como","con","contra","cual","cuando","de","del","desde","donde","durante","e","el","él","ella","ellas","ellos","en","entre","era","erais","eran","eras","eres","es","esa","esas","ese","eso","esos","esta","estaba","estabais","estaban","estabas","estad","estada","estadas","estado","estados","estamos","estando","estar","estaremos","estará","estarán","estarás","estaré","estaréis","estaría","estaríais","estaríamos","estarían","estarías","estas","este","estemos","esto","estos","estoy","estuve","estuviera","estuvierais","estuvieran","estuvieras","estuvieron","estuviese","estuvieseis","estuviesen","estuvieses","estuvimos","estuviste","estuvisteis","estuviéramos","estuviésemos","estuvo","está","estábamos","estáis","están","estás","esté","estéis","estén","estés","fue","fuera","fuerais","fueran","fueras","fueron","fuese","fueseis","fuesen","fueses","fui","fuimos","fuiste","fuisteis","fuéramos","fuésemos","ha","habida","habidas","habido","habidos","habiendo","habremos","habrá","habrán","habrás","habré","habréis","habría","habríais","habríamos","habrían","habrías","habéis","había","habíais","habíamos","habían","habías","han","has","hasta","hay","haya","hayamos","hayan","hayas","hayáis","he","hemos","hube","hubiera","hubierais","hubieran","hubieras","hubieron","hubiese","hubieseis","hubiesen","hubieses","hubimos","hubiste","hubisteis","hubiéramos","hubiésemos","hubo","la","las","le","les","lo","los","me","mi","mis","mucho","muchos","muy","más","mí","mía","mías","mío","míos","nada","ni","no","nos","nosotras","nosotros","nuestra","nuestras","nuestro","nuestros","o","os","otra","otras","otro","otros","para","pero","poco","por","porque","que","quien","quienes","qué","se","sea","seamos","sean","seas","seremos","será","serán","serás","seré","seréis","sería","seríais","seríamos","serían","serías","seáis","sido","siendo","sin","sobre","sois","somos","son","soy","su","sus","suya","suyas","suyo","suyos","sí","también","tanto","te","tendremos","tendrá","tendrán","tendrás","tendré","tendréis","tendría","tendríais","tendríamos","tendrían","tendrías","tened","tenemos","tenga","tengamos","tengan","tengas","tengo","tengáis","tenida","tenidas","tenido","tenidos","teniendo","tenéis","tenía","teníais","teníamos","tenían","tenías","ti","tiene","tienen","tienes","todo","todos","tu","tus","tuve","tuviera","tuvierais","tuvieran","tuvieras","tuvieron","tuviese","tuvieseis","tuviesen","tuvieses","tuvimos","tuviste","tuvisteis","tuviéramos","tuviésemos","tuvo","tuya","tuyas","tuyo","tuyos","tú","un","una","uno","unos","vosotras","vosotros","vuestra","vuestras","vuestro","vuestros","y","ya","yo"};
		CharArraySet SPANISH_STOP_WORDS_SET=new CharArraySet(0, false);
		for(int i=0; i<spanish_words.length; i++)	SPANISH_STOP_WORDS_SET.add(spanish_words[i]);
		
		CharArraySet COMMON_WORDS=new CharArraySet(0,false);
		COMMON_WORDS.add("camión");
		COMMON_WORDS.add("filete");
	
		System.out.print("\n");
			
		String contenido="Ayer me compré un camión negro y por la tarde me comí un filete de ternera";
		//String contenido_ingles="Yesterday I bought a black truck and in the afternoon I ate a veal steak";
	
		Analyzer analyzer=new WhitespaceAnalyzer();
		
		PrintTokens(analyzer.tokenStream(null, contenido), "Por defecto");
		// StandardFilter está obsoleto y no aparece en Lucene 8.2.0
		PrintTokens(new LowerCaseFilter(analyzer.tokenStream(null, contenido)), "LowerCaseFilter");
		PrintTokens(new StopFilter(analyzer.tokenStream(null, contenido), SPANISH_STOP_WORDS_SET), "StopFilter");
		PrintTokens(new SnowballFilter(analyzer.tokenStream(null, contenido), "Spanish"), "SnowballFilter");
		PrintTokens(new ShingleFilter(analyzer.tokenStream(null, contenido)), "ShingleFilter");
		PrintTokens(new EdgeNGramTokenFilter(analyzer.tokenStream(null, contenido), 3), "EdgeNGramTokenFilter");
		PrintTokens(new NGramTokenFilter(analyzer.tokenStream(null, contenido), 3), "NGramTokenFilter");
		PrintTokens(new CommonGramsFilter(analyzer.tokenStream(null, contenido), COMMON_WORDS), "CommonGramsFilter");
		// SynonymFilter necesita un mapa de sinonimos el cual no nos da Lucene y deberiamos de hacer a mano, por lo que no lo ejecutamos
		//PrintTokens(new SynonymFilter(analyzer.tokenStream(null, contenido)), "SynonymFilter");	
	}
}
