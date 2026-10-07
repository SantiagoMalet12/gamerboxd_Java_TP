package servidor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.crypto.SecretKey;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import entities.Compania;
import entities.Juego;
import entities.Persona;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import data.Conexion;
import data.DataCompania;
import data.DataJuego;
import data.Data_persona;
import data.Cors;

public class AbmcJuegos {
	
	private static final SecretKey KEY = GeneracionWebToken.llaveJWT();
	
	
	
	public static class juegoid implements HttpHandler { 

	    public void handle(HttpExchange exchange) throws IOException {
	        Cors.controlCors(exchange);

	        if (exchange.getRequestMethod().equals("OPTIONS")) {
	            exchange.sendResponseHeaders(204, -1);
	            exchange.close();
	            return;
	        }

	        String respuesta = "";
	        int codigoestado = 200;
	        Gson gson = new Gson();


	      
	        try {
	            InputStream is = exchange.getRequestBody();
	            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
	            is.close();

	            Juego jue = gson.fromJson(body, Juego.class);
	            Juego juegoEncontrado = DataJuego.juegoPorId(jue.getId_juego());

	            if (juegoEncontrado != null) {
	                respuesta = gson.toJson(juegoEncontrado);
	                codigoestado = 200;
	            } else {
	                respuesta = "Juego no encontrado";
	                codigoestado = 404;
	            }
	        } catch (Exception e) {
	            respuesta = "Error en el servidor";
	            codigoestado = 500;
	        }

	     
	        byte[] bytesRespuesta = respuesta.getBytes(StandardCharsets.UTF_8);
	        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
	        exchange.sendResponseHeaders(codigoestado, bytesRespuesta.length);
	        OutputStream os = exchange.getResponseBody();
	        os.write(bytesRespuesta);
	        os.close();
	    }
	}

	
	
	
public static class listajuegos implements HttpHandler {
	
	ArrayList<Juego> listadejuegos = new ArrayList<>();
		
		public void handle(HttpExchange exchange) throws IOException {
			
			
			Cors.controlCors(exchange);
			
		    if (exchange.getRequestMethod().equals("OPTIONS")) {

		        exchange.sendResponseHeaders(204, -1);
		        exchange.close();

		        return;
		    }
			
		    listadejuegos = DataJuego.listarJuegos();
			
			Gson gson = new Gson();
		    String jsonRespuesta = gson.toJson(listadejuegos);
		    
		    
		    byte[] bytesRespuesta = jsonRespuesta.getBytes("UTF-8");
		    exchange.sendResponseHeaders(200, bytesRespuesta.length);
		    
		    
		    OutputStream os = exchange.getResponseBody();
		    os.write(bytesRespuesta);
		    os.close();
			
			
		}
}

public static class existejuego implements HttpHandler{
	
	public void handle(HttpExchange exchange) throws IOException{
		
		String respuesta;
		
		boolean existejuego = false;
		Cors.controlCors(exchange);
		
		if (exchange.getRequestMethod().equals("OPTIONS")) {

	        exchange.sendResponseHeaders(204, -1);
	        exchange.close();

	        return;
	    }
		
		 
	    try {
	    	
	    	
	    	String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
	    	
	    	String token = authHeader.substring(7);
	    	
    	    
    	    Claims claims = Jwts.parser()
    	    		.verifyWith(KEY) 
    	            .build()
    	            .parseSignedClaims(token)
    	            .getPayload();
    	    
		}catch(Exception e ) {
	    	
			respuesta = "Error token";
	    	exchange.sendResponseHeaders(402, respuesta.getBytes().length);
	    	
		}
		
	    try {
	    	
    	 	InputStream is = exchange.getRequestBody();
		    String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
		    is.close();
		    Gson gson = new Gson();
			Juego juego = gson.fromJson(body, Juego.class);
			
			existejuego = DataJuego.buscarjuego(juego.getTitulo());
					
			if(!existejuego) {
				
				respuesta = "No existe empresa";
		    	exchange.sendResponseHeaders(404, respuesta.getBytes().length);
				
			}
			
	    	respuesta = "todo bem";
	    	exchange.sendResponseHeaders(200, respuesta.getBytes().length);

    	
    }
    catch(Error e ) {
    	
    	respuesta = "Error en la bd";
    	exchange.sendResponseHeaders(401, respuesta.getBytes().length);
    	
    	
    }
    	
    	
    OutputStream os = exchange.getResponseBody();
    os.write(respuesta.getBytes(StandardCharsets.UTF_8));
    os.close();
	
    	
    }
	

}




	public static class devolverjuego implements HttpHandler{
	
