-- Camiones
insert into camion (id, matricula, transportista, estado) values (1, '1234-ABC', 'TransGarcia', 'EN_MUELLE');

-- Orden con camión
insert into orden_carga (id, codigo, destino, estado, camion_id) values (10, 'ORD-001', 'Madrid', 'ABIERTA', 1);

-- Palets
insert into palet (id, modelo, tara_kg, dim_largo_m, dim_ancho_m, tipo_producto, estado, orden_id)
values (100, 'Europalé', 30.0, 1.2, 0.8, 'Ciruelas', 'DISPONIBLE', null);

insert into palet (id, modelo, tara_kg, dim_largo_m, dim_ancho_m, tipo_producto, estado, orden_id)
values (101, 'Europalé', 30.0, 1.2, 0.8, 'Ciruelas', 'DISPONIBLE', null);
