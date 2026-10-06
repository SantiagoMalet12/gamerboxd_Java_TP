// src/config.js

// Cambiá a false para trabajar en local, o true para producción 
const IS_ONLINE = false; 

export const API_URL = IS_ONLINE 
  ? "https://gamerboxd-java-tp-1.onrender.com" 
  : "http://localhost:8081"