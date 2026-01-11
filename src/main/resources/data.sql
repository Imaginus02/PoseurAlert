-- Insert test user for development
-- Temporary: using noop encoder for testing
INSERT INTO sp_user (email, password, company_name, sector) VALUES
('test@test.com', '{noop}test', 'Test Company', 'RESTAURANT');