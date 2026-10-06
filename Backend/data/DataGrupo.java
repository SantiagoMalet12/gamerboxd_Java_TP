package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;

import org.mindrot.jbcrypt.BCrypt;

import entities.Compania;
import entities.Grupo;
import entities.Juego;
import entities.Persona;
import entities.Propuesta;

public class DataGrupo {
	
	
	
	
public static String nombregrupo (String mail)	{
	
	String nombregrupo = "Sin grupo";
	try {
			
		
		Connection conn = Conexion.getInstancia().getConn();
		String query = "SELECT g.nombre \r\n"
				+ "FROM persona\r\n"
				+ "INNER JOIN grupo g ON g.idgrupo = persona.idgrupo\r\n"
				+ "WHERE persona.mail = ?";
		PreparedStatement Resultado = conn.prepareStatement(query);
		Resultado.setString(1, mail);
		ResultSet rs = Resultado.executeQuery();
		
		if (rs.next()) {	
			
			nombregrupo = rs.getString("nombre");
		}
			
		
	}catch(SQLException ex) {
		
		System.out.println("SQLException: " + ex.getMessage());
	    System.out.println("SQLState: " + ex.getSQLState());
	    System.out.println("VendorError: " + ex.getErrorCode());
		
	}
	
	
	return nombregrupo;
}
	

	
public static boolean existegrupo(String nombreJuego) {
		
		boolean resultado = false;
		
		try {
			
			String nombremin = nombreJuego.replace(" ", "").toLowerCase();
			
			
			Connection conn = Conexion.getInstancia().getConn();
			String query = "SELECT 1 FROM grupo WHERE LOWER(REPLACE(nombre, ' ', '')) = ?";
			PreparedStatement Resultado = conn.prepareStatement(query);
			Resultado.setString(1, nombremin);
			ResultSet rs = Resultado.executeQuery();
			
			if (rs.next()) {	
				
				resultado = true;
			}
				
			
		}catch(SQLException ex) {
			
			System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
			
		}
		
		
		return resultado;
		
	}
	
	
	
	
	
public static boolean buscargrupo(String nombreJuego) {
		
		boolean resultado = false;
		
		try {
			
			String nombremin = nombreJuego.replace(" ", "").toLowerCase();
			
			
			Connection conn = Conexion.getInstancia().getConn();
			String query = "SELECT 1 FROM juego WHERE LOWER(REPLACE(titulo, ' ', '')) = ?";
			PreparedStatement Resultado = conn.prepareStatement(query);
			Resultado.setString(1, nombremin);
			ResultSet rs = Resultado.executeQuery();
			
			if (rs.next()) {	
				
				resultado = true;
			}
				
			
		}catch(SQLException ex) {
			
			System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
			
		}
		
		
		return resultado;
		
	}
	
	public static String actualizarGrupo(int id, String nuevoNombre, String Nuevaimagen, String Nuevadescripcion) {
	    
	
	    
	    String query = "UPDATE grupo SET "
	                 + "foto_perfil = COALESCE(NULLIF(?, ''), foto_perfil), "
	                 + "nombre = COALESCE(NULLIF(?, ''), nombre), "
	                 + "descripcion = COALESCE(NULLIF(?, ''), descripcion) "
	                 + "WHERE idgrupo = ?";

	    Connection conn = Conexion.getInstancia().getConn();

	    try (PreparedStatement ps = conn.prepareStatement(query)) {
	        ps.setString(1, Nuevaimagen);
	        ps.setString(2, nuevoNombre);
	        ps.setString(3, Nuevadescripcion);
	        ps.setInt(4, id);

	        int filasAfectadas = ps.executeUpdate();
	        return filasAfectadas > 0 ? "exito" : "no encontrado";

	    } catch (SQLException ex) {
	        System.out.println("SQLException: " + ex.getMessage());
	        return "error en la bd";
	    }
	}




	
	
	
public static boolean dardebajagrupo (int id) {
		
	boolean respuesta = false;
	Connection conn = null;

	try {
	    conn = Conexion.getInstancia().getConn();
	    

	    conn.setAutoCommit(false);

	    String query = "UPDATE grupo SET estado = ? WHERE idgrupo = ?";
	    String query2 = "UPDATE persona SET idgrupo = NULL, rolgrupo = NULL WHERE idgrupo = ?";

	    try (PreparedStatement ps = conn.prepareStatement(query);
	         PreparedStatement ps2 = conn.prepareStatement(query2)) {

	        ps.setString(1, "inactivo");
	        ps.setInt(2, id);
	        ps.executeUpdate();

	        ps2.setInt(1, id);
	        ps2.executeUpdate();

	  
	        conn.commit();
	        respuesta = true;
	    }

	} catch (SQLException e) {
	    
	    if (conn != null) {
	        try {
	            conn.rollback();
	        } catch (SQLException ex) {
	            System.out.println("Error en rollback: " + ex.getMessage());
	        }
	    }
	    System.out.println("Error en la transacción: " + e.getMessage());
	    respuesta = false;

	} finally {
	  
	    if (conn != null) {
	        try {
	            conn.setAutoCommit(true);
	        } catch (SQLException ex) {
	            System.out.println("Error al restaurar autoCommit: " + ex.getMessage());
	        }
	    }
	}
	
	return respuesta;
		
		
		
		
	}
	
	

