-- =========================
-- E2E seed: Admin ride history
-- Login: admin@gmail.com / andjela123
-- =========================

BEGIN;

DELETE FROM ride_cancellation;
DELETE FROM ride_stop;
DELETE FROM ride_passengers;
DELETE FROM ride;
DELETE FROM drivers;
DELETE FROM passenger;
DELETE FROM vehicle;
DELETE FROM users;
INSERT INTO users (id, name, last_name, email, password, role, enabled, is_blocked)
VALUES
(1, 'Admin', 'User', 'admin@gmail.com',
 '$2b$10$7p5dMorM3wtWZhj8iw/wOuaixy.zx3NxFhtd3/Wn6seO72kZ2NXCO',
 'ADMIN', TRUE, FALSE);

-- DRIVER
INSERT INTO users (id, name, last_name, email, password, role, enabled, is_blocked)
VALUES
(2, 'Test', 'Driver', 'driver1@gmail.com',
 '$2b$10$7p5dMorM3wtWZhj8iw/wOuaixy.zx3NxFhtd3/Wn6seO72kZ2NXCO',
 'DRIVER', TRUE, FALSE);

-- PASSENGERS
INSERT INTO users (id, name, last_name, email, password, role, enabled, is_blocked)
VALUES
(3, 'Ana', 'Passenger', 'passenger1@gmail.com',
 '$2b$10$7p5dMorM3wtWZhj8iw/wOuaixy.zx3NxFhtd3/Wn6seO72kZ2NXCO',
 'PASSENGER', TRUE, FALSE),
(4, 'Marko', 'Passenger', 'passenger2@gmail.com',
 '$2b$10$7p5dMorM3wtWZhj8iw/wOuaixy.zx3NxFhtd3/Wn6seO72kZ2NXCO',
 'PASSENGER', TRUE, FALSE);

INSERT INTO vehicle (id, model, vehicle_type, license_plate, passenger_capacity, baby_transport, pet_transport, current_lat, current_lng, current_route_index)
VALUES
(10, 'Skoda Octavia', 'STANDARD', 'NS-123-AA', 4, TRUE, FALSE, 45.2671, 19.8335, 0);

INSERT INTO drivers (id, is_active, vehicle_id, average_rating)
VALUES
(2, TRUE, 10, 4.7);
INSERT INTO passenger (id) VALUES (3), (4);
INSERT INTO ride (
  id, driver_id, panic, status, distance_km, estimated_time,
  babies_transport, pets_transport, vehicle_type,
  created_at, scheduled_for, started_at, finished_at,
  total_price, creator_id
) VALUES
-- Ride A (normal)
(100, 2, FALSE, 'COMPLETED', 5.2, 18,
 TRUE, FALSE, 'STANDARD',
 '2026-02-01 10:00:00', NULL, '2026-02-01 10:05:00', '2026-02-01 10:25:00',
 500.00, 3),

-- Ride B (panic TRUE)
(101, 2, TRUE, 'COMPLETED', 8.7, 25,
 FALSE, FALSE, 'STANDARD',
 '2026-02-02 11:00:00', NULL, '2026-02-02 11:03:00', '2026-02-02 11:35:00',
 750.00, 3),

-- Ride C (cancelled)
(102, 2, FALSE, 'CANCELLED', 2.1, 8,
 FALSE, TRUE, 'STANDARD',
 '2026-02-03 12:00:00', NULL, '2026-02-03 12:02:00', '2026-02-03 12:05:00',
 200.00, 4),

-- Ride D (more stops)
(103, 2, FALSE, 'COMPLETED', 12.4, 35,
 TRUE, TRUE, 'STANDARD',
 '2026-02-04 09:00:00', NULL, '2026-02-04 09:05:00', '2026-02-04 09:50:00',
 1200.00, 4);


INSERT INTO ride_passengers (rides_id, passengers_id) VALUES
(100, 3),
(101, 3),
(102, 4),
(103, 4);

-- Ride A: pickup + destination
INSERT INTO ride_stop (id, address, latitude, longitude, order_index, ride_id) VALUES
(1000, 'Bulevar oslobođenja 1, Novi Sad', 45.2670, 19.8330, 0, 100),
(1001, 'Trg slobode 1, Novi Sad',        45.2551, 19.8452, 1, 100);

-- Ride B
INSERT INTO ride_stop (id, address, latitude, longitude, order_index, ride_id) VALUES
(1010, 'Železnička stanica, Novi Sad',   45.2650, 19.8400, 0, 101),
(1011, 'Spens, Novi Sad',                45.2460, 19.8510, 1, 101);

-- Ride C (cancelled)
INSERT INTO ride_stop (id, address, latitude, longitude, order_index, ride_id) VALUES
(1020, 'Futoška pijaca, Novi Sad',       45.2565, 19.8305, 0, 102),
(1021, 'Limanski park, Novi Sad',        45.2410, 19.8450, 1, 102);

-- Ride D (has additional stops)
INSERT INTO ride_stop (id, address, latitude, longitude, order_index, ride_id) VALUES
(1030, 'Detelinara, Novi Sad',           45.2700, 19.8100, 0, 103),
(1031, 'Grbavica, Novi Sad',             45.2500, 19.8300, 1, 103),
(1032, 'Keј, Novi Sad',                  45.2550, 19.8500, 2, 103),
(1033, 'Petrovaradin, Novi Sad',         45.2520, 19.8720, 3, 103);

INSERT INTO ride_cancellation (id, user_id, ride_id, cancellation_reason, time)
VALUES
(5000, 4, 102, 'Changed plans', '2026-02-03 12:04:00');

COMMIT;
