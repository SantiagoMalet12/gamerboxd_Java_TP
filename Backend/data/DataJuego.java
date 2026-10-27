package data;

import java.util.ArrayList;



import org.mindrot.jbcrypt.BCrypt;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;


import java.io.IOException;
import java.sql.*;
import entities.Compania;
import entities.Juego;
import entities.Plataforma;
import entities.Propuesta;


public class DataJuego {

public static boolean buscarjuego(String nombreJuego) {
		
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


public static Juego recuperarPorTitulo(String titulo) {		
	Juego j = null;

	try {
		
		Connection conn = Conexion.getInstancia().getConn();

		
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM juego WHERE LOWER(REPLACE(titulo, ' ', '')) = ?");
        
        String tituloNormalizado = titulo.replace(" ", "").toLowerCase();
        stmt.setString(1, tituloNormalizado);

       

      
        ResultSet rs= stmt.executeQuery();


        if(rs.next()) {
    		j=new Juego();
    		j.setTitulo(rs.getString("titulo"));
    		j.setDescripcion(rs.getString("descripcion"));
    		j.setId_juego(rs.getString("idjuego"));
    		j.setEstado(rs.getString("estado"));
    		j.setImagen(rs.getString("imagen"));

        }
        
        if(rs!=null){rs.close();}
        if(stmt!=null){stmt.close();}

	    
	    

	} catch (SQLException ex) {
	
	    System.out.println("SQLException: " + ex.getMessage());
	    System.out.println("SQLState: " + ex.getSQLState());
	    System.out.println("VendorError: " + ex.getErrorCode());
	}
	return j;
}



public static boolean insertarjuego(Propuesta pro) {
	
	boolean resultado = false;
    Connection conn = null;
    
   
	
	try {
	
	
	conn = Conexion.getInstancia().getConn();
	
	conn.setAutoCommit(false);
    
    String query = "insert into juego(titulo, imagen, descripcion) values (?,?,?)";
    PreparedStatement statement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
    
    
    statement.setString(1, pro.getNombreJuego());
    statement.setString(2, pro.getFoto());
    statement.setString(3, pro.getDescripcionjuego());
    
    statement.executeUpdate();
    
    ResultSet rsKeys = statement.getGeneratedKeys();
    int idJuegoGenerado = 0;
    if (rsKeys.next()) {
        idJuegoGenerado = rsKeys.getInt(1);
    }
    statement.close();
    
    
    String queryRelacion = "insert into juego_compania(idjuego, id_comp) values (?,?)";
    PreparedStatement psRelacion = conn.prepareStatement(queryRelacion);
    
    for (Compania comp : pro.getCompaniasJuego()) {
        psRelacion.setInt(1, idJuegoGenerado);
        psRelacion.setInt(2, comp.getId()); 
        
        psRelacion.executeUpdate();
        
    }

    
    conn.commit();
    resultado = true;
	}
	
	
	
	catch(SQLException ex){
		
		if (conn != null) {
            try {
                conn.rollback();
                System.out.println("Transacción revertida (Rollback realizado).");
            } catch (SQLException rollbackEx) {
                System.out.println("Error en rollback: " + rollbackEx.getMessage());
            }
        }
        
        System.out.println("SQLException: " + ex.getMessage());
        System.out.println("SQLState: " + ex.getSQLState());
        System.out.println("VendorError: " + ex.getErrorCode());
		
		
	}finally {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("Error al restaurar autoCommit: " + e.getMessage());
            }
        }
    }
	
	return resultado;
}




public static String actualizarJuego(int idJuego, String nuevoTitulo, String estado, String imagen, String descripcion) {
    
	
    String query = "UPDATE juego SET "
                 + "titulo = COALESCE(NULLIF(?, ''), titulo), "
                 + "estado = COALESCE(NULLIF(?, ''), estado), "
                 + "imagen = COALESCE(NULLIF(?, ''), imagen), "
                 + "descripcion = COALESCE(NULLIF(?, ''), descripcion) "
                 + "WHERE idjuego = ?";

    Connection conn = Conexion.getInstancia().getConn();

    try (PreparedStatement ps = conn.prepareStatement(query)) {
        ps.setString(1, nuevoTitulo);
        ps.setString(2, estado);
        ps.setString(3, imagen);
        ps.setString(4, descripcion);
        ps.setInt(5, idJuego);

        ps.executeUpdate();
        return "exito";

    } catch (SQLException ex) {
        System.out.println("SQLException: " + ex.getMessage());
        return "error en la bd";
    }
}

