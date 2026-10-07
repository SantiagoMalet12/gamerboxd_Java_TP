import { useState } from 'react';
import { useNavigate } from "react-router-dom"; // Se recomienda usar react-router-dom
import './crearGrupo.css'
import '../../styles.css'
import FooterC from '../../components/Footer/Footer'
import AlertMessage from '../../components/AlertMessage/AlertMessage'; // Importamos el componente
import { API_URL } from '../../config';

export default function CrearGrupo(){
	const token = localStorage.getItem('token');
	const navigate = useNavigate();
	
	const [formData, setFormData] = useState({
	    nombre: '',
	    descripcion: ''
	});
	  
	const [imagen, setimagen] = useState("");
	const [fotoPreview, setFotoPreview] = useState(null);
	const [alerta, setAlerta] = useState(null);
	  
	const insertarimagen = (e) => {
		const file = e.target.files[0];
		    
		if (file) {
		    setFotoPreview(URL.createObjectURL(file));
		       
		    let reader = new FileReader();
		    reader.readAsDataURL(file);
		    reader.onload = () => {
		        setimagen(reader.result);
		    };
		}
	}

	const handleChange = (e) => {
	    const { name, value } = e.target;
	    setFormData({
	      ...formData,
	      [name]: value
	    });
	};

	const handleSubmit = async (e) => {
	    e.preventDefault();

	    try {
	        const response = await fetch(`${API_URL}/creargrupo`, {
	            method: 'POST',
	            headers: {
	                'Content-Type': 'application/json',
			        'Authorization': token ? `Bearer ${token}` : ''
	            },
	            body: JSON.stringify({
	                nombre: formData.nombre,
	                descripcion: formData.descripcion,
			        foto_perfil: imagen
	            }),
	        });

	        if (response.ok) {
	            setFormData({ nombre: '', descripcion: '' });
			    setFotoPreview(null);
			    setAlerta({ tipo: 'ok', mensaje: 'Grupo creado exitosamente.' });
			    setTimeout(() => {
			        navigate("/");
			    }, 4000);
			    
	        } else {
			    const errorData = await response.json();
			    console.log(errorData);
			    setAlerta({ tipo: 'error', mensaje: 'Error al crear el grupo.' });
			    setTimeout(() => setAlerta(null), 4000);
	        }
	    } catch (error) {
	        console.error('fallo', error);
	        setAlerta({ tipo: 'error', mensaje: 'Error de conexión con el servidor.' });
	        setTimeout(() => setAlerta(null), 4000);
	    }
	};
		
	return (
		<section className="crearGrupoBody">
		{alerta !== null && (
								      <div style={{ margin: '5rem auto -4rem auto', width: '30%' }}>
								          <AlertMessage 
								              tipo={alerta.tipo} 
								              mensaje={alerta.mensaje} 
								              onClose={() => setAlerta(null)} 
								          />
								      </div>
								  )}
			<header className="crearGrupoHeader">
		    <div className="form-container">
		      <h2 className="form-title">Creacion de grupo</h2>
		      
		      <form onSubmit={handleSubmit} className="gaming-form">
			  	<div className="avatar-upload-container">
			            <label htmlFor="foto" className="avatar-preview-circle">
			              {fotoPreview ? (
			                <img src={fotoPreview} className="avatar-image" alt="Preview"/>
			              ) : (
			                <div className="avatar-placeholder">
			                  <svg viewBox="0 0 24 24" fill="currentColor">
			                    <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z" />
			                  </svg>
			                </div>
			              )}
			            </label>
			            <input 
			              type="file" 
			              id="foto" 
			              name="foto" 
			              accept="image/png, image/jpeg, image/webp" 
			              onChange={insertarimagen} 
			              className="hidden-file-input"
			            />
			            <span className="avatar-hint">Agregar foto</span>
			          </div>
	
		        <div className="input-group">
		          <label htmlFor="nombre">Nombre</label>
		          <input 
		            type="text" 
		            id="nombre" 
		            name="nombre" 
		            placeholder="PC Gamers" 
		            value={formData.nombre}
		            onChange={handleChange} 
		            required 
		          />
		        </div>
	
		        <div className="input-group">
		          <label htmlFor="descripcion" >Descripcion</label>
		          <textarea 
		            id="descripcion" 
		            name="descripcion" 
		            rows="5" 
		            placeholder="habla sobre tu grupo" 
		            value={formData.descripcion}
		            onChange={handleChange} 
		            required 
		          />
		        </div>
	
		        <button type="submit" className="submit-btn">Crear Grupo</button>
		      </form>
			
			  
			  
			
		    </div>
			
			</header>
		
			<section className="footer">
				<FooterC/>
			</section>
			
		</section>
	);
};