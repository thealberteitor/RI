import java.io.*;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import java.util.*; 


//Imports para detectar idioma:

import org.apache.tika.language.LanguageIdentifier;
import org.apache.tika.language.detect.LanguageDetector;
import org.apache.tika.langdetect.OptimaizeLangDetector;
import org.apache.tika.language.detect.LanguageResult;

//Imports para los links 

import org.apache.tika.sax.LinkContentHandler;
import org.apache.tika.sax.TeeContentHandler;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.apache.tika.sax.ToHTMLContentHandler;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import java.io.InputStream;

import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.Link;
import org.apache.commons.lang3.ArrayUtils;


public class p1{

	public static String identifyLanguage(String text) {
		LanguageIdentifier identifier = new LanguageIdentifier(text);
		return identifier.getLanguage();
	}

	

	public static void main(String[] args) throws Exception{
		if(args.length<1){
			System.out.println("Debe pasar un directorio a la ejecución del programa.");
			System.exit(0);
		}
		
		//Leemos el directorio y los archivos que cuelgan de el
		File directory = new File(args[0]);
		String[] ficheros = directory.list();

		//Creamos una instancia de Tika con la configuracion por defecto
		Tika tika = new Tika();
		
		//Para que el string no tenga memoria limitada
		tika.setMaxStringLength(10*1024*1024);
		
		Metadata metadata = new Metadata();

		//for(int i=0; i<args.length; i++)	System.out.println(i+ " " +args[i]);
		
		System.out.println("\n\n\n");

		if(args.length>1){
			if(args[1].compareTo("-d")==0){
				//Creacion de la tabla para mostrar los datos en la terminal
				System.out.format("+----------------------------------------+----------------------------+-------------------------+--------------------+%n");
				System.out.format("| Nombre del fichero                     | Tipo de fichero            | Codificacion            | Idioma             |%n");
				System.out.format("+----------------------------------------+----------------------------+-------------------------+--------------------+%n");

				//Se parsean los ficheros pasados como argumento y se extrae el contenido
				for(String f : ficheros){
					File file = new File("./"+args[0]+"/"+f);
					
					//Parseamos
					tika.parse(file, metadata);
					String contenido = tika.parseToString(file);            

					// Detectamos el MIME tipo del fichero
					String type = tika.detect(file);

					//Extraemos el texto plano en un string
					String text = tika.parseToString(file);

					//Extraemos el idioma
					String idioma = identifyLanguage(contenido);

					//Extraemos la codificación y el nombre
					String cod = metadata.get(Metadata.CONTENT_ENCODING);
					String name = metadata.get(Metadata.RESOURCE_NAME_KEY);

					//Formato de cada fila de la tabla, s string, d double,.... el numero indica el la longitud de cada celda
					String leftAlignFormat = "| %-36s   |  %-25s | %-23s | %-18s |%n";
					System.out.format(leftAlignFormat, name ,type, cod, idioma);
				}
				
				System.out.format("+----------------------------------------+----------------------------+-------------------------+--------------------+%n");
				
			}else if(args[1].compareTo("-l")==0){
				for(String f : ficheros){
					File file = new File("./"+args[0]+"/"+f);
					
					LinkContentHandler linkHandler = new LinkContentHandler();
					ContentHandler textHandler = new BodyContentHandler();
					TeeContentHandler teeHandler = new TeeContentHandler(linkHandler);
					ParseContext parseContext = new ParseContext();
					InputStream target = new FileInputStream(file);

					AutoDetectParser parser = new AutoDetectParser();

					parser.parse(target,teeHandler,metadata,parseContext);
					
					//System.out.println("LINKS EN EL ARCHIVO " +file.getName()+ ":\n" + linkHandler.getLinks() + "\n");
					
					List<Link> links=linkHandler.getLinks();
					System.out.println("LINKS EN EL ARCHIVO " +file.getName()+ ":");
					
					if(links.isEmpty())		System.out.println("No hay links :(");
					else	for(Link link:links)	System.out.println(link);
					
					System.out.println("\n");
					
				}

			}else if(args[1].compareTo("-t")==0){
				Boolean rminutiles=false;
					
				if(args.length>2 && args[2].compareTo("-rminutiles")==0)
					rminutiles=true;
				
				for(String f : ficheros){
					File file = new File("./"+args[0]+"/"+f);
					
					// Parseamos el fichero y obtenemos su texto plano.
					tika.parse(file, metadata);
					String contenido = tika.parseToString(file);
					
					// Separamos todas las palabras que aparecen en el fichero y las añadimos a vector
					String[] todas=contenido.split("\\s+|[\\.\\;\\:\\,]\\s+|[()¿?¡!]"); // Podemos añadir eliminar numeros con [0-9]
					
					// Creamos un ArrayList donde guardaremos el número de veces que se repite cada palabra
					ArrayList<PalabraOcurrencias> palaocu=new ArrayList<PalabraOcurrencias>();
					
					for(String palabra:todas){
						Boolean encontrado=false;
						int i=0;
						
						while(i<palaocu.size() && !encontrado){
							if(palaocu.get(i).palabra.compareTo(palabra)==0)
								encontrado=true;
							else
								i++;
						}
						
						if(encontrado)	palaocu.get(i).ocurrencias++;
						else			palaocu.add(new PalabraOcurrencias(palabra, 1));
					}
					
					// Eliminamos las palabras que no queremos tratar
					
					String filename="./CSV";
					
					if(rminutiles){
						for(int i=0; i<palaocu.size(); i++){
							if(palaocu.get(i).palabra.compareTo("")==0 ||
								palaocu.get(i).palabra.compareToIgnoreCase("a")==0 ||
								palaocu.get(i).palabra.compareToIgnoreCase("á")==0 ||
								palaocu.get(i).palabra.compareToIgnoreCase("al")==0 ||
								palaocu.get(i).palabra.compareToIgnoreCase("como")==0 ||
								palaocu.get(i).palabra.compareToIgnoreCase("con")==0 ||
								palaocu.get(i).palabra.compareToIgnoreCase("de")==0 ||
								palaocu.get(i).palabra.compareToIgnoreCase("del")==0 ||
								palaocu.get(i).palabra.compareToIgnoreCase("él")==0 ||
								palaocu.get(i).palabra.compareToIgnoreCase("el")==0 ||
								palaocu.get(i).palabra.compareToIgnoreCase("en")==0 ||
								palaocu.get(i).palabra.compareToIgnoreCase("es")==0 ||
								palaocu.get(i).palabra.compareToIgnoreCase("ha")==0 ||
								palaocu.get(i).palabra.compareToIgnoreCase("la")==0 ||
								palaocu.get(i).palabra.compareToIgnoreCase("las")==0 ||
								palaocu.get(i).palabra.compareToIgnoreCase("le")==0 ||
								palaocu.get(i).palabra.compareToIgnoreCase("les")==0 ||
								palaocu.get(i).palabra.compareToIgnoreCase("lo")==0 ||
								palaocu.get(i).palabra.compareToIgnoreCase("los")==0 ||
								palaocu.get(i).palabra.compareToIgnoreCase("mas")==0 ||
								palaocu.get(i).palabra.compareToIgnoreCase("más")==0 ||
								palaocu.get(i).palabra.compareToIgnoreCase("me")==0 ||
								palaocu.get(i).palabra.compareToIgnoreCase("mi")==0 ||
								palaocu.get(i).palabra.compareToIgnoreCase("no")==0 ||
								palaocu.get(i).palabra.compareToIgnoreCase("para")==0 ||
								palaocu.get(i).palabra.compareToIgnoreCase("por")==0 ||
								palaocu.get(i).palabra.compareToIgnoreCase("que")==0 ||
								palaocu.get(i).palabra.compareToIgnoreCase("se")==0 ||
								palaocu.get(i).palabra.compareToIgnoreCase("su")==0 ||
								palaocu.get(i).palabra.compareToIgnoreCase("si")==0 ||
								palaocu.get(i).palabra.compareToIgnoreCase("sus")==0 ||
								palaocu.get(i).palabra.compareToIgnoreCase("te")==0 ||
								palaocu.get(i).palabra.compareToIgnoreCase("una")==0 || 
								palaocu.get(i).palabra.compareToIgnoreCase("unas")==0 ||
								palaocu.get(i).palabra.compareToIgnoreCase("un")==0 ||
								palaocu.get(i).palabra.compareToIgnoreCase("unos")==0 ||
								palaocu.get(i).palabra.compareToIgnoreCase("y")==0 ||
								palaocu.get(i).palabra.compareToIgnoreCase("ya")==0)				
								{
									palaocu.remove(i);
								}
						}
						filename+="/rminutiles/"+file.getName()+"_inutiles.csv";
					}else{
						for(int i=0; i<palaocu.size(); i++){					
							if(palaocu.get(i).palabra.compareTo("")==0)
								palaocu.remove(i);						
						}
						filename+="/normal/"+file.getName()+".csv";
					}
					
					// Ordenamos las palabras con su número de ocurrencias en orden descendente
					Collections.sort(palaocu, new SortbyOcurrencias());
					
					/*
					// Print por pantalla
					
					System.out.println("----------------------------------------------------------------------------");
					System.out.println(file.getName());
					System.out.println("----------------------------------------------------------------------------");
					for(int i=0; i<palaocu.size(); i++){
						System.out.println(palaocu.get(i).palabra+";"+palaocu.get(i).ocurrencias);
					}
					System.out.println("----------------------------------------------------------------------------\n\n\n\n\n\n");
					*/
					
					// Añadimos la información al fichero CSV
					String palcsv="";
					
					for(int i=0; i<palaocu.size(); i++){
						palcsv+=palaocu.get(i).palabra+";"+palaocu.get(i).ocurrencias+"\n";
					}
					
					PrintWriter writer = new PrintWriter(filename);
					writer.print(palcsv);
					writer.close();
				}
				
				System.out.println("FIN\n\n");
				
			}else{
				System.out.println("ERROR: Parámetro no válido (-d, -l, -t, -t -rminutiles).\n\n");
				System.exit(1);
			}
		}else{
			System.out.println("ERROR: Debe pasar un parámetro al programa (-d, -l, -t, -t -rminutiles).\n\n");
			System.exit(1);
		}
	
		
		System.out.println("\n\n");
		System.exit(0);
		
		
		/*
		//Creacion de la tabla para mostrar los datos en la terminal
		System.out.format("+------------------------------+------------------+---------------+----------+%n");
		System.out.format("| Nombre del fichero           | Tipo de fichero  | Codificacion  | Idioma   |%n");
		System.out.format("+------------------------------+------------------+---------------+----------+%n");
		*/
	}
}
