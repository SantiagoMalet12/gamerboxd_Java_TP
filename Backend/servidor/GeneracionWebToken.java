package servidor;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;

import javax.crypto.SecretKey;

public class GeneracionWebToken {
	
	
	private static final String SECRET_TEXT = "mi_clave_secreta_gamerboxd_tp_final_2026";
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET_TEXT.getBytes(StandardCharsets.UTF_8));
    
    // Método para reutilizar la misma clave en toda web
    public static SecretKey llaveJWT() {
        return KEY;
    }
	public static void main(String[] args) {
		

}
	


public static void probando(){
	byte[] content = "Hello World".getBytes(StandardCharsets.UTF_8);
	
	
	SecretKey key = Jwts.SIG.HS256.key().build();
	
	String jws = Jwts.builder().subject("Santi").signWith(key).compact();
	
	try {
	
	    Jwts.parser().verifyWith(key).build().parseSignedClaims(jws);
	
	    System.out.println("Confiaaa");
	
	} catch (JwtException e) {
	
		System.out.println("No Confies");
	}

}




public static String enviotoken(String mail, String rol) {
	

	
	String jws = Jwts.builder()
	
	.header()
		.keyId("id1")
		.and()
	
		.subject(mail)
		.claim("rol", rol)
	    .signWith(llaveJWT())                     
	    .compact();  
	
	return(jws);
	
	
	
}

}