import { useLocation } from 'react-router-dom';
import { useNavigate } from "react-router-dom"; 
import { useState, useEffect } from 'react';
import { API_URL } from '../../config';
import Footer from '../../components/Footer/Footer';
import AlertMessage from '../../components/AlertMessage/AlertMessage';
import './modificarcompanias.css'

function Modificarcompanias(){
	
	
	
	
	
	const navigate = useNavigate();
	const location = useLocation();
	const nombreEmpresaOriginal = location.state?.nombre || "";
	
	const [nuevonombreempresa, setnuevonombre] = useState("");
	const [estado, setestado] = useState("");
	const [id, setid] = useState("");
	
	const [empresaconfirmada, setempresaconfirmada] = useState(nombreEmpresaOriginal);
	const [estadoconfirmado, setestadoconfirmado] = useState("");
	

    const [alerta, setAlerta] = useState(null);
	
	let token = localStorage.getItem('token');
	
	
	
	
	
	
	const volver = () => {
		navigate("/AdministrarCompanias");
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
		(nuevonombreempresa.trim() !== "" && nuevonombreempresa.trim() !== empresaconfirmada) || 
		(estado !== estadoconfirmado);

	const guardarcambios = (e) => {
		e.preventDefault();
		
		let nombreFinalParaEnviar = nuevonombreempresa.trim() === "" ? empresaconfirmada : nuevonombreempresa;
		
		if (nuevonombreempresa.trim() !== "") {
			const nombreoriginalnormalizado = empresaconfirmada.toLowerCase().trim().normalize("NFD").replace(/[\u0300-\u0306]/g, "");
			const nuevonombrenormalizado = nuevonombreempresa.toLowerCase().trim().normalize("NFD").replace(/[\u0300-\u0306]/g, "");
			
			if(nombreoriginalnormalizado === nuevonombrenormalizado){
                setAlerta({ tipo: 'error', mensaje: "El nombre elegido es el mismo que ya posee la empresa." });
                setTimeout(() => setAlerta(null), 4000);
				return;
			}
		}
		
		fetch(`${API_URL}/actualizardatosdeempresa`, {
			method: "POST",
			body: JSON.stringify({
				name: nombreFinalParaEnviar,
				estado: estado,
				id: id
			}),
			headers: {
				"Content-type": "application/json",	
				'Authorization': 'Bearer ' + token 
			}
		})
		.then(response => {
			if(response.status === 200){
                setAlerta({ tipo: 'ok', mensaje: "Actualización realizada con exito." });
                setTimeout(() => setAlerta(null), 3500);
				
				setempresaconfirmada(nombreFinalParaEnviar);
				setestadoconfirmado(estado);
				setnuevonombre(""); 
			}
			else if (response.status === 409){
                setAlerta({ tipo: 'error', mensaje: "Ya existe una empresa registrada con ese nombre." });
                setTimeout(() => setAlerta(null), 4000);
			}
			else{
                setAlerta({ tipo: 'error', mensaje: "Error en la conexion, pruebe mas tarde." });
                setTimeout(() => setAlerta(null), 4000);
			}
		})
		.catch(error => {
			console.error("El error real es:", error); 
            setAlerta({ tipo: 'error', mensaje: "Error en la conexion con la base de datos." });
            setTimeout(() => setAlerta(null), 4000);
		});
	}
	
	useEffect(() => {		
		fetch(`${API_URL}/devolverempresa`,{
			method: 'POST', 
			headers: {
				'Content-Type': 'application/json',
				'Authorization': 'Bearer ' + token 
			},
			body: JSON.stringify({name: nombreEmpresaOriginal})
		})
		.then(response => {
			if (response.status === 200) {
				return response.json().then(data => {
					
					setid(data.id);
					setestado(data.estado);
					setestadoconfirmado(data.estado); 
				});
			} 
			else if (response.status === 402) {
                setAlerta({ tipo: 'error', mensaje: "Error de autorizacion (Token invalido)." });
			}
			else {
                setAlerta({ tipo: 'error', mensaje: "Error en la base de datos, intente mas tarde." });
			}
		})
		.catch(error => {
			console.error("El error real es:", error); 
            setAlerta({ tipo: 'error', mensaje: "Error en la conexion con la base de datos." });
		});
	}, [nombreEmpresaOriginal, token]);

	return(
        <section className="pagina-modificar-compania">
            <div className='modificar-container'>
                
                <div className="modificar-card-glass">
                    <div className="modificar-header">
                        <h2 className="modificar-titulo">Modificar Compañia</h2>
                        <p className="modificar-subtitulo">Gestionando: <strong>{empresaconfirmada}</strong></p>
                    </div>

                    <form className="modificar-form" onSubmit={guardarcambios}>
                        
                        <div className="modificar-info">
                            <span className={`estado-badge ${estado === 'activo' ? 'estado-activo' : 'estado-inactivo'}`}>
                                Estado actual: {estado}
                            </span>
                        </div>

                        <div className="modificar-campo">
                            <label className="modificar-label">Cambiar nombre</label>
                            <input 
                                className="modificar-input"
                                type="text"
                                placeholder="Dejar en blanco para conservar el actual"
                                value={nuevonombreempresa} 
                                onChange={cambiarnombre}
                            />
                        </div>
                        
                        <div className="modificar-acciones">
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
                    <div className="modificar-alerta-wrapper">
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

export default Modificarcompanias;