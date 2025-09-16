# ExpeditionSec – Backend

API REST con **Spring Boot** para gestionar la **asignación de palets a órdenes de carga** (cada orden tiene asociado un camión). Este backend lo uso junto con el frontend ***expeditions-ui***.

---

## Qué hace y por qué (lógica de negocio)

El caso de uso principal es **asignar un palet a una orden de carga**. En esa operación aplico estas reglas:

1. **Existe el palet** y está en estado **DISPONIBLE**.  
2. **No está ya asignado** a otra orden (ni a la misma).  
3. **Existe la orden** y su estado es **ABIERTA** o **EN_CARGA** (no permito asignar a una orden **CERRADA**).  
4. La orden **tiene camión asociado** (si no hay camión, la asignación no tiene sentido).  
5. Si todo es correcto, marco el palet como **ASIGNADO**, lo **vinculo a la orden** y devuelvo una respuesta con ids y un mensaje entendible.

La operación está anotada con @Transactional para que si algo falla en medio, se deshaga todo lo realizado automáticamente y no deje datos a medias.

Uso **DTOs** como AssignPalletRequest y AssignPalletResponse (con record de Java).

---

## Base de datos (por qué H2)

Uso **H2 en memoria** por simplicidad:
- No necesito instalar nada externo.
- Arranca muy rápido y es perfecta para pruebas y demo.
- Hibernate me genera el esquema a partir de las entidades.

> Nota: el import.sq **no se ejecuta automáticamente** en este proyecto. Si quiero datos de ejemplo, **entro en la consola H2** y ejecuto un script manualmente (abajo dejo uno para poder comenzar
> a trabajar con unos datos de prueba)_
> SCRIPT:
> SCRIPT PARA METER EN LA BBDD H2

-- Limpieza opcional
DELETE FROM PALET;
DELETE FROM ORDEN_CARGA;
DELETE FROM CAMION;

-- Camión
INSERT INTO CAMION (ID, MATRICULA, TRANSPORTISTA, ESTADO)
VALUES (1, '1234-ABC', 'TransGarcia', 'EN_MUELLE');

-- Orden con camión
INSERT INTO ORDEN_CARGA (ID, CODIGO, DESTINO, ESTADO, CAMION_ID)
VALUES (10, 'ORD-001', 'Madrid', 'ABIERTA', 1);

-- Palets (sin orden asignada inicialmente)
INSERT INTO PALET (ID, MODELO, TARA_KG, DIM_LARGOM, DIM_ANCHOM, TIPO_PRODUCTO, ESTADO, ORDEN_ID)
VALUES (100, 'Europalé', 30.0, 1.2, 0.8, 'Ciruelas', 'DISPONIBLE', NULL);

INSERT INTO PALET (ID, MODELO, TARA_KG, DIM_LARGOM, DIM_ANCHOM, TIPO_PRODUCTO, ESTADO, ORDEN_ID)
VALUES (101, 'Europalé', 30.0, 1.2, 0.8, 'Ciruelas', 'DISPONIBLE', NULL);

-- Comprobamos
SELECT * FROM CAMION;
SELECT * FROM ORDEN_CARGA;
SELECT * FROM PALET;

---

## Estructura del proyecto

Para la estructura del proyecto me he basado en el modelo típico donde se estructuran los archivos en model, service y controller típico de las apis con un directorio aparte para manejar errores y otro
para los dtos entre otros más archivos. Vendría a ser algo así:

<img width="706" height="1567" alt="image" src="https://github.com/user-attachments/assets/f6690462-5968-4993-84ce-5277c9fa5186" />
<img width="620" height="1477" alt="image" src="https://github.com/user-attachments/assets/abc19648-d7bf-4073-91cd-6fe29a72f722" />

---

##  Endpoints

### Asignar palet a orden

POST /api/assignments/pallet-to-order
Content-Type: application/json

**Body**
json
{ "palletId": 100, "orderId": 10 }


Respuesta (200)

{
  "palletId": 100,
  "orderId": 10,
  "truckId": 1,
  "message": "Palet 100 asignado a la orden 10 (camión 1)"
}

***Cómo ejecutar el proyecto***

- Clonar y entrar en la carpeta:

git clone https://github.com/TU_USUARIO/expeditionsec.git
cd expeditionsec


- Arrancar con Maven:

mvn spring-boot:run


- Consola H2
Ir a http://localhost:8080/h2-console y usar:

JDBC URL: jdbc:h2:mem:expeditions
User: sa
Password:  (vacío)

- Insertar datos de ejemplo
Ejecutar esto (que es el mismo script de prueba puesto antes) en la consola H2:

-- Camión
insert into camion (id, matricula, transportista, estado)
values (1, '1234-ABC', 'TransGarcia', 'EN_MUELLE');

-- Orden abierta con camión 1
insert into orden_carga (id, codigo, destino, estado, camion_id)
values (10, 'ORD-001', 'Madrid', 'ABIERTA', 1);

-- Palets (uno disponible y otro ya asignado de ejemplo)
insert into palet (id, modelo, tara_kg, dim_largom, dim_anchom, tipo_producto, estado, orden_id)
values (100, 'Europalé', 30.0, 1.2, 0.8, 'Ciruelas', 'DISPONIBLE', null);

insert into palet (id, modelo, tara_kg, dim_largom, dim_anchom, tipo_producto, estado, orden_id)
values (101, 'Europalé', 30.0, 1.2, 0.8, 'Ciruelas', 'DISPONIBLE', null);


- Probar con Postman
POST http://localhost:8080/api/assignments/pallet-to-order con el body JSON mostrado arriba.

<img width="2208" height="954" alt="image" src="https://github.com/user-attachments/assets/3691bde1-2fd3-44bf-a088-5e0f68ad33ac" />


- Por último, manteniéndo arrancado el back, arrancaríamos el front y tras hacer npm run serve, accederíamos a la url que nos ofrece la terminal al ejecutar el front y ya
podríamos asignar un palet a un camión/orden.


***Decisiones y justificación***
La plantilla para organizar el proyecto de back la cree a partir de Spring Initializr que me gusta mucho para arrancar.

H2 en memoria: perfecta para pruebas, arranca rápido y no tengo que instalar nada. En producción cambiaría a Postgres/MySQL.

@Transactional en el servicio para garantizar consistencia: o se aplican todos los cambios (estado + relación), o ninguno.

DTOs con record: son inmutables, concisos y me parecía mejor opción que crearme una nueva clase dto y un directorio mappers con funciones para pasar de entity --> dto , dto --> entity

Excepciones claras (BusinessException, NotFoundException) para distinguir errores de negocio de errores de datos.

