import java.io.IOException;


import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.custom.CustomAnalyzer;


import java.io.*;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import java.util.*;

import java.io.IOException;

public class MyAnalyzer{

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
		
		
		
		
		/* ************************************************************************ */
		Analyzer myanalyzer = CustomAnalyzer.builder()
			.withTokenizer("standard")
			.addTokenFilter("lowercase")
			.addTokenFilter("reversestring")
			.addTokenFilter("capitalization")
			.build();
		/* ************************************************************************ */
		
		
		
		
		
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
			
			Analyzer(myanalyzer, "MyAnalyzer", contenido);
			
			System.out.println("\n\n");
        }
    }
}
