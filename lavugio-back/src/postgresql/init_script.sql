-- ====================================
-- LAVUGIO - PostgreSQL Init Script
-- ====================================

-- Čišćenje baze (opciono - obriši ako ne želiš)
-- DROP TABLE IF EXISTS ride_passengers CASCADE;
-- DROP TABLE IF EXISTS favorite_route_destinations CASCADE;
-- DROP TABLE IF EXISTS ride_destinations CASCADE;
-- DROP TABLE IF EXISTS ride_reports CASCADE;
-- DROP TABLE IF EXISTS reviews CASCADE;
-- DROP TABLE IF EXISTS messages CASCADE;
-- DROP TABLE IF EXISTS notifications CASCADE;
-- DROP TABLE IF EXISTS favorite_routes CASCADE;
-- DROP TABLE IF EXISTS rides CASCADE;
-- DROP TABLE IF EXISTS driver_registration_token CASCADE;
-- DROP TABLE IF EXISTS drivers CASCADE;
-- DROP TABLE IF EXISTS regular_users CASCADE;
-- DROP TABLE IF EXISTS blockable_accounts CASCADE;
-- DROP TABLE IF EXISTS administrators CASCADE;
-- DROP TABLE IF EXISTS accounts CASCADE;
-- DROP TABLE IF EXISTS vehicles CASCADE;
-- DROP TABLE IF EXISTS addresses CASCADE;

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
                                                                                                         (10, 'Svetozara Markovića', 'Kragujevac', 'Srbija', '7', 34000, 20.9142, 44.0145);

SELECT setval('addresses_id_seq', 10, true);

-- ====================================
-- 2. VEHICLES
-- ====================================
INSERT INTO vehicles (id, make, model, license_plate, seats_number, pet_friendly, baby_friendly, color, type) VALUES
                                                                                                                  (1, 'Volkswagen', 'Golf 7', 'BG-123-AB', 5, true, true, 'Siva', 'STANDARD'),
                                                                                                                  (2, 'Mercedes', 'E-Class', 'BG-456-CD', 5, false, true, 'Crna', 'LUXURY'),
                                                                                                                  (3, 'Škoda', 'Octavia Combi', 'NS-789-EF', 5, true, true, 'Bela', 'COMBI'),
                                                                                                                  (4, 'BMW', '5 Series', 'BG-111-GH', 5, false, true, 'Plava', 'LUXURY'),
                                                                                                                  (5, 'Toyota', 'Corolla', 'NI-222-IJ', 5, true, false, 'Crvena', 'STANDARD'),
                                                                                                                  (6, 'Audi', 'A6 Avant', 'BG-333-KL', 5, true, true, 'Siva', 'COMBI'),
                                                                                                                  (7, 'Volkswagen', 'Passat', 'KG-444-MN', 5, false, true, 'Crna', 'STANDARD'),
                                                                                                                  (8, 'Mercedes', 'S-Class', 'BG-555-OP', 5, false, true, 'Bela', 'LUXURY');

SELECT setval('vehicles_id_seq', 8, true);

