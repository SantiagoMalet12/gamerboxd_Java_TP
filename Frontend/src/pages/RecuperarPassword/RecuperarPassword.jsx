import { useNavigate } from "react-router-dom";
import { useState } from 'react';
import { API_URL } from '../../config';
import Footer from '../../components/Footer/Footer';
import AlertMessage from '../../components/AlertMessage/AlertMessage';
import './RecuperarPassword.css';

function Recuperarpassword() {
	
	const [nombremail, setmail] = useState("");
	const [alerta, setAlerta] = useState(null);
	const navigate = useNavigate();

	const enviado = async(e) => {
		e.preventDefault();
		
		try {
			const res = await fetch(`${API_URL}/recuperarpassword`, {
				method: "POST",
				body: JSON.stringify({
					mail: nombremail
				}),
				headers: {
					"Content-type": "application/json",
				},
			});
			
			localStorage.setItem("mail", nombremail);
			
			if (res.ok) {
				
				setAlerta({ tipo: 'ok', mensaje: "Enviando codigo de verificacion al mail." });
				setTimeout(() => {
					ingresotoken();
				}, 2000);
			} else if (res.status === 401) {
				const data = await res.text();
				setAlerta({ tipo: 'error', mensaje: data });
				setTimeout(() => setAlerta(null), 5000);
			} else {
                setAlerta({ tipo: 'error', mensaje: "Ocurrió un error inesperado." });
                setTimeout(() => setAlerta(null), 5000);
            }
		} catch {
			setAlerta({ tipo: 'error', mensaje: "Error en la conexión con la base de datos" });
			setTimeout(() => setAlerta(null), 5000);
		}
	};
	
	const volver = () => {
		navigate('/login');
	};
	
	const ingresotoken = () => {
		navigate('/ingresoToken');
	};
	
	const cargarmail = (e) => {
		setmail(e.target.value);
	};

	return (
		<section className="pagina-recuperar">
			<div className="recuperar-container">
				<div className="recuperar-card-glass">
					
					<div className="recuperar-header">
						<h2 className="recuperar-titulo">Recuperar password</h2>
						<p className="recuperar-subtitulo">Ingrese su mail para recibir el código</p>
					</div>

					<form className="recuperar-form" onSubmit={enviado}>
						
						<div className="recuperar-campo">
							<label className="recuperar-label">Correo electrónico</label>
							<input 
								className="recuperar-input"
								placeholder="Ingrese su mail"
								onChange={cargarmail}
								type="email"
								autoComplete="email"
								name="email"
								value={nombremail}
								required
							/>
						</div>

						<div className="recuperar-acciones">
							<button className="btn-primario" type="submit">Enviar</button>
						</div>
					</form>

					<div className="recuperar-links">
						<p onClick={volver} className="link-aviso">Volver al inicio de sesión</p>
					</div>
				</div>
				
			</div>
			{alerta !== null && (
							<div className="recuperar-alerta-wrapper">
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

export default Recuperarpassword;
