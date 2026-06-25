-- test database for student1 (2.4.3)

-- manual:
-- 1. open pgAdmin, DBeaver or anouther tool
-- 2. connect to database
-- 3. run this sql
-- 4. test data set for e2e


BEGIN;

-- 1. delete all data before if needed
TRUNCATE TABLE passenger_favourite_rides RESTART IDENTITY CASCADE;
TRUNCATE TABLE ride_stop RESTART IDENTITY CASCADE;
TRUNCATE TABLE ride_passengers RESTART IDENTITY CASCADE;
TRUNCATE TABLE panic_notification RESTART IDENTITY CASCADE;
TRUNCATE TABLE ride RESTART IDENTITY CASCADE;
TRUNCATE TABLE drivers RESTART IDENTITY CASCADE;
TRUNCATE TABLE passenger RESTART IDENTITY CASCADE;
TRUNCATE TABLE vehicle RESTART IDENTITY CASCADE;
TRUNCATE TABLE users RESTART IDENTITY CASCADE;
TRUNCATE TABLE vehicle_pricing RESTART IDENTITY CASCADE;

-- 2. add two users (driver and passenger)
INSERT INTO users (id, email, name, last_name, password, role, enabled, is_blocked) VALUES
    (5, 'passenger3@test.com', 'Elena', 'Nikolic', '$2a$12$eQDJydpvN0SQK9EgWqr1xuYTBzWO1IMmQZ9RBEXPXTbD8t6ZsnNOG', 'PASSENGER', true, false),
    (2, 'driver1@gmail.com', 'Nikola', 'Nikolic', '$2a$12$eQDJydpvN0SQK9EgWqr1xuYTBzWO1IMmQZ9RBEXPXTbD8t6ZsnNOG', 'DRIVER', true, false);

INSERT INTO passenger (id) VALUES (5);

-- 3. create vehicle and driver
INSERT INTO vehicle (id, model, license_plate, current_lat, current_lng, vehicle_type, passenger_capacity, baby_transport, pet_transport, current_route_index) VALUES
    (1, 'Tesla Model 3', 'NS-001-EL', 45.2675, 19.8338, 'STANDARD', 4, true, false, 0);

INSERT INTO drivers (id, vehicle_id, is_active, average_rating) VALUES
    (2, 1, true, 5.0);

-- 4. pricing table
INSERT INTO vehicle_pricing (id, base_price, vehicle_type) VALUES
                                                               (1, 250.0, 'STANDARD'),
                                                               (2, 500.0, 'LUXURY'),
                                                               (3, 400.0, 'VAN');

-- 5. create ride (it will be added to favourite routes)
INSERT INTO ride (id, driver_id, status, total_price, distance_km, babies_transport, pets_transport, vehicle_type, created_at, started_at, finished_at, estimated_time, panic) VALUES
    (500, 2, 'COMPLETED', 450.0, 3.5, false, false, 'STANDARD',
     CURRENT_TIMESTAMP - INTERVAL '1' DAY,
     CURRENT_TIMESTAMP - INTERVAL '23' HOUR,
     CURRENT_TIMESTAMP - INTERVAL '22' HOUR,
     7.5, false);

INSERT INTO ride_passengers (rides_id, passengers_id) VALUES
    (500, 5);

-- 6. add ride to favorite
INSERT INTO passenger_favourite_rides (passenger_id, favourite_rides_id) VALUES
    (5, 500);

INSERT INTO ride_stop (id, address, latitude, longitude, order_index, ride_id) VALUES
    (501, 'Bulevar Oslobodjenja 104, Novi Sad', 45.2675, 19.8338, 0, 500),
    (502, 'Strazilovska 28, Novi Sad', 45.2512, 19.8366, 1, 500);

SELECT setval('users_id_seq', (SELECT max(id) FROM users));
SELECT setval('vehicle_id_seq', (SELECT max(id) FROM vehicle));
SELECT setval('ride_id_seq', (SELECT max(id) FROM ride));
SELECT setval('ride_stop_id_seq', (SELECT max(id) FROM ride_stop));
SELECT setval('vehicle_pricing_id_seq', (SELECT max(id) FROM vehicle_pricing));

COMMIT;