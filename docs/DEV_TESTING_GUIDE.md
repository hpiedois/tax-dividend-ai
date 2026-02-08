# Guide de test en environnement DEV

Ce guide explique comment tester les deux flows d'authentification (Registration classique + SSO) en environnement de développement.

## Prérequis

- Docker Compose démarré (`docker-compose up -d`)
- Services actifs :
  - Frontend : http://localhost:5173
  - BFF Gateway : http://localhost:8082
  - Backend : http://localhost:8081
  - Keycloak : http://localhost:8080
  - PostgreSQL : localhost:5432

---

## Test Flow 1 : Registration classique (email/password)

### Étape 1 : S'enregistrer via formulaire

1. Aller sur http://localhost:5173
2. Cliquer sur **"Sign Up"** ou **"S'inscrire"**
3. Remplir le formulaire :
   ```
   Email: test@example.com
   Password: Test1234!
   First Name: John
   Last Name: Doe
   ```
4. Cliquer **"Register"**

**✅ Résultat attendu** :
```json
{
  "id": "uuid-123...",
  "message": "Registration successful! Please check your email (test@example.com) to verify your account. You won't be able to log in until you verify your email."
}
```

### Étape 2 : Vérifier la création dans Keycloak

```bash
# Lister les users Keycloak
docker exec -it keycloak /opt/keycloak/bin/kcadm.sh get users \
  -r taxdividend \
  --server http://localhost:8080 \
  --realm master \
  --user admin \
  --password admin
```

**✅ Résultat attendu** :
- User créé dans Keycloak
- `emailVerified: false`
- `requiredActions: ["VERIFY_EMAIL"]`

### Étape 3 : Vérifier la création en DB

```bash
# Se connecter à PostgreSQL
docker exec -it postgres psql -U taxdividend_dev -d taxdividend_dev

# Vérifier le user
SELECT id, email, is_verified, registration_source, created_at 
FROM users 
WHERE email = 'test@example.com';
```

**✅ Résultat attendu** :
```
id                  | email              | is_verified | registration_source | created_at
--------------------|-----------------------|-------------|---------------------|------------
uuid-123...         | test@example.com      | false       | CLASSIC             | 2026-02-07...
```

### Étape 4 : Vérifier l'email de vérification (dev mode)

En dev, les emails Keycloak sont loggés dans la console :

```bash
docker-compose logs keycloak | grep "Verification"
```

**✅ Résultat attendu** :
```
keycloak | Email verification link: http://localhost:8080/realms/taxdividend/login-actions/action-token?...
```

### Étape 5 : Cliquer sur le lien de vérification

1. Copier le lien de vérification depuis les logs
2. Ouvrir dans le navigateur
3. Keycloak marque l'email comme vérifié

**✅ Résultat attendu** :
```
Email verified successfully! You can now log in.
```

### Étape 6 : Se connecter

1. Frontend → **"Sign In"**
2. Email : `test@example.com`
3. Password : `Test1234!`
4. Cliquer **"Sign In"**

**❌ Attendu si email non vérifié** :
```
You must verify your email before logging in.
```

**✅ Attendu si email vérifié** :
- Redirection vers Dashboard
- JWT Token stocké
- User authentifié

### Étape 7 : Vérifier que l'user existe en DB

```bash
# Première requête API déclenche la vérification
curl -H "Authorization: Bearer YOUR_JWT" \
     http://localhost:8082/api/v1/dividends/stats?taxYear=2024
```

**✅ Résultat attendu** :
- Pas d'erreur "User not registered"
- User existe déjà en DB depuis l'étape 3

---

## Test Flow 2 : SSO Google

### Prérequis : Configurer Google OAuth

Suivre le guide **KEYCLOAK_SSO_SETUP.md** section "Configuration Google OAuth"

### Étape 1 : Se connecter via Google

1. Frontend http://localhost:5173
2. Cliquer **"Sign in with Google"**
3. Sélectionner un compte Google (ex: `john.doe@gmail.com`)
4. Autoriser l'accès
5. Redirection automatique vers Dashboard

**✅ Résultat attendu** :
- Connexion réussie
- Redirection vers Dashboard
- JWT Token stocké

### Étape 2 : Vérifier le JWT contient `identity_provider`

```bash
# Dans la console browser (F12)
localStorage.getItem('access_token')

# Decoder le JWT (utiliser https://jwt.io/)
```

**✅ Résultat attendu** :
```json
{
  "sub": "google-user-uuid",
  "email": "john.doe@gmail.com",
  "email_verified": true,
  "identity_provider": "google"  ← IMPORTANT
}
```

### Étape 3 : Vérifier l'auto-provisioning en DB

```bash
# PostgreSQL
SELECT id, email, is_verified, password_hash, registration_source, created_at 
FROM users 
WHERE email = 'john.doe@gmail.com';
```

**✅ Résultat attendu** :
```
id                  | email                 | is_verified | password_hash | registration_source | created_at
--------------------|------------------------|-------------|---------------|---------------------|------------
google-uuid...      | john.doe@gmail.com     | true        | SSO           | GOOGLE_SSO          | 2026-02-07...
```

**Points clés** :
- `is_verified = true` (email vérifié par Google)
- `password_hash = "SSO"` (pas de mot de passe)
- `registration_source = "GOOGLE_SSO"`

### Étape 4 : Vérifier les logs Backend

