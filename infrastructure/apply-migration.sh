#!/usr/bin/env bash

# ============================================================================
# APPLY INFRASTRUCTURE MIGRATION SCRIPT
# ============================================================================
# Ce script applique manuellement des migrations PostgreSQL d'infrastructure
# sur un conteneur en cours d'exécution
# ============================================================================

set -euo pipefail

# Configuration
CONTAINER_NAME="${POSTGRES_CONTAINER:-tax-dividend-postgres}"
DB_NAME="${POSTGRES_DB:-taxdividend}"
DB_USER="${POSTGRES_USER:-postgres}"
MIGRATIONS_DIR="$(dirname "$0")/migrations"

# Couleurs pour les messages
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Fonctions utilitaires
log_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

log_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

log_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

log_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Vérifier que le conteneur PostgreSQL est en cours d'exécution
check_container() {
    if ! docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
        log_error "Le conteneur PostgreSQL '${CONTAINER_NAME}' n'est pas en cours d'exécution"
        log_info "Démarrez-le avec : cd $(dirname "$0") && docker-compose up -d postgres"
        exit 1
    fi
    log_success "Conteneur '${CONTAINER_NAME}' trouvé et actif"
}

# Afficher l'aide
show_help() {
    cat << EOF
Usage: $0 [MIGRATION_FILE]

Applique une ou plusieurs migrations SQL d'infrastructure sur PostgreSQL.

⚠️  IMPORTANT: Ce script applique uniquement les migrations d'INFRASTRUCTURE:
    - Extensions PostgreSQL
    - Schémas
    - Rôles/Users

    Les TABLES de l'application sont gérées par Flyway dans le backend !

Arguments:
    MIGRATION_FILE    Nom du fichier de migration à appliquer (optionnel)
                      Si omis, applique toutes les migrations dans l'ordre

Exemples:
    # Appliquer une migration spécifique
    $0 01_extensions.sql

    # Appliquer toutes les migrations (dans l'ordre)
    $0

Variables d'environnement:
    POSTGRES_CONTAINER    Nom du conteneur PostgreSQL (défaut: tax-dividend-postgres)
    POSTGRES_DB           Nom de la base de données (défaut: taxdividend)
    POSTGRES_USER         Utilisateur PostgreSQL (défaut: postgres)

Notes:
    - Ce script est idempotent : les migrations utilisent IF NOT EXISTS
    - Il vérifie l'état du conteneur avant d'appliquer les migrations
    - Les migrations sont exécutées dans l'ordre alphabétique

EOF
}

# Appliquer une migration spécifique
apply_migration() {
    local migration_file="$1"
    local migration_path="${MIGRATIONS_DIR}/${migration_file}"

    if [[ ! -f "$migration_path" ]]; then
        log_error "Fichier de migration introuvable : ${migration_path}"
        exit 1
    fi

    log_info "Application de la migration : ${migration_file}"

    if docker exec -i "${CONTAINER_NAME}" psql -U "${DB_USER}" -d "${DB_NAME}" < "${migration_path}"; then
        log_success "Migration appliquée avec succès : ${migration_file}"
        return 0
    else
        log_error "Échec de l'application de la migration : ${migration_file}"
        return 1
    fi
}

# Appliquer toutes les migrations
apply_all_migrations() {
    log_info "Application de toutes les migrations d'infrastructure dans l'ordre..."

    local migration_count=0
    local success_count=0

    for migration_file in "${MIGRATIONS_DIR}"/*.sql; do
        if [[ -f "$migration_file" ]]; then
            migration_count=$((migration_count + 1))
            local filename=$(basename "$migration_file")

            log_info "[$migration_count] ${filename}"

            if docker exec -i "${CONTAINER_NAME}" psql -U "${DB_USER}" -d "${DB_NAME}" < "${migration_file}"; then
                success_count=$((success_count + 1))
            else
                log_warning "Migration échouée (peut être déjà appliquée) : ${filename}"
            fi
        fi
    done

    echo ""
    log_success "${success_count}/${migration_count} migrations traitées"
}

# Vérifier l'état de la base de données
check_database_status() {
    log_info "État de la base de données '${DB_NAME}' :"
    echo ""
    docker exec -it "${CONTAINER_NAME}" psql -U "${DB_USER}" -d "${DB_NAME}" -c "\dn"
    echo ""
    docker exec -it "${CONTAINER_NAME}" psql -U "${DB_USER}" -d "${DB_NAME}" -c "SELECT rolname FROM pg_roles WHERE rolname LIKE 'taxdividend%';"
}

# Main
main() {
    # Afficher l'aide si demandé
    if [[ "${1:-}" == "-h" ]] || [[ "${1:-}" == "--help" ]]; then
        show_help
        exit 0
    fi

    echo ""
    log_info "🚀 Script d'application de migrations d'INFRASTRUCTURE PostgreSQL"
    log_warning "Les tables de l'application sont gérées par Flyway (backend)"
    echo ""

    # Vérifier que le conteneur est actif
    check_container

    echo ""

    # Appliquer les migrations
    if [[ $# -eq 0 ]]; then
        # Aucun argument : appliquer toutes les migrations
        apply_all_migrations
    else
        # Argument fourni : appliquer la migration spécifique
        apply_migration "$1"
    fi

    echo ""

    # Afficher l'état final
    check_database_status

    echo ""
    log_success "✨ Terminé !"
    log_info "Pour créer les tables de l'application, démarrez le backend (Flyway s'exécutera automatiquement)"
}

# Exécuter le script
main "$@"
