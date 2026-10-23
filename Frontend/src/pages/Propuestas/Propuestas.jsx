import './Propuestas.css';
import Footer from '../../components/Footer/Footer';
import AlertMessage from '../../components/AlertMessage/AlertMessage';
import { useState, useEffect } from 'react';
import { API_URL } from '../../config';

function Propuestas(){
	
	const [listaPropuestas, setListaPropuestas] = useState([]);
    const [alerta, setAlerta] = useState(null);
	const [descripcionModal, setDescripcionModal] = useState(null);
	useEffect(() => {
        let token = localStorage.getItem('token');
        
        fetch(`${API_URL}/listarPropuestas`,{
            method: 'POST', 
            headers: {
                'Authorization': 'Bearer ' + token 
            },			
        })
        .then(response => {
            if (!response.ok) {
                throw new Error("Fallo al obtener las propuestas");
            }
            return response.json();
        })
        .then(data => {
            setListaPropuestas(data);
            console.log(data);
        })
        .catch(error => {
            console.error('Error:', error);
            setAlerta({ tipo: 'error', mensaje: 'No se pudieron cargar las propuestas. Intente nuevamente más tarde.' });
            setTimeout(() => setAlerta(null), 5000);
        });

	}, []);
    
	const actualizarEstado = (propuesta, nuevoEstado) => {
	        let token = localStorage.getItem('token');
	        
	        fetch(`${API_URL}/actualizarpropuesta`, {
	            method: 'POST', 
	            headers: {
	                'Content-Type': 'application/json',
	                'Authorization': 'Bearer ' + token
	            },
	            body: JSON.stringify({
	                id_propuesta: propuesta.id_propuesta,            
	                estado: nuevoEstado,
	                nombrejuego: propuesta.nombrejuego,
	                foto: propuesta.foto,  
	                descripcionjuego: propuesta.descripcionjuego,
	                companiasJuego: propuesta.companiasJuego
	            })
	        })
	        .then(response => {
	            if (response.status === 200) {
	                setAlerta({ tipo: 'ok', mensaje: 'Juego añadido a la plataforma' });
	                setTimeout(() => setAlerta(null), 3500);
	                
	                
	                setListaPropuestas(listaActual => 
	                    listaActual.map(p => 
	                        p.id_propuesta === propuesta.id_propuesta ? { ...p, estado: 'aceptado' } : p
	                    )
	                );
	            }
	            else if (response.status === 202) {
	                setAlerta({ tipo: 'ok', mensaje: 'Juego rechazado.' });
	                setTimeout(() => setAlerta(null), 3500);
	                
	                
	                setListaPropuestas(listaActual => 
	                    listaActual.map(p => 
	                        p.id_propuesta === propuesta.id_propuesta ? { ...p, estado: 'rechazado' } : p
	                    )
	                );
	            }
	            else {
	                setAlerta({ tipo: 'error', mensaje: "Hubo un problema al actualizar el estado: " + response.status });
	                setTimeout(() => setAlerta(null), 4000);
	            }
	        })
	        .catch(error => {
	            console.error('Error al actualizar:', error);
	            setAlerta({ tipo: 'error', mensaje: 'Error de conexión con el servidor.' });
	            setTimeout(() => setAlerta(null), 4000);
	        });
	    };

    
    const pendientes = listaPropuestas.filter(p => p.estado?.toLowerCase() === 'pendiente');
    const resueltas = listaPropuestas.filter(p => p.estado?.toLowerCase() !== 'pendiente');
	
	return(
        <section className="pagina-propuestas">
            <div className='propuestas-container'>
                
                <div className="propuestas-header">
                    <h2 className="propuestas-titulo">Propuestas Enviadas</h2>
                    <p className="propuestas-subtitulo">Revisa los juegos sugeridos para la plataforma.</p>
                    
                    {alerta !== null && (
                        <div className="propuestas-alerta-wrapper">
                            <AlertMessage 
                                tipo={alerta.tipo} 
                                mensaje={alerta.mensaje} 
                                onClose={() => setAlerta(null)} 
                            />
                        </div>
                    )}
                </div>
                
                <div className="tablas-seccion">
                    
                    
                    <h3 className="tabla-titulo-seccion">Pendientes de revision</h3>
                    <div className="tabla-glass-wrapper">
                        <div className="tabla-responsive">
                            <table className="propuestas-tabla">
                                <thead>
                                    <tr>
                                        <th>Imagen</th>
                                        <th>Nombre del Juego</th>
                                        <th>Descripcion</th>
                                        <th>Estado</th>
                                        <th>Compañía</th>
                                        <th>Acciones</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {pendientes.length > 0 ? (
                                        pendientes.map((propuesta) => (
                                            <tr key={propuesta.id_propuesta}>
                                                <td>
                                                    <div className="tabla-img-container">
                                                        <img 
                                                            src={propuesta.foto || "https://thumb.wikimedia.org/wikipedia/commons/thumb/2/2c/Default_pfp.svg/500px-Default_pfp.svg.png?_=20220226140232"} 
                                                            alt="Imagen de propuesta" 
                                                            className="tabla-img"
                                                        />
                                                    </div>
                                                </td>
                                                <td className="tabla-texto-destacado">{propuesta.nombrejuego}</td>
												<td className="tabla-descripcion descripcion-clickable" 
		                                            onClick={() => setDescripcionModal(propuesta.descripcionjuego)}
		                                            title="Clic para leer toda la descripción">
		                                            {propuesta.descripcionjuego}
		                                        </td>
                                                <td>
                                                    <span className="tabla-badge badge-pendiente">
                                                        {propuesta.estado}
                                                    </span>
                                                </td>
                                                <td className="tabla-texto">
                                                    {Array.isArray(propuesta.companiasJuego) 
                                                        ? propuesta.companiasJuego.map(c => c.name).join(', ') 
                                                        : (propuesta.companiasJuego?.name || propuesta.companiasJuego || "Sin compañía")}
                                                </td>
												<td className="tabla-acciones">
		                                            <button 
		                                                className="btn-tabla-aceptar" 
		                                                onClick={() => actualizarEstado(propuesta, 'aceptado')}
		                                            >
		                                                Aceptar
		                                            </button>
		                                            <button 
		                                                className="btn-tabla-rechazar"
		                                                onClick={() => actualizarEstado(propuesta, 'rechazado')}
		                                            >
		                                                Rechazar
		                                            </button>
		                                        </td>
                                       		</tr>
                                        ))
                                    ) : (
                                        <tr>
                                            <td colSpan="6" className="propuestas-vacio">
                                                No hay propuestas pendientes de revision.
                                            </td>
                                        </tr>
                                    )}
                                </tbody>
                            </table>
                        </div>
                    </div>

                    
                    <h3 className="tabla-titulo-seccion titulo-espaciado">Historial de Propuestas</h3>
                    <div className="tabla-glass-wrapper">
                        <div className="tabla-responsive">
                            <table className="propuestas-tabla">
                                <thead>
                                    <tr>
                                        <th>Imagen</th>
                                        <th>Nombre del Juego</th>
                                        <th>Descripcion</th>
                                        <th>Estado</th>
                                        <th>Compañia</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {resueltas.length > 0 ? (
                                        resueltas.map((propuesta) => (
                                            <tr key={propuesta.id_propuesta}>
                                                <td>
                                                    <div className="tabla-img-container">
                                                        <img 
                                                            src={propuesta.foto || "https://thumb.wikimedia.org/wikipedia/commons/thumb/2/2c/Default_pfp.svg/500px-Default_pfp.svg.png?_=20220226140232"} 
                                                            alt="Imagen de propuesta" 
                                                            className="tabla-img"
                                                        />
                                                    </div>
                                                </td>
                                                <td className="tabla-texto-destacado">{propuesta.nombrejuego}</td>
												<td className="tabla-descripcion descripcion-clickable" 
		                                            onClick={() => setDescripcionModal(propuesta.descripcionjuego)}
		                                            title="Clic para leer toda la descripción">
		                                            {propuesta.descripcionjuego}
		                                        </td>
                                                <td>
                                                    <span className={`tabla-badge badge-${propuesta.estado?.toLowerCase() || 'pendiente'}`}>
                                                        {propuesta.estado}
                                                    </span>
                                                </td>
                                                <td className="tabla-texto">
                                                    {Array.isArray(propuesta.companiasJuego) 
                                                        ? propuesta.companiasJuego.map(c => c.name).join(', ') 
                                                        : (propuesta.companiasJuego?.name || propuesta.companiasJuego || "Sin compañia")}
                                                </td>
                                            </tr>
                                        ))
                                    ) : (
                                        <tr>
                                            <td colSpan="5" className="propuestas-vacio">
                                                Aun no hay propuestas aceptadas o rechazadas.
                                            </td>
                                        </tr>
                                    )}
                                </tbody>
                            </table>
                        </div>
                    </div>

                </div>
            </div>
            
            <Footer />
			
			{descripcionModal && (
			                <div className="modal-overlay" onClick={() => setDescripcionModal(null)}>
			                    
			                    <div className="modal-glass" onClick={(e) => e.stopPropagation()}>
			                        <div className="modal-header">
			                            <h3 className="modal-titulo">Descripcion completa</h3>
			                            <button className="modal-cerrar" onClick={() => setDescripcionModal(null)}>×</button>
			                        </div>
			                        <div className="modal-body">
			                            <p className="modal-texto">{descripcionModal}</p>
			                        </div>
			                    </div>
			                </div>
			            )}
        </section>
    );
}

export default Propuestas;