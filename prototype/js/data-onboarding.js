/* ===== Domaine 1 — Onboarding, Sécurité & Conformité (KYC/AML) ===== */

SCREENS.push({
  id: 'onb-inscription',
  group: 'onboarding',
  title: L('onb.registration.screen_title', 'Inscription'),
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
    <h1 class="block-title">${L('onb.registration.title', 'Créer votre compte')}</h1>
    <p class="block-sub">${L('onb.registration.subtitle', '100% mobile — vérifié par SMS et e-mail')}</p>
    <div class="field"><label>${L('onb.phone.label', 'Numéro de téléphone')} <span class="req">*</span></label>
      <div class="input-group"><span class="prefix">🇨🇩 +243</span><input class="input has-prefix" data-field="phoneNumber" placeholder="9XX XXX XXX" style="padding-left:4.75rem;" /></div>
      <div class="hint">${L('onb.phone.hint', 'Format RDC +243 suivi de 9 chiffres')}</div>
    </div>
    ${renderField(F({ label: L('onb.email.label', 'Adresse e-mail'), ui: 'email', required: 'yes', fieldId: 'email', placeholder: 'nom@exemple.cd' }))}
    ${renderField(F({ label: L('onb.password.label', 'Mot de passe'), ui: 'password', required: 'yes', hint: L('onb.password.hint', 'Min. 8 caractères, majuscule, chiffre, caractère spécial') }))}
    <div class="checkline checked" data-field="termsAccepted" style="margin:var(--space-4) 0;">
      <div class="box">${ICONS.check}</div>
      <span>${L('onb.terms.accept', 'J\'accepte les')} <a href="#">${L('onb.terms.conditions', 'Conditions générales')}</a> ${L('onb.terms.and', 'et la')} <a href="#">${L('onb.terms.privacy', 'Politique de confidentialité')}</a> Switsh.</span>
    </div>
    <div class="sticky-cta"><button class="btn btn-primary">${L('onb.registration.send_code', 'Recevoir le code SMS')}</button></div>
  `,
});

SCREENS.push({
  id: 'onb-otp',
  group: 'onboarding',
  title: L('onb.otp.screen_title', 'Confirmation du numéro'),
  subtitle: 'Étape de vérification de l\'écran « Inscription »',
  chrome: 'sub',
  step: [2, 5],
  bonus: true,
  meta: { br: ['BR-ONB-01'], qc: ['QC-ONB-01'], note: 'Étape UX séparant la saisie OTP de l\'inscription pour plus de clarté — champ « Code OTP » de l\'écran « Inscription » du BRD.' },
  fields: [
    F({ label: 'Code OTP reçu', specType: 'Numérique, 6 chiffres', ui: 'otp', required: 'yes', hint: 'Validité 5 minutes, 3 tentatives max' }),
  ],
  body: () => `
    <h1 class="block-title">${L('onb.otp.title', 'Entrez le code reçu')}</h1>
    <p class="block-sub">${L('onb.otp.subtitle', 'Un code à 6 chiffres a été envoyé au')} <strong>+243 9XX XXX XXX</strong></p>
    ${renderField(F({ label: L('onb.otp.label', 'Code OTP'), ui: 'otp', required: 'yes' }))}
    <p style="font-size:var(--text-xs);color:var(--color-text-faint);margin-top:var(--space-2);">${L('onb.otp.expiry', 'Le code expire dans')} <strong style="color:var(--color-primary);">04:12</strong> · ${L('onb.otp.attempts', '3 tentatives maximum')}</p>
    <div class="notice-box notice-info" style="margin-top:var(--space-5);">${ICONS.shield}<span>${L('onb.otp.notice', 'Ce code confirme votre numéro de ligne RDC conformément aux exigences ARPTC (BR-ONB-03).')}</span></div>
    <div class="sticky-cta"><button class="btn btn-primary">${L('onb.otp.verify', 'Vérifier')}</button><div style="text-align:center;margin-top:var(--space-3);"><button class="btn-ghost btn-sm" style="color:var(--color-primary);">${L('onb.otp.resend', 'Renvoyer le code')}</button></div></div>
  `,
});

SCREENS.push({
  id: 'onb-auth-security',
  group: 'onboarding',
  title: L('onb.security.screen_title', 'Authentification & sécurité'),
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
    <h1 class="block-title">${L('onb.security.title', 'Sécurisez votre compte')}</h1>
    <p class="block-sub">${L('onb.security.subtitle', 'Définissez un code PIN et activez la biométrie')}</p>
    ${renderField(F({ label: L('onb.security.create_pin', 'Créer un code PIN'), ui: 'password', required: 'yes', hint: L('onb.security.pin_hint', '4 ou 6 chiffres, jamais de séquence triviale') }))}
    ${renderField(F({ label: L('onb.security.confirm_pin', 'Confirmer le code PIN'), ui: 'password', required: 'yes' }))}
    <div class="card" style="margin-top:var(--space-2);">
      ${renderField(F({ label: L('onb.security.face_id', 'Face ID'), ui: 'toggle', on: true, placeholder: L('onb.security.face_id_unlock', 'Utiliser Face ID pour déverrouiller') }))}
      ${renderField(F({ label: L('onb.security.fingerprint', 'Empreinte digitale'), ui: 'toggle', on: false, placeholder: L('onb.security.fingerprint_unlock', 'Utiliser l\'empreinte digitale') }))}
    </div>
    <div class="notice-box notice-success">${ICONS.shield}<span>${L('onb.security.biometric_notice', 'Biométrie détectée automatiquement : Face ID disponible sur cet appareil.')}</span></div>
    <div class="sticky-cta"><button class="btn btn-primary">${L('onb.security.activate', 'Activer la sécurité')}</button></div>
  `,
});

