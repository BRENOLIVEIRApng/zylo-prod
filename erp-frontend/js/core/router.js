/* ─── Zylo ERP · router.js ──────────────────────────────────────────────────
   Guardas de rota e helpers de navegação.
   Depende de: auth.js (ZyloAuth)
────────────────────────────────────────────────────────────────────────────── */

const ZyloRouter = (() => {

  // --Mapa: arquivo HTML → permissão mínima necessária
  const _rotasProtegidas = {
    'usuarios-lista.html':    'USUARIOS:VER',
    'usuarios-detalhe.html':  'USUARIOS:VER',
    'perfis-lista.html':      'USUARIOS:VER',
    'clientes-lista.html':    'CLIENTES:VER',
    'contratos-lista.html':   'CONTRATOS:VER',
    'servicos-lista.html':    'SERVICOS:VER',
    'os-kanban.html':         'OS:VER',
    'faturas-lista.html':     'FATURAMENTO:VER',
  };

  // --Exigir autenticação (redireciona para login se não estiver logado)
  const requireAuth = () => {
    if (!ZyloAuth.isLogado()) {
      window.location.href = '/index.html';
      return false;
    }
    return true;
  };

  // --Verificar permissão mínima para a rota atual
  const checkPermissao = () => {
    const pagina = window.location.pathname.split('/').pop();
    const permissaoNecessaria = _rotasProtegidas[pagina];
    if (!permissaoNecessaria) return true; // rota sem restrição

    const usuario = ZyloAuth.getUsuario();
    if (!usuario?.permissoes) return true; // sem mapa de permissões no token = deixa passar

    const [modulo, acao] = permissaoNecessaria.split(':');
    const temAcesso = usuario.permissoes?.some(p => p === permissaoNecessaria)
      ?? true; // fallback permissivo enquanto não há mapa completo no JWT

    if (!temAcesso) {
      window.location.href = '/pages/dashboard/home.html';
      return false;
    }
    return true;
  };

  // --Inicializar proteção (chamar no início de toda página interna)
  const init = () => {
    requireAuth();
    checkPermissao();
  };

  // --Navegar preservando query string atual
  const goTo = (url) => { window.location.href = url; };

  // --Ler parâmetro da query string
  const param = (key) => new URLSearchParams(window.location.search).get(key);

  return { init, requireAuth, checkPermissao, goTo, param };

})();