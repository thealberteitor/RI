import java.io.IOException;

import java.io.*;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import java.util.*;

import java.io.IOException;

import org.apache.tika.language.LanguageIdentifier;



public class Ejercicio3{

	public static String identifyLanguage(String text) {
		LanguageIdentifier identifier = new LanguageIdentifier(text);
		return identifier.getLanguage();
	}


	public static void main(String[] args) throws IOException{
		if(args.length<1){
			System.out.println("Debe pasar un directorio a la ejecución del programa.");
			System.exit(0);
		}

		//Leemos el directorio y los archivos que cuelgan de el
		File directory = new File(args[0]);
		String[] ficheros = directory.list();
		ArrayList<String> salida = new ArrayList<>();

		//Creamos una instancia de Tika con la configuracion por defecto
		Tika tika = new Tika();
		tika.setMaxStringLength(-1);

		Metadata metadata = new Metadata();

		System.out.println("\n\n\n");

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

			String idioma = identifyLanguage(contenido);
			
			//Quitamos todos los signos extraños (menos los .)
			contenido = contenido.replaceAll("\n", " ");
			contenido = contenido.replaceAll("[(!-+•&^:.,“”)/]", "");
			//System.out.println(contenido);
	
			//Pasamos de string a string[] todas las palabras separadas por espacio
			String[] separado= contenido.split(" ");

			System.out.println("\n\n---------------------- " + f + " ----------------------");

			NameAnalyzer analyzer = new NameAnalyzer(separado);
			salida = analyzer.Analyze(idioma);

			System.out.println("Tokens tras aplicar parser:");

			for(String p : salida){
				System.out.print(p + "\t\t");
			}
	
			System.out.println("\n--------------------------------------------------------\n\n");
		}
	}
}
