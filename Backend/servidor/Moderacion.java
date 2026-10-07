package servidor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class Moderacion {

    
	static ArrayList<String> PALABRAS_PROHIBIDAS = new ArrayList<>(List.of("mierda", "puto", "puta", "pendejo", "pendeja", 
		    "verga", "joder", "coño", "maricon", "mamada", "mamon", "chingar","nigger","pija",
		    "concha", "boludo", "boluda", "tarado", "tarada",
		    "retrasado", "retrasada", "mogolico", "mogolica", 
		    "suicidate", "invalido", "invalida","conchudo","conchuda", "pija",
		    "free-skins"));
		    


 
    public static String contienePalabrasProhibidas(String texto) {
        if (texto == null) return "";

        for (String palabra : PALABRAS_PROHIBIDAS) {
            if(texto.contains(palabra))
            
                return palabra;
            
        }
        return "";
    }

    
    
}
