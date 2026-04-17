import React, { useState } from 'react';
import { reservasApi } from '../services/api';

const FormularioReserva: React.FC = () => {
    const [habitacionId, setHabitacionId] = useState<number>(1);
    const [nombreCliente, setNombreCliente] = useState<string>('');
    const [horaInicio, setHoraInicio] = useState<string>('');
    const [reservaResponse, setReservaResponse] = useState<any>(null);
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState<boolean>(false);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        setError(null);
        setReservaResponse(null);

        try {
            const data = await reservasApi.crearReserva(habitacionId, nombreCliente, horaInicio);
            setReservaResponse(data);
        } catch (err: any) {
            console.error(err);
            setError(err.response?.data?.message || err.message || 'Error al conectar con el servidor. Verifica que el backend esté corriendo y el puerto 8080 esté expuesto.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={{
            maxWidth: '500px',
            margin: '2rem auto',
            padding: '2rem',
            background: 'rgba(255, 255, 255, 0.05)',
            backdropFilter: 'blur(10px)',
            borderRadius: '16px',
            boxShadow: '0 8px 32px 0 rgba(0, 0, 0, 0.37)'
        }}>
            <h2 style={{ color: '#fff', textAlign: 'center', marginBottom: '1.5rem' }}>Nueva Reserva</h2>
            <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                <label style={{ color: '#ccc' }}>
                    ID Habitación:
                    <input
                        type="number"
                        value={habitacionId}
                        onChange={(e) => setHabitacionId(parseInt(e.target.value))}
                        disabled={loading}
                        style={{ width: '100%', padding: '0.5rem', marginTop: '0.2rem', borderRadius: '4px', border: '1px solid #444', background: '#222', color: '#fff' }}
                    />
                </label>
                <label style={{ color: '#ccc' }}>
                    Nombre del Cliente:
                    <input
                        type="text"
                        value={nombreCliente}
                        onChange={(e) => setNombreCliente(e.target.value)}
                        required
                        disabled={loading}
                        placeholder="Ej. Carlos Mesa"
                        style={{ width: '100%', padding: '0.5rem', marginTop: '0.2rem', borderRadius: '4px', border: '1px solid #444', background: '#222', color: '#fff' }}
                    />
                </label>
                <label style={{ color: '#ccc' }}>
                    Hora de Inicio:
                    <input
                        type="datetime-local"
                        value={horaInicio}
                        onChange={(e) => setHoraInicio(e.target.value)}
                        required
                        disabled={loading}
                        style={{ width: '100%', padding: '0.5rem', marginTop: '0.2rem', borderRadius: '4px', border: '1px solid #444', background: '#222', color: '#fff' }}
                    />
                </label>
                <button
                    type="submit"
                    disabled={loading}
                    style={{
                        padding: '0.8rem',
                        marginTop: '1rem',
                        background: loading ? '#444' : 'linear-gradient(45deg, #2196F3 30%, #21CBF3 90%)',
                        color: 'white',
                        border: 'none',
                        borderRadius: '4px',
                        cursor: loading ? 'not-allowed' : 'pointer',
                        fontWeight: 'bold',
                        transition: '0.3s'
                    }}
                >
                    {loading ? 'Procesando...' : 'Crear Reserva'}
                </button>
            </form>

            {error && (
                <div style={{ marginTop: '1.5rem', padding: '1rem', background: 'rgba(244, 67, 54, 0.1)', border: '1px solid red', color: '#ff8a80', borderRadius: '8px' }}>
                    <strong>Error:</strong> {error}
                </div>
            )}

            {reservaResponse && (
                <div style={{ marginTop: '1.5rem' }}>
                    <h3 style={{ color: '#4CAF50' }}>Reserva Exitosa</h3>
                    <pre style={{
                        background: '#111',
                        padding: '1rem',
                        borderRadius: '8px',
                        overflowX: 'auto',
                        fontSize: '0.8rem',
                        color: '#eee'
                    }}>
                        {JSON.stringify(reservaResponse, null, 2)}
                    </pre>
                </div>
            )}
        </div>
    );
};

export default FormularioReserva;
