import './GruposCard.css'
import { useNavigate } from "react-router-dom";
import { API_URL } from '../../config';

const IconoUsuarios = () => (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
        <circle cx="9" cy="7" r="4"></circle>
        <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
        <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
    </svg>
);


function GruposCard({ gru, onMostrarAlerta }) {
	const navigate = useNavigate();
	
	const actualizarEstado = () => {
	        let token = localStorage.getItem('token');
	        
	        fetch(`${API_URL}/aniadirMiembroAGrupo`, {
	            method: 'POST', 
	            headers: {
	                'Content-Type': 'application/json',
	                'Authorization': 'Bearer ' + token
	            },
	            body: JSON.stringify({ id: gru.id })
	        })
	        .then(res => {
	         
	            if (res.ok) {
	                return true; 
	            } else {
	                throw new Error("El servidor devolvió un error");
	            }
	        })
	        .then(() => {
	            console.log("Mostrando alerta verde...");
	          
	            onMostrarAlerta({ tipo: 'ok', mensaje: '¡Fuiste añadido al grupo exitosamente!' });
	            
	       
	            setTimeout(() => {
	                navigate('/PaginaGrupo');
	            }, 3000);
	        })
	        .catch(error => {
	            console.error('Error al actualizar:', error);
	            onMostrarAlerta({ tipo: 'error', mensaje: "Hubo un problema al agregarte al grupo." });
	            
	            setTimeout(() => onMostrarAlerta(null), 5000);
	        });
	    };
	
	return(
		<div className='card-grupo'>
            <div className="card-grupo-imagen-container">
                <img 
                    className='card-grupo-imagen' 
                    src={gru.imagen || "https://thumb.wikimedia.org/wikipedia/commons/thumb/2/2c/Default_pfp.svg/500px-Default_pfp.svg.png?_=20220226140232"} 
                    alt={`Perfil de ${gru.name}`}
                />
            </div>
            
            <div className='card-grupo-contenido'>
                <h3 className='card-grupo-titulo'>{gru.name}</h3>
                <p className='card-grupo-descripcion'>{gru.descripcion}</p>
                
                <span className='card-grupo-badge'>
                    <IconoUsuarios />
                    {gru.cantidadIntegrantes} {gru.cantidadIntegrantes === 1 ? 'Integrante' : 'Integrantes'}
                </span>
            </div>
        
            <div className="card-grupo-footer">
                <button className='btn-entrar' onClick={actualizarEstado}> 
                    Entrar 
                </button>
            </div>
		</div>
	);
}

export default GruposCard;