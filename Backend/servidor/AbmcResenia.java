package servidor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.crypto.SecretKey;

import data.Conexion;
import data.Cors;
import data.Data_persona;
import entities.Juego;
import entities.Persona;
import entities.Resenia;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

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
		try {
			// crear una conexión
			Connection conn = Conexion.getInstancia().getConn();

			// ejecutar la query
            Statement stmt = conn.createStatement();
            String sql = "SELECT r.*, " +
                    "j.titulo AS nombre_juego, " +
                    "j.imagen AS foto_juego, " +
                    "p.nombre AS nombre_usuario, " +
                    "g.nombre AS nombre_grupo " +
                    "FROM resenia r " +
                    "INNER JOIN juego j ON r.id_juego = j.idjuego " +
                    "INNER JOIN persona p ON r.mail_usuario = p.mail " +
                    "LEFT JOIN grupo g ON p.idgrupo = g.idgrupo";
            ResultSet rs= stmt.executeQuery(sql);

            // mapear de resultset a objeto
            while(rs.next()) {
            	Resenia r=new Resenia();
            	
                r.setId_juego(rs.getInt("id_juego"));
                r.setTitulo(rs.getString("titulo"));
                r.setDescripcion(rs.getString("descripcion"));
                r.setFecha(rs.getString("fecha"));
                r.setHora(rs.getString("hora"));
                r.setPuntaje(rs.getFloat("puntaje"));
                r.setMail_usuario(rs.getString("mail_usuario"));
                
                Juego j = new Juego();
                j.setId_juego(rs.getString("id_juego"));
                j.setTitulo(rs.getString("nombre_juego")); 
                j.setImagen(rs.getString("foto_juego"));   
                r.setJuego(j);

                
                Persona p = new Persona();
                p.setMail(rs.getString("mail_usuario"));
                p.setNombre_usuario(rs.getString("nombre_usuario")); 
                p.setNombre_grupo(rs.getString("nombre_grupo"));
                r.setUsuario(p);
                
                resenias.add(r);

               

            }
            //cerrar recursos
            if(rs!=null){rs.close();}
            if(stmt!=null){stmt.close();}

		    
		    
		    // mostrar info
		    System.out.println("Listado Completo");
		    System.out.println(resenias);
		    System.out.println();System.out.println();
		    
		    
		    
		    
		    

		} catch (SQLException ex) {
		    // Manejo de errores
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		}
		return resenias;
		
	}
	
	public static boolean actualizar(Resenia r) {

	    String sql = "UPDATE resenia SET titulo = ?, descripcion = ?, puntaje = ? " +
	                 "WHERE id_juego = ? AND mail_usuario = ?";
	    Connection conn = Conexion.getInstancia().getConn();
	    try (
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setString(1, r.getTitulo());
	        stmt.setString(2, r.getDescripcion());
	        stmt.setFloat(3, r.getPuntaje());
	        stmt.setInt(4, r.getId_juego());
	        stmt.setString(5, r.getMail_usuario());

	        int filasAfectadas = stmt.executeUpdate();

	        return filasAfectadas > 0;

	    } catch (SQLException e) {
	        System.out.println(e);
	        return false;
	    }
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

	            
	            Resenia reseniaExistente = recuperarPorIdJuegoYmail(reseniaRecibida.getId_juego(), mail);


	            if (reseniaExistente == null) {
	            	 codigoestado = 404;
	           
	                
	            }else {
	            	if (Moderacion.contienePalabrasProhibidas(reseniaRecibida.getDescripcion()).isEmpty() == false || Moderacion.contienePalabrasProhibidas(reseniaRecibida.getTitulo()).isEmpty()==false) {
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
	            boolean actualizado = actualizar(reseniaExistente);
	            
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
    
	
	public static void insertarNuevo(int idJuego, String mailUsuario, String titulo, String descripcion, float puntaje) {
		Resenia resenia= new Resenia();
		
		 resenia.setId_juego(idJuego);
		 resenia.setMail_usuario(mailUsuario);
		 resenia.setTitulo(titulo);
		 resenia.setDescripcion(descripcion);
		 resenia.setPuntaje(puntaje);
		 Connection conn = Conexion.getInstancia().getConn();
		try {
			
			// definir la query
            PreparedStatement pstmt = conn.prepareStatement(
            		"insert into resenia(id_juego,fecha,hora,titulo,descripcion,puntaje,mail_usuario) values (?,?,?,?,?,?,?)"
            		,PreparedStatement.RETURN_GENERATED_KEYS
            		);
            
            
            LocalDate fecha = LocalDate.now();
            LocalTime hora = LocalTime.now();

            pstmt.setInt(1, idJuego);
            pstmt.setString(2, fecha.toString());
            pstmt.setString(3, hora.toString());
            pstmt.setString(4, titulo);
            pstmt.setString(5, descripcion);
            pstmt.setString(6, String.valueOf(puntaje));
            pstmt.setString(7, mailUsuario);

            pstmt.executeUpdate();

            if (pstmt != null) { pstmt.close(); }

           

        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
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
	        if (Moderacion.contienePalabrasProhibidas(nuevaResenia.getDescripcion()).isEmpty() == false|| Moderacion.contienePalabrasProhibidas(nuevaResenia.getTitulo()).isEmpty() == false) {
	                
	                String error = "La reseña contiene palabras prohibidas.";
	                exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
	                exchange.sendResponseHeaders(400, error.getBytes().length);
	                
	                OutputStream os = exchange.getResponseBody();
	                os.write(error.getBytes());
	                os.close();
	                 
	                return; 
	            }
	        	
	        
	        	
	        
	        
	        else {
	        	if (existeResenia(nuevaResenia.getId_juego(), mail)) {
	        
	            
	            String error = "ya escribiste una reseña para este juego.";
	            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
	            exchange.sendResponseHeaders(400, error.getBytes().length);
	            OutputStream os = exchange.getResponseBody();
	            os.write(error.getBytes());
	            os.close();
	        } else {
	            
	            insertarNuevo(nuevaResenia.getId_juego(),nuevaResenia.getMail_usuario(),nuevaResenia.getTitulo(),nuevaResenia.getDescripcion(),nuevaResenia.getPuntaje());
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
	public static boolean existeResenia(int idJuego, String mailUsuario) {
		boolean existe = false;

		
		try {
			Connection conn = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			conn = Conexion.getInstancia().getConn();
			String sql = "SELECT COUNT(*) AS total FROM resenia WHERE id_juego = ? AND mail_usuario = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, idJuego);
			stmt.setString(2, mailUsuario);
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				if(rs.getInt("total")>0) {
					existe=true;
				}
			} 
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
		}
		return existe;
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
	                
	                List<Resenia> listaResenias = recuperarPorIdJuego(idJuego);
	                
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
	
	public static List<Resenia> recuperarPorIdJuego(int id) {

		LinkedList<Resenia> lista = new LinkedList<>();



		try {

			// crear una conexión
	
			Connection conn = Conexion.getInstancia().getConn();

			// definir la query
	
			PreparedStatement stmt = conn.prepareStatement("select * from resenia where id_juego=?");

			// setear el/los parámetros
	
			stmt.setInt(1, id);

			// ejecutar query y obtener resultados
	
			ResultSet rs = stmt.executeQuery();

			// mapear cada fila del resultset a un objeto y agregarlo a la lista
	
			while (rs.next()) {
	
				Resenia r = new Resenia();
				
				Persona p = new Persona();

				r.setId_juego(rs.getInt("id_juego"));
		
				r.setFecha(rs.getString("fecha"));
		
				r.setHora(rs.getString("hora"));
		
				r.setTitulo(rs.getString("titulo"));
		
				r.setDescripcion(rs.getString("descripcion"));
		
				r.setPuntaje(rs.getFloat("puntaje"));
		
				r.setMail_usuario(rs.getString("mail_usuario"));
				
				p = Data_persona.buscar_solo_persona_pormail(r.getMail_usuario());
				
				r.setUsuario(p);
		
				lista.add(r);
	
			}		
				// cerrar recursos
		
				if (rs != null) { rs.close(); }
		
				if (stmt != null) { stmt.close(); }
		

				// mostrar objetos
		
	
			} catch (SQLException ex) {
	
				// Manejo de errores
		
				System.out.println("SQLException: " + ex.getMessage());
		
				System.out.println("SQLState: " + ex.getSQLState());
		
				System.out.println("VendorError: " + ex.getErrorCode());
		
			}
	
			return lista;

		}
	
	public static Resenia recuperarPorIdJuegoYmail(int id, String mail) {

		Resenia r = new Resenia();



		try {

			// crear una conexión
	
			Connection conn = Conexion.getInstancia().getConn();
	
	
	
			// definir la query
	
			PreparedStatement stmt = conn.prepareStatement("select * from resenia where id_juego=? and mail_usuario=?");
	
	
	
			// setear el/los parámetros
	
			stmt.setInt(1, id);
			stmt.setString(2, mail);
	
	
	
			// ejecutar query y obtener resultados
	
			ResultSet rs = stmt.executeQuery();
	
	
	
			// mapear cada fila del resultset a un objeto y agregarlo a la lista
	
			while (rs.next()) {
	
				
				Persona p = new Persona();
				
				
		
				r.setId_juego(rs.getInt("id_juego"));
		
				r.setFecha(rs.getString("fecha"));
		
				r.setHora(rs.getString("hora"));
		
				r.setTitulo(rs.getString("titulo"));
		
				r.setDescripcion(rs.getString("descripcion"));
		
				r.setPuntaje(rs.getFloat("puntaje"));
		
				r.setMail_usuario(rs.getString("mail_usuario"));
				
				p = Data_persona.buscar_solo_persona_pormail(r.getMail_usuario());
				
				r.setUsuario(p);
		
				
	
			}
	
		
		
				// cerrar recursos
		
				if (rs != null) { rs.close(); }
		
				if (stmt != null) { stmt.close(); }
		
				
		
		
		
				// mostrar objetos
		
				System.out.println("Buscar por idjuego y mailusuario");
		
				System.out.println();
		
				System.out.println();
	
	
	
			} catch (SQLException ex) {
	
				// Manejo de errores
		
				System.out.println("SQLException: " + ex.getMessage());
		
				System.out.println("SQLState: " + ex.getSQLState());
		
				System.out.println("VendorError: " + ex.getErrorCode());
		
			}
	
			return r;

		}
	
	public static List<Resenia> recuperarPorMailUsuario(String mail_usuario) {
		LinkedList<Resenia> lista = new LinkedList<>();

		try {
			// crear una conexión
			Connection conn = Conexion.getInstancia().getConn();

			// definir la query
			PreparedStatement stmt = conn.prepareStatement("select * from resenia where mail_usuario=?");

			// setear el/los parámetros
			stmt.setString(1, mail_usuario);

			// ejecutar query y obtener resultados
			ResultSet rs = stmt.executeQuery();

			// mapear cada fila del resultset a un objeto y agregarlo a la lista
			while (rs.next()) {
				Resenia r = new Resenia();
				r.setId_juego(rs.getInt("id_juego"));
				r.setFecha(rs.getString("fecha"));
				r.setHora(rs.getString("hora"));
				r.setTitulo(rs.getString("titulo"));
				r.setDescripcion(rs.getString("descripcion"));
				r.setPuntaje(rs.getFloat("puntaje"));
				r.setMail_usuario(rs.getString("mail_usuario"));
				lista.add(r);
			}

			// cerrar recursos
			if (rs != null) { rs.close(); }
			if (stmt != null) { stmt.close(); }
			

			// mostrar objetos
			System.out.println("Buscar por mail usuario");
			System.out.println();
			System.out.println();

		} catch (SQLException ex) {
			// Manejo de errores
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
		return lista;
	}
	
	public static boolean eliminarResenia(String mailusuario, int id_juego) {
	    boolean exito = false;
	    

	    try {
	        Connection conn = Conexion.getInstancia().getConn();
	        
	        
	        String sql = "DELETE FROM resenia WHERE mail_usuario = ? AND id_juego = ?";
	        PreparedStatement stmt = conn.prepareStatement(sql);
	        
	        stmt.setString(1, mailusuario);
	        stmt.setInt(2, id_juego);
	        
	        
	        int afectado = stmt.executeUpdate();
	        if (afectado > 0) {
	            exito = true;
	        }

	        if (stmt != null) { stmt.close(); }

	    } catch (SQLException ex) {
	        System.out.println("SQLException: " + ex.getMessage());
	        System.out.println("SQLState: " + ex.getSQLState());
	        System.out.println("VendorError: " + ex.getErrorCode());
	    }

	    return exito;
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
	                
	                Resenia reseniaExistente = recuperarPorIdJuegoYmail(idJuego, mail);
	                
	                if (reseniaExistente == null) {
	                    responder(exchange, 404, "{\"error\": \"no se encontro la resenia\"}");
	                    return;
	                }

	                boolean borrado = eliminarResenia(mail, idJuego);

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
