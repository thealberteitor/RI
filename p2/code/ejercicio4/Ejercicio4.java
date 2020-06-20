import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.miscellaneous.LengthFilter;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.*;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import java.util.*;

import java.io.IOException;

public class Ejercicio4{
    

	public static void Analyzer1(Analyzer analyzer, String name, String contenido) throws IOException{
		System.out.println(name);
		TokenStream stream = analyzer.tokenStream(null, contenido);
		//stream = new LengthFilter(true, stream, 4, Integer.MAX_VALUE);
		stream = new last4CharFilter(stream);
		Map<String,Integer> palabra_ocurrencias = new HashMap<String, Integer>();

		stream.reset();

		while(stream.incrementToken()){
			String palabra=stream.getAttribute(CharTermAttribute.class).toString();
			//System.out.println(palabra);
			
			Integer value=palabra_ocurrencias.get(palabra);
			
			if(value==null)	palabra_ocurrencias.put(palabra,1);
			else			palabra_ocurrencias.put(palabra,value+1);
			
		}

		for (Map.Entry<String, Integer> pair : palabra_ocurrencias.entrySet()) {
			System.out.print(pair.getKey() + "\t");
		}
		

		System.out.println("\n\n");

		stream.end();
		stream.close();
	}



    public static void main(String[] args) throws IOException{
        if(args.length<1){
			System.out.println("Debe pasar un directorio a la ejecución del programa.");
			System.exit(0);
		}
		
		//Leemos el directorio y los archivos que cuelgan de el
		File directory = new File(args[0]);
		String[] ficheros = directory.list();

		//Creamos una instancia de Tika con la configuracion por defecto
        Tika tika = new Tika();
        tika.setMaxStringLength(-1);

        Metadata metadata = new Metadata();

		System.out.println("\n\n\n");
		//string de prueba para test del programa sin documentos largos
        // String contenido = "arbol aceituna y oscar y me comi una tortilla de patatas impresionante";



		//Parsear los archivos como en la P1 y pasarle el analizador que lleva incluido el filtro
         for(String f : ficheros){
            
            File file = new File("./"+args[0]+"/"+f);
            //Parseamos
            tika.parse(file, metadata);
            String contenido = new String();    
            try{
                contenido = tika.parseToString(file);            
            }catch (Exception e){ 
                System.out.println("No se puede parsear...");
            }

			System.out.println("Archivo parseado: " + file.getName());
			Analyzer an =  new WhitespaceAnalyzer();
			Analyzer1(an,"*** last4CharFilter (elimina el token si tiene menos de 4 caracteres y en caso contrario se queda con los 4 ultimos) ***",contenido );

		}
		
	}
}

