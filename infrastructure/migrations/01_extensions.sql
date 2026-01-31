-- ============================================================================
-- EXTENSIONS INITIALIZATION - Tax Dividend AI
-- ============================================================================
-- Installation des extensions nécessaires pour l'infrastructure
-- ============================================================================

\echo '==> Installing PostgreSQL extensions...'

-- Extension UUID pour générer des identifiants uniques
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Extension pour le chiffrement (passwords, sensitive data)
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Extension pour les statistiques de requêtes (monitoring)
CREATE EXTENSION IF NOT EXISTS pg_stat_statements;

-- Extension pour la recherche plein texte (fuzzy search)
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- Extension pour la recherche sans accents
CREATE EXTENSION IF NOT EXISTS unaccent;

\echo '==> Extensions installed successfully!'
\echo ''
