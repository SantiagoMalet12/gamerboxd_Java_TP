import { useNavigate } from "react-router-dom"; 
import { useState } from 'react';
import { API_URL } from '../../config';
import Footer from '../../components/Footer/Footer';
import AlertMessage from '../../components/AlertMessage/AlertMessage';
import './ingresoToken.css';

function IngresarToken(){
	
	const navigate = useNavigate();
	const [token, setToken] = useState("");
	const [alerta, setAlerta] = useState(null);
	
	const mail1 = localStorage.getItem("mail");
	
	const volver = () => {
		navigate('/login');
	}
	
	const enviar = async(e) => {
		e.preventDefault();
		
		try {
			const res = await fetch (`${API_URL}/verificartoken`, {
				method: "POST",
				body: JSON.stringify({
					token: token,
					mail : mail1
				}),
				headers:{
					"Content-type": "application/json",
				},
			});
			
			if(res.ok){
				avanzar();
			} else if(res.status === 401){
				setAlerta({ tipo: 'error', mensaje: "Token incorrecto o expirado" });
                setTimeout(() => setAlerta(null), 5000);
			} else {
                setAlerta({ tipo: 'error', mensaje: "Ocurrió un error al verificar el token" });
                setTimeout(() => setAlerta(null), 5000);
            }
		}
		catch {
            setAlerta({ tipo: 'error', mensaje: "Error en la conexión con la base de datos, intente nuevamente" });
            setTimeout(() => {
                setAlerta(null);
                navigate("/login");
            }, 5000);
		}
	}
	
	const actualizarvalor = (e) => {
		setToken(e.target.value);
	}
	
	const avanzar = () => {
		navigate('/ingresarnuevapassword');
	}
	
	return(
        <section className="pagina-login">
            <div className="login-container">
                
                <div className="login-card-glass">
                    <div className="login-header">
                        <h2 className="login-titulo">Verificar Token</h2>
                        <p className="login-subtitulo">Revisa tu correo para obtener el código</p>
                    </div>

                    <form className='login-form' onSubmit={enviar}>
                        <div className="login-campo">
                            <label className="login-label">Token de seguridad</label>
                            <input 
                                className='login-input'
                                placeholder="Ingrese el token"
                                onChange={actualizarvalor}
                                value={token}
                                required 
                            />
                        </div>
                        
                        <div className="login-acciones">
                            <button type="submit" className='btn-primario'>Enviar</button>
                        </div>
                    </form>

                    <div className="login-links">
                        <p onClick={volver} className='link-aviso'>Volver al inicio de sesión</p>
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
                
            </div>
            
            <Footer />
        </section>
	);
}

export default IngresarToken;