import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;


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

public class last4CharFilter extends TokenFilter{
	private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);  
	private final PositionIncrementAttribute posIncrAtt = addAttribute(PositionIncrementAttribute.class);

	//constructor que hereda de TokenFilter
	public last4CharFilter(TokenStream input){
		super(input);
	}

	//devuelve true si el token tiene longitud mayor que 4, y por tanto es válido para aplicarle el filtro
	public boolean accept(){
		return (termAtt.length() >= 4 );
	}



	@Override
	public final boolean incrementToken() throws IOException{
		int skippedPositions=0;
		while(input.incrementToken()){
			if (accept()) {
				//si nos hemos saltado algún token no válido anteriormente avanzamos posiciones con el atributo posIncrAtt
				if (skippedPositions != 0) {
					posIncrAtt.setPositionIncrement(posIncrAtt.getPositionIncrement() + skippedPositions);
				}
				//realizamos la copia de los 4 ultimos caracteres del token a un nuevo buffer y lo copiamos en termAtt
				int length = termAtt.length();
				char[] buffer = termAtt.buffer();
				char[] newBuffer = new char[4];

				for (int i = 0; i <4 ; i++) {
					newBuffer[3-i] = buffer[length-1-i];
				}

				termAtt.setEmpty();
				termAtt.copyBuffer(newBuffer, 0, newBuffer.length);
				return true;
			}
			//guardamos las posiciones avanzadas
			skippedPositions += posIncrAtt.getPositionIncrement();
		}
		// reached EOS -- return false
		return false;
	}

}  
