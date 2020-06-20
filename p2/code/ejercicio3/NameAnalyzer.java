import java.io.*;
import java.util.*;
import java.util.Scanner;


import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;


public class NameAnalyzer{

	public static String[] entrada;

	public NameAnalyzer(String[] entrada){
		this.entrada=entrada;
	}

	public static String[] LeerArchivo(String idioma){

		ArrayList<String> TodasInutiles = new ArrayList<>();

		File stopwords;

		switch (idioma){
			case "es":
				stopwords = new File("./stopwords/es.txt");
				break;
			case "en":
				stopwords = new File("./stopwords/en.txt");
				break;
			default:
				//System.out.println("El documento puede no estar bien parseado ya que no está en español ni inglés");
				stopwords = new File("./stopwords/empty.txt");
				break;
		}

		Tika tika = new Tika();
		tika.setMaxStringLength(-1);

		Metadata metadata = new Metadata();
		String contenido = new String();

		try{
			tika.parse(stopwords, metadata);
			contenido = tika.parseToString(stopwords);
			System.out.println("Idioma detectado: " + idioma + ". Parseado Stopwords correcto.");
		}catch (Exception e){
			System.out.println("Idioma no detectado.");
		}

		String[] devora= contenido.split("\n");
		return devora;

		//Para leer fichero sin usar Tika:
		/*
		ArrayList<String> TodasInutiles = new ArrayList<>());
		File stopwords = new File("stopwords.txt");
		Scanner s = null;
		
		try{
			System.out.println("Leyendo archivo stopwords.txt...");
			s = new Scanner(stopwords);
			//Leemos línea a línea
			while(s.hasNextLine()){
				TodasInutiles.add(s.nextLine());
			}
		}catch (Exception ex){
			System.out.println("Error leyendo archivo stopwords.txt...");
		}finally{
			try{
				if(s!=null)
					s.close();
			}catch(Exception ex2){
				System.out.println("Error cerrando el archivo...");
			}
		}

		*/

	}


	public static boolean EsInutil(String palabra, String[] inutiles){
		boolean inutil=false;

		for(int i=0; i<inutiles.length && !inutil; i++){
			//System.out.println("Compruebo: " +palabra + " con " + inutiles[i]);
			if(palabra.equals(inutiles[i])){
				System.out.print(palabra + ", ");
				inutil=true;
			}
		}

		return inutil;
	}

	public static ArrayList<String> Analyze(String idioma){
		String[] inutiles = LeerArchivo(idioma);

		ArrayList<String> salida = new ArrayList<>();

		System.out.println("\nPalabras eliminadas:");

		//Añadimos las palabras mayúsculas que no sea artículos, preposiciones...
		for(int i=0; i<entrada.length; i++){
			if(entrada[i].length()>0){
				//System.out.println("Tamaño " + entrada[i] + " : " + entrada[i].length());
				if(Character.isUpperCase(entrada[i].charAt(0)) && !EsInutil(entrada[i],inutiles) && entrada[i].compareTo("")!=0){
					//System.out.println("Añado " + entrada[i]);
					salida.add(entrada[i]);
				}
			}
		}
	
		System.out.println("\n\n");

		return salida;
	}

}
