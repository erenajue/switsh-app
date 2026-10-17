const fs=require('fs');const {parseHTML}=require('linkedom');
const {SCREENS,GROUPS}=require('./load');const {norm,byInner,ICONS}=require('./icons');
const warn=[];
const kids=e=>[...e.children];
const cls=e=>(e.getAttribute&&e.getAttribute('class')||'').split(/\s+/).filter(Boolean);
const has=(e,c)=>cls(e).includes(c);
const tag=e=>e.tagName.toUpperCase();
const txt=e=>e.textContent.replace(/\s+/g,' ').trim();
const own=e=>[...e.childNodes].filter(n=>n.nodeType===3).map(n=>n.textContent).join('').replace(/\s+/g,' ').trim();
function iconOf(e){const s=tag(e)==='SVG'?e:e.querySelector('svg');if(!s)return null;
  const n=norm(s.outerHTML);if(byInner[n])return byInner[n];if(n.includes('M30.5 11c'))return 'logo';warn.push('icon?'+n.slice(0,60));return null;}
function clean(o){for(const k of Object.keys(o))if(o[k]===undefined||o[k]===null||o[k]===''||(Array.isArray(o[k])&&!o[k].length&&k!=='options'))delete o[k];return o;}

function fieldBlock(el){
  const k=kids(el);const label=k.find(x=>tag(x)==='LABEL');
  const hint=k.find(x=>has(x,'hint'));
  const b={t:'field',hint:hint&&txt(hint)};
  b.fieldId = (el.querySelector('input,textarea')||el).getAttribute('data-field') || undefined;
  if(label){b.label=own(label);const sp=[...label.querySelectorAll('span')];
    const s=sp.map(x=>txt(x)).join(' ');
    b.required=sp.some(x=>has(x,'req'))?'yes':/LECTURE/.test(s)?'ro':/SI APPLICABLE/.test(s)?'cond':'no';}
  const c=k.find(x=>x!==label&&x!==hint);
  if(!c){return clean(b);}
  if(has(c,'input-group')){const i=c.querySelector('input,textarea');const p=c.querySelector('.prefix');
    b.ui=i.getAttribute('type')||'text';b.placeholder=i.getAttribute('placeholder')||'';b.prefix=p&&txt(p);
    const su=c.querySelector('.suffix-icon');if(su)b.icon=iconOf(su);}
  else if(has(c,'select-wrap')){b.ui='select';b.options=[...c.querySelectorAll('option')].map(txt);}
  else if(has(c,'otp-row')){b.ui='otp';b.length=c.querySelectorAll('input').length;}
  else if(has(c,'upload-box')){b.ui='file';b.icon=iconOf(c);b.title=txt(c.querySelector('strong'));b.sub=txt(c.querySelector('span'));b.done=has(c,'done');}
  else if(has(c,'toggle-row')){b.ui='toggle';const l=c.querySelector('.lbl');b.placeholder=l?txt(l):'';b.on=!!c.querySelector('.toggle.on');b.required='no';}
  else if(tag(c)==='TEXTAREA'){b.ui='textarea';b.placeholder=c.getAttribute('placeholder')||'';}
  else if(has(c,'input')){b.ui='readonly';b.placeholder=txt(c);}
  else warn.push('field?'+c.outerHTML.slice(0,80));
  return clean(b);
}
const trail=e=>{ // trailing element -> {trailing, trailingKind}
  if(has(e,'toggle'))return{kind:'toggle',on:has(e,'on')};
  if(tag(e)==='SVG')return{kind:'chevron'};
  if(has(e,'badge'))return{kind:'badge',text:txt(e),style:cls(e).find(c=>c.startsWith('badge-'))||'badge-muted'};
  if(has(e,'amount'))return{kind:'amount',text:txt(e),style:has(e,'pos')?'pos':has(e,'neg')?'neg':''};
  return{kind:'text',text:txt(e),bold:tag(e)==='STRONG'};};
function leadOf(e){return has(e,'icon')||iconOf(e)&&!txt(e)?{icon:iconOf(e)}:{text:txt(e)};}
function infoOf(e){const s=e.querySelector('strong');const rest=kids(e).filter(x=>x!==s);
  return{title:s?txt(s):own(e),sub:rest.map(txt).join(' ')};}
function listRow(el){const b={t:'row'};
  for(const c of kids(el)){
    if(has(c,'avatar')||has(c,'icon')){const l=iconOf(c)&&!txt(c)?{icon:iconOf(c)}:{lead:txt(c)};Object.assign(b,l.icon?{icon:l.icon}:{lead:l.lead});}
    else if(has(c,'info')||c.querySelector&&c.querySelector('strong')&&!has(c,'amount')&&tag(c)==='DIV'&&!b.title){Object.assign(b,infoOf(c));}
    else {const tr=trail(c);b.trailing=tr;}
  }
  return clean(b);}
