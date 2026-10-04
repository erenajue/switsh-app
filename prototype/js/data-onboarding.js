/* ===== Domaine 1 — Onboarding, Sécurité & Conformité (KYC/AML) ===== */

SCREENS.push({
  id: 'onb-inscription',
  group: 'onboarding',
  title: 'Inscription',
  subtitle: 'Écran « Inscription »',
  chrome: 'sub',
  step: [1, 5],
  meta: { br: ['BR-ONB-01'], qc: ['QC-ONB-01', 'QC-ONB-02'], note: 'Compte créé uniquement après confirmation OTP + e-mail valide.' },
  fields: [
    F({ label: 'Numéro de téléphone', specType: 'Texte / MSISDN', ui: 'tel', required: 'yes', prefix: '+243', placeholder: '9XX XXX XXX', hint: 'Format RDC +243 + 9 chiffres ; unique en base' }),
    F({ label: 'Adresse e-mail', specType: 'Texte / e-mail', ui: 'email', required: 'yes', placeholder: 'nom@exemple.cd', hint: 'Format RFC 5322 ; unique en base' }),
    F({ label: 'Mot de passe', specType: 'Texte masqué', ui: 'password', required: 'yes', hint: 'Min. 8 caractères, majuscule + chiffre + caractère spécial' }),
    F({ label: 'Consentement CGU/CGV', specType: 'Case à cocher', ui: 'checkbox', required: 'yes', hint: 'Doit être cochée pour poursuivre' }),
  ],
  qcList: [
    { id: 'QC-ONB-01', rule: 'Format RDC (+243 + 9 chiffres) et unicité en base.', sev: 'Bloquant' },
    { id: 'QC-ONB-02', rule: 'Syntaxe e-mail RFC 5322 valide et unicité en base.', sev: 'Bloquant' },
  ],
  body: () => `
    <h1 class="block-title">Créer votre compte</h1>
    <p class="block-sub">100% mobile — vérifié par SMS et e-mail</p>
    <div class="field"><label>Numéro de téléphone <span class="req">*</span></label>
      <div class="input-group"><span class="prefix">🇨🇩 +243</span><input class="input has-prefix" placeholder="9XX XXX XXX" style="padding-left:4.75rem;" /></div>
      <div class="hint">Format RDC +243 suivi de 9 chiffres</div>
    </div>
    ${renderField(F({ label: 'Adresse e-mail', ui: 'email', required: 'yes', placeholder: 'nom@exemple.cd' }))}
    ${renderField(F({ label: 'Mot de passe', ui: 'password', required: 'yes', hint: 'Min. 8 caractères, majuscule, chiffre, caractère spécial' }))}
    <div class="checkline checked" style="margin:var(--space-4) 0;">
      <div class="box">${ICONS.check}</div>
      <span>J\'accepte les <a href="#">Conditions générales</a> et la <a href="#">Politique de confidentialité</a> Switsh.</span>
    </div>
    <div class="sticky-cta"><button class="btn btn-primary">Recevoir le code SMS</button></div>
  `,
});

SCREENS.push({
  id: 'onb-otp',
  group: 'onboarding',
  title: 'Confirmation du numéro',
  subtitle: 'Étape de vérification de l\'écran « Inscription »',
  chrome: 'sub',
  step: [2, 5],
  bonus: true,
  meta: { br: ['BR-ONB-01'], qc: ['QC-ONB-01'], note: 'Étape UX séparant la saisie OTP de l\'inscription pour plus de clarté — champ « Code OTP » de l\'écran « Inscription » du BRD.' },
  fields: [
    F({ label: 'Code OTP reçu', specType: 'Numérique, 6 chiffres', ui: 'otp', required: 'yes', hint: 'Validité 5 minutes, 3 tentatives max' }),
  ],
  body: () => `
    <h1 class="block-title">Entrez le code reçu</h1>
    <p class="block-sub">Un code à 6 chiffres a été envoyé au <strong>+243 9XX XXX XXX</strong></p>
    ${renderField(F({ label: 'Code OTP', ui: 'otp', required: 'yes' }))}
    <p style="font-size:var(--text-xs);color:var(--color-text-faint);margin-top:var(--space-2);">Le code expire dans <strong style="color:var(--color-primary);">04:12</strong> · 3 tentatives maximum</p>
    <div class="notice-box notice-info" style="margin-top:var(--space-5);">${ICONS.shield}<span>Ce code confirme votre numéro de ligne RDC conformément aux exigences ARPTC (BR-ONB-03).</span></div>
    <div class="sticky-cta"><button class="btn btn-primary">Vérifier</button><div style="text-align:center;margin-top:var(--space-3);"><button class="btn-ghost btn-sm" style="color:var(--color-primary);">Renvoyer le code</button></div></div>
  `,
});

