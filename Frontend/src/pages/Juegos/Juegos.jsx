import './Juegos.css'
import Select from 'react-select'

import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom'

import GameCard from '../../components/GameCard/GameCard'

import { API_URL } from '../../config';
import Footer from '../../components/Footer/Footer'

function Juegos(){
	
	const [listaJuegos, setListaJuegos] = useState([]);
	const [listaEmpresas, setListaEmpresas] = useState([]);
	const [cargando, setCargando] = useState(true);
	
	useEffect(() => {
	  fetch(`${API_URL}/listaempresas`)
	    .then((Response) => Response.json())
	    .then((dataa) => {
		  const nuevoarray = dataa.map(empresa => ({
		  	value: empresa.id,
		  	label: empresa.name
		  }));
		  const opcionPorDefecto = { value: '', label: 'Todos' };
		  setListaEmpresas([opcionPorDefecto, ...nuevoarray]);
	    })
	    .catch((error) => console.error("Error cargando empresas:", error));
	}, []);
	
	useEffect(() =>{
		fetch(`${API_URL}/listajuegos`)
		.then((response) => response.json())
		.then((data) => {
		        setListaJuegos(data);
		        setCargando(false); 
		    })
		.catch((error) => {
            console.error("Error cargando juegos:", error);
            setCargando(false); 
        });
	}, [])
	
	const [companiaelegida, seleccionarcompaniaelegida] = useState('Todos');
	
	const manejarCambioOpcion = (event) => {
		seleccionarcompaniaelegida(event.label);
	}
	
	
	const [inputTexto, setInputTexto] = useState(''); 
	const [busqueda, setBusqueda] = useState('');    

	const manejarCambioBusqueda = (event) => {
	    setInputTexto(event.target.value);
	};
	
// aca retraso la busqueda
	useEffect(() => {
        const temporizador = setTimeout(() => {
            setBusqueda(inputTexto);
        }, 500); 
        return () => clearTimeout(temporizador);
    }, [inputTexto]);

	const array_filtro = listaJuegos.filter(juego => {
        const cumpleCompania = (companiaelegida === "Todos") || 
                               (juego.developers && juego.developers.includes(companiaelegida));
        
        const nombreJuego = juego.name ? juego.name.toLowerCase() : "";
        const cumpleInput = (busqueda.trim() === "") || 
                            (nombreJuego.includes(busqueda.toLowerCase()));

        return cumpleCompania && cumpleInput;
    });
	
	return(
		<section>
		<div className='juegosbody'>
			<div>
			    <p className="titulo"> ¡Elige tu juego a reseñar! </p>
			</div>
			
			<div className="filtros">
			    <h3 className='texto'>Buscar juego</h3>
			    <input value={inputTexto} onChange={manejarCambioBusqueda} placeholder="Escribe para buscar..." />
			
			    <div className="select">
			        <h3 className='texto'>Filtrar por compañia</h3>
			        <Select
			            defaultValue={{ value: 1, label: 'Todos' }}
			            options={listaEmpresas}
			            onChange={manejarCambioOpcion}
			        />
			    </div>
			  
                {cargando ? (
                    <div className="cargando"style={{ padding: '50px', textAlign: 'center', color: '#b0b0c0', transition:'2s' }}>
                        <h2>Cargando juegos, por favor espera...</h2>
                    </div>
                ) : (
                    <div className="catalogo-juegos cargando">
                        {array_filtro.map((juego) => (
                            <Link key={juego.id} to={`/juego/${juego.id}`}>
                                <GameCard         
                                    titulo={juego.name}   
                                    imagen={juego.background_image}   
                                />
                            </Link>
                        ))}
                    </div>
                )}
			</div>
		</div>
		<Footer/>
		</section>
	)	
}

export default Juegos;