-- ====================================
-- LAVUGIO - PostgreSQL Init Script
-- ====================================

-- ====================================
-- BRISANJE SVIH PODATAKA IZ TABELA
-- ====================================
TRUNCATE TABLE ride_passengers CASCADE;
TRUNCATE TABLE favorite_route_destinations CASCADE;
TRUNCATE TABLE ride_destinations CASCADE;
TRUNCATE TABLE ride_reports CASCADE;
TRUNCATE TABLE reviews CASCADE;
TRUNCATE TABLE messages CASCADE;
TRUNCATE TABLE notifications CASCADE;
TRUNCATE TABLE favorite_routes CASCADE;
TRUNCATE TABLE rides CASCADE;
TRUNCATE TABLE driver_registration_token CASCADE;
TRUNCATE TABLE drivers CASCADE;
TRUNCATE TABLE regular_users CASCADE;
TRUNCATE TABLE blockable_accounts CASCADE;
TRUNCATE TABLE administrators CASCADE;
TRUNCATE TABLE accounts CASCADE;
TRUNCATE TABLE vehicles CASCADE;
TRUNCATE TABLE addresses CASCADE;

-- Reset sequences
ALTER SEQUENCE addresses_id_seq RESTART WITH 1;
ALTER SEQUENCE vehicles_id_seq RESTART WITH 1;
ALTER SEQUENCE accounts_id_seq RESTART WITH 1;
ALTER SEQUENCE driver_registration_token_id_seq RESTART WITH 1;
ALTER SEQUENCE rides_id_seq RESTART WITH 1;
ALTER SEQUENCE ride_destinations_id_seq RESTART WITH 1;
ALTER SEQUENCE reviews_id_seq RESTART WITH 1;
ALTER SEQUENCE favorite_routes_id_seq RESTART WITH 1;
ALTER SEQUENCE favorite_route_destinations_id_seq RESTART WITH 1;
ALTER SEQUENCE notifications_id_seq RESTART WITH 1;
ALTER SEQUENCE messages_id_seq RESTART WITH 1;

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

SELECT setval('addresses_id_seq', 15, true);

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
-- 9. RIDES - 120 vožnji za vozača ID 5
-- ====================================

-- Vožnje za vozača 5 (120 vožnji)
INSERT INTO rides (id, driver_id, user_id, start_date_time, end_date_time, price, distance, ride_status, has_panic)
SELECT
    id,
    5 as driver_id,
    (ARRAY[1, 2, 3, 4, 9, 10])[1 + mod(id::integer, 6)] as user_id,
    timestamp '2024-10-01 08:00:00' + (id || ' days')::interval + (mod(id::integer, 12) || ' hours')::interval,
    CASE
        WHEN id <= 80 THEN timestamp '2024-10-01 08:00:00' + (id || ' days')::interval + (mod(id::integer, 12) + 1 || ' hours')::interval
        ELSE NULL
        END as end_date_time,
    300.0 + (random() * 700)::numeric(10,2) as price,
    2.0 + (random() * 18)::numeric(10,2) as distance,
    CASE
        WHEN id <= 80 THEN 'FINISHED'
        WHEN id <= 100 THEN 'SCHEDULED'
        WHEN id <= 110 THEN 'ACTIVE'
        ELSE 'CANCELLED'
        END as ride_status,
    CASE WHEN id = 50 THEN true ELSE false END as has_panic
FROM generate_series(1, 120) as id;

