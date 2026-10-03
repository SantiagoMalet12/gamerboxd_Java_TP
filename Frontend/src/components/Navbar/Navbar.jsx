import { Link, useNavigate } from 'react-router-dom';
import './Navbar.css';
import { useState, useEffect, useRef } from 'react';

import { API_URL } from '../../config';

export default function Navbar({ autenticado }) {
    const [rolGrupo, setrolGrupo] = useState("");
    const [imagen, setimagen] = useState("");
    const [rol, setrol] = useState("");
    const [menuGrupoAbierto, setMenuGrupoAbierto] = useState(false);
    const navigate = useNavigate();
    const comunidadRef = useRef(null);

    let tokenActual = localStorage.getItem('token');

    
	fetch(`${API_URL}/fotousuario`, {
	    method: 'POST',
	    headers: {
	        'Content-Type': 'application/json',
	        'Authorization': 'Bearer ' + tokenActual
	    },
	    body: JSON.stringify({})
	})
	    .then(response => response.json())
	    .then(data => {
	        setimagen(data.foto_perfil);
	    })
	    .catch(error => console.error('Error al obtener foto:', error));
	
	
    useEffect(() => {
        if (!tokenActual) {
            console.log("no estas logeado");
            return;
        }

        
        fetch(`${API_URL}/verificarjwt`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + tokenActual
            },
            body: JSON.stringify({})
        })
            .then(response => response.json())
            .then(data => {
                setrol(data);
            })
            .catch(error => console.error('Error al verificar JWT:', error));

        

    }, [tokenActual]);

    
    useEffect(() => {
        function manejarClickAfuera(e) {
            if (comunidadRef.current && !comunidadRef.current.contains(e.target)) {
                setMenuGrupoAbierto(false);
            }
        }

        document.addEventListener('mousedown', manejarClickAfuera);
        return () => document.removeEventListener('mousedown', manejarClickAfuera);
    }, []);

    const handleLogout = () => {
        localStorage.removeItem('token');
        window.location.href = '/login';
    };

    const handleComunidad = async () => {
        
        if (menuGrupoAbierto) {
            setMenuGrupoAbierto(false);
            return;
        }

        try {
            const response = await fetch(`${API_URL}/rolengrupo`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + tokenActual
                },
                body: JSON.stringify({})
            });

            if (!response.ok) {
                console.error("Error en la respuesta del servidor");
                return;
            }

            const data = await response.json();
            const rolObtenido = data.rolgrupo;
            setrolGrupo(rolObtenido);

            if (rolObtenido === "admin" || rolObtenido === "miembro") {
                navigate("/PaginaGrupo", { state: { rol: rolObtenido } });
            } else {
                setMenuGrupoAbierto(true);
            }
        } catch (error) {
            console.error('Error al procesar comunidad:', error);
        }
    };

    const irACrearGrupo = () => {
        setMenuGrupoAbierto(false);
        navigate("/crearGrupo");
    };

    const irAGruposExistentes = () => {
        setMenuGrupoAbierto(false);
        navigate("/ListadoGrupos");
    };

    const renderPanelDerecho = () => {
        if (!autenticado) {
            return (
                <Link to="/login">
                    <button className="btnlogin">Login</button>
                </Link>
            );
        }

        return (
            <div className='usuariologeado'>
                <button className="btnlogin" onClick={handleLogout}>
                    Cerrar Sesión
                </button>
                <div className="avatar-perfil">
                    <img 
                        src={imagen || null} 
                        onClick={() => navigate('/Modificarperfil')} 
                        alt="Perfil" 
                        className="avatar-img" 
                    />
                </div>
            </div>
        );
    };

    return (
        <nav>
            <div className="nav-group">
                <div className="logo">Gamerboxd</div>
                <ul className="nav-links">
                    <li><Link to="/">Inicio</Link></li>
                    <li><Link to="Juegos">Juegos</Link></li>

                    {autenticado && rol === "usuario" && (
                    <>
                        <li className="nav-comunidad" ref={comunidadRef}>
                            <span onClick={handleComunidad} style={{ cursor: 'pointer' }}>
                                Comunidad
                            </span>

                            {menuGrupoAbierto && (
                                <div className="comunidad-dropdown">
                                <button className="menugrupo-item" onClick={irACrearGrupo}>
                                    Crear grupo
                                </button>
                                <button className="menugrupo-item" onClick={irAGruposExistentes}>
                                    Ver grupos existentes
                                </button>
                                </div>
                            )}
                        </li>
                        <li><Link to="/CrearJuegos">Agregar juego</Link></li>
                    </>
                    )}

                    {autenticado && rol === "administrador" && (
                        <>
                            <li><Link to="/AdministrarCompanias">Admninistrar companias</Link></li>
                            <li><Link to="/MenuPropuestas">Administrar juegos</Link></li>
                            <li><Link to="/CreacionAdmin">Crear administrador</Link></li>
                        </>
                    )}
                </ul>
            </div>

            <div className="divloginnavbar">
                {renderPanelDerecho()}
            </div>
        </nav>
    );
}
