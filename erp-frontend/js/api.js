// ─── Zylo ERP — API Client ────────────────────────────────────────────────────
// Centraliza todas as chamadas HTTP ao backend.
// Injeta o token JWT automaticamente em todos os requests autenticados.

const API_URL = 'http://localhost:8080/api'; // trocar pela URL de produção no deploy

// ─── Token helpers ────────────────────────────────────────────────────────────

const TOKEN_KEY = 'zylo_token';
const USUARIO_KEY = 'zylo_usuario';

export const Auth = {
    salvarToken: (token) => localStorage.setItem(TOKEN_KEY, token),
    obterToken: () => localStorage.getItem(TOKEN_KEY),
    removerToken: () => {
        localStorage.removeItem(TOKEN_KEY);
        localStorage.removeItem(USUARIO_KEY);
    },
    estaLogado: () => !!localStorage.getItem(TOKEN_KEY),

    salvarUsuario: (usuario) => localStorage.setItem(USUARIO_KEY, JSON.stringify(usuario)),
    obterUsuario: () => {
        const u = localStorage.getItem(USUARIO_KEY);
        return u ? JSON.parse(u) : null;
    },

    // Decodifica o payload do JWT sem biblioteca (apenas leitura, não valida assinatura)
    decodificarToken: () => {
        const token = Auth.obterToken();
        if (!token) return null;
        try {
            const payload = token.split('.')[1];
            return JSON.parse(atob(payload));
        } catch {
            return null;
        }
    },

    tokenExpirado: () => {
        const claims = Auth.decodificarToken();
        if (!claims) return true;
        return Date.now() >= claims.exp * 1000;
    }
};

// ─── HTTP Client ──────────────────────────────────────────────────────────────

async function request(method, endpoint, body = null, auth = true) {
    // Redireciona para login se token expirado
    if (auth && Auth.tokenExpirado()) {
        Auth.removerToken();
        window.location.href = '/pages/login.html';
        return;
    }

    const headers = { 'Content-Type': 'application/json' };

    if (auth) {
        headers['Authorization'] = `Bearer ${Auth.obterToken()}`;
    }

    const options = { method, headers };
    if (body) options.body = JSON.stringify(body);

    const response = await fetch(`${API_URL}${endpoint}`, options);

    // Token expirou durante a sessão
    if (response.status === 401) {
        Auth.removerToken();
        window.location.href = '/pages/login.html';
        return;
    }

    // Sem conteúdo (204 No Content)
    if (response.status === 204) return null;

    const data = await response.json();

    if (!response.ok) {
        // Lança o erro com a mensagem do backend
        throw new Error(data.erro || data.message || 'Erro desconhecido');
    }

    return data;
}

// ─── Auth endpoints ───────────────────────────────────────────────────────────

export const AuthAPI = {
    login: async (email, senha) => {
        const data = await request('POST', '/auth/login', { email, senha }, false);

        Auth.salvarToken(data.token);
        Auth.salvarUsuario(data.usuario);

        return data;
    },

    logout: () => {
        Auth.removerToken();
        window.location.href = '/pages/login.html';
    },

    me: () => request('GET', '/usuarios/me')
};

// ─── Usuários endpoints ───────────────────────────────────────────────────────

export const UsuariosAPI = {
    listar: () => request('GET', '/usuarios'),
    buscarPorId: (id) => request('GET', `/usuarios/${id}`),
    criar: (dto) => request('POST', '/usuarios', dto),
    editar: (id, dto) => request('PUT', `/usuarios/${id}`, dto),
    desativar: (id) => request('PATCH', `/usuarios/${id}/desativar`),
    ativar: (id) => request('PATCH', `/usuarios/${id}/ativar`),
    resetarSenha: (id, novaSenha) => request('PATCH', `/usuarios/${id}/resetar-senha`, { novaSenha }),
    alterarSenha: (senhaAtual, novaSenha) => request('PATCH', '/usuarios/alterar-senha', { senhaAtual, novaSenha })
};

// ─── Perfis endpoints ─────────────────────────────────────────────────────────

export const PerfisAPI = {
    listar: () => request('GET', '/perfis'),
    listarSistema: () => request('GET', '/perfis/sistema'),
    buscarPorId: (id) => request('GET', `/perfis/${id}`),
    criar: (dto) => request('POST', '/perfis', dto),
    editar: (id, dto) => request('PUT', `/perfis/${id}`, dto),
    excluir: (id) => request('DELETE', `/perfis/${id}`),
    sincronizarPermissoes: (id, permissoesIds) =>
        request('PUT', `/perfis/${id}/permissoes`, { permissoesIds })
};

// ─── Helpers de UI ────────────────────────────────────────────────────────────

export function mostrarErro(mensagem, containerId = 'erro-container') {
    const el = document.getElementById(containerId);
    if (el) {
        el.textContent = mensagem;
        el.style.display = 'block';
    }
}

export function ocultarErro(containerId = 'erro-container') {
    const el = document.getElementById(containerId);
    if (el) el.style.display = 'none';
}

export function mostrarLoading(btnId, texto = 'Aguarde...') {
    const btn = document.getElementById(btnId);
    if (btn) {
        btn.disabled = true;
        btn.dataset.textoOriginal = btn.textContent;
        btn.textContent = texto;
    }
}

export function pararLoading(btnId) {
    const btn = document.getElementById(btnId);
    if (btn) {
        btn.disabled = false;
        btn.textContent = btn.dataset.textoOriginal || 'Confirmar';
    }
}