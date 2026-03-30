-- 1. INDEPENDENT TABLES
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- 2. Create users with a direct link to the role
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    contact_number VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role_id BIGINT NOT NULL REFERENCES roles(id), -- The new Foreign Key
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE cinema_halls (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    total_rows INT,
    seat_per_rows INT
);

CREATE TABLE movies (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    duration_in_mins INT,
    release_date DATE,
    language VARCHAR(255),
    poster_url VARCHAR(255),
    created_at DATE NOT NULL DEFAULT CURRENT_DATE,
    updated_at DATE
);

CREATE TABLE payment_gateway_config (
    id BIGSERIAL PRIMARY KEY,
    gateway_name VARCHAR(255) NOT NULL UNIQUE,
    api_key VARCHAR(255) NOT NULL,
    api_secret VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL
);



CREATE TABLE seat (
    id BIGSERIAL PRIMARY KEY,
    hall_id BIGINT NOT NULL REFERENCES cinema_halls(id),
    row_identifier VARCHAR(5) NOT NULL,
    seat_number INT NOT NULL,
    seat_tier VARCHAR(20) NOT NULL,
    CONSTRAINT chk_seat_tier CHECK (seat_tier IN ('STANDARD', 'PREMIUM', 'VIP'))
);

CREATE TABLE show_time (
    id BIGSERIAL PRIMARY KEY,
    movie_id BIGINT NOT NULL REFERENCES movies(id),
    cinema_hall_id BIGINT NOT NULL REFERENCES cinema_halls(id),
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    base_price DECIMAL(10, 2) NOT NULL
);

CREATE TABLE reservation (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    showtime_id BIGINT NOT NULL REFERENCES show_time(id),
    total_amount DECIMAL(10, 2) NOT NULL,
    reservation_status VARCHAR(20) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    CONSTRAINT chk_reservation_status CHECK (reservation_status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'REFUNDED'))
);

CREATE TABLE showtime_seat (
    id BIGSERIAL PRIMARY KEY,
    showtime_id BIGINT NOT NULL REFERENCES show_time(id),
    seat_id BIGINT NOT NULL REFERENCES seat(id),
    reservation_id BIGINT REFERENCES reservation(id),
    seat_status VARCHAR(20) NOT NULL,
    version INT NOT NULL DEFAULT 0,
    CONSTRAINT chk_showtime_seat_status CHECK (seat_status IN ('AVAILABLE', 'LOCKED', 'BOOKED'))
);

CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    reservation_id BIGINT NOT NULL REFERENCES reservation(id),
    provider_transaction_id VARCHAR(255) UNIQUE,
    amount DECIMAL(10, 2) NOT NULL,
    payment_status VARCHAR(20) NOT NULL,
    gateway_config_id BIGINT NOT NULL REFERENCES payment_gateway_config(id),
    CONSTRAINT chk_payment_status CHECK (payment_status IN ('PENDING', 'SUCCESS', 'FAILED', 'REFUNDED'))
);

CREATE TABLE notification (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    notification_type VARCHAR(20) NOT NULL,
    message_payload VARCHAR(255),
    status VARCHAR(20) NOT NULL,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_notification_type CHECK (notification_type IN ('SMS', 'EMAIL')),
    CONSTRAINT chk_notification_status CHECK (status IN ('PENDING', 'SENT', 'FAILED'))
);