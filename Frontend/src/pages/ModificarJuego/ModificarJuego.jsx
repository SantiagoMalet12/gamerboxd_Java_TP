import { useLocation, useNavigate } from 'react-router-dom';
import { useState, useEffect } from 'react';
import { API_URL } from '../../config';
import Footer from '../../components/Footer/Footer';
import AlertMessage from '../../components/AlertMessage/AlertMessage';
import './ModificarJuego.css';

function ModificarJuego(){
	
	const navigate = useNavigate();
	const location = useLocation();
	const nombreJuegoOriginal = location.state?.nombre || "";
	
	const [nuevonombrejuego, setnuevonombre] = useState("");
	const [estado, setestado] = useState("");
	const [id, setid] = useState("");
	const [imagen, setimagen] = useState("");
	const [estadofoto, setestadofoto] = useState(false);
	
	const [juegoconfirmado, setjuegoconfirmado] = useState(nombreJuegoOriginal);
	const [estadoconfirmado, setestadoconfirmado] = useState(""); 
    
    
    const [alerta, setAlerta] = useState(null);
	
	let token = localStorage.getItem('token');
	
	const insertarimagen = (e) => {
        if(e.target.files && e.target.files[0]) {
            let reader = new FileReader();
            reader.readAsDataURL(e.target.files[0]);
            reader.onload = () => {
                setimagen(reader.result);
                setestadofoto(true);
            }
        }
    }
	
	const volver = () => {
		navigate("/MenuPropuestas");
	}
	
	const cambiarnombre = (e) => {
		setnuevonombre(e.target.value);
	}
	
	const dardebaja = () => {
		if(estado === "activo"){
			setestado("inactivo");
		} else if(estado === "inactivo"){
			setestado("activo");
		}
	}
	
	const huboCambios = 
		(nuevonombrejuego.trim() !== "" && nuevonombrejuego.trim() !== juegoconfirmado) || 
		(estado !== estadoconfirmado) || 
        (estadofoto !== false);

	const guardarcambios = (e) => {
		e.preventDefault();
		
		let nombreFinalParaEnviar = nuevonombrejuego.trim();
		    
        if (nombreFinalParaEnviar !== "") {
            const nombreoriginalnormalizado = juegoconfirmado.toLowerCase().trim().normalize("NFD").replace(/[\u0300-\u0306]/g, "");
            const nuevonombrenormalizado = nombreFinalParaEnviar.toLowerCase().trim().normalize("NFD").replace(/[\u0300-\u0306]/g, "");
            
            if (nombreoriginalnormalizado === nuevonombrenormalizado) {
                setAlerta({ tipo: 'error', mensaje: "El nombre elegido es el mismo que ya posee el juego." });
                setTimeout(() => setAlerta(null), 4000);
                return;
            }
        }
		
		fetch(`${API_URL}/actualizardatosjuego`, {
			method: "POST",
			body: JSON.stringify({
				name: nombreFinalParaEnviar,
				estado: estado,
				id: id,
				background_image: imagen
			}),
			headers: {
				"Content-type": "application/json",	
				'Authorization': 'Bearer ' + token 
			}
		})
		.then(response => {
			if(response.status === 200){
                setAlerta({ tipo: 'ok', mensaje: "Actualización realizada con éxito." });
                setTimeout(() => setAlerta(null), 3500);
				
				if (nombreFinalParaEnviar !== "") {
                    setjuegoconfirmado(nombreFinalParaEnviar);
                }
				
				setnuevonombre("");
				setestadoconfirmado(estado);
				setestadofoto(false);
			}
			else if (response.status === 409){
                setAlerta({ tipo: 'error', mensaje: "Ya existe un juego registrado con ese nombre." });
                setTimeout(() => setAlerta(null), 4000);
			}
			else{
                setAlerta({ tipo: 'error', mensaje: "Error en la conexión, pruebe más tarde." });
                setTimeout(() => setAlerta(null), 4000);
			}
		})
		.catch(error => {
			console.error("El error real es:", error); 
            setAlerta({ tipo: 'error', mensaje: "Error en la conexión con la base de datos." });
            setTimeout(() => setAlerta(null), 4000);
		});
	}
	
	useEffect(() => {		
		fetch(`${API_URL}/devolverjuego`,{
			method: 'POST', 
			headers: {
				'Content-Type': 'application/json',
				'Authorization': 'Bearer ' + token 
			},
			body: JSON.stringify({name: nombreJuegoOriginal})
		})
		.then(response => {
			if (response.status === 200) {
				return response.json().then(data => {
					console.log(data);
					setid(data.id);
					setestado(data.estado);
					setestadoconfirmado(data.estado); 
					setimagen(data.background_image);
				});
			} 
			else if (response.status === 402) {
                setAlerta({ tipo: 'error', mensaje: "Error de autorización (Token inválido)." });
			}
			else {
                setAlerta({ tipo: 'error', mensaje: "Error en la base de datos, intente más tarde." });
			}
		})
		.catch(error => {
			console.error("El error real es:", error); 
            setAlerta({ tipo: 'error', mensaje: "Error en la conexión con la base de datos." });
		});
	}, [nombreJuegoOriginal, token]);

	return(
        <section className="pagina-modificar-juego">
            
            
            <div className='modificar-juego-container'>
                
                <div className="modificar-juego-card-glass">
                    <div className="modificar-juego-header">
                        <h2 className="modificar-juego-titulo">Modificar Juego</h2>
                        <p className="modificar-juego-subtitulo">Editando datos de: <strong>{juegoconfirmado}</strong></p>
                    </div>

                    <form className="modificar-juego-form" onSubmit={guardarcambios}>
                        
                        
                        <div className="modificar-juego-avatar-seccion">
                            <img 
                                className="modificar-juego-avatar" 
                                src={imagen || "https://picsum.photos/150"} 
                                alt="Portada del juego" 
                            />
                            
                           
                            <input 
                                id="subir-foto-juego"
                                className='file-input-oculto' 
                                type="file" 
                                accept="image/*"
                                onChange={insertarimagen}	
                            />
                            <label htmlFor="subir-foto-juego" className="btn-secundario btn-chico">
                                Cambiar imagen
                            </label>
                        </div>

                     
                        <div className="modificar-juego-info">
                            <span className={`estado-badge ${estado === 'activo' ? 'estado-activo' : 'estado-inactivo'}`}>
                                Estado actual: {estado}
                            </span>
                        </div>

                        <div className="modificar-juego-campo">
                            <label className="modificar-juego-label">Modificar nombre</label>
                            <input 
                                className="modificar-juego-input"
                                type="text"
                                placeholder="Dejar en blanco para mantener el actual"
                                value={nuevonombrejuego} 
                                onChange={cambiarnombre}
                            />
                        </div>
                        
                        <div className="modificar-juego-acciones">
                            <button className="btn-secundario" type="button" onClick={dardebaja}>
                                Pasar a {estado === 'activo' ? 'Inactivo' : 'Activo'}
                            </button>
                            
                            <button className="btn-primario" type="submit" disabled={!huboCambios}>
                                Guardar cambios
                            </button>
                            
                            <button className="btn-peligro" type="button" onClick={volver}>
                                 Volver
                            </button>
                        </div>
                    </form>
                </div>

                
                {alerta !== null && (
                    <div className="modificar-juego-alerta-wrapper">
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
	)
}

export default ModificarJuego;