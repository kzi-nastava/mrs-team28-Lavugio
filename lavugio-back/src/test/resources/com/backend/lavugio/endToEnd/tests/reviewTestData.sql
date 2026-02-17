-- ====================================
-- LAVUGIO - H2 Init Script
-- ====================================

-- ====================================
-- 1. ADDRESSES
-- ====================================
INSERT INTO addresses (id, street_name, city, country, street_number, zip_code, longitude, latitude) VALUES
                                                                                                         (1, 'Bulevar kralja Aleksandra', 'Beograd', 'Srbija', '73', 11000, 20.4489, 44.8020),
                                                                                                         (2, 'Knez Mihailova', 'Beograd', 'Srbija', '12', 11000, 20.4572, 44.8176),
                                                                                                         (3, 'Njegoševa', 'Beograd', 'Srbija', '45', 11000, 20.4681, 44.8073),
                                                                                                         (4, 'Makedonska', 'Beograd', 'Srbija', '22', 11000, 20.4784, 44.8064),
                                                                                                         (5, 'Cara Dušana', 'Novi Sad', 'Srbija', '15', 21000, 19.8335, 45.2551),
                                                                                                         (6, 'Zmaj Jovina', 'Novi Sad', 'Srbija', '8', 21000, 19.8451, 45.2556),
                                                                                                         (7, 'Niška', 'Niš', 'Srbija', '33', 18000, 21.8958, 43.3209),
                                                                                                         (8, 'Vozda Karađorđa', 'Niš', 'Srbija', '12', 18000, 21.9028, 43.3192),
                                                                                                         (9, 'Kralja Petra', 'Kragujevac', 'Srbija', '55', 34000, 20.9111, 44.0125),
                                                                                                         (10, 'Svetozara Markovića', 'Kragujevac', 'Srbija', '7', 34000, 20.9142, 44.0145),
                                                                                                         (11, 'Terazije', 'Beograd', 'Srbija', '1', 11000, 20.4612, 44.8125),
                                                                                                         (12, 'Trg Republike', 'Beograd', 'Srbija', '5', 11000, 20.4598, 44.8170),
                                                                                                         (13, 'Skadarlija', 'Beograd', 'Srbija', '29', 11000, 20.4642, 44.8189),
                                                                                                         (14, 'Bulevar Oslobođenja', 'Novi Sad', 'Srbija', '80', 21000, 19.8369, 45.2504),
                                                                                                         (15, 'Dunavska', 'Novi Sad', 'Srbija', '27', 21000, 19.8440, 45.2569);

-- ====================================
-- 2. VEHICLES
-- ====================================
INSERT INTO vehicles (id, make, model, license_plate, passenger_seats, pet_friendly, baby_friendly, color, type) VALUES
                                                                                                                     (1, 'Volkswagen', 'Golf 7', 'BG-123-AB', 5, true, true, 'Siva', 'STANDARD'),
                                                                                                                     (2, 'Mercedes', 'E-Class', 'BG-456-CD', 5, false, true, 'Crna', 'LUXURY'),
                                                                                                                     (3, 'Škoda', 'Octavia Combi', 'NS-789-EF', 5, true, true, 'Bela', 'COMBI'),
                                                                                                                     (4, 'BMW', '5 Series', 'BG-111-GH', 5, false, true, 'Plava', 'LUXURY'),
                                                                                                                     (5, 'Toyota', 'Corolla', 'NI-222-IJ', 5, true, false, 'Crvena', 'STANDARD'),
                                                                                                                     (6, 'Audi', 'A6 Avant', 'BG-333-KL', 5, true, true, 'Siva', 'COMBI'),
                                                                                                                     (7, 'Volkswagen', 'Passat', 'KG-444-MN', 5, false, true, 'Crna', 'STANDARD'),
                                                                                                                     (8, 'Mercedes', 'S-Class', 'BG-555-OP', 5, false, true, 'Bela', 'LUXURY');

-- ====================================
-- 3. ACCOUNTS
-- ====================================
INSERT INTO accounts (id, name, last_name, email, password, profile_photo_path, phone_number, address, email_verified) VALUES
                                                                                                                           (1, 'Marko', 'Marković', 'marko.markovic@gmail.com', '$2a$12$DvYeLDvQeXaDerGFoypiBOZGyiuAXeDYVQjjNF1..krm7fUAEHoOS', '/photos/marko.jpg', '+381641234567', 'Bulevar kralja Aleksandra 73', true),
                                                                                                                           (2, 'Ana', 'Anić', 'ana.anic@gmail.com', '$2a$12$DvYeLDvQeXaDerGFoypiBOZGyiuAXeDYVQjjNF1..krm7fUAEHoOS', '/photos/ana.jpg', '+381642345678', 'Knez Mihailova 12', true),
                                                                                                                           (3, 'Nikola', 'Nikolić', 'nikola.nikolic@gmail.com', '$2a$12$DvYeLDvQeXaDerGFoypiBOZGyiuAXeDYVQjjNF1..krm7fUAEHoOS', '/photos/nikola.jpg', '+381643456789', 'Njegoševa 45', true),
                                                                                                                           (4, 'Jelena', 'Jovanović', 'jelena.jovanovic@gmail.com', '$2a$12$DvYeLDvQeXaDerGFoypiBOZGyiuAXeDYVQjjNF1..krm7fUAEHoOS', '/photos/jelena.jpg', '+381644567890', 'Makedonska 22', true),
                                                                                                                           (5, 'Stefan', 'Stefanović', 'stefan.stefanovic@gmail.com', '$2a$12$DvYeLDvQeXaDerGFoypiBOZGyiuAXeDYVQjjNF1..krm7fUAEHoOS', '/photos/stefan.jpg', '+381645678901', 'Cara Dušana 15', true),
                                                                                                                           (6, 'Milica', 'Milić', 'milica.milic@gmail.com', '$2a$12$DvYeLDvQeXaDerGFoypiBOZGyiuAXeDYVQjjNF1..krm7fUAEHoOS', '/photos/milica.jpg', '+381646789012', 'Zmaj Jovina 8', true),
                                                                                                                           (7, 'Đorđe', 'Đorđević', 'djordje.djordjevic@gmail.com', '$2a$12$DvYeLDvQeXaDerGFoypiBOZGyiuAXeDYVQjjNF1..krm7fUAEHoOS', '/photos/djordje.jpg', '+381647890123', 'Niška 33', true),
                                                                                                                           (8, 'Ivana', 'Ivanović', 'ivana.ivanovic@gmail.com', '$2a$12$DvYeLDvQeXaDerGFoypiBOZGyiuAXeDYVQjjNF1..krm7fUAEHoOS', '/photos/ivana.jpg', '+381648901234', 'Vozda Karađorđa 12', true),
                                                                                                                           (9, 'Petar', 'Petrović', 'petar.petrovic@gmail.com', '$2a$12$DvYeLDvQeXaDerGFoypiBOZGyiuAXeDYVQjjNF1..krm7fUAEHoOS', '/photos/petar.jpg', '+381649012345', 'Kralja Petra 55', true),
                                                                                                                           (10, 'Jovana', 'Jovanović', 'jovana.jovanovic@gmail.com', '$2a$12$DvYeLDvQeXaDerGFoypiBOZGyiuAXeDYVQjjNF1..krm7fUAEHoOS', '/photos/jovana.jpg', '+381640123456', 'Svetozara Markovića 7', true),
                                                                                                                           (11, 'Admin', 'Adminović', 'admin@lavugio.com', '$2a$12$DvYeLDvQeXaDerGFoypiBOZGyiuAXeDYVQjjNF1..krm7fUAEHoOS', '/photos/admin.jpg', '+381611111111', 'Bulevar Nikole Tesle 1', true),
                                                                                                                           (12, 'Test', 'Testović', 'test@gmail.com', '$2a$12$DvYeLDvQeXaDerGFoypiBOZGyiuAXeDYVQjjNF1..krm7fUAEHoOS', '/photos/test.jpg', '+381650000000', 'Terazije 1', true);

