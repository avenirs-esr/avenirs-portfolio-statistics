# avenirs-portfolio-statistics

Statistics module for Avenirs-ESR ePortfolio (COFOLIO).

## Overview

This module collects the usage events emitted by the other COFOLIO micro-services and exposes the
aggregated figures used by the usage indicators (deployment, adoption, engagement, portfolio usage).

Collection is kept separate from restitution: the events are stored here independently of any
dashboard, and their reading is restricted to the `ROLE_SUPER_ADMIN` role.

## Endpoints

| Method | Path                         | Security                         | Purpose                                   |
|--------|------------------------------|----------------------------------|-------------------------------------------|
| POST   | `/statistics/events`         | `X-API-Key` (service to service) | Records a usage event (idempotent by id). |
| GET    | `/statistics/events/count`   | HMAC signed context, super admin | Counts events by type, institution, dates. |

## Local development

- Port: `10030`
- Database: `avenirs_statistics`, schema `dev` (`src/main/resources/db/init-db.sql`)
- Liquibase changelogs: `src/main/resources/db/changelog`
