. Roadmap de développement (macro)
Phase 0 – Cadrage (1–2 semaines)
	•	Valider le périmètre initial :
	•	pays source : France uniquement,
	•	pays de résidence : Suisse,
	•	types de revenus : dividendes actions cotées.
	•	Définir 2–3 personas cibles : particulier Swissquote/IBKR, petite fiduciaire, family office.
	•	Spécifier les premiers parcours :
	•	import dividendes → calcul → génération 5000/5001,
	•	gestion d’un client (identité + résidence fiscale).
Phase 1 – MVP monolithique « logique métier » (4–6 semaines)
Objectif : un monolithe Spring Boot bien modularisé en packages internes (qui seront plus tard des microservices), avec un frontend simple.
	•	Backend :
	•	Authentification basique (user / password, JWT), rôle `USER` / `ADMIN`.
	•	Modèle de données core (voir section suivante).
	•	Import CSV (Swissquote/IBKR) pour les dividendes.
	•	Moteur de calcul France–Suisse (taux interne FR, taux conventionnel, trop‑perçu).
	•	Génération de PDF 5000/5001 pré‑remplis à partir de modèles.
	•	Frontend :
	•	Tableau de bord simple.
	•	Création d’un « client fiscal » (toi-même dans un premier temps).
	•	Upload CSV et affichage des dividendes, marquage de ceux à inclure dans un dossier de remboursement.
	•	Téléchargement des PDF générés.
Déployer en environnement de test (Docker Compose).
Phase 2 – Séparation logique en microservices (4–6 semaines)
Objectif : passer du monolithe à 3–4 microservices clairs, avec une API Gateway.
	•	Extraire en services :
	•	`identity-service` (users, clients fiscaux).
	•	`portfolio-service` (dividendes, titres, brokers).
	•	`tax-engine-service` (règles, calculs, génération des dossiers).
	•	`document-service` (génération PDF / stockage).
	•	Ajouter un `api-gateway` (Spring Cloud Gateway / Traefik / NGINX) et un `auth-service` (Keycloak ou Spring Authorization Server).
	•	Mise en place d’un bus d’événements (Kafka ou RabbitMQ) pour notifier la création / mise à jour de dossiers.
Phase 3 – B2B / multi‑pays (6–10 semaines)
	•	Multi‑pays côté source (ex. ajout Allemagne) et côté résidence (ex. Allemagne, Italie).
	•	Multi‑clients pour les fiduciaires (gestion d’un portefeuille de contribuables).
	•	API publiques : endpoints documentés (OpenAPI) pour intégration par des banques / gérants.
2. Modèle de données – tables principales
Je te propose un modèle logique (tu peux le traduire en PostgreSQL). C’est pensé pour être multi‑pays dès le départ.
2.1. Identité & multitenant
	•	`tenant`
	•	`id`
	•	`name` (p.ex. « Fiduciaire X », ou « Particulier – default »)
	•	`type` (INDIVIDUAL, FIRM, BANK)
	•	`created_at`, `updated_at`
	•	`app_user`
	•	`id`
	•	`tenant_id` (FK → `tenant`)
	•	`email` (unique par tenant)
	•	`password_hash`
	•	`role` (ADMIN, USER, VIEWER)
	•	`created_at`, `updated_at`
	•	`tax_client` (le « contribuable » géré par la plateforme)
	•	`id`
	•	`tenant_id` (FK → `tenant`)
	•	`type` (INDIVIDUAL, COMPANY)
	•	`full_name` / `company_name`
	•	`date_of_birth` (si individu)
	•	`address_line1`, `address_line2`, `postal_code`, `city`, `country_code`
	•	`tax_identification_number` (NIF, AVS, etc.)
	•	`residence_country_code`
	•	`created_at`, `updated_at`
2.2. Titres / brokers / dividendes
	•	`broker`
	•	`id`
	•	`tenant_id` (optionnel, si tu veux permettre des brokers spécifiques à un tenant, sinon global)
	•	`name` (Swissquote, IBKR, …)
	•	`country_code`
	•	`supports_api` (bool)
	•	`created_at`, `updated_at`
	•	`security` (titre / action)
	•	`id`
	•	`isin`
	•	`ticker`
	•	`name`
	•	`issuer_country_code` (FR, CH, etc.)
	•	`created_at`, `updated_at`
	•	`portfolio_account`
	•	`id`
	•	`tax_client_id` (FK → `tax_client`)
	•	`broker_id` (FK → `broker`)
	•	`account_ref` (référence compte broker)
	•	`currency`
	•	`created_at`, `updated_at`
	•	`dividend_event` (événement de dividende sur un titre)
	•	`id`
	•	`security_id` (FK → `security`)
	•	`ex_date`
	•	`payment_date`
	•	`gross_amount_per_share`
	•	`currency`
	•	`source_country_code` (important pour le moteur fiscal)
	•	`created_at`, `updated_at`
	•	`dividend_payment` (dividende effectivement reçu par un client sur un compte)
	•	`id`
	•	`tax_client_id` (FK → `tax_client`)
	•	`portfolio_account_id` (FK → `portfolio_account`)
	•	`dividend_event_id` (FK → `dividend_event`)
	•	`shares_held`
	•	`gross_amount_total`
	•	`withholding_tax_amount`
	•	`withholding_tax_rate`
	•	`net_amount`
	•	`currency`
	•	`received_date`