-- ====================================
-- 4. ADMINISTRATORS
-- ====================================
INSERT INTO administrators (id) VALUES (11);

-- ====================================
-- 5. BLOCKABLE_ACCOUNTS
-- ====================================
INSERT INTO blockable_accounts (id, blocked, block_reason) VALUES
                                                               (1, false, NULL),
                                                               (2, false, NULL),
                                                               (3, false, NULL),
                                                               (4, false, NULL),
                                                               (5, false, NULL),
                                                               (6, true, 'Neprikladno ponašanje prema vozačima'),
                                                               (7, false, NULL),
                                                               (8, false, NULL),
                                                               (9, false, NULL),
                                                               (10, false, NULL),
                                                               (12, false, NULL);

-- ====================================
-- 6. REGULAR_USERS
-- ====================================
INSERT INTO regular_users (id, can_order) VALUES
                                              (1, true),
                                              (2, true),
                                              (3, true),
                                              (4, true),
                                              (6, false),
                                              (9, true),
                                              (10, true),
                                              (12, true);

-- ====================================
-- 7. DRIVERS
-- ====================================
INSERT INTO drivers (id, is_driving, vehicle_id, is_active) VALUES
                                                                (5, false, 1, true),
                                                                (7, true, 2, true),
                                                                (8, false, 3, true);

-- ====================================
-- 8. DRIVER_REGISTRATION_TOKEN
-- ====================================
INSERT INTO driver_registration_token (id, token, driver_id, created_at, expires_at, used) VALUES
                                                                                               (1, 'token-abc123-driver5', 5, '2025-01-20 10:00:00', '2025-01-21 10:00:00', true),
                                                                                               (2, 'token-def456-driver7', 7, '2025-01-21 14:30:00', '2025-01-22 14:30:00', true),
                                                                                               (3, 'token-ghi789-driver8', 8, '2025-01-22 09:15:00', '2025-01-23 09:15:00', false);

-- ====================================
-- 9. RIDES - Sample vožnji
-- ====================================
-- Koristimo eksplicitne INSERT-e umesto generate_series jer H2 ne podržava tu funkciju

-- Vožnje 1-20 (završene, vozač 5)
INSERT INTO rides (id, driver_id, user_id, start_date_time, end_date_time, estimated_duration_seconds, price, distance, ride_status, has_panic) VALUES
                                                                                                                                                    (1, 5, 1, '2024-10-01 08:00:00', '2024-10-01 09:00:00', 2100, 450.50, 8.5, 'FINISHED', false),
                                                                                                                                                    (2, 5, 2, '2024-10-02 09:00:00', '2024-10-02 10:00:00', 2200, 520.75, 10.2, 'FINISHED', false),
                                                                                                                                                    (3, 5, 3, '2024-10-03 10:00:00', '2024-10-03 11:00:00', 1900, 380.00, 6.8, 'FINISHED', false),
                                                                                                                                                    (4, 5, 4, '2024-10-04 11:00:00', '2024-10-04 12:00:00', 2400, 600.00, 12.0, 'FINISHED', false),
                                                                                                                                                    (5, 5, 9, '2024-10-05 12:00:00', '2024-10-05 13:00:00', 2000, 420.00, 7.5, 'FINISHED', false),
                                                                                                                                                    (6, 5, 10, '2024-10-06 13:00:00', '2024-10-06 14:00:00', 2300, 550.00, 11.0, 'FINISHED', false),
                                                                                                                                                    (7, 5, 1, '2024-10-07 14:00:00', '2024-10-07 15:00:00', 2100, 480.00, 9.0, 'FINISHED', false),
                                                                                                                                                    (8, 5, 2, '2024-10-08 15:00:00', '2024-10-08 16:00:00', 1800, 360.00, 6.0, 'FINISHED', false),
                                                                                                                                                    (9, 5, 3, '2024-10-09 16:00:00', '2024-10-09 17:00:00', 2500, 620.00, 13.0, 'FINISHED', false),
                                                                                                                                                    (10, 5, 4, '2024-10-10 17:00:00', '2024-10-10 18:00:00', 2200, 500.00, 10.0, 'FINISHED', false),
                                                                                                                                                    (11, 5, 9, '2024-10-11 08:00:00', '2024-10-11 09:00:00', 2000, 430.00, 8.0, 'FINISHED', false),
                                                                                                                                                    (12, 5, 10, '2024-10-12 09:00:00', '2024-10-12 10:00:00', 2100, 470.00, 9.5, 'FINISHED', false),
                                                                                                                                                    (13, 5, 1, '2024-10-13 10:00:00', '2024-10-13 11:00:00', 1900, 390.00, 7.0, 'FINISHED', false),
                                                                                                                                                    (14, 5, 2, '2024-10-14 11:00:00', '2024-10-14 12:00:00', 2300, 540.00, 11.5, 'FINISHED', false),
                                                                                                                                                    (15, 5, 3, '2024-10-15 12:00:00', '2024-10-15 13:00:00', 2400, 580.00, 12.5, 'FINISHED', false),
                                                                                                                                                    (16, 5, 4, '2024-10-16 13:00:00', '2024-10-16 14:00:00', 2000, 440.00, 8.5, 'FINISHED', false),
                                                                                                                                                    (17, 5, 9, '2024-10-17 14:00:00', '2024-10-17 15:00:00', 2200, 510.00, 10.5, 'FINISHED', false),
                                                                                                                                                    (18, 5, 10, '2024-10-18 15:00:00', '2024-10-18 16:00:00', 2100, 490.00, 9.8, 'FINISHED', false),
                                                                                                                                                    (19, 5, 1, '2024-10-19 16:00:00', '2024-10-19 17:00:00', 1800, 370.00, 6.5, 'FINISHED', false),
                                                                                                                                                    (20, 5, 2, '2024-10-20 17:00:00', '2024-10-20 18:00:00', 2500, 610.00, 13.5, 'FINISHED', false);

