import './Footer.css';

import { Link } from 'react-router-dom';


export default function Footer() {
	


  return (
	<footer className="footer">
				  
			<div className="foot-logo">Gamerboxd</div>
			<div className="foot-secciones">
			<div className="foot-seccion">
			<h2 className="foot-title">Navegacion</h2>
			<ul className="foot-links">
				<li>
				<Link to="/">Inicio</Link>
				</li>
				<li>
					<Link to="/Juegos">Juegos</Link>
				</li>
				<li>
					<Link to="/ListadoGrupos">Comunidad</Link>
				</li>

			</ul>
			</div>
			
				<div className="foot-seccion">
				<h2 className="foot-title">Proyecto</h2>
						<ul className="foot-links">
							<li>
							<a href="https://github.com/utnfrrojava" target="_blank">Materia</a>
							</li>
							<li>
								<a href="https://github.com/juabn/gamerboxd_Java_TP" target="_blank">GitHub</a>
							</li>
							
						</ul>
			</div> 
			</div>
			
			
	</footer>
  );
}