SCREENS.push({
  id: 'onb-profile-select',
  group: 'onboarding',
  title: L('onb.profile.screen_title', 'Sélection du profil client'),
  subtitle: 'Écran « Sélection du profil client »',
  chrome: 'sub',
  step: [4, 5],
  meta: { br: ['BR-ONB-01'], note: 'Détermine le parcours KYC (particulier) ou KYB (entreprise) suivant.' },
  fields: [
    F({ label: 'Type de client', specType: 'Liste (Particulier / Entreprise)', ui: 'select', required: 'yes', hint: 'Détermine le parcours KYC/KYB suivant', options: ['Particulier', 'Entreprise / PME'] }),
    F({ label: 'Pays de résidence fiscale', specType: 'Liste pays', ui: 'select', required: 'yes', hint: 'RDC par défaut ; impacte les règles de conformité applicables', options: ['République démocratique du Congo', 'Autre pays'] }),
  ],
  body: () => `
    <h1 class="block-title">${L('onb.profile.title', 'Quel type de compte souhaitez-vous ouvrir ?')}</h1>
    <p class="block-sub">${L('onb.profile.subtitle', 'Ce choix oriente votre parcours de vérification d\'identité')}</p>
    <div class="choice-card sel"><div class="icon">${ICONS.user}</div><div class="txt"><strong>${L('onb.profile.personal', 'Particulier')}</strong><span>${L('onb.profile.personal_sub', 'Compte personnel bidevises USD/CDF')}</span></div></div>
    <div class="choice-card"><div class="icon">${ICONS.building}</div><div class="txt"><strong>${L('onb.profile.business', 'Entreprise / PME')}</strong><span>${L('onb.profile.business_sub', 'Compte société — dossier KYB requis')}</span></div></div>
    ${renderField(F({ label: L('onb.profile.country', 'Pays de résidence fiscale'), ui: 'select', required: 'yes', options: [L('onb.country.dr_congo', 'République démocratique du Congo'), L('onb.country.other', 'Autre pays')] }))}
    <div class="sticky-cta"><button class="btn btn-primary">${L('common.continue', 'Continuer')}</button></div>
  `,
});