-- Vožnje 21-50 (završene, vozač 5, sa panic na vožnji 50)
INSERT INTO rides (id, driver_id, user_id, start_date_time, end_date_time, estimated_duration_seconds, price, distance, ride_status, has_panic) VALUES
                                                                                                                                                    (21, 5, 3, '2024-10-21 08:00:00', '2024-10-21 09:00:00', 2000, 420.00, 8.0, 'FINISHED', false),
                                                                                                                                                    (22, 5, 4, '2024-10-22 09:00:00', '2024-10-22 10:00:00', 2100, 460.00, 9.0, 'FINISHED', false),
                                                                                                                                                    (23, 5, 9, '2024-10-23 10:00:00', '2024-10-23 11:00:00', 1900, 380.00, 7.2, 'FINISHED', false),
                                                                                                                                                    (24, 5, 10, '2024-10-24 11:00:00', '2024-10-24 12:00:00', 2300, 530.00, 11.0, 'FINISHED', false),
                                                                                                                                                    (25, 5, 1, '2024-10-25 12:00:00', '2024-10-25 13:00:00', 2200, 500.00, 10.3, 'FINISHED', false),
                                                                                                                                                    (26, 5, 2, '2024-10-26 13:00:00', '2024-10-26 14:00:00', 2400, 570.00, 12.0, 'FINISHED', false),
                                                                                                                                                    (27, 5, 3, '2024-10-27 14:00:00', '2024-10-27 15:00:00', 2000, 430.00, 8.5, 'FINISHED', false),
                                                                                                                                                    (28, 5, 4, '2024-10-28 15:00:00', '2024-10-28 16:00:00', 2100, 480.00, 9.5, 'FINISHED', false),
                                                                                                                                                    (29, 5, 9, '2024-10-29 16:00:00', '2024-10-29 17:00:00', 1800, 360.00, 6.8, 'FINISHED', false),
                                                                                                                                                    (30, 5, 10, '2024-10-30 17:00:00', '2024-10-30 18:00:00', 2500, 600.00, 13.0, 'FINISHED', false),
                                                                                                                                                    (31, 5, 1, '2024-10-31 08:00:00', '2024-10-31 09:00:00', 2200, 510.00, 10.5, 'FINISHED', false),
                                                                                                                                                    (32, 5, 2, '2024-11-01 09:00:00', '2024-11-01 10:00:00', 2000, 440.00, 8.8, 'FINISHED', false),
                                                                                                                                                    (33, 5, 3, '2024-11-02 10:00:00', '2024-11-02 11:00:00', 2300, 540.00, 11.2, 'FINISHED', false),
                                                                                                                                                    (34, 5, 4, '2024-11-03 11:00:00', '2024-11-03 12:00:00', 2100, 470.00, 9.3, 'FINISHED', false),
                                                                                                                                                    (35, 5, 9, '2024-11-04 12:00:00', '2024-11-04 13:00:00', 1900, 390.00, 7.5, 'FINISHED', false),
                                                                                                                                                    (36, 5, 10, '2024-11-05 13:00:00', '2024-11-05 14:00:00', 2400, 580.00, 12.5, 'FINISHED', false),
                                                                                                                                                    (37, 5, 1, '2024-11-06 14:00:00', '2024-11-06 15:00:00', 2200, 500.00, 10.0, 'FINISHED', false),
                                                                                                                                                    (38, 5, 2, '2024-11-07 15:00:00', '2024-11-07 16:00:00', 2000, 430.00, 8.3, 'FINISHED', false),
                                                                                                                                                    (39, 5, 3, '2024-11-08 16:00:00', '2024-11-08 17:00:00', 2100, 460.00, 9.0, 'FINISHED', false),
                                                                                                                                                    (40, 5, 4, '2024-11-09 17:00:00', '2024-11-09 18:00:00', 2300, 530.00, 11.0, 'FINISHED', false),
                                                                                                                                                    (41, 5, 9, '2024-11-10 08:00:00', '2024-11-10 09:00:00', 2000, 420.00, 8.0, 'FINISHED', false),
                                                                                                                                                    (42, 5, 10, '2024-11-11 09:00:00', '2024-11-11 10:00:00', 2200, 490.00, 9.8, 'FINISHED', false),
                                                                                                                                                    (43, 5, 1, '2024-11-12 10:00:00', '2024-11-12 11:00:00', 1800, 370.00, 6.5, 'FINISHED', false),
                                                                                                                                                    (44, 5, 2, '2024-11-13 11:00:00', '2024-11-13 12:00:00', 2400, 570.00, 12.0, 'FINISHED', false),
                                                                                                                                                    (45, 5, 3, '2024-11-14 12:00:00', '2024-11-14 13:00:00', 2100, 480.00, 9.5, 'FINISHED', false),
                                                                                                                                                    (46, 5, 4, '2024-11-15 13:00:00', '2024-11-15 14:00:00', 2000, 440.00, 8.5, 'FINISHED', false),
                                                                                                                                                    (47, 5, 9, '2024-11-16 14:00:00', '2024-11-16 15:00:00', 2300, 520.00, 10.5, 'FINISHED', false),
                                                                                                                                                    (48, 5, 10, '2024-11-17 15:00:00', '2024-11-17 16:00:00', 2200, 500.00, 10.0, 'FINISHED', false),
                                                                                                                                                    (49, 5, 1, '2024-11-18 16:00:00', '2024-11-18 17:00:00', 2100, 460.00, 9.0, 'FINISHED', false),
                                                                                                                                                    (50, 5, 9, '2024-11-19 17:00:00', '2024-11-19 18:00:00', 2000, 450.00, 8.7, 'FINISHED', true);