	public void handle(HttpExchange exchange) throws IOException {
		
		
		Juego juego = new Juego();
		Juego juegoRespuesta = new Juego();
		
		String respuesta = "aaa no seee";
		

		Cors.controlCors(exchange);
		
	    if (exchange.getRequestMethod().equals("OPTIONS")) {

	        exchange.sendResponseHeaders(204, -1);
	        exchange.close();

	        return;
	    }
	    
	    try {
	    	
	    	
	    	String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
	    	
	    	String token = authHeader.substring(7);
	    	
    	    
    	    Claims claims = Jwts.parser()
    	    		.verifyWith(KEY) 
    	            .build()
    	            .parseSignedClaims(token)
    	            .getPayload();
    	    
		}catch(Exception e ) {
	    	
			respuesta = "Error token";
	    	exchange.sendResponseHeaders(402, respuesta.getBytes().length);
	    	
	    	
		}
	    
	    try {
	    	
	    	 	InputStream is = exchange.getRequestBody();
			    String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
			    is.close();
			    Gson gson = new Gson();
			    juego = gson.fromJson(body, Juego.class);
			   
				
				juegoRespuesta = DataJuego.recuperarPorTitulo(juego.getTitulo());
				
				respuesta = gson.toJson(juegoRespuesta);
				
		    	exchange.sendResponseHeaders(200, respuesta.getBytes().length);

	    	
	    }
	    catch(Exception e ) {
	    	
	    	respuesta = "Error en la bd";
	    	exchange.sendResponseHeaders(401, respuesta.getBytes().length);
	    	
	    	
	    }
	    	
	    OutputStream os = exchange.getResponseBody();
        os.write(respuesta.getBytes(StandardCharsets.UTF_8));
        os.close();
		
	    	
	    }
	
	
	
	
}
	
	
	
	
	
	public static class actualizardatosjuego implements HttpHandler {
		
		
		public void handle(HttpExchange exchange) throws IOException {
			
			
			Boolean existe;
			Juego juego = new Juego();
			Juego comRespuesta = new Juego();
			
			String respuesta = "aaa no seee";
			
			
			
			Cors.controlCors(exchange);
			
		    if (exchange.getRequestMethod().equals("OPTIONS")) {

		        exchange.sendResponseHeaders(204, -1);
		        exchange.close();

		        return;
		    }
		    
		    
		    try {
		    	
		    	
		    	String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
		    	
		    	String token = authHeader.substring(7);
		    	
	    	    
	    	    Claims claims = Jwts.parser()
	    	    		.verifyWith(KEY) 
	    	            .build()
	    	            .parseSignedClaims(token)
	    	            .getPayload();
	    	    
			}catch(Exception e ) {
		    	
				respuesta = "Error token";
		    	exchange.sendResponseHeaders(402, respuesta.getBytes().length);
		    	
			}
		    
		    try {
		    	
		    	 	InputStream is = exchange.getRequestBody();
				    String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
				    is.close();
				    Gson gson = new Gson();
				    juego = gson.fromJson(body, Juego.class);
				    
				   
				    	
				    existe = DataJuego.buscarjuego(juego.getTitulo());
				    
				    
				    if(existe) {
				    	
					respuesta = "empresa repetida";
						

						
			    	exchange.sendResponseHeaders(409, respuesta.getBytes().length);
				    }
				    else if (!existe) {
				    	
				    	try {
				    		
				    	    
				    	    DataJuego.actualizarJuego(
				    	    	Integer.parseInt(juego.getId_juego()),
				    	        juego.getTitulo(), 
				    	        juego.getEstado(), 
				    	        juego.getImagen(), 
				    	        juego.getDescripcion()
				    	    );
				    	    
				    	    respuesta = "Actualizado con exito";
				    	    byte[] bytesResp = respuesta.getBytes(StandardCharsets.UTF_8);
				    	    exchange.sendResponseHeaders(200, bytesResp.length);

				    	} catch(Exception e) {
				    	    System.out.println("Error al actualizar en la BD: " + e.getMessage());
				    	    
				    	    
				    	    respuesta = "Error en la bd";
				    	    byte[] bytesError = respuesta.getBytes(StandardCharsets.UTF_8);
				    	    exchange.sendResponseHeaders(500, bytesError.length);
				    	    
				    	} finally {
				    	    OutputStream os = exchange.getResponseBody();
				    	    os.write(respuesta.getBytes(StandardCharsets.UTF_8));
				    	    os.close();
				    	}
				    	
				    	
				    }


		    	
		    }
		    catch(Exception e ) {
		    	
		    	respuesta = "Error en la bd";
		    	exchange.sendResponseHeaders(401, respuesta.getBytes().length);
		    	
		    	
		    }
		    	
		    	
		    OutputStream os = exchange.getResponseBody();
            os.write(respuesta.getBytes(StandardCharsets.UTF_8));
            os.close();
			
		    	
		    }
		
		
		
		
		
		
	}


}
