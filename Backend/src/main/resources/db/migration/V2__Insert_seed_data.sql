-- 1. Insert Default Roles as per Requirements
INSERT INTO roles (name, description) VALUES
('ADMIN', 'System Administrator with full access to movies, showtimes, and reports'),
('USER', 'Regular customer who can browse movies and book tickets');

-- 2. Insert Initial Admin User
-- Password is 'admin123' hashed with BCrypt
INSERT INTO users (full_name, email, contact_number, password, role_id, created_at, updated_at)
VALUES (
    'CineReserve Admin',
    'admin@cinereserve.com',
    '9999999999',
    '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HCG3JZGZJ2M./wK90I.8O',
    (SELECT id FROM roles WHERE name = 'ADMIN'),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 3. Insert Initial Payment Gateway Config
INSERT INTO payment_gateway_config (gateway_name, api_key, api_secret, is_active)
VALUES
('RAZORPAY', 'rzp_test_key', 'rzp_test_secret', TRUE),
('PAYU', 'payu_test_key', 'payu_test_secret', FALSE);

-- 4. Seed Initial Genres for a Sample Movie [cite: 27]
-- This ensures your 'movie_genres' collection table works immediately
INSERT INTO movies (title, description, duration_in_mins, release_date, language, poster_url, is_active, created_at)
VALUES ('Inception', 'A thief who steals corporate secrets through use of dream-sharing technology.', 148, '2010-07-16', 'English', 'inception_poster.jpg', TRUE, CURRENT_TIMESTAMP);

INSERT INTO movie_genres (movie_id, genre)
VALUES
((SELECT id FROM movies WHERE title = 'Inception'), 'ACTION'),
((SELECT id FROM movies WHERE title = 'Inception'), 'SCI_FI'),
((SELECT id FROM movies WHERE title = 'Inception'), 'THRILLER');