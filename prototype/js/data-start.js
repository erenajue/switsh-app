SCREENS.push({
  id: 'start-splash',
  group: 'start',
  title: 'Écran de lancement',
  subtitle: 'Premier lancement de l\'application',
  chrome: 'splash',
  meta: { note: 'Écran complémentaire (UX) précédant le choix de la langue — pose l\'identité visuelle Switsh avant tout écran métier du BRD.' },
  bonus: true,
  fields: [],
  body: () => `
    <div style="height:100%;display:flex;flex-direction:column;align-items:center;justify-content:center;gap:var(--space-6);background:linear-gradient(160deg, var(--color-primary) 0%, var(--color-primary-active) 100%);color:white;margin:-1rem -1.25rem 0;padding:2rem;flex:1;">
      <div style="width:88px;height:88px;border-radius:28px;background:rgba(255,255,255,0.14);display:flex;align-items:center;justify-content:center;">
        ${LOGO_MARK(44)}
      </div>
      <div style="text-align:center;">
        <div style="font-family:var(--font-display);font-size:1.8rem;font-weight:700;letter-spacing:-0.02em;">Switsh</div>
        <div style="font-size:var(--text-sm);opacity:0.85;margin-top:4px;">Votre banque mobile bidevise USD/CDF</div>
      </div>
      <div style="position:absolute;bottom:56px;display:flex;gap:6px;">
        <span style="width:22px;height:5px;border-radius:99px;background:white;"></span>
        <span style="width:5px;height:5px;border-radius:99px;background:rgba(255,255,255,0.4);"></span>
        <span style="width:5px;height:5px;border-radius:99px;background:rgba(255,255,255,0.4);"></span>
      </div>
    </div>`,
});

SCREENS.push({
  id: 'start-language',
  group: 'start',
  title: 'Choix de la langue',
  subtitle: 'Premier lancement, avant inscription',
  chrome: 'auth',
  meta: {
    br: ['BR-LNG-01', 'BR-LNG-02', 'BR-LNG-05'],
    qc: ['QC-LNG-01', 'QC-LNG-03'],
    note: 'Module transverse 14 — l\'écran s\'affiche avant toute saisie de données ; seules les langues actives du référentiel sont proposées, dans leur forme native.',
  },
  fields: [
    F({ label: 'Langue de l\'interface', specType: 'Liste (langues actives uniquement)', ui: 'select', required: 'yes', hint: 'Valeurs issues du référentiel des langues ; libellé affiché dans sa forme native', options: ['Français', 'English', 'Lingála', 'Kikongo', 'Tshiluba', 'Kiswahili'] }),
    F({ label: 'Langue proposée par défaut', specType: 'Lecture seule', ui: 'readonly', required: 'ro', hint: 'Déduite de la langue du terminal si active, sinon français' }),
    F({ label: 'Aperçu de l\'interface', specType: 'Lecture seule', ui: 'readonly', required: 'ro', hint: 'Extrait de libellés affiché dans la langue sélectionnée avant confirmation' }),
    F({ label: 'Confirmation du choix', specType: 'Bouton d\'action', ui: 'toggle', required: 'yes', hint: 'Enregistre la langue sur l\'appareil puis sur le profil dès la création du compte' }),
  ],
  engines: [{ name: 'Moteur de résolution de la langue active', logic: 'Préférence profil → langue du terminal si active → langue par défaut (français) ; exclusion des langues inactives.' }],
  body: () => `
    <div style="text-align:center;padding-top:var(--space-8);margin-bottom:var(--space-6);">
      ${LOGO_MARK(40)}
      <h1 class="block-title" style="margin-top:var(--space-4);">Choisissez votre langue</h1>
      <p class="block-sub">Sélectionnez la langue de l\'application. Vous pourrez la modifier à tout moment.</p>
    </div>
    ${[
      ['Français', 'FR', true], ['English', 'EN', false], ['Lingála', 'LN', false],
      ['Kikongo', 'KG', false], ['Tshiluba', 'LUA', false], ['Kiswahili', 'SW', false],
    ].map(([native, code, sel]) => `
      <div class="language-row ${sel ? 'sel' : ''}">
        <div><span class="native">${native}</span><span class="code">${code}</span></div>
        <div class="check">${sel ? `<div style="color:white;display:flex;align-items:center;justify-content:center;height:100%;">${ICONS.check}</div>` : ''}</div>
      </div>`).join('')}
    <div class="sticky-cta"><button class="btn btn-primary">Continuer</button></div>
  `,
});

SCREENS.push({
  id: 'start-offline',
  group: 'start',
  title: L('onb.network.required.title', 'Une connexion Internet active est nécessaire pour continuer.'),
  subtitle: '',
  chrome: 'auth',
  body: () => `
    <h1 class="block-title">${L('onb.network.required.title', 'Une connexion Internet active est nécessaire pour continuer.')}</h1>
    <p class="block-sub">${L('onb.network.required.message', 'Connectez-vous à Internet, puis réessayez.')}</p>
    <div class="sticky-cta">
      <button class="btn btn-primary" data-to="start-splash">${L('common.retry', 'Réessayer')}</button>
    </div>
  `,
});

SCREENS.push({
  id: 'start-unlock',
  group: 'start',
  title: 'Déverrouillage — PIN & biométrie',
  subtitle: 'Reconnexion d\'un client déjà inscrit',
  chrome: 'auth',
  bonus: true,
  meta: {
    br: ['BR-ONB-02'],
    note: 'Écran complémentaire (UX) illustrant l\'authentification forte (SCA) exigée par BR-ONB-02 pour toute session — aucune opération sensible sans elle.',
  },
  fields: [
    F({ label: 'Code PIN', specType: 'Numérique, 4 ou 6 chiffres', ui: 'text', required: 'yes', hint: 'Ne doit pas être une séquence triviale (0000, 1234)' }),
    F({ label: 'Déverrouillage biométrique', specType: 'Face ID / empreinte', ui: 'toggle', required: 'no', hint: 'Proposé automatiquement si le capteur de l\'appareil le permet' }),
  ],
  body: () => `
    <div style="text-align:center;padding-top:var(--space-10);">
      <div style="width:64px;height:64px;border-radius:50%;background:var(--color-primary-highlight);color:var(--color-primary);display:flex;align-items:center;justify-content:center;margin:0 auto var(--space-4);">${ICONS.face}</div>
      <div style="font-weight:700;">Bon retour, Mireille</div>
      <p class="block-sub">Entrez votre code PIN pour accéder à votre compte</p>
      <div style="display:flex;justify-content:center;gap:var(--space-3);margin:var(--space-6) 0;">
        ${[0,1,2,3].map(i => `<span style="width:14px;height:14px;border-radius:50%;background:${i<2?'var(--color-primary)':'var(--color-surface-offset-2)'};display:inline-block;"></span>`).join('')}
      </div>
      <div style="display:grid;grid-template-columns:repeat(3,1fr);gap:var(--space-3);max-width:230px;margin:0 auto;">
        ${['1','2','3','4','5','6','7','8','9',ICONS.fingerprint,'0','⌫'].map(k => `<div style="aspect-ratio:1;border-radius:50%;display:flex;align-items:center;justify-content:center;font-weight:700;font-size:var(--text-lg);background:var(--color-surface-offset);color:${typeof k==='string' && k.length<=2 ? 'var(--color-text)':'var(--color-primary)'};">${k}</div>`).join('')}
      </div>
      <div style="margin-top:var(--space-6);"><button class="btn btn-ghost" style="width:auto;">Code PIN oublié ?</button></div>
    </div>
  `,
});
