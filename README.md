# Sistema de Reservas para Residenciales

Sistema centralizado para la gestión de reservas de corta estadía en residenciales, diseñado bajo el Proceso Unificado de **Craig Larman** y una **Arquitectura en Capas**.

## Características Principales
- **Gestión de Disponibilidad**: Control en tiempo real de habitaciones.
- **Cola de Espera FIFO**: Asignación automática de habitaciones liberadas al primer cliente en espera.
- **Automatización de No-Show**: Motor de fondo para cancelar pre-reservas vencidas y liberar recursos.
- **Basado en GRASP**: Diseño orientado a objetos con alta cohesión y bajo acoplamiento.

## Arquitectura del Proyecto
- **Frontend**: React + TypeScript (Vite).
- **Backend**: Java 21 + Spring Boot 3 (Spring Data JPA).
- **Base de Datos**: PostgreSQL.
- **Infraestructura**: Docker & Docker Compose.

---

## Requisitos Previos
Para levantar el sistema es necesario tener instalado:
- **Docker**
- **Docker Compose**

---

## Instrucciones de Instalación y Ejecución

1. **Clonar el repositorio.**
2. **Levantar los servicios:**
   Desde la raíz del proyecto, ejecuta:
   ```bash
   docker compose up --build -d
   ```
   *Nota: La primera ejecución puede tardar unos minutos mientras se compilan los servicios.*

## Puertos y Servicios
| Servicio | Puerto | Descripción |
| :--- | :--- | :--- |
| **Frontend** | [5173](http://localhost:5173) | Interfaz de Cliente y Recepción |
| **Backend** | [8080](http://localhost:8080/api) | REST API |
| **PostgreSQL**| 5433 | Persistencia de datos |

---

## Suite de Pruebas
Para ejecutar las pruebas automatizadas (Unitarias, Integración y API) del backend, utiliza:
```bash
docker exec -w /app reservas_backend ./mvnw test
```

## Credenciales de Base de Datos (Dev)
- **Host**: localhost
- **User**: user
- **Pass**: password
- **DB**: reservas_db
- **Port**: 5433
