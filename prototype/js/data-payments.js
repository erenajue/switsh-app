/* ===== Domaine 4 — Paiements & Interopérabilité ===== */

SCREENS.push({
  id: 'pay-p2p',
  group: 'payments',
  title: 'Virement P2P',
  subtitle: 'Écran « Virement P2P (entre utilisateurs Switsh) »',
  chrome: 'sub',
  tab: 'payments',
  meta: { br: ['BR-PAY-01'], qc: ['QC-PAY-01', 'QC-PAY-02'], note: 'Transfert instantané entre wallets Switsh ; contrôle du solde disponible et des plafonds KYC avant exécution.' },
  fields: [
    F({ label: 'Destinataire (numéro / identifiant Switsh)', specType: 'Texte / MSISDN', ui: 'tel', required: 'yes', hint: 'Doit correspondre à un compte Switsh actif' }),
    F({ label: 'Montant', specType: 'Numérique', ui: 'number', required: 'yes', hint: '> 0 et ≤ solde disponible et plafond KYC' }),
    F({ label: 'Devise', specType: 'Liste (USD / CDF)', ui: 'select', required: 'yes', options: ['USD', 'CDF'] }),
    F({ label: 'Motif du virement', specType: 'Texte', ui: 'text', required: 'no' }),
    F({ label: 'Code PIN / biométrie', specType: 'SCA', ui: 'password', required: 'yes', hint: 'Authentification forte obligatoire' }),
  ],
  qcList: [
    { id: 'QC-PAY-01', rule: 'Montant > 0, ≤ solde disponible et ≤ plafond KYC applicable.', sev: 'Bloquant' },
    { id: 'QC-PAY-02', rule: 'Destinataire = compte Switsh actif et non restreint.', sev: 'Bloquant' },
  ],
  body: () => `
    <h1 class="block-title">Envoyer de l\'argent</h1>
    <p class="block-sub">À un contact Switsh — instantané, sans frais</p>
    ${renderField(F({ label: 'Destinataire', ui: 'tel', required: 'yes', placeholder: 'Nom, numéro ou @identifiant Switsh' }))}
    <div class="card" style="display:flex;align-items:center;gap:var(--space-3);">
      <div class="avatar">JT</div><div style="flex:1;"><strong style="font-size:var(--text-sm);">Jonas Tshimanga</strong><div style="font-size:11px;color:var(--color-text-faint);">+243 998 765 432 · Vérifié</div></div><span class="badge badge-success">Switsh</span>
    </div>
    <div style="display:flex;gap:var(--space-3);">
      <div style="flex:1;">${renderField(F({ label: 'Montant', ui: 'number', required: 'yes', prefix: '$' }))}</div>
      <div style="flex:1;">${renderField(F({ label: 'Devise', ui: 'select', required: 'yes', options: ['USD', 'CDF'] }))}</div>
    </div>
    ${renderField(F({ label: 'Motif (optionnel)', ui: 'text', required: 'no', placeholder: 'Ex. Remboursement taxi' }))}
    <div class="notice-box notice-info">${ICONS.wallet}<span>Solde disponible : <strong>$ 820.00</strong> · Plafond restant ce mois : <strong>$ 1,180</strong></span></div>
    <div class="sticky-cta"><button class="btn btn-primary">Envoyer $ 0.00</button></div>
  `,
});

