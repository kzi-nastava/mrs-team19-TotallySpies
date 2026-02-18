-- test database for student2 (2.8)

-- manual:
-- 1. open pgAdmin, DBeaver or anouther tool
-- 2. connect to database
-- 3. run thi s sqruipt
-- 4. test data set fro e2e


-- 1. delete all data before if needed

-- TRUNCATE TABLE panic_notification RESTART IDENTITY CASCADE;
-- TRUNCATE TABLE route_point RESTART IDENTITY CASCADE;
-- TRUNCATE TABLE ride_stop RESTART IDENTITY CASCADE;
-- TRUNCATE TABLE ride_passengers RESTART IDENTITY CASCADE;
-- TRUNCATE TABLE ride RESTART IDENTITY CASCADE;
-- TRUNCATE TABLE drivers RESTART IDENTITY CASCADE;
-- TRUNCATE TABLE vehicle RESTART IDENTITY CASCADE;
-- TRUNCATE TABLE passenger RESTART IDENTITY CASCADE;
-- TRUNCATE TABLE users RESTART IDENTITY CASCADE;
-- TRUNCATE TABLE vehicle_pricing RESTART IDENTITY CASCADE;


CREATE TABLE IF NOT EXISTS vehicle_pricing (
    id SERIAL PRIMARY KEY,
    vehicle_type VARCHAR(255) UNIQUE NOT NULL,
    base_price DOUBLE PRECISION NOT NULL
);

-- 2. users
INSERT INTO users (id, email, name, last_name, password, role, enabled, is_blocked) VALUES 
(1, 'passenger@test.com', 'Marko', 'Tepic', '$2a$12$eQDJydpvN0SQK9EgWqr1xuYTBzWO1IMmQZ9RBEXPXTbD8t6ZsnNOG', 'PASSENGER', true, false),

-- Vozači koji će biti ocenjivani
(2, 'driver1@gmail.com', 'Nikola', 'Nikolic', '$2a$12$eQDJydpvN0SQK9EgWqr1xuYTBzWO1IMmQZ9RBEXPXTbD8t6ZsnNOG', 'DRIVER', true, false),
(3, 'driver2@gmail.com', 'Jovan', 'Jovanovic', '$2a$12$eQDJydpvN0SQK9EgWqr1xuYTBzWO1IMmQZ9RBEXPXTbD8t6ZsnNOG', 'DRIVER', true, false),
(4, 'driver3@gmail.com', 'Kosta', 'Peric', '$2a$12$eQDJydpvN0SQK9EgWqr1xuYTBzWO1IMmQZ9RBEXPXTbD8t6ZsnNOG', 'DRIVER', true, false);


-- 3. passengers
INSERT INTO passenger (id) VALUES 
(1);  -- passenger@test.com

-- 4. vehicles
INSERT INTO vehicle (id, model, license_plate, current_lat, current_lng, vehicle_type, passenger_capacity, baby_transport, pet_transport, current_route_index) VALUES 
(1, 'Tesla Model 3', 'NS-001-EL', 45.2675, 19.8338, 'STANDARD', 4, true, false, 0),
(2, 'Golf 5', 'NS-541-SC', 45.2551, 19.8453, 'STANDARD', 4, false, false, 0),
(3, 'Renault Kadjar', 'NS-239-NN', 45.2496, 19.8369, 'STANDARD', 4, true, true, 0);

-- 5. DRIVERS
INSERT INTO drivers (id, vehicle_id, is_active, average_rating) VALUES 
(2, 1, true, 5.0),  -- Nikola Nikolic vozi Teslu
(3, 2, true, 5.0),  -- Jovan Jovanovic vozi Golf
(4, 3, true, 5.0);  -- Kosta Peric vozi Renault


-- 6. rides 
-- Voznje 1, 2, 4, 7, 6 su u roku od 3 dana → MOGU da se ocene
-- Voznja 3 je starija od 3 dana → NE MOZE da se oceni

INSERT INTO ride (id, driver_id, status, total_price, distance_km, babies_transport, pets_transport, vehicle_type, created_at, started_at, finished_at, estimated_time, panic) VALUES 
(1, 2, 'COMPLETED', 450.0, 3.5, false, false, 'STANDARD', 
 CURRENT_TIMESTAMP - INTERVAL '1' DAY, 
 CURRENT_TIMESTAMP - INTERVAL '23' HOUR, 
 CURRENT_TIMESTAMP - INTERVAL '22' HOUR, 
 7.5, false);


INSERT INTO ride (id, driver_id, status, total_price, distance_km, babies_transport, pets_transport, vehicle_type, created_at, scheduled_for, estimated_time, panic)
VALUES (3, 2, 'SCHEDULED', 500.0, 4.0, false, false, 'STANDARD', 
        CURRENT_TIMESTAMP, 
        CURRENT_TIMESTAMP + INTERVAL '2' HOUR, 
        10.0, false);

