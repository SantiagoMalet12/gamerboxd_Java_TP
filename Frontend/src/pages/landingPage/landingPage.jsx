import './landingPage.css';
import { Link } from 'react-router-dom';
// eslint-disable-next-line no-unused-vars
import {useState,useEffect} from 'react';
import { register } from 'swiper/element/bundle';
register();
import { API_URL } from '../../config';


const IMAGEN_DEFAULT = ["https://media.rawg.io/media/games/4cf/4cfc6b7f1850590a4634b08bfab308ab.jpg"];

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
			console.log("Datos crudos del servidor:", datos);
			const juegosVistos = new Set();
			        const resenasJuegosUnicos = datos.filter(resenia => {
			          
			          const id = resenia.id_juego;
					  if (!juegosVistos.has(id)) {
					              juegosVistos.add(id);
					              return true;
					            }
						return false;
						});
						
					
				const resenasMezcladas = resenasJuegosUnicos.sort(()=> 0.5 - Math.random());
				const aleatorias = resenasMezcladas.slice(0, 5);
				
				setResenas(aleatorias);
				
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
			  			  	Reseñas destacadas
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