SCREENS.push({
  id: 'pay-external',
  group: 'payments',
  title: 'Virement externe',
  subtitle: 'Écran « Virement vers compte bancaire externe »',
  chrome: 'sub',
  meta: { br: ['BR-PAY-02'], qc: ['QC-PAY-03'], note: 'Virement interbancaire via le rail ATS/RIP ; délai de traitement affiché avant confirmation.' },
  fields: [
    F({ label: 'Banque bénéficiaire', specType: 'Liste banques RDC', ui: 'select', required: 'yes', options: ['Rawbank', 'Equity BCDC', 'TMB', 'Trust Merchant Bank', 'Autre'] }),
    F({ label: 'Numéro de compte / RIB', specType: 'Alphanumérique', ui: 'text', required: 'yes', hint: 'Format contrôlé selon la banque sélectionnée' }),
    F({ label: 'Nom du titulaire', specType: 'Texte', ui: 'text', required: 'yes' }),
    F({ label: 'Montant', specType: 'Numérique', ui: 'number', required: 'yes', hint: '≤ solde disponible et plafond KYC' }),
    F({ label: 'Devise', specType: 'Liste (USD / CDF)', ui: 'select', required: 'yes', options: ['USD', 'CDF'] }),
    F({ label: 'Motif du virement', specType: 'Texte', ui: 'text', required: 'yes', hint: 'Obligatoire pour la traçabilité AML' }),
  ],
  body: () => `
    <h1 class="block-title">Vers un compte bancaire</h1>
    <p class="block-sub">Virement interbancaire RDC (rail ATS/RIP)</p>
    ${renderField(F({ label: 'Banque bénéficiaire', ui: 'select', required: 'yes', options: ['Rawbank', 'Equity BCDC', 'TMB', 'Trust Merchant Bank', 'Autre'] }))}
    ${renderField(F({ label: 'Numéro de compte / RIB', ui: 'text', required: 'yes' }))}
    ${renderField(F({ label: 'Nom du titulaire', ui: 'text', required: 'yes' }))}
    <div style="display:flex;gap:var(--space-3);">
      <div style="flex:1;">${renderField(F({ label: 'Montant', ui: 'number', required: 'yes', prefix: '$' }))}</div>
      <div style="flex:1;">${renderField(F({ label: 'Devise', ui: 'select', required: 'yes', options: ['USD', 'CDF'] }))}</div>
    </div>
    ${renderField(F({ label: 'Motif du virement', ui: 'text', required: 'yes', placeholder: 'Ex. Paiement fournisseur' }))}
    <div class="notice-box notice-warn">${ICONS.alert}<span>Délai de traitement estimé : sous 24h ouvrées via le rail interbancaire.</span></div>
    <div class="sticky-cta"><button class="btn btn-primary">Continuer</button></div>
  `,
});

SCREENS.push({
  id: 'pay-cashinout',
  group: 'payments',
  title: 'Cash-in / Cash-out',
  subtitle: 'Écran « Cash-in/Cash-out Mobile Money »',
  chrome: 'sub',
  meta: { br: ['BR-PAY-03'], qc: ['QC-PAY-04'], note: 'Interopérabilité avec les opérateurs Mobile Money (Airtel Money, Orange Money, M-Pesa, Africell Money).' },
  fields: [
    F({ label: 'Type d\'opération', specType: 'Liste (Cash-in / Cash-out)', ui: 'select', required: 'yes', options: ['Cash-in — dépôt vers Switsh', 'Cash-out — retrait vers Mobile Money'] }),
    F({ label: 'Opérateur Mobile Money', specType: 'Liste opérateurs RDC', ui: 'select', required: 'yes', options: ['Airtel Money', 'Orange Money', 'M-Pesa', 'Africell Money'] }),
    F({ label: 'Numéro Mobile Money', specType: 'MSISDN', ui: 'tel', required: 'yes', prefix: '+243' }),
    F({ label: 'Montant', specType: 'Numérique', ui: 'number', required: 'yes', hint: 'Soumis aux limites de l\'opérateur et au plafond KYC' }),
    F({ label: 'Frais applicables', specType: 'Numérique', ui: 'readonly', required: 'ro', hint: 'Calculés par le moteur de tarification interopérabilité' }),
  ],
  body: () => `
    <h1 class="block-title">Cash-in / Cash-out</h1>
    <div class="segmented"><div class="seg-opt active">Cash-in</div><div class="seg-opt">Cash-out</div></div>
    ${renderField(F({ label: 'Opérateur Mobile Money', ui: 'select', required: 'yes', options: ['Airtel Money', 'Orange Money', 'M-Pesa', 'Africell Money'] }))}
    ${renderField(F({ label: 'Numéro Mobile Money', ui: 'tel', required: 'yes', prefix: '+243', placeholder: '9XX XXX XXX' }))}
    ${renderField(F({ label: 'Montant', ui: 'number', required: 'yes', prefix: '$' }))}
    <div class="notice-box notice-info">${ICONS.phone}<span>Frais estimés : <strong>$ 0.30</strong> · Crédit instantané sur votre wallet Switsh.</span></div>
    <div class="sticky-cta"><button class="btn btn-primary">Confirmer le dépôt</button></div>
  `,
});

