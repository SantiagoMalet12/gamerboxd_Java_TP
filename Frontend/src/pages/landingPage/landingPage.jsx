import './landingPage.css';
import { Link } from 'react-router-dom';
// eslint-disable-next-line no-unused-vars
import {useState,useEffect} from 'react';
import { register } from 'swiper/element/bundle';
register();
import { API_URL } from '../../config';


const IMAGEN_DEFAULT = ["https://media.rawg.io/media/games/4cf/4cfc6b7f1850590a4634b08bfab308ab.jpg"];


const RESENIAS_DESTACADAS = [
  { id_juego: 28, mail_usuario: "pixelninja@gmail.com" },    
  { id_juego: 3328, mail_usuario: "voidwalker@gmail.com" },   
  { id_juego: 4200, mail_usuario: "retromaniac@gmail.com" },  
  { id_juego: 58175, mail_usuario: "questgiver@gmail.com" },  
  { id_juego: 3498, mail_usuario: "trophyhunter@yahoo.com" }, 
];

import Carrousel from '../../components/carrousel/Carrousel';

import ReviewLandCard from '../../components/reviewLandCard/reviewLandCard';
import FooterC from '../../components/Footer/Footer';
export default function LandingPage() {

	const [juegosImagenes, setJuegosImagenes] = useState([]);

	useEffect(() => {
		    fetch(`${API_URL}/listajuegos`) 
		      .then(respuesta => respuesta.json())
		      .then(datos => {
					const imagenes = datos.map(juego => juego.background_image)
					setJuegosImagenes(imagenes);

				})
		      .catch(error => console.error("err:", error));
		      
		    
		  }, []);
	
	
	
	const [resenas, setResenas] = useState([]);

	useEffect(() => {
		fetch(`${API_URL}/allresenias`) 
			.then(respuesta => respuesta.json())
			.then(datos => {
				
				const elegidas = RESENIAS_DESTACADAS
					.map(dest => datos.find(r => Number(r.id_juego) === dest.id_juego && r.mail_usuario === dest.mail_usuario))
					.filter(Boolean);

				if (elegidas.length > 0) {
					setResenas(elegidas);
				} else {
					
					const juegosVistos = new Set();
					const fallback = datos.filter(r => {
						const id = Number(r.id_juego);
						if (!juegosVistos.has(id) && r.descripcion && r.descripcion.length <= 180 && Number(r.puntaje) >= 4) {
							juegosVistos.add(id);
							return true;
						}
						return false;
					}).slice(0, 5);
					setResenas(fallback);
				}
			})
			.catch(error => console.error("Error conexion:", error));
	}, []);

  return (
	  <section className="landingBody">
	  	
              <div className="headerContainer">
                  <div className='headerDiv'>
                      <h1 className='headerDivTitle'>Reseña tus juegos favoritos</h1>
                      <Link to='/Juegos'>
                          <button className="botonLogin">Pruebalo ahora</button>
                      </Link>
                  </div>
                  <div className="cFilter">
                      <Carrousel 
					  key={juegosImagenes.length}
					      imagenes={juegosImagenes.length > 0 ? juegosImagenes : IMAGEN_DEFAULT} 
					      className="cAnim"
								 />
                  </div>
              </div>
			  <h1 className="highlighterReviewsTitle">
			  			  	Reseñas de usuarios
			  			  </h1>
			  <div className='highlightedReviews'>
			    <swiper-container
				className='swiper-container-highlighted-r' 
				  effect="coverflow"
				  coverflow-effect-rotate="0"     
				    coverflow-effect-stretch="-90"   
				    coverflow-effect-depth="150"     
				    coverflow-effect-modifier="1"    
				    coverflow-effect-slide-shadows="false" 
				    grab-cursor="false" 
				    centered-slides="true"
				    slides-per-view="1" 
				    navigation="true" 
				    loop="true"
			      
			    >
			      {resenas.map((resenia, index) => (
			        <swiper-slide key={index}>
			          <ReviewLandCard 
			            titulo={resenia.titulo} 
			            descripcion={resenia.descripcion} 
			            puntaje={resenia.puntaje}
			            nombreJuego={resenia.juego.name} 
			            nombreUsuario={resenia.usuario.nombre_usuario}
			            fotoJuego={resenia.juego.background_image} 
			          />
			        </swiper-slide>
			      ))}
			    </swiper-container>
			  </div>
			  <section className="footer">
			  	<FooterC/>
			  </section>
          </section>
		  
  );

}