SCREENS.push({
  id: 'onb-auth-security',
  group: 'onboarding',
  title: 'Authentification & sécurité',
  subtitle: 'Écran « Authentification & sécurité »',
  chrome: 'sub',
  step: [3, 5],
  meta: { br: ['BR-ONB-02'], qc: [], note: 'Aucune opération sensible ne peut être exécutée sans authentification forte (SCA).' },
  fields: [
    F({ label: 'Code PIN', specType: 'Numérique, 4 ou 6 chiffres', ui: 'text', required: 'yes', hint: 'Ne doit pas être une séquence triviale (0000, 1234)' }),
    F({ label: 'Confirmation du PIN', specType: 'Numérique', ui: 'password', required: 'yes', hint: 'Doit être identique au PIN saisi' }),
    F({ label: 'Activation biométrie', specType: 'Bascule Oui/Non', ui: 'toggle', required: 'no', hint: 'Disponible selon capteur de l\'appareil', on: true }),
    F({ label: 'Type de biométrie détecté', specType: 'Liste (Face ID / Empreinte)', ui: 'readonly', required: 'ro', hint: 'Renseigné automatiquement par l\'appareil', placeholder: 'Face ID détecté' }),
  ],
  body: () => `
    <h1 class="block-title">Sécurisez votre compte</h1>
    <p class="block-sub">Définissez un code PIN et activez la biométrie</p>
    ${renderField(F({ label: 'Créer un code PIN', ui: 'password', required: 'yes', hint: '4 ou 6 chiffres, jamais de séquence triviale' }))}
    ${renderField(F({ label: 'Confirmer le code PIN', ui: 'password', required: 'yes' }))}
    <div class="card" style="margin-top:var(--space-2);">
      ${renderField(F({ label: 'Face ID', ui: 'toggle', on: true, placeholder: 'Utiliser Face ID pour déverrouiller' }))}
      ${renderField(F({ label: 'Empreinte digitale', ui: 'toggle', on: false, placeholder: 'Utiliser l\'empreinte digitale' }))}
    </div>
    <div class="notice-box notice-success">${ICONS.shield}<span>Biométrie détectée automatiquement : Face ID disponible sur cet appareil.</span></div>
    <div class="sticky-cta"><button class="btn btn-primary">Activer la sécurité</button></div>
  `,
});

