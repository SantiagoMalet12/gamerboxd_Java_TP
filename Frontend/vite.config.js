import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  optimizeDeps: {
    include: ['react', 'react/jsx-runtime', 'react-dom', 'swiper/react']
  },
  build: {
    cssMinify: false
  }
})