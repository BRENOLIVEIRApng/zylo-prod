/* ─── Zylo ERP · sidebar.js ─────────────────────────────────────────────────
   Sidebar icon-rail com flyout controlado por JS.
   Hover → mouseenter/mouseleave (desktop)
   Clique → toggle (mobile/touch)
   Depende de: auth.js, utils.js (ZyloAuth, ZyloFormat)
────────────────────────────────────────────────────────────────────────────── */

const ZyloSidebar = (() => {

  // --Definição centralizada da navegação
  const _nav = [
    {
      icon: 'bi-house',
      label: 'Dashboard',
      links: [
        { href: '/pages/dashboard/home.html', label: 'Início', page: 'home.html' }
      ]
    },
    {
      icon: 'bi-buildings',
      label: 'Clientes',
      links: [
        { href: '/pages/clientes/clientes-lista.html',    label: 'Lista de Clientes', page: 'clientes-lista.html' },
        { href: '/pages/clientes/clientes-form.html',     label: 'Novo Cliente',      page: 'clientes-form.html'  }
      ]
    },
    {
      icon: 'bi-file-earmark-text',
      label: 'Contratos',
      links: [
        { href: '/pages/contratos/contratos-lista.html',  label: 'Lista de Contratos', page: 'contratos-lista.html'  },
        { href: '/pages/contratos/contratos-wizard.html', label: 'Novo Contrato',       page: 'contratos-wizard.html' }
      ]
    },
    {
      icon: 'bi-gear',
      label: 'Serviços',
      links: [
        { href: '/pages/servicos/servicos-lista.html',    label: 'Catálogo',      page: 'servicos-lista.html' },
        { href: '/pages/servicos/servicos-form.html',     label: 'Novo Serviço',  page: 'servicos-form.html'  }
      ]
    },
    {
      icon: 'bi-kanban',
      label: 'Ordens de Serviço',
      badge: 'badgeOS',
      links: [
        { href: '/pages/ordens-servico/os-kanban.html',   label: 'Kanban',      page: 'os-kanban.html' },
        { href: '/pages/ordens-servico/os-lista.html',    label: 'Lista de OS', page: 'os-lista.html'  },
        { href: '/pages/ordens-servico/os-form.html',     label: 'Nova OS',     page: 'os-form.html'   }
      ]
    },
    {
      icon: 'bi-receipt',
      label: 'Faturamento',
      links: [
        { href: '/pages/faturamento/faturas-lista.html',  label: 'Faturas',     page: 'faturas-lista.html' },
        { href: '/pages/faturamento/faturas-form.html',   label: 'Nova Fatura', page: 'faturas-form.html'  }
      ]
    },
    { divider: true },
    {
      icon: 'bi-people',
      label: 'Usuários',
      links: [
        { href: '/pages/admin/usuarios-lista.html',       label: 'Lista de Usuários', page: 'usuarios-lista.html'  },
        { href: '/pages/admin/usuarios-detalhe.html',     label: 'Meu Perfil',        page: 'usuarios-detalhe.html' }
      ]
    },
    {
      icon: 'bi-shield-lock',
      label: 'Perfis e Permissões',
      links: [
        { href: '/pages/admin/perfis-lista.html',         label: 'Perfis',  page: 'perfis-lista.html' }
      ]
    }
  ];

  // --Página atual ativa
  const _isAtivo = (grupo, currentPage) =>
    grupo.links?.some(l => l.page === currentPage);

  // --HTML de um item de navegação
  const _renderItem = (grupo, currentPage) => {
    if (grupo.divider) return `<div class="sidebar-divider"></div>`;

    const ativo     = _isAtivo(grupo, currentPage);
    const linksHtml = grupo.links.map(l => {
      const linkAtivo = l.page === currentPage;
      return `<a href="${l.href}" class="flyout-link${linkAtivo ? ' active' : ''}">${l.label}</a>`;
    }).join('');

    return `
      <div class="nav-rail-item${ativo ? ' active' : ''}">
        <div class="nav-rail-btn" title="${grupo.label}">
          <i class="bi ${grupo.icon}"></i>
          ${grupo.badge ? `<span class="nav-rail-badge" id="${grupo.badge}"></span>` : ''}
        </div>
        <div class="nav-flyout">
          <span class="flyout-title">${grupo.label}</span>
          ${linksHtml}
        </div>
      </div>`;
  };

  // --Fechar todos os flyouts abertos
  const _fecharTodos = (mountPoint) => {
    mountPoint.querySelectorAll('.nav-rail-item.flyout-open')
      .forEach(el => el.classList.remove('flyout-open'));
  };

  // --Bind de eventos em um item do rail (hover desktop + clique mobile)
  const _bindItem = (item, mountPoint) => {
    let closeTimer = null;

    // Hover desktop — mouseenter abre, mouseleave fecha com pequeno delay
    item.addEventListener('mouseenter', () => {
      clearTimeout(closeTimer);
      _fecharTodos(mountPoint);
      item.classList.add('flyout-open');
    });

    item.addEventListener('mouseleave', () => {
      closeTimer = setTimeout(() => item.classList.remove('flyout-open'), 120);
    });

    // Manter aberto quando mouse está sobre o flyout
    const flyout = item.querySelector('.nav-flyout');
    flyout?.addEventListener('mouseenter', () => clearTimeout(closeTimer));
    flyout?.addEventListener('mouseleave', () => {
      closeTimer = setTimeout(() => item.classList.remove('flyout-open'), 120);
    });

    // Clique no botão (toggle — útil em touch e teclado)
    item.querySelector('.nav-rail-btn')?.addEventListener('click', (e) => {
      e.stopPropagation();
      const wasOpen = item.classList.contains('flyout-open');
      _fecharTodos(mountPoint);
      if (!wasOpen) item.classList.add('flyout-open');
    });
  };

  // --Renderizar sidebar completa no #sidebar
  const render = (currentPage = '') => {
    const mountPoint = document.getElementById('sidebar');
    if (!mountPoint) return;

    const usuario = ZyloAuth.getUsuario();
    const iniciais = ZyloFormat.iniciais(usuario?.nomeCompleto || '');
    const navHtml   = _nav.map(g => _renderItem(g, currentPage)).join('');

    mountPoint.innerHTML = `
      <div class="sidebar-logo-wrap">
        <div class="sidebar-logo" title="Zylo ERP">
          <i class="bi bi-grid-3x3-gap-fill"></i>
        </div>
      </div>

      <nav class="sidebar-nav" aria-label="Navegação principal">
        ${navHtml}
      </nav>

      <div class="sidebar-footer">
        <div class="nav-rail-item">
          <div class="nav-rail-btn" title="${usuario?.nomeCompleto || 'Usuário'}">
            <div class="user-avatar-sm">${iniciais}</div>
          </div>
          <div class="nav-flyout flyout-user">
            <div class="flyout-user-header">
              <div class="flyout-user-avatar">${iniciais}</div>
              <div>
                <div class="flyout-user-nome">${usuario?.nomeCompleto || '—'}</div>
                <div class="flyout-user-perfil">${usuario?.perfil || ''}</div>
              </div>
            </div>
            <div class="flyout-divider"></div>
            <a href="/pages/admin/usuarios-detalhe.html?id=${usuario?.codigoUsuario || ''}" class="flyout-link">
              <i class="bi bi-person"></i> Minha Conta
            </a>
            <a href="/pages/admin/usuarios-detalhe.html?id=${usuario?.codigoUsuario || ''}#senha" class="flyout-link">
              <i class="bi bi-key"></i> Alterar Senha
            </a>
            <div class="flyout-divider"></div>
            <a href="#" class="flyout-link flyout-link-danger" data-action="logout">
              <i class="bi bi-box-arrow-right"></i> Sair
            </a>
          </div>
        </div>
      </div>`;

    // --Bind hover/clique em cada item
    mountPoint.querySelectorAll('.nav-rail-item').forEach(item => {
      _bindItem(item, mountPoint);
    });

    // --Logout
    mountPoint.querySelectorAll('[data-action="logout"]').forEach(el => {
      el.addEventListener('click', (e) => {
        e.preventDefault();
        if (confirm('Deseja realmente sair?')) ZyloAuth.logout();
      });
    });

    // --Fechar flyouts ao clicar fora da sidebar
    document.addEventListener('click', (e) => {
      if (!mountPoint.contains(e.target)) _fecharTodos(mountPoint);
    });
  };

  return { render };

})();