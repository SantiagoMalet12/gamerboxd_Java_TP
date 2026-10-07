package servidor;


import java.util.LinkedList;
import java.util.List;

import javax.crypto.SecretKey;



import data.DataResenia;


import entities.Resenia;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class AbmcResenia {
	private static final SecretKey KEY = GeneracionWebToken.llaveJWT();

	
	
	
	
	
	
	public static LinkedList<Resenia> recuperarTodos() {

		
		LinkedList<Resenia> resenias = new LinkedList<>();
		
		resenias = DataResenia.recuperarTodos();
		
		
		return resenias;
		
	}
	

	
	public static class editarResenia implements HttpHandler{
		@Override
		public void handle(HttpExchange exchange) throws IOException{
			
			 exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
		        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS, POST, PUT, DELETE");
		        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
		        
		        
		        if ("OPTIONS".equals(exchange.getRequestMethod())) {
		            exchange.sendResponseHeaders(204, -1);
		            return;
		        }
		        int codigoestado;
				 String mensaje = "";
			
			try {

	            
	            String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
	            String token = authHeader.substring(7);

	            Claims claims = Jwts.parser()
	                    .verifyWith(KEY)
	                    .build()
	                    .parseSignedClaims(token)
	                    .getPayload();

	            String mail = claims.getSubject();
	           

	            Gson gson = new Gson();
	            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
	            Resenia reseniaRecibida = gson.fromJson(isr, Resenia.class);
	            System.out.println("resenia que traigo: "+reseniaRecibida);

	            
	            Resenia reseniaExistente = DataResenia.recuperarPorIdJuegoYmail(reseniaRecibida.getId_juego(), mail);


	            if (reseniaExistente == null) {
	            	 codigoestado = 404;
	           
	                
	            }else {
	            	if (Moderacion.contienePalabrasProhibidas(reseniaRecibida.getDescripcion().toLowerCase()).isEmpty() == false || Moderacion.contienePalabrasProhibidas(reseniaRecibida.getTitulo().toLowerCase()).isEmpty()==false) {
	            		String error = "La reseña contiene palabras prohibidas";
	            		exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
	                    exchange.sendResponseHeaders(400, error.getBytes().length);
	                    OutputStream os = exchange.getResponseBody();
	                    os.write(error.getBytes());
	                    os.close();
	                    return;
	    	        	
	    	        }
	            
		            reseniaExistente.setTitulo(reseniaRecibida.getTitulo());
		            reseniaExistente.setPuntaje(reseniaRecibida.getPuntaje());
		            reseniaExistente.setDescripcion(reseniaRecibida.getDescripcion());
	            boolean actualizado = DataResenia.actualizar(reseniaExistente);
	            
	            if (actualizado) {
	            	mensaje = "Resenia actualizada correctamente";
	                exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
	                exchange.sendResponseHeaders(200, mensaje.getBytes().length);
	                OutputStream os = exchange.getResponseBody();
	                os.write(mensaje.getBytes());
	                os.close();
	            } else {
	            	String error = "No se pudo actualizar la resenia";
	                exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
	                exchange.sendResponseHeaders(500, error.getBytes().length);
	                OutputStream os = exchange.getResponseBody();
	                os.write(error.getBytes());
	                os.close();	            }
	            }
	            

	        } catch (Exception e) {
	        	codigoestado=401;
	            System.out.println(e);
	            enviarRespuesta(exchange, 401, "Token invalido o error en la solicitud");
	        }
	    }
		
	    
	    private void enviarRespuesta(HttpExchange exchange, int codigoestado, String mensaje) throws IOException {
	        Gson gson = new Gson();
	        String jsonRespuesta = gson.toJson(mensaje);
	        byte[] responseBytes = jsonRespuesta.getBytes(StandardCharsets.UTF_8);
	        exchange.sendResponseHeaders(codigoestado, responseBytes.length);
	        OutputStream os = exchange.getResponseBody();
	        os.write(responseBytes);
	        os.close();
	    }
	}
			
			

	
	public static class obtenerResenias implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
			exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
			exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
			if ("GET".equals(exchange.getRequestMethod())) {
				LinkedList<Resenia> listaResenias = recuperarTodos();
				
				Gson gson = new Gson();
				String jsonResponse = gson.toJson(listaResenias);
				
				byte[] bytesResponse = jsonResponse.getBytes("UTF-8");
				exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
				exchange.sendResponseHeaders(200, bytesResponse.length);
				
				OutputStream os = exchange.getResponseBody();
				os.write(bytesResponse);
				os.close();
				
			} else {
				exchange.sendResponseHeaders(405, -1);
			}
		}
	}
    
	
	
	
	public static class nuevaResenia implements HttpHandler{
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
	        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
	        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
	        
	        if ("OPTIONS".equals(exchange.getRequestMethod())) {
	            exchange.sendResponseHeaders(204, -1);
	            return;
	        }
	        
	        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
	    	
	    	String token = authHeader.substring(7);
	    	
    	    
    	    Claims claims = Jwts.parser()
    	    		.verifyWith(KEY) 
    	            .build()
    	            .parseSignedClaims(token)
    	            .getPayload();

    	    
    	    String mail = claims.getSubject();
    	   
    	    Gson gson = new Gson();
    	    InputStream is = exchange.getRequestBody();
    	    String jsonBody = new String(is.readAllBytes(), StandardCharsets.UTF_8);
	        Resenia nuevaResenia = gson.fromJson(jsonBody, Resenia.class);
	        nuevaResenia.setMail_usuario(mail);
	        if (Moderacion.contienePalabrasProhibidas(nuevaResenia.getDescripcion().toLowerCase()).isEmpty() == false|| Moderacion.contienePalabrasProhibidas(nuevaResenia.getTitulo().toLowerCase()).isEmpty() == false) {
	                
	                String error = "La reseña contiene palabras prohibidas.";
	                exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
	                exchange.sendResponseHeaders(400, error.getBytes().length);
	                
	                OutputStream os = exchange.getResponseBody();
	                os.write(error.getBytes());
	                os.close();
	                 
	                return; 
	            }
	        	
	        
	        	
	        
	        
	        else {
	        	if (DataResenia.existeResenia(nuevaResenia.getId_juego(), mail)) {
	        
	            
	            String error = "ya escribiste una reseña para este juego.";
	            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
	            exchange.sendResponseHeaders(400, error.getBytes().length);
	            OutputStream os = exchange.getResponseBody();
	            os.write(error.getBytes());
	            os.close();
	        } else {
	            
	            DataResenia.insertarNuevo(nuevaResenia.getId_juego(),nuevaResenia.getMail_usuario(),nuevaResenia.getTitulo(),nuevaResenia.getDescripcion(),nuevaResenia.getPuntaje());
	            String exito = "reseña guardada correctamente";
	            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
	            exchange.sendResponseHeaders(200, exito.getBytes().length);
	            OutputStream os = exchange.getResponseBody();
	            os.write(exito.getBytes());
	            os.close();
	        }
		}
	}
	}

	
	public static class obtenerReseniasPorJuego implements HttpHandler {
	    @Override
	    public void handle(HttpExchange exchange) throws IOException {
	        
	        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
	        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
	        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
	        
	        
	        if ("OPTIONS".equals(exchange.getRequestMethod())) {
	            exchange.sendResponseHeaders(204, -1);
	            return;
	        }

	        if ("GET".equals(exchange.getRequestMethod())) {
	            String query = exchange.getRequestURI().getQuery();
	            int idJuego = -1;

	            if (query.contains("id=")) {
	                try {
	                    String[] parametros = query.split("&");
	                    for (String param : parametros) {
	                        if (param.startsWith("id=")) {
	                            idJuego = Integer.parseInt(param.split("=")[1]);
	                            break;
	                        }
	                    }
	                } catch (NumberFormatException e) {
	                    System.out.println("Error: El ID pasado no es un número válido.");
	                }
	            }

	            if (idJuego != -1) {
	                
	                List<Resenia> listaResenias = DataResenia.recuperarPorIdJuego(idJuego);
	                
	                Gson gson = new Gson();
	                String jsonResponse = gson.toJson(listaResenias);
	                
	                byte[] bytesResponse = jsonResponse.getBytes("UTF-8");
	                exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
	                exchange.sendResponseHeaders(200, bytesResponse.length);
	                
	                OutputStream os = exchange.getResponseBody();
	                os.write(bytesResponse);
	                os.close();
	            } else {
	                String error = "Falta el parametro 'id' en la URL";
	                byte[] bytesError = error.getBytes("UTF-8");
	                exchange.sendResponseHeaders(400, bytesError.length);
	                OutputStream os = exchange.getResponseBody();
	                os.write(bytesError);
	                os.close();
	            }
	            
	        } else {
	            
	            exchange.sendResponseHeaders(405, -1);
	        }
	    }
	}
	



	
	public static class borrarResenia implements HttpHandler {
	    @Override
	    public void handle(HttpExchange exchange) throws IOException {
	    	exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
	        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
	        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
	        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
	            exchange.sendResponseHeaders(204, -1); // 204 No Content, sin body
	            exchange.close();
	            return;
	        }
	        
	        

	        String metodo = exchange.getRequestMethod();
	        if (metodo.equalsIgnoreCase("DELETE")) {
	            try {
	                String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
	                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
	                    responder(exchange, 401, "{\"error\": \"Falta token\"}");
	                    return;
	                }
	                String token = authHeader.substring(7);

	                Claims claims = Jwts.parser()
	                        .verifyWith(KEY)
	                        .build()
	                        .parseSignedClaims(token)
	                        .getPayload();

	                String mail = claims.getSubject();
	               

	                
	                String query = exchange.getRequestURI().getQuery();
	                if (!query.startsWith("id=")) {
	                    responder(exchange, 400, "{\"error\": \"Falta el parametro id\"}");
	                    return;
	                }
	                int idJuego = Integer.parseInt(query.split("=")[1]);
	                
	                Resenia reseniaExistente = DataResenia.recuperarPorIdJuegoYmail(idJuego, mail);
	                
	                if (reseniaExistente == null) {
	                    responder(exchange, 404, "{\"error\": \"no se encontro la resenia\"}");
	                    return;
	                }

	                boolean borrado = DataResenia.eliminarResenia(mail, idJuego);

	                if (borrado) {
	                    responder(exchange, 200, "{\"mensaje\": \"Resenia eliminada\"}");
	                } else {
	                    responder(exchange, 404, "{\"error\": \"no se encontro la resenia\"}");
	                }

	            } catch (Exception e) {
	                e.printStackTrace();
	                responder(exchange, 403, "{\"error\": \"Error de autenticacion\"}");
	            }
	        } else {
	            responder(exchange, 405, "{\"error\": \"Metodo no permitido\"}");
	        }

	        }
	    private static void responder(HttpExchange exchange, int status, String json) throws IOException {
	        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
	        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
	        exchange.sendResponseHeaders(status, bytes.length);
	        OutputStream os = exchange.getResponseBody();
	        os.write(bytes);
	        os.close();
	    }
  
	                
	} 
	            
	        
	    
	
	
}