-- Vožnje 51-80 (završene, vozač 5)
INSERT INTO rides (id, driver_id, user_id, start_date_time, end_date_time, estimated_duration_seconds, price, distance, ride_status, has_panic) VALUES
                                                                                                                                                    (51, 5, 2, '2024-11-20 08:00:00', '2024-11-20 09:00:00', 2200, 510.00, 10.2, 'FINISHED', false),
                                                                                                                                                    (52, 5, 3, '2024-11-21 09:00:00', '2024-11-21 10:00:00', 1900, 390.00, 7.0, 'FINISHED', false),
                                                                                                                                                    (53, 5, 4, '2024-11-22 10:00:00', '2024-11-22 11:00:00', 2400, 580.00, 12.5, 'FINISHED', false),
                                                                                                                                                    (54, 5, 9, '2024-11-23 11:00:00', '2024-11-23 12:00:00', 2100, 470.00, 9.5, 'FINISHED', false),
                                                                                                                                                    (55, 5, 10, '2024-11-24 12:00:00', '2024-11-24 13:00:00', 2000, 430.00, 8.3, 'FINISHED', false),
                                                                                                                                                    (56, 5, 1, '2024-11-25 13:00:00', '2024-11-25 14:00:00', 2300, 540.00, 11.0, 'FINISHED', false),
                                                                                                                                                    (57, 5, 2, '2024-11-26 14:00:00', '2024-11-26 15:00:00', 2200, 500.00, 10.0, 'FINISHED', false),
                                                                                                                                                    (58, 5, 3, '2024-11-27 15:00:00', '2024-11-27 16:00:00', 2000, 420.00, 8.0, 'FINISHED', false),
                                                                                                                                                    (59, 5, 4, '2024-11-28 16:00:00', '2024-11-28 17:00:00', 2100, 480.00, 9.5, 'FINISHED', false),
                                                                                                                                                    (60, 5, 9, '2024-11-29 17:00:00', '2024-11-29 18:00:00', 1800, 370.00, 6.8, 'FINISHED', false),
                                                                                                                                                    (61, 5, 10, '2024-11-30 08:00:00', '2024-11-30 09:00:00', 2500, 610.00, 13.0, 'FINISHED', false),
                                                                                                                                                    (62, 5, 1, '2024-12-01 09:00:00', '2024-12-01 10:00:00', 2200, 490.00, 9.8, 'FINISHED', false),
                                                                                                                                                    (63, 5, 2, '2024-12-02 10:00:00', '2024-12-02 11:00:00', 2000, 440.00, 8.5, 'FINISHED', false),
                                                                                                                                                    (64, 5, 3, '2024-12-03 11:00:00', '2024-12-03 12:00:00', 2300, 530.00, 11.0, 'FINISHED', false),
                                                                                                                                                    (65, 5, 4, '2024-12-04 12:00:00', '2024-12-04 13:00:00', 2100, 460.00, 9.0, 'FINISHED', false),
                                                                                                                                                    (66, 5, 9, '2024-12-05 13:00:00', '2024-12-05 14:00:00', 1900, 380.00, 7.2, 'FINISHED', false),
                                                                                                                                                    (67, 5, 10, '2024-12-06 14:00:00', '2024-12-06 15:00:00', 2400, 570.00, 12.0, 'FINISHED', false),
                                                                                                                                                    (68, 5, 1, '2024-12-07 15:00:00', '2024-12-07 16:00:00', 2200, 500.00, 10.0, 'FINISHED', false),
                                                                                                                                                    (69, 5, 2, '2024-12-08 16:00:00', '2024-12-08 17:00:00', 2000, 430.00, 8.3, 'FINISHED', false),
                                                                                                                                                    (70, 5, 3, '2024-12-09 17:00:00', '2024-12-09 18:00:00', 2100, 470.00, 9.3, 'FINISHED', false),
                                                                                                                                                    (71, 5, 4, '2024-12-10 08:00:00', '2024-12-10 09:00:00', 2300, 540.00, 11.2, 'FINISHED', false),
                                                                                                                                                    (72, 5, 9, '2024-12-11 09:00:00', '2024-12-11 10:00:00', 2000, 420.00, 8.0, 'FINISHED', false),
                                                                                                                                                    (73, 5, 10, '2024-12-12 10:00:00', '2024-12-12 11:00:00', 2200, 510.00, 10.5, 'FINISHED', false),
                                                                                                                                                    (74, 5, 1, '2024-12-13 11:00:00', '2024-12-13 12:00:00', 1800, 360.00, 6.5, 'FINISHED', false),
                                                                                                                                                    (75, 5, 2, '2024-12-14 12:00:00', '2024-12-14 13:00:00', 2400, 580.00, 12.5, 'FINISHED', false),
                                                                                                                                                    (76, 5, 3, '2024-12-15 13:00:00', '2024-12-15 14:00:00', 2100, 480.00, 9.5, 'FINISHED', false),
                                                                                                                                                    (77, 5, 4, '2024-12-16 14:00:00', '2024-12-16 15:00:00', 2000, 440.00, 8.5, 'FINISHED', false),
                                                                                                                                                    (78, 5, 9, '2024-12-17 15:00:00', '2024-12-17 16:00:00', 2300, 520.00, 10.5, 'FINISHED', false),
                                                                                                                                                    (79, 5, 10, '2024-12-18 16:00:00', '2024-12-18 17:00:00', 2200, 500.00, 10.0, 'FINISHED', false),
                                                                                                                                                    (80, 5, 1, '2024-12-19 17:00:00', '2024-12-19 18:00:00', 2100, 460.00, 9.0, 'FINISHED', false);