-- ====================================
-- 3. ACCOUNTS (Base table)
-- ====================================
INSERT INTO accounts (id, name, last_name, email, password, profile_photo_path, phone_number, address, email_verified) VALUES
                                                                                                                           (1, 'Marko', 'Marković', 'marko.markovic@gmail.com', '$2a$10$abcdefghijklmnopqrstuv', '/photos/marko.jpg', '+381641234567', 'Bulevar kralja Aleksandra 73', true),
                                                                                                                           (2, 'Ana', 'Anić', 'ana.anic@gmail.com', '$2a$10$abcdefghijklmnopqrstuv', '/photos/ana.jpg', '+381642345678', 'Knez Mihailova 12', true),
                                                                                                                           (3, 'Nikola', 'Nikolić', 'nikola.nikolic@gmail.com', '$2a$10$abcdefghijklmnopqrstuv', '/photos/nikola.jpg', '+381643456789', 'Njegoševa 45', true),
                                                                                                                           (4, 'Jelena', 'Jovanović', 'jelena.jovanovic@gmail.com', '$2a$10$abcdefghijklmnopqrstuv', '/photos/jelena.jpg', '+381644567890', 'Makedonska 22', true),
                                                                                                                           (5, 'Stefan', 'Stefanović', 'stefan.stefanovic@gmail.com', '$2a$10$abcdefghijklmnopqrstuv', '/photos/stefan.jpg', '+381645678901', 'Cara Dušana 15', true),
                                                                                                                           (6, 'Milica', 'Milić', 'milica.milic@gmail.com', '$2a$10$abcdefghijklmnopqrstuv', '/photos/milica.jpg', '+381646789012', 'Zmaj Jovina 8', true),
                                                                                                                           (7, 'Đorđe', 'Đorđević', 'djordje.djordjevic@gmail.com', '$2a$10$abcdefghijklmnopqrstuv', '/photos/djordje.jpg', '+381647890123', 'Niška 33', true),
                                                                                                                           (8, 'Ivana', 'Ivanović', 'ivana.ivanovic@gmail.com', '$2a$10$abcdefghijklmnopqrstuv', '/photos/ivana.jpg', '+381648901234', 'Vozda Karađorđa 12', true),
                                                                                                                           (9, 'Petar', 'Petrović', 'petar.petrovic@gmail.com', '$2a$10$abcdefghijklmnopqrstuv', '/photos/petar.jpg', '+381649012345', 'Kralja Petra 55', true),
                                                                                                                           (10, 'Jovana', 'Jovanović', 'jovana.jovanovic@gmail.com', '$2a$10$abcdefghijklmnopqrstuv', '/photos/jovana.jpg', '+381640123456', 'Svetozara Markovića 7', true),
                                                                                                                           (11, 'Admin', 'Adminović', 'admin@lavugio.com', '$2a$10$abcdefghijklmnopqrstuv', '/photos/admin.jpg', '+381611111111', 'Bulevar Nikole Tesle 1', true);

SELECT setval('accounts_id_seq', 11, true);

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
                                                               (10, false, NULL);

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
                                              (10, true);

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

SELECT setval('driver_registration_token_id_seq', 3, true);

-- ====================================
-- 9. RIDES
-- ====================================
INSERT INTO rides (id, driver_id, user_id, start_date_time, end_date_time, price, distance, ride_status, has_panic) VALUES
                                                                                                                        (1, 5, 1, '2025-01-15 08:30:00', '2025-01-15 09:00:00', 450.00, 5.2, 'FINISHED', false),
                                                                                                                        (2, 7, 2, '2025-01-16 14:00:00', '2025-01-16 14:45:00', 820.00, 12.5, 'FINISHED', false),
                                                                                                                        (3, 8, 3, '2025-01-17 10:15:00', '2025-01-17 11:00:00', 650.00, 8.3, 'FINISHED', false),
                                                                                                                        (4, 5, 4, '2025-01-18 16:30:00', '2025-01-18 17:15:00', 550.00, 6.8, 'FINISHED', false),
                                                                                                                        (5, 7, 1, '2025-01-19 09:00:00', '2025-01-19 09:40:00', 720.00, 10.2, 'FINISHED', false),
                                                                                                                        (6, 8, 2, '2025-01-20 12:00:00', '2025-01-20 12:50:00', 980.00, 15.7, 'FINISHED', true),
                                                                                                                        (7, 5, 9, '2025-01-21 15:00:00', NULL, 600.00, 7.5, 'ACTIVE', false),
                                                                                                                        (8, 7, 10, '2025-01-23 10:00:00', NULL, 500.00, 6.0, 'SCHEDULED', false),
                                                                                                                        (9, NULL, 3, '2025-01-24 14:00:00', NULL, 700.00, 9.0, 'SCHEDULED', false),
                                                                                                                        (10, 5, 4, '2025-01-22 11:30:00', '2025-01-22 11:35:00', 300.00, 2.5, 'CANCELLED', false);

SELECT setval('rides_id_seq', 10, true);