function toggleRow(el){const l=el.querySelector('.lbl');const sm=l&&l.querySelector('small');
  return clean({t:'toggle',label:l?own(l):'',sub:sm&&txt(sm),on:!!el.querySelector('.toggle.on')});}
function rowCard(el){ // card whose direct children are lead + info + trailing
  const b={t:'row',card:true};const k=kids(el);
  for(const c of k){
    if(has(c,'avatar')){const i=iconOf(c);if(i&&!txt(c))b.icon=i;else b.lead=txt(c);}
    else if(has(c,'icon')){b.icon=iconOf(c);b.tint=/accent/.test(c.getAttribute('style')||'')?'accent':undefined;}
    else if(tag(c)==='DIV'&&c.querySelector('strong')){Object.assign(b,infoOf(c));}
    else b.trailing=trail(c);}
  return clean(b);}
function bankCard(el){const k=kids(el);const top=kids(k[0]);const bot=kids(k[2]);
  return{t:'bankcard',label:txt(top[0]),badge:top[1]&&tag(top[1])==='SPAN'&&has(top[1],'badge')?txt(top[1]):undefined,
    icon:top[1]&&tag(top[1])==='SVG'?iconOf(top[1]):undefined,number:txt(k[1]),holder:txt(bot[0]),extra:txt(bot[1])};}
function notice(el){const b=clean({t:'notice',kind:(cls(el).find(c=>c.startsWith('notice-')&&c!=='notice-box')||'notice-info').slice(7),icon:iconOf(el)});
  const btn=el.querySelector('button');const clone=el.cloneNode(true);[...clone.querySelectorAll('svg,button')].forEach(x=>x.remove());
  b.text=txt(clone);if(btn)b.action=txt(btn);return b;}