-- Vožnje 81-100 (SCHEDULED, vozač 5)
INSERT INTO rides (id, driver_id, user_id, start_date_time, end_date_time, estimated_duration_seconds, price, distance, ride_status, has_panic) VALUES
                                                                                                                                                    (81, 5, 2, '2025-02-12 08:00:00', NULL, 2000, 450.00, 8.5, 'SCHEDULED', false),
                                                                                                                                                    (82, 5, 3, '2025-02-12 10:00:00', NULL, 2100, 480.00, 9.2, 'SCHEDULED', false),
                                                                                                                                                    (83, 5, 4, '2025-02-12 12:00:00', NULL, 1900, 390.00, 7.0, 'SCHEDULED', false),
                                                                                                                                                    (84, 5, 9, '2025-02-12 14:00:00', NULL, 2300, 530.00, 11.0, 'SCHEDULED', false),
                                                                                                                                                    (85, 5, 10, '2025-02-12 16:00:00', NULL, 2200, 500.00, 10.0, 'SCHEDULED', false),
                                                                                                                                                    (86, 5, 1, '2025-02-13 08:00:00', NULL, 2400, 570.00, 12.0, 'SCHEDULED', false),
                                                                                                                                                    (87, 5, 2, '2025-02-13 10:00:00', NULL, 2000, 430.00, 8.3, 'SCHEDULED', false),
                                                                                                                                                    (88, 5, 3, '2025-02-13 12:00:00', NULL, 2100, 460.00, 9.0, 'SCHEDULED', false),
                                                                                                                                                    (89, 5, 4, '2025-02-13 14:00:00', NULL, 2300, 540.00, 11.2, 'SCHEDULED', false),
                                                                                                                                                    (90, 5, 9, '2025-02-13 16:00:00', NULL, 2200, 510.00, 10.5, 'SCHEDULED', false),
                                                                                                                                                    (91, 5, 10, '2025-02-14 08:00:00', NULL, 2000, 420.00, 8.0, 'SCHEDULED', false),
                                                                                                                                                    (92, 5, 1, '2025-02-14 10:00:00', NULL, 2100, 470.00, 9.3, 'SCHEDULED', false),
                                                                                                                                                    (93, 5, 2, '2025-02-14 12:00:00', NULL, 1900, 380.00, 7.2, 'SCHEDULED', false),
                                                                                                                                                    (94, 5, 3, '2025-02-14 14:00:00', NULL, 2400, 580.00, 12.5, 'SCHEDULED', false),
                                                                                                                                                    (95, 5, 4, '2025-02-14 16:00:00', NULL, 2200, 500.00, 10.0, 'SCHEDULED', false),
                                                                                                                                                    (96, 5, 9, '2025-02-15 08:00:00', NULL, 2000, 440.00, 8.5, 'SCHEDULED', false),
                                                                                                                                                    (97, 5, 10, '2025-02-15 10:00:00', NULL, 2300, 520.00, 10.5, 'SCHEDULED', false),
                                                                                                                                                    (98, 5, 1, '2025-02-15 12:00:00', NULL, 2100, 480.00, 9.5, 'SCHEDULED', false),
                                                                                                                                                    (99, 5, 2, '2025-02-15 14:00:00', NULL, 2200, 510.00, 10.2, 'SCHEDULED', false),
                                                                                                                                                    (100, 5, 3, '2025-02-15 16:00:00', NULL, 2000, 430.00, 8.3, 'SCHEDULED', false);

-- Vožnje 101-110 (ACTIVE, vozač 5)
INSERT INTO rides (id, driver_id, user_id, start_date_time, end_date_time, estimated_duration_seconds, price, distance, ride_status, has_panic) VALUES
                                                                                                                                                    (101, 5, 4, '2025-02-11 20:00:00', NULL, 2100, 460.00, 9.0, 'ACTIVE', false),
                                                                                                                                                    (102, 5, 9, '2025-02-11 20:30:00', NULL, 1900, 390.00, 7.5, 'ACTIVE', false),
                                                                                                                                                    (103, 5, 10, '2025-02-11 21:00:00', NULL, 2400, 570.00, 12.0, 'ACTIVE', false),
                                                                                                                                                    (104, 5, 1, '2025-02-11 21:30:00', NULL, 2200, 500.00, 10.0, 'ACTIVE', false),
                                                                                                                                                    (105, 5, 2, '2025-02-11 22:00:00', NULL, 2000, 440.00, 8.5, 'ACTIVE', false),
                                                                                                                                                    (106, 5, 3, '2025-02-11 22:30:00', NULL, 2300, 530.00, 11.0, 'ACTIVE', false),
                                                                                                                                                    (107, 5, 4, '2025-02-11 23:00:00', NULL, 2100, 470.00, 9.3, 'ACTIVE', false),
                                                                                                                                                    (108, 5, 9, '2025-02-11 23:30:00', NULL, 2000, 420.00, 8.0, 'ACTIVE', false),
                                                                                                                                                    (109, 5, 10, '2025-02-12 00:00:00', NULL, 2200, 490.00, 9.8, 'ACTIVE', false),
                                                                                                                                                    (110, 5, 1, '2025-02-12 00:30:00', NULL, 2100, 480.00, 9.5, 'ACTIVE', false);

-- Vožnje 111-120 (CANCELLED, vozač 5)
INSERT INTO rides (id, driver_id, user_id, start_date_time, end_date_time, estimated_duration_seconds, price, distance, ride_status, has_panic) VALUES
                                                                                                                                                    (111, 5, 2, '2025-01-20 08:00:00', NULL, 2000, 430.00, 8.3, 'CANCELLED', false),
                                                                                                                                                    (112, 5, 3, '2025-01-21 09:00:00', NULL, 2300, 540.00, 11.2, 'CANCELLED', false),
                                                                                                                                                    (113, 5, 4, '2025-01-22 10:00:00', NULL, 2200, 510.00, 10.5, 'CANCELLED', false),
                                                                                                                                                    (114, 5, 9, '2025-01-23 11:00:00', NULL, 2100, 460.00, 9.0, 'CANCELLED', false),
                                                                                                                                                    (115, 5, 10, '2025-01-24 12:00:00', NULL, 1900, 380.00, 7.2, 'CANCELLED', false),
                                                                                                                                                    (116, 5, 1, '2025-01-25 13:00:00', NULL, 2400, 580.00, 12.5, 'CANCELLED', false),
                                                                                                                                                    (117, 5, 2, '2025-01-26 14:00:00', NULL, 2200, 500.00, 10.0, 'CANCELLED', false),
                                                                                                                                                    (118, 5, 3, '2025-01-27 15:00:00', NULL, 2000, 440.00, 8.5, 'CANCELLED', false),
                                                                                                                                                    (119, 5, 4, '2025-01-28 16:00:00', NULL, 2300, 520.00, 10.5, 'CANCELLED', false),
                                                                                                                                                    (120, 5, 9, '2025-01-29 17:00:00', NULL, 2100, 470.00, 9.3, 'CANCELLED', false);