-- ====================================
-- 10. RIDE_PASSENGERS (Many-to-Many)
-- ====================================
INSERT INTO ride_passengers (ride_id, user_id) VALUES
                                                   (1, 1),
                                                   (2, 2),
                                                   (3, 3),
                                                   (4, 4),
                                                   (5, 1),
                                                   (6, 2),
                                                   (6, 3), -- Ride 6 ima 2 putnika
                                                   (7, 9),
                                                   (8, 10),
                                                   (9, 3),
                                                   (10, 4);

-- ====================================
-- 11. RIDE_DESTINATIONS
-- ====================================
INSERT INTO ride_destinations (id, ride_id, address_id, destination_order) VALUES
                                                                               (1, 1, 1, 0),
                                                                               (2, 1, 2, 1),
                                                                               (3, 2, 3, 0),
                                                                               (4, 2, 4, 1),
                                                                               (5, 3, 5, 0),
                                                                               (6, 3, 6, 1),
                                                                               (7, 4, 7, 0),
                                                                               (8, 4, 8, 1),
                                                                               (9, 5, 1, 0),
                                                                               (10, 5, 3, 1),
                                                                               (11, 6, 2, 0),
                                                                               (12, 6, 4, 1),
                                                                               (13, 6, 5, 2), -- Ride 6 ima 3 destinacije
                                                                               (14, 7, 9, 0),
                                                                               (15, 7, 10, 1),
                                                                               (16, 8, 1, 0),
                                                                               (17, 8, 2, 1),
                                                                               (18, 9, 3, 0),
                                                                               (19, 9, 4, 1),
                                                                               (20, 10, 5, 0),
                                                                               (21, 10, 6, 1);

SELECT setval('ride_destinations_id_seq', 21, true);

-- ====================================
-- 12. REVIEWS
-- ====================================
INSERT INTO reviews (id, car_rating, driver_rating, comment, ride_id, user_id) VALUES
                                                                                   (1, 5, 5, 'Odlična vožnja, sve pohvale!', 1, 1),
                                                                                   (2, 4, 5, 'Vozač je bio izuzetno ljubazan, ali auto malo stariji.', 2, 2),
                                                                                   (3, 5, 4, 'Auto fantastičan, vozač malo brzo vozi.', 3, 3),
                                                                                   (4, 3, 3, 'Prosečno iskustvo, ništa posebno.', 4, 4),
                                                                                   (5, 5, 5, 'Perfektna vožnja, siguran i udoban prevoz!', 5, 1),
                                                                                   (6, 2, 2, 'Auto bio prljav, vozač neprijatan. Panično dugme aktivirano.', 6, 2);

SELECT setval('reviews_id_seq', 6, true);

-- ====================================
-- 13. RIDE_REPORTS
-- ====================================
INSERT INTO ride_reports (report_id, ride_id, report_message, account_id) VALUES
                                                                              (1, 6, 'Vozač je vozio prebrzo i ignorisao moje molbe da uspori. Osećao sam se ugroženo.', 2),
                                                                              (2, 4, 'Auto je bio nečist i imao je neugodan miris.', 4);

-- ride_reports koristi SEQUENCE umesto IDENTITY, pa ne treba setval
-- SELECT setval('ride_reports_report_id_seq', 2, true);

-- ====================================
-- 14. FAVORITE_ROUTES
-- ====================================
INSERT INTO favorite_routes (id, name, user_id) VALUES
                                                    (1, 'Posao - Kuća', 1),
                                                    (2, 'Teretana - Kuća', 1),
                                                    (3, 'Fakultet - Stan', 2),
                                                    (4, 'Aerodrom - Hotel', 3),
                                                    (5, 'Centar - Kuća', 9);

SELECT setval('favorite_routes_id_seq', 5, true);

