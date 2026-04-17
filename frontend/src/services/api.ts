import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

export const reservasApi = {
  crearReserva: async (habitacionId: number, nombreCliente: string, horaInicio: string) => {
    const response = await axios.post(`${API_URL}/reservas`, null, {
      params: {
        habitacionId,
        nombreCliente,
        horaInicio
      }
    });
    return response.data;
  },
  checkIn: async (reservaId: number) => {
    const response = await axios.put(`${API_URL}/reservas/${reservaId}/checkin`);
    return response.data;
  },
  checkOut: async (reservaId: number) => {
    const response = await axios.put(`${API_URL}/reservas/${reservaId}/checkout`);
    return response.data;
  },
  finalizarLimpieza: async (habitacionId: number) => {
    const response = await axios.put(`${API_URL}/habitaciones/${habitacionId}/finalizar-limpieza`);
    return response.data;
  },
  getHabitacion: async (habitacionId: number) => {
    const response = await axios.get(`${API_URL}/habitaciones/${habitacionId}`);
    return response.data;
  },
  procesarNoShows: async () => {
    const response = await axios.post(`${API_URL}/reservas/procesar-no-shows`);
    return response.data;
  }
};