SCREENS.push({
  id: 'onb-id-particulier',
  group: 'onboarding',
  title: L('onb.identity.screen_title', 'Pièce d\'identité'),
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
    <h1 class="block-title">${L('onb.identity.title', 'Vérifiez votre identité')}</h1>
    <p class="block-sub">${L('onb.identity.subtitle', 'Document officiel RDC admis')}</p>
    ${renderField(F({ label: L('onb.identity.document_type', 'Type de document'), ui: 'select', required: 'yes', options: ['CENI', 'ONIP / NIN', L('onb.identity.passport', 'Passeport biométrique'), L('onb.identity.resident_card', 'Carte de résident')] }))}
    ${renderField(F({ label: L('onb.identity.document_number', 'Numéro du document'), ui: 'text', required: 'yes', placeholder: L('onb.identity.document_example', 'Ex. 12-3456789-A00812') }))}
    <div style="display:flex;gap:var(--space-3);">
      <div style="flex:1;">${renderField(F({ label: L('onb.identity.issue_date', 'Date d\'émission'), ui: 'date', required: 'yes' }))}</div>
      <div style="flex:1;">${renderField(F({ label: L('onb.identity.expiry_date', 'Date d\'expiration'), ui: 'date', required: 'yes' }))}</div>
    </div>
    <div class="section-label">${L('onb.identity.documents_section', 'Documents & selfie')}</div>
    <div class="upload-box done">${ICONS.checkCircle}<strong>${L('onb.identity.front_captured', 'Photo recto — capturée')}</strong><span>${L('onb.identity.ocr_sharpness', 'Netteté vérifiée par l\'OCR')}</span></div>
    ${renderField(F({ label: L('onb.identity.back_photo', 'Photo verso'), ui: 'file', required: 'cond', icon: 'camera', placeholder: L('onb.identity.take_back_photo', 'Photographier le verso') }))}
    ${renderField(F({ label: L('onb.identity.liveness_selfie', 'Selfie de vivacité'), ui: 'file', required: 'yes', icon: 'face', placeholder: L('onb.identity.start_liveness', 'Lancer le contrôle de vivacité') }))}
    <div class="sticky-cta"><button class="btn btn-primary">${L('onb.identity.validate', 'Valider le document')}</button></div>
  `,
});

SCREENS.push({
  id: 'onb-infos-perso',
  group: 'onboarding',
  title: L('onb.personal.screen_title', 'Informations personnelles'),
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
    <h1 class="block-title">${L('onb.personal.title', 'Vos informations')}</h1>
    <p class="block-sub">${L('onb.personal.subtitle', 'Pré-remplies depuis votre pièce d\'identité')}</p>
    <div style="display:flex;gap:var(--space-3);">
      <div style="flex:1;">${renderField(F({ label: L('onb.personal.last_name', 'Nom'), ui: 'text', required: 'yes', placeholder: 'KABUYA' }))}</div>
      <div style="flex:1;">${renderField(F({ label: L('onb.personal.first_name', 'Prénom(s)'), ui: 'text', required: 'yes', placeholder: 'Mireille' }))}</div>
    </div>
    <div style="display:flex;gap:var(--space-3);">
      <div style="flex:1;">${renderField(F({ label: L('onb.personal.birth_date', 'Date de naissance'), ui: 'date', required: 'yes' }))}</div>
      <div style="flex:1;">${renderField(F({ label: L('onb.personal.gender', 'Sexe'), ui: 'select', required: 'yes', options: [L('onb.gender.male', 'Masculin'), L('onb.gender.female', 'Féminin')] }))}</div>
    </div>
    ${renderField(F({ label: L('onb.personal.birth_place', 'Lieu de naissance'), ui: 'text', required: 'yes', placeholder: L('onb.example.kinshasa', 'Kinshasa, RDC') }))}
    ${renderField(F({ label: L('onb.personal.nationality', 'Nationalité'), ui: 'select', required: 'yes', options: [L('onb.nationality.congolese', 'Congolaise (RDC)'), L('onb.country.other', 'Autre')] }))}
    ${renderField(F({ label: L('onb.personal.address', 'Adresse complète'), ui: 'text', required: 'yes', placeholder: L('onb.example.address', 'Avenue de la Paix, n°12') }))}
    ${renderField(F({ label: L('onb.personal.city', 'Ville / Commune / Quartier'), ui: 'text', required: 'yes', placeholder: L('onb.example.city', 'Kinshasa / Gombe / Croisée') }))}
    ${renderField(F({ label: L('onb.personal.profession', 'Profession'), ui: 'text', required: 'yes', placeholder: L('onb.example.profession', 'Commerçante') }))}
    ${renderField(F({ label: L('onb.personal.income_source', 'Source des revenus déclarée'), ui: 'select', required: 'yes', options: [L('onb.income.salary', 'Salaire'), L('onb.income.business', 'Activité commerciale'), L('onb.country.other', 'Autre')] }))}
    ${renderField(F({ label: L('onb.personal.monthly_income', 'Revenu mensuel estimé (USD)'), ui: 'number', required: 'no', placeholder: '0' }))}
    <div class="sticky-cta"><button class="btn btn-primary">${L('common.continue', 'Continuer')}</button></div>
  `,
});

