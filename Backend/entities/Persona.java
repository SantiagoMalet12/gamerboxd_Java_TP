package entities;

public class Persona {
	private String nombre_usuario;
	private String contrasenia;
	private String mail;
	private String rol;
	private String foto_perfil;
	private String token;
	private Integer idgrupo;
	private String rolgrupo;
	private String estado;
	private String nombre_grupo;
	
	
	
	
	
	
	

	public String getNombre_grupo() {
		return nombre_grupo;
	}
	public void setNombre_grupo(String nombre_grupo) {
		this.nombre_grupo = nombre_grupo;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public String getContrasenia() {
		return contrasenia;
	}
	public void setContrasenia(String contrasenia) {
		this.contrasenia = contrasenia;
	}
	
	
	public Integer getIdgrupo() {
		return idgrupo;
	}
	public void setIdgrupo(Integer idgrupo) {
		this.idgrupo = idgrupo;
	}
	public String getRolgrupo() {
		return rolgrupo;
	}
	public void setRolgrupo(String rolgrupo) {
		this.rolgrupo = rolgrupo;
	}
	public String getRol() {
		return rol;
	}
	public void setRol(String rol) {
		this.rol = rol;
	}

	public String getNombre_usuario() {
		return nombre_usuario;
	}
	public void setNombre_usuario(String nombre_usuario) {
		this.nombre_usuario = nombre_usuario;
	}
	public String getContrasena() {
		return contrasenia;
	}
	public void setContrasena(String contrasena) {
		this.contrasenia = contrasena;
	}
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	public String getFoto_perfil() {
		return foto_perfil;
	}
	public void setFoto_perfil(String foto_perfil) {
		this.foto_perfil = foto_perfil;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
	
}