SCREENS.push({
  id: 'onb-profile-select',
  group: 'onboarding',
  title: 'Sélection du profil client',
  subtitle: 'Écran « Sélection du profil client »',
  chrome: 'sub',
  step: [4, 5],
  meta: { br: ['BR-ONB-01'], note: 'Détermine le parcours KYC (particulier) ou KYB (entreprise) suivant.' },
  fields: [
    F({ label: 'Type de client', specType: 'Liste (Particulier / Entreprise)', ui: 'select', required: 'yes', hint: 'Détermine le parcours KYC/KYB suivant', options: ['Particulier', 'Entreprise / PME'] }),
    F({ label: 'Pays de résidence fiscale', specType: 'Liste pays', ui: 'select', required: 'yes', hint: 'RDC par défaut ; impacte les règles de conformité applicables', options: ['République démocratique du Congo', 'Autre pays'] }),
  ],
  body: () => `
    <h1 class="block-title">Quel type de compte souhaitez-vous ouvrir ?</h1>
    <p class="block-sub">Ce choix oriente votre parcours de vérification d\'identité</p>
    <div class="choice-card sel"><div class="icon">${ICONS.user}</div><div class="txt"><strong>Particulier</strong><span>Compte personnel bidevises USD/CDF</span></div></div>
    <div class="choice-card"><div class="icon">${ICONS.building}</div><div class="txt"><strong>Entreprise / PME</strong><span>Compte société — dossier KYB requis</span></div></div>
    ${renderField(F({ label: 'Pays de résidence fiscale', ui: 'select', required: 'yes', options: ['République démocratique du Congo', 'Autre pays'] }))}
    <div class="sticky-cta"><button class="btn btn-primary">Continuer</button></div>
  `,
});

SCREENS.push({
  id: 'onb-id-particulier',
  group: 'onboarding',
  title: 'Pièce d\'identité',
  subtitle: 'Écran « Pièce d\'identité — Particulier »',
  chrome: 'sub',
  step: [5, 5],
  meta: {
    br: ['BR-ID-01', 'BR-ID-02'], qc: ['QC-ONB-04', 'QC-ONB-05', 'QC-ONB-06', 'QC-ONB-07'],
    note: 'Extraction OCR automatique ; revue manuelle si le taux de confiance est insuffisant. Rejet immédiat en cas d\'échec du Liveness Check.',
  },
  fields: [
    F({ label: 'Type de document', specType: 'Liste (CENI / ONIP-NIN / Passeport / Carte de résident)', ui: 'select', required: 'yes', hint: 'Doit appartenir à la liste des documents admis en RDC', options: ['CENI', 'ONIP / NIN', 'Passeport biométrique', 'Carte de résident'] }),
    F({ label: 'Numéro du document', specType: 'Alphanumérique', ui: 'text', required: 'yes', hint: 'Format spécifique par type ; unicité en base' }),
    F({ label: 'Date d\'émission', specType: 'Date', ui: 'date', required: 'yes', hint: 'Doit être antérieure à la date du jour' }),
    F({ label: 'Date d\'expiration', specType: 'Date', ui: 'date', required: 'yes', hint: 'Doit être postérieure à la date du jour' }),
    F({ label: 'Photo recto', specType: 'Image', ui: 'file', required: 'yes', hint: 'Résolution minimale et lisibilité vérifiées par l\'OCR', icon: 'camera' }),
    F({ label: 'Photo verso', specType: 'Image', ui: 'file', required: 'cond', hint: 'Requis pour les documents à double face (CENI, carte de résident)', icon: 'camera' }),
    F({ label: 'Selfie de vivacité', specType: 'Image/vidéo courte', ui: 'file', required: 'yes', hint: 'Analysé par le moteur de Liveness', icon: 'face' }),
  ],
  engines: [
    { name: 'Moteur OCR d\'extraction documentaire', logic: 'Extrait nom, prénom, date de naissance, n° document, date d\'expiration + score de confiance par champ (seuil auto ≥ 85 %).' },
    { name: 'Moteur de Liveness / biométrie faciale', logic: 'Détection de vivacité + comparaison faciale selfie/document → score 0–100 et décision GO/NO-GO.' },
  ],
  qcList: [
    { id: 'QC-ONB-04', rule: 'Score de confiance OCR ≥ 85 % par champ, sinon revue manuelle.', sev: 'Bloquant jusqu\'à revue' },
    { id: 'QC-ONB-05', rule: 'Score de vivacité et de correspondance faciale ≥ seuil paramétré, sinon rejet automatique.', sev: 'Bloquant' },
  ],
  body: () => `
    <h1 class="block-title">Vérifiez votre identité</h1>
    <p class="block-sub">Document officiel RDC admis</p>
    ${renderField(F({ label: 'Type de document', ui: 'select', required: 'yes', options: ['CENI', 'ONIP / NIN', 'Passeport biométrique', 'Carte de résident'] }))}
    ${renderField(F({ label: 'Numéro du document', ui: 'text', required: 'yes', placeholder: 'Ex. 12-3456789-A00812' }))}
    <div style="display:flex;gap:var(--space-3);">
      <div style="flex:1;">${renderField(F({ label: 'Date d\'émission', ui: 'date', required: 'yes' }))}</div>
      <div style="flex:1;">${renderField(F({ label: 'Date d\'expiration', ui: 'date', required: 'yes' }))}</div>
    </div>
    <div class="section-label">Documents & selfie</div>
    <div class="upload-box done">${ICONS.checkCircle}<strong>Photo recto — capturée</strong><span>Netteté vérifiée par l\'OCR</span></div>
    ${renderField(F({ label: 'Photo verso', ui: 'file', required: 'cond', icon: 'camera', placeholder: 'Photographier le verso' }))}
    ${renderField(F({ label: 'Selfie de vivacité', ui: 'file', required: 'yes', icon: 'face', placeholder: 'Lancer le contrôle de vivacité' }))}
    <div class="sticky-cta"><button class="btn btn-primary">Valider le document</button></div>
  `,
});

