import FormularioReserva from './components/FormularioReserva'
import DashboardRecepcion from './components/DashboardRecepcion'
import './App.css'

function App() {
  return (
    <div className="App" style={{ minHeight: '100vh', padding: '20px', background: '#121212', color: '#fff' }}>
      <header style={{ textAlign: 'center', marginBottom: '3rem' }}>
        <h1 style={{ fontSize: '2.5rem', fontWeight: 'bold' }}>Sistema de Reservas - Residenciales</h1>
        <p style={{ color: '#888' }}>Iteración 2: Gestión de Recepción y Cola FIFO</p>
      </header>
      <main style={{ display: 'flex', flexWrap: 'wrap', justifyContent: 'center', gap: '2rem' }}>
        <FormularioReserva />
        <DashboardRecepcion />
      </main>
    </div>
  )
}

export default App
