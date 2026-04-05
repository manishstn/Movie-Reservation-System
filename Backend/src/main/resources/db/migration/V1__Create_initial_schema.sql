-- 1. INDEPENDENT TABLES & CONFIG
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE payment_gateway_config (
    id BIGSERIAL PRIMARY KEY,
    gateway_name VARCHAR(255) NOT NULL UNIQUE,
    api_key VARCHAR(255) NOT NULL,
    api_secret VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE coupons (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(255) NOT NULL UNIQUE,
    discount_percentage DECIMAL(5,2),
    max_discount_amount DECIMAL(10,2),
    expiry_date TIMESTAMP,
    usage_limit INT,
    current_usage INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    version INT DEFAULT 0 -- For Optimistic Locking in Java
);

-- 2. CORE INVENTORY
CREATE TABLE movies (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    duration_in_mins INT,
    release_date DATE,
    language VARCHAR(255),
    poster_url VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- LocalDateTime
    updated_at TIMESTAMP
);

-- Join table for Movie Genres (@ElementCollection)
CREATE TABLE movie_genres (
    movie_id BIGINT NOT NULL REFERENCES movies(id),
    genre VARCHAR(50) NOT NULL
);

CREATE TABLE cinema_halls (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    location VARCHAR(200),
    total_rows INT,
    seat_per_rows INT,
    is_active BOOLEAN DEFAULT TRUE
);

-- 3. USERS & SECURITY
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    contact_number VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role_id BIGINT NOT NULL REFERENCES roles(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(512) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL REFERENCES users(id),
    expires_at TIMESTAMP NOT NULL,
    is_revoked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. SHOWTIMES & SEATS
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
    base_price DECIMAL(10, 2) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE
);

-- 5. TRANSACTIONS
CREATE TABLE reservation (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    showtime_id BIGINT NOT NULL REFERENCES show_time(id),
    coupon_id BIGINT REFERENCES coupons(id),
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    total_amount DECIMAL(10, 2) NOT NULL,
    reservation_status VARCHAR(20) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    cancelled_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_reservation_status CHECK (reservation_status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'REFUNDED'))
);

-- Concurrent Seat Management Table
CREATE TABLE showtime_seat (
    id BIGSERIAL PRIMARY KEY,
    showtime_id BIGINT NOT NULL REFERENCES show_time(id),
    seat_id BIGINT NOT NULL REFERENCES seat(id),
    reservation_id BIGINT REFERENCES reservation(id),
    seat_status VARCHAR(20) NOT NULL,
    version INT NOT NULL DEFAULT 0, -- Matches @Version in Java
    CONSTRAINT chk_showtime_seat_status CHECK (seat_status IN ('AVAILABLE', 'LOCKED', 'BOOKED')),
    UNIQUE(showtime_id, seat_id) -- Database-level overbooking guard
);

CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    reservation_id BIGINT NOT NULL REFERENCES reservation(id),
    provider_transaction_id VARCHAR(255) UNIQUE,
    amount DECIMAL(10, 2) NOT NULL,
    payment_status VARCHAR(20) NOT NULL,
    gateway_config_id BIGINT NOT NULL REFERENCES payment_gateway_config(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT chk_payment_status CHECK (payment_status IN ('PENDING', 'SUCCESS', 'FAILED', 'REFUNDED'))
);

CREATE TABLE refunds (
    id BIGSERIAL PRIMARY KEY,
    payment_id BIGINT NOT NULL REFERENCES payments(id),
    refund_amount DECIMAL(10,2) NOT NULL,
    refund_transaction_id VARCHAR(255) UNIQUE,
    status VARCHAR(20),
    reason TEXT,
    initiated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 6. AUDIT & OUTPUT
CREATE TABLE tickets (
    id BIGSERIAL PRIMARY KEY,
    ticket_number VARCHAR(255) NOT NULL UNIQUE,
    reservation_id BIGINT NOT NULL REFERENCES reservation(id),
    movie_title_snapshot VARCHAR(255) NOT NULL,
    hall_name_snapshot VARCHAR(255) NOT NULL,
    seat_details_snapshot VARCHAR(255) NOT NULL,
    qr_code_data TEXT,
    issued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    action VARCHAR(255) NOT NULL,
    entity_name VARCHAR(255) NOT NULL,
    entity_id VARCHAR(255) NOT NULL, -- UUID-safe string
    old_values TEXT,
    new_values TEXT,
    performed_by BIGINT REFERENCES users(id),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE notification (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    notification_type VARCHAR(20) NOT NULL,
    message_payload TEXT,
    status VARCHAR(20) NOT NULL,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_notification_type CHECK (notification_type IN ('SMS', 'EMAIL', 'WHATSAPP')),
    CONSTRAINT chk_notification_status CHECK (status IN ('PENDING', 'SENT', 'FAILED'))
);