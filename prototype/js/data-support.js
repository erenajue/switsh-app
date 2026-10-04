/* ===== Domaine 6 — Support Client ===== */

SCREENS.push({
  id: 'support-chatbot',
  group: 'support',
  title: 'Chatbot & FAQ',
  subtitle: 'Écran « Chatbot & FAQ »',
  chrome: 'sub',
  tab: 'more',
  meta: { br: ['BR-SUP-01'], qc: [], note: 'Premier niveau de support automatisé, avec bascule vers le live chat si aucune réponse ne satisfait le client.' },
  fields: [
    F({ label: 'Question du client', specType: 'Texte libre', ui: 'text', required: 'yes' }),
    F({ label: 'Catégorie FAQ suggérée', specType: 'Liste', ui: 'readonly', required: 'ro', hint: 'Déduite par le moteur de matching sémantique' }),
    F({ label: 'Réponse jugée satisfaisante', specType: 'Oui / Non', ui: 'toggle', required: 'no', hint: 'Si Non → proposition de bascule vers le live chat' }),
  ],
  engines: [{ name: 'Moteur de matching sémantique FAQ', logic: 'Recherche par similarité sur la base de connaissances multilingue ; score de confiance → réponse directe ou escalade.' }],
  body: () => `
    <h1 class="block-title">Assistant Switsh</h1>
    <div class="chat-bubble bot">Bonjour Mireille 👋 Comment puis-je vous aider aujourd\'hui ?</div>
    <div class="chat-bubble user">Comment augmenter mon plafond de virement ?</div>
    <div class="chat-bubble bot">Pour augmenter votre plafond, complétez votre dossier au Niveau 3 KYC en fournissant un justificatif de domicile. Voulez-vous que je vous y redirige ?</div>
    <div class="pill-select"><span class="opt sel">Oui, m\'y rediriger</span><span class="opt">Voir la FAQ</span><span class="opt">Parler à un agent</span></div>
    <div class="field" style="position:sticky;bottom:0;background:var(--color-surface);padding-top:var(--space-3);">
      <div class="input-group"><input class="input" style="padding-right:2.75rem;" placeholder="Écrivez votre message…" /><span class="suffix-icon" style="cursor:pointer;color:var(--color-primary);">${ICONS.send}</span></div>
    </div>
  `,
});

SCREENS.push({
  id: 'support-livechat',
  group: 'support',
  title: 'Live Chat',
  subtitle: 'Écran « Live Chat / Motif de contact »',
  chrome: 'sub',
  meta: { br: ['BR-SUP-02'], qc: ['QC-SUP-01'], note: 'Sélection du motif de contact avant mise en relation, pour un routage direct vers l\'agent compétent.' },
  fields: [
    F({ label: 'Motif de contact', specType: 'Liste (Compte / Carte / Paiement / KYC / Autre)', ui: 'select', required: 'yes', options: ['Problème de compte', 'Carte bancaire', 'Paiement / virement', 'Vérification KYC', 'Autre'] }),
    F({ label: 'Description du problème', specType: 'Texte libre', ui: 'textarea', required: 'yes' }),
    F({ label: 'Pièce jointe', specType: 'Image/PDF', ui: 'file', required: 'no', icon: 'paperclip' }),
    F({ label: 'Temps d\'attente estimé', specType: 'Numérique (minutes)', ui: 'readonly', required: 'ro', hint: 'Calculé selon la charge des agents en ligne' }),
  ],
  body: () => `
    <h1 class="block-title">Contacter un agent</h1>
    ${renderField(F({ label: 'Motif de contact', ui: 'select', required: 'yes', options: ['Problème de compte', 'Carte bancaire', 'Paiement / virement', 'Vérification KYC', 'Autre'] }))}
    ${renderField(F({ label: 'Décrivez votre problème', ui: 'textarea', required: 'yes', placeholder: 'Expliquez votre situation en quelques mots…' }))}
    ${renderField(F({ label: 'Ajouter une pièce jointe', ui: 'file', required: 'no', icon: 'paperclip', placeholder: 'Capture d\'écran ou document' }))}
    <div class="notice-box notice-info">${ICONS.headset}<span>Temps d\'attente estimé : <strong>~3 minutes</strong> · 4 agents en ligne</span></div>
    <div class="sticky-cta"><button class="btn btn-primary">${ICONS.chat} Démarrer le chat</button></div>
  `,
});

SCREENS.push({
  id: 'support-dispute',
  group: 'support',
  title: 'Ouvrir un litige',
  subtitle: 'Écran « Ouvrir un litige »',
  chrome: 'sub',
  meta: { br: ['BR-SUP-03'], qc: ['QC-SUP-02', 'QC-SUP-03'], note: 'Litige rattaché obligatoirement à une transaction existante ; délai réglementaire de traitement affiché au client.' },
  fields: [
    F({ label: 'Transaction concernée', specType: 'Sélection depuis l\'historique', ui: 'select', required: 'yes', hint: 'Doit correspondre à une transaction existante du client', options: ['Kin Marché — $ 12.50 · 30/08', 'Conversion USD→CDF — $ 30.00 · 28/08'] }),
    F({ label: 'Motif du litige', specType: 'Liste (Fraude / Montant incorrect / Non reçu / Double débit / Autre)', ui: 'select', required: 'yes', options: ['Transaction non reconnue (fraude)', 'Montant incorrect', 'Service/bien non reçu', 'Double débit', 'Autre'] }),
    F({ label: 'Description détaillée', specType: 'Texte libre', ui: 'textarea', required: 'yes' }),
    F({ label: 'Pièces justificatives', specType: 'Image/PDF (multi-fichiers)', ui: 'file', required: 'no', icon: 'paperclip' }),
    F({ label: 'Numéro de dossier', specType: 'Alphanumérique', ui: 'readonly', required: 'ro', hint: 'Généré automatiquement à la soumission' }),
  ],
  qcList: [{ id: 'QC-SUP-02', rule: 'La transaction sélectionnée doit exister et appartenir au client.', sev: 'Bloquant' }],
  body: () => `
    <h1 class="block-title">Ouvrir un litige</h1>
    <p class="block-sub">Traitement sous 10 jours ouvrés (réglementation RDC)</p>
    ${renderField(F({ label: 'Transaction concernée', ui: 'select', required: 'yes', options: ['Kin Marché — $ 12.50 · 30/08', 'Conversion USD→CDF — $ 30.00 · 28/08'] }))}
    ${renderField(F({ label: 'Motif du litige', ui: 'select', required: 'yes', options: ['Transaction non reconnue (fraude)', 'Montant incorrect', 'Service/bien non reçu', 'Double débit', 'Autre'] }))}
    ${renderField(F({ label: 'Décrivez la situation', ui: 'textarea', required: 'yes' }))}
    ${renderField(F({ label: 'Pièces justificatives', ui: 'file', required: 'no', icon: 'paperclip', placeholder: 'Ajouter des photos ou documents' }))}
    <div class="notice-box notice-warn">${ICONS.alert}<span>Un numéro de dossier vous sera communiqué immédiatement après soumission.</span></div>
    <div class="sticky-cta"><button class="btn btn-primary">Soumettre le litige</button></div>
  `,
});
