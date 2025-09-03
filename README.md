# Linktic - Microservicios Productos e Inventario

## 1. Descripción
Este proyecto implementa dos microservicios independientes que interactúan entre sí:

- **Productos**: gestión completa de productos (CRUD).
- **Inventario**: gestión de inventario, compras y consulta de cantidades disponibles.

Se utiliza **JSON API** para todas las respuestas, SQLite como base de datos, y comunicación HTTP entre servicios mediante API keys.

Repositorio en GitHub: [https://github.com/CesarLeal1991/linktic](https://github.com/CesarLeal1991/linktic)

---

## 2. Tecnologías usadas
- Lenguaje: Java (Spring Boot)
- Base de datos: SQLite (ligera, portable y fácil de configurar)
- Docker / Docker Compose para contenerización
- Testing: JUnit 5 + Mockito
- IA: GPT-5 para acelerar generación de código y mejorar calidad
- Control de versiones: Git + Git Flow

---

## 3. Estructura de los Microservicios

### 3.1 Microservicio Productos
**Proyecto independiente:** `microservicio-productos`
- **Modelo Producto**:
    - `id`: Long
    - `nombre`: String
    - `precio`: Double
    - `descripcion` (opcional): String
- **Endpoints**:
    - `POST /productos` - Crear producto
    - `GET /productos/{id}` - Obtener producto por ID
    - `GET /productos` - Listar todos los productos
    - `PUT /productos/{id}` - Actualizar producto
    - `DELETE /productos/{id}` - Eliminar producto

### 3.2 Microservicio Inventario
**Proyecto independiente:** `microservicio-inventario`
- **Modelo Inventario**:
    - `producto_id`: Long
    - `cantidad`: Integer
- **Endpoints**:
    - `GET /inventario` - Listar todos los inventarios
    - `GET /inventario/{productoId}` - Consultar cantidad de un producto
    - `PUT /inventario/{productoId}` - Actualizar cantidad disponible
    - `POST /inventario/compra` - Registrar una compra
    - `GET /inventario/total` - Consultar inventario total

---

## 4. Flujo de Compra
1. Se realiza la solicitud `POST /inventario/compra` con `productoId` y `cantidad`.
2. Se verifica existencia del producto en el microservicio Productos.
3. Se valida disponibilidad en Inventario.
4. Se descuenta la cantidad comprada del inventario.
5. Se retorna un JSON con:
    - `productoId`
    - `cantidadComprada`
    - `cantidadRestante`
    - `id` del inventario actualizado

**Decisión técnica:**  
El endpoint de compra se implementó en el microservicio **Inventario** para mantener responsabilidad única (Inventario gestiona la cantidad disponible) y minimizar acoplamiento con Productos.

---

## 5. Comunicación entre Microservicios
- HTTP + JSON API
- Autenticación con API Key (configurable en variables de entorno)
- Timeout y reintentos básicos implementados con WebClient

---

## 6. Base de Datos
- SQLite
- Justificación: ligera, fácil de configurar para pruebas locales y microservicios pequeños.
- Archivo de base de datos incluido en cada servicio.

---

## 7. Dockerización
- Cada microservicio tiene su **Dockerfile**.
- Se recomienda usar **Docker Compose** para levantar ambos servicios simultáneamente.

Ejemplo de ejecución:

```bash
docker-compose up --build