//soolo juegos activos
public static ArrayList<Juego> listarJuegos() {
	
	ArrayList<Juego> listadejuegos = new ArrayList<>();
	
	try {
		
		Connection conn = Conexion.getInstancia().getConn();

		String query = "SELECT juego.idjuego, juego.titulo, juego.imagen, juego.descripcion, "
	             + "       GROUP_CONCAT(compania.nombre SEPARATOR ', ') AS todas_las_companias "
	             + "FROM juego "
	             + "INNER JOIN juego_compania ON juego_compania.idjuego = juego.idjuego "
	             + "INNER JOIN compania ON juego_compania.id_comp = compania.idcompania "
	             + "WHERE LOWER(juego.estado) = ? "
	             + "  AND LOWER(compania.estado) = ? "
	             + "GROUP BY juego.idjuego;";

	PreparedStatement resultado = conn.prepareStatement(query);
	resultado.setString(1, "activo");
	resultado.setString(2, "activo");
	ResultSet rs = resultado.executeQuery();
		
		
		while (rs.next()) {
			
			Juego juego = new Juego();
			
			juego.setId_juego(rs.getString("idjuego"));
			juego.setTitulo(rs.getString("titulo"));
			juego.setImagen(rs.getString("imagen"));
			juego.setDescripcion(rs.getString("descripcion"));
			juego.setCompanias(rs.getString("todas_las_companias"));
			// agregar precio y genero y puntaje promedio
			
			listadejuegos.add(juego);
			
			
		}
		
		

	}
	
	
	catch(SQLException ex){
		
		
		System.out.println("SQLException: " + ex.getMessage());
	    System.out.println("SQLState: " + ex.getSQLState());
	    System.out.println("VendorError: " + ex.getErrorCode());
	}
	
	return listadejuegos;
}



public static ArrayList<Juego> listarTodosJuegos() {
	
	ArrayList<Juego> listadejuegos = new ArrayList<>();
	
	try {
		
		Connection conn = Conexion.getInstancia().getConn();

		String query = "SELECT juego.idjuego, juego.titulo, juego.imagen, juego.descripcion from juego ";


	PreparedStatement resultado = conn.prepareStatement(query);

	ResultSet rs = resultado.executeQuery();
		
		
		while (rs.next()) {
			
			Juego juego = new Juego();
			
			juego.setId_juego(rs.getString("idjuego"));
			juego.setTitulo(rs.getString("titulo"));
			juego.setImagen(rs.getString("imagen"));
			juego.setDescripcion(rs.getString("descripcion"));
			
			// agregar precio y genero y puntaje promedio
			
			listadejuegos.add(juego);
			
			
		}
		
		

	}
	
	
	catch(SQLException ex){
		
		
		System.out.println("SQLException: " + ex.getMessage());
	    System.out.println("SQLState: " + ex.getSQLState());
	    System.out.println("VendorError: " + ex.getErrorCode());
	}
	
	return listadejuegos;
}




