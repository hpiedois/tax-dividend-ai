Oui, il y a clairement du potentiel, surtout pour des résidents suisses avec titres français, mais le positionnement « particuliers vs pros » et la profondeur d’automatisation doivent être pensés finement.
Pourquoi il y a un vrai besoin
	•	Le processus 5000/5001 est mal compris, paperassier, avec délais longs et risque de rater les délais ou de mal remplir, surtout pour des particuliers sans support bancaire.
	•	Beaucoup de brokers (comme Swissquote et d’autres acteurs grand public) n’offrent pas de relief at source et laissent les clients se débrouiller pour les demandes de remboursement, ce qui crée un manque évident d’outils pratiques côté client final.
Idées de fonctionnalités pour particuliers
	•	Assistant de saisie guidée pour les formulaires 5000/5001 :
	•	questionnaire simple, génération automatique des PDF pré-remplis, checklist des pièces (relevés de dividendes, attestations de résidence, etc.).
	•	Suivi des dividendes et retenues :
	•	import automatique (ou semi-automatique) des relevés Swissquote/IBKR/etc., calcul du trop-perçu et des montants remboursables par année.
	•	Gestion des délais et rappels :
	•	calendrier des dates limites de réclamation, rappels email/notifications, archivage des demandes envoyées.
	•	« Mode débutant » :
	•	explications vulgarisées sur la convention France–Suisse, les taux applicables, ce qui est réaliste de réclamer et à partir de quel montant ça vaut le coup.
Une version « light » pourrait déjà se vendre comme SaaS low-cost ou application web, avec un modèle de type abonnement annuel + éventuellement un pack « support email » pour les cas compliqués.
Potentiel côté pros (fiduciaires, gérants, family offices)
	•	Pour un fiduciary / gérant / multi-family office, les demandes de remboursement sur dividendes étrangers sont récurrentes, mais très chronophages si traitées à la main client par client.
	•	Une application B2B pourrait offrir :
	•	gestion multi-clients, multi-portefeuilles,
	•	génération en masse des formulaires 5000/5001,
	•	exports structurés pour les autorités/fournisseurs de services (batch PDF ou XML),
	•	tableaux de bord de suivi des montants en jeu et des statuts de remboursement.
	•	Tu peux aussi viser un modèle « API / back-office » pour être utilisé par des banques locales ou des robo-advisors suisses qui ne veulent pas monter leur propre chaîne de traitement.
Ticket moyen et pouvoir d’achat étant plus élevés côté pros, une offre B2B (SaaS + setup + éventuellement success fee) est potentiellement plus rentable que le pur B2C, même avec moins de clients.
Points techniques et réglementaires à anticiper
	•	Sûreté juridique :
	•	bien cadrer ce que fait l’app (aide à la préparation de formulaires) vs activité de conseil fiscal réglementé, et s’appuyer sur les notices officielles françaises et suisses.
	•	Intégrations :
	•	connecteurs pour récupérer des données de transactions (PDF parsing, CSV exports de brokers), éventuellement agrégation via des API d’open banking là où disponible.
	•	Internationalisation future :
	•	architecture pensée pour réutiliser le moteur sur d’autres pays (Allemagne, Italie, etc.), en changeant seulement les « règles fiscales » et les modèles de formulaires.
Stratégie réaliste pour démarrer
	•	V1 très focalisée :
	•	cas d’usage « résident suisse personne physique, titres français détenus via un ou deux brokers typiques (Swissquote, IBKR) »,
	•	uniquement génération 5000/5001 + checklist pour envoi.
	•	Tester le marché :
	•	landing page, calculateur « combien pourriez-vous récupérer ? » pour valider l’intérêt,
	•	partenariats avec fiduciaires qui gèrent déjà des frontaliers / expatriés France–Suisse.
Vu ton profil dev + ton expérience avec fiscalité suisse, tu as un avantage pour prototyper rapidement une app orientée workflow (import relevés → calcul → génération PDF → suivi), puis la pousser vers du B2B si la traction B2C reste limitée. Si tu veux, on peut détailler une architecture technique (stack, modules, modèle de données) pour une V1 ciblée « résident suisse + actions françaises ».