```bash
docker-compose logs backend | grep "Auto-provisioning"
```

**✅ Résultat attendu** :
```
backend | Auto-provisioning SSO user: john.doe@gmail.com (provider: google)
backend | Creating SSO user with registration source: GOOGLE_SSO
```

### Étape 5 : Tester une requête API

```bash
curl -H "Authorization: Bearer YOUR_JWT" \
     http://localhost:8082/api/v1/dividends/stats?taxYear=2024
```

**✅ Résultat attendu** :
- Requête réussie
- Pas d'erreur "User not registered"
- Statistiques retournées (vides pour un nouveau user)

---

## Test Flow 3 : SSO GitHub

### Étape 1 : Configurer GitHub OAuth

Suivre le guide **KEYCLOAK_SSO_SETUP.md** section "Configuration GitHub OAuth"

### Étape 2 : Se connecter via GitHub

1. Frontend → **"Sign in with GitHub"**
2. Autoriser l'accès
3. Vérifier auto-provisioning en DB :
   ```sql
   SELECT * FROM users WHERE registration_source = 'GITHUB_SSO';
   ```

**✅ Résultat attendu** :
- User créé avec `registration_source = "GITHUB_SSO"`
- `is_verified = true`
- `password_hash = "SSO"`

---

## Test de sécurité : User classique non enregistré

### Scénario : User créé dans Keycloak mais pas en DB

1. Créer un user directement dans Keycloak (sans passer par `/auth/register`)
2. Se connecter avec ce user
3. Tenter une requête API

**✅ Résultat attendu** :
```json
{
  "error": "Unauthorized",
  "message": "User must complete registration via /auth/register endpoint"
}
```

**Logs Backend** :
```
backend | User not registered: uuid-123 (classic login requires /auth/register)
```

---

## Commandes de debug utiles

### Vérifier les services

```bash
# Tous les services up
docker-compose ps

# Logs en temps réel
docker-compose logs -f backend bff-gateway keycloak

# Logs d'un service spécifique
docker-compose logs --tail=100 backend
```

### Vérifier la DB

```bash
# Connexion PostgreSQL
docker exec -it postgres psql -U taxdividend_dev -d taxdividend_dev

# Lister tous les users
SELECT id, email, registration_source, is_verified FROM users;

# Lister les users SSO
SELECT * FROM users WHERE registration_source != 'CLASSIC';

# Supprimer un user de test
DELETE FROM users WHERE email = 'test@example.com';
```

### Vérifier Keycloak

```bash
# Liste des Identity Providers
curl -s http://localhost:8080/realms/taxdividend | jq '.identity_providers[]'

# Liste des users Keycloak
docker exec -it keycloak /opt/keycloak/bin/kcadm.sh get users \
  -r taxdividend \
  --server http://localhost:8080 \
  --realm master \
  --user admin \
  --password admin
```

### Reset complet de l'environnement

```bash
# Arrêter tous les services
docker-compose down -v

# Supprimer les volumes (⚠️ perte de données)
docker volume rm tax-dividend-ai_postgres_data
docker volume rm tax-dividend-ai_keycloak_data

# Redémarrer
docker-compose up -d

# Attendre que les services soient prêts
sleep 30

# Vérifier la DB est initialisée
docker exec -it postgres psql -U taxdividend_dev -d taxdividend_dev -c "\dt"
```

---

## Checklist de validation

### ✅ Registration classique

- [ ] Formulaire d'inscription fonctionne
- [ ] User créé dans Keycloak avec `requiredActions: ["VERIFY_EMAIL"]`
- [ ] User créé en DB avec `is_verified = false`, `registration_source = "CLASSIC"`
- [ ] Email de vérification envoyé
- [ ] Après vérification email, connexion possible
- [ ] Requêtes API fonctionnent après connexion

### ✅ SSO Google

- [ ] Bouton "Sign in with Google" visible
- [ ] Redirection vers Google OAuth
- [ ] Autorisation Google fonctionne
- [ ] JWT contient `identity_provider = "google"`
- [ ] User auto-provisionné en DB avec `registration_source = "GOOGLE_SSO"`, `is_verified = true`
- [ ] Requêtes API fonctionnent immédiatement

### ✅ SSO GitHub

- [ ] Bouton "Sign in with GitHub" visible
- [ ] Auto-provisioning fonctionne
- [ ] `registration_source = "GITHUB_SSO"`

### ✅ Sécurité

- [ ] User classique non enregistré est rejeté
- [ ] Erreur claire : "User must complete registration"
- [ ] Logs d'audit corrects

---

## Cas d'usage réels

### Investisseur individuel (B2C)

1. S'inscrit avec email/password (registration classique)
2. Vérifie son email
3. Se connecte
4. Importe ses dividend statements
5. Génère ses formulaires 5000/5001

### Utilisateur Google (B2C)

1. Clique "Sign in with Google"
2. Autorisation Google
3. Auto-provisionné automatiquement
4. Accès immédiat à l'application

### Fiduciaire (B2B)

1. S'inscrit avec email professionnel (registration classique)
2. Vérifie son email
3. Crée plusieurs clients
4. Gère les reclaims pour tous ses clients

---

## Support

En cas de problème :
1. Vérifier les logs : `docker-compose logs -f`
2. Vérifier la DB : `SELECT * FROM users;`
3. Vérifier Keycloak admin console : http://localhost:8080
4. Reset l'environnement si nécessaire

