# Guía de Integración Frontend - Backend

Esta guía detalla los contratos de operación y la estructura de datos para la comunicación entre el cliente React y el servidor Spring Boot, siguiendo los estándares de **Mannino** (Persistencia y Tipos) y **Larman** (Contratos y Flujo).

## 1. Seguridad y Autenticación (JWT)

El sistema utiliza **Stateless Authentication** mediante tokens JWT.

### Proceso de Login
- **Endpoint**: `POST /api/auth/login`
- **Contrato (Input)**:
  ```json
  {
    "email": "admin@residencial.com",
    "password": "12345"
  }
  ```
- **Contrato (Output)**:
  ```json
  {
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  }
  ```

### Uso de Tokens
Para cada petición a un endpoint protegido (todos excepto `/api/auth/**` y `/api/archivos/**`), se debe incluir el header de autorización:
`Authorization: Bearer <TU_TOKEN_AQUÍ>`

---

## 2. Multimedia y Gestión de Archivos

Los archivos se gestionan localmente en el servidor, pero se sirven a través de una URL pública.

### Subida de Fotos
- **Endpoint**: `POST /api/habitaciones/{id}/fotos`
- **Método**: Multipart (utilizar `FormData` en el frontend).
- **Campos**:
    - `file`: El archivo binario de la imagen.
    - `esPrincipal`: (Boolean) Indica si es la foto de portada.
- **Relación (Mannino)**: Relación 1:N entre `Habitacion` y `FotoHabitacion`.

### Visualización de Imágenes
Las rutas devueltas por el JSON (ej. `/api/archivos/uuid-file.jpg`) son relativas. Para visualizar la imagen en React:
`<img src={`http://localhost:8080${foto.rutaLocal}`} />`

---

## 3. Gestión de Reservas y Estados

Las reservas siguen un ciclo de vida definido por un diagrama de estados (Larman).

### Estados de la Reserva
- `EN_ESPERA`: Creada pero el cliente no ha llegado.
- `CONFIRMADA`: Verificada.
- `OCUPADA`: El cliente está en la habitación.
- `FINALIZADA`: El cliente ha salido.
- `CANCELADA` / `CANCELADA_POR_NO_SHOW`: Fallo en la llegada o cancelación manual.

### Transiciones Críticas
- **Finalizar Limpieza**: `PUT /api/habitaciones/{id}/finalizar-limpieza`
  - *Efecto*: Cambia el estado de la habitación de `LIMPIEZA` a `DISPONIBLE`.

---

## 4. Ejemplos de Pruebas (curl)

Utiliza estos comandos para validar que tu conexión es exitosa:

### 4.1 Login y obtención de Token
```bash
curl -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"email":"admin@residencial.com", "password":"12345"}'
```

### 4.2 Ver detalles de una habitación (Protegido)
```bash
curl -X GET http://localhost:8080/api/habitaciones/1 \
     -H "Authorization: Bearer <TOKEN>"
```

### 4.3 Subir una foto de prueba
```bash
curl -X POST http://localhost:8080/api/habitaciones/1/fotos \
     -H "Authorization: Bearer <TOKEN>" \
     -F "file=@/ruta/a/tu/imagen.jpg" \
     -F "esPrincipal=true"
```

### 4.4 Finalizar Limpieza
```bash
curl -X PUT http://localhost:8080/api/habitaciones/1/finalizar-limpieza \
     -H "Authorization: Bearer <TOKEN>"
```

---

## 5. Documentación Interactiva
Para ver el detalle técnico exacto de cada campo y código de error (Swagger/OpenAPI):
👉 **http://localhost:8080/swagger-ui/index.html**