SCREENS.push({
  id: 'onb-infos-perso',
  group: 'onboarding',
  title: 'Informations personnelles',
  subtitle: 'Écran « Informations personnelles »',
  chrome: 'sub',
  meta: { br: ['BR-ID-01', 'BR-AML-03'], qc: ['QC-ONB-03'], note: 'Le nom et prénom doivent correspondre au document scanné (contrôle OCR) ; âge ≥ 18 ans révolus.' },
  fields: [
    F({ label: 'Nom', specType: 'Texte', ui: 'text', required: 'yes', hint: 'Doit correspondre au document scanné (contrôle OCR)' }),
    F({ label: 'Prénom(s)', specType: 'Texte', ui: 'text', required: 'yes', hint: 'Doit correspondre au document scanné' }),
    F({ label: 'Date de naissance', specType: 'Date', ui: 'date', required: 'yes', hint: 'Âge ≥ 18 ans révolus' }),
    F({ label: 'Lieu de naissance', specType: 'Texte', ui: 'text', required: 'yes' }),
    F({ label: 'Sexe', specType: 'Liste (M/F)', ui: 'select', required: 'yes', options: ['Masculin', 'Féminin'] }),
    F({ label: 'Nationalité', specType: 'Liste pays', ui: 'select', required: 'yes', hint: 'Utilisée pour le screening PPE/sanctions', options: ['Congolaise (RDC)', 'Autre'] }),
    F({ label: 'Adresse complète', specType: 'Texte', ui: 'text', required: 'yes' }),
    F({ label: 'Ville / Commune / Quartier', specType: 'Texte', ui: 'text', required: 'yes' }),
    F({ label: 'Profession', specType: 'Liste / texte libre', ui: 'text', required: 'yes', hint: 'Utilisée par le moteur de scoring de risque' }),
    F({ label: 'Source des revenus déclarée', specType: 'Liste', ui: 'select', required: 'yes', hint: 'Utilisée pour le scoring AML', options: ['Salaire', 'Activité commerciale', 'Autre'] }),
    F({ label: 'Revenu mensuel estimé', specType: 'Numérique', ui: 'number', required: 'no', hint: 'Utilisé pour le calcul de plafonds recommandés' }),
  ],
  body: () => `
    <h1 class="block-title">Vos informations</h1>
    <p class="block-sub">Pré-remplies depuis votre pièce d\'identité</p>
    <div style="display:flex;gap:var(--space-3);">
      <div style="flex:1;">${renderField(F({ label: 'Nom', ui: 'text', required: 'yes', placeholder: 'KABUYA' }))}</div>
      <div style="flex:1;">${renderField(F({ label: 'Prénom(s)', ui: 'text', required: 'yes', placeholder: 'Mireille' }))}</div>
    </div>
    <div style="display:flex;gap:var(--space-3);">
      <div style="flex:1;">${renderField(F({ label: 'Date de naissance', ui: 'date', required: 'yes' }))}</div>
      <div style="flex:1;">${renderField(F({ label: 'Sexe', ui: 'select', required: 'yes', options: ['Masculin', 'Féminin'] }))}</div>
    </div>
    ${renderField(F({ label: 'Lieu de naissance', ui: 'text', required: 'yes', placeholder: 'Kinshasa, RDC' }))}
    ${renderField(F({ label: 'Nationalité', ui: 'select', required: 'yes', options: ['Congolaise (RDC)', 'Autre'] }))}
    ${renderField(F({ label: 'Adresse complète', ui: 'text', required: 'yes', placeholder: 'Avenue de la Paix, n°12' }))}
    ${renderField(F({ label: 'Ville / Commune / Quartier', ui: 'text', required: 'yes', placeholder: 'Kinshasa / Gombe / Croisée' }))}
    ${renderField(F({ label: 'Profession', ui: 'text', required: 'yes', placeholder: 'Commerçante' }))}
    ${renderField(F({ label: 'Source des revenus déclarée', ui: 'select', required: 'yes', options: ['Salaire', 'Activité commerciale', 'Autre'] }))}
    ${renderField(F({ label: 'Revenu mensuel estimé (USD)', ui: 'number', required: 'no', placeholder: '0' }))}
    <div class="sticky-cta"><button class="btn btn-primary">Continuer</button></div>
  `,
});

