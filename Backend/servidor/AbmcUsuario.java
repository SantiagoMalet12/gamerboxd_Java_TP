package servidor;

import java.io.IOException;



import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import javax.crypto.SecretKey;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import data.Cors;
import data.Data_persona;
import servidor.GestionMail;

import entities.Persona;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class AbmcUsuario {
	

	
	private static final SecretKey KEY = GeneracionWebToken.llaveJWT();

	

	
	
	public static class rolengrupo implements HttpHandler {
		
		public void handle(HttpExchange exchange) throws IOException {
			Persona p = new Persona();

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
	    	    
	    	    String mail = claims.getSubject();

				
				
	    	    p.setRolgrupo(Data_persona.obtenerrolgrupo(mail));
	    	    
	    	    Gson gson = new Gson();
	    	    String jsonRespuesta = gson.toJson(p);
			    exchange.sendResponseHeaders(200, jsonRespuesta.getBytes().length);	
			    OutputStream os = exchange.getResponseBody();
			    os.write(jsonRespuesta.getBytes(StandardCharsets.UTF_8));
			    os.close();
			    return;
			
  
	    	    
		    }catch(Error e) {
		    	
		    
		    
		    	}
		    
	}
	}
	public static class convertirenadmin implements HttpHandler {
		
		
		public void handle(HttpExchange exchange) throws IOException {
			String respuesta = "aaa no seee";
			boolean exito; 
			Persona per_completa;
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
	    	    

		    	InputStream is = exchange.getRequestBody();
			    String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
			    is.close();
			    Gson gson = new Gson();
				Persona per = gson.fromJson(body, Persona.class);
				
				
				

	    	    
	    	    per_completa = Data_persona.buscar_solo_persona_pormail(per.getMail());
	    	    
				if (per_completa.getNombre_usuario() == null) {
					
					respuesta = "Usuario no existe o credenciales incorrectas";
					exchange.sendResponseHeaders(405, respuesta.getBytes().length);
		            return; 
		        }
				
				if(per_completa.getEstado().equals("inactivo")) {
					
					respuesta = "Usuario no existe o credenciales incorrectas";
					exchange.sendResponseHeaders(401, respuesta.getBytes().length);
		            return; 
					
				}
				
				if(per_completa.getRol().equals("administrador")) {
					
					respuesta = "Este usuario ya es administrador";
					exchange.sendResponseHeaders(402, respuesta.getBytes().length);
		            return; 
					
				}
				else {
					
					exito = Data_persona.convertirenadmin(per.getMail());
				
					if(exito) {
						
						respuesta = "Exito";
						exchange.sendResponseHeaders(200, respuesta.getBytes().length);
						
					}
					else {
						
						respuesta = "Error en la bd";
						exchange.sendResponseHeaders(403, respuesta.getBytes().length);
					}

				}
	    	    
	    	    
		    }catch(Error e) {
		    	
		    
		    
		    	}
		    }
		
		
		
	}
	
	
	public static class dardebaja implements HttpHandler {
		
		public void handle(HttpExchange exchange) throws IOException {
			String respuesta = "aaa no seee";
			boolean respuestadb; 
			
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
	    	    
	    	    String mail = claims.getSubject();
	    	    
	  
				respuestadb = Data_persona.dardebaja(mail);
				
				if(respuestadb) {
					
					respuesta = "todo bem";
			    	exchange.sendResponseHeaders(200, respuesta.getBytes().length);
		
				}
				
				else {
					
					respuesta = "error";
			    	exchange.sendResponseHeaders(401, respuesta.getBytes().length);
			    	
					
				}
				
		    	
		    
		    }
		    catch(Error e){
		    	
		    	respuesta = "error";
		    	exchange.sendResponseHeaders(401, respuesta.getBytes().length);
		    	
		    }
		    
		    	
		    	
		    OutputStream os = exchange.getResponseBody();
            os.write(respuesta.getBytes(StandardCharsets.UTF_8));
            os.close();
			
		    	
		    }
		
		
	}
	
	
	
	//Actualiza imagen y nombre de un usuario dado su mail
	public static class actualizardatosusuario implements HttpHandler {
		
		public void handle(HttpExchange exchange) throws IOException {
			String respuesta = "aaa no seee";
			int codigoestado;
			
			

			
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

	    	    
	    	    String mail = claims.getSubject();
	    	    
	    	    
		    	
		    	 InputStream is = exchange.getRequestBody();
				    String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
				    is.close();
				    Gson gson = new Gson();
					Persona per = gson.fromJson(body, Persona.class);
					

					respuesta = Data_persona.actualizarImagenYnombre(mail, per.getFoto_perfil(), per.getNombre_usuario());
					System.out.println(respuesta);
					codigoestado = 200;
					
					
					
		    	
		    	
		    }
		    catch(Exception e ) {
		    	
		    	System.out.println(e);
		    	respuesta = "Error";
		    	codigoestado = 401;
		    	
		    	
		    }

		    Gson gson = new Gson();
		    String jsonRespuesta = gson.toJson(respuesta);
		    exchange.sendResponseHeaders(codigoestado, jsonRespuesta.getBytes().length);	
		    OutputStream os = exchange.getResponseBody();
           os.write(jsonRespuesta.getBytes(StandardCharsets.UTF_8));
           os.close();
		
		    
		
		}
		

	}
	
	
	//Devuelve foto de usuario a partir de su mail
	
	public static class obtencionfotousuario implements HttpHandler {
		
		public void handle(HttpExchange exchange) throws IOException {
			
			int codigoestado;
			Persona perr = new Persona();
			
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

	    	    
	    	    String mail = claims.getSubject();
	    	    
	    	    
	    	   //mail llegar por JWT

					
					perr = Data_persona.buscar_solo_persona_pormail(mail);
					codigoestado = 200;
					
					
		    	
		    	
		    }
		    catch(Exception e ) {
		    	
		    	
		    	codigoestado = 401;
		    	
		    	
		    	
		    }
		    
		    Gson gson = new Gson();
		    String jsonRespuesta = gson.toJson(perr);
		    exchange.sendResponseHeaders(codigoestado, jsonRespuesta.getBytes().length);	
		    OutputStream os = exchange.getResponseBody();
           os.write(jsonRespuesta.getBytes(StandardCharsets.UTF_8));
           os.close();
		
		    
		
		}
		
		
	
		
		
		
		
		
		
	}
	
	
	
	
	
	public static class cambiarpassword implements HttpHandler{
		
		public void handle(HttpExchange exchange) throws IOException {
			String respuesta = "aaa no seee";
			
			Cors.controlCors(exchange);
			
		    if (exchange.getRequestMethod().equals("OPTIONS")) {

		        exchange.sendResponseHeaders(204, -1);
		        exchange.close();

		        return;
		    }
		    
		    try {
		    	
		    	 InputStream is = exchange.getRequestBody();
				    String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
				    is.close();
				    Gson gson = new Gson();
					Persona per = gson.fromJson(body, Persona.class);
					System.out.println(per.getContrasena());
					System.out.println(per.getMail());
					Data_persona.actualizar_contrasenia(per.getMail(), per.getContrasena());
					
			    	respuesta = "todo bem";
			    	exchange.sendResponseHeaders(200, respuesta.getBytes().length);
					
		    	
		    	
		    	
		    	
		    	
		    	
		    }
		    catch(Error e ) {
		    	
		    	respuesta = "Error";
		    	exchange.sendResponseHeaders(401, respuesta.getBytes().length);
		    	
		    	
		    }
		    	
		    	
		    OutputStream os = exchange.getResponseBody();
            os.write(respuesta.getBytes(StandardCharsets.UTF_8));
            os.close();
			
		    	
		    }

			
		}
		
	
	
	public static class verificarjwt implements HttpHandler {
		
		
		public void handle(HttpExchange exchange) throws IOException {
			
			String respuesta = "";
			int codigoestado;
			
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
	    	    
	    	    
	    	    String rol = claims.get("rol", String.class);

					respuesta = rol;
					codigoestado = 200;
					
		    	
		    }
		    catch(Exception e ) {
		    	
		    	System.out.println(e);
		    	respuesta = "Error o token invalido";
		    	codigoestado = 401;
		    	
		    	
		    }
		    
		    Gson gson = new Gson();
		    String jsonRespuesta = gson.toJson(respuesta);
		    exchange.sendResponseHeaders(codigoestado, jsonRespuesta.getBytes().length);	
		    OutputStream os = exchange.getResponseBody();
           os.write(jsonRespuesta.getBytes(StandardCharsets.UTF_8));
           os.close();
		
		
		}
	}
	
	
	

	
	
	//verificar token mail
	public static class verificartoken implements HttpHandler {
		
		public void handle(HttpExchange exchange) throws IOException {
			
			String respuesta = "aaa no seee";
			
			Cors.controlCors(exchange);
			
		    if (exchange.getRequestMethod().equals("OPTIONS")) {

		        exchange.sendResponseHeaders(204, -1);
		        exchange.close();

		        return;
		    }
		    
		    try {
		    	
			    InputStream is = exchange.getRequestBody();
			    String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
			    is.close();
			    
			    Gson gson = new Gson();
				Persona per = gson.fromJson(body, Persona.class);
				Boolean respuesta_token = Data_persona.veriToken(per.getToken(), per.getMail());
				if (respuesta_token == true) {
					
					respuesta = "todo bem";
					exchange.sendResponseHeaders(200, respuesta.getBytes().length);

				}
				
				else {
					
					respuesta = "token vencido a ver";
			    	exchange.sendResponseHeaders(401, respuesta.getBytes().length);
					
				}
				
    	
		    	
		    }catch(Error e ) {
		    	
		    	respuesta = "todinho mal";
		    	exchange.sendResponseHeaders(401, respuesta.getBytes().length);
		    	
		    	
		    }
		    
		    OutputStream os = exchange.getResponseBody();
            os.write(respuesta.getBytes(StandardCharsets.UTF_8));
            os.close();
			
			
		}
		
		
	}
	
	
	
	public static class recuperarpersona implements HttpHandler {
		
		
		public void handle(HttpExchange exchange) throws IOException {
			
			 String respuesta = "";
			
			
			
				Cors.controlCors(exchange);
			
		    if (exchange.getRequestMethod().equals("OPTIONS")) {

		        exchange.sendResponseHeaders(204, -1);
		        exchange.close();

		        return;
		    }
		    
		    try {
		    InputStream is = exchange.getRequestBody();
		    String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
			is.close();
		    
		    respuesta = "ok";
		    Gson gson = new Gson();
			Persona per = gson.fromJson(body, Persona.class);	
			String mail = per.getMail();
			String response =  Data_persona.enviar_token(mail);
			
			if (response.equals("No existe mail")) {
				
				respuesta = "No existe usuario con ese mail";
		    	exchange.sendResponseHeaders(401, respuesta.getBytes().length);
				
			}
			else {
				respuesta = "ok";
				exchange.sendResponseHeaders(200, respuesta.getBytes().length);
			}
			
			
		    }
		    catch(Error e){
		    	respuesta = "Error en la conexion con la base de datos";
		    	exchange.sendResponseHeaders(401, respuesta.getBytes().length);
		    }
			
		    
		    OutputStream os = exchange.getResponseBody();
            os.write(respuesta.getBytes(StandardCharsets.UTF_8));
            os.close();
		}
	}
	

	
	
	
	
	public static class login implements HttpHandler {

	    public void handle(HttpExchange exchange) throws IOException {
	        Cors.controlCors(exchange);

	        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
	            exchange.sendResponseHeaders(204, -1);
	            exchange.close();
	            return;
	        }

	        InputStream is = exchange.getRequestBody();
	        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
	        is.close();

	        Gson gson = new Gson();
	        Persona per_login = gson.fromJson(body, Persona.class);

	        String token = "";
	        int codigoEstado = 401;

	        try {
	            Persona per = Data_persona.buscar_solo_persona_pormail(per_login.getMail());

	            if (per != null && per.getNombre_usuario() != null && !"inactivo".equalsIgnoreCase(per.getEstado())) {
	                Boolean resultado = Data_persona.buscar_persona(per_login.getMail(), per_login.getContrasena());

	                if (Boolean.TRUE.equals(resultado)) {
	                    System.out.println("Usuario existe y la contrasenia es correcta");
	                    token = GeneracionWebToken.enviotoken(per_login.getMail(), per.getRol());
	                    codigoEstado = 200;
	                }
	            }
	        
	        }
	        catch (Exception e) {
	        	
	        	codigoEstado = 401;
	        }
	        String jsonRespuesta = "{\"token\":\"" + token + "\"}";
	        byte[] bytes = jsonRespuesta.getBytes(StandardCharsets.UTF_8);

	        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
	        exchange.sendResponseHeaders(codigoEstado, bytes.length);

	        OutputStream os = exchange.getResponseBody() ;
            os.write(bytes);
            os.flush();
	        exchange.close();
	    }
	}

	
	

	

	public static class obtenerUsuarioToken implements HttpHandler {
	
		
		
		public void handle(HttpExchange exchange) throws IOException {
			
		
			int codigoestado;
			Persona perr = new Persona();
			
			
			
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
	
	    	    
	    	    String mail = claims.getSubject();
				
					perr = Data_persona.buscar_solo_persona_pormail(mail);
					System.out.println(perr);
					codigoestado = 200;
					
		    	
		    }
		    catch(Exception e ) {
		    	
		    	
		    	codigoestado = 401;
		    	System.out.println(e);
		    	
		    	
		    }
		    
		    Gson gson = new Gson();
		    String jsonRespuesta = gson.toJson(perr);
		    exchange.sendResponseHeaders(codigoestado, jsonRespuesta.getBytes().length);	
		    OutputStream os = exchange.getResponseBody();
	       os.write(jsonRespuesta.getBytes(StandardCharsets.UTF_8));
	       os.close();

		}
	
}
	
	
	public static class registro implements HttpHandler{
		
		public void handle(HttpExchange exchange) throws IOException {
			
			Persona persona_repetida = new Persona();
			String var;
	
			Cors.controlCors(exchange);
			
			
			if (exchange.getRequestMethod().equals("OPTIONS")) {
		        exchange.sendResponseHeaders(204, -1);
		        return;
		    }
			
			String mensaje = "funcionando";
			
			
			try {
			InputStream is = exchange.getRequestBody();
			String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
			
			
			Gson gson = new Gson();
			Persona per = gson.fromJson(body, Persona.class);	
			
			persona_repetida = Data_persona.buscar_solo_persona_pormail(per.getMail());
			
			if(persona_repetida == null || persona_repetida.getMail() == null) {
				
				var = "noexiste";			
			}	
			else {
				var = persona_repetida.getMail();
			}
			
			if( var.equals(per.getMail())) {
				
				mensaje = "usuario duplicadoooo";
		        exchange.sendResponseHeaders(409, mensaje.getBytes().length);
		        OutputStream os = exchange.getResponseBody();
	            os.write(mensaje.getBytes()); 
	            os.close();
			}
			
			else {
			
			Data_persona.insertar_persona(per.getNombre_usuario(), per.getContrasena(), per.getMail(), "usuario", per.getFoto_perfil());		
			exchange.sendResponseHeaders(200, mensaje.getBytes().length);
			OutputStream os = exchange.getResponseBody();
            os.write(mensaje.getBytes()); 
            os.close();
			}
			
			}catch (SQLException e) {
			    System.out.println(e.getErrorCode());

			    mensaje = "error al acceder a la bd";
			    exchange.sendResponseHeaders(403, mensaje.getBytes().length);
			    
			    
			    OutputStream os = exchange.getResponseBody();
			    os.write(mensaje.getBytes());
			    os.close();
			} catch (Exception e) {
			    // Para otros errores generaless
			    e.printStackTrace();
			
			}
					
			
			
		}
		
	}

}
