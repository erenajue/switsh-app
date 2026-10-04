/* ===== Domaine 3 — Cartes Physiques & Virtuelles ===== */

SCREENS.push({
  id: 'card-physical',
  group: 'cards',
  title: 'Commander une carte physique',
  subtitle: 'Écran « Commander une carte physique »',
  chrome: 'sub',
  tab: 'cards',
  meta: { br: ['BR-CARD-01'], qc: ['QC-CARD-01'], note: 'Frais d\'émission débités du wallet choisi ; adresse de livraison contrôlée pour cohérence RDC.' },
  fields: [
    F({ label: 'Type de carte', specType: 'Liste (Débit standard / Débit premium)', ui: 'select', required: 'yes', options: ['Débit standard', 'Débit Premium'] }),
    F({ label: 'Devise associée', specType: 'Liste (USD / CDF)', ui: 'select', required: 'yes', options: ['USD', 'CDF'] }),
    F({ label: 'Nom à imprimer sur la carte', specType: 'Texte', ui: 'text', required: 'yes', hint: 'Doit correspondre à l\'identité vérifiée (KYC)' }),
    F({ label: 'Adresse de livraison', specType: 'Texte', ui: 'text', required: 'yes', hint: 'Contrôlée pour cohérence géographique RDC' }),
    F({ label: 'Ville de livraison', specType: 'Liste villes desservies', ui: 'select', required: 'yes', options: ['Kinshasa', 'Lubumbashi', 'Goma', 'Autre'] }),
    F({ label: 'Frais d\'émission', specType: 'Numérique', ui: 'readonly', required: 'ro', hint: 'Calculé par le moteur de tarification' }),
  ],
  engines: [{ name: 'Moteur de tarification des cartes', logic: 'Frais d\'émission et cotisation selon type de carte, grille tarifaire active et devise choisie.' }],
  body: () => `
    <h1 class="block-title">Commander ma carte</h1>
    <p class="block-sub">Livraison à domicile en 5 à 7 jours ouvrés</p>
    <div class="choice-card sel"><div class="icon">${ICONS.card}</div><div class="txt"><strong>Débit standard</strong><span>Frais d\'émission $ 5 · Sans frais annuels</span></div></div>
    <div class="choice-card"><div class="icon">${ICONS.card}</div><div class="txt"><strong>Débit Premium</strong><span>Frais d\'émission $ 15 · Assurances incluses</span></div></div>
    ${renderField(F({ label: 'Devise associée', ui: 'select', required: 'yes', options: ['USD', 'CDF'] }))}
    ${renderField(F({ label: 'Nom sur la carte', ui: 'text', required: 'yes', placeholder: 'MIREILLE KABUYA' }))}
    ${renderField(F({ label: 'Adresse de livraison', ui: 'text', required: 'yes', placeholder: 'Avenue de la Paix, n°12' }))}
    ${renderField(F({ label: 'Ville', ui: 'select', required: 'yes', options: ['Kinshasa', 'Lubumbashi', 'Goma', 'Autre'] }))}
    <div class="notice-box notice-info">${ICONS.wallet}<span>Frais d\'émission : <strong>$ 5.00</strong>, débités de votre wallet USD à la validation.</span></div>
    <div class="sticky-cta"><button class="btn btn-primary">Confirmer la commande</button></div>
  `,
});

SCREENS.push({
  id: 'card-virtual',
  group: 'cards',
  title: 'Générer une carte virtuelle',
  subtitle: 'Écran « Générer une carte virtuelle »',
  chrome: 'sub',
  meta: { br: ['BR-CARD-02'], qc: ['QC-CARD-02'], note: 'Carte disponible instantanément pour les paiements en ligne, avec plafond dédié configurable.' },
  fields: [
    F({ label: 'Devise associée', specType: 'Liste (USD / CDF)', ui: 'select', required: 'yes', options: ['USD', 'CDF'] }),
    F({ label: 'Usage prévu', specType: 'Liste (achats en ligne / abonnements / usage unique)', ui: 'select', required: 'yes', options: ['Achats en ligne récurrents', 'Abonnements', 'Usage unique'] }),
    F({ label: 'Plafond dédié', specType: 'Numérique', ui: 'number', required: 'yes', hint: '≤ plafond global autorisé par le niveau KYC' }),
    F({ label: 'Durée de validité', specType: 'Liste (1 mois / 1 an / illimitée)', ui: 'select', required: 'no', options: ['1 mois', '1 an', 'Illimitée'] }),
  ],
  body: () => `
    <h1 class="block-title">Carte virtuelle</h1>
    <p class="block-sub">Générée instantanément pour vos achats en ligne</p>
    <div class="card" style="background:linear-gradient(135deg,var(--color-primary) 0%, #063A30 100%);color:white;padding:var(--space-5);border:none;">
      <div style="display:flex;justify-content:space-between;align-items:center;"><span style="font-size:11px;opacity:0.8;">SWITSH VIRTUAL</span>${ICONS.qr}</div>
      <div style="font-family:monospace;font-size:var(--text-lg);letter-spacing:2px;margin:var(--space-5) 0 var(--space-3);">•••• •••• •••• 8841</div>
      <div style="display:flex;justify-content:space-between;font-size:11px;opacity:0.85;"><span>MIREILLE KABUYA</span><span>12/28</span></div>
    </div>
    ${renderField(F({ label: 'Devise associée', ui: 'select', required: 'yes', options: ['USD', 'CDF'] }))}
    ${renderField(F({ label: 'Usage prévu', ui: 'select', required: 'yes', options: ['Achats en ligne récurrents', 'Abonnements', 'Usage unique'] }))}
    ${renderField(F({ label: 'Plafond dédié', ui: 'number', required: 'yes', prefix: '$', placeholder: '200' }))}
    ${renderField(F({ label: 'Durée de validité', ui: 'select', required: 'no', options: ['1 mois', '1 an', 'Illimitée'] }))}
    <div class="sticky-cta"><button class="btn btn-primary">Générer la carte</button></div>
  `,
});

