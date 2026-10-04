const GROUPS = [
  { id: 'start', label: 'Démarrage', color: 'var(--color-accent)', domainRef: 'Accès & première ouverture' },
  { id: 'onboarding', label: 'Onboarding & KYC/AML', color: '#C23B3B', domainRef: 'Domaine 1 — BRD §5' },
  { id: 'banking', label: 'Comptes bidevises', color: 'var(--color-usd)', domainRef: 'Domaine 2 — BRD §6' },
  { id: 'cards', label: 'Cartes physiques & virtuelles', color: '#7A39BB', domainRef: 'Domaine 3 — BRD §7' },
  { id: 'payments', label: 'Paiements & interopérabilité', color: 'var(--color-primary)', domainRef: 'Domaine 4 — BRD §8' },
  { id: 'pfm', label: 'Épargne & budget (PFM)', color: 'var(--color-accent)', domainRef: 'Domaine 5 — BRD §9' },
  { id: 'support', label: 'Service client & support', color: '#006494', domainRef: 'Domaine 6 — BRD §10' },
  { id: 'settings', label: 'Langues & profil', color: '#437A22', domainRef: 'Module transverse — BRD §14' },
];

function groupById(id) { return GROUPS.find((g) => g.id === id); }
