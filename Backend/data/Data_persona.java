

package data;
import java.sql.*;
import java.time.LocalDateTime;
import servidor.GestionMail;
import java.util.Random;
import entities.Grupo;
import entities.Persona;
import org.mindrot.jbcrypt.BCrypt;




public class Data_persona {
	
	public static String obtenerrolgrupo(String mail) {
		
		String rol = "";
		
		Connection conn = Conexion.getInstancia().getConn();
		String query = "select rolgrupo from persona where mail = ?";
	    
		try (PreparedStatement ps = conn.prepareStatement(query)) {
	    ps.setString(1, mail);
	    
	    	try (ResultSet rs = ps.executeQuery()){
	    
	    		if (rs.next()) {
	    			rol = rs.getString("rolgrupo");

	    		}
	    	
	    	}
	        
		} catch(SQLException ex){
	
		System.out.println("SQLException: " + ex.getMessage());
	    System.out.println("SQLState: " + ex.getSQLState());
	    System.out.println("VendorError: " + ex.getErrorCode());
		}
		
		return rol;	
	}
	
	
	public static boolean convertirenadmin(String mail) {
		
		boolean respuesta = false;
		
		Connection conn = Conexion.getInstancia().getConn();
		String query = "update persona set rol = ? where LOWER(REPLACE(mail, ' ', '')) = ?";
		
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			
		
		 ps.setString(1, "administrador");
		 String mailNormalizado = mail.replace(" ", "").toLowerCase();
		 ps.setString(2, mailNormalizado);
		 ps.executeUpdate();		
		 respuesta = true;
		    
		}
		
		catch(SQLException ex){
	
	
		System.out.println("SQLException: " + ex.getMessage());
	    System.out.println("SQLState: " + ex.getSQLState());
	    System.out.println("VendorError: " + ex.getErrorCode());
		}
		

