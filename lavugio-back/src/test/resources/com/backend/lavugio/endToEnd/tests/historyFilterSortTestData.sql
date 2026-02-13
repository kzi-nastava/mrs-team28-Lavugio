-- ====================================
-- LAVUGIO - E2E Test Data for History Filtering and Sorting Tests (Student 3)
-- Functionality 2.9.3: Filtriranje i sortiranje pregleda istorije
-- ====================================

-- ====================================
-- 0. CLEANUP - Delete existing test data (reverse order of foreign keys)
-- ====================================
DELETE FROM ride_passengers WHERE ride_id BETWEEN 1001 AND 1012;
DELETE FROM ride_destinations WHERE ride_id BETWEEN 1001 AND 1012;
DELETE FROM rides WHERE id BETWEEN 1001 AND 1012;
DELETE FROM drivers WHERE id = 102;
DELETE FROM regular_users WHERE id IN (101, 103);
DELETE FROM blockable_accounts WHERE id IN (101, 102, 103);
DELETE FROM accounts WHERE id IN (101, 102, 103);
DELETE FROM vehicles WHERE id = 101;
DELETE FROM addresses WHERE id BETWEEN 101 AND 106;

-- ====================================
-- 1. ADDRESSES
-- ====================================
INSERT INTO addresses (id, street_name, city, country, street_number, zip_code, longitude, latitude) VALUES
    (101, 'Bulevar kralja Aleksandra', 'Beograd', 'Srbija', '73', 11000, 20.4489, 44.8020),
    (102, 'Knez Mihailova', 'Beograd', 'Srbija', '12', 11000, 20.4572, 44.8176),
    (103, 'Zmaj Jovina', 'Novi Sad', 'Srbija', '8', 21000, 19.8451, 45.2556),
    (104, 'Cara Dušana', 'Novi Sad', 'Srbija', '15', 21000, 19.8335, 45.2551),
    (105, 'Terazije', 'Beograd', 'Srbija', '1', 11000, 20.4612, 44.8125),
    (106, 'Skadarlija', 'Beograd', 'Srbija', '29', 11000, 20.4642, 44.8189);

-- ====================================
-- 2. VEHICLES
-- ====================================
INSERT INTO vehicles (id, make, model, license_plate, passenger_seats, pet_friendly, baby_friendly, color, type) VALUES
    (101, 'Volkswagen', 'Golf 7', 'BG-E2E-01', 5, true, true, 'Siva', 'STANDARD');

-- ====================================
-- 3. ACCOUNTS - Test user for filtering tests
-- ====================================
INSERT INTO accounts (id, name, last_name, email, password, profile_photo_path, phone_number, address, email_verified) VALUES
    (101, 'Filter', 'TestUser', 'filtertest@gmail.com', '$2a$12$DvYeLDvQeXaDerGFoypiBOZGyiuAXeDYVQjjNF1..krm7fUAEHoOS', '/photos/test.jpg', '+381650000001', 'Terazije 1', true),
    (102, 'Driver', 'TestDriver', 'drivertest@gmail.com', '$2a$12$DvYeLDvQeXaDerGFoypiBOZGyiuAXeDYVQjjNF1..krm7fUAEHoOS', '/photos/driver.jpg', '+381650000002', 'Knez Mihailova 12', true),
    (103, 'Empty', 'HistoryUser', 'emptyhistory@gmail.com', '$2a$12$DvYeLDvQeXaDerGFoypiBOZGyiuAXeDYVQjjNF1..krm7fUAEHoOS', '/photos/empty.jpg', '+381650000003', 'Zmaj Jovina 8', true);

-- ====================================
-- 4. BLOCKABLE_ACCOUNTS
-- ====================================
INSERT INTO blockable_accounts (id, blocked, block_reason) VALUES
    (101, false, NULL),
    (102, false, NULL),
    (103, false, NULL);

-- ====================================
-- 5. REGULAR_USERS
-- ====================================
INSERT INTO regular_users (id, can_order) VALUES
    (101, true),
    (103, true);

-- ====================================
-- 6. DRIVERS
-- ====================================
INSERT INTO drivers (id, is_driving, vehicle_id, is_active) VALUES
    (102, false, 101, true);

-- ====================================
-- 7. RIDES - Test rides with different dates for filtering tests
-- Password for all test accounts: "perapera"
-- NOTE: Dates are set to January/February 2026 to minimize calendar navigation in tests
-- ====================================

