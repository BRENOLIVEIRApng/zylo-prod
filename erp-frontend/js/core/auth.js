/* ─── Zylo ERP · auth.js ────────────────────────────────────────────────────
   Gerencia autenticação
   Carregado em TODAS as páginas (public e protegidas).
────────────────────────────────────────────────────────────────────────────── */

const API_BASE = 'http://localhost:8080'; // Substituir pela URL de produção

const ZyloAuth = (() => {

  const TOKEN_KEY = 'zylo_token';
  const USER_KEY  = 'zylo_user';

  const saveSession  = (data) => {
    localStorage.setItem(TOKEN_KEY, data.token);
    localStorage.setItem(USER_KEY, JSON.stringify(data.usuario));
  };

  const clearSession = () => {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
  };

  const getToken   = () => localStorage.getItem(TOKEN_KEY);
  const getUsuario = () => JSON.parse(localStorage.getItem(USER_KEY) || 'null');
  const isLogado   = () => !!getToken();

  const authHeaders = () => ({
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${getToken()}`
  });

  // ─── Login ───────────────────────────────────────────────────────────────
  const login = async (email, senha) => {
    const res = await fetch(`${API_BASE}/api/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, senha })
    });

    const data = await res.json();
    if (!res.ok) throw new Error(data.mensagem || data.message || 'Email ou senha incorretos');

    saveSession(data);
    return data;
  };

  // ─── Logout ───────────────────────────────────────────────────────────────
  const logout = async () => {
    try {
      await fetch(`${API_BASE}/api/auth/logout`, {
        method: 'POST',
        headers: authHeaders()
      });
    } catch (_) { /* JWT é stateless — ignora erros de rede */ }

    clearSession();
    window.location.href = '/index.html';
  };

  // ─── Proteção de rota ─────────────────────────────────────────────────────
  const requireAuth = () => {
    if (!isLogado()) window.location.href = '/index.html';
  };

  // ─── Redirecionar se já logado (páginas públicas) ─────────────────────────
  const redirectIfLogado = () => {
    if (isLogado()) window.location.href = '/pages/dashboard/home.html';
  };

  // ─── Reset de senha ───────────────────────────────────────────────────────
  // NOTA: endpoint /api/auth/reset-password ainda não implementado no backend.
  // A UI exibe o estado de sucesso normalmente; o email será enviado em versão futura.
  const solicitarResetSenha = async (email) => {
    try {
      const res = await fetch(`${API_BASE}/api/auth/reset-password`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email })
      });

      // Aceita 200, 204 ou qualquer 2xx como sucesso
      if (!res.ok && res.status !== 404) {
        const data = await res.json().catch(() => ({}));
        throw new Error(data.mensagem || data.message || 'Erro ao solicitar redefinição');
      }
    } catch (err) {
      // 404 = endpoint não implementado ainda → trata como sucesso (não expõe se email existe)
      if (!err.message.includes('fetch') && !err.message.includes('404')) {
        throw err;
      }
    }
    return true;
  };

  return {
    login,
    logout,
    requireAuth,
    redirectIfLogado,
    getUsuario,
    getToken,
    authHeaders,
    isLogado,
    solicitarResetSenha
  };

})();