/* ===== Shared render helpers used by every screen module ===== */

function esc(s) {
  return String(s == null ? '' : s).replace(/[&<>"']/g, (c) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
}

function L(key, fallback) {
  return typeof LABEL_KEYS !== 'undefined' && LABEL_KEYS ? `{{${key}}}` : fallback;
}

/* Field descriptor factory — one object powers both the mobile UI input AND the BRD spec table row. */
function F(opts) {
  return Object.assign({
    label: '', specType: 'Texte', ui: 'text', required: 'yes', hint: '', options: [], placeholder: '', icon: null, prefix: null, fieldId: null,
  }, opts);
}

function reqBadge(required) {
  if (required === 'yes') return '<span class="req-yes">Oui</span>';
  if (required === 'cond') return '<span class="req-cond">Cond.</span>';
  if (required === 'ro') return '<span class="req-no">Lecture seule</span>';
  return '<span class="req-no">Non</span>';
}

function fieldLabelHtml(f) {
  const star = f.required === 'yes' ? '<span class="req">*</span>' : (f.required === 'cond' ? '<span class="opt">SI APPLICABLE</span>' : (f.required === 'ro' ? '<span class="opt">LECTURE SEULE</span>' : '<span class="opt">OPTIONNEL</span>'));
  return `<label>${esc(f.label)} ${star}</label>`;
}

/* Renders one field as a realistic mobile input control */
function renderField(f) {
  const hint = f.hint ? `<div class="hint">${esc(f.hint)}</div>` : '';
  let control = '';
  switch (f.ui) {
    case 'text':
    case 'email':
    case 'tel':
    case 'number':
    case 'date': {
      const prefix = f.prefix ? `<span class="prefix">${esc(f.prefix)}</span>` : '';
      control = `<div class="input-group">${prefix}<input class="input ${f.prefix ? 'has-prefix' : ''}" type="${f.ui === 'tel' ? 'text' : f.ui}" placeholder="${esc(f.placeholder)}" ${f.fieldId ? `data-field="${esc(f.fieldId)}"` : ''} /></div>`;
      break;
    }
    case 'password':
      control = `<div class="input-group"><input class="input" type="password" placeholder="${esc(f.placeholder || '••••••••')}" ${f.fieldId ? `data-field="${esc(f.fieldId)}"` : ''} /></div>`;
      break;
    case 'textarea':
      control = `<textarea class="input" rows="3" placeholder="${esc(f.placeholder)}" style="resize:none;"></textarea>`;
      break;
    case 'otp':
      control = `<div class="otp-row">${[0, 1, 2, 3, 4, 5].map((i) => `<input class="otp-box ${i < 3 ? 'filled' : ''}" maxlength="1" value="${i < 3 ? '•' : ''}" />`).join('')}</div>`;
      break;
    case 'select': {
      const opts = f.options.map((o) => `<option>${esc(o)}</option>`).join('');
      control = `<div class="select-wrap"><select class="select">${opts || '<option>' + esc(f.placeholder || 'Sélectionner') + '</option>'}</select></div>`;
      break;
    }
    case 'file':
      control = `<div class="upload-box ${f.done ? 'done' : ''}">${ICONS[f.icon || 'upload']}<strong>${esc(f.placeholder || L('common.add_file', 'Ajouter un fichier'))}</strong><span>${esc(f.hint || L('common.file_formats', 'JPG, PNG ou PDF · 10 Mo max'))}</span></div>`;
      hint && (control += '');
      break;
    case 'toggle':
      control = `<div class="toggle-row" style="padding:0;border-bottom:none;"><span class="lbl" style="font-weight:400;font-size:var(--text-xs);color:var(--color-text-muted);">${esc(f.placeholder || 'Activer')}</span><div class="toggle ${f.on ? 'on' : ''}"><div class="knob"></div></div></div>`;
      break;
    case 'readonly':
      control = `<div class="input" style="background:var(--color-surface-offset);color:var(--color-text-muted);">${esc(f.placeholder || '—')}</div>`;
      break;
    default:
      control = `<div class="input">${esc(f.placeholder)}</div>`;
  }
  if (f.ui === 'toggle') return `<div class="field">${control}${hint}</div>`;
  return `<div class="field">${fieldLabelHtml(f)}${control}${hint}</div>`;
}

function renderFields(fields) {
  return fields.map(renderField).join('');
}

/* Renders the BRD field-spec table for the right-hand inspector panel */
function renderSpecTable(fields) {
  return fields.map((f) => `
    <div class="spec-field-row">
      <div class="spec-field-head">
        <span class="spec-field-name">${esc(f.label)}</span>
        ${reqBadge(f.required)}
      </div>
      <div class="spec-field-type">${esc(f.specType)}</div>
      ${f.hint ? `<div class="spec-field-hint">${esc(f.hint)}</div>` : ''}
    </div>`).join('');
}

function renderEngines(engines) {
  if (!engines || !engines.length) return '';
  return `<div class="spec-section-title">Moteurs de calcul</div>` + engines.map((e) => `
    <div class="spec-engine"><strong>${esc(e.name)}</strong>${esc(e.logic)}</div>`).join('');
}

function renderQC(qcs) {
  if (!qcs || !qcs.length) return '';
  return `<div class="spec-section-title">Contrôles qualité</div>` + qcs.map((q) => `
    <div class="spec-qc-item"><span class="sev ${q.sev === 'Bloquant' ? 'sev-block' : 'sev-warn'}">${esc(q.id)}</span><span>${esc(q.rule)}</span></div>`).join('');
}

/* Header used inside the phone for sub-screens (with back arrow) */
function phoneHeader(title, opts) {
  opts = opts || {};
  return `<div class="app-header">
    <button class="back-btn" data-action="prev-screen" aria-label="Retour">${ICONS.chevronLeft}</button>
    <h1>${esc(title)}</h1>
    ${opts.action ? `<span class="header-action">${opts.action}</span>` : '<span style="width:2rem"></span>'}
  </div>`;
}

function statusBar() {
  return `<div class="status-bar"><span>9:41</span><div class="status-icons"><span style="font-size:11px;">4G</span><span>▮▮▮</span><span>🔋</span></div></div>`;
}

function stepProgress(current, total) {
  let segs = '';
  for (let i = 1; i <= total; i++) {
    segs += `<div class="seg ${i < current ? 'done' : i === current ? 'current' : ''}"></div>`;
  }
  return `<div class="step-progress">${segs}</div>`;
}

const TABS = [
  { id: 'accounts', label: 'Accueil', icon: 'home' },
  { id: 'cards', label: 'Cartes', icon: 'card' },
  { id: 'payments', label: 'Paiements', icon: 'send' },
  { id: 'pfm', label: 'Épargne', icon: 'piggy' },
  { id: 'more', label: 'Plus', icon: 'gear' },
];

function tabBar(active) {
  return `<div class="tab-bar">${TABS.map((t) => `
    <div class="tab-item ${t.id === active ? 'active' : ''}">${ICONS[t.icon]}<span>${t.label}</span></div>`).join('')}</div>`;
}