-- Dodatne vožnje za ostale vozače
INSERT INTO rides (id, driver_id, user_id, start_date_time, end_date_time, estimated_duration_seconds, price, distance, ride_status, has_panic) VALUES
                                                                                                                                                    (121, 7, 2, '2024-11-16 14:00:00', '2024-11-16 14:45:00', 2700, 820.00, 12.5, 'FINISHED', false),
                                                                                                                                                    (122, 8, 3, '2024-12-17 10:15:00', '2024-12-17 11:00:00', 2700, 650.00, 8.3, 'FINISHED', false),
                                                                                                                                                    (123, 7, 1, '2025-01-19 09:00:00', '2025-01-19 09:40:00', 2400, 720.00, 10.2, 'FINISHED', false),
                                                                                                                                                    (124, 8, 2, '2025-01-20 12:00:00', '2025-01-20 12:50:00', 3000, 980.00, 15.7, 'FINISHED', true),
                                                                                                                                                    (125, 7, 10, '2025-01-23 10:00:00', NULL, 1800, 500.00, 6.0, 'SCHEDULED', false),
                                                                                                                                                    (126, NULL, 3, '2025-01-24 14:00:00', NULL, 2100, 700.00, 9.0, 'SCHEDULED', false);

-- Vožnje za test korisnika (ID 12)
INSERT INTO rides (id, driver_id, user_id, start_date_time, end_date_time, estimated_duration_seconds, price, distance, ride_status, has_panic) VALUES
                                                                                                                                                    (127, 5, 12, '2026-02-05 10:00:00', '2026-02-05 10:35:00', 2100, 550.00, 7.5, 'FINISHED', false),
                                                                                                                                                    (128, 7, 12, '2026-02-09 14:30:00', '2026-02-09 15:15:00', 2700, 680.00, 9.2, 'FINISHED', false),
                                                                                                                                                    (129, 8, 12, '2026-02-10 16:00:00', NULL, 1800, 450.00, 5.5, 'CANCELLED', false);

-- ====================================
-- 10. RIDE_PASSENGERS
-- ====================================
INSERT INTO ride_passengers (ride_id, user_id)
SELECT id, user_id FROM rides WHERE id <= 120;

INSERT INTO ride_passengers (ride_id, user_id) VALUES
                                                   (121, 2), (122, 3), (123, 1), (124, 2), (124, 3),
                                                   (125, 10), (126, 3), (127, 12), (128, 12), (129, 12);