SCREENS.push({
  id: 'card-manage',
  group: 'cards',
  title: 'Gérer ma carte',
  subtitle: 'Écran « Gérer ma carte »',
  chrome: 'sub',
  meta: {
    br: ['BR-CARD-03', 'BR-CARD-04'], qc: ['QC-CARD-03'],
    note: 'Blocage instantané, modification du plafond, activation du paiement en ligne/à l\'étranger et consultation du PIN sécurisée par SCA.',
  },
  fields: [
    F({ label: 'Statut de la carte', specType: 'Liste (Active / Bloquée / Opposée)', ui: 'readonly', required: 'ro', placeholder: 'Active' }),
    F({ label: 'Plafond de paiement', specType: 'Numérique', ui: 'number', required: 'no', hint: '≤ plafond autorisé par le niveau KYC' }),
    F({ label: 'Plafond de retrait', specType: 'Numérique', ui: 'number', required: 'no' }),
    F({ label: 'Paiement en ligne', specType: 'Bascule Oui/Non', ui: 'toggle', required: 'no' }),
    F({ label: 'Paiement à l\'étranger', specType: 'Bascule Oui/Non', ui: 'toggle', required: 'no' }),
    F({ label: 'Paiement sans contact (NFC)', specType: 'Bascule Oui/Non', ui: 'toggle', required: 'no' }),
    F({ label: 'Blocage temporaire', specType: 'Bascule Oui/Non', ui: 'toggle', required: 'no', hint: 'Effet immédiat, réversible par le client' }),
  ],
  qcList: [{ id: 'QC-CARD-03', rule: 'Modification de plafond ou consultation du PIN soumise à authentification forte (SCA).', sev: 'Bloquant' }],
  body: () => `
    <h1 class="block-title">Ma carte débit standard</h1>
    <div class="card" style="background:linear-gradient(135deg,var(--color-primary) 0%, #063A30 100%);color:white;padding:var(--space-5);border:none;">
      <div style="display:flex;justify-content:space-between;align-items:center;"><span style="font-size:11px;opacity:0.8;">SWITSH DEBIT</span><span class="badge" style="background:rgba(255,255,255,0.18);color:white;">Active</span></div>
      <div style="font-family:monospace;font-size:var(--text-lg);letter-spacing:2px;margin:var(--space-5) 0 var(--space-3);">•••• •••• •••• 2210</div>
      <div style="display:flex;justify-content:space-between;font-size:11px;opacity:0.85;"><span>MIREILLE KABUYA</span><span>USD</span></div>
    </div>
    <div class="card">
      ${renderField(F({ label: 'Paiement en ligne', ui: 'toggle', on: true, placeholder: 'Paiement en ligne' }))}
      ${renderField(F({ label: 'Paiement à l\'étranger', ui: 'toggle', on: false, placeholder: 'Paiement à l\'étranger' }))}
      ${renderField(F({ label: 'Paiement sans contact (NFC)', ui: 'toggle', on: true, placeholder: 'Paiement sans contact (NFC)' }))}
    </div>
    ${renderField(F({ label: 'Plafond de paiement mensuel', ui: 'number', required: 'no', prefix: '$', placeholder: '500' }))}
    ${renderField(F({ label: 'Plafond de retrait mensuel', ui: 'number', required: 'no', prefix: '$', placeholder: '300' }))}
    <button class="btn btn-secondary" style="width:100%;margin-bottom:var(--space-3);">${ICONS.lock} Voir mon code PIN (authentification requise)</button>
    <button class="btn" style="width:100%;background:var(--color-error-highlight);color:var(--color-error);margin-bottom:var(--space-4);">${ICONS.alert} Bloquer temporairement ma carte</button>
  `,
});
