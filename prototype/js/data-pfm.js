/* ===== Domaine 5 — PFM & Épargne (Personal Finance Management) ===== */

SCREENS.push({
  id: 'pfm-budget',
  group: 'pfm',
  title: 'Définir un budget',
  subtitle: 'Écran « Définir un budget »',
  chrome: 'sub',
  tab: 'pfm',
  meta: { br: ['BR-PFM-01'], qc: ['QC-PFM-01'], note: 'Budgets définis par catégorie marchand (MCC) ; alerte automatique lorsque le seuil est atteint.' },
  fields: [
    F({ label: 'Catégorie budgétaire', specType: 'Liste (codes MCC regroupés)', ui: 'select', required: 'yes', options: ['Alimentation', 'Transport', 'Logement', 'Loisirs', 'Santé', 'Autre'] }),
    F({ label: 'Montant du budget', specType: 'Numérique', ui: 'number', required: 'yes', hint: '> 0' }),
    F({ label: 'Devise', specType: 'Liste (USD / CDF)', ui: 'select', required: 'yes', options: ['USD', 'CDF'] }),
    F({ label: 'Périodicité', specType: 'Liste (Hebdomadaire / Mensuel)', ui: 'select', required: 'yes', options: ['Hebdomadaire', 'Mensuel'] }),
    F({ label: 'Seuil d\'alerte', specType: 'Pourcentage', ui: 'number', required: 'no', hint: 'Déclenche une notification push (défaut 80%)' }),
  ],
  engines: [{ name: 'Moteur de catégorisation des transactions', logic: 'Classe chaque transaction par code MCC marchand → catégorie budgétaire ; agrège la consommation par période.' }],
  body: () => `
    <h1 class="block-title">Mes budgets</h1>
    <div class="card">
      <div class="list-row"><span class="info"><strong>Alimentation</strong><span>$ 96 / $ 150 ce mois</span></span></div>
      <div class="progress-track"><div class="fill" style="width:64%;"></div></div>
    </div>
    <div class="section-label">Nouveau budget</div>
    ${renderField(F({ label: 'Catégorie', ui: 'select', required: 'yes', options: ['Alimentation', 'Transport', 'Logement', 'Loisirs', 'Santé', 'Autre'] }))}
    <div style="display:flex;gap:var(--space-3);">
      <div style="flex:1;">${renderField(F({ label: 'Montant du budget', ui: 'number', required: 'yes', prefix: '$', placeholder: '150' }))}</div>
      <div style="flex:1;">${renderField(F({ label: 'Périodicité', ui: 'select', required: 'yes', options: ['Hebdomadaire', 'Mensuel'] }))}</div>
    </div>
    ${renderField(F({ label: 'Seuil d\'alerte (%)', ui: 'number', required: 'no', placeholder: '80' }))}
    <div class="sticky-cta"><button class="btn btn-primary">Enregistrer le budget</button></div>
  `,
});

