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

INSERT INTO users (id, email, name, last_name, password, role, enabled, is_blocked)
VALUES (1, 'p1@test.com', 'Ana', 'Anic', 'pass', 'PASSENGER', true, false);

INSERT INTO users (id, email, name, last_name, password, role, enabled, is_blocked)
VALUES (2, 'p2@test.com', 'Petra', 'Petric', 'pass', 'PASSENGER', true, false);

-- 3. UBACIVANJE VOZILA (Potrebno jer Driver ima FK na Vehicle)
INSERT INTO vehicle (id, model, license_plate, current_lat, current_lng, vehicle_type, passenger_capacity, baby_transport, pet_transport, current_route_index) 
VALUES (100, 'Test Auto', 'NS-TEST', 45.0, 19.0, 'STANDARD', 4, true, true, 0);

-- 4. POVEZIVANJE VOZAČA I VOZILA
INSERT INTO drivers (id, vehicle_id, is_active, average_rating) 
VALUES (100, 100, true, 5.0);

-- POVEZIVANJE PASSENGER SA USER
INSERT INTO passenger (id) VALUES (1);
INSERT INTO passenger (id) VALUES (2);

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

-- Voznja 5: PENDING voznja bez vozaca, kreator p2 (zakazana za buducnost)
INSERT INTO ride (id, driver_id, status, total_price, distance_km, babies_transport, pets_transport, vehicle_type, created_at, scheduled_for, estimated_time, panic, creator_id)
VALUES (5, null, 'PENDING', 600.0, 4.0,false, false, 'STANDARD',CURRENT_TIMESTAMP, DATEADD('HOUR', 2, CURRENT_TIMESTAMP),12.0, false, 2);

-- Voznja 6: STOPPED voznja vozaca 10, kreator p1 (prije dva dana)
INSERT INTO ride (id, driver_id, status, total_price, distance_km, babies_transport, pets_transport, vehicle_type, created_at, started_at, finished_at, estimated_time, panic, creator_id)
VALUES (6, 100, 'STOPPED', 360.0, 2.0,false, false, 'STANDARD',DATEADD('DAY', -2, CURRENT_TIMESTAMP),DATEADD('DAY', -2, CURRENT_TIMESTAMP),DATEADD('DAY', -2, CURRENT_TIMESTAMP),8.0, false, 1);

-- 6. VEZA PUTNIK-VOZNJA
INSERT INTO ride_passengers (rides_id, passengers_id) VALUES (1, 1);
INSERT INTO ride_passengers (rides_id, passengers_id) VALUES (2, 1);
INSERT INTO ride_passengers (rides_id, passengers_id) VALUES (3, 2);
INSERT INTO ride_passengers (rides_id, passengers_id) VALUES (4, 1);
INSERT INTO ride_passengers (rides_id, passengers_id) VALUES (5, 2);
INSERT INTO ride_passengers (rides_id, passengers_id) VALUES (6, 1);