-- Rides in January 2026 - Sorted by different pickup locations (alphabetically)
INSERT INTO rides (id, driver_id, user_id, start_date_time, end_date_time, estimated_duration_seconds, price, distance, ride_status, has_panic) VALUES
    (1001, 102, 101, '2026-01-05 08:00:00', '2026-01-05 08:30:00', 1800, 300.00, 5.0, 'FINISHED', false),
    (1002, 102, 101, '2026-01-10 10:00:00', '2026-01-10 10:45:00', 2700, 450.00, 8.0, 'FINISHED', false),
    (1003, 102, 101, '2026-01-15 14:00:00', '2026-01-15 14:30:00', 1800, 320.00, 5.5, 'FINISHED', false),
    (1004, 102, 101, '2026-01-20 16:00:00', '2026-01-20 16:40:00', 2400, 400.00, 7.0, 'FINISHED', false),
    (1005, 102, 101, '2026-01-25 09:00:00', '2026-01-25 09:35:00', 2100, 380.00, 6.5, 'FINISHED', false);

-- Rides in early February 2026 - Different dates for date range filtering
INSERT INTO rides (id, driver_id, user_id, start_date_time, end_date_time, estimated_duration_seconds, price, distance, ride_status, has_panic) VALUES
    (1006, 102, 101, '2026-02-01 07:00:00', '2026-02-01 07:30:00', 1800, 290.00, 4.8, 'FINISHED', false),
    (1007, 102, 101, '2026-02-02 11:00:00', '2026-02-02 11:40:00', 2400, 420.00, 7.2, 'FINISHED', false),
    (1008, 102, 101, '2026-02-03 15:00:00', '2026-02-03 15:35:00', 2100, 360.00, 6.0, 'FINISHED', false),
    (1009, 102, 101, '2026-02-04 18:00:00', '2026-02-04 18:45:00', 2700, 480.00, 8.5, 'FINISHED', false),
    (1010, 102, 101, '2026-02-05 12:00:00', '2026-02-05 12:30:00', 1800, 310.00, 5.2, 'FINISHED', false);

-- Rides in mid-February 2026 - More recent rides
INSERT INTO rides (id, driver_id, user_id, start_date_time, end_date_time, estimated_duration_seconds, price, distance, ride_status, has_panic) VALUES
    (1011, 102, 101, '2026-02-10 08:30:00', '2026-02-10 09:00:00', 1800, 330.00, 5.8, 'FINISHED', false),
    (1012, 102, 101, '2026-02-12 13:00:00', '2026-02-12 13:30:00', 1800, 340.00, 5.9, 'FINISHED', false);

-- ====================================
-- 8. RIDE_DESTINATIONS - Link rides to addresses (destination_order: 0=start, 1=end)
-- ====================================

-- January Rides
INSERT INTO ride_destinations (ride_id, address_id, destination_order) VALUES
    (1001, 101, 0),  -- Ride 1001 start: Bulevar kralja Aleksandra
    (1001, 102, 1),  -- Ride 1001 end: Knez Mihailova
    (1002, 103, 0),  -- Ride 1002 start: Zmaj Jovina
    (1002, 104, 1),  -- Ride 1002 end: Cara Dušana
    (1003, 105, 0),  -- Ride 1003 start: Terazije
    (1003, 106, 1),  -- Ride 1003 end: Skadarlija
    (1004, 102, 0),  -- Ride 1004 start: Knez Mihailova
    (1004, 105, 1),  -- Ride 1004 end: Terazije
    (1005, 104, 0),  -- Ride 1005 start: Cara Dušana
    (1005, 101, 1);  -- Ride 1005 end: Bulevar kralja Aleksandra

-- February Rides
INSERT INTO ride_destinations (ride_id, address_id, destination_order) VALUES
    (1006, 106, 0),  -- Ride 1006 start: Skadarlija
    (1006, 103, 1),  -- Ride 1006 end: Zmaj Jovina
    (1007, 101, 0),  -- Ride 1007 start: Bulevar kralja Aleksandra
    (1007, 104, 1),  -- Ride 1007 end: Cara Dušana
    (1008, 102, 0),  -- Ride 1008 start: Knez Mihailova
    (1008, 106, 1),  -- Ride 1008 end: Skadarlija
    (1009, 105, 0),  -- Ride 1009 start: Terazije
    (1009, 101, 1),  -- Ride 1009 end: Bulevar kralja Aleksandra
    (1010, 103, 0),  -- Ride 1010 start: Zmaj Jovina
    (1010, 102, 1);  -- Ride 1010 end: Knez Mihailova

-- March Rides
INSERT INTO ride_destinations (ride_id, address_id, destination_order) VALUES
    (1011, 104, 0),  -- Ride 1011 start: Cara Dušana
    (1011, 105, 1),  -- Ride 1011 end: Terazije
    (1012, 106, 0),  -- Ride 1012 start: Skadarlija
    (1012, 104, 1);  -- Ride 1012 end: Cara Dušana

-- ====================================
-- 9. RIDE_PASSENGERS - Link rides to passenger users
-- ====================================
INSERT INTO ride_passengers (ride_id, user_id) VALUES
    (1001, 101),
    (1002, 101),
    (1003, 101),
    (1004, 101),
    (1005, 101),
    (1006, 101),
    (1007, 101),
    (1008, 101),
    (1009, 101),
    (1010, 101),
    (1011, 101),
    (1012, 101);
