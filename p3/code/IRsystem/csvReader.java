import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;


public class csvReader{

    //private static final String SAMPLE_CSV_FILE_PATH = "../prueba/wiki_movie_plots_deduped_CHIQUITO.csv";
    private static final String SAMPLE_CSV_FILE_PATH = "../wiki_movie/wiki_movie_plots_deduped.csv";

    public static void main(String[] args) throws IOException {
        int counter=0;
		Reader reader = Files.newBufferedReader(Paths.get(SAMPLE_CSV_FILE_PATH));
		CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
		Iterator<CSVRecord> csv_it = csvParser.iterator();
		CSVRecord first=csv_it.next();

		String[] campos= new String[first.size()]; 
		//{first.get(0), first.get(1), first.get(2), first.get(3), first.get(4), first.get(5), first.get(6), first.get(7)};
		for(int i=0; i<first.size(); i++){
			campos[i]=first.get(i);
		}			

		for (CSVRecord csvRecord : csvParser) {

			int movie_number=(int)csvRecord.getRecordNumber();
			movie_number-=1;

			for(int i=0; i<csvRecord.size(); i++){
				System.out.println(campos[i] + " : " + csvRecord.get(i));
			}
			System.out.println("---------------\n\n");

			/*
			// Accessing Values by Column Index
			String year = csvRecord.get(0);
			String title = csvRecord.get(1);
			String origin = csvRecord.get(2);
			String director = csvRecord.get(3);
			String cast = csvRecord.get(4);
			String genre = csvRecord.get(5);
			String wiki = csvRecord.get(6);
			String plot = csvRecord.get(7);
	
			//Show Info
			System.out.println("Movie Number - " + csvRecord.getRecordNumber());
			System.out.println("---------------");
			System.out.println("Year : " + year);
			System.out.println("Title : " + title);
			System.out.println("Origin/Ethnicity : " + origin);
			System.out.println("Director : " + director);
			System.out.println("Cast : " + cast);
			System.out.println("Genre : " + genre);
			System.out.println("Wiki Page : " + wiki);
			System.out.println("Plot : " + plot);
	
			System.out.println("---------------\n\n");
			*/
	
			
			
			//PARA MOSTRAR SOLO LAS 10 PRIMERAS
			if(counter==6){
				break;
			}
			counter+=1;
			
		}
	}
}
