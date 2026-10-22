import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'
import BackendConnection from './components/BackendConnection.jsx'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <BackendConnection>
      <App />
    </BackendConnection>
  </StrictMode>,
)

