import java.util.*;


// Clase que definirá un objeto que contiene una palabra con su número de ocurrencias
public class PalabraOcurrencias{ // Para la opcion -t
	public String palabra;
	public int ocurrencias;
	
	public PalabraOcurrencias(String p, int o){
		palabra=p;
		ocurrencias=o;
	}
}

// Para ordenar las palabras en orden descendente
class SortbyOcurrencias implements Comparator<PalabraOcurrencias>{ 
	public int compare(PalabraOcurrencias a, PalabraOcurrencias b) {
		return b.ocurrencias - a.ocurrencias; 
	} 
} 