SCREENS.push({
  id: 'onb-justif-domicile',
  group: 'onboarding',
  title: L('onb.address_proof.screen_title', 'Justificatif de domicile'),
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
    <h1 class="block-title">${L('onb.address_proof.title', 'Justificatif de domicile')}</h1>
    <p class="block-sub">${L('onb.address_proof.subtitle', 'Nécessaire pour débloquer le Niveau 3 (plafonds élevés)')}</p>
    ${renderField(F({ label: L('onb.address_proof.type', 'Type de justificatif'), ui: 'select', required: 'yes', options: [L('onb.address_proof.residence', 'Attestation de résidence'), L('onb.address_proof.utility_bill', 'Facture REGIDESO / SNEL'), L('onb.address_proof.geolocation', 'Géolocalisation certifiée'), L('onb.address_proof.honor_declaration', 'Déclaration sur l\'honneur')] }))}
    ${renderField(F({ label: L('onb.identity.issue_date', 'Date d\'émission'), ui: 'date', required: 'yes', hint: L('onb.address_proof.age_hint', 'Ancienneté maximale recommandée : 3 mois') }))}
    ${renderField(F({ label: L('onb.address_proof.scanned_document', 'Document scanné'), ui: 'file', required: 'cond', icon: 'file', placeholder: L('onb.address_proof.upload_hint', 'Importer une photo ou un PDF') }))}
    <div class="card" style="display:flex;align-items:center;gap:var(--space-3);">
      <div class="icon" style="width:2.5rem;height:2.5rem;border-radius:var(--radius-md);background:var(--color-primary-highlight);color:var(--color-primary);display:flex;align-items:center;justify-content:center;">${ICONS.globe}</div>
      <div style="flex:1;"><strong style="font-size:var(--text-sm);">${L('onb.address_proof.geolocation', 'Géolocalisation certifiée')}</strong><div style="font-size:11px;color:var(--color-text-faint);">${L('onb.address_proof.alternative', 'Alternative si aucun document disponible')}</div></div>
      <div class="toggle"><div class="knob"></div></div>
    </div>
    <div class="sticky-cta"><button class="btn btn-primary">${L('onb.address_proof.submit', 'Soumettre mon dossier')}</button></div>
  `,
});

SCREENS.push({
  id: 'onb-kyb',
  group: 'onboarding',
  title: L('onb.business.screen_title', 'Entreprise / PME (KYB)'),
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
    <h1 class="block-title">${L('onb.business.title', 'Dossier entreprise')}</h1>
    <p class="block-sub">${L('onb.business.subtitle', 'Informations légales de votre société')}</p>
    ${renderField(F({ label: L('onb.business.company_name', 'Raison sociale'), ui: 'text', required: 'yes', placeholder: 'Switsh Trading SARL' }))}
    <div style="display:flex;gap:var(--space-3);">
      <div style="flex:1;">${renderField(F({ label: L('onb.business.rccm', 'Numéro RCCM'), ui: 'text', required: 'yes' }))}</div>
      <div style="flex:1;">${renderField(F({ label: 'NIF', ui: 'text', required: 'yes' }))}</div>
    </div>
    ${renderField(F({ label: L('onb.business.legal_form', 'Forme juridique'), ui: 'select', required: 'yes', options: ['SARL', 'SA', L('onb.business.sole_proprietorship', 'Entreprise individuelle'), L('onb.country.other', 'Autre')] }))}
    ${renderField(F({ label: L('onb.business.industry', 'Secteur d\'activité'), ui: 'select', required: 'yes', options: [L('onb.business.general_trade', 'Commerce général'), L('onb.business.import_export', 'Import-export'), L('onb.business.services', 'Services'), L('onb.business.agri_food', 'Agroalimentaire'), L('onb.country.other', 'Autre')] }))}
    ${renderField(F({ label: L('onb.business.registered_address', 'Adresse du siège social'), ui: 'text', required: 'yes' }))}
    <div class="section-label">${L('onb.business.representatives', 'Représentant légal & bénéficiaires effectifs')}</div>
    ${renderField(F({ label: L('onb.business.representative_name', 'Nom et fonction du représentant'), ui: 'text', required: 'yes' }))}
    <div class="upload-box">${ICONS.file}<strong>${L('onb.business.representative_id', 'Pièce d\'identité du représentant')}</strong><span>${L('onb.business.accepted_ids', 'CENI, passeport ou carte de résident')}</span></div>
    <div class="card" style="display:flex;align-items:center;gap:var(--space-3);">
      <div class="avatar">MK</div>
      <div style="flex:1;"><strong style="font-size:var(--text-sm);">Mireille Kabuya</strong><div style="font-size:11px;color:var(--color-text-faint);">${L('onb.business.ubo', 'Bénéficiaire effectif')} · 60%</div></div>
      <span class="badge badge-success">${L('common.verified', 'Vérifié')}</span>
    </div>
    <button class="btn btn-secondary btn-sm" style="width:100%;margin-bottom:var(--space-4);">${ICONS.plus} ${L('onb.business.add_ubo', 'Ajouter un bénéficiaire effectif')}</button>
    <div class="sticky-cta"><button class="btn btn-primary">${L('onb.business.submit', 'Soumettre le dossier KYB')}</button></div>
  `,
});

