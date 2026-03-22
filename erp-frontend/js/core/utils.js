/* ─── Zylo ERP · utils.js ───────────────────────────────────────────────────
   Utilitários de UI e formatação.
   Carregado em TODAS as páginas (após auth.js).
────────────────────────────────────────────────────────────────────────────── */

/* ─── UI Helpers ─────────────────────────────────────────────────────────── */
const ZyloUI = (() => {

  const _icons = {
    error:   'bi-exclamation-triangle-fill',
    success: 'bi-check-circle-fill',
    info:    'bi-info-circle-fill',
    warning: 'bi-exclamation-circle-fill'
  };

  // --Exibir alerta inline em um container
  const showAlert = (containerId, mensagem, tipo = 'error') => {
    const el = document.getElementById(containerId);
    if (!el) return;
    el.innerHTML = `
      <div class="zylo-alert ${tipo}" role="alert">
        <i class="bi ${_icons[tipo] || _icons.info}"></i>
        <span>${mensagem}</span>
      </div>`;
  };

  const clearAlert = (containerId) => {
    const el = document.getElementById(containerId);
    if (el) el.innerHTML = '';
  };

  // --Estado de loading em botão
  const btnLoading = (btn, loading = true, texto = 'Aguarde...') => {
    if (loading) {
      btn.dataset.originalHtml = btn.innerHTML;
      btn.innerHTML = `<span class="spinner-border spinner-border-sm" aria-hidden="true"></span> ${texto}`;
      btn.disabled = true;
    } else {
      btn.innerHTML = btn.dataset.originalHtml || btn.innerHTML;
      btn.disabled = false;
    }
  };

  // --Toggle visibilidade de campo senha
  const toggleSenha = (inputId, btnId) => {
    const input = document.getElementById(inputId);
    const btn   = document.getElementById(btnId);
    if (!input || !btn) return;
    const visible = input.type === 'text';
    input.type = visible ? 'password' : 'text';
    const icon = btn.querySelector('i');
    if (icon) icon.className = `bi ${visible ? 'bi-eye' : 'bi-eye-slash'}`;
  };

  // --Toast flutuante (canto superior direito)
  const toast = (mensagem, tipo = 'success', duracao = 3500) => {
    let container = document.getElementById('zylo-toast-container');
    if (!container) {
      container = document.createElement('div');
      container.id = 'zylo-toast-container';
      container.style.cssText =
        'position:fixed;top:1rem;right:1rem;z-index:9999;display:flex;flex-direction:column;gap:.5rem;';
      document.body.appendChild(container);
    }
    const el = document.createElement('div');
    el.className = `zylo-alert ${tipo}`;
    el.style.cssText = 'min-width:260px;box-shadow:0 4px 12px rgba(0,0,0,.1);animation:fadeIn .2s ease;';
    el.innerHTML = `<i class="bi ${_icons[tipo] || _icons.info}"></i><span>${mensagem}</span>`;
    container.appendChild(el);
    setTimeout(() => el.remove(), duracao);
  };

  return { showAlert, clearAlert, btnLoading, toggleSenha, toast };

})();

/* ─── Formatadores ────────────────────────────────────────────────────────── */
const ZyloFormat = {
  moeda:    (v) => new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(v || 0),
  data:     (v) => v ? new Date(v + 'T00:00:00').toLocaleDateString('pt-BR') : '—',
  dataHora: (v) => v ? new Date(v).toLocaleString('pt-BR') : '—',
  iniciais: (nome) => nome?.split(' ').filter(Boolean).slice(0, 2).map(n => n[0].toUpperCase()).join('') || '?',
  cnpj:     (v) => v?.replace(/^(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})$/, '$1.$2.$3/$4-$5') || v,
  telefone: (v) => v?.replace(/^(\d{2})(\d{4,5})(\d{4})$/, '($1) $2-$3') || v
};