-- Splitia Database Schema Migration V7
-- Update passwords for @splitia.com users with correct BCrypt hash (cost factor 12)

-- Update passwords for Rodrigo, Luis, and Israel
-- Password: splitia123 (BCrypt hash with cost factor 12)
UPDATE users 
SET password = '$2a$12$CVhNdtJqnRNjVyNAH952.eTkSnQIqNmo69Eh6hflRO/IsIvCwk6Zm',
    updated_at = CURRENT_TIMESTAMP
WHERE email IN ('rodrigo@splitia.com', 'luis@splitia.com', 'israel@splitia.com');

