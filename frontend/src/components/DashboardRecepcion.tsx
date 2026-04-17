import React, { useState, useEffect } from 'react';
import { reservasApi } from '../services/api';

const DashboardRecepcion: React.FC = () => {
    const [reservaId, setReservaId] = useState<string>('');
    const [habitacionId] = useState<string>('1');
    const [habitacionData, setHabitacionData] = useState<any>(null);
    const [statusMessage, setStatusMessage] = useState<string>('');
    const [loading, setLoading] = useState<boolean>(false);

    const refreshHabitacion = async () => {
        try {
            const data = await reservasApi.getHabitacion(parseInt(habitacionId));
            setHabitacionData(data);
        } catch (err) {
            console.error("Error al refrescar habitación", err);
        }
    };

    useEffect(() => {
        refreshHabitacion();
    }, [habitacionId]);

    const handleAction = async (action: 'checkin' | 'checkout' | 'limpieza') => {
        setLoading(true);
        setStatusMessage('');
        try {
            let res;
            if (action === 'checkin') {
                res = await reservasApi.checkIn(parseInt(reservaId));
                setStatusMessage(`Check-in exitoso para Reserva #${res.id}`);
            } else if (action === 'checkout') {
                res = await reservasApi.checkOut(parseInt(reservaId));
                setStatusMessage(`Check-out exitoso. Habitación ${res.habitacion.numero} en LIMPIEZA.`);
            } else {
                res = await reservasApi.finalizarLimpieza(parseInt(habitacionId));
                setStatusMessage(`Limpieza finalizada. Habitación ${res.numero} ahora ${res.estado}.`);
            }
            await refreshHabitacion();
        } catch (err: any) {
            setStatusMessage(`Error: ${err.response?.data?.message || err.message}`);
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
            boxShadow: '0 8px 32px 0 rgba(0, 0, 0, 0.37)',
            color: '#fff'
        }}>
            <h2 style={{ textAlign: 'center', marginBottom: '1.5rem' }}>Panel de Recepción</h2>
            
            <div style={{ marginBottom: '1.5rem', padding: '1rem', background: '#222', borderRadius: '8px', borderLeft: '4px solid #2196F3' }}>
                <h4 style={{ margin: 0 }}>Estado Habitación 101</h4>
                {habitacionData ? (
                    <p style={{ fontSize: '1.2rem', margin: '0.5rem 0' }}>
                        Estado: <span style={{ color: habitacionData.estado === 'DISPONIBLE' ? '#4CAF50' : '#FF9800' }}>
                            {habitacionData.estado}
                        </span>
                    </p>
                ) : <p>Cargando...</p>}
                <button onClick={refreshHabitacion} style={{ fontSize: '0.7rem' }}>Refrescar</button>
            </div>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                <label>
                    ID Reserva:
                    <input
                        type="number"
                        value={reservaId}
                        onChange={(e) => setReservaId(e.target.value)}
                        placeholder="ID"
                        style={{ width: '100%', padding: '0.5rem', background: '#333', color: '#fff', border: '1px solid #555' }}
                    />
                </label>

                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.5rem' }}>
                    <button onClick={() => handleAction('checkin')} disabled={loading || !reservaId} style={{ padding: '0.8rem', background: '#4CAF50', color: 'white', border: 'none', cursor: 'pointer' }}>
                        Check-In
                    </button>
                    <button onClick={() => handleAction('checkout')} disabled={loading || !reservaId} style={{ padding: '0.8rem', background: '#F44336', color: 'white', border: 'none', cursor: 'pointer' }}>
                        Check-Out
                    </button>
                </div>

                <div style={{ marginTop: '1rem', borderTop: '1px solid #444', paddingTop: '1rem' }}>
                    <button onClick={() => handleAction('limpieza')} disabled={loading} style={{ width: '100%', padding: '0.8rem', background: '#9C27B0', color: 'white', border: 'none', cursor: 'pointer', marginBottom: '1rem' }}>
                        Finalizar Limpieza
                    </button>

                    <button 
                        onClick={async () => {
                            setLoading(true);
                            try {
                                const msg = await reservasApi.procesarNoShows();
                                setStatusMessage(msg);
                                await refreshHabitacion();
                            } catch (err: any) {
                                setStatusMessage(`Error: ${err.message}`);
                            } finally {
                                setLoading(false);
                            }
                        }} 
                        disabled={loading} 
                        style={{ width: '100%', padding: '0.8rem', background: 'linear-gradient(45deg, #F44336 30%, #FF5252 90%)', color: 'white', border: 'none', cursor: 'pointer', fontWeight: 'bold' }}>
                        Forzar Sweep No-Show (Simulador)
                    </button>
                </div>
            </div>

            {statusMessage && (
                <div style={{ marginTop: '1rem', padding: '0.8rem', background: '#333', borderRadius: '4px', textAlign: 'center' }}>
                    {statusMessage}
                </div>
            )}
        </div>
    );
};

export default DashboardRecepcion;
