package data;
import java.sql.*;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import org.mindrot.jbcrypt.BCrypt;

import entities.Compania;
import servidor.GestionMail;

public class DataCompania {
	
	
	
	
	
	public void persistirCompania(Compania c, Connection conn) {
	    String sql = "INSERT IGNORE INTO compania (idcompania, nombre) VALUES (?, ?)";
	    
	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, c.getId());
	        stmt.setString(2, c.getNombre());
	        stmt.executeUpdate();
	    } catch (SQLException e) {
	        System.err.println("Error al persistir compañía: " + c.getNombre() + " - " + e.getMessage());
	    }
	}
	
	
	
	
	public static String actualizarcompania(String mail, String estado, int id) {
		
		String Respuesta;
		
		if(mail.equals("")) {
			
			try {
				
				Connection conn = Conexion.getInstancia().getConn();
			    
			    
			    String query = "update compania set estado = ? where idcompania = ?";
			    PreparedStatement ps = conn.prepareStatement(query);   
			    ps.setString(1, estado);
			    ps.setInt(2, id);
			    ps.executeUpdate();
				
			    Respuesta = "exito";
		   
			    
			}catch(SQLException ex){
		
		
			System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		    Respuesta = "error en la bd";
			}
			
			
		}
		
		else {
		try {
			
			Connection conn = Conexion.getInstancia().getConn();
		    
		    
		    String query = "update compania set estado = ?, nombre = ? where idcompania = ?";
		    PreparedStatement ps = conn.prepareStatement(query);   
		    ps.setString(1, estado);
		    ps.setString(2, mail);
		    ps.setInt(3, id);
		    ps.executeUpdate();
			
		    Respuesta = "exito";
	   
		    
		}catch(SQLException ex){
	
	
		System.out.println("SQLException: " + ex.getMessage());
	    System.out.println("SQLState: " + ex.getSQLState());
	    System.out.println("VendorError: " + ex.getErrorCode());
	    Respuesta = "error en la bd";
		}
		
		
		
		}
		
		
		return Respuesta;
		
		
	}
	
	public static Compania recuperarPorMail(String mail) {		
		Compania c = null;

		try {
			// crear una conexión
			Connection conn = Conexion.getInstancia().getConn();

			// definir la query
            PreparedStatement stmt = conn.prepareStatement("select * from compania where nombre=?");
            
            // setear el/los parámetros
            stmt.setString(1, mail);

            

            // ejecutar query y obtener resultados
            ResultSet rs= stmt.executeQuery();

            // mapear de resultset a objeto
            if(rs.next()) {
        		c=new Compania();
                c.setId(rs.getInt("idcompania"));
                c.setNombre(rs.getString("nombre"));
                c.setEstado(rs.getString("estado"));
            }
            //cerrar recursos
            if(rs!=null){rs.close();}
            if(stmt!=null){stmt.close();}

		    
		    

		} catch (SQLException ex) {
		    // Manejo de errores
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		}
		return c;
	}
	
	
	
	public static String dardebaja(int id) {
		
		String Respuesta = "ok";
		

		try {
			
			Connection conn = Conexion.getInstancia().getConn();
		    
		    
		    String query = "update compania set estado = ? where idcompania = ?";
		    PreparedStatement ps = conn.prepareStatement(query);   
		    ps.setString(1, "inactivo");
		    ps.setInt(2, id);
		    ps.executeUpdate();
			
	   
		    
		}catch(SQLException ex){
	
	
		System.out.println("SQLException: " + ex.getMessage());
	    System.out.println("SQLState: " + ex.getSQLState());
	    System.out.println("VendorError: " + ex.getErrorCode());
	    Respuesta = "Error en bd";
		}
		
		
		
		
		
		
		return Respuesta;
}
	

	public static String actualizanombre(int id, String nombre) {
		
		String Respuesta = "ok";
		

		try {
			
			Connection conn = Conexion.getInstancia().getConn();
		    
		    
		    String query = "update compania set nombre = ? where idcompania = ?";
		    PreparedStatement ps = conn.prepareStatement(query);   
		    ps.setString(1, nombre);
		    ps.setInt(2, id);
		    ps.executeUpdate();
			
	   
		    
		}catch(SQLException ex){
	
	
		System.out.println("SQLException: " + ex.getMessage());
	    System.out.println("SQLState: " + ex.getSQLState());
	    System.out.println("VendorError: " + ex.getErrorCode());
	    Respuesta = "Error en bd";
		}
		
		
	
		
		
		
		
		
		return Respuesta;
}