2.3. Règles fiscales & conventions
	•	`tax_convention`
	•	`id`
	•	`source_country_code` (p.ex. FR)
	•	`residence_country_code` (p.ex. CH)
	•	`name` (Convention fiscale France – Suisse)
	•	`effective_from`
	•	`effective_to` (nullable)
	•	`tax_rate_rule` (paramétrable par type de revenu)
	•	`id`
	•	`tax_convention_id` (FK → `tax_convention`, nullable si « droit interne seulement »)
	•	`income_type` (DIVIDEND, INTEREST, ROYALTY, etc.)
	•	`domestic_rate` (taux interne, ex. 25% FR)
	•	`treaty_rate` (taux conventionnel, ex. 15% pour dividendes de portefeuilles)
	•	`valid_from`, `valid_to`
	•	`tax_refund_case` (dossier de remboursement)
	•	`id`
	•	`tax_client_id`
	•	`source_country_code`
	•	`residence_country_code`
	•	`income_type` (DIVIDEND, …)
	•	`period_start` (p.ex. début d’année)
	•	`period_end`
	•	`status` (DRAFT, READY_FOR_SIGNATURE, SENT, ACCEPTED, REJECTED, CLOSED)
	•	`estimated_refund_amount`
	•	`actual_refund_amount` (une fois connu)
	•	`created_at`, `updated_at`
	•	`tax_refund_case_item` (ligne de dossier, lié à des paiements de dividendes)
	•	`id`
	•	`tax_refund_case_id`
	•	`dividend_payment_id`
	•	`gross_amount`
	•	`withholding_tax_amount`
	•	`refundable_amount`
2.4. Documents & formulaires
	•	`document_template`
	•	`id`
	•	`name` (FORM_5000_FR_2024, FORM_5001_FR_2024, etc.)
	•	`country_code`
	•	`income_type`
	•	`language`
	•	`version`
	•	`storage_path` (où se trouve le modèle PDF/HTML)
	•	`generated_document`
	•	`id`
	•	`tax_refund_case_id`
	•	`document_template_id`
	•	`file_path` (ou storage key S3/MinIO)
	•	`created_at`
3. Découpage en microservices
Le but est de garder les bounded contexts clairs. Tu peux partir sur 4–5 services principaux, plus une gateway + auth.
3.1. `auth-service`
	•	Gère : utilisateurs, tenants, rôles, tokens.
	•	Tech : Keycloak ou Spring Authorization Server.
	•	Base de données propre pour `app_user`, `tenant`.
	•	Expose :
	•	`/auth/token`
	•	`/auth/users` (admin)
3.2. `identity-service`
	•	Gère : `tax_client`.
	•	Expose :
	•	CRUD clients fiscaux.
	•	API pour retrouver les clients par tenant, NIF, etc.
3.3. `portfolio-service`
	•	Gère : `broker`, `security`, `portfolio_account`, `dividend_event`, `dividend_payment`.
	•	Expose :
	•	Import de dividendes (CSV upload ou API).
	•	Consultation des dividendes par client/période.
3.4. `tax-engine-service`
	•	Cœur métier fiscal.
	•	Gère : `tax_convention`, `tax_rate_rule`, `tax_refund_case`, `tax_refund_case_item`.
	•	Expose :
	•	Endpoint pour calculer un `tax_refund_case` à partir d’un ensemble de `dividend_payment_id`.
	•	Endpoint pour recalculer un dossier (si ajout de dividendes).
	•	Endpoint pour obtenir les montants remboursables par client/période.
	•	Consomme :
	•	API de `portfolio-service` pour les détails des dividendes.
	•	Éventuellement des events (ex. « nouveaux dividendes importés »).
3.5. `document-service`
	•	Gère : `document_template`, `generated_document`.
	•	Expose :
	•	Endpoint pour générer un PDF à partir d’un `tax_refund_case` (via API `tax-engine-service`).
	•	Endpoint pour lister / télécharger les documents.
	•	Implémentation :
	•	moteur de templates (HTML + PDF) ou remplissage de formulaires PDF.
3.6. `api-gateway`
	•	Point d’entrée unique (front + API externes).
	•	Fait :
	•	routage vers les services internes,
	•	auth (vérification token, propagation du tenant-id, etc.),
	•	throttling / logging.
3.7. Communication & infrastructure
	•	Communication sync : REST (Spring WebFlux si tu veux, mais REST classique suffit).
	•	Communication async :
	•	Kafka ou RabbitMQ pour les événements type `DIVIDENDS_IMPORTED`, `REFUND_CASE_CREATED`.
	•	Observabilité :
	•	chaque microservice avec Prometheus metrics + logs centralisés (ELK / OpenSearch).
4. Extension multi‑pays dès le départ
Avec ce modèle, l’extension à d’autres pays se fait principalement par :
	•	Ajout de lignes dans `tax_convention` et `tax_rate_rule` (nouveaux couples source/résidence, nouveaux taux).
	•	Nouveaux `document_template` pour les formulaires locaux (p.ex. formulaires allemands, italiens).
	•	Un module de « mapping de formulaires » dans `tax-engine-service` et `document-service` qui associe :
	•	`source_country_code` + `residence_country_code` + `income_type` → templates et règles spécifiques.
Tu peux commencer par France source / Suisse résidence, puis ajouter un second cas (ex. Allemagne source / Suisse résidence) pour tester la généricité du moteur sans faire exploser la complexité.
Si tu veux, on peut faire ensuite un exemple d’API contract (OpenAPI) pour la création d’un `tax_refund_case` à partir de dividendes d’un client (c’est un bon pivot pour aligner backend et frontend dès le début).