-- Dodatne vožnje za ostale vozače
INSERT INTO rides (id, driver_id, user_id, start_date_time, end_date_time, price, distance, ride_status, has_panic) VALUES
                                                                                                                        (121, 7, 2, '2024-11-16 14:00:00', '2024-11-16 14:45:00', 820.00, 12.5, 'FINISHED', false),
                                                                                                                        (122, 8, 3, '2024-12-17 10:15:00', '2024-12-17 11:00:00', 650.00, 8.3, 'FINISHED', false),
                                                                                                                        (123, 7, 1, '2025-01-19 09:00:00', '2025-01-19 09:40:00', 720.00, 10.2, 'FINISHED', false),
                                                                                                                        (124, 8, 2, '2025-01-20 12:00:00', '2025-01-20 12:50:00', 980.00, 15.7, 'FINISHED', true),
                                                                                                                        (125, 7, 10, '2025-01-23 10:00:00', NULL, 500.00, 6.0, 'SCHEDULED', false),
                                                                                                                        (126, NULL, 3, '2025-01-24 14:00:00', NULL, 700.00, 9.0, 'SCHEDULED', false);

SELECT setval('rides_id_seq', 126, true);

-- ====================================
-- 10. RIDE_PASSENGERS (Many-to-Many)
-- ====================================
INSERT INTO ride_passengers (ride_id, user_id)
SELECT
    r.id,
    r.user_id
FROM rides r
WHERE r.id <= 120;

-- Dodatni putnici za ostale vožnje
INSERT INTO ride_passengers (ride_id, user_id) VALUES
                                                   (121, 2),
                                                   (122, 3),
                                                   (123, 1),
                                                   (124, 2),
                                                   (124, 3),
                                                   (125, 10),
                                                   (126, 3);

-- ====================================
-- 11. RIDE_DESTINATIONS
-- ====================================

-- Destinacije za vozača 5 (120 vožnji sa po 2 destinacije)
-- ID range: 1-240
INSERT INTO ride_destinations (id, ride_id, address_id, destination_order)
SELECT
    (id - 1) * 2 + 1,
    id,
    1 + mod((id - 1)::integer, 15),
    0
FROM generate_series(1, 120) as id;

INSERT INTO ride_destinations (id, ride_id, address_id, destination_order)
SELECT
    (id - 1) * 2 + 2,
    id,
    1 + mod(id::integer, 15),
    1
FROM generate_series(1, 120) as id;

-- Dodaj treću destinaciju za neke vožnje (svaka 3. vožnja)
-- ID range: 241-250
INSERT INTO ride_destinations (id, ride_id, address_id, destination_order)
SELECT
    240 + ROW_NUMBER() OVER (),
    id * 3,
    1 + mod((id * 3 + 7)::integer, 15),
    2
FROM generate_series(1, 10) as id
WHERE id * 3 <= 120;

-- Destinacije za ostale vožnje (121-126)
-- ID range: 251-263
INSERT INTO ride_destinations (id, ride_id, address_id, destination_order) VALUES
                                                                               (251, 121, 3, 0),
                                                                               (252, 121, 4, 1),
                                                                               (253, 122, 5, 0),
                                                                               (254, 122, 6, 1),
                                                                               (255, 123, 1, 0),
                                                                               (256, 123, 3, 1),
                                                                               (257, 124, 2, 0),
                                                                               (258, 124, 4, 1),
                                                                               (259, 124, 5, 2),
                                                                               (260, 125, 1, 0),
                                                                               (261, 125, 2, 1),
                                                                               (262, 126, 3, 0),
                                                                               (263, 126, 4, 1);

SELECT setval('ride_destinations_id_seq', 263, true);

-- ====================================
-- 12. REVIEWS
-- ====================================
-- Reviews za prvih 50 završenih vožnji vozača 5
INSERT INTO reviews (id, car_rating, driver_rating, comment, ride_id, user_id)
SELECT
    id,
    3 + floor(random() * 3)::integer,
    3 + floor(random() * 3)::integer,
    CASE mod(id, 5)
        WHEN 0 THEN 'Odlična vožnja, sve pohvale!'
        WHEN 1 THEN 'Vozač je bio izuzetno ljubazan.'
        WHEN 2 THEN 'Auto čist i udoban.'
        WHEN 3 THEN 'Prosečno iskustvo.'
        ELSE 'Sve je bilo u redu.'
        END,
    id,
    (ARRAY[1, 2, 3, 4, 9, 10])[1 + mod(id::integer, 6)]
FROM generate_series(1, 50) as id;

-- Dodatni reviews
INSERT INTO reviews (id, car_rating, driver_rating, comment, ride_id, user_id) VALUES
                                                                                   (51, 4, 5, 'Vozač je bio izuzetno ljubazan, ali auto malo stariji.', 121, 2),
                                                                                   (52, 5, 4, 'Auto fantastičan, vozač malo brzo vozi.', 122, 3),
                                                                                   (53, 5, 5, 'Perfektna vožnja, siguran i udoban prevoz!', 123, 1),
                                                                                   (54, 2, 2, 'Auto bio prljav, vozač neprijatan. Panično dugme aktivirano.', 124, 2);

SELECT setval('reviews_id_seq', 54, true);

-- ====================================
-- 13. RIDE_REPORTS
-- ====================================
INSERT INTO ride_reports (report_id, ride_id, report_message, account_id) VALUES
                                                                              (1, 124, 'Vozač je vozio prebrzo i ignorisao moje molbe da uspori. Osećao sam se ugroženo.', 2),
                                                                              (2, 50, 'Aktivirano panično dugme zbog neprijatne situacije.', 9);

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
                                                                                                                         (1, 'Vožnja završena', 'Vaša vožnja je uspešno završena. Hvala što koristite Lavugio!', '/rides/1', 1, 'REGULAR', '2024-10-01', '09:00:00', true),
                                                                                                                         (2, 'Ocenite vožnju', 'Molimo vas da ocenite svoju poslednju vožnju.', '/rides/2', 2, 'REGULAR', '2024-10-02', '14:45:00', true),
                                                                                                                         (3, 'PANIKA!', 'Korisnik je aktivirao panično dugme tokom vožnje #50', '/rides/50', 11, 'PANIC', '2024-11-19', '12:35:00', true),
                                                                                                                         (4, 'Vožnja zakazana', 'Vaša vožnja je zakazana za sutra u 10:00.', '/rides/125', 10, 'LINKED', '2025-01-22', '18:00:00', false),
                                                                                                                         (5, 'Novi vozač dostupan', 'Vaš zahtev za vožnju je prihvaćen. Vozač je na putu!', '/rides/105', 9, 'LINKED', '2025-01-15', '15:05:00', true);

SELECT setval('notifications_id_seq', 5, true);

-- ====================================
-- 17. MESSAGES
-- ====================================
INSERT INTO messages (id, sender_id, receiver_id, message_date, message_time, text, is_read) VALUES
                                                                                                 (1, 1, 5, '2024-10-01', '08:25:00', 'Gde ste? Čekam ispred zgrade.', true),
                                                                                                 (2, 5, 1, '2024-10-01', '08:26:00', 'Stigao sam, vidim vas!', true),
                                                                                                 (3, 2, 7, '2024-11-16', '13:55:00', 'Možete li me pokupiti 5 minuta ranije?', true),
                                                                                                 (4, 7, 2, '2024-11-16', '13:56:00', 'Naravno, već sam u blizini.', true),
                                                                                                 (5, 9, 5, '2025-01-15', '14:58:00', 'Koliko će još trajati do mene?', true),
                                                                                                 (6, 5, 9, '2025-01-15', '14:59:00', 'Stižem za 2 minuta!', true),
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
SELECT 'Rides (Total)', COUNT(*) FROM rides
UNION ALL
SELECT 'Rides (Driver 5)', COUNT(*) FROM rides WHERE driver_id = 5
UNION ALL
SELECT 'Ride Destinations', COUNT(*) FROM ride_destinations
UNION ALL
SELECT 'Reviews', COUNT(*) FROM reviews
UNION ALL
SELECT 'Messages', COUNT(*) FROM messages
UNION ALL
SELECT 'Notifications', COUNT(*) FROM notifications
UNION ALL
SELECT 'Favorite Routes', COUNT(*) FROM favorite_routes;