SCREENS.push({
  id: 'pay-converter',
  group: 'payments',
  title: 'Convertisseur de devises',
  subtitle: 'Écran « Convertisseur de devises »',
  chrome: 'sub',
  meta: { br: ['BR-PAY-04'], qc: ['QC-PAY-05'], note: 'Taux de change appliqué au moment de la confirmation, avec marge de conversion transparente affichée.' },
  fields: [
    F({ label: 'Devise source', specType: 'Liste (USD / CDF)', ui: 'select', required: 'yes', options: ['USD', 'CDF'] }),
    F({ label: 'Devise cible', specType: 'Liste (USD / CDF)', ui: 'select', required: 'yes', options: ['CDF', 'USD'] }),
    F({ label: 'Montant à convertir', specType: 'Numérique', ui: 'number', required: 'yes', hint: '≤ solde disponible en devise source' }),
    F({ label: 'Taux de change appliqué', specType: 'Numérique', ui: 'readonly', required: 'ro', hint: 'Rafraîchi en continu par le moteur de change' }),
    F({ label: 'Montant crédité estimé', specType: 'Numérique', ui: 'readonly', required: 'ro' }),
  ],
  engines: [{ name: 'Moteur de change temps réel', logic: 'Récupère le taux de référence BCC, applique la marge Switsh paramétrée, verrouille le taux 30 secondes à la confirmation.' }],
  body: () => `
    <h1 class="block-title">Convertir des devises</h1>
    <div class="card" style="text-align:center;">
      <div class="section-label" style="margin-top:0;">Vous convertissez</div>
      ${renderField(F({ label: 'Montant', ui: 'number', required: 'yes', prefix: '$', placeholder: '100' }))}
      <div style="display:flex;justify-content:center;margin:var(--space-2) 0;color:var(--color-primary);">${ICONS.arrows}</div>
      <div class="section-label" style="margin-top:0;">Vous recevez (estimation)</div>
      ${renderField(F({ label: 'Montant crédité', ui: 'readonly', placeholder: '285 000 CDF' }))}
    </div>
    <div class="list-row" style="border:1px solid var(--color-border);border-radius:var(--radius-md);padding:0 var(--space-4);"><span class="info"><strong>Taux appliqué</strong></span><strong>1 USD = 2 850 CDF</strong></div>
    <div class="notice-box notice-info">${ICONS.shield}<span>Taux garanti pendant 30 secondes après affichage.</span></div>
    <div class="sticky-cta"><button class="btn btn-primary">Confirmer la conversion</button></div>
  `,
});

SCREENS.push({
  id: 'pay-merchant',
  group: 'payments',
  title: 'Paiement marchand',
  subtitle: 'Écran « Paiement marchand NFC/QR »',
  chrome: 'sub',
  meta: { br: ['BR-PAY-05'], qc: ['QC-PAY-06'], note: 'Paiement instantané par QR dynamique du marchand ou approche NFC ; ticket de caisse affiché après validation.' },
  fields: [
    F({ label: 'Mode de paiement', specType: 'Liste (Scanner QR / NFC)', ui: 'select', required: 'yes', options: ['Scanner le QR marchand', 'Approcher (NFC)'] }),
    F({ label: 'Marchand identifié', specType: 'Texte', ui: 'readonly', required: 'ro', hint: 'Résolu depuis le code QR/NFC scanné' }),
    F({ label: 'Montant à payer', specType: 'Numérique', ui: 'readonly', required: 'ro', hint: 'Transmis par le terminal marchand, non modifiable côté client' }),
    F({ label: 'Wallet de paiement', specType: 'Liste (USD / CDF)', ui: 'select', required: 'yes', options: ['USD', 'CDF'] }),
  ],
  body: () => `
    <div style="text-align:center;padding-top:var(--space-4);">
      <div style="width:220px;height:220px;margin:0 auto var(--space-4);border-radius:var(--radius-lg);background:var(--color-surface-offset);display:flex;align-items:center;justify-content:center;color:var(--color-text-faint);">${ICONS.qr}</div>
      <h1 class="block-title">Scannez pour payer</h1>
      <p class="block-sub">Ou approchez votre téléphone du terminal NFC</p>
    </div>
    <div class="card">
      <div class="list-row"><span class="info"><strong>Marchand</strong></span><span>Kin Marché</span></div>
      <div class="list-row"><span class="info"><strong>Montant</strong></span><strong>$ 12.50</strong></div>
      ${renderField(F({ label: 'Payer avec', ui: 'select', required: 'yes', options: ['Wallet USD — $820.00', 'Wallet CDF — 628 400'] }))}
    </div>
    <div class="sticky-cta"><button class="btn btn-primary">${ICONS.nfc} Confirmer le paiement</button></div>
  `,
});