		return respuesta;
		
	
	}
	
	
	public static boolean dardebaja (String mail) {
		
		Boolean respuesta = false;
		String query = "update persona set estado = ? where mail = ?";
		Connection conn = Conexion.getInstancia().getConn();
		
		try (PreparedStatement ps = conn.prepareStatement(query);) {
	
			 ps.setString(1, "inactivo");
			 ps.setString(2, mail);	
			 ps.executeUpdate();
			 respuesta = true;
				    
		}
		
		catch(SQLException ex){
	
		System.out.println("SQLException: " + ex.getMessage());
	    System.out.println("SQLState: " + ex.getSQLState());
	    System.out.println("VendorError: " + ex.getErrorCode());
		}
		
	
		return respuesta;
		
	
	}
	
	
	public static String actualizarImagenYnombre(String mail, String nuevaimagen, String nuevonombre) {
		
		String Respuesta = "error";
		Connection conn = Conexion.getInstancia().getConn();
		
		
		
		try {
			
			if(nuevaimagen.equals("") && !nuevonombre.equals("")) {
				String query = "update persona set nombre = ? where mail = ?";
				
				try(PreparedStatement ps = conn.prepareStatement(query)){
				
				 ps.setString(1, nuevonombre);
				 ps.setString(2, mail);
				 ps.executeUpdate();
				 Respuesta = "ok";
				}
				
			}
			
			if(nuevonombre.equals("") && !nuevaimagen.equals("")) {
				String query = "update persona set foto_perfil = ? where mail = ?";
				
				try(PreparedStatement ps = conn.prepareStatement(query)){

				ps.setString(1, nuevaimagen);
				ps.setString(2, mail);
				ps.executeUpdate();
				Respuesta = "ok";
				}
				
			}
			
			if(!nuevonombre.equals("") && !nuevaimagen.equals("")) {
				String query = "UPDATE persona SET foto_perfil = ?, nombre = ? WHERE mail = ?";
				try(PreparedStatement ps = conn.prepareStatement(query)){
				ps.setString(1, nuevaimagen);
				ps.setString(2, nuevonombre);
				ps.setString(3, mail);
				ps.executeUpdate();
				Respuesta = "ok";
				}
				
			}
	    
		}
		
		catch(SQLException ex){
	
	
		System.out.println("SQLException: " + ex.getMessage());
	    System.out.println("SQLState: " + ex.getSQLState());
	    System.out.println("VendorError: " + ex.getErrorCode());
		}
			
		return Respuesta;
	}
	
	


	//verifica token que envio po mail
	
	public static Boolean veriToken(String token, String mail) {
		
		Boolean ok = false;
		Boolean ok1 = false;
		String token_real = "asdas";
		LocalDateTime tiempo_expiracion = LocalDateTime.MIN;
		
		try {
			
			Connection conn = Conexion.getInstancia().getConn();
			String query = "SELECT id FROM recuperacion_password WHERE id IN (SELECT MAX(id) FROM recuperacion_password  WHERE mail_persona = ?  GROUP BY mail_persona)";
			try(PreparedStatement ps = conn.prepareStatement(query)){
	    	ps.setString(1, mail);
	    	
	    	
	    	try(ResultSet rs = ps.executeQuery()){
	    	
	    	if (rs.next()) {
	    		
	    		String query1 = "SELECT * from recuperacion_password where id = ?";
	    			try(PreparedStatement ps1 = conn.prepareStatement(query1)){
	    				ps1.setString(1, rs.getString("id"));
	    				
	    			try(ResultSet rs1 = ps1.executeQuery()){
	    				
		    	if (rs1.next()) {
		    		
		    		tiempo_expiracion = rs1.getObject("fecha_expiracion", LocalDateTime.class);
		    		token_real = rs1.getString("codigo");
		    	}
		    	
	    	
	    		if (token_real.equals(token)) {
	    			ok1 = true;
				}
	    	
	    	
	    	if (ok1 == true && tiempo_expiracion.isAfter(LocalDateTime.now()) ) {
	    		
	    		ok = true;
	    		
				
			}else {
    			
    			ok = false;
    		}
	    			}
	    			}
	    				}
												}
			}
		}catch(SQLException ex){
			
			System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
			
		}
		
		
		return ok;
			
		
	}
	
	
	//Genera token de cambio de password, lo guarda en la bd y lo envia por mail
	public static String enviar_token(String mail) {
		
		String token = "Error";	
		try {
			
			Connection conn = Conexion.getInstancia().getConn();
			String query = "select * from persona where mail = ?";
			
			
		    try(PreparedStatement ps = conn.prepareStatement(query)){
		    ps.setString(1, mail);
		   
		    
		    try(ResultSet rs = ps.executeQuery()){
		    
		    if (rs.next()) {
		    	
		    	Random random = new Random();
		    	int numeroMinMax = random.nextInt(1000, 9999);
		    	token = String.valueOf(numeroMinMax);

		        LocalDateTime fecha_hora_vencimiento = LocalDateTime.now().plusMinutes(10);
		    	
		    	String query1 = "insert into recuperacion_password (codigo,fecha_expiracion, mail_persona) values  (?, ?, ?)";
		    	try(PreparedStatement ps1 = conn.prepareStatement(query1)){
		    	ps1.setString(1, token);
		    	ps1.setObject(2, fecha_hora_vencimiento);
		    	ps1.setString(3, mail);
		    	ps1.executeUpdate();
		    	GestionMail.enviarmail(mail, "nuevacontrasenia",  token);
		    	}
				
			}else {
				
				token = "No existe mail";
				
			}
		    }	
		    }
			
		}
		catch(SQLException ex){
			
			System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
			
		}		
		
		return token;			
	}
		
	
	
	

	public static Persona buscar_solo_persona_pormail(String mail) {
		
		Persona per = new Persona();
		String query = "select * from persona where mail = ?";
		Connection conn = Conexion.getInstancia().getConn();
		
		try {
		    
	    try(PreparedStatement ps = conn.prepareStatement(query)){
	    	ps.setString(1, mail);
	    	try(ResultSet rs = ps.executeQuery()){
	    
	    	if (rs.next()) {
	    	
		    	
		    	per.setFoto_perfil(rs.getString("foto_perfil"));
		    	per.setNombre_usuario(rs.getString("nombre"));
		    	per.setRol(rs.getString("rol"));
		    	per.setIdgrupo(rs.getInt("idgrupo"));
		    	per.setRolgrupo(rs.getString("rolgrupo"));
		    	per.setEstado(rs.getString("estado"));
		 
	
		    	per.setMail(mail);

	    	}
		}
	    }
	        
	   
	    
	}
catch(SQLException ex){
	
	
	System.out.println("SQLException: " + ex.getMessage());
    System.out.println("SQLState: " + ex.getSQLState());
    System.out.println("VendorError: " + ex.getErrorCode());
}
		
return per;
		
	}
	
	
