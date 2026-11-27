SELECT pg_terminate_backend(pid)
FROM pg_stat_activity
WHERE datname = 'template1'
  AND pid <> pg_backend_pid();

CREATE ROLE avenirs_statistics_admin_role SUPERUSER;
CREATE ROLE avenirs_statistics_admin PASSWORD 'ENC(RU/oKTdrWITCnPCQmoYT5KdHtuOB6Bf6aF7owLL2d5QZukCEUpihkA==)' NOSUPERUSER CREATEDB CREATEROLE INHERIT LOGIN;
GRANT avenirs_statistics_admin_role to avenirs_statistics_admin;

CREATE DATABASE avenirs_statistics OWNER avenirs_statistics_admin;
GRANT ALL PRIVILEGES ON DATABASE avenirs_statistics TO avenirs_statistics_admin_role;
\c avenirs_statistics
CREATE SCHEMA IF NOT EXISTS dev AUTHORIZATION avenirs_statistics_admin;
ALTER USER avenirs_statistics_admin SET search_path TO dev, public;
CREATE EXTENSION IF NOT EXISTS citext;
CREATE EXTENSION IF NOT EXISTS pgcrypto;
