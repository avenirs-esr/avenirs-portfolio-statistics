\connect template1;
SELECT pg_terminate_backend(pid)
FROM pg_stat_activity
WHERE datname = 'avenirs_statistics' AND pid <> pg_backend_pid();
DROP DATABASE IF EXISTS avenirs_statistics;
DROP ROLE IF EXISTS avenirs_statistics_admin;
DROP ROLE IF EXISTS avenirs_statistics_admin_role;