SCREENS.push({
  id: 'onb-justif-domicile',
  group: 'onboarding',
  title: 'Justificatif de domicile',
  subtitle: 'Écran « Justificatif de domicile »',
  chrome: 'sub',
  meta: { br: ['BR-ID-03'], note: 'Document requis pour l\'accès au Niveau 3 KYC ; revue manuelle si aucune preuve fiable n\'est disponible.' },
  fields: [
    F({ label: 'Type de justificatif', specType: 'Liste (attestation / facture REGIDESO-SNEL / géolocalisation / déclaration sur l\'honneur)', ui: 'select', required: 'yes', hint: 'Requis pour l\'accès au Niveau 3 KYC', options: ['Attestation de résidence', 'Facture REGIDESO / SNEL', 'Géolocalisation certifiée', 'Déclaration sur l\'honneur'] }),
    F({ label: 'Date d\'émission du justificatif', specType: 'Date', ui: 'date', required: 'yes', hint: 'Ancienneté maximale recommandée : 3 mois' }),
    F({ label: 'Fichier scanné / photo', specType: 'Image/PDF', ui: 'file', required: 'cond', hint: 'Requis sauf en cas de géolocalisation certifiée', icon: 'file' }),
    F({ label: 'Géolocalisation', specType: 'Coordonnées GPS', ui: 'toggle', required: 'cond', hint: 'Requise si type = géolocalisation certifiée' }),
  ],
  body: () => `
    <h1 class="block-title">Justificatif de domicile</h1>
    <p class="block-sub">Nécessaire pour débloquer le Niveau 3 (plafonds élevés)</p>
    ${renderField(F({ label: 'Type de justificatif', ui: 'select', required: 'yes', options: ['Attestation de résidence', 'Facture REGIDESO / SNEL', 'Géolocalisation certifiée', 'Déclaration sur l\'honneur'] }))}
    ${renderField(F({ label: 'Date d\'émission', ui: 'date', required: 'yes', hint: 'Ancienneté maximale recommandée : 3 mois' }))}
    ${renderField(F({ label: 'Document scanné', ui: 'file', required: 'cond', icon: 'file', placeholder: 'Importer une photo ou un PDF' }))}
    <div class="card" style="display:flex;align-items:center;gap:var(--space-3);">
      <div class="icon" style="width:2.5rem;height:2.5rem;border-radius:var(--radius-md);background:var(--color-primary-highlight);color:var(--color-primary);display:flex;align-items:center;justify-content:center;">${ICONS.globe}</div>
      <div style="flex:1;"><strong style="font-size:var(--text-sm);">Géolocalisation certifiée</strong><div style="font-size:11px;color:var(--color-text-faint);">Alternative si aucun document disponible</div></div>
      <div class="toggle"><div class="knob"></div></div>
    </div>
    <div class="sticky-cta"><button class="btn btn-primary">Soumettre mon dossier</button></div>
  `,
});

