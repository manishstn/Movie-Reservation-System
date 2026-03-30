-- Insert Default Roles
INSERT INTO roles (name, description) VALUES
('ROLE_ADMIN', 'System Administrator with full access'),
('ROLE_MANAGER', 'Cinema Manager who handles catalog and scheduling'),
('ROLE_CUSTOMER', 'Regular user who can book tickets');

-- Insert Initial Admin User and link the role immediately
INSERT INTO users (full_name, email, contact_number, password, role_id, created_at, updated_at) VALUES
(
    'System Admin',
    'admin@example.com',
    '1234567890',
        '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HCG3JZGZJ2M./wK90I.8O',
    (SELECT id FROM roles WHERE name = 'ROLE_ADMIN'), -- Fetches the ID automatically
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);