	public static boolean salirGrupo (String mail) {
		
		boolean respuesta = false;
		
		try {
			
			Connection conn = Conexion.getInstancia().getConn();
			
			
				String query = "UPDATE persona  SET idgrupo = NULL, rolgrupo = NULL WHERE mail = ?";
				PreparedStatement ps = conn.prepareStatement(query);
				
				 ps.setString(1, mail);
				 ps.executeUpdate();
				 respuesta = true;
				
		    
		}
		
		catch(SQLException ex){
	
		respuesta = false;
	
		System.out.println("SQLException: " + ex.getMessage());
	    System.out.println("SQLState: " + ex.getSQLState());
	    System.out.println("VendorError: " + ex.getErrorCode());
		}
		
		
		
		
		
		
		return respuesta;
		
		
		
		
	}
	
	
	public static Grupo buscarGrupoPorMiembro (String mail) {
		
		Grupo g = null;
		ArrayList<Persona> miembros = new ArrayList<>();

		try {
			
			Connection conn = Conexion.getInstancia().getConn();

			
	        PreparedStatement stmt = conn.prepareStatement("select grupo.foto_perfil, grupo.nombre, grupo.descripcion, grupo.idgrupo from grupo inner join persona on grupo.idgrupo = persona.idgrupo where persona.mail = ?");
	        PreparedStatement stmt2 = conn.prepareStatement("select * from persona where idgrupo = ? and estado = ?");
	        
	        
	        stmt.setString(1, mail);

	        ResultSet rs= stmt.executeQuery();


	        if(rs.next()) {
	    		g=new Grupo();
	    		g.setDescripcion(rs.getString("descripcion"));
	    		g.setFoto_perfil(rs.getString("foto_perfil"));
	    		g.setNombre(rs.getString("nombre"));
	    		g.setId(rs.getInt("idgrupo"));
	    		
	    		stmt2.setString(1, rs.getString("idgrupo"));
	    		stmt2.setString(2, "Activo");
	    		ResultSet rs2= stmt2.executeQuery();
	    		
	    		while (rs2.next()) {
	    			
	    			Persona per = new Persona();
	    			per.setNombre_usuario(rs2.getString("nombre"));
	    			per.setIdgrupo(rs2.getInt("idgrupo"));
	    			per.setRolgrupo(rs2.getString("rolgrupo"));
	    			miembros.add(per);
				}	
	    		
	    		g.setIntegrantes(miembros);


	        }
	        
	        if(rs!=null){rs.close();}
	        if(stmt!=null){stmt.close();}

		    
		    

		} catch (SQLException ex) {
		
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		}
		return g;
		
		
	}
	
	public static boolean insertarMiembro(int idgrupo, String mail) {
		
		boolean resultado = false;
		
		try {
			Connection conn = Conexion.getInstancia().getConn();
		    
		    
		    String query = "update persona set idgrupo = ?, rolgrupo = ? where mail = ?";
		    PreparedStatement ps = conn.prepareStatement(query);
		    
		    	    
		    
		    ps.setInt(1,idgrupo);
		    ps.setString(2, "miembro");
		    ps.setString(3, mail);
		    ps.executeUpdate();
		    
		    resultado = true;
	
	
		}catch (SQLException ex) {
	        System.out.println("SQLException: " + ex.getMessage());
	        System.out.println("SQLState: " + ex.getSQLState());
	        System.out.println("VendorError: " + ex.getErrorCode());
	    }
	
	return resultado;
	
}
	
	
	
public static LinkedList<Grupo> listargrupos() {
	
	LinkedList<Grupo> grupos = new LinkedList<>();


	try {
		// crear una conexión
		Connection conn = Conexion.getInstancia().getConn();

		
		String query = "SELECT g.idgrupo, g.nombre, g.descripcion, g.foto_perfil, COUNT(p.mail) AS total_integrantes FROM grupo g LEFT JOIN persona p ON g.idgrupo = p.idgrupo WHERE g.estado = ? GROUP BY g.idgrupo, g.nombre, g.descripcion, g.foto_perfil";
	    PreparedStatement ps = conn.prepareStatement(query);
	    ps.setString(1, "activo");
	    ResultSet rs = ps.executeQuery();

        // mapear de resultset a objeto
        while(rs.next()) {
        	
        	
        	Grupo gru =new Grupo();
        	gru.setDescripcion(rs.getString("descripcion"));
        	gru.setFoto_perfil(rs.getString("foto_perfil"));
        	gru.setNombre(rs.getString("nombre"));
        	gru.setId(rs.getInt("idgrupo"));
        	gru.setCantidadIntegrantes(rs.getInt("total_integrantes"));
        	 
            grupos.add(gru);

        }

	    
	    

	} catch (SQLException ex) {
	    // Manejo de errores
	    System.out.println("SQLException: " + ex.getMessage());
	    System.out.println("SQLState: " + ex.getSQLState());
	    System.out.println("VendorError: " + ex.getErrorCode());
	}
	return grupos;
	
	
}



public static ArrayList<Grupo> recuperarGrupoPorNombre(String nombre) {
	
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
	
}
