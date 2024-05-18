-- Insert initial type data only if it does not already exist
INSERT INTO type (name) VALUES ('WOMEN')
ON CONFLICT (name) DO NOTHING;

INSERT INTO type (name) VALUES ('MEN')
ON CONFLICT (name) DO NOTHING;

INSERT INTO type (name) VALUES ('UNISEX')
ON CONFLICT (name) DO NOTHING;

INSERT INTO type (name) VALUES ('KIDS')
ON CONFLICT (name) DO NOTHING;