SCREENS.push({
  id: 'onb-status',
  group: 'onboarding',
  title: L('onb.status.screen_title', 'Statut de vérification'),
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
      <h1 class="block-title">${L('onb.status.title', 'Identité vérifiée')}</h1>
      <p class="block-sub">${L('onb.status.subtitle', 'Votre compte est au Niveau 2 — plafonds intermédiaires actifs')}</p>
    </div>
    <div class="card">
      <div class="list-row"><span class="info"><strong>${L('onb.status.kyc_status', 'Statut KYC')}</strong><span>${L('onb.status.final_decision', 'Décision finale')}</span></span><span class="badge badge-success">VERIFIED</span></div>
      <div class="list-row"><span class="info"><strong>${L('onb.status.assigned_level', 'Niveau attribué')}</strong><span>${L('onb.status.limits_features', 'Plafonds &amp; fonctionnalités')}</span></span><span class="badge badge-usd">${L('onb.status.level_two', 'Niveau 2')}</span></div>
      <div class="list-row"><span class="info"><strong>${L('onb.status.aml_screening', 'Screening AML/PPE')}</strong><span>${L('onb.status.screening_lists', 'Listes CENAREF / ONU / GAFI')}</span></span><span class="badge badge-success">CLEAR</span></div>
    </div>
    <div class="notice-box notice-warn">${ICONS.alert}<span>${L('onb.status.address_proof_notice', 'Fournissez un justificatif de domicile pour atteindre le Niveau 3 et lever vos plafonds de transaction.')}</span></div>
    <div class="sticky-cta"><button class="btn btn-primary">${L('onb.status.open_account', 'Accéder à mon compte')}</button></div>
  `,
});