INSERT INTO ride (id, driver_id, status, total_price, distance_km, babies_transport, pets_transport, vehicle_type, created_at, started_at, finished_at, estimated_time, panic)
VALUES (4, 4, 'STOPPED', 1200.0, 10.2, true, false, 'STANDARD', 
        CURRENT_TIMESTAMP - INTERVAL '1' HOUR, 
        CURRENT_TIMESTAMP - INTERVAL '55' MINUTE, 
        CURRENT_TIMESTAMP - INTERVAL '50' MINUTE, 
        20.0, true);

INSERT INTO ride (id, driver_id, status, total_price, distance_km, babies_transport, pets_transport, vehicle_type, created_at, started_at, finished_at, estimated_time, panic) VALUES 
(6, 4, 'COMPLETED', 380.0, 2.1, false, false, 'STANDARD', 
 CURRENT_TIMESTAMP - INTERVAL '2' DAY, 
 CURRENT_TIMESTAMP - INTERVAL '47' HOUR, 
 CURRENT_TIMESTAMP - INTERVAL '46' HOUR, 
 5.0, false);

INSERT INTO ride (id, driver_id, status, total_price, distance_km, babies_transport, pets_transport, vehicle_type, created_at, started_at, finished_at, estimated_time, panic) VALUES 
(7, 3, 'COMPLETED', 520.0, 4.2, true, false, 'STANDARD', 
 CURRENT_TIMESTAMP - INTERVAL '5' HOUR, 
 CURRENT_TIMESTAMP - INTERVAL '4' HOUR, 
 CURRENT_TIMESTAMP - INTERVAL '3' HOUR, 
 10.0, false);

INSERT INTO ride (id, driver_id, status, total_price, distance_km, babies_transport, pets_transport, vehicle_type, created_at, started_at, finished_at, estimated_time, panic) VALUES 
(8, 2, 'COMPLETED', 650.0, 5.5, false, true, 'STANDARD', 
 CURRENT_TIMESTAMP - INTERVAL '4' DAY, 
 CURRENT_TIMESTAMP - (INTERVAL '4' DAY + INTERVAL '1' HOUR), 
 CURRENT_TIMESTAMP - (INTERVAL '4' DAY + INTERVAL '2' HOUR), 
 12.0, false);

-- 7. connect passenger with ride
INSERT INTO ride_passengers (rides_id, passengers_id) VALUES 
(1, 1),  -- Voznja 1 (pre 1 dan)
(3, 1),  -- Voznja 3 (zakazana) 
(4, 1),  -- Voznja 4 (stopirana)
(6, 1),  -- Voznja 6 (pre 2 dana)
(7, 1),  -- Voznja 7 (pre 5 sati)
(8, 1);  -- Voznja 8 (pre 4 dana)

-- 8. ride stops
INSERT INTO ride_stop (id, address, latitude, longitude, order_index, ride_id) VALUES
(1, 'Bulevar Oslobodjenja 104, Novi Sad', 45.2675, 19.8338, 0, 1),
(2, 'Strazilovska 28, Novi Sad', 45.2512, 19.8366, 1, 1),

(11, 'Micurinova 38, Novi Sad', 45.2551, 19.8453, 0, 6),
(12, 'Narodnog fronta 70, Novi Sad', 45.2396, 19.8227, 1, 6),

(13, 'Micurinova 38, Novi Sad', 45.2551, 19.8453, 0, 7),
(14, 'Maksima Gorkog 15, Novi Sad', 45.2486, 19.8319, 1, 7),

(15, 'Futoska 25, Novi Sad', 45.2600, 19.8300, 0, 8),
(16, 'Zeleznicka 12, Novi Sad', 45.2639, 19.8495, 1, 8);

-- panic
INSERT INTO panic_notification (id, ride_id, user_id, reason, time) 
VALUES (1, 4, 1, 'crazy!', CURRENT_TIMESTAMP);

-- prices
INSERT INTO vehicle_pricing (id, base_price, vehicle_type) VALUES 
(1, 250.0, 'STANDARD');
INSERT INTO vehicle_pricing (id, base_price, vehicle_type) VALUES 
(2, 500.0, 'LUXURY');
INSERT INTO vehicle_pricing (id, base_price, vehicle_type) VALUES 
(3, 400.0, 'VAN');

-- sync 
SELECT setval('users_id_seq', (SELECT max(id) FROM users));
SELECT setval('vehicle_id_seq', (SELECT max(id) FROM vehicle));
SELECT setval('ride_id_seq', (SELECT max(id) FROM ride));
SELECT setval('ride_stop_id_seq', (SELECT max(id) FROM ride_stop));
SELECT setval('vehicle_pricing_id_seq', (SELECT max(id) FROM vehicle_pricing));

-- ALTER TABLE drivers 
-- ALTER COLUMN average_rating SET DEFAULT 5.0;