function blockOf(el,ctx){
  const c=cls(el),T=tag(el);
  if(has(el,'block-title'))return{t:'title',text:txt(el)};
  if(has(el,'block-sub'))return{t:'subtitle',text:txt(el)};
  if(has(el,'section-label'))return{t:'section',text:txt(el)};
  if(has(el,'field'))return fieldBlock(el);
  if(has(el,'checkline'))return{t:'check',text:txt(el),fieldId:el.getAttribute('data-field')||undefined,checked:has(el,'checked')};
  if(has(el,'notice-box'))return notice(el);
  if(has(el,'card')){const k=kids(el);
    if(k.some(x=>has(x,'avatar')||has(x,'icon')))return{t:'card',children:[rowCard(el)]};
    if(k[0]&&/SWITSH/.test(txt(k[0])))return{t:'card',children:[bankCard(el)],bare:true};
    return{t:'card',children:kids(el).map(x=>blockOf(x,ctx)).filter(Boolean)};}
  if(has(el,'list-row'))return listRow(el);
  if(has(el,'toggle-row'))return toggleRow(el);
  if(has(el,'choice-card')){const t=el.querySelector('.txt');return clean({t:'choice',icon:iconOf(el),title:txt(t.querySelector('strong')),sub:txt(t.querySelector('span')),selected:has(el,'sel')});}
  if(has(el,'upload-box'))return{t:'field',ui:'file',icon:iconOf(el),title:txt(el.querySelector('strong')),sub:txt(el.querySelector('span')),done:has(el,'done'),required:'no'};
  if(has(el,'sticky-cta'))return{t:'cta',children:kids(el).map(x=>{const y=(tag(x)==='DIV'&&kids(x).length===1&&tag(kids(x)[0])==='BUTTON')?kids(x)[0]:x;return blockOf(y,ctx)}).filter(Boolean)};
  if(T==='BUTTON'){const st=has(el,'btn-primary')?'primary':has(el,'btn-secondary')?'secondary':has(el,'btn-danger')?'danger':has(el,'btn-ghost')?'ghost':has(el,'icon-btn')?'icon':/Bloquer/.test(txt(el))?'danger':'secondary';
    return clean({t:'button',text:txt(el),style:st,small:has(el,'btn-sm')||undefined,icon:iconOf(el)});}
  if(has(el,'balance-hero'))return{t:'balance',tag:txt(el.querySelector('.tag')),amount:txt(el.querySelector('.amount')),sub:txt(el.querySelector('.sub'))};
  if(has(el,'wallet-row'))return{t:'wallets',items:kids(el).map(w=>({cur:txt(w.querySelector('.badge')),style:cls(w.querySelector('.badge')).find(x=>x.startsWith('badge-')),label:txt(w.querySelector('.cur')).replace(txt(w.querySelector('.badge')),'').trim(),amount:txt(w.querySelector('.amt'))}))};
  if(has(el,'quick-actions'))return{t:'quick',items:kids(el).map(q=>({icon:iconOf(q),label:txt(q.querySelector('span'))}))};
  if(has(el,'segmented'))return{t:'segmented',options:kids(el).map(txt),active:kids(el).findIndex(x=>has(x,'active'))};
  if(has(el,'pill-select'))return{t:'pills',options:kids(el).map(txt),actives:kids(el).map((x,i)=>has(x,'sel')?i:-1).filter(i=>i>=0)};
  if(has(el,'divider-note'))return{t:'divider',text:txt(el)};
  if(has(el,'progress-track')){const f=el.querySelector('.fill');const m=/width:\s*([\d.]+)%/.exec(f.getAttribute('style')||'');return{t:'progress',percent:m?+m[1]:0};}
  if(has(el,'chat-bubble'))return{t:'chat',from:has(el,'user')?'user':'bot',text:txt(el)};
  if(T==='P')return{t:'text',text:txt(el)};
  if(T==='SVG')return{t:'icon',icon:iconOf(el)};
  if(T==='DIV'){const k=kids(el);
    if(!k.length)return null;
    if(k.length>=2&&k.every(x=>tag(x)==='DIV'&&x.querySelector('.field')&&!has(x,'field')))return{t:'fieldrow',children:k.map(x=>fieldBlock(x.querySelector('.field')))};
    if(k.length===1&&tag(k[0])==='SVG')return{t:'icon',icon:iconOf(k[0])};
    if(el.querySelector('h1.block-title')){const h=el.querySelector('h1');const p=el.querySelector('p');return clean({t:'hero',icon:iconOf(el),title:txt(h),sub:p&&txt(p)});}
    if(ctx&&ctx.id==='bank-accounts'&&k.length===2&&k[1].querySelector('button')){const a=k[0];return{t:'greeting',hello:txt(kids(a)[0]),name:txt(a.querySelector('strong')),bell:true};}
    if(k.some(x=>has(x,'section-label'))&&k.length===2)return{t:'sectionlink',text:txt(k[0]),link:txt(k[1])};
    return null;}
  warn.push('unhandled '+T+' '+c.join('.'));return null;
}
const ICON_FILE={};
const out=[];
for(const s of SCREENS){
  const {document}=parseHTML('<div id=r>'+s.body()+'</div>');const root=document.getElementById('r');
  const sc={id:s.id,group:s.group,title:s.title,subtitle:s.subtitle,chrome:s.chrome,tab:s.tab,step:s.step,bonus:s.bonus,blocks:[]};
  const k=kids(root);
  if(s.id==='start-splash'){sc.blocks.push({t:'splash'});out.push(clean(sc));continue;}
  if(s.id==='start-unlock'){sc.blocks.push({t:'pinpad',name:'Mireille',title:'Bon retour, Mireille',sub:txt(root.querySelector('.block-sub')),forgot:'Code PIN oublié ?'});out.push(clean(sc));continue;}
  const langRows=k.filter(x=>has(x,'language-row'));
  for(const e of k){
    if(has(e,'language-row')){if(e===langRows[0])sc.blocks.push({t:'languages',items:langRows.map(r=>({name:txt(r.querySelector('.native')),code:txt(r.querySelector('.code'))}))});continue;}
    let b;
    try{b=blockOf(e,sc)}catch(err){warn.push(s.id+' ERR '+err.message+' '+e.outerHTML.slice(0,100));b=null}
    if(!b){warn.push(s.id+' dropped: '+e.outerHTML.replace(/<svg.*?<\/svg>/gs,'').replace(/\s+/g,' ').slice(0,160));continue;}
    sc.blocks.push(b);
  }
  out.push(clean(sc));
}