-- ====================================
-- 11. RIDE_DESTINATIONS
-- ====================================
-- Destinacije za sve vožnje (1-120) - po 2 destinacije
INSERT INTO ride_destinations (ride_id, address_id, destination_order) VALUES
                                                                           (1, 1, 0), (1, 2, 1), (2, 3, 0), (2, 4, 1), (3, 5, 0), (3, 6, 1),
                                                                           (4, 7, 0), (4, 8, 1), (5, 9, 0), (5, 10, 1), (6, 11, 0), (6, 12, 1),
                                                                           (7, 13, 0), (7, 14, 1), (8, 15, 0), (8, 1, 1), (9, 2, 0), (9, 3, 1),
                                                                           (10, 4, 0), (10, 5, 1), (11, 6, 0), (11, 7, 1), (12, 8, 0), (12, 9, 1),
                                                                           (13, 10, 0), (13, 11, 1), (14, 12, 0), (14, 13, 1), (15, 14, 0), (15, 15, 1),
                                                                           (16, 1, 0), (16, 2, 1), (17, 3, 0), (17, 4, 1), (18, 5, 0), (18, 6, 1),
                                                                           (19, 7, 0), (19, 8, 1), (20, 9, 0), (20, 10, 1), (21, 11, 0), (21, 12, 1),
                                                                           (22, 13, 0), (22, 14, 1), (23, 15, 0), (23, 1, 1), (24, 2, 0), (24, 3, 1),
                                                                           (25, 4, 0), (25, 5, 1), (26, 6, 0), (26, 7, 1), (27, 8, 0), (27, 9, 1),
                                                                           (28, 10, 0), (28, 11, 1), (29, 12, 0), (29, 13, 1), (30, 14, 0), (30, 15, 1),
                                                                           (31, 1, 0), (31, 2, 1), (32, 3, 0), (32, 4, 1), (33, 5, 0), (33, 6, 1),
                                                                           (34, 7, 0), (34, 8, 1), (35, 9, 0), (35, 10, 1), (36, 11, 0), (36, 12, 1),
                                                                           (37, 13, 0), (37, 14, 1), (38, 15, 0), (38, 1, 1), (39, 2, 0), (39, 3, 1),
                                                                           (40, 4, 0), (40, 5, 1), (41, 6, 0), (41, 7, 1), (42, 8, 0), (42, 9, 1),
                                                                           (43, 10, 0), (43, 11, 1), (44, 12, 0), (44, 13, 1), (45, 14, 0), (45, 15, 1),
                                                                           (46, 1, 0), (46, 2, 1), (47, 3, 0), (47, 4, 1), (48, 5, 0), (48, 6, 1),
                                                                           (49, 7, 0), (49, 8, 1), (50, 9, 0), (50, 10, 1), (51, 11, 0), (51, 12, 1),
                                                                           (52, 13, 0), (52, 14, 1), (53, 15, 0), (53, 1, 1), (54, 2, 0), (54, 3, 1),
                                                                           (55, 4, 0), (55, 5, 1), (56, 6, 0), (56, 7, 1), (57, 8, 0), (57, 9, 1),
                                                                           (58, 10, 0), (58, 11, 1), (59, 12, 0), (59, 13, 1), (60, 14, 0), (60, 15, 1),
                                                                           (61, 1, 0), (61, 2, 1), (62, 3, 0), (62, 4, 1), (63, 5, 0), (63, 6, 1),
                                                                           (64, 7, 0), (64, 8, 1), (65, 9, 0), (65, 10, 1), (66, 11, 0), (66, 12, 1),
                                                                           (67, 13, 0), (67, 14, 1), (68, 15, 0), (68, 1, 1), (69, 2, 0), (69, 3, 1),
                                                                           (70, 4, 0), (70, 5, 1), (71, 6, 0), (71, 7, 1), (72, 8, 0), (72, 9, 1),
                                                                           (73, 10, 0), (73, 11, 1), (74, 12, 0), (74, 13, 1), (75, 14, 0), (75, 15, 1),
                                                                           (76, 1, 0), (76, 2, 1), (77, 3, 0), (77, 4, 1), (78, 5, 0), (78, 6, 1),
                                                                           (79, 7, 0), (79, 8, 1), (80, 9, 0), (80, 10, 1), (81, 11, 0), (81, 12, 1),
                                                                           (82, 13, 0), (82, 14, 1), (83, 15, 0), (83, 1, 1), (84, 2, 0), (84, 3, 1),
                                                                           (85, 4, 0), (85, 5, 1), (86, 6, 0), (86, 7, 1), (87, 8, 0), (87, 9, 1),
                                                                           (88, 10, 0), (88, 11, 1), (89, 12, 0), (89, 13, 1), (90, 14, 0), (90, 15, 1),
                                                                           (91, 1, 0), (91, 2, 1), (92, 3, 0), (92, 4, 1), (93, 5, 0), (93, 6, 1),
                                                                           (94, 7, 0), (94, 8, 1), (95, 9, 0), (95, 10, 1), (96, 11, 0), (96, 12, 1),
                                                                           (97, 13, 0), (97, 14, 1), (98, 15, 0), (98, 1, 1), (99, 2, 0), (99, 3, 1),
                                                                           (100, 4, 0), (100, 5, 1), (101, 6, 0), (101, 7, 1), (102, 8, 0), (102, 9, 1),
                                                                           (103, 10, 0), (103, 11, 1), (104, 12, 0), (104, 13, 1), (105, 14, 0), (105, 15, 1),
                                                                           (106, 1, 0), (106, 2, 1), (107, 3, 0), (107, 4, 1), (108, 5, 0), (108, 6, 1),
                                                                           (109, 7, 0), (109, 8, 1), (110, 9, 0), (110, 10, 1), (111, 11, 0), (111, 12, 1),
                                                                           (112, 13, 0), (112, 14, 1), (113, 15, 0), (113, 1, 1), (114, 2, 0), (114, 3, 1),
                                                                           (115, 4, 0), (115, 5, 1), (116, 6, 0), (116, 7, 1), (117, 8, 0), (117, 9, 1),
                                                                           (118, 10, 0), (118, 11, 1), (119, 12, 0), (119, 13, 1), (120, 14, 0), (120, 15, 1);

-- Destinacije za ostale vožnje (121-129)
INSERT INTO ride_destinations (ride_id, address_id, destination_order) VALUES
                                                                           (121, 3, 0), (121, 4, 1),
                                                                           (122, 5, 0), (122, 6, 1),
                                                                           (123, 1, 0), (123, 3, 1),
                                                                           (124, 2, 0), (124, 4, 1), (124, 5, 2),
                                                                           (125, 1, 0), (125, 2, 1),
                                                                           (126, 3, 0), (126, 4, 1),
                                                                           (127, 1, 0), (127, 2, 1),
                                                                           (128, 3, 0), (128, 5, 1),
                                                                           (129, 11, 0), (129, 12, 1);

-- ====================================
-- 12. REVIEWS
-- ====================================
-- Reviews za prvih 50 završenih vožnji
INSERT INTO reviews (car_rating, driver_rating, comment, ride_id, user_id) VALUES
                                                                               (5, 5, 'Odlična vožnja, sve pohvale!', 1, 1),
                                                                               (4, 5, 'Vozač je bio izuzetno ljubazan.', 2, 2),
                                                                               (5, 4, 'Auto čist i udoban.', 3, 3),
                                                                               (4, 4, 'Prosečno iskustvo.', 4, 4),
                                                                               (5, 5, 'Sve je bilo u redu.', 5, 9),
                                                                               (5, 5, 'Odlična vožnja, sve pohvale!', 6, 10),
                                                                               (4, 5, 'Vozač je bio izuzetno ljubazan.', 7, 1),
                                                                               (5, 4, 'Auto čist i udoban.', 8, 2),
                                                                               (4, 4, 'Prosečno iskustvo.', 9, 3),
                                                                               (5, 5, 'Sve je bilo u redu.', 10, 4),
                                                                               (5, 5, 'Odlična vožnja, sve pohvale!', 11, 9),
                                                                               (4, 5, 'Vozač je bio izuzetno ljubazan.', 12, 10),
                                                                               (5, 4, 'Auto čist i udoban.', 13, 1),
                                                                               (4, 4, 'Prosečno iskustvo.', 14, 2),
                                                                               (5, 5, 'Sve je bilo u redu.', 15, 3),
                                                                               (5, 5, 'Odlična vožnja, sve pohvale!', 16, 4),
                                                                               (4, 5, 'Vozač je bio izuzetno ljubazan.', 17, 9),
                                                                               (5, 4, 'Auto čist i udoban.', 18, 10),
                                                                               (4, 4, 'Prosečno iskustvo.', 19, 1),
                                                                               (5, 5, 'Sve je bilo u redu.', 20, 2),
                                                                               (5, 5, 'Odlična vožnja, sve pohvale!', 21, 3),
                                                                               (4, 5, 'Vozač je bio izuzetno ljubazan.', 22, 4),
                                                                               (5, 4, 'Auto čist i udoban.', 23, 9),
                                                                               (4, 4, 'Prosečno iskustvo.', 24, 10),
                                                                               (5, 5, 'Sve je bilo u redu.', 25, 1),
                                                                               (5, 5, 'Odlična vožnja, sve pohvale!', 26, 2),
                                                                               (4, 5, 'Vozač je bio izuzetno ljubazan.', 27, 3),
                                                                               (5, 4, 'Auto čist i udoban.', 28, 4),
                                                                               (4, 4, 'Prosečno iskustvo.', 29, 9),
                                                                               (5, 5, 'Sve je bilo u redu.', 30, 10),
                                                                               (5, 5, 'Odlična vožnja, sve pohvale!', 31, 1),
                                                                               (4, 5, 'Vozač je bio izuzetno ljubazan.', 32, 2),
                                                                               (5, 4, 'Auto čist i udoban.', 33, 3),
                                                                               (4, 4, 'Prosečno iskustvo.', 34, 4),
                                                                               (5, 5, 'Sve je bilo u redu.', 35, 9),
                                                                               (5, 5, 'Odlična vožnja, sve pohvale!', 36, 10),
                                                                               (4, 5, 'Vozač je bio izuzetno ljubazan.', 37, 1),
                                                                               (5, 4, 'Auto čist i udoban.', 38, 2),
                                                                               (4, 4, 'Prosečno iskustvo.', 39, 3),
                                                                               (5, 5, 'Sve je bilo u redu.', 40, 4),
                                                                               (5, 5, 'Odlična vožnja, sve pohvale!', 41, 9),
                                                                               (4, 5, 'Vozač je bio izuzetno ljubazan.', 42, 10),
                                                                               (5, 4, 'Auto čist i udoban.', 43, 1),
                                                                               (4, 4, 'Prosečno iskustvo.', 44, 2),
                                                                               (5, 5, 'Sve je bilo u redu.', 45, 3),
                                                                               (5, 5, 'Odlična vožnja, sve pohvale!', 46, 4),
                                                                               (4, 5, 'Vozač je bio izuzetno ljubazan.', 47, 9),
                                                                               (5, 4, 'Auto čist i udoban.', 48, 10),
                                                                               (4, 4, 'Prosečno iskustvo.', 49, 1),
                                                                               (5, 5, 'Sve je bilo u redu.', 50, 9);

