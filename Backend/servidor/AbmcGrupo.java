package servidor;
import data.Data_persona;



import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.crypto.SecretKey;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.OutputStream;

import data.Conexion;
import data.Cors;
import data.DataGrupo;
import data.DataJuego;
import data.DataPropuesta;
import data.Data_persona;
import entities.Grupo;
import entities.Juego;
import entities.Persona;
import entities.Plataforma;
import entities.Propuesta;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class AbmcGrupo {
	private static final SecretKey KEY = GeneracionWebToken.llaveJWT();
	//ABMC Grupo uso
	
	//AbmcGrupo.insertarNuevo("foto2.jpg", "IGN", "Grupo reconocitdo internacionalmente ");
	//Grupo g = AbmcGrupo.recuperarPorId(1);
	//LinkedList<Grupo> grupos = AbmcGrupo.recuperarTodos();
	//ArrayList<Grupo> grupos = AbmcGrupo.recuperarPorNombre("IG");

	
	
	public static class obtenergruporeseniador implements HttpHandler {
		
	    public void handle(HttpExchange exchange) throws IOException {
	        
	        String respuestadb = "{\"error\":\"error\"}"; 
	        
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
	  
	            try {
	                String nombre = DataGrupo.nombregrupo(per.getMail());
	                // Armamos el JSON que espera tu React (dataa.nombregrupo)
	                respuestadb = "{\"nombregrupo\":\"" + nombre + "\"}";
	                
	                byte[] bytes = respuestadb.getBytes(StandardCharsets.UTF_8);
	                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
	                exchange.sendResponseHeaders(200, bytes.length);
	            } catch(Exception e) {
	                respuestadb = "{\"error\":\"error\"}";
	                byte[] bytes = respuestadb.getBytes(StandardCharsets.UTF_8);
	                exchange.sendResponseHeaders(500, bytes.length);
	            }

	        } catch(Exception e) {
	            respuestadb = "{\"error\":\"unauthorized\"}";
	            byte[] bytes = respuestadb.getBytes(StandardCharsets.UTF_8);
	            exchange.sendResponseHeaders(401, bytes.length);
	        }
	        
	        OutputStream os = exchange.getResponseBody();
	        os.write(respuestadb.getBytes(StandardCharsets.UTF_8));
	        os.close();
	    }
	}
	
	public static class actualizardatosgrupo implements HttpHandler {
		
		
		public void handle(HttpExchange exchange) throws IOException {
			
			
			Boolean existe;
			Grupo grupo = new Grupo();
			
			
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
				    grupo = gson.fromJson(body, Grupo.class);
				    
				   
				    	
				    existe = DataGrupo.existegrupo(grupo.getNombre());	
				   
				    
				    if(existe) {
				    	
					respuesta = "Grupo repetido";
						
			    	exchange.sendResponseHeaders(409, respuesta.getBytes().length);
				    }
				    
				    else if (!existe) {
				    	
				    	try {
				    		
				    		String valor =DataGrupo.actualizarGrupo(grupo.getId(), grupo.getNombre(), grupo.getFoto_perfil(), grupo.getDescripcion());			    	    			    	 
				    	    System.out.println(valor);
				    		
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
	
	
	
	public static class dardebajagrupo implements HttpHandler {
		
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
	    	    
	    	    
	    	    
	    	    
	    	    InputStream is = exchange.getRequestBody();
			    String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
			    is.close();
			    Gson gson = new Gson();
				Grupo gru = gson.fromJson(body, Grupo.class);
	    	    
	  
				respuestadb = DataGrupo.dardebajagrupo(gru.getId());
				
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
	
	
	
	
	
	
	
	
	
	
	
	
	public static class listarGrupos implements HttpHandler{
		String respuesta;
		LinkedList<Grupo> grupos = new LinkedList<>();
		

		public void handle(HttpExchange exchange) throws IOException {
			
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
    
		    	
		    	
		    }catch(Error e) {
		    	
		    	respuesta = "Error token";
		    	
		    	exchange.sendResponseHeaders(403, respuesta.getBytes().length);
		    	return;
		    	
		    }
		    
		    
		    try {
		    	
		    	Gson gson = new Gson();
		    	
		    	grupos = DataGrupo.listargrupos();
		    	
		    	respuesta = gson.toJson(grupos);
		    	
		    	exchange.sendResponseHeaders(200, respuesta.getBytes().length);
		    	
				
   	
		    }catch(Error e) {
		    	
		    	respuesta = "Error";
		    	exchange.sendResponseHeaders(400, respuesta.getBytes().length);
		    	return;
		    	
		    	
		    }
				

		   
		   OutputStream os = exchange.getResponseBody();
	       os.write(respuesta.getBytes(StandardCharsets.UTF_8));
	       os.close();
		    
		    
				
			
			
			
			
		}
	}
	
	
	
	
	
	
	
	public static LinkedList<Grupo> recuperarTodos() {
		LinkedList<Grupo> grupos = new LinkedList<>();
		try {
			// crear una conexión
			Connection conn = Conexion.getInstancia().getConn();

			// ejecutar la query
            Statement stmt = conn.createStatement();
            ResultSet rs= stmt.executeQuery("select * from grupo");

            // mapear de resultset a objeto
            while(rs.next()) {
            	Grupo g=new Grupo();
                g.setId(rs.getInt("idgrupo"));
                g.setNombre(rs.getString("nombre"));
                g.setFoto_perfil(rs.getString("foto_perfil"));
                g.setDescripcion(rs.getString("descripcion"));

                grupos.add(g);

            }
            //cerrar recursos
            if(rs!=null){rs.close();}
            if(stmt!=null){stmt.close();}

		    conn.close();
		    
		    // mostrar info
		    System.out.println("Listado Completo");
		    System.out.println(grupos);
		    System.out.println();System.out.println();
		    
		    
		    

		} catch (SQLException ex) {
		    // Manejo de errores
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		}
		return grupos;
		
	}
	
	public static Grupo recuperarPorId(int id) {		
		Grupo g = null;

		try {
			// crear una conexión
			Connection conn = Conexion.getInstancia().getConn();

			// definir la query
            PreparedStatement stmt = conn.prepareStatement("select * from grupo where idgrupo=?");
            
            // setear el/los parámetros
            stmt.setInt(1, id);

            

            // ejecutar query y obtener resultados
            ResultSet rs= stmt.executeQuery();

            // mapear de resultset a objeto
            if(rs.next()) {
        		g=new Grupo();
                g.setId(rs.getInt("idgrupo"));
                g.setNombre(rs.getString("nombre"));
            }
            //cerrar recursos
            if(rs!=null){rs.close();}
            if(stmt!=null){stmt.close();}

		    conn.close();
		    
		    // mostrar objeto
		    System.out.println("Buscar por id");
		    System.out.println(g);
		    System.out.println();System.out.println();

		} catch (SQLException ex) {
		    // Manejo de errores
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		}
		return g;
	}
	
	public static class creargrupo implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
			exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
			exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
			
			

			// preflight por el post
			if ("OPTIONS".equals(exchange.getRequestMethod())) {
				exchange.sendResponseHeaders(204, -1);
				return;
			}
			String respuesta = "aaa no seee";
			int codigoestado;
			String mail = "";
			try {
		    	
		    	
		    	String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
		    	String token = authHeader.substring(7);
		    	System.out.println("TOKEN EXTRAÍDO PARA REVISAR: [" + token + "]");
	    	    
	    	    Claims claims = Jwts.parser()
	    	    		.verifyWith(KEY) 
	    	            .build()
	    	            .parseSignedClaims(token)
	    	            .getPayload();

	    	    
	    	    mail = claims.getSubject();
		    	
		    	
		    }
		    catch(Exception e ) {
		    	
		    	System.out.println(e);
		    	respuesta = "Error";
		    	codigoestado = 401;
		    	
		    	
		    }

			if ("POST".equals(exchange.getRequestMethod())) {
				InputStream is = exchange.getRequestBody();
				String body = new String(is.readAllBytes(), "UTF-8");
				
				Gson gson = new Gson();
				Grupo nuevoGrupo = gson.fromJson(body, Grupo.class);
				boolean existe = false;
				ArrayList<Grupo> gruposExistentes = AbmcGrupo.recuperarPorNombre(nuevoGrupo.getNombre());
				for (Grupo g : gruposExistentes) {
				    if (g.getNombre().equalsIgnoreCase(nuevoGrupo.getNombre())) {
				        existe = true;
				        break;
				    }
				}
				
				if (existe) {
				    
				    String errorResponse = "{\"status\":\"error\", \"mensaje\":\"El nombre del grupo ya esta en uso\"}";
				    byte[] bytesError = errorResponse.getBytes("UTF-8");
				    
				    exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
				    exchange.sendResponseHeaders(400, bytesError.length); // 400 = Petición incorrecta
				    
				    OutputStream os = exchange.getResponseBody();
				    os.write(bytesError);
				    os.close();
				    return; 
				}
				
				
				Persona u = Data_persona.buscar_solo_persona_pormail(mail);
				Grupo gPersona = Data_persona.obtener_grupo_persona(u);
				System.out.println("grupo: "+gPersona);
				if (gPersona != null) { 
				    String errorResponse = "{\"status\":\"error\", \"mensaje\":\"Ya pertenecés a un grupo. \"}";
				    byte[] bytesError = errorResponse.getBytes("UTF-8");
				    exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
				    exchange.sendResponseHeaders(409, bytesError.length);
				    try (OutputStream os = exchange.getResponseBody()) { os.write(bytesError); }
				    return;
				}
				ArrayList<Persona> personas = new ArrayList<>();
				personas.add(u);
				nuevoGrupo.setIntegrantes(personas);
				
				
				//el primer parametro es la foto de perfil

				System.out.println("llegue aca");
				insertarNuevo(nuevoGrupo.getFoto_perfil(), nuevoGrupo.getNombre(), nuevoGrupo.getDescripcion(), personas, mail, u);
				
				String jsonResponse = "{\"status\":\"ok\", \"mensaje\":\"Grupo insertado correctamente\"}";
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
	
	public static void insertarNuevo(String foto_perfil,String nombre,  String descripcion, ArrayList<Persona> integrantes, String mail, Persona u) {
		Grupo grupo= new Grupo();
		int id = -1;
		// esta re contra al pedo esto pero bueno lo dejo asi porque yafue :v
		grupo.setNombre(nombre);
		grupo.setFoto_perfil(foto_perfil);
		grupo.setDescripcion(descripcion);
		grupo.setIntegrantes(integrantes);
		u.setIdgrupo(grupo.getId());
		u.setRolgrupo("admin");
		System.out.println(grupo);
		try {
			Connection conn = Conexion.getInstancia().getConn();
			// definir la query
            PreparedStatement pstmtGrupo = conn.prepareStatement(
            		"insert into grupo(foto_perfil,nombre,descripcion) values (?,?,?)"
            		,PreparedStatement.RETURN_GENERATED_KEYS
            		);
            
            
            pstmtGrupo.setString(1, grupo.getFoto_perfil());
            pstmtGrupo.setString(2, grupo.getNombre());
            pstmtGrupo.setString(3, grupo.getDescripcion());
            pstmtGrupo.executeUpdate();
            
            ResultSet keyResultSet=pstmtGrupo.getGeneratedKeys();
            
            if(keyResultSet!=null && keyResultSet.next()) {
                    id=keyResultSet.getInt(1);
                    
                    grupo.setId(id);
            }
           
            
            if(id!=-1) {
            	
            	PreparedStatement pstmtUsuario = conn.prepareCall("UPDATE persona SET idgrupo = ?, rolgrupo = ? WHERE mail = ?");
            	
            	pstmtUsuario.setInt(1,id);
            	pstmtUsuario.setString(2,"admin");
            	pstmtUsuario.setString(3,mail);
            	
            	
            	pstmtUsuario.executeUpdate();
            	
            	
            	
            }
            
            

            if(keyResultSet!=null){keyResultSet.close();}

            

		    
		    
		    // mostrar objeto
		    System.out.println("lo creo");
		    System.out.println(grupo);
		    System.out.println();System.out.println();

		} catch (SQLException ex) {
			System.out.println("fallo aca");
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		}
	}
	
	public static ArrayList<Grupo> recuperarPorNombre(String nombre) {		
		ArrayList<Grupo> grupos = new ArrayList<>();

		try {
			// crear una conexión
			Connection conn = Conexion.getInstancia().getConn();

			// definir la query
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM grupo WHERE LOWER(nombre) LIKE LOWER(?)");
            
            // setear el/los parámetros
            stmt.setString(1, "%"+nombre+"%");

            

            // ejecutar query y obtener resultados
            ResultSet rs= stmt.executeQuery();

            // mapear de resultset a objeto
            while(rs.next()) {
        		Grupo g=new Grupo();
                g.setId(rs.getInt("idgrupo"));
                g.setNombre(rs.getString("nombre"));
                g.setDescripcion(rs.getString("descripcion"));
                g.setFoto_perfil(rs.getString("foto_perfil"));
                grupos.add(g);
            }
            //cerrar recursos
            if(rs!=null){rs.close();}
            if(stmt!=null){stmt.close();}

		    
		    // mostrar objeto
		    System.out.println("Buscar por nombre");
		    System.out.println(grupos);
		    System.out.println();System.out.println();

		} catch (SQLException ex) {
		    // Manejo de errores
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		}
		return grupos;
	}
	
	
	public static class aniadirMiembroAGrupo implements HttpHandler {
		
		boolean resultado;
		String mail;
		String respuesta = "error";
		
		public void handle(HttpExchange exchange) throws IOException {
			
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
	    	    
	    	    mail = claims.getSubject();
	    	    
	    	    
	    	    InputStream is = exchange.getRequestBody();
	    	    String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
	    	    is.close();
	    	    Gson gson = new Gson();
	    	    Grupo gru = gson.fromJson(body, Grupo.class);
	    	    	    	    
    	    	
	    	    resultado = DataGrupo.insertarMiembro(gru.getId(), mail);
	    	    
	    	   
	    	    
	    	    if(resultado) {		
	    	    	
    	    		respuesta = "Bien";
		    		exchange.sendResponseHeaders(200, respuesta.getBytes().length);
	    	    	
		    		
		    	}
	    	    
	    	    else {
	    	    	
	    	    	respuesta = "Ocurrio un error";
		    		exchange.sendResponseHeaders(400, respuesta.getBytes().length);
	    	    }
	    	    
	    	    
		    }catch(Exception e) {		    		
		    		respuesta = "Error token";	
		    		System.out.println("error" + e);
		    	exchange.sendResponseHeaders(403, respuesta.getBytes().length);
		    	return;
		    	
		    }
		    
		    OutputStream os = exchange.getResponseBody();
	       os.write(respuesta.getBytes(StandardCharsets.UTF_8));
	       os.close();
	
	}
		
		

	}
	
	
public static class recuperarGrupoPorMiembro implements HttpHandler {
		
		Grupo gru;
		String mail;
		String respuesta = "error";
		
		public void handle(HttpExchange exchange) throws IOException {
			
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
	    	    
	    	    mail = claims.getSubject();
	    	    

	    	    Gson gson = new Gson();
	    	    
	    	    gru = DataGrupo.buscarGrupoPorMiembro(mail);
	    	    
	    	    respuesta = gson.toJson(gru);
	    	    
	    	    exchange.sendResponseHeaders(200, respuesta.getBytes().length);
	    
		    }catch(Exception e) {		    		
		    		respuesta = "Error";	
		    		System.out.println("error" + e);
		    		exchange.sendResponseHeaders(403, respuesta.getBytes().length);
		    		return;
		    	
		    }
		    
		   OutputStream os = exchange.getResponseBody();
	       os.write(respuesta.getBytes(StandardCharsets.UTF_8));
	       os.close();
	
		
	}
		
		

	}


public static class salirDeGrupo implements HttpHandler {
	
	boolean resultado = false;
	
	String mail;
	String respuesta = "error";
	
	public void handle(HttpExchange exchange) throws IOException {
		
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
    	    
    	    mail = claims.getSubject();
    	    

    	    
    	    resultado = DataGrupo.salirGrupo(mail);
    	    
    	    if(resultado) {
    	    
    	    respuesta = "Bien";
    	    exchange.sendResponseHeaders(200, respuesta.getBytes().length);
    	    }
    	    else {
    	    	respuesta = "Error";
    	    	exchange.sendResponseHeaders(400, respuesta.getBytes().length);
    	    }
    	    
    	    
    
	    }catch(Exception e) {		    		
	    		respuesta = "Error";	
	    		System.out.println("error" + e);
	    		exchange.sendResponseHeaders(403, respuesta.getBytes().length);
	    		return;
	    	
	    }
	    
	    
	   OutputStream os = exchange.getResponseBody();
       os.write(respuesta.getBytes(StandardCharsets.UTF_8));
       os.close();

	
}
	
	

}
	
	
}
