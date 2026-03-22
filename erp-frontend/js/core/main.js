/* ─── Zylo ERP · main.js ────────────────────────────────────────────────────
   Carregado em TODAS as páginas protegidas (após auth.js).
   Fornece: ZyloHttp, ZyloFormat e inicialização do layout.
────────────────────────────────────────────────────────────────────────────── */

document.addEventListener('DOMContentLoaded', () => {

  // ─── Sidebar toggle (mobile) ───────────────────────────────────────────────
  const sidebar       = document.getElementById('sidebar');
  const sidebarToggle = document.getElementById('sidebarToggle');

  sidebarToggle?.addEventListener('click', () => sidebar?.classList.toggle('open'));

  document.addEventListener('click', (e) => {
    if (sidebar && !sidebar.contains(e.target) && !sidebarToggle?.contains(e.target)) {
      sidebar.classList.remove('open');
    }
  });

  // ─── Preencher dados do usuário logado ────────────────────────────────────
  const usuario = ZyloAuth.getUsuario();

  if (usuario) {
    const iniciais = usuario.nomeCompleto
      ?.split(' ').slice(0, 2).map(n => n[0]).join('').toUpperCase() || '?';

    document.querySelectorAll('[data-user-nome]').forEach(el => {
      el.textContent = usuario.nomeCompleto?.split(' ')[0] || usuario.nomeCompleto;
    });
    document.querySelectorAll('[data-user-perfil]').forEach(el => {
      el.textContent = usuario.perfil || '';
    });
    document.querySelectorAll('[data-user-iniciais]').forEach(el => {
      el.textContent = iniciais;
    });
    document.querySelectorAll('[data-user-email]').forEach(el => {
      el.textContent = usuario.email;
    });
  }

  // ─── Logout ───────────────────────────────────────────────────────────────
  document.querySelectorAll('[data-action="logout"]').forEach(el => {
    el.addEventListener('click', (e) => {
      e.preventDefault();
      if (confirm('Deseja realmente sair do sistema?')) ZyloAuth.logout();
    });
  });

  // ─── Nav link active state ─────────────────────────────────────────────────
  const currentPage = window.location.pathname.split('/').pop();
  document.querySelectorAll('.nav-link[data-page]').forEach(link => {
    if (link.dataset.page === currentPage) link.classList.add('active');
  });

  // ─── Bootstrap tooltips ───────────────────────────────────────────────────
  document.querySelectorAll('[data-bs-toggle="tooltip"]').forEach(el => {
    new bootstrap.Tooltip(el, { trigger: 'hover' });
  });

});