SCREENS.push({
  id: 'onb-kyb',
  group: 'onboarding',
  title: 'Entreprise / PME (KYB)',
  subtitle: 'Écran « Entreprise / PME (KYB) »',
  chrome: 'sub',
  meta: {
    br: ['BR-KYB-01'], qc: ['QC-ONB-08', 'QC-ONB-09'],
    note: 'Dossier entreprise jugé complet uniquement si tous les éléments obligatoires sont fournis et cohérents entre eux.',
  },
  fields: [
    F({ label: 'Raison sociale', specType: 'Texte', ui: 'text', required: 'yes' }),
    F({ label: 'Numéro RCCM', specType: 'Alphanumérique', ui: 'text', required: 'yes', hint: 'Format officiel RDC ; contrôle de cohérence de format' }),
    F({ label: 'Numéro d\'Identification Nationale (NIF)', specType: 'Alphanumérique', ui: 'text', required: 'yes', hint: 'Format officiel RDC' }),
    F({ label: 'Forme juridique', specType: 'Liste (SARL, SA, EI, etc.)', ui: 'select', required: 'yes', options: ['SARL', 'SA', 'Entreprise individuelle', 'Autre'] }),
    F({ label: 'Date de création', specType: 'Date', ui: 'date', required: 'yes' }),
    F({ label: 'Secteur d\'activité', specType: 'Liste codifiée', ui: 'select', required: 'yes', hint: 'Utilisé pour le scoring de risque sectoriel', options: ['Commerce général', 'Import-export', 'Services', 'Agroalimentaire', 'Autre'] }),
    F({ label: 'Adresse du siège social', specType: 'Texte', ui: 'text', required: 'yes' }),
    F({ label: 'Chiffre d\'affaires annuel déclaré', specType: 'Numérique', ui: 'number', required: 'no', hint: 'Utilisé pour le calibrage des plafonds entreprise' }),
    F({ label: 'Représentant légal — Nom et fonction', specType: 'Texte', ui: 'text', required: 'yes', hint: 'Doit être vérifié par pièce d\'identité' }),
    F({ label: 'Représentant légal — Pièce d\'identité', specType: 'Document', ui: 'file', required: 'yes', hint: 'Mêmes règles de contrôle que pour un particulier', icon: 'file' }),
    F({ label: 'Bénéficiaires effectifs (UBO)', specType: 'Liste répétable (Nom, % détention, pièce d\'identité)', ui: 'file', required: 'yes', hint: 'Somme des % de détention déclarés contrôlée', icon: 'users' }),
  ],
  qcList: [{ id: 'QC-ONB-09', rule: 'Somme des pourcentages de détention déclarés ≤ 100 %.', sev: 'Avertissement' }],
  body: () => `
    <h1 class="block-title">Dossier entreprise</h1>
    <p class="block-sub">Informations légales de votre société</p>
    ${renderField(F({ label: 'Raison sociale', ui: 'text', required: 'yes', placeholder: 'Switsh Trading SARL' }))}
    <div style="display:flex;gap:var(--space-3);">
      <div style="flex:1;">${renderField(F({ label: 'Numéro RCCM', ui: 'text', required: 'yes' }))}</div>
      <div style="flex:1;">${renderField(F({ label: 'NIF', ui: 'text', required: 'yes' }))}</div>
    </div>
    ${renderField(F({ label: 'Forme juridique', ui: 'select', required: 'yes', options: ['SARL', 'SA', 'Entreprise individuelle', 'Autre'] }))}
    ${renderField(F({ label: 'Secteur d\'activité', ui: 'select', required: 'yes', options: ['Commerce général', 'Import-export', 'Services', 'Agroalimentaire', 'Autre'] }))}
    ${renderField(F({ label: 'Adresse du siège social', ui: 'text', required: 'yes' }))}
    <div class="section-label">Représentant légal & bénéficiaires effectifs</div>
    ${renderField(F({ label: 'Nom et fonction du représentant', ui: 'text', required: 'yes' }))}
    <div class="upload-box">${ICONS.file}<strong>Pièce d\'identité du représentant</strong><span>CENI, passeport ou carte de résident</span></div>
    <div class="card" style="display:flex;align-items:center;gap:var(--space-3);">
      <div class="avatar">MK</div>
      <div style="flex:1;"><strong style="font-size:var(--text-sm);">Mireille Kabuya</strong><div style="font-size:11px;color:var(--color-text-faint);">Bénéficiaire effectif · 60%</div></div>
      <span class="badge badge-success">Vérifié</span>
    </div>
    <button class="btn btn-secondary btn-sm" style="width:100%;margin-bottom:var(--space-4);">${ICONS.plus} Ajouter un bénéficiaire effectif</button>
    <div class="sticky-cta"><button class="btn btn-primary">Soumettre le dossier KYB</button></div>
  `,
});

