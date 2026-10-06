import './Juego.css'
import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import FooterC from '../../components/Footer/Footer';
import AlertMessage from '../../components/AlertMessage/AlertMessage';
import { API_URL } from '../../config';


function JuegoResenia(){

	const { id } = useParams(); 
	const [juego, setJuego] = useState(null);
	const [resenias, setResenias] = useState([]);
	const [usuario, setUsuario] = useState(null);
	const [mostrarModalBorrar, setMostrarModalBorrar] = useState(false);
	const [alerta, setAlerta] = useState(null);
	const [nombregrupo, setNombreGrupo] = useState("");
	const [usuarioLogueado, setUsuarioLogueado] = useState(()=>{
		const token = localStorage.getItem('token');
		return token ? token : null
	});
	
	const [reseniaPropia, setReseniaPropia] = useState(null);
	const [editando, setEditando] = useState(false); 

	const handleBorrarResenia = () => {
	    setMostrarModalBorrar(true);};
		  
	  const confirmarBorrado = () => {
		    const token = localStorage.getItem('token'); 
		    
		    fetch(`${API_URL}/borrarResenia?id=${id}`, {
		        method: 'DELETE',
		        headers: {
		            'Content-Type': 'application/json',
		            'Authorization': `Bearer ${token}`
		        }
		    })
		    .then(response => {
		        setMostrarModalBorrar(false); 
		        
		        if (response.ok) {
		            setAlerta({ tipo: 'ok', mensaje: "Reseña borrada correctamente." });
		            cargarResenias();
		            setTimeout(() => setAlerta(null), 3000);
		        } else {
		            setAlerta({ tipo: 'error', mensaje: "Error al borrar reseña." });
		            setTimeout(() => setAlerta(null), 5000);
		        }
		    })
		    .catch(error => {
		        console.error("Error en la petición:", error);
		        setMostrarModalBorrar(false); 
		    });
		};
	const obtenerUsuario = async () => {
	  const token = localStorage.getItem('token'); 

	  try {
	    const response = await fetch(`${API_URL}/obtenerUsuarioToken`, {
	      method: 'GET',
	      headers: {
	        'Content-Type': 'application/json',
	        'Authorization': `Bearer ${token}`
	      }
	    });

	    if (!response.ok) {
	      throw new Error('error al obtener usuario');
	    }

	    const data = await response.json();
	    return data;
	  } catch (error) {
	    console.error('Error:', error);
	    return null;
	  }
	};

	useEffect(() => {


	    fetch(`${API_URL}/juego/${id}`)
	      .then(res => res.json())
	      .then(data => setJuego(data))
	      .catch(err => console.error(err));
	  }, [id]);

	  const cargarResenias = () => {
		fetch(`${API_URL}/reseniasPorJuego?id=${id}`, {
		        cache: 'no-store'
		    })
		  .then(res => res.json())
		  .then(data => setResenias(data))
		  .catch(err => console.error(err));
	  };

	  useEffect(() => {
		cargarResenias();
	  }, [id]);
	  
	  

	  

	  
	  	

	  useEffect(() => {
	  	if (usuarioLogueado) {
	  		obtenerUsuario().then(data => setUsuario(data));
	  	}
	  }, [usuarioLogueado]);

	  useEffect(() => {
	  	if (usuario && resenias.length > 0) {
	  		const propia = resenias.find(
	  			r => r.usuario.mail === usuario.mail
	  		);
	  		setReseniaPropia(propia);
	  	} else {
	  		setReseniaPropia(null);
	  	}
	  }, [usuario, resenias]);

	  const manejarEnvioResenia = (e) => {
	          e.preventDefault();
			  const formData = new FormData(e.target);
			  const token = localStorage.getItem('token');
			  const datosResenia = {
			  		id_juego: id,
			  		titulo: formData.get('titulo'),
			  		puntaje:parseFloat(formData.get('puntaje')),
			  		descripcion:formData.get('descripcion')
			  	}
			  	fetch(`${API_URL}/nuevaResenia`, {
			  	        method: 'POST',
			  	        headers: {
			  	            'Content-Type': 'application/json',
			  	            'Authorization': `Bearer ${token}` 
			  	        },
			  	        body: JSON.stringify(datosResenia)
			  	    })
					
			  		.then(res => {
			  		        if (!res.ok) {
			  		            return res.text().then(text => { throw new Error(text) });
			  		        }
			  		        return res.text();
			  		    })
			  		    .then(mensaje => {
							setAlerta({ tipo: 'ok', mensaje: "Reseña publicada correctamente." });
							e.target.reset();
							setTimeout(() => setAlerta(null), 3000);
			  				cargarResenias();
			  		    })
			  		    .catch(err => {
			  		        setAlerta({ tipo: 'error', mensaje: err.message });
			  		    });
	      };

	  const manejarEdicionResenia = (e) => {
	  		e.preventDefault();
	  		const formData = new FormData(e.target);
	  		const token = localStorage.getItem('token');
	  		const datosResenia = {
	  			id_juego: id,
	  			titulo: formData.get('titulo'),
	  			puntaje: parseFloat(formData.get('puntaje')),
	  			descripcion: formData.get('descripcion')
	  		}
	  		fetch(`${API_URL}/editarResenia`, {
	  			method: 'PUT',
	  			headers: {
	  				'Content-Type': 'application/json',
	  				'Authorization': `Bearer ${token}`
	  			},
	  			body: JSON.stringify(datosResenia)
	  		})
	  			.then(res => {
	  				if (!res.ok) {
	  					return res.text().then(text => { throw new Error(text) });
	  				}
	  				return res.text();
	  			})
	  			.then(mensaje => {
	  				setAlerta({ tipo: 'ok', mensaje: "Reseña editada correctamente." });
	  				setEditando(false); 
	  				cargarResenias();
	  			})
	  			.catch(err => {
	  				setAlerta({ tipo: 'error', mensaje: err.message });
	  			});
	  };

	  if (!juego) return <p>Cargando...</p>;


	  return (
		<section className="paginaJuegoDetalle">
		{mostrarModalBorrar && (
		        <div className="modal-overlay">
		            <div className="modal-content">
		                <h3>Eliminar reseña?</h3>
		                <p>Estas seguro de que queres borrar tu reseña?</p>
		                <div className="modal-acciones">
		                    <button 
		                        className="btn-secundario" 
		                        onClick={() => setMostrarModalBorrar(false)}
		                    >
		                        Cancelar
		                    </button>
		                    <button 
		                        className="btn-primary btn-danger" 
		                        onClick={confirmarBorrado}
		                    >
		                        Eliminar
		                    </button>
		                </div>
		            </div>
		        </div>
		    )}
	    <div className="detalle-juego-container">
		      <header className="game-header">
			  	<div className="gameInfo">
					<div className="gameInfo1">
				        <h1>{juego.name}</h1>
				        <p>{juego.description_raw}</p>
					</div>
			        <span>Desarrolladores: {juego.developers}</span>
				</div>
				<div className="game-pic">
					<img src={juego.background_image}/>
				</div>
		      </header>
			  {alerta && (
			              <div classname="juego-alerta-wrapper">
			                  <AlertMessage 
			                      tipo={alerta.tipo} 
			                      mensaje={alerta.mensaje} 
			                      onClose={() => setAlerta(null)} 
			                  />
			              </div>
			          )}
		      <section className="reviews-section">
			  

		        <h2>Reseñas de usuarios</h2>
	
				
				{!usuarioLogueado && (
				    <div className="mensaje-login-requerido">
				        <p>Necesitas loguearte para reseniar</p>
				        <Link to="/login" className="btn-login">
				            Login
				        </Link>
				    </div>
				)}
	
				{usuarioLogueado && !reseniaPropia && (
				    <div className="review-card">
					
				        <form onSubmit={manejarEnvioResenia} className="formulario-resenia">
				            <div className="form-group">
				                <label>Titulo</label>
				                <input type="text" name="titulo" required />
				            </div>
	
				            <div className="form-group">
				                <label>Puntaje (1-5)</label>
				                <input type="number" name="puntaje" min="1" max="5" required />
				            </div>
	
				            <div className="form-group">
				                <label>Dinos por que</label>
				                <textarea name="descripcion" required></textarea>
				            </div>
	
				            <button type="submit" className="btn-primario">Enviar</button>
				        </form>
						
				    </div>
				)}
	
		        {resenias.map((r,index) => {
					console.log('usuario:', usuario);
					console.log('resenias:', resenias);
		        	const esPropia = usuario && r.usuario?.mail === usuario.mail;
	
		        	
		        	if (esPropia && editando) {
		        		return (
							
		        			<div key={index} className="review-card review-card-propia">
		        				<form onSubmit={manejarEdicionResenia} className="formulario-resenia">
		        					<div className="form-group">
		        						<label>Titulo</label>
		        						<input type="text" name="titulo" defaultValue={r.titulo} required />
		        					</div>
	
		        					<div className="form-group">
		        						<label>Puntaje (1-5)</label>
		        						<input type="number" name="puntaje" min="1" max="5" defaultValue={r.puntaje} required />
		        					</div>
	
		        					<div className="form-group">
		        						<label>Dinos por que</label>
		        						<textarea name="descripcion" defaultValue={r.descripcion} required></textarea>
		        					</div>
	
		        					<div className="acciones-form">
		        						<button type="submit" className="btn-primario">Guardar cambios</button>
		        						<button type="button" className="btn-secundario" onClick={() => setEditando(false)}>Cancelar</button>
		        					</div>
		        				</form>
								
		        			</div>
							
							
		        		);
		        	}
	
		        	
		        	return (
				        <div key={index} className={`review-card ${esPropia ? 'review-card-propia' : ''}`}>
							<div className="review-card-header">
								<div className="reviewUserInfo">
									<img 
								          src={r.usuario.foto_perfil || `https://picsum.photos/200`} 
								          className="user-avatar" 
								        />
										<div className="user-text-info">
										    <strong>{r.usuario?.nombre_usuario}</strong>
										    <span className="user-group">
										        {r.usuario?.nombre_grupo || "Sin grupo"}
										    </span>
										</div>
								</div>

	
								{esPropia && (
									<div className="botones-accion-resenia">
									<button className="btn-editar" onClick={() => setEditando(true)}>
										Editar
									</button>
									<button 
									        className="btn-primary btn-danger" 
									        onClick={() => handleBorrarResenia()}
									    >
									        Borrar
									    </button>
									</div>
								)}
							</div>
						<h3 className="titulo-resenia">{r.titulo}</h3>
			            <p>{r.descripcion}</p>
			            <span className="puntaje-badge">Puntaje: {r.puntaje}/5</span>
						
						
			          </div>
			        ); })}
		        
		      </section>
			  </div>
			  
			  
			  	<FooterC/>
			  
		</section>
	    
	  );
}

export default JuegoResenia;