const NEXT={'onb-inscription':'onb-otp','onb-otp':'onb-auth-security','onb-auth-security':'onb-profile-select','onb-profile-select':'onb-id-particulier','onb-id-particulier':'onb-infos-perso','onb-infos-perso':'onb-justif-domicile','onb-justif-domicile':'onb-status','onb-kyb':'onb-status','onb-status':'bank-accounts','start-language':'onb-inscription','start-offline':'start-splash'};
const QUICK={'Envoyer':'pay-p2p','Convertir':'pay-converter','Payer QR':'pay-merchant','Sous-compte':'bank-subaccount'};
const ROWS={'Langue':'settings-language','Sécurité & authentification':'onb-auth-security','Aide & support':'support-chatbot','Mireille Kabuya':'onb-status'};
const link=(icon,title,to,sub)=>({t:'row',icon,title,sub,trailing:{kind:'chevron'},to});
const MENU={
 'card-physical':['Mes cartes',[link('card','Gérer ma carte','card-manage','Plafonds, options, blocage'),link('layers','Carte virtuelle','card-virtual','Générée instantanément')]],
 'pay-p2p':['Autres paiements',[link('bank','Virement externe','pay-external'),link('phone','Cash-in / Cash-out','pay-cashinout'),link('arrows','Convertisseur de devises','pay-converter'),link('qr','Paiement marchand','pay-merchant'),link('users','Cagnotte partagée','pay-cagnotte'),link('file','Division de facture','pay-split')]],
 'pfm-budget':['Épargne automatique',[link('piggy','Arrondi automatique','pfm-roundup'),link('repeat','Virement récurrent d\'épargne','pfm-recurring')]],
 'settings-profile':['Mes opérations',[link('file','Historique des opérations','bank-history'),link('download','Générer un relevé','bank-statement'),link('plus','Créer un sous-compte','bank-subaccount'),link('building','Entreprise / PME (KYB)','onb-kyb'),link('headset','Chat avec un agent','support-livechat'),link('flag','Ouvrir un litige','support-dispute')]],
};
for(const sc of out){
  const walk=bs=>{for(const b of bs){
    if(b.t==='cta'){const pb=b.children.find(x=>x.t==='button'&&x.style==='primary');if(pb&&NEXT[sc.id])pb.to=NEXT[sc.id];if(sc.id==='onb-profile-select')pb.toChoice=['onb-id-particulier','onb-kyb'];
      const g=b.children.find(x=>x.style==='ghost');if(g&&sc.id==='onb-otp')g.action='resend';}
    if(b.t==='quick')b.items.forEach(i=>i.to=QUICK[i.label]);
    if(b.t==='sectionlink'&&b.link==='Tout voir')b.to='bank-history';
    if(b.t==='row'&&sc.id==='settings-profile'&&ROWS[b.title])b.to=ROWS[b.title];
    if(b.t==='pills'&&sc.id==='support-chatbot')b.targets=['onb-justif-domicile','','support-livechat'];
    if(b.t==='button'&&b.text==='Se déconnecter')b.action='logout';
    if(b.children)walk(b.children);}};
  walk(sc.blocks);
  if(MENU[sc.id])sc.blocks.push({t:'section',text:MENU[sc.id][0]},{t:'card',children:MENU[sc.id][1]});
}
// Groups exported to the app; extend this list as more sections are implemented.
const INCLUDE=['start','onboarding'];
fs.mkdirSync('../../app/src/main/assets',{recursive:true});
const A='../../app/src/main/';
fs.writeFileSync(A+'assets/screens.json',JSON.stringify({groups:GROUPS.filter(g=>INCLUDE.includes(g.id)).map(g=>({id:g.id,label:g.label})),screens:out.filter(s=>INCLUDE.includes(s.group))}));
// icons
const used=new Set(JSON.stringify(out).match(/"icon":"(\w+)"/g).map(x=>x.slice(8,-1)));
const snake=n=>n.replace(/[A-Z]/g,c=>'_'+c.toLowerCase());
const f=n=>+(+n).toFixed(3);
function vec(inner){let p='';
  for(const m of inner.matchAll(/<(path|rect|circle)\s([^>]*?)\/?>/g)){
    const a={};for(const x of m[2].matchAll(/([\w-]+)="([^"]*)"/g))a[x[1]]=x[2];
    let d,filled=a.fill==='currentColor';
    if(m[1]==='path')d=a.d;
    else if(m[1]==='circle'){const cx=+a.cx,cy=+a.cy,r=+a.r;d=`M${f(cx-r)},${cy}a${r},${r} 0 1,0 ${f(2*r)},0a${r},${r} 0 1,0 ${f(-2*r)},0Z`;}
    else{const x=+a.x,y=+a.y,w=+a.width,h=+a.height,r=+(a.rx||0);d=r?`M${x+r},${y}H${x+w-r}A${r},${r} 0 0,1 ${x+w},${y+r}V${y+h-r}A${r},${r} 0 0,1 ${x+w-r},${y+h}H${x+r}A${r},${r} 0 0,1 ${x},${y+h-r}V${y+r}A${r},${r} 0 0,1 ${x+r},${y}Z`:`M${x},${y}H${x+w}V${y+h}H${x}Z`;}
    p+=filled?`    <path android:fillColor="#FF000000" android:pathData="${d}"/>\n`:`    <path android:pathData="${d}" android:strokeColor="#FF000000" android:strokeWidth="2" android:strokeLineCap="round" android:strokeLineJoin="round"/>\n`;
  }
  return `<vector xmlns:android="http://schemas.android.com/apk/res/android"\n    android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24">\n${p}</vector>\n`;}
for(const [n,svg] of Object.entries(ICONS)){fs.writeFileSync(A+`res/drawable/ic_${snake(n)}.xml`,vec(norm(svg)));}
console.log('icons used not defined:',[...used].filter(u=>!ICONS[u]&&u!=='logo'));

console.log('warnings:\n'+warn.join('\n'));
