package servidor;

import data.FormaEjecucion;

import servidor.AbmcUsuario.login;










import servidor.AbmcUsuario.registro;
import servidor.AbmcUsuario.rolengrupo;
import servidor.AbmcUsuario.recuperarpersona;
import servidor.AbmcUsuario.verificartoken;

import servidor.AbmcCompania.actualizarnombrecompania;
import servidor.AbmcCompania.bajalogicacompania;
import servidor.AbmcCompania.crearcompania;
import servidor.AbmcCompania.existeempresa;
import servidor.AbmcCompania.devolverempresa;



import servidor.AbmcUsuario.cambiarpassword;
import servidor.AbmcUsuario.obtencionfotousuario;
import servidor.AbmcJuegos.actualizardatosjuego;
import servidor.AbmcJuegos.devolverjuego;
import servidor.AbmcJuegos.existejuego;
import servidor.AbmcJuegos.juegoid;

import servidor.AbmcJuegos.listajuegos;
import servidor.AbmcCompania.listaempresas;
import servidor.AbmcGrupo.creargrupo;
import servidor.AbmcGrupo.listarGrupos;
import servidor.AbmcGrupo.obtenergruporeseniador;
import servidor.AbmcUsuario.actualizardatosusuario;
import servidor.AbmcUsuario.verificarjwt;
import java.io.IOException;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;
import servidor.AbmcResenia.obtenerResenias;
import servidor.AbmcCompania.actualizardatosdeempresa;
import servidor.AbmcUsuario.dardebaja;
import servidor.AbmcUsuario.convertirenadmin;
import servidor.AbmcPropuesta.crearPropuesta;
import servidor.AbmcPropuesta.listarPropuestas;
import servidor.AbmcPropuesta.actualizarpropuesta;
import servidor.AbmcGrupo.aniadirMiembroAGrupo;
import servidor.AbmcGrupo.recuperarGrupoPorMiembro;
import servidor.AbmcGrupo.salirDeGrupo;
import servidor.AbmcGrupo.dardebajagrupo;
import servidor.AbmcGrupo.actualizardatosgrupo;

public class ServerHTTP {

	public static void main(String[] args) throws IOException {
		
	
		
		try {
			
				
		int port = System.getenv("PORT") != null ? Integer.parseInt(System.getenv("PORT")) : 8081;
		HttpServer  server = HttpServer.create(new InetSocketAddress(port), 0);
			

		
		server.createContext("/login", new login());
		server.createContext("/registro", new registro());
		server.createContext("/recuperarpassword", new recuperarpersona());
		server.createContext("/verificartoken", new verificartoken());
		server.createContext("/cambiarpassword", new cambiarpassword());
		server.createContext("/allresenias", new obtenerResenias());
		server.createContext("/listajuegos", new listajuegos());
		server.createContext("/listaempresas", new listaempresas());
		server.createContext("/actualizardatosperfil", new actualizardatosusuario());
		server.createContext("/verificarjwt", new verificarjwt());
		server.createContext("/juego/", new juegoid());
		server.createContext("/Actualizarnombrecompania", new actualizarnombrecompania());
		server.createContext("/bajalogicacompania", new bajalogicacompania());
		server.createContext("/crearcompania", new crearcompania());
		server.createContext("/existeempresa", new existeempresa());
		server.createContext("/devolverempresa", new devolverempresa());
		server.createContext("/actualizardatosdeempresa", new actualizardatosdeempresa());
		server.createContext("/dardebaja", new dardebaja());
		server.createContext("/reseniasPorJuego", new AbmcResenia.obtenerReseniasPorJuego());
		server.createContext("/nuevaResenia", new AbmcResenia.nuevaResenia());
		server.createContext("/obtenerUsuarioToken", new AbmcUsuario.obtenerUsuarioToken());
		server.createContext("/editarResenia", new AbmcResenia.editarResenia());
		server.createContext("/borrarResenia", new AbmcResenia.borrarResenia());
		server.createContext("/fotousuario", new obtencionfotousuario());
		server.createContext("/creargrupo", new creargrupo());
		server.createContext("/convertirenadmin", new convertirenadmin());
		server.createContext("/crearPropuesta", new crearPropuesta());	
		server.createContext("/listarPropuestas", new listarPropuestas());	
		server.createContext("/actualizarpropuesta", new actualizarpropuesta());
		server.createContext("/existejuego", new existejuego());
		server.createContext("/devolverjuego", new devolverjuego());
		server.createContext("/actualizardatosjuego", new actualizardatosjuego());
		server.createContext("/rolengrupo", new rolengrupo());
		server.createContext("/listarGrupos", new listarGrupos());
		server.createContext("/aniadirMiembroAGrupo", new aniadirMiembroAGrupo());
		server.createContext("/recuperarGrupoPorMiembro", new recuperarGrupoPorMiembro());
		server.createContext("/salirDeGrupo", new salirDeGrupo());
		server.createContext("/dardebajagrupo", new dardebajagrupo());
		server.createContext("/actualizardatosgrupo", new actualizardatosgrupo());
		server.createContext("/obtenergruporeseniador", new obtenergruporeseniador());
		

		server.start();
		System.out.println("Server corriendo");
		}
		catch (IOException e){
			 System.out.println("error al inciar el servidor:" + e);
			
		}
				
	}
	
	}	

