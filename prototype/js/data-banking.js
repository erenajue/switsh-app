/* ===== Domaine 2 — Core Banking & Gestion des Comptes (Bidevises USD/CDF) ===== */

SCREENS.push({
  id: 'bank-accounts',
  group: 'banking',
  title: 'Mes comptes',
  subtitle: 'Écran « Mes comptes » — tableau de bord principal',
  chrome: 'hub',
  tab: 'accounts',
  meta: {
    br: ['BR-CB-01', 'BR-CB-02', 'BR-CB-07'],
    qc: ['QC-CB-01', 'QC-CB-04'],
    note: 'Les deux wallets USD/CDF coexistent nativement dans un profil unique ; le solde disponible est recalculé en temps réel et ne peut jamais devenir négatif.',
  },
  fields: [
    F({ label: 'Libellé du wallet', specType: 'Texte', ui: 'text', required: 'no', hint: 'Modifiable par le client, valeur par défaut = nom de la devise' }),
    F({ label: 'Devise', specType: 'Liste (USD / CDF)', ui: 'select', required: 'yes', hint: 'Valeur fixée à l\'ouverture, non modifiable' }),
    F({ label: 'Solde comptable', specType: 'Numérique', ui: 'readonly', required: 'ro', hint: 'Calculé, lecture seule' }),
    F({ label: 'Solde bloqué (holds)', specType: 'Numérique', ui: 'readonly', required: 'ro', hint: 'Somme des réservations en cours (autorisations carte, virements en cours)' }),
    F({ label: 'Solde disponible', specType: 'Numérique', ui: 'readonly', required: 'ro', hint: 'Calculé en temps réel par le moteur de solde disponible' }),
    F({ label: 'Identifiant bancaire / IBAN local', specType: 'Alphanumérique', ui: 'readonly', required: 'ro', hint: 'Généré à l\'ouverture du compte, affiché en lecture seule' }),
  ],
  engines: [
    { name: 'Moteur de calcul du solde disponible temps réel', logic: 'solde_disponible = solde_comptable − Σ(holds) − Σ(transactions_pending), recalcul à chaque événement (< 500 ms).' },
    { name: 'Moteur de valorisation multidevise', logic: 'Conversion des soldes USD/CDF vers la devise de référence choisie, au taux du jour, pour affichage consolidé.' },
  ],
  body: () => `
    <div style="display:flex;align-items:center;justify-content:space-between;padding-top:var(--space-2);margin-bottom:var(--space-2);">
      <div><div style="font-size:11px;color:var(--color-text-faint);">Bonjour</div><strong style="font-size:var(--text-lg);">Mireille Kabuya</strong></div>
      <div style="display:flex;gap:var(--space-2);">
        <button class="icon-btn" style="width:2.25rem;height:2.25rem;position:relative;">${ICONS.bell}<span style="position:absolute;top:4px;right:5px;width:7px;height:7px;border-radius:50%;background:var(--color-notification);"></span></button>
      </div>
    </div>
    <div class="balance-hero">
      <div class="tag">Équivalent consolidé (USD)</div>
      <div class="amount">$ 1,248.60</div>
      <div class="sub">IBAN Switsh · CD10 SWT 0001 2345 6789 01</div>
    </div>
    <div class="wallet-row">
      <div class="wallet-chip"><div class="cur"><span class="badge badge-usd">USD</span> Dollar</div><div class="amt">$ 820.00</div></div>
      <div class="wallet-chip"><div class="cur"><span class="badge badge-cdf">CDF</span> Franc</div><div class="amt">628 400</div></div>
    </div>
    <div class="quick-actions">
      <div class="qa-item"><div class="ic">${ICONS.send}</div><span>Envoyer</span></div>
      <div class="qa-item"><div class="ic">${ICONS.arrows}</div><span>Convertir</span></div>
      <div class="qa-item"><div class="ic">${ICONS.qr}</div><span>Payer QR</span></div>
      <div class="qa-item"><div class="ic">${ICONS.plus}</div><span>Sous-compte</span></div>
    </div>
    <div style="display:flex;align-items:center;justify-content:space-between;">
      <div class="section-label" style="margin-top:0;">Transactions récentes</div>
      <span style="font-size:11px;color:var(--color-primary);font-weight:600;">Tout voir</span>
    </div>
    <div class="card">
      <div class="list-row"><div class="avatar">${ICONS.headset}</div><div class="info"><strong>Kin Marché — Paiement NFC</strong><span>Aujourd\'hui, 14:32 · Commerce</span></div><div class="amount neg">− $ 12.50</div></div>
      <div class="list-row"><div class="avatar">JT</div><div class="info"><strong>Jonas Tshimanga — P2P</strong><span>Hier, 09:14 · Virement reçu</span></div><div class="amount pos">+ 50 000 CDF</div></div>
      <div class="list-row"><div class="avatar">${ICONS.arrows}</div><div class="info"><strong>Conversion USD → CDF</strong><span>28 août · Taux 2 850</span></div><div class="amount neg">− $ 30.00</div></div>
    </div>
    <div class="section-label">Sous-comptes &amp; coffres</div>
    <div class="card" style="display:flex;align-items:center;gap:var(--space-3);">
      <div class="icon" style="width:2.5rem;height:2.5rem;border-radius:var(--radius-md);background:var(--color-accent-highlight);color:var(--color-accent-hover);display:flex;align-items:center;justify-content:center;">${ICONS.piggy}</div>
      <div style="flex:1;"><strong style="font-size:var(--text-sm);">Coffre — Loyer 2026</strong><div style="font-size:11px;color:var(--color-text-faint);">Objectif $ 600 · USD</div></div>
      <strong style="font-size:var(--text-sm);">$ 340</strong>
    </div>
  `,
});

