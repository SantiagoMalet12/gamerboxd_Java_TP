package data;

import java.sql.*;


import java.util.ArrayList;
import java.util.LinkedList;

import entities.Compania;
import entities.Propuesta;

public class DataPropuesta {
	
	
	
	
	
	public static boolean actualizarestado(Propuesta pro) {
		boolean respuesta = false;
		
		String query = "UPDATE propuesta SET estado = ? WHERE idpropuesta = ?";
		
		try {
			
			Connection conn = Conexion.getInstancia().getConn();
			
			try (PreparedStatement ps = conn.prepareStatement(query)) {
				
				ps.setString(1, pro.getEstado());
				ps.setInt(2, pro.getIdPropuesta());
				
				int filasAfectadas = ps.executeUpdate();
				
				if (filasAfectadas > 0) {
					if ("aceptado".equalsIgnoreCase(pro.getEstado())) {
						respuesta = DataJuego.insertarjuego(pro);
						
					} else {
						respuesta = true; 
					}
				}
			}
		    
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
		
		return respuesta;
	}
	
	
	
	public static LinkedList<Propuesta> listarpropuestas() {
		
		LinkedList<Propuesta> propuestas = new LinkedList<>();
		 ArrayList<Compania> companias = new ArrayList<>();
		
		
		
		
		try {
			// crear una conexión
			Connection conn = Conexion.getInstancia().getConn();

			// ejecutar la quer
			String query = "select * from propuesta";
		    PreparedStatement ps = conn.prepareStatement(query);
		    ResultSet rs = ps.executeQuery();

            // mapear de resultset a objeto
            while(rs.next()) {
            	
            	
            	Propuesta pro =new Propuesta();
            	pro.setDescripcionjuego(rs.getString("descripcionjuego"));
            	pro.setMail_usuario(rs.getString("mail_usuario"));
            	pro.setEstado(rs.getString("estado"));
            	pro.setFoto(rs.getString("imagen"));
            	pro.setNombreJuego(rs.getString("nombrejuego"));
            	pro.setIdPropuesta(rs.getInt("idpropuesta"));
            	
            	String query2 = "select * from compania_propuesta inner join compania on "
            			+ "compania.idcompania = compania_propuesta.id_comp where id_propuesta = ?";
            	 PreparedStatement ps2 = conn.prepareStatement(query2);
            	 ps2.setString(1, rs.getString("idpropuesta"));
            	 ResultSet rs2 = ps2.executeQuery();
            	 
            	 companias = new ArrayList<>();
            	 
            	 while(rs2.next()) {
            		
            		 Compania comp = new Compania();
            		 comp.setNombre(rs2.getString("nombre"));
            		 comp.setId(rs2.getInt("idcompania"));
            		 companias.add(comp);
            		 
            	 }
            	
            	          	
           
            	 pro.setCompaniasJuego(companias);
                propuestas.add(pro);

            }
            //cerrar recursos
      
		    
		    /* mostrar info
		    System.out.println("Listado Completo");
		    System.out.println(companias);
		    System.out.println();System.out.println();
		    */
		    
		    

		} catch (SQLException ex) {
		    // Manejo de errores
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		}
		return propuestas;
		
		
	}
	
	
	public static boolean insertarpropuesta (String nombrejuego, String descripcion, String mailusuario, ArrayList<Compania> companias, String foto) {
		
		boolean respuesta = false;
		Connection conn = null;
		PreparedStatement ps1 = null;
	    PreparedStatement ps2 = null;
	    ResultSet rs = null;
		
		
		
		try {

		conn = Conexion.getInstancia().getConn();
		
		conn.setAutoCommit(false);
		
		String query1 = "insert into propuesta (nombrejuego, imagen, descripcionjuego, mail_usuario) values (?,?,?,?)";
		ps1 = conn.prepareStatement(query1, Statement.RETURN_GENERATED_KEYS);
		
		ps1.setString(1, nombrejuego);
		ps1.setString(2, foto);
		ps1.setString(3, descripcion);
		ps1.setString(4, mailusuario);
		ps1.executeUpdate();
		
		 rs = ps1.getGeneratedKeys();
		int idPropuestaGenerado = 0;
		
		if (rs.next()) {
            idPropuestaGenerado = rs.getInt(1); 
        }
		
		try {
			
			String query2 = "insert into compania_propuesta (id_comp, id_propuesta) values (?,?)";
			ps2 = conn.prepareStatement(query2);
			
			ps2.setInt(2, idPropuestaGenerado);
			
			for(Compania comp: companias) {
				
				ps2.setInt(1, comp.getId());
				ps2.executeUpdate();
				
			}
			
			conn.commit();
            respuesta = true;
			
			
			respuesta = true;
			
			
		}catch(SQLException e) {
			
			System.out.println(e);
			if (conn != null) {
                conn.rollback();
            }
            
			respuesta = false;
			
			
			
		}
		
		}
		
		catch(SQLException e) {
			
			
			respuesta = false;
			System.out.println(e);
			
			
		}
		finally {
	        
	        try {
	            if (conn != null) conn.setAutoCommit(true);
	            if (rs != null) rs.close();
	            if (ps1 != null) ps1.close();
	            if (ps2 != null) ps2.close();
	        } catch (SQLException ex) {
	            System.out.println("Error al cerrar recursos: " + ex.getMessage());
	        }
		
		
		
		}
		return respuesta;
		
		
		
		
		
		
	}
	
	
	
	
	
}
