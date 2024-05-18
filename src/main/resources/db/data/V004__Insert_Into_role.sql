-- Insert initial role data only if it does not already exist
INSERT INTO role (name) VALUES ('USER')
ON CONFLICT (name) DO NOTHING;

INSERT INTO role (name) VALUES ('ADMIN')
ON CONFLICT (name) DO NOTHING;