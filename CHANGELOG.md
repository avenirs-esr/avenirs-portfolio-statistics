# 📦 Changelog

This file tracks all notable changes to this repository, following
the [Keep a Changelog](https://keepachangelog.com/en/1.0.0/) format
and [Conventional Commits](https://www.conventionalcommits.org/) standard.

---

## [Unreleased]

- **Initialization of the statistics micro-service**
    - Collection of the COFOLIO usage events: `POST /statistics/events`, restricted to the internal
      services (api key), idempotent when the emitter provides an `eventId`.
    - Count of the collected events by type, institution and period:
      `GET /statistics/events/count`, restricted to `ROLE_SUPER_ADMIN`.
- **Update process**
    - New database `avenirs_statistics` (schema `dev`), created by `src/main/resources/db/init-db.sql`.
    - The changelog creates the `statistic_event` table and its three indexes
      (`event_type, occurred_at`), (`institution_id, occurred_at`) and (`user_id, occurred_at`).