SCREENS.push({
  id: 'pfm-roundup',
  group: 'pfm',
  title: 'Épargne automatique — Arrondi',
  subtitle: 'Écran « Épargne automatique — Arrondi »',
  chrome: 'sub',
  meta: { br: ['BR-PFM-02'], qc: ['QC-PFM-02'], note: 'Chaque paiement par carte est arrondi au multiple choisi, la différence étant automatiquement épargnée dans le sous-compte désigné.' },
  fields: [
    F({ label: 'Activation de l\'arrondi', specType: 'Bascule Oui/Non', ui: 'toggle', required: 'yes', on: true }),
    F({ label: 'Multiple d\'arrondi', specType: 'Liste (1 / 5 / 10)', ui: 'select', required: 'yes', options: ['1', '5', '10'] }),
    F({ label: 'Multiplicateur d\'épargne', specType: 'Liste (x1 / x2 / x5)', ui: 'select', required: 'no', options: ['x1', 'x2', 'x5'] }),
    F({ label: 'Sous-compte destination', specType: 'Liste sous-comptes du client', ui: 'select', required: 'yes', options: ['Coffre — Loyer 2026', 'Épargne générale'] }),
  ],
  engines: [{ name: 'Moteur d\'arrondi automatique', logic: 'montant_epargne = (arrondi_supérieur(montant, multiple) − montant) × multiplicateur, déclenché à chaque paiement carte réussi.' }],
  body: () => `
    <h1 class="block-title">Épargne par arrondi</h1>
    <div class="card" style="display:flex;align-items:center;gap:var(--space-3);">
      <div class="icon" style="width:2.5rem;height:2.5rem;border-radius:var(--radius-md);background:var(--color-accent-highlight);color:var(--color-accent-hover);display:flex;align-items:center;justify-content:center;">${ICONS.piggy}</div>
      <div style="flex:1;"><strong style="font-size:var(--text-sm);">Activer l\'arrondi automatique</strong><div style="font-size:11px;color:var(--color-text-faint);">Épargnez à chaque paiement carte</div></div>
      <div class="toggle on"><div class="knob"></div></div>
    </div>
    ${renderField(F({ label: 'Arrondir au multiple de', ui: 'select', required: 'yes', options: ['$ 1', '$ 5', '$ 10'] }))}
    ${renderField(F({ label: 'Multiplicateur d\'épargne', ui: 'select', required: 'no', options: ['x1', 'x2', 'x5'] }))}
    ${renderField(F({ label: 'Sous-compte destination', ui: 'select', required: 'yes', options: ['Coffre — Loyer 2026', 'Épargne générale'] }))}
    <div class="notice-box notice-success">${ICONS.piggy}<span>Depuis ce mois : <strong>$ 14.60</strong> épargnés automatiquement.</span></div>
    <div class="sticky-cta"><button class="btn btn-primary">Enregistrer</button></div>
  `,
});

SCREENS.push({
  id: 'pfm-recurring',
  group: 'pfm',
  title: 'Virement récurrent d\'épargne',
  subtitle: 'Écran « Virement récurrent d\'épargne »',
  chrome: 'sub',
  meta: { br: ['BR-PFM-03'], qc: ['QC-PFM-03'], note: 'Virement automatique planifié vers un sous-compte, avec contrôle du solde disponible à chaque échéance.' },
  fields: [
    F({ label: 'Sous-compte destination', specType: 'Liste sous-comptes du client', ui: 'select', required: 'yes', options: ['Coffre — Loyer 2026', 'Épargne générale'] }),
    F({ label: 'Montant', specType: 'Numérique', ui: 'number', required: 'yes', hint: '> 0' }),
    F({ label: 'Devise', specType: 'Liste (USD / CDF)', ui: 'select', required: 'yes', options: ['USD', 'CDF'] }),
    F({ label: 'Fréquence', specType: 'Liste (Hebdomadaire / Mensuelle)', ui: 'select', required: 'yes', options: ['Hebdomadaire', 'Mensuelle'] }),
    F({ label: 'Date de première exécution', specType: 'Date', ui: 'date', required: 'yes' }),
    F({ label: 'Comportement si solde insuffisant', specType: 'Liste (Ignorer / Réessayer J+1 / Notifier)', ui: 'select', required: 'no', options: ['Ignorer cette échéance', 'Réessayer le lendemain', 'Notifier uniquement'] }),
  ],
  body: () => `
    <h1 class="block-title">Virement récurrent</h1>
    <p class="block-sub">Automatisez votre épargne mensuelle</p>
    ${renderField(F({ label: 'Sous-compte destination', ui: 'select', required: 'yes', options: ['Coffre — Loyer 2026', 'Épargne générale'] }))}
    <div style="display:flex;gap:var(--space-3);">
      <div style="flex:1;">${renderField(F({ label: 'Montant', ui: 'number', required: 'yes', prefix: '$', placeholder: '50' }))}</div>
      <div style="flex:1;">${renderField(F({ label: 'Fréquence', ui: 'select', required: 'yes', options: ['Hebdomadaire', 'Mensuelle'] }))}</div>
    </div>
    ${renderField(F({ label: 'Date de première exécution', ui: 'date', required: 'yes' }))}
    ${renderField(F({ label: 'Si solde insuffisant', ui: 'select', required: 'no', options: ['Ignorer cette échéance', 'Réessayer le lendemain', 'Notifier uniquement'] }))}
    <div class="sticky-cta"><button class="btn btn-primary">Activer le virement récurrent</button></div>
  `,
});
