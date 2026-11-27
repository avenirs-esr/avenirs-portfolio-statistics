# avenirs-portfolio-statistics

Micro-service de statistiques d'utilisation de COFOLIO : il collecte les événements d'usage émis par
les autres micro-services et expose les chiffres utilisés par les indicateurs (déploiement, adoption,
engagement, usages du portfolio).

La collecte est séparée de la restitution : les événements sont conservés indépendamment de tout
tableau de bord, et leur lecture est réservée au rôle `ROLE_SUPER_ADMIN`.

| Méthode | Chemin                     | Sécurité                                                                 | Usage                                          |
|---------|----------------------------|--------------------------------------------------------------------------|------------------------------------------------|
| POST    | `/statistics/events`       | `X-ADMIN-TOKEN` + permission `statistic-event:record` (`ROLE_SUPER_ADMIN`) | Enregistre un événement (idempotent par `eventId`). |
| GET     | `/statistics/events/count` | `X-ADMIN-TOKEN` + permission `statistic-event:read` (`ROLE_SUPER_ADMIN`)   | Compte les événements par type, établissement et période. |

`X-ADMIN-TOKEN` est comparé à la propriété `admin.token` (`403` si incorrect, `503` si non configurée).
L'authentification elle-même (contexte HMAC signé, ou `X-API-Key` pour les appels de service à
service) reste requise en amont.

## 🚀 Prérequis

- **Java** 25
- **Maven** 3.9.9
- **PostgreSQL**
- **Docker** pour lancer la BDD en conteneur et les tests d'intégration (Testcontainers)

---

## 🏗️ Installation

Clone le projet :

```bash
git clone --recurse-submodules https://github.com/avenirs-esr/avenirs-portfolio-statistics.git
cd avenirs-portfolio-statistics
```

---

## 🛠️ Lancer l’application

### 🚀 Mode développement

```bash
mvn spring-boot:run
```

Le service sera accessible sur : [http://localhost:10030](http://localhost:10030)

---

## 📦 Génération et application des changelogs Liquibase

Pour initialiser la base de données avec tous les changelogs à jour, utilise la commande suivante **à partir du
dossier `srv-dev`** :

**Commande :**

```bash
npm run statistics:reset-db
```

Cette commande :

- Supprime la base de données existante
- Recrée la base, les rôles et les schémas
- Applique **tous les changelogs Liquibase** pour créer les tables

---

### 🛠️ Générer un nouveau changelog

Toujours depuis `srv-dev`, exécute :

```bash
npm run statistics:generate-changelog
```

Un fichier sera généré dans :  
`src/main/resources/db/changelog/generated/`

**⚠️ Attention** :

- Même si aucun changement n’est détecté, **un fichier vide est quand même généré** → pense à le **supprimer
  manuellement**
- Liquibase ne détecte **pas tous les changements** (nullable, renommages, contraintes, etc.)  
  → Toujours **relire le changelog généré** et corriger manuellement si besoin.

---

### ✅ Appliquer les nouveaux changelogs

Deux options s’offrent à toi :

- **Réinitialiser complètement** la base : `npm run statistics:reset-db`

- **Appliquer les différences** sans supprimer les données : `mvn liquibase:update` _(à lancer depuis le service)_

---

## 🧪 Lancer les tests

```bash
 mvn clean compile javadoc:javadoc verify
```

Les tests unitaires, d’intégration et les rapports de couverture sont générés.

---

## 🧹 Code Formatting – Google Java Format (via Spotless)

This project enforces code formatting using [google-java-format](https://github.com/google/google-java-format) via
the [Spotless Maven plugin](https://github.com/diffplug/spotless).

---

### ✅ Check code formatting

Before committing or pushing code, you can verify that your Java files follow the required format:

```bash
mvn spotless:check
```

### ✨ Format code

If you find any files that are not formatted correctly, you can automatically format them using:

```bash
mvn spotless:apply
```

---

## 📚 Documentation API

L’API est documentée via **Swagger** :

👉 [http://localhost:10030/avenirs-portfolio-statistics/swagger-ui](http://localhost:10030/avenirs-portfolio-statistics/swagger-ui) (en
local)

---

## 🔍 Surveillance et santé

Le projet inclut **Spring Boot Actuator**. Pour accéder aux endpoints:

👉 [http://localhost:10030/actuator](http://localhost:10030/actuator)

---

## ⚡ CI/CD et sécurité

Le projet inclut:

✅ Lint automatique  
✅ Couverture de tests et rapports  
✅ Analyse de sécurité (Trivy)  
✅ Publication automatique des rapports sur **GitHub Pages**