SCREENS.push({
  id: 'bank-subaccount',
  group: 'banking',
  title: 'Créer un sous-compte',
  subtitle: 'Écran « Créer un sous-compte »',
  chrome: 'sub',
  meta: { br: ['BR-CB-03'], qc: ['QC-CB-02', 'QC-CB-03'], note: 'Le client peut créer, nommer et alimenter un sous-compte par devise, utilisable comme destination de virement interne.' },
  fields: [
    F({ label: 'Nom du sous-compte', specType: 'Texte', ui: 'text', required: 'yes', hint: '3 à 50 caractères, unique par client' }),
    F({ label: 'Devise', specType: 'Liste (USD / CDF)', ui: 'select', required: 'yes', hint: 'Doit correspondre à une devise gérée par Switsh', options: ['USD', 'CDF'] }),
    F({ label: 'Objectif d\'épargne', specType: 'Texte', ui: 'text', required: 'no' }),
    F({ label: 'Montant cible', specType: 'Numérique', ui: 'number', required: 'no', hint: '> 0 si renseigné' }),
    F({ label: 'Montant de dotation initiale', specType: 'Numérique', ui: 'number', required: 'no', hint: '≥ 0 et ≤ solde disponible du compte source' }),
  ],
  body: () => `
    <h1 class="block-title">Nouveau sous-compte</h1>
    <p class="block-sub">Isolez une épargne ou un projet</p>
    ${renderField(F({ label: 'Nom du sous-compte', ui: 'text', required: 'yes', placeholder: 'Ex. Loyer 2026' }))}
    ${renderField(F({ label: 'Devise', ui: 'select', required: 'yes', options: ['USD', 'CDF'] }))}
    ${renderField(F({ label: 'Objectif d\'épargne', ui: 'text', required: 'no', placeholder: 'Ex. Constituer une réserve loyer' }))}
    <div style="display:flex;gap:var(--space-3);">
      <div style="flex:1;">${renderField(F({ label: 'Montant cible', ui: 'number', required: 'no', prefix: '$' }))}</div>
      <div style="flex:1;">${renderField(F({ label: 'Dotation initiale', ui: 'number', required: 'no', prefix: '$' }))}</div>
    </div>
    <div class="notice-box notice-info">${ICONS.wallet}<span>Solde disponible du compte source : <strong>$ 820.00</strong></span></div>
    <div class="sticky-cta"><button class="btn btn-primary">Créer le sous-compte</button></div>
  `,
});

