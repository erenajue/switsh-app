/* ===== Switsh — prototype engine ===== */
(function () {
  let currentId = SCREENS[0].id;
  let query = '';
  const collapsed = new Set();
  let theme = 'light';

  const $sidebar = document.getElementById('sidebar-list');
  const $stage = document.getElementById('phone-stage-content');
  const $spec = document.getElementById('spec-panel-content');
  const $search = document.getElementById('sidebar-search-input');
  const $themeToggle = document.getElementById('theme-toggle');
  const $sidebarToggle = document.getElementById('sidebar-toggle');
  const $specToggle = document.getElementById('spec-toggle');
  const $sidebarEl = document.querySelector('.sidebar');
  const $specEl = document.querySelector('.spec-panel');

  function findScreen(id) { return SCREENS.find((s) => s.id === id); }
  function indexOf(id) { return SCREENS.findIndex((s) => s.id === id); }

  function norm(s) { return (s || '').toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, ''); }

  function matches(screen, q) {
    if (!q) return true;
    const hay = norm(screen.title + ' ' + screen.subtitle + ' ' + groupById(screen.group).label);
    return hay.includes(norm(q));
  }

  /* ---------- Sidebar ---------- */
  function renderSidebar() {
    const html = GROUPS.map((g) => {
      const screens = SCREENS.filter((s) => s.group === g.id && matches(s, query));
      if (!screens.length) return '';
      const isCollapsed = collapsed.has(g.id);
      return `<div class="group ${isCollapsed ? 'collapsed' : ''}" data-group="${g.id}">
        <button class="group-header" data-action="toggle-group" data-group="${g.id}">
          <span class="dot" style="background:${g.color}"></span>
          <span>${esc(g.label)}</span>
          <span class="count">${screens.length}</span>
          <span class="chev">${ICONS.chevronDown}</span>
        </button>
        <div class="group-list">
          ${screens.map((s, i) => `
            <button class="screen-link ${s.id === currentId ? 'active' : ''}" data-action="go-screen" data-screen="${s.id}">
              <span class="num">${String(i + 1).padStart(2, '0')}</span>
              <span>${esc(s.title)}</span>
            </button>`).join('')}
        </div>
      </div>`;
    }).join('');
    $sidebar.innerHTML = html || `<p style="padding:var(--space-4);font-size:var(--text-sm);color:var(--color-text-faint);">Aucun écran ne correspond à « ${esc(query)} ».</p>`;
  }

  /* ---------- Phone stage ---------- */
  function composePhone(screen) {
    const sb = statusBar();
    if (screen.chrome === 'splash') {
      return `${sb}<div class="screen-scroll" style="padding:0;">${screen.body()}</div>`;
    }
    if (screen.chrome === 'hub') {
      return `${sb}<div class="screen-scroll">${screen.body()}</div>${tabBar(screen.tab)}`;
    }
    if (screen.chrome === 'auth') {
      return `${sb}<div class="screen-scroll">${screen.body()}</div>`;
    }
    const header = phoneHeader(screen.title);
    const step = screen.step ? stepProgress(screen.step[0], screen.step[1]) : '';
    return `${sb}${header}${step}<div class="screen-scroll">${screen.body()}</div>`;
  }

  function renderStage() {
    const screen = findScreen(currentId);
    const idx = indexOf(currentId);
    const group = groupById(screen.group);

    $stage.innerHTML = `
      <div class="stage-breadcrumb">
        <span class="crumb-domain">${esc(group.label)}</span>
        <span>${ICONS.chevronRight}</span>
        <span>${esc(screen.title)}</span>
      </div>
      <div class="phone-frame">
        <div class="phone-notch"></div>
        <div class="phone-screen">${composePhone(screen)}</div>
        <div class="home-indicator"></div>
      </div>
      <div class="stage-nav">
        <button class="icon-btn" data-action="prev-screen" ${idx <= 0 ? 'disabled' : ''} aria-label="Écran précédent">${ICONS.chevronLeft}</button>
        <div class="stage-nav-title"><strong>${esc(screen.title)}</strong><span>Écran ${idx + 1} / ${SCREENS.length}</span></div>
        <button class="icon-btn" data-action="next-screen" ${idx >= SCREENS.length - 1 ? 'disabled' : ''} aria-label="Écran suivant">${ICONS.chevronRight}</button>
      </div>
      <p class="stage-footer-note">${screen.bonus ? 'Écran complémentaire ajouté pour une expérience complète — voir le panneau BRD pour la traçabilité.' : 'Écran directement issu de la spécification BRD — voir le panneau de droite pour la fiche complète.'}</p>
    `;
  }

  /* ---------- Spec panel ---------- */
  function renderSpec() {
    const screen = findScreen(currentId);
    const group = groupById(screen.group);
    const meta = screen.meta || {};
    const tags = [
      ...(meta.br || []).map((b) => `<span class="tag br">${esc(b)}</span>`),
      ...(meta.qc || []).map((q) => `<span class="tag qc">${esc(q)}</span>`),
      screen.bonus ? `<span class="tag bonus">Complément UX</span>` : '',
    ].join('');

    $spec.innerHTML = `
      <div class="spec-card">
        <div class="spec-eyebrow">${esc(group.domainRef)}</div>
        <h2 class="spec-title">${esc(screen.title)}</h2>
        <p class="spec-desc">${esc(screen.subtitle)}</p>
        ${tags ? `<div class="spec-meta">${tags}</div>` : ''}
        ${meta.note ? `<p class="spec-desc" style="margin-bottom:var(--space-4);">${esc(meta.note)}</p>` : ''}
        ${screen.fields && screen.fields.length ? `<div class="spec-section-title">Spécification des champs (BRD)</div>${renderSpecTable(screen.fields)}` : ''}
        ${renderEngines(screen.engines)}
        ${renderQC(screen.qcList)}
      </div>
    `;
  }

  function renderAll() {
    renderSidebar();
    renderStage();
    renderSpec();
  }

  /* ---------- Navigation ---------- */
  function goTo(id) {
    currentId = id;
    renderAll();
    if (window.innerWidth <= 860) $sidebarEl.classList.remove('mobile-open');
  }
  function goDelta(delta) {
    const idx = indexOf(currentId);
    const next = SCREENS[idx + delta];
    if (next) goTo(next.id);
  }

  /* ---------- Theme ---------- */
  function applyTheme() {
    document.documentElement.setAttribute('data-theme', theme);
    $themeToggle.innerHTML = theme === 'dark' ? ICONS.sun : ICONS.moon;
  }

  /* ---------- Event delegation ---------- */
  document.addEventListener('click', (e) => {
    const goBtn = e.target.closest('[data-action="go-screen"]');
    if (goBtn) { goTo(goBtn.dataset.screen); return; }

    const toggleGroup = e.target.closest('[data-action="toggle-group"]');
    if (toggleGroup) {
      const g = toggleGroup.dataset.group;
      collapsed.has(g) ? collapsed.delete(g) : collapsed.add(g);
      renderSidebar();
      return;
    }

    if (e.target.closest('[data-action="prev-screen"]')) { goDelta(-1); return; }
    if (e.target.closest('[data-action="next-screen"]')) { goDelta(1); return; }

    if (e.target.closest('#theme-toggle')) { theme = theme === 'light' ? 'dark' : 'light'; applyTheme(); return; }
    if (e.target.closest('#sidebar-toggle')) { $sidebarEl.classList.toggle('mobile-open'); $specEl.classList.remove('mobile-open'); return; }
    if (e.target.closest('#spec-toggle')) { $specEl.classList.toggle('mobile-open'); $sidebarEl.classList.remove('mobile-open'); return; }
  });

  $search.addEventListener('input', (e) => { query = e.target.value; renderSidebar(); });

  /* ---------- Init ---------- */
  applyTheme();
  renderAll();
})();
