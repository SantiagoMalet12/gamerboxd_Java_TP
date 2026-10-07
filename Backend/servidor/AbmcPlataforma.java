package servidor;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

import entities.Grupo;
import entities.Plataforma;

import data.Conexion;
import data.DataPlataforma;

public class AbmcPlataforma {
	//ABMC Plataforma USOS
	//LinkedList<Plataforma> plataformas = AbmcPlataforma.recuperarTodos();
	//Plataforma plataforma = AbmcPlataforma.recuperarPorId(1);
	//AbmcPlataforma.insertarNuevo("Gamecube");
	
	
	
	public static LinkedList<Plataforma> recuperarTodos() {
		LinkedList<Plataforma> companias = new LinkedList<>();
		
		companias = DataPlataforma.recuperarTodos();
		return companias;	
	}
	
	public static Plataforma recuperarPorId(int id) {		
		Plataforma p = null;
		p = DataPlataforma.recuperarPorId(id);
		return p;
	}
	

	public static void insertarNuevo(String nombre) {
		Plataforma plataforma= new Plataforma();
		Plataforma plataformaRecuperada = new Plataforma();
		plataforma.setNombre(nombre);
		plataformaRecuperada = DataPlataforma.insertarPlataforma(plataforma);
		System.out.println(plataformaRecuperada.getNombre());
	}
	
	

}
