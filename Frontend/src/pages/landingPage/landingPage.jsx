import './landingPage.css';
import { Link } from 'react-router-dom';
import { useState, useEffect } from 'react';
import { register } from 'swiper/element/bundle';
register();
import { API_URL } from '../../config';

const IMAGEN_DEFAULT = ["https://media.rawg.io/media/games/4cf/4cfc6b7f1850590a4634b08bfab308ab.jpg"];
// las hardcodeo en el front para que no tarden en cargar en web
const RESENIAS_DESTACADAS = [
  {
    titulo: "Una obra maestra absoluta",
    descripcion: "La historia de Arthur Morgan te llega al corazon. El mejor mundo abierto y narrativo que probe en mi vida.",
    puntaje: 5.0,
    nombreJuego: "Red Dead Redemption 2",
    nombreUsuario: "PixelNinja",
    fotoJuego: "https://media.rawg.io/media/games/511/5118aff5091cb3efec399c808f8c598f.jpg"
  },
  {
    titulo: "Narrativa y mundo insuperables",
    descripcion: "La profundidad de su historia y misiones secundarias superan ampliamente a cualquier otro RPG. Imprescindible.",
    puntaje: 5.0,
    nombreJuego: "The Witcher 3: Wild Hunt",
    nombreUsuario: "VoidWalker",
    fotoJuego: "https://media.rawg.io/media/games/618/618c2031a07bbff6b4f611f10b6bcdbc.jpg"
  },
  {
    titulo: "Diseno de niveles magistral",
    descripcion: "Puzles ingeniosos, humor brillante y un ritmo perfecto de principio a fin. Una obra cumbre del diseno de juegos.",
    puntaje: 5.0,
    nombreJuego: "Portal 2",
    nombreUsuario: "RetroManiac",
    fotoJuego: "https://media.rawg.io/media/games/2ba/2bac0e87cf45e5b508f227d281c9252a.jpg"
  },
  {
    titulo: "Reinvencion brillante de la saga",
    descripcion: "El combate con el hacha Leviatan es sumamente satisfactorio y la relacion entre Kratos y Atreus es conmovedora.",
    puntaje: 5.0,
    nombreJuego: "God of War (2018)",
    nombreUsuario: "QuestGiver",
    fotoJuego: "https://media.rawg.io/media/games/4be/4be6a6ad0364751a96229c56bf69be59.jpg"
  },
  {
    titulo: "Una cumbre del genero metroidvania",
    descripcion: "Direccion de arte impecable, banda sonora melancolica y una jugabilidad precisa que desafia y recompensa.",
    puntaje: 5.0,
    nombreJuego: "Hollow Knight",
    nombreUsuario: "CyberKnight",
    fotoJuego: "https://media.rawg.io/media/games/4cf/4cfc6b7f1850590a4634b08bfab308ab.jpg"
  },
  {
    titulo: "Diversion y escala inigualables",
    descripcion: "Los Santos se siente un mundo vivo en cada rincon. La dinamica entre los tres personajes es un espectaculo.",
    puntaje: 5.0,
    nombreJuego: "Grand Theft Auto V",
    nombreUsuario: "TrophyHunter",
    fotoJuego: "https://media.rawg.io/media/games/20a/20aa03a10cda45239fe22d035c0ebe64.jpg"
  }
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
				const imagenes = datos.map(juego => juego.background_image);
				setJuegosImagenes(imagenes);
			})
			.catch(error => console.error("err:", error));
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
					{RESENIAS_DESTACADAS.map((resenia, index) => (
						<swiper-slide key={index}>
							<ReviewLandCard 
								titulo={resenia.titulo} 
								descripcion={resenia.descripcion} 
								puntaje={resenia.puntaje}
								nombreJuego={resenia.nombreJuego} 
								nombreUsuario={resenia.nombreUsuario}
								fotoJuego={resenia.fotoJuego} 
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
