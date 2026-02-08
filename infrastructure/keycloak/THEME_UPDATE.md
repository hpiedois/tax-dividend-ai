# Mise à jour du thème Keycloak

## Pour appliquer les changements du thème :

```bash
# 1. Arrêter Keycloak
docker compose stop keycloak

# 2. Supprimer le conteneur (pour forcer le réimport du realm)
docker compose rm -f keycloak

# 3. Optionnel: Supprimer la base de données Keycloak si le thème ne s'applique pas
docker compose rm -f keycloak-db
docker volume rm tax-dividend-ai_keycloak-db-data

# 4. Redémarrer Keycloak (réimporte automatiquement le realm avec le thème)
docker compose up -d keycloak

# 5. Vérifier que Keycloak est démarré (attendre ~30 secondes)
docker compose logs -f keycloak
```

## Vérification du thème

1. Ouvrir http://localhost:8180/realms/tax-dividend/account
2. La page devrait avoir le design personnalisé avec :
   - Gradient de fond (blanc/violet)
   - Card avec backdrop-blur
   - Style moderne avec Tailwind CSS

## Si le thème ne s'applique toujours pas

### Vérifier que le thème est bien chargé :
```bash
# Se connecter au conteneur
docker compose exec keycloak bash

# Lister les thèmes disponibles
ls -la /opt/keycloak/themes/

# Vérifier le thème tax-dividend
ls -la /opt/keycloak/themes/tax-dividend/login/
```

### Forcer la recompilation du CSS (si nécessaire) :
```bash
cd infrastructure/keycloak/themes/tax-dividend
npm run build
```

### Via l'admin Keycloak :
1. Ouvrir http://localhost:8180/admin
2. Login: `admin` / `admin`
3. Sélectionner le realm **tax-dividend**
4. Aller dans **Realm settings** → **Themes**
5. Vérifier que "Login theme" est bien défini sur **tax-dividend**
6. Sauvegarder

## Structure du thème

```
infrastructure/keycloak/themes/tax-dividend/
├── login/
│   ├── theme.properties         # Configuration du thème
│   ├── template.ftl             # Template principal avec Tailwind
│   ├── login.ftl                # Page de login
│   ├── register.ftl             # Page d'inscription
│   └── resources/
│       └── css/
│           ├── input.css        # CSS source
│           └── output.css       # CSS compilé (Tailwind)
├── package.json                 # Build config
└── tailwind.config.js          # Tailwind config
```
