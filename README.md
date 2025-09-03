# Linktic Microservicios

## Descripción

Este proyecto implementa dos microservicios independientes que interactúan entre sí:

1. **Productos**: gestión de productos.
2. **Inventario**: control del inventario y proceso de compras.

El proyecto utiliza **SQLite** como base de datos para simplicidad y facilidad de configuración. La comunicación entre microservicios se realiza mediante HTTP y sigue el estándar **JSON API**.

Se utilizó **GPT-5** para acelerar tareas de desarrollo y **Git** para control de versiones, siguiendo buenas prácticas de Git Flow.

---

## Arquitectura

```
+-------------------+          +-------------------+
| Microservicio     |  HTTP    | Microservicio     |
| Productos         | <------> | Inventario        |
|-------------------|          |-------------------|
| CRUD productos    |          | Consultar stock   |
|                   |          | Actualizar stock  |
|                   |          | Registrar compra  |
+-------------------+          +-------------------+
          ^                             ^
          |                             |
          +--------- SQLite ------------+
```

- Cada microservicio tiene su propia API.
- Inventario consulta Productos para validar existencia antes de procesar compras.

---

## Microservicio Productos

**Modelo Producto**:
- `id` (Long)
- `nombre` (String)
- `precio` (Double)
- `descripcion` (String, opcional)

**Endpoints**:
- `POST /productos` → Crear producto
- `GET /productos/{id}` → Obtener producto por ID
- `GET /productos` → Listar productos
- `PUT /productos/{id}` → Actualizar producto
- `DELETE /productos/{id}` → Eliminar producto

---

## Microservicio Inventario

**Modelo Inventario**:
- `productoId` (Long)
- `cantidad` (Integer)

**Endpoints**:
- `GET /inventario` → Listar inventario
- `GET /inventario/{productoId}` → Consultar cantidad de un producto
- `PUT /inventario/{productoId}` → Actualizar cantidad disponible
- `POST /inventario/compra` → Registrar compra y descontar stock
- `GET /inventario/total` → Inventario total (opcional)

**Flujo de Compra**:
1. Recibe `productoId` y `cantidad`.
2. Valida existencia del producto en microservicio Productos.
3. Verifica inventario suficiente.
4. Descuenta la cantidad comprada.
5. Retorna información de la compra.
6. Manejo de errores:
    - Producto inexistente → 404
    - Inventario insuficiente → 400
    - Error interno → 500

---

## Base de datos

- SQLite para ambos microservicios.
- Justificación: ligera, sin configuración de servidor adicional, ideal para pruebas locales y demostraciones.

---

## Colección de Postman

Se incluye una colección de Postman para probar todos los endpoints de los microservicios.

- Archivo: `postman/Linktic.postman_collection.json`
- Contiene requests para:
    - **Productos**: Crear, listar, obtener, actualizar y eliminar producto.
    - **Inventario**: Listar inventario, consultar cantidad de producto, registrar compra, obtener inventario total.
- Uso:
    1. Importar la colección en Postman.
    2. Ejecutar los endpoints según la secuencia de prueba.
    3. Autenticación básica mediante API key entre servicios.

---

## Requisitos técnicos

- **Lenguaje**: Java (Spring Boot)
- **JSON API**: Para todas las respuestas.
- **Docker**: Cada microservicio containerizado.
- **Pruebas unitarias**: Cobertura de creación de productos, gestión de inventario, compras y comunicación entre servicios.
- **Prueba de integración**: Al menos una por microservicio.

---

## IA y herramientas de desarrollo

- Se utilizó **GPT-5** para generar ejemplos de endpoints, pruebas unitarias y acelerar desarrollo de lógica repetitiva.
- Verificación de calidad:
    - Revisión manual del código generado.
    - Ejecución de pruebas unitarias y de integración.

---

## Instrucciones de instalación y ejecución

1. Clonar el repositorio:

```bash
git clone https://github.com/CesarLeal1991/linktic.git
cd linktic
```

2. Construir y ejecutar con Maven:

```bash
cd productos
./mvnw clean install
./mvnw spring-boot:run
```

```bash
cd inventario
./mvnw clean install
./mvnw spring-boot:run
```

3. Alternativamente, usar Docker Compose (si los archivos `Dockerfile` y `docker-compose.yml` están configurados):

```bash
docker-compose up --build
```

---

## Decisiones técnicas

- **Endpoint de compra** se implementa en **Inventario** porque:
    - Mantiene la responsabilidad de gestionar stock.
    - Reduce acoplamiento con Productos.
    - Facilita consistencia de datos y manejo de errores locales de inventario.
- **SQLite** elegido por su simplicidad para pruebas locales.
- **JSON API** garantiza consistencia en respuestas y compatibilidad futura.

---

## Diagramas

- Arquitectura de microservicios y flujo de compra incluidos en la sección **Arquitectura** arriba.
- Se recomienda ampliar con herramientas como Draw.io o diagrams.net si se desea.

---

## Buenas prácticas implementadas

- Control de versiones con **Git** siguiendo Git Flow.
- Pruebas unitarias e integración.
- Manejo de errores y validaciones.
- Documentación de endpoints y flujo de compra.