-- Dodatni reviews
INSERT INTO reviews (car_rating, driver_rating, comment, ride_id, user_id) VALUES
                                                                               (4, 5, 'Vozač je bio izuzetno ljubazan, ali auto malo stariji.', 121, 2),
                                                                               (5, 4, 'Auto fantastičan, vozač malo brzo vozi.', 122, 3),
                                                                               (5, 5, 'Perfektna vožnja, siguran i udoban prevoz!', 123, 1),
                                                                               (2, 2, 'Auto bio prljav, vozač neprijatan. Panično dugme aktivirano.', 124, 2);

-- ====================================
-- 13. RIDE_REPORTS
-- ====================================
INSERT INTO ride_reports (report_id, ride_id, report_message, account_id) VALUES
                                                                              (1, 124, 'Vozač je vozio prebrzo i ignorisao moje molbe da uspori. Osećao sam se ugroženo.', 2),
                                                                              (2, 50, 'Aktivirano panično dugme zbog neprijatne situacije.', 9);

-- ====================================
-- 14. FAVORITE_ROUTES
-- ====================================
INSERT INTO favorite_routes (name, user_id) VALUES
                                                ('Posao - Kuća', 1),
                                                ('Teretana - Kuća', 1),
                                                ('Fakultet - Stan', 2),
                                                ('Aerodrom - Hotel', 3),
                                                ('Centar - Kuća', 9);

-- ====================================
-- 15. FAVORITE_ROUTE_DESTINATIONS
-- ====================================
INSERT INTO favorite_route_destinations (favorite_route_id, address_id, destination_order) VALUES
                                                                                               (1, 1, 0), (1, 2, 1),
                                                                                               (2, 3, 0), (2, 1, 1),
                                                                                               (3, 4, 0), (3, 5, 1),
                                                                                               (4, 6, 0), (4, 7, 1),
                                                                                               (5, 2, 0), (5, 9, 1);

-- ====================================
-- 16. NOTIFICATIONS
-- ====================================
-- ====================================
-- 16. NOTIFICATIONS
-- ====================================
INSERT INTO notifications (title, text, link_to_ride, user_id, notification_type, sent_date, is_read) VALUES
                                                                                                          ('Vožnja završena', 'Vaša vožnja je uspešno završena. Hvala što koristite Lavugio!', '/rides/1', 1, 'REGULAR', '2024-10-01 09:00:00', true),
                                                                                                          ('Ocenite vožnju', 'Molimo vas da ocenite svoju poslednju vožnju.', '/rides/2', 2, 'REGULAR', '2024-10-02 14:45:00', true),
                                                                                                          ('PANIKA!', 'Korisnik je aktivirao panično dugme tokom vožnje #50', '/rides/50', 11, 'PANIC', '2024-11-19 12:35:00', true),
                                                                                                          ('Vožnja zakazana', 'Vaša vožnja je zakazana za sutra u 10:00.', '/rides/125', 10, 'LINKED', '2025-01-22 18:00:00', false),
                                                                                                          ('Novi vozač dostupan', 'Vaš zahtev za vožnju je prihvaćen. Vozač je na putu!', '/rides/105', 9, 'LINKED', '2025-01-15 15:05:00', true);
-- ====================================
-- 17. MESSAGES
-- ====================================
INSERT INTO messages (sender_id, receiver_id, timestamp, text, is_read) VALUES
                                                                            (1, 5, '2024-10-01 08:25:00', 'Gde ste? Čekam ispred zgrade.', true),
                                                                            (5, 1, '2024-10-01 08:26:00', 'Stigao sam, vidim vas!', true),
                                                                            (2, 7, '2024-11-16 13:55:00', 'Možete li me pokupiti 5 minuta ranije?', true),
                                                                            (7, 2, '2024-11-16 13:56:00', 'Naravno, već sam u blizini.', true),
                                                                            (9, 5, '2025-01-15 14:58:00', 'Koliko će još trajati do mene?', true),
                                                                            (5, 9, '2025-01-15 14:59:00', 'Stižem za 2 minuta!', true),
                                                                            (10, 7, '2025-01-22 17:55:00', 'Potvrdite vožnju za sutra.', false),
                                                                            (3, 11, '2025-01-20 12:40:00', 'Prijavljujem vozača zbog brzine.', true);