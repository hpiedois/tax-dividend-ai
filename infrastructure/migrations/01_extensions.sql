-- ============================================================================
-- EXTENSIONS INITIALIZATION - Tax Dividend AI
-- ============================================================================
-- Installation des extensions nécessaires pour l'infrastructure
-- ============================================================================

\echo '==> Installing PostgreSQL extensions...'

-- Extension UUID: using gen_random_uuid() (built-in since PostgreSQL 13)
-- No extension needed for UUID generation

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
