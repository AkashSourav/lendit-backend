-- passwords
-- superadmin@gmail.com - super_admin
-- admin@gmail.com - admin
-- user@gmail.com  - user

INSERT INTO user
(id, first_name, last_name, email, password, authorised, user_role, created_date, updated_date)
VALUES
(1, 'super', 'admin', 'superadmin@gmail.com', '$2a$05$rvZFs4OMuCvJRF5UNgv0GOIAIeyPUHCwU1qQaxMopvVWd7ek6g1sS', 1, 0, now(), now()),
(2, 'admin', 'admin', 'admin@gmail.com', '$2a$05$Ac0K299JvzUVj0MnhKxva.g6cJR.8KAHz8g2Lf50zaIcS/f/iQq9.', 1, 1, now(), now()),
(3, 'app', 'user', 'user@gmail.com', '$2a$05$ks009xTMAPpkT5b3rOo6XOsF4XkmG7jGhpBvk5sRREIGIceCIsas.', 1, 2, now(), now());

INSERT INTO login_detail
( id, last_login, failed_attempt, blocked_time, user_id, user_ip )
VALUES
('1', now(), 0, 0, '1', 'test'),
('2', now(), 0, 0, '2', 'test'),
('3', now(), 0, 0, '3', 'test');
