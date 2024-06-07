INSERT INTO nuvolo_user (first_name, last_name, email, password)
VALUES ('nuvolo_first_name', 'nuvolo_last_name', 'nuvolo@mail.com',
        '$2a$12$8lPdJ9riIWZyGTDRITnltuyfSfGeSadNR85Pg/xxRE1ub9t5U2y6S');

INSERT INTO nuvolo_user (first_name, last_name, email, password, is_enabled)
VALUES ('nuvolo_first_name', 'nuvolo_last_name', 'verified@mail.com',
        '$2a$12$8lPdJ9riIWZyGTDRITnltuyfSfGeSadNR85Pg/xxRE1ub9t5U2y6S', TRUE);

INSERT INTO nuvolo_user (first_name, last_name, email, password, is_enabled)
VALUES ('nuvolo_first_name', 'nuvolo_last_name', 'unverified@mail.com',
        '$2a$12$8lPdJ9riIWZyGTDRITnltuyfSfGeSadNR85Pg/xxRE1ub9t5U2y6S', FALSE);

INSERT INTO verification (token, user_id)
VALUES ('123e4567-e89b-12d3-a456-426614174000', (SELECT id
                                                 FROM nuvolo_user
                                                 WHERE email = 'nuvolo@mail.com'));

INSERT INTO verification (token, is_verified, user_id)
VALUES ('e7e66e28-90a8-468b-ab33-a0bdf3002c49', TRUE, (SELECT id
                                                       FROM nuvolo_user
                                                       WHERE email = 'verified@mail.com'));

INSERT INTO verification (token, user_id)
VALUES ('123e4567-e89b-12d3-a456-426614174044', (SELECT id
                                                 FROM nuvolo_user
                                                 WHERE email = 'unverified@mail.com'));


INSERT INTO forgotten_pass_reset (token, user_id)
VALUES ('e7e44e28-90a8-468b-cc33-a0bdf4002c49', (SELECT id
                                                 FROM nuvolo_user
                                                 WHERE email = 'verified@mail.com'));

INSERT INTO forgotten_pass_reset (token, utilised, user_id)
VALUES ('7e199298-1bf1-4c12-8056-15a740914c79', TRUE, (SELECT id
                                                       FROM nuvolo_user
                                                       WHERE email = 'verified@mail.com'));

INSERT INTO forgotten_pass_reset (token, user_id)
VALUES ('b1e44e28-90a8-458b-dd33-a0cdf4002c49', (SELECT id
                                                 FROM nuvolo_user
                                                 WHERE email = 'verified@mail.com'));