SCREENS.push({
  id: 'pay-cagnotte',
  group: 'payments',
  title: 'Cagnotte partagée',
  subtitle: 'Écran « Cagnotte partagée »',
  chrome: 'sub',
  meta: { br: ['BR-PAY-06'], qc: ['QC-PAY-07'], note: 'Collecte multi-contributeurs avec objectif et suivi en temps réel ; lien de participation partageable.' },
  fields: [
    F({ label: 'Nom de la cagnotte', specType: 'Texte', ui: 'text', required: 'yes' }),
    F({ label: 'Objectif (montant cible)', specType: 'Numérique', ui: 'number', required: 'yes', hint: '> 0' }),
    F({ label: 'Devise', specType: 'Liste (USD / CDF)', ui: 'select', required: 'yes', options: ['USD', 'CDF'] }),
    F({ label: 'Date limite', specType: 'Date', ui: 'date', required: 'no' }),
    F({ label: 'Participants invités', specType: 'Liste de contacts', ui: 'file', required: 'no', icon: 'users' }),
    F({ label: 'Visibilité', specType: 'Liste (Privée / Lien public)', ui: 'select', required: 'yes', options: ['Privée (invitation)', 'Lien public'] }),
  ],
  body: () => `
    <h1 class="block-title">Créer une cagnotte</h1>
    ${renderField(F({ label: 'Nom de la cagnotte', ui: 'text', required: 'yes', placeholder: 'Ex. Anniversaire Papa' }))}
    <div style="display:flex;gap:var(--space-3);">
      <div style="flex:1;">${renderField(F({ label: 'Objectif', ui: 'number', required: 'yes', prefix: '$', placeholder: '200' }))}</div>
      <div style="flex:1;">${renderField(F({ label: 'Devise', ui: 'select', required: 'yes', options: ['USD', 'CDF'] }))}</div>
    </div>
    ${renderField(F({ label: 'Date limite', ui: 'date', required: 'no' }))}
    ${renderField(F({ label: 'Visibilité', ui: 'select', required: 'yes', options: ['Privée (invitation)', 'Lien public'] }))}
    <div class="progress-track" style="margin-top:var(--space-2);"><div class="fill" style="width:34%;"></div></div>
    <p style="font-size:11px;color:var(--color-text-faint);">$ 68 collectés sur $ 200 · 6 participants</p>
    <div class="sticky-cta"><button class="btn btn-primary">${ICONS.plus} Créer la cagnotte</button></div>
  `,
});

SCREENS.push({
  id: 'pay-split',
  group: 'payments',
  title: 'Division de facture',
  subtitle: 'Écran « Division de facture »',
  chrome: 'sub',
  meta: { br: ['BR-PAY-07'], qc: ['QC-PAY-08'], note: 'Répartition égale ou personnalisée entre participants Switsh, avec demande de paiement automatique envoyée à chacun.' },
  fields: [
    F({ label: 'Montant total de la facture', specType: 'Numérique', ui: 'number', required: 'yes' }),
    F({ label: 'Devise', specType: 'Liste (USD / CDF)', ui: 'select', required: 'yes', options: ['USD', 'CDF'] }),
    F({ label: 'Mode de répartition', specType: 'Liste (Égale / Personnalisée)', ui: 'select', required: 'yes', options: ['Répartition égale', 'Montants personnalisés'] }),
    F({ label: 'Participants', specType: 'Liste de contacts Switsh', ui: 'file', required: 'yes', icon: 'users' }),
  ],
  qcList: [{ id: 'QC-PAY-08', rule: 'Somme des montants personnalisés = montant total de la facture.', sev: 'Bloquant' }],
  body: () => `
    <h1 class="block-title">Diviser une facture</h1>
    <div style="display:flex;gap:var(--space-3);">
      <div style="flex:1;">${renderField(F({ label: 'Montant total', ui: 'number', required: 'yes', prefix: '$', placeholder: '40' }))}</div>
      <div style="flex:1;">${renderField(F({ label: 'Devise', ui: 'select', required: 'yes', options: ['USD', 'CDF'] }))}</div>
    </div>
    <div class="segmented"><div class="seg-opt active">Égale</div><div class="seg-opt">Personnalisée</div></div>
    <div class="card">
      <div class="list-row"><div class="avatar">JT</div><div class="info"><strong>Jonas Tshimanga</strong></div><strong>$ 10.00</strong></div>
      <div class="list-row"><div class="avatar">SM</div><div class="info"><strong>Sarah Mbuyi</strong></div><strong>$ 10.00</strong></div>
      <div class="list-row"><div class="avatar">MK</div><div class="info"><strong>Vous</strong></div><strong>$ 20.00</strong></div>
    </div>
    <button class="btn btn-secondary btn-sm" style="width:100%;margin-bottom:var(--space-4);">${ICONS.plus} Ajouter un participant</button>
    <div class="sticky-cta"><button class="btn btn-primary">Envoyer les demandes de paiement</button></div>
  `,
});
