import { useState } from 'react';
import { useNavigate } from "react-router-dom";
import './Register.css'
import { API_URL } from '../../config';
import Footer from '../../components/Footer/Footer';
import AlertMessage from '../../components/AlertMessage/AlertMessage';

function Registro(){
	
	const [nombre, setnombre] = useState("");
	const [mail, setmail] = useState("");
	const [contrasenia, setcontrasenia] = useState("");
	const [imagen, setimagen] = useState("");
    
    // Estado para la alerta personalizada
    const [alerta, setAlerta] = useState(null);

	const navigate = useNavigate();
	
	const volver = () => {
		navigate("/login")
	}
	
	const enviar = async (e) => {
		e.preventDefault();
		
		try {
			const res = await fetch(`${API_URL}/registro`, {
				method: "POST", 
				body: JSON.stringify({
                    nombre_usuario: nombre,
                    mail: mail,
                    contrasenia: contrasenia,
                    foto_perfil : imagen
				}),
				headers: {
				    "Content-type": "application/json",
				},
			});
			
			if (res.status === 200) {
                setAlerta({ tipo: 'ok', mensaje: "¡Usuario registrado con éxito!" });
				console.log(res.status);
				
                setTimeout(() => {
                    navigate("/login");
                }, 2000);
				
			} else if (res.status === 409) {
                setAlerta({ tipo: 'error', mensaje: "Ya existe un usuario con ese correo o nombre." });
                setTimeout(() => setAlerta(null), 5000);
			} else {
                setAlerta({ tipo: 'error', mensaje: "Ocurrió un error al registrar el usuario." });
                setTimeout(() => setAlerta(null), 5000);
            }
			
		} catch {
            setAlerta({ tipo: 'error', mensaje: "Error en la conexión con la base de datos, intente más tarde." });
            setTimeout(() => setAlerta(null), 5000);
        }
	}
			
	const insertarnombre = (e) => setnombre(e.target.value);
	const insertarcontrasenia = (e) => setcontrasenia(e.target.value);
	const insertarmail = (e) => setmail(e.target.value);
		
	const insertarimagen = (e) => {
        if (e.target.files && e.target.files[0]) {
            let reader = new FileReader();
            reader.readAsDataURL(e.target.files[0]);
            reader.onload = () => {
                setimagen(reader.result);
            }
        }
	}

	return(	
		<section className="pagina-registro">
            
            
            
            <div className="registro-container">
                <div className="registro-card-glass">
                    
                    <div className="registro-header">
                        <h2 className="registro-titulo">Crear cuenta</h2>
                        <p className="registro-subtitulo">Unite a Gamerboxd y empeza a opinar.</p>
                    </div>

                    <form onSubmit={enviar} className='registro-form'>
                        
                        <div className="registro-campo">
                            <label className="registro-label">Correo electronico</label>
                            <input 
                                className="registro-input"
                                type="email"
                                onChange={insertarmail} 
                                placeholder="Ingrese su mail"
                                value={mail}
                                required
                            />
                        </div>
                        
                        <div className="registro-campo">
                            <label className="registro-label">Nombre de usuario</label>
                            <input 
                                className="registro-input"
                                placeholder="Ingrese su nombre"
                                onChange={insertarnombre} 
                                value={nombre}
                                required
                            />
                        </div>

                        <div className="registro-campo">
                            <label className="registro-label">Contraseña</label>
                            <input 
                                className="registro-input"
                                type="password"
                                placeholder="Cree una contraseña"
                                onChange={insertarcontrasenia} 
                                value={contrasenia}
                                required
                            />
                        </div>
                        
                        <div className="registro-campo">
                            <label className="registro-label">Foto de perfil</label>
                            <div className="registro-imagen-row">
                                <div className="registro-imagen-preview">
                                    {imagen ? (
                                        <img className="registro-imagen" src={imagen} alt="Preview" />
                                    ) : (
                                        <span className="registro-imagen-placeholder">Sin imagen</span>
                                    )}
                                </div>
                                
                                <label className="registro-file-btn">
                                    Elegir imagen
                                    <input 
                                        className="registro-file-input"
                                        type="file" 
                                        accept="image/*"
                                        onChange={insertarimagen}	
                                    />
                                </label>
                            </div>
                        </div>

                        <div className="registro-acciones">
                            <button className="btn-primario" type="submit">Registrarse</button>
                            <button className="btn-secundario" type="button" onClick={volver}>Volver</button>
							
                        </div>

                    </form>
					{alerta !== null && (
					                <div className="registro-alerta-wrapper">
					                    <AlertMessage 
					                        tipo={alerta.tipo} 
					                        mensaje={alerta.mensaje} 
					                        onClose={() => setAlerta(null)} 
					                    />
					                </div>
					            )}

					
                </div>
            </div>

            <Footer />
        </section>
	)
}

export default Registro;