SCREENS.push({
  id: 'onb-status',
  group: 'onboarding',
  title: 'Statut de vérification',
  subtitle: 'Écran complémentaire (UX) — suivi du tiering KYC',
  chrome: 'sub',
  bonus: true,
  meta: {
    br: ['BR-TIER-01', 'BR-TIER-02', 'BR-AML-04'],
    note: 'Reflète le statut PENDING/VERIFIED/REJECTED et le niveau KYC (1/2/3) calculés par le moteur de tiering — écran ajouté pour une expérience transparente et rassurante.',
  },
  fields: [
    F({ label: 'Statut KYC', specType: 'PENDING / VERIFIED / REJECTED', ui: 'readonly', required: 'ro', placeholder: 'VERIFIED' }),
    F({ label: 'Niveau KYC attribué', specType: 'Liste (1 / 2 / 3)', ui: 'readonly', required: 'ro', placeholder: 'Niveau 2' }),
  ],
  engines: [{ name: 'Moteur de tiering KYC', logic: 'Arbre de décision (BCC) sur complétude, OCR/Liveness, AML, justificatif de domicile → niveau KYC 1/2/3 + plafonds associés.' }],
  body: () => `
    <div style="text-align:center;padding-top:var(--space-6);">
      <div style="width:72px;height:72px;border-radius:50%;background:var(--color-success-highlight);color:var(--color-success);display:flex;align-items:center;justify-content:center;margin:0 auto var(--space-4);">${ICONS.checkCircle}</div>
      <h1 class="block-title">Identité vérifiée</h1>
      <p class="block-sub">Votre compte est au Niveau 2 — plafonds intermédiaires actifs</p>
    </div>
    <div class="card">
      <div class="list-row"><span class="info"><strong>Statut KYC</strong><span>Décision finale</span></span><span class="badge badge-success">VERIFIED</span></div>
      <div class="list-row"><span class="info"><strong>Niveau attribué</strong><span>Plafonds &amp; fonctionnalités</span></span><span class="badge badge-usd">Niveau 2</span></div>
      <div class="list-row"><span class="info"><strong>Screening AML/PPE</strong><span>Listes CENAREF / ONU / GAFI</span></span><span class="badge badge-success">CLEAR</span></div>
    </div>
    <div class="notice-box notice-warn">${ICONS.alert}<span>Fournissez un justificatif de domicile pour atteindre le Niveau 3 et lever vos plafonds de transaction.</span></div>
    <div class="sticky-cta"><button class="btn btn-primary">Accéder à mon compte</button></div>
  `,
});
