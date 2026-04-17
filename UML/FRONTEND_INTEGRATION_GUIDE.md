# Guía de Integración Frontend - Sistema de Reservas Residenciales

Este documento contiene la especificación técnica necesaria para construir la interfaz de usuario. Está optimizado para ser utilizado como instrucción (prompt) para una IA de programación.

## 1. Contexto Técnico
- **Backend URL**: `http://localhost:8080/api`
- **Formato de Datos**: JSON
- **Formato de Fechas**: ISO 8601 (`YYYY-MM-DDTHH:mm:ss`)
- **CORS**: Habilitado para todos los orígenes (`*`).

## 2. Modelos de Datos (Estructura JSON)

### Habitacion
```json
{
  "id": 1,
  "numero": "101",
  "estado": "DISPONIBLE | OCUPADA | LIMPIEZA",
  "tarifa": 50.00,
  "tiempoXLimpieza": 30
}
```

### Reserva
```json
{
  "id": 10,
  "nombreCliente": "Juan Perez",
  "horaInicio": "2026-04-17T12:00:00",
  "tiempoLimiteCheckIn": "2026-04-17T12:30:00",
  "estado": "CONFIRMADA | EN_ESPERA | OCUPADA | FINALIZADA | CANCELADA_POR_NO_SHOW",
  "habitacion": { ...objeto habitacion... }
}
```

## 3. Catálogo de Endpoints (API)

### A. Gestión de Reservas
1. **Crear Reserva**: `POST /api/reservas`
   - **Query Params**: `habitacionId` (Long), `nombreCliente` (String), `horaInicio` (String ISO).
   - **Comportamiento**: Si la habitación no está DISPONIBLE, la reserva se crea en estado `EN_ESPERA`.

2. **Check-In**: `PUT /api/reservas/{id}/checkin`
   - **Efecto**: Cambia Reserva a `OCUPADA` y Habitación a `OCUPADA`.

3. **Check-Out**: `PUT /api/reservas/{id}/checkout`
   - **Efecto**: Cambia Reserva a `FINALIZADA` y Habitación a `LIMPIEZA`.

### B. Gestión de Operaciones
4. **Finalizar Limpieza**: `PUT /api/habitaciones/{id}/finalizar-limpieza`
   - **Lógica Crítica**: Al finalizar la limpieza, el backend automáticamente asigna la habitación al primer cliente en la cola FIFO (si existe) y cambia ese estado a `CONFIRMADA`.

5. **Obtener Estado de Habitación**: `GET /api/habitaciones/{id}`
   - Útil para refrescar dashboards.

### C. Utilidades de Pruebas
6. **Simular No-Show**: `POST /api/reservas/procesar-no-shows`
   - Ejecuta manualmente el escaneo de reservas que expiraron su tiempo de gracia.

## 4. Lógica de Negocio en la UI (Reglas de Oro)

1.  **La Cola FIFO es Transparente**: El frontend no decide quién sigue. Simplemente llama a `finalizar-limpieza` y el backend resuelve la cola. El frontend debe refrescar los datos para ver quién es el nuevo ocupante confirmado.
2.  **Tiempo de Gracia**: Por defecto son **30 minutos**. La UI debería mostrar una cuenta regresiva basada en `tiempoLimiteCheckIn`.
3.  **Estados Visuales**:
    *   `EN_ESPERA`: Color Amarillo/Naranja (indica cola).
    *   `CONFIRMADA`: Color Azul (esperando cliente).
    *   `OCUPADA`: Color Rojo (no disponible).
    *   `DISPONIBLE`: Color Verde.

---
**Instrucción para la IA del Frontend**: "Utiliza React con TypeScript y Axios. Implementa un Dashboard que permita ver el estado de la Habitación 101 y realizar las acciones de Check-in, Check-out y Limpieza. Asegúrate de manejar los estados de carga y errores del API."
