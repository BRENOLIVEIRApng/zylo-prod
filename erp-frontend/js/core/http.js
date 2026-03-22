/* ─── Zylo ERP · http.js ────────────────────────────────────────────────────
   Cliente HTTP com injeção automática de token JWT.
   Depende de: auth.js (ZyloAuth)
   Uso: ZyloHttp.get('/api/endpoint')
────────────────────────────────────────────────────────────────────────────── */

const ZyloHttp = (() => {

  const BASE = 'http://localhost:8080'; // --Substituir pela URL de produção

  const request = async (method, endpoint, body) => {
    const res = await fetch(`${BASE}${endpoint}`, {
      method,
      headers: ZyloAuth.authHeaders(),
      ...(body !== undefined ? { body: JSON.stringify(body) } : {})
    });

    // --Sessão expirada
    if (res.status === 401) { ZyloAuth.logout(); return; }

    // --Sem conteúdo (204 — DELETE, PATCH sem retorno)
    if (res.status === 204) return null;

    const data = await res.json().catch(() => null);

    if (!res.ok) {
      throw new Error(data?.mensagem || data?.message || `HTTP ${res.status}`);
    }

    return data;
  };

  return {
    get:    (ep)       => request('GET',    ep),
    post:   (ep, body) => request('POST',   ep, body),
    put:    (ep, body) => request('PUT',    ep, body),
    patch:  (ep, body) => request('PATCH',  ep, body ?? {}),
    delete: (ep)       => request('DELETE', ep)
  };

})();