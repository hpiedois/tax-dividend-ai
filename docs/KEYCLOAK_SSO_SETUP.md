# Guide de configuration Keycloak SSO

Ce guide explique comment configurer les Identity Providers (Google, GitHub, etc.) dans Keycloak pour l'authentification SSO.

## Prérequis

- Keycloak 26.0.7 démarré (`docker-compose up keycloak`)
- Accès admin Keycloak : http://localhost:8080
- Credentials admin : `admin` / `admin` (dev uniquement)

---

## 1. Configuration Google OAuth

### 1.1 Créer un projet Google Cloud

1. Aller sur https://console.cloud.google.com/
2. Créer un nouveau projet ou sélectionner un projet existant
3. Activer l'API "Google+ API" ou "People API"

### 1.2 Créer les credentials OAuth 2.0

1. Dans Google Cloud Console → **APIs & Services** → **Credentials**
2. Cliquer **Create Credentials** → **OAuth 2.0 Client ID**
3. Type d'application : **Web application**
4. Nom : `Tax Dividend AI - Keycloak`
5. **Authorized redirect URIs** :
   ```
   http://localhost:8080/realms/taxdividend/broker/google/endpoint
   ```
   ⚠️ **IMPORTANT** : L'URI doit correspondre exactement à celle de Keycloak

6. Sauvegarder et noter :
   - **Client ID** : `123456789-abc...apps.googleusercontent.com`
   - **Client Secret** : `GOCSPX-...`

### 1.3 Configurer Keycloak

