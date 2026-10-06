import { useState } from 'react';
import { useNavigate } from "react-router-dom"; 
import { API_URL } from '../../config';
import Footer from '../../components/Footer/Footer';
import AlertMessage from '../../components/AlertMessage/AlertMessage';
import './Login.css'

function Login({ setAuth }){
	
	const [nombre, setnombre] = useState("");
	const [contrasenia, setcontrasenia] = useState("");
    
    
    const [alerta, setAlerta] = useState(null);

	const navigate = useNavigate();
	
	const registro_navigate = () => {
		navigate('/registro');
	}
	
	const recuperarpassword_navigate = () => {
		navigate('/recuperarpassword')
	}

	const enviado = async(e) => { 
		e.preventDefault();
		
		try{
            const res = await fetch (`${API_URL}/login`, {
                method: "POST", 
                body: JSON.stringify({
                    mail: nombre,
                    contrasenia: contrasenia
                }),
                headers: {
                    "Content-type": "application/json",
                },
            });
		
            if(res.ok){
                const data = await res.json();	
                localStorage.setItem('token', data.token);
                
                setAlerta({ tipo: 'ok', mensaje: "¡Inicio de sesión exitoso!" });
                
                setTimeout(() => {
                    setAuth(true);
                    navigate('/');
                }, 1500);
            } else if(res.status === 401){
                setAlerta({ tipo: 'error', mensaje: "Mail no encontrado o credenciales incorrectas" });
                setTimeout(() => setAlerta(null), 5000);
            } else {
                setAlerta({ tipo: 'error', mensaje: "Ocurrió un error al intentar iniciar sesión." });
                setTimeout(() => setAlerta(null), 5000);
            }
		
		} catch {
            setAlerta({ tipo: 'error', mensaje: "Error en la conexión con la base de datos" });
            setTimeout(() => setAlerta(null), 5000);
        }
	}
	
	const manejarusuario = (e) => {
		setnombre(e.target.value);
	}
	
	const manejarpassword = (e) => {
		setcontrasenia(e.target.value);
	}
		
	return(
		<section className="pagina-login">
            
            

            <div className="login-container">
                <div className="login-card-glass">
                    
                    <div className="login-header">
                        <h2 className="login-titulo">Bienvenido</h2>
                        <p className="login-subtitulo">Inicia sesion en Gamerboxd</p>
                    </div>

                    <form className='login-form' onSubmit={enviado}>
                        
                        <div className="login-campo">
                            <label className="login-label">Correo electronico</label>
                            <input  
                                className='login-input' 
                                type="email" 
                                placeholder='Ingrese su mail'
                                onChange={manejarusuario}
                                value={nombre}
                                required
                            />
                        </div>

                        <div className="login-campo">
                            <label className="login-label">Contraseña</label>
                            <input  
                                className='login-input' 
                                type="password" 
                                placeholder='Ingrese su contraseña' 
                                onChange={manejarpassword}
                                value={contrasenia}
                                required
                            />
                        </div>				
                        
                        <div className="login-acciones">
                            <button className='btn-primario' type="submit">Ingresar</button>
                        </div>
                    </form>

                    <div className="login-links">
                        <p onClick={registro_navigate} className='link-aviso'>¿No tienes cuenta? Registrate aca</p>
                        <p onClick={recuperarpassword_navigate} className='link-aviso'>¿Olvidaste tu contraseña?</p>		
                    </div>
                </div>		
				
            </div>
			{alerta !== null && (
										                <div className="login-alerta-wrapper">
										                    <AlertMessage 
										                        tipo={alerta.tipo} 
										                        mensaje={alerta.mensaje} 
										                        onClose={() => setAlerta(null)} 
										                    />
										                </div>
										            )}
			
            
            <Footer />
		</section>
	);
}

export default Login;