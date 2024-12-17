import { defineConfig } from 'vite'
import solid from 'vite-plugin-solid'

export default defineConfig({
  plugins: [solid()],
  server: {
    port: 5173,
    proxy: {
      'api': {
        target: 'http://localhost:8080',
        changeOrigin:true
      },
    },
  },
  build: {
    target: 'esnext',
  }
})