SCREENS.push({
  id: 'bank-history',
  group: 'banking',
  title: 'Historique des opérations',
  subtitle: 'Écran « Historique des opérations »',
  chrome: 'sub',
  meta: {
    br: ['BR-CB-04', 'BR-CB-05'], qc: ['QC-CB-05'],
    note: 'Chaque ligne comporte montant, devise, taux si conversion, et solde après opération ; enrichie des données marchand (enseigne, catégorie, localisation).',
  },
  fields: [
    F({ label: 'Période (date début/fin)', specType: 'Date', ui: 'date', required: 'yes', hint: 'Date de fin ≥ date de début' }),
    F({ label: 'Devise', specType: 'Liste (Toutes / USD / CDF)', ui: 'select', required: 'no', options: ['Toutes', 'USD', 'CDF'] }),
    F({ label: 'Type d\'opération', specType: 'Liste (crédit/débit/virement/carte/change)', ui: 'select', required: 'no', options: ['Toutes', 'Crédit', 'Débit', 'Virement', 'Carte', 'Change'] }),
    F({ label: 'Marchand / catégorie', specType: 'Texte / liste', ui: 'text', required: 'no', hint: 'Filtre sur les données enrichies marchand' }),
  ],
  body: () => `
    <h1 class="block-title">Historique</h1>
    <div class="segmented"><div class="seg-opt active">Toutes</div><div class="seg-opt">USD</div><div class="seg-opt">CDF</div></div>
    <div class="pill-select" style="margin-bottom:var(--space-4);">
      <span class="opt sel">Ce mois</span><span class="opt">Carte</span><span class="opt">Virement</span><span class="opt">Change</span>
    </div>
    <div class="divider-note">Aujourd\'hui</div>
    <div class="card">
      <div class="list-row"><div class="avatar">${ICONS.qr}</div><div class="info"><strong>Kin Marché · Alimentation</strong><span>14:32 · Kinshasa/Gombe · Solde après : $ 807.50</span></div><div class="amount neg">− $ 12.50</div></div>
      <div class="list-row"><div class="avatar">${ICONS.arrows}</div><div class="info"><strong>Change USD → CDF · Taux 2 850</strong><span>11:05 · Solde après : 628 400 CDF</span></div><div class="amount neg">− $ 30.00</div></div>
    </div>
    <div class="divider-note">Hier</div>
    <div class="card">
      <div class="list-row"><div class="avatar">JT</div><div class="info"><strong>Jonas Tshimanga · P2P reçu</strong><span>09:14 · Solde après : 578 400 CDF</span></div><div class="amount pos">+ 50 000 CDF</div></div>
      <div class="list-row"><div class="avatar">${ICONS.card}</div><div class="info"><strong>Abonnement Standard</strong><span>08:00 · Frais d\'abonnement</span></div><div class="amount neg">− $ 2.00</div></div>
    </div>
  `,
});

SCREENS.push({
  id: 'bank-statement',
  group: 'banking',
  title: 'Générer un relevé',
  subtitle: 'Écran « Générer un relevé »',
  chrome: 'sub',
  meta: { br: ['BR-CB-06'], qc: ['QC-CB-06'], note: 'Relevé exportable par période, incluant les mentions légales et informations d\'identification du client.' },
  fields: [
    F({ label: 'Période', specType: 'Date début/fin', ui: 'date', required: 'yes', hint: 'Maximum 36 mois glissants sans validation compliance' }),
    F({ label: 'Format d\'export', specType: 'Liste (PDF / CSV)', ui: 'select', required: 'yes', options: ['PDF', 'CSV'] }),
    F({ label: 'Devise du relevé', specType: 'Liste (USD / CDF / Toutes)', ui: 'select', required: 'yes', options: ['Toutes', 'USD', 'CDF'] }),
    F({ label: 'Langue du relevé', specType: 'Liste (Français / Lingala / Swahili)', ui: 'select', required: 'no', hint: 'Défaut = langue de l\'application', options: ['Français', 'Lingála', 'Kiswahili'] }),
  ],
  body: () => `
    <h1 class="block-title">Générer un relevé</h1>
    <p class="block-sub">Document officiel avec mentions légales RDC</p>
    <div style="display:flex;gap:var(--space-3);">
      <div style="flex:1;">${renderField(F({ label: 'Du', ui: 'date', required: 'yes' }))}</div>
      <div style="flex:1;">${renderField(F({ label: 'Au', ui: 'date', required: 'yes' }))}</div>
    </div>
    ${renderField(F({ label: 'Devise du relevé', ui: 'select', required: 'yes', options: ['Toutes', 'USD', 'CDF'] }))}
    ${renderField(F({ label: 'Format d\'export', ui: 'select', required: 'yes', options: ['PDF', 'CSV'] }))}
    ${renderField(F({ label: 'Langue du relevé', ui: 'select', required: 'no', options: ['Français (par défaut)', 'Lingála', 'Kiswahili'] }))}
    <div class="notice-box notice-warn">${ICONS.alert}<span>Au-delà de 36 mois glissants, une validation de la conformité est requise.</span></div>
    <div class="sticky-cta"><button class="btn btn-primary">${ICONS.download} Télécharger le relevé</button></div>
  `,
});
