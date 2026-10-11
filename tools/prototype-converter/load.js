const fs=require('fs'),vm=require('vm');
const P='../../prototype/js/';
const ctx={console,LABEL_KEYS:true};vm.createContext(ctx);
for(const f of ['logo','groups','icons','helpers','data-start','data-onboarding','data-banking','data-cards','data-payments','data-pfm','data-support','data-settings']){
  let src=fs.readFileSync(P+f+'.js','utf8');
  // make const/function top-level visible
  src=src.replace(/^const (\w+) =/gm,'var $1 =');
  vm.runInContext(src,ctx,{filename:f});
}
module.exports=vm.runInContext('({SCREENS,GROUPS,TABS,ICONS})',ctx);
