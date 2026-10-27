import './PaginaGrupo.css'
import { useLocation } from 'react-router-dom';
import { useState, useEffect } from 'react';
import { useNavigate } from "react-router-dom";
import { API_URL } from '../../config';
import Footer from '../../components/Footer/Footer';
import AlertMessage from '../../components/AlertMessage/AlertMessage';

const IconoMiembro = () => (
    <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
        <circle cx="12" cy="7" r="4"></circle>
    </svg>
);

const IconoAdmin = () => (
	<svg
	  xmlns="http://www.w3.org/2000/svg"
	  width="16"
	  height="16"
	  viewBox="0 0 24 24"
	  fill="none"
	  stroke="#ffcc00"
	  stroke-width="1"
	  stroke-linecap="round"
	  stroke-linejoin="round"
	>
	  <path d="M12 6l4 6l5 -4l-2 10h-14l-2 -10l5 4z" />
	</svg>
);

function PaginaGrupo(){
	const location = useLocation();
	const navigate = useNavigate();
	
	const rol = location.state?.rol;
	
	const [imagen, setimagen] = useState("")
	const [nombre, setnombre] = useState("")
	const [descripcion, setdescripcion] = useState("")
	const [miembros, setMiembros] = useState([]);
	const [idgrupo, setidgrupo] = useState("")
    
    const [alerta, setAlerta] = useState(null);
	
	const modificarGrupo = () => {
		navigate("/ModificarGrupo", {
			state: {
				img: imagen,
				nombre: nombre,
				id: idgrupo
			}
		})
	}
	
	const salirDelGrupo = async () => {
	    const tokenActual = localStorage.getItem('token');

	    try {
	        const response = await fetch(`${API_URL}/salirDeGrupo`, {
	            method: 'POST',
	            headers: {
	                'Content-Type': 'application/json',
	                'Authorization': 'Bearer ' + tokenActual
	            },
	            body: JSON.stringify({})
	        });

	        if (!response.ok) { 
                const text = await response.text();
                throw new Error(text || `Error HTTP ${response.status}`);
	        }
            
            setAlerta({ tipo: 'ok', mensaje: "Saliste del grupo correctamente." });
            setTimeout(() => {
                navigate('/');
            }, 2000);
            
	    } catch (error) {
	        console.error('Error en salirDeGrupo:', error);
	        setAlerta({ tipo: 'error', mensaje: error.message || "Error al intentar salir del grupo" });
            setTimeout(() => setAlerta(null), 5000);
	    }
	};
	
	useEffect(() => {
		let tokenActual = localStorage.getItem('token');
		
	    fetch(`${API_URL}/recuperarGrupoPorMiembro`, {
		  method: 'POST', 
		  headers: {
		    'Content-Type': 'application/json',
			'Authorization': 'Bearer ' + tokenActual
		  },
		  body: JSON.stringify({}) 
		})
		.then(response => response.json())
		.then(data => {
			setimagen(data.foto_perfil)
			setnombre(data.nombre)
			setdescripcion(data.descripcion)
			setMiembros(data.integrantes)
			setidgrupo(data.id)
		})
		.catch(error => console.error('Error:', error));
		
	}, []);
	console.log(miembros)
	return(
        <section className="pagina-detalle-grupo">
            
            {alerta !== null && (
                <div className="grupo-alerta-wrapper">
                    <AlertMessage 
                        tipo={alerta.tipo} 
                        mensaje={alerta.mensaje} 
                        onClose={() => setAlerta(null)} 
                    />
                </div>
            )}

            <div className='divprincipalgrupo'>
                <div className="grupo-card-glass">
                    
                    <div className="grupo-header">
                        <img 
                            className='grupo-imagen' 
                            src={imagen || "https://thumb.wikimedia.org/wikipedia/commons/thumb/2/2c/Default_pfp.svg/500px-Default_pfp.svg.png?_=20220226140232"} 
                            alt={`Imagen de ${nombre}`}
                        />
                        <h2 className='grupo-titulo'>{nombre}</h2>
                    </div>

                    <div className="grupo-body">
                        <h4 className='grupo-subtitulo'>Descripción del grupo</h4>
                        <p className='grupo-texto'>{descripcion}</p>

                        <h4 className='grupo-subtitulo'>Miembros del grupo ({miembros.length})</h4>
                        
						<div className='seccion-miembros'>
	                            {[...miembros]
	                                .sort((a, b) => {
	                                    if (a.rolgrupo === 'admin' && b.rolgrupo !== 'admin') return -1;
	                                    if (a.rolgrupo !== 'admin' && b.rolgrupo === 'admin') return 1;
	                                    return a.nombre_usuario.localeCompare(b.nombre_usuario);
	                                })
	                                .map((miembro, index) => {
	                                    const esAdmin = miembro.rolgrupo === 'admin';
	                                    
	                                    return (
	                                        <span key={index} className={`miembro-pill ${esAdmin ? 'pill-admin' : ''}`}>
	                                            {esAdmin ? <IconoAdmin /> : <IconoMiembro />}
	                                            {miembro.nombre_usuario}
	                                        </span>
	                                    );
	                                })}
	                        </div>
                    </div>

                    <div className="grupo-acciones">
                        {rol === 'miembro' && (
                            <button className="btn-peligro" onClick={salirDelGrupo} type="button">
                                Salir del grupo
                            </button>
                        )}
                        
                        {rol === 'admin' && (
                            <button className="btn-primario" onClick={modificarGrupo} type="button">
                                Editar información del grupo
                            </button>
                        )}
                    </div>

                </div>
            </div>

            <Footer />
        </section>
	)
}

export default PaginaGrupo;