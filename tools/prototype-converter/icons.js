const {ICONS}=require('./load');
const norm=s=>s.replace(/^<svg[^>]*>/,'').replace(/<\/svg>$/,'').replace(/\s+/g,' ').replace(/\s*\/>/g,'/>').replace(/<(\w+)\s+/g,'<$1 ').trim();
const byInner={};for(const [k,v] of Object.entries(ICONS))byInner[norm(v)]=k;
module.exports={norm,byInner,ICONS};