public static Juego juegoPorId(String idJuego) {
	
	Juego juego = null;
	try {
		Connection conn = Conexion.getInstancia().getConn();
		String query = 
			    "SELECT juego.idjuego, juego.titulo, juego.imagen, juego.descripcion, juego.puntaje_promedio, " +
			    "GROUP_CONCAT(compania.nombre SEPARATOR ', ') AS todas_las_companias " +
			    "FROM juego " +
			    "LEFT JOIN juego_compania ON juego.idjuego = juego_compania.idjuego " +
			    "LEFT JOIN compania ON juego_compania.id_comp = compania.idcompania " +
			    "WHERE juego.idjuego = ? " +
			    "GROUP BY juego.idjuego, juego.titulo, juego.imagen, juego.descripcion, juego.puntaje_promedio";
		PreparedStatement resultado = conn.prepareStatement(query);
		resultado.setString(1, idJuego);
		ResultSet rs = resultado.executeQuery();
		
		if(rs.next()){
			juego = new Juego();
			
			juego.setId_juego(rs.getString("idjuego"));
			juego.setTitulo(rs.getString("titulo"));
			juego.setImagen(rs.getString("imagen"));
			juego.setDescripcion(rs.getString("descripcion"));
			juego.setCompanias(rs.getString("todas_las_companias"));
			juego.setPuntaje_promedio(rs.getDouble("puntaje_promedio"));
		}
		rs.close();
		resultado.close();
	
	}
	catch(SQLException ex){
		
		
		System.out.println("SQLException: " + ex.getMessage());
	    System.out.println("SQLState: " + ex.getSQLState());
	    System.out.println("VendorError: " + ex.getErrorCode());
	}
	
	return juego;
}

	public static boolean persistirJuegoCompleto(int idJuego, String titulo, String imagen, String descripcion, java.util.List<entities.Compania> companias, java.util.List<entities.Plataforma> plataformas) {
		Connection conn = null;
		boolean ok = false;
		try {
			conn = Conexion.getInstancia().getConn();
			conn.setAutoCommit(false);

			// 1. Asegurar compañías (FK hacia compania)
			if (companias != null && !companias.isEmpty()) {
				String sqlComp = "INSERT INTO compania (idcompania, nombre, estado) VALUES (?, ?, 'activo') ON DUPLICATE KEY UPDATE nombre = VALUES(nombre)";
				try (PreparedStatement psComp = conn.prepareStatement(sqlComp)) {
					for (entities.Compania c : companias) {
						psComp.setInt(1, c.getId());
						String nom = c.getNombre() != null ? (c.getNombre().length() > 45 ? c.getNombre().substring(0, 45) : c.getNombre()) : "";
						psComp.setString(2, nom);
						psComp.executeUpdate();
					}
				}
			}

			// 2. Asegurar plataformas (FK hacia plataforma)
			if (plataformas != null && !plataformas.isEmpty()) {
				String sqlPlat = "INSERT INTO plataforma (idplataforma, nombre) VALUES (?, ?) ON DUPLICATE KEY UPDATE nombre = VALUES(nombre)";
				try (PreparedStatement psPlat = conn.prepareStatement(sqlPlat)) {
					for (entities.Plataforma p : plataformas) {
						psPlat.setInt(1, p.getId());
						String nom = p.getNombre() != null ? (p.getNombre().length() > 45 ? p.getNombre().substring(0, 45) : p.getNombre()) : "";
						psPlat.setString(2, nom);
						psPlat.executeUpdate();
					}
				}
			}

			// 3. Insertar / Actualizar juego
			String sqlJuego = "INSERT INTO juego (idjuego, titulo, imagen, descripcion, precio, genero, puntaje_promedio, estado) "
			                + "VALUES (?, ?, ?, ?, NULL, NULL, NULL, 'activo') "
			                + "ON DUPLICATE KEY UPDATE "
			                + "titulo = VALUES(titulo), imagen = VALUES(imagen), descripcion = VALUES(descripcion), estado = 'activo'";
			try (PreparedStatement psJuego = conn.prepareStatement(sqlJuego)) {
				psJuego.setInt(1, idJuego);
				String tit = titulo != null ? (titulo.length() > 45 ? titulo.substring(0, 45) : titulo) : "";
				psJuego.setString(2, tit);
				psJuego.setString(3, imagen);
				psJuego.setString(4, descripcion);
				psJuego.executeUpdate();
			}

			// 4. Relación juego_compania
			if (companias != null && !companias.isEmpty()) {
				String sqlJC = "INSERT IGNORE INTO juego_compania (idjuego, id_comp) VALUES (?, ?)";
				try (PreparedStatement psJC = conn.prepareStatement(sqlJC)) {
					for (entities.Compania c : companias) {
						psJC.setInt(1, idJuego);
						psJC.setInt(2, c.getId());
						psJC.executeUpdate();
					}
				}
			}

			// 5. Relación plataforma_juego
			if (plataformas != null && !plataformas.isEmpty()) {
				String sqlPJ = "INSERT IGNORE INTO plataforma_juego (idplataforma, idjuego) VALUES (?, ?)";
				try (PreparedStatement psPJ = conn.prepareStatement(sqlPJ)) {
					for (entities.Plataforma p : plataformas) {
						psPJ.setInt(1, p.getId());
						psPJ.setInt(2, idJuego);
						psPJ.executeUpdate();
					}
				}
			}

			conn.commit();
			ok = true;
		} catch (SQLException ex) {
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException rbe) {
					System.err.println("Error en rollback: " + rbe.getMessage());
				}
			}
			System.err.println("SQLException al persistir juego ID " + idJuego + ": " + ex.getMessage());
		} finally {
			if (conn != null) {
				try {
					conn.setAutoCommit(true);
				} catch (SQLException e) {
					System.err.println("Error al restaurar autoCommit: " + e.getMessage());
				}
			}
		}
		return ok;
	}
}