1. Connexion Keycloak admin : http://localhost:8080
2. Sélectionner realm : **taxdividend**
3. **Identity Providers** → **Add provider** → **Google**
4. Remplir :
   - **Alias** : `google` (ne pas changer, utilisé dans le code)
   - **Display name** : `Google`
   - **Client ID** : Coller le Client ID de Google
   - **Client Secret** : Coller le Client Secret de Google
   - **Default Scopes** : `openid profile email`
   - **Trust Email** : `ON` (pour marquer l'email comme vérifié)
   - **Store Tokens** : `OFF` (pas besoin de stocker les tokens Google)
   - **Enabled** : `ON`

5. **Save**

6. Copier le **Redirect URI** affiché et vérifier qu'il correspond à celui configuré dans Google Cloud Console

### 1.4 Tester

1. Aller sur http://localhost:5173 (frontend)
2. Cliquer sur le bouton "Sign in with Google"
3. Sélectionner un compte Google
4. Autoriser l'accès
5. Vérifier que :
   - User est créé dans Keycloak (Users → List)
   - User est auto-provisionné dans la DB (`SELECT * FROM users WHERE registration_source = 'GOOGLE_SSO'`)
   - Email est marqué `is_verified = true`

---

## 2. Configuration GitHub OAuth

### 2.1 Créer une OAuth App GitHub

1. Aller sur https://github.com/settings/developers
2. **OAuth Apps** → **New OAuth App**
3. Remplir :
   - **Application name** : `Tax Dividend AI`
   - **Homepage URL** : `http://localhost:5173`
   - **Authorization callback URL** :
     ```
     http://localhost:8080/realms/taxdividend/broker/github/endpoint
     ```
4. Cliquer **Register application**
5. Noter :
   - **Client ID** : `Iv1.abc123...`
   - **Client Secret** : Cliquer **Generate a new client secret** et copier

### 2.2 Configurer Keycloak

1. Keycloak admin → Realm **taxdividend**
2. **Identity Providers** → **Add provider** → **GitHub**
3. Remplir :
   - **Alias** : `github` (ne pas changer)
   - **Display name** : `GitHub`
   - **Client ID** : Coller le Client ID de GitHub
   - **Client Secret** : Coller le Client Secret de GitHub
   - **Default Scopes** : `user:email`
   - **Trust Email** : `ON`
   - **Enabled** : `ON`

4. **Save**

### 2.3 Tester

1. Frontend → "Sign in with GitHub"
2. Autoriser l'accès
3. Vérifier auto-provisioning dans DB (`registration_source = 'GITHUB_SSO'`)

---

## 3. Configuration Microsoft Azure AD (optionnel)

### 3.1 Créer une App Registration Azure

1. Aller sur https://portal.azure.com/
2. **Azure Active Directory** → **App registrations** → **New registration**
3. Remplir :
   - **Name** : `Tax Dividend AI`
   - **Supported account types** : `Accounts in any organizational directory and personal Microsoft accounts`
   - **Redirect URI** : 
     ```
     http://localhost:8080/realms/taxdividend/broker/microsoft/endpoint
     ```
4. **Register**
5. Noter :
   - **Application (client) ID** : `abc-123-def...`
6. **Certificates & secrets** → **New client secret** → Noter la valeur

### 3.2 Configurer Keycloak

1. Keycloak admin → **Identity Providers** → **Add provider** → **Microsoft**
2. Remplir :
   - **Alias** : `microsoft`
   - **Client ID** : Application ID d'Azure
   - **Client Secret** : Client Secret d'Azure
   - **Trust Email** : `ON`
   - **Enabled** : `ON`

3. **Save**

---

## 4. Vérification de la configuration

### 4.1 Vérifier les Identity Providers

```bash
# Liste des Identity Providers configurés
curl -s http://localhost:8080/realms/taxdividend | jq '.identity_providers[]'
```

Devrait retourner :
```json
[
  {
    "alias": "google",
    "displayName": "Google",
    "providerId": "google"
  },
  {
    "alias": "github",
    "displayName": "GitHub",
    "providerId": "github"
  }
]
```

### 4.2 Vérifier le JWT

Après connexion SSO, vérifier le JWT contient `identity_provider` :

```bash
# Decoder le JWT (remplacer YOUR_JWT_TOKEN)
echo "YOUR_JWT_TOKEN" | jq -R 'split(".") | .[1] | @base64d | fromjson'
```

Devrait contenir :
```json
{
  "sub": "uuid-123...",
  "email": "user@gmail.com",
  "identity_provider": "google"  ← IMPORTANT
}
```

---

## 5. Troubleshooting

### Erreur : "Invalid redirect URI"

**Cause** : L'URI de redirection dans Google/GitHub ne correspond pas à celle de Keycloak

**Solution** :
1. Vérifier l'URI exacte dans Keycloak (Identity Providers → Google → Redirect URI)
2. Copier-coller cette URI dans Google Cloud Console / GitHub OAuth App
3. Vérifier qu'il n'y a pas d'espaces ou de caractères supplémentaires

### Erreur : "User not found in database"

**Cause** : Auto-provisioning ne fonctionne pas

**Solution** :
1. Vérifier que `identity_provider` est présent dans le JWT
2. Vérifier les logs du Backend :
   ```bash
   docker-compose logs backend | grep "Auto-provisioning"
   ```
3. Vérifier que le BFF extrait bien `identity_provider` :
   ```bash
   docker-compose logs bff-gateway | grep "identity_provider"
   ```

### Email non vérifié pour SSO

**Cause** : "Trust Email" est désactivé dans Keycloak

**Solution** :
1. Keycloak → Identity Providers → Google → **Trust Email** : `ON`
2. Sauvegarder
3. Déconnecter et reconnecter l'utilisateur

---

## 6. Configuration Production

Pour la production, mettre à jour les URIs de redirection :

```
https://app.taxdividend.com/realms/taxdividend/broker/google/endpoint
https://app.taxdividend.com/realms/taxdividend/broker/github/endpoint
```

Et configurer :
- HTTPS obligatoire
- Rate limiting sur les endpoints OAuth
- Monitoring des connexions SSO (audit logs)
- Rotation des Client Secrets tous les 6 mois

---

## Ressources

- [Keycloak Identity Providers](https://www.keycloak.org/docs/latest/server_admin/#_identity_broker)
- [Google OAuth 2.0](https://developers.google.com/identity/protocols/oauth2)
- [GitHub OAuth Apps](https://docs.github.com/en/developers/apps/building-oauth-apps)
