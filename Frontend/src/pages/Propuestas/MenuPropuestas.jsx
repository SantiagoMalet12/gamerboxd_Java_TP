import { useNavigate } from "react-router-dom";
import { useState, useEffect } from 'react';
import { API_URL } from '../../config';
import Footer from '../../components/Footer/Footer';
import AlertMessage from '../../components/AlertMessage/AlertMessage';
import './menuPropuestas.css';

function MenuPropuestas(){
	
	const navigate = useNavigate();
	
	const [nombrejuego, setnombrejuego] = useState("");
    const [alerta, setAlerta] = useState(null);
	const [juegos, setJuegos] = useState([]);
    const [filtrados, setFiltrados] = useState([]);
    const [mostrarFiltrados, setMostrarFiltrados] = useState(false);
	
	useEffect(() => {
	        fetch(`${API_URL}/listajuegos`)
	            .then(res => res.json())
	            .then(data => setJuegos(data))
	            .catch(err => console.error("Error al traer juegos", err));
	    }, []);
		
		const manejarnombrejuego = (e) => {
		        const valor = e.target.value;
		        setnombrejuego(valor);
		        
		        if (valor.length > 0) {
		           
		            const juegosFiltrados = juegos.filter(juego => 
		                juego.name.toLowerCase().includes(valor.toLowerCase())
		            );
		            setFiltrados(juegosFiltrados);
		            setMostrarFiltrados(true); 
		        } else {
		            setFiltrados([]);
		            setMostrarFiltrados(false);
		        }
		    };
			
	const seleccionarFiltrado = (nombre) => {
	        setnombrejuego(nombre);
	        setMostrarFiltrados(false);
	    };
		
	const verpropuestas = () => {
		navigate("/Propuestas");
	}
	
	const modificarjuego = (e) => {
		e.preventDefault();
		let token = localStorage.getItem('token');
        
        if (!nombrejuego.trim()) {
            setAlerta({ tipo: 'error', mensaje: "Por favor, ingresa el nombre de un juego para buscar." });
            setTimeout(() => setAlerta(null), 4000);
            return;
        }
				
		fetch(`${API_URL}/existejuego`,{
			method: 'POST', 
			headers: {
			    'Content-Type': 'application/json',
			    'Authorization': 'Bearer ' + token 
            },
			body: JSON.stringify({name: nombrejuego})
		})
		.then(response => {
	        if (response.status === 200) {
                setAlerta({ tipo: 'ok', mensaje: "Juego encontrado. Redirigiendo..." });
                setTimeout(() => {
                    setnombrejuego(""); 
				    navigate("/ModificarJuego", {state:{nombre: nombrejuego}});
                }, 1500);
	        } 
			else if (response.status === 404) {
                setAlerta({ tipo: 'error', mensaje: "No existe un juego con ese nombre en la base de datos." });
	            setnombrejuego(""); 
                setTimeout(() => setAlerta(null), 4000);
	        }
			else if (response.status === 401 || response.status === 402) {
                setAlerta({ tipo: 'error', mensaje: "Error de autorización o token invalido." });
	            setnombrejuego(""); 
                setTimeout(() => setAlerta(null), 4000);
	        }
			else {
                setAlerta({ tipo: 'error', mensaje: "Error en la base de datos, intente mas tarde." });
                setTimeout(() => setAlerta(null), 4000);
	        }
	    })
	    .catch(error => {
            console.error('Error de conexión:', error);
            setAlerta({ tipo: 'error', mensaje: "Error en la conexion con la base de datos." });
            setTimeout(() => setAlerta(null), 4000);
        });
	};

    return(
        <section className="pagina-menu-propuestas">
            <div className="menu-propuestas-container">
                
                <div className="menu-propuestas-card-glass">
                    <div className="menu-propuestas-header">
                        <h2 className="menu-propuestas-titulo">Gestion de Juegos</h2>
                       i<p className="menu-propuestas-subtitulo">Busca un titulo existente para modificarlo o revisa las nuevas sugerencias.</p>
                    </div>

                    <form className="menu-propuestas-form" onSubmit={modificarjuego}>
                        <div className="menu-propuestas-campo">
                            <label className="menu-propuestas-label">Buscar juego por nombre</label>
                            <input
                                className="menu-propuestas-input"
                                type="text"
                                placeholder="Ej: Fallout 4"
                                value={nombrejuego}
                                onChange={manejarnombrejuego}
                            /> 
							{mostrarFiltrados && filtrados.length > 0 && (
                                <ul className="sugerencias-lista">
                                    {filtrados.map((juego, index) => (
                                        <li 
                                            key={index} 
                                            onClick={() => seleccionarFiltrado(juego.name)}
                                            className="sugerencia-item"
                                        >
                                            {juego.name}
                                        </li>
                                    ))}
                                </ul>
                            )}
                        </div>
                        
                        <div className="menu-propuestas-acciones">
                            <button className="btn-primario" type="submit">
                                Modificar juego
                            </button>
                            
                            <button className="btn-secundario" type="button" onClick={verpropuestas}>
                                Ver propuestas recibidas
                            </button>
                        </div>
                    </form>
                </div>

                
                {alerta !== null && (
                    <div className="menu-propuestas-alerta-wrapper">
                        <AlertMessage 
                            tipo={alerta.tipo} 
                            mensaje={alerta.mensaje} 
                            onClose={() => setAlerta(null)} 
                        />
                    </div>
                )}

            </div>
            
            <Footer />
        </section>
    );
}

export default MenuPropuestas;