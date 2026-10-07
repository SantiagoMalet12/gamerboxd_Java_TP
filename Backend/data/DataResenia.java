package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

import entities.Juego;
import entities.Persona;
import entities.Resenia;

public class DataResenia {
	
	
	public static Resenia recuperarPorIdJuegoYmail(int id, String mail) {

		Resenia r = new Resenia();



		try {
	
			Connection conn = Conexion.getInstancia().getConn();

	
			PreparedStatement stmt = conn.prepareStatement("select * from resenia where id_juego=? and mail_usuario=?");

	
			stmt.setInt(1, id);
			stmt.setString(2, mail);

	
			ResultSet rs = stmt.executeQuery();
	
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
	
		
				if (rs != null) { rs.close(); }
		
				if (stmt != null) { stmt.close(); }
		
		
				System.out.println("Buscar por idjuego y mailusuario");
		
				System.out.println();
		
				System.out.println();

			} catch (SQLException ex) {
	
		
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
	
	

}
