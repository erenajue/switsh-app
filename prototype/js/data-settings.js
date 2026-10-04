/* ===== Module transverse — Langues & Profil client ===== */

SCREENS.push({
  id: 'settings-profile',
  group: 'settings',
  title: 'Profil',
  subtitle: 'Écran complémentaire (UX) — accès aux réglages du compte',
  chrome: 'hub',
  tab: 'more',
  bonus: true,
  meta: { note: 'Écran ajouté pour regrouper les accès aux réglages transverses (langue, sécurité, notifications) — hub d\'entrée de l\'onglet « Plus ».' },
  fields: [],
  body: () => `
    <h1 class="block-title" style="margin-top:var(--space-2);">Plus</h1>
    <div class="card" style="display:flex;align-items:center;gap:var(--space-3);margin-bottom:var(--space-5);">
      <div class="avatar" style="width:3rem;height:3rem;font-size:var(--text-base);">MK</div>
      <div style="flex:1;"><strong>Mireille Kabuya</strong><div style="font-size:12px;color:var(--color-text-faint);">Niveau KYC 2 · Client depuis mars 2026</div></div>
      ${ICONS.chevronRight}
    </div>
    <div class="card">
      <div class="list-row"><div class="avatar" style="background:var(--color-primary-highlight);color:var(--color-primary);">${ICONS.globe}</div><div class="info"><strong>Langue</strong><span>Français</span></div>${ICONS.chevronRight}</div>
      <div class="list-row"><div class="avatar" style="background:var(--color-primary-highlight);color:var(--color-primary);">${ICONS.shield}</div><div class="info"><strong>Sécurité & authentification</strong><span>PIN, biométrie, appareils</span></div>${ICONS.chevronRight}</div>
      <div class="list-row"><div class="avatar" style="background:var(--color-primary-highlight);color:var(--color-primary);">${ICONS.bell}</div><div class="info"><strong>Notifications</strong><span>Push, SMS, e-mail</span></div>${ICONS.chevronRight}</div>
      <div class="list-row"><div class="avatar" style="background:var(--color-primary-highlight);color:var(--color-primary);">${ICONS.headset}</div><div class="info"><strong>Aide & support</strong><span>Chatbot, live chat, litiges</span></div>${ICONS.chevronRight}</div>
    </div>
    <button class="btn" style="width:100%;background:var(--color-error-highlight);color:var(--color-error);margin-top:var(--space-4);">Se déconnecter</button>
  `,
});

SCREENS.push({
  id: 'settings-language',
  group: 'settings',
  title: 'Préférences de langue',
  subtitle: 'Écran « Préférences de langue »',
  chrome: 'sub',
  meta: {
    br: ['BR-LNG-03', 'BR-LNG-04'], qc: ['QC-LNG-02'],
    note: 'Le client peut modifier sa langue d\'interface à tout moment depuis son profil ; la modification s\'applique immédiatement sans reconnexion.',
  },
  fields: [
    F({ label: 'Langue de l\'interface active', specType: 'Liste (langues actives)', ui: 'select', required: 'yes', options: ['Français', 'English', 'Lingála', 'Kikongo', 'Tshiluba', 'Kiswahili'] }),
    F({ label: 'Langue des notifications', specType: 'Liste (indépendante de l\'interface)', ui: 'select', required: 'no', hint: 'Peut différer de la langue de l\'interface', options: ['Identique à l\'interface', 'Français', 'English', 'Lingála'] }),
    F({ label: 'Format des dates et montants', specType: 'Lecture seule', ui: 'readonly', required: 'ro', hint: 'Dérivé automatiquement de la langue sélectionnée' }),
  ],
  qcList: [{ id: 'QC-LNG-02', rule: 'La langue sélectionnée doit appartenir à la liste des langues actives du référentiel.', sev: 'Bloquant' }],
  body: () => `
    <h1 class="block-title">Langue</h1>
    <p class="block-sub">Application immédiate, sans reconnexion</p>
    ${[
      ['Français', 'FR', true], ['English', 'EN', false], ['Lingála', 'LN', false],
      ['Kikongo', 'KG', false], ['Tshiluba', 'LUA', false], ['Kiswahili', 'SW', false],
    ].map(([native, code, sel]) => `
      <div class="language-row ${sel ? 'sel' : ''}">
        <div><span class="native">${native}</span><span class="code">${code}</span></div>
        <div class="check">${sel ? `<div style="color:white;display:flex;align-items:center;justify-content:center;height:100%;">${ICONS.check}</div>` : ''}</div>
      </div>`).join('')}
    <div class="section-label">Langue des notifications</div>
    ${renderField(F({ label: 'Notifications push & SMS', ui: 'select', required: 'no', options: ['Identique à l\'interface (Français)', 'English', 'Lingála'] }))}
    <div class="sticky-cta"><button class="btn btn-primary">Enregistrer</button></div>
  `,
});