public static Boolean buscar_persona(String mail, String contrasenia) {
		
		Boolean resultado = false;
		Connection conn = Conexion.getInstancia().getConn(); 
	    String query = "select * from persona where mail = ?";
		
		try {
		
		try( PreparedStatement ps = conn.prepareStatement(query)){
	    ps.setString(1, mail);
	    try(ResultSet rs = ps.executeQuery()){
	    
	    if (rs.next()) {
	    	resultado = BCrypt.checkpw(contrasenia,rs.getString("contrasenia"));
		}
	    
	        
		}
		}
	}catch(SQLException ex){
	
	System.out.println("SQLException: " + ex.getMessage());
    System.out.println("SQLState: " + ex.getSQLState());
    System.out.println("VendorError: " + ex.getErrorCode());
}
		
return resultado;
		
}

	
	
	
	public static void actualizar_contrasenia(String mail, String password) {
		Connection conn = Conexion.getInstancia().getConn();
	    String query = "update persona set contrasenia = ? where mail = ?";
		
		
		try {
			
			
		    try(PreparedStatement ps = conn.prepareStatement(query)){
		    
		    
		    int logRounds = 12;
		    String salt = BCrypt.gensalt(logRounds);
		    String hashedPassword = BCrypt.hashpw(password, salt);	    
		    
		    ps.setString(1, hashedPassword);
		    ps.setString(2, mail);
		    ps.executeUpdate();
		    
		    }
		}
		catch(SQLException ex){
	
	
		System.out.println("SQLException: " + ex.getMessage());
	    System.out.println("SQLState: " + ex.getSQLState());
	    System.out.println("VendorError: " + ex.getErrorCode());
		}
		
		
		
	}
	
	public static void insertar_persona(String valor1, String valor2, String mail, String rol, String imagen ) throws SQLException {
		
		Connection conn = Conexion.getInstancia().getConn();
	    String query = "insert into persona (nombre, contrasenia,mail,foto_perfil,rol) values (?,?,?,?,?)";
	    
	    try {
	    	try(PreparedStatement statement = conn.prepareStatement(query)){
	    
			    
			    int logRounds = 12;
			    String salt = BCrypt.gensalt(logRounds);
			    String hashedPassword = BCrypt.hashpw(valor2, salt);
			    
			    statement.setString(1, valor1);
			    statement.setString(2, hashedPassword);
			    statement.setString(3, mail);
			    statement.setString(4, imagen);
			    statement.setString(5, rol);
			    
			    statement.executeUpdate();
		
	    	}
		
	    }catch(SQLException ex){
	    	
    	
		System.out.println("SQLException: " + ex.getMessage());
	    System.out.println("SQLState: " + ex.getSQLState());
	    System.out.println("VendorError: " + ex.getErrorCode());
		}
			
	}
	
	public static Grupo obtener_grupo_persona(Persona p) {
	    // Si la persona no tiene grupo asignado, no hay nada que buscar
	    if (p.getIdgrupo() == null) {
	        return null;
	    }

	    String query = "select * from grupo where idgrupo = ?";
	    
	    Connection conn = Conexion.getInstancia().getConn();

	    try (PreparedStatement ps = conn.prepareStatement(query)) {

	        ps.setInt(1, p.getIdgrupo());

	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                Grupo grupo = new Grupo();
	                grupo.setId(rs.getInt("idgrupo"));
	                grupo.setNombre(rs.getString("nombre"));
	                grupo.setDescripcion(rs.getString("descripcion"));
	                grupo.setFoto_perfil(rs.getString("foto_perfil"));
	                // agrega aca el resto de los campos que tenga tu tabla grupo
	                return grupo;
	            }
	        }

	    } catch (SQLException ex) {
	        System.out.println("SQLException: " + ex.getMessage());
	        System.out.println("SQLState: " + ex.getSQLState());
	        System.out.println("VendorError: " + ex.getErrorCode());
	    }

	    return null; 
	}
	
	
	public static void obtener_todos() {
		
		Connection conn = Conexion.getInstancia().getConn();
		String query = "select * from persona";
			
			try {
				
				
				try(PreparedStatement Resultado = conn.prepareStatement(query)){
				ResultSet rs = Resultado.executeQuery();
				
				
					while (rs.next()) {
					
					String nombre = rs.getString("nombre");
					
					System.out.println(nombre);
					
				}

			}
			}
			
			catch(SQLException ex){
				
				
				System.out.println("SQLException: " + ex.getMessage());
			    System.out.println("SQLState: " + ex.getSQLState());
			    System.out.println("VendorError: " + ex.getErrorCode());
			}
			
				
		

}
}

