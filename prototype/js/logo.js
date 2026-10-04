/* ===== Global screen registry (populated by data-*.js modules) ===== */
const SCREENS = [];

/* ===== Switsh brand mark =====
   Two interlocking arcs forming an implied "S" — reads as a currency
   switch/exchange glyph (USD ⇄ CDF). Monochrome, currentColor, works at
   any size from 20px (tab bar) to 200px (splash). */
function LOGO_MARK(size) {
  size = size || 28;
  return `<svg width="${size}" height="${size}" viewBox="0 0 40 40" fill="none" aria-label="Switsh" role="img">
    <path d="M30.5 11c-1.8-3.2-5.3-5.3-9.3-5.3-6 0-10.4 4-10.4 8.8 0 4.4 3.2 6.6 8.4 7.8"
      stroke="currentColor" stroke-width="4" stroke-linecap="round" fill="none"/>
    <path d="M9.5 29c1.8 3.2 5.3 5.3 9.3 5.3 6 0 10.4-4 10.4-8.8 0-4.4-3.2-6.6-8.4-7.8"
      stroke="currentColor" stroke-width="4" stroke-linecap="round" fill="none" opacity="0.45"/>
    <path d="M27 7.5 30.8 11l-4 3" stroke="currentColor" stroke-width="4" stroke-linecap="round" stroke-linejoin="round" fill="none"/>
    <path d="M13 32.5 9.2 29l4-3" stroke="currentColor" stroke-width="4" stroke-linecap="round" stroke-linejoin="round" fill="none" opacity="0.45"/>
  </svg>`;
}

/* Full lock-up used in the topbar: mark + wordmark */
function LOGO_FULL() {
  return `<span class="brand-mark">${LOGO_MARK(26)}</span><span class="brand-word">Switsh</span>`;
}