-- ====================================
-- 15. FAVORITE_ROUTE_DESTINATIONS
-- ====================================
INSERT INTO favorite_route_destinations (id, favorite_route_id, address_id, destination_order) VALUES
                                                                                                   (1, 1, 1, 0),
                                                                                                   (2, 1, 2, 1),
                                                                                                   (3, 2, 3, 0),
                                                                                                   (4, 2, 1, 1),
                                                                                                   (5, 3, 4, 0),
                                                                                                   (6, 3, 5, 1),
                                                                                                   (7, 4, 6, 0),
                                                                                                   (8, 4, 7, 1),
                                                                                                   (9, 5, 2, 0),
                                                                                                   (10, 5, 9, 1);

SELECT setval('favorite_route_destinations_id_seq', 10, true);

-- ====================================
-- 16. NOTIFICATIONS
-- ====================================
INSERT INTO notifications (id, title, text, link_to_ride, user_id, notification_type, sent_date, sent_time, is_read) VALUES
                                                                                                                         (1, 'Vožnja završena', 'Vaša vožnja je uspešno završena. Hvala što koristite Lavugio!', '/rides/1', 1, 'REGULAR', '2025-01-15', '09:00:00', true),
                                                                                                                         (2, 'Ocenite vožnju', 'Molimo vas da ocenite svoju poslednju vožnju.', '/rides/2', 2, 'REGULAR', '2025-01-16', '14:45:00', true),
                                                                                                                         (3, 'PANIKA!', 'Korisnik je aktivirao panično dugme tokom vožnje #6', '/rides/6', 11, 'PANIC', '2025-01-20', '12:35:00', true),
                                                                                                                         (4, 'Vožnja zakazana', 'Vaša vožnja je zakazana za sutra u 10:00.', '/rides/8', 10, 'LINKED', '2025-01-22', '18:00:00', false),
                                                                                                                         (5, 'Novi vozač dostupan', 'Vaš zahtev za vožnju je prihvaćen. Vozač je na putu!', '/rides/7', 9, 'LINKED', '2025-01-21', '15:05:00', true);

SELECT setval('notifications_id_seq', 5, true);

-- ====================================
-- 17. MESSAGES
-- ====================================
INSERT INTO messages (id, sender_id, receiver_id, message_date, message_time, text, is_read) VALUES
                                                                                                 (1, 1, 5, '2025-01-15', '08:25:00', 'Gde ste? Čekam ispred zgrade.', true),
                                                                                                 (2, 5, 1, '2025-01-15', '08:26:00', 'Stigao sam, vidim vas!', true),
                                                                                                 (3, 2, 7, '2025-01-16', '13:55:00', 'Možete li me pokupiti 5 minuta ranije?', true),
                                                                                                 (4, 7, 2, '2025-01-16', '13:56:00', 'Naravno, već sam u blizini.', true),
                                                                                                 (5, 9, 5, '2025-01-21', '14:58:00', 'Koliko će još trajati do mene?', true),
                                                                                                 (6, 5, 9, '2025-01-21', '14:59:00', 'Stižem za 2 minuta!', true),
                                                                                                 (7, 10, 7, '2025-01-22', '17:55:00', 'Potvrdite vožnju za sutra.', false),
                                                                                                 (8, 3, 11, '2025-01-20', '12:40:00', 'Prijavljujem vozača zbog brzine.', true);

SELECT setval('messages_id_seq', 8, true);

-- ====================================
-- ZAVRŠENO!
-- ====================================

-- Provera podataka
SELECT 'Addresses' as table_name, COUNT(*) as count FROM addresses
UNION ALL
SELECT 'Vehicles', COUNT(*) FROM vehicles
UNION ALL
SELECT 'Accounts', COUNT(*) FROM accounts
UNION ALL
SELECT 'Regular Users', COUNT(*) FROM regular_users
UNION ALL
SELECT 'Drivers', COUNT(*) FROM drivers
UNION ALL
SELECT 'Administrators', COUNT(*) FROM administrators
UNION ALL
SELECT 'Rides', COUNT(*) FROM rides
UNION ALL
SELECT 'Reviews', COUNT(*) FROM reviews
UNION ALL
SELECT 'Messages', COUNT(*) FROM messages
UNION ALL
SELECT 'Notifications', COUNT(*) FROM notifications
UNION ALL
SELECT 'Favorite Routes', COUNT(*) FROM favorite_routes;