public static Boolean validarempresaexistentenorepetida(String nombre, int id) {
		
		Boolean Respuesta = false;
		
		try {
			
			Connection conn = Conexion.getInstancia().getConn();
			
			String nombremin = nombre.replace(" ", "").toLowerCase();
		    
			String query = "SELECT 1 FROM compania WHERE LOWER(REPLACE(nombre, ' ', '')) = ? AND idcompania != ?";
			PreparedStatement Resultado = conn.prepareStatement(query);
			Resultado.setString(1, nombremin);
			Resultado.setInt(2, id);
			ResultSet rs = Resultado.executeQuery();
			
			
			if (rs.next()) {		
				Respuesta = true;
			}
		    
		}catch(SQLException ex){
	
	
		System.out.println("SQLException: " + ex.getMessage());
	    System.out.println("SQLState: " + ex.getSQLState());
	    System.out.println("VendorError: " + ex.getErrorCode());

		}
	
	return Respuesta;
}

	public static Boolean validarempresaexistente(String nombre) {
		
		Boolean Respuesta = false;
		
		try {
			
			Connection conn = Conexion.getInstancia().getConn();  
			
			String nombremin = nombre.replace(" ", "").toLowerCase();
		    
			String query = "SELECT 1 FROM compania WHERE LOWER(REPLACE(nombre, ' ', '')) = ?";
			PreparedStatement Resultado = conn.prepareStatement(query);
			Resultado.setString(1, nombremin);
			ResultSet rs = Resultado.executeQuery();
			
			
			if (rs.next()) {		
				Respuesta = true;
			}
		    
		}catch(SQLException ex){
	
	
		System.out.println("SQLException: " + ex.getMessage());
	    System.out.println("SQLState: " + ex.getSQLState());
	    System.out.println("VendorError: " + ex.getErrorCode());

		}
	
	return Respuesta;
}
	
	
	
public static String  crearcompania(String nombre) throws SQLException{
		
	String Respuesta = "No se";
	


	Connection conn = Conexion.getInstancia().getConn();
	
	String query1 = "insert into compania (nombre) values (?)";
	PreparedStatement ps1 = conn.prepareStatement(query1);
	ps1.setString(1, nombre);
	ps1.executeUpdate();
	Respuesta = "ok";
	    	

	return Respuesta;
		
}


public static  LinkedList<Compania> recuperarCompanias() {
	
	LinkedList<Compania> companias = new LinkedList<>();
	
	try {
	
		Connection conn = Conexion.getInstancia().getConn();

        Statement stmt = conn.createStatement();
        ResultSet rs= stmt.executeQuery("select * from compania");

        while(rs.next()) {
        	Compania c=new Compania();
            c.setId(rs.getInt("idcompania"));
            c.setNombre(rs.getString("nombre"));

            companias.add(c);

        }

        if(rs!=null){rs.close();}
        if(stmt!=null){stmt.close();}

	
	    
	    

	} catch (SQLException ex) {
	    // Manejo de errores
	    System.out.println("SQLException: " + ex.getMessage());
	    System.out.println("SQLState: " + ex.getSQLState());
	    System.out.println("VendorError: " + ex.getErrorCode());
	}
	return companias;
	
}

//Solo companias activas
public static ArrayList<Compania> listaCompanias() {
	
	   ArrayList<Compania> listadeempresas = new ArrayList<>();
	
	try {
		
		Connection conn = Conexion.getInstancia().getConn();

		String query = "select * from compania where estado <> ?";
		PreparedStatement Resultado = conn.prepareStatement(query);
		Resultado.setString(1, "inactivo");
		ResultSet rs = Resultado.executeQuery();
		
		
		while (rs.next()) {
			
			Compania compania = new Compania();
			
			compania.setId(rs.getInt("idcompania"));
			compania.setNombre(rs.getString("nombre"));
		
			listadeempresas.add(compania);
					
		}
		

	}
	catch(SQLException ex){	
		
		System.out.println("SQLException: " + ex.getMessage());
	    System.out.println("SQLState: " + ex.getSQLState());
	    System.out.println("VendorError: " + ex.getErrorCode());
	}
	
	return listadeempresas;
	
}





}