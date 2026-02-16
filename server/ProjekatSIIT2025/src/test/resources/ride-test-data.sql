-- 1. BRISANJE (Redosled je bitan zbog stranih ključeva)
DELETE FROM ride_passengers;
DELETE FROM ride_stop;
DELETE FROM route_point;
DELETE FROM ride;
DELETE FROM drivers;
DELETE FROM vehicle;
DELETE FROM users;

-- 2. UBACIVANJE VOZAČA (ID: 100)
-- Kolone: id, email, name, last_name, password, role, enabled, is_blocked
INSERT INTO users (id, email, name, last_name, password, role, enabled, is_blocked) 
VALUES (100, 'test_driver@gmail.com', 'Nedeljko', 'Pavlovic', 'pass', 'DRIVER', true, false);

-- 3. UBACIVANJE VOZILA (Potrebno jer Driver ima FK na Vehicle)
INSERT INTO vehicle (id, model, license_plate, current_lat, current_lng, vehicle_type, passenger_capacity, baby_transport, pet_transport, current_route_index) 
VALUES (100, 'Test Auto', 'NS-TEST', 45.0, 19.0, 'STANDARD', 4, true, true, 0);

-- 4. POVEZIVANJE VOZAČA I VOZILA
INSERT INTO drivers (id, vehicle_id, is_active, average_rating) 
VALUES (100, 100, true, 5.0);

-- 5. VOŽNJE ZA TESTIRANJE (Tabela je 'ride', ne 'rides')
-- Vožnja 1: ACTIVE (Ovu završavaš u servisu)
INSERT INTO ride (id, driver_id, status, total_price, distance_km, babies_transport, pets_transport, vehicle_type, created_at, started_at, estimated_time, panic) 
VALUES (1, 100, 'ACTIVE', 500.0, 3.0, false, false, 'STANDARD', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 10.0, false);

-- Vožnja 2: SCHEDULED (Ranija - treba da bude prva sledeća)
-- Koristimo H2 sintaksu DATEADD za simulaciju budućeg vremena
INSERT INTO ride (id, driver_id, status, total_price, distance_km, babies_transport, pets_transport, vehicle_type, created_at, scheduled_for, estimated_time, panic, started_at) 
VALUES (2, 100, 'SCHEDULED', 600.0, 4.0, false, false, 'STANDARD', CURRENT_TIMESTAMP, DATEADD('HOUR', 1, CURRENT_TIMESTAMP), 15.0, false, DATEADD('HOUR', 1, CURRENT_TIMESTAMP));

-- Vožnja 3: SCHEDULED (Kasnija)
INSERT INTO ride (id, driver_id, status, total_price, distance_km, babies_transport, pets_transport, vehicle_type, created_at, scheduled_for, estimated_time, panic, started_at) 
VALUES (3, 100, 'SCHEDULED', 700.0, 5.0, false, false, 'STANDARD', CURRENT_TIMESTAMP, DATEADD('HOUR', 3, CURRENT_TIMESTAMP), 20.0, false, DATEADD('HOUR', 3, CURRENT_TIMESTAMP));

-- Vožnja 4: COMPLETED (Ovu ne sme da pokupi metoda findByDriverIdAndStatusIn za aktivne/zakazane)
INSERT INTO ride (id, driver_id, status, total_price, distance_km, babies_transport, pets_transport, vehicle_type, created_at, started_at, finished_at, estimated_time, panic) 
VALUES (4, 100, 'COMPLETED', 800.0, 6.0, false, false, 'STANDARD', DATEADD('DAY', -1, CURRENT_TIMESTAMP), DATEADD('DAY', -1, CURRENT_TIMESTAMP), DATEADD('DAY', -1, CURRENT_TIMESTAMP), 25.0, false);