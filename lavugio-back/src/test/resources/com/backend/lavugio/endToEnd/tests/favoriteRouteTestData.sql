-- ====================================
-- LAVUGIO - H2 Init Script
-- ====================================

-- Clean up existing data (in reverse order of dependencies)
DELETE FROM favorite_route_destinations WHERE 1=1;
DELETE FROM favorite_routes WHERE 1=1;
DELETE FROM regular_users WHERE 1=1;
DELETE FROM blockable_accounts WHERE 1=1;
DELETE FROM accounts WHERE 1=1;
DELETE FROM addresses WHERE 1=1;

-- ====================================
-- 1. ADDRESSES
-- ====================================
INSERT INTO addresses (id, street_name, city, country, street_number, zip_code, longitude, latitude) VALUES
                                                                                                         (1, 'Bulevar kralja Aleksandra', 'Beograd', 'Srbija', '73', 11000, 20.4489, 44.8020),
                                                                                                         (2, 'Knez Mihailova', 'Beograd', 'Srbija', '12', 11000, 20.4572, 44.8176),
                                                                                                         (3, 'Njegoševa', 'Beograd', 'Srbija', '45', 11000, 20.4681, 44.8073),
                                                                                                         (4, 'Terazije', 'Beograd', 'Srbija', '5', 11000, 20.4603, 44.8152),
                                                                                                         (5, 'Skadarska', 'Beograd', 'Srbija', '22', 11000, 20.4632, 44.8168);
-- ====================================
-- 2. ACCOUNTS
-- ====================================
INSERT INTO accounts (id, name, last_name, email, password, profile_photo_path, phone_number, address, email_verified, fcm_token, fcm_token_updated_at) VALUES (1, 'Marko', 'Marković', 'marko.markovic@gmail.com', '$2a$12$DvYeLDvQeXaDerGFoypiBOZGyiuAXeDYVQjjNF1..krm7fUAEHoOS', '/photos/marko.jpg', '+381641234567', 'Bulevar kralja Aleksandra 73', true, null, null),
                                                                                                                                                               (2, 'Marko', 'Marković', 'no.favorite.routes@gmail.com', '$2a$12$DvYeLDvQeXaDerGFoypiBOZGyiuAXeDYVQjjNF1..krm7fUAEHoOS', '/photos/marko.jpg', '+381641234567', 'Bulevar kralja Aleksandra 73', true, null, null);
-- ====================================
-- 3. BLOCKABLE_ACCOUNTS
-- ====================================
INSERT INTO blockable_accounts (id, blocked, block_reason) VALUES (1, false, NULL), (2, false, NULL);
-- ====================================
-- 4. REGULAR_USERS
-- ====================================
INSERT INTO regular_users (id, can_order) VALUES (1, true), (2, true);
-- 5. FAVORITE_ROUTES
-- ====================================
INSERT INTO favorite_routes (id, name, user_id) VALUES
                                                (1, 'Route 1', 1),
                                                (2, 'Route 2', 1);

-- ====================================
-- 6. FAVORITE_ROUTE_DESTINATIONS
-- ====================================
INSERT INTO favorite_route_destinations (favorite_route_id, address_id, destination_order) VALUES
                                                                                               (1, 1, 0), (1, 2, 1),
                                                                                               (2, 3, 0), (2, 4, 1), (2, 5, 2);
