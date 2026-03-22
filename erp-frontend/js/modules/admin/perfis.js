/* ─── Zylo ERP · perfis.js ──────────────────────────────────────────────────── */

const ZyloPerfis = (() => {

  // ─── Estado privado ────────────────────────────────────────────────────────
  let _perfis         = [];
  let _permissoes     = [];       // todas as permissoes do sistema
  let _perfilSelecionado = null;  // objeto perfil completo com permissoes[]
  let _permAlteradas  = new Set();// ids das permissoes marcadas no painel atual
  let _editando       = false;    // painel em modo edição

  // Mapeamento de módulo para ícone BI
  const _moduloIcone = {
    CLIENTES:    'bi-buildings',
    CONTRATOS:   'bi-file-earmark-text',
    SERVICOS:    'bi-gear',
    OS:          'bi-kanban',
    FATURAMENTO: 'bi-receipt',
    USUARIOS:    'bi-people',
    DASHBOARD:   'bi-house',
    RELATORIOS:  'bi-bar-chart',
    AUDITORIA:   'bi-shield-exclamation',
  };

  // Mapeamento de ação para ícone BI
  const _acaoIcone = {
    VER:    'bi-eye',
    CRIAR:  'bi-plus-circle',
    EDITAR: 'bi-pencil',
    EXCLUIR:'bi-trash3',
  };

  const _acaoLabel = { VER: 'Ver', CRIAR: 'Criar', EDITAR: 'Editar', EXCLUIR: 'Excluir' };

  // ─── Service (espelha PerfilController) ────────────────────────────────────
  const Service = {

    listarTodos:         () => ZyloHttp.get('/api/perfis'),
    listarSistema:       () => ZyloHttp.get('/api/perfis/sistema'),
    listarPersonalizados:() => ZyloHttp.get('/api/perfis/personalizados'),
    buscarComPermissoes: (id) => ZyloHttp.get(`/api/perfis/${id}`),
    listarPermissoesById:(id) => ZyloHttp.get(`/api/perfis/${id}/permissoes`),

    criar: (payload) => ZyloHttp.post('/api/perfis', payload),
    editar:(id, payload) => ZyloHttp.put(`/api/perfis/${id}`, payload),
    excluir:(id) => ZyloHttp.delete(`/api/perfis/${id}`),

    sincronizarPermissoes: (id, permissoesIds) =>
      ZyloHttp.put(`/api/perfis/${id}/permissoes`, { permissoesIds: [...permissoesIds] }),

    adicionarPermissao: (idPerfil, idPermissao) =>
      ZyloHttp.post(`/api/perfis/${idPerfil}/permissoes/${idPermissao}`, {}),

    removerPermissao: (idPerfil, idPermissao) =>
      ZyloHttp.delete(`/api/perfis/${idPerfil}/permissoes/${idPermissao}`),

    // --Buscar todas as permissoes do sistema via /api/perfis (extraindo de perfil Admin)
    // Na ausência de endpoint dedicado, listamos permissoes de um perfil sistema
    listarTodasPermissoes: async () => {
      // Tenta buscar do perfil ADMIN (id=1 ou pelo endpoint /api/perfis/sistema)
      const sistema = await Service.listarSistema();
      if (sistema?.length) {
        const admin = await Service.buscarComPermissoes(sistema[0].codigoPerfil);
        return admin.permissoes || [];
      }
      return [];
    },
  };

  // ─── UI ────────────────────────────────────────────────────────────────────
  const UI = {

    // --Atualizar stat cards
    atualizarStats: () => {
      const total     = _perfis.length;
      const sistema   = _perfis.filter(p => p.sistema).length;
      const custom    = _perfis.filter(p => !p.sistema).length;
      const modulos   = [...new Set(_permissoes.map(p => p.modulo))].length;

      document.getElementById('statTotalPerfis').textContent     = total;
      document.getElementById('statPersonalizados').textContent   = custom;
      document.getElementById('statTotalPermissoes').textContent  = _permissoes.length;
      document.getElementById('contSistema').textContent          = sistema;
      document.getElementById('contModulos').textContent          = modulos;
      document.getElementById('labelTotalPerfis').textContent     = `${total} perfil${total !== 1 ? 's' : ''} cadastrado${total !== 1 ? 's' : ''}`;
    },

    // --Renderizar lista de perfis
    renderListaPerfis: (lista) => {
      const container = document.getElementById('listaPerfis');
      document.getElementById('loadingPerfis').style.display = 'none';

      if (!lista.length) {
        container.innerHTML = `
          <div class="empty-state">
            <i class="bi bi-shield-slash"></i>
            <p>Nenhum perfil encontrado</p>
          </div>`;
        return;
      }

      // --Manter apenas cards (remover empty state)
      const existingCards = container.querySelectorAll('.perfil-card');
      existingCards.forEach(c => c.remove());
      const emptyEl = container.querySelector('.empty-state');
      if (emptyEl) emptyEl.remove();

      lista.forEach(perfil => {
        const div = document.createElement('div');
        div.className = `perfil-card${perfil.sistema ? ' sistema' : ''}`;
        div.dataset.id = perfil.codigoPerfil;

        const permQtd = perfil.permissoes?.length ?? 0;
        const isSelected = _perfilSelecionado?.codigoPerfil === perfil.codigoPerfil;
        if (isSelected) div.classList.add('selected');

        div.innerHTML = `
          <div class="d-flex align-items-start justify-content-between gap-2 mb-1">
            <div class="perfil-nome">${perfil.nomePerfil}</div>
            <span class="perfil-badge-sistema">
              <i class="bi bi-cpu"></i> Sistema
            </span>
          </div>
          <div class="perfil-desc">${perfil.descricaoPerfil || '<span style="font-style:italic; opacity:.6;">Sem descrição</span>'}</div>
          <div class="perfil-meta">
            <span class="perfil-perm-count">
              <i class="bi bi-key"></i>
              ${permQtd} permiss${permQtd !== 1 ? 'ões' : 'ão'}
            </span>
            <div class="perfil-actions">
              ${!perfil.sistema ? `
                <button class="btn btn-outline-secondary btn-sm btn-editar-perfil" data-id="${perfil.codigoPerfil}" title="Editar">
                  <i class="bi bi-pencil"></i>
                </button>
                <button class="btn btn-sm btn-excluir-perfil"
                  data-id="${perfil.codigoPerfil}"
                  data-nome="${perfil.nomePerfil}"
                  title="Excluir"
                  style="border:1.5px solid var(--accent-danger); color:var(--accent-danger); background:transparent; border-radius:var(--radius-sm); padding:.25rem .5rem;">
                  <i class="bi bi-trash3"></i>
                </button>
              ` : `
                <span style="font-size:.7rem; color:var(--text-muted); display:flex; align-items:center; gap:.25rem;">
                  <i class="bi bi-lock"></i> Protegido
                </span>
              `}
            </div>
          </div>`;

        // --Selecionar perfil ao clicar no card
        div.addEventListener('click', (e) => {
          if (e.target.closest('.btn-editar-perfil') || e.target.closest('.btn-excluir-perfil')) return;
          Events._selecionarPerfil(perfil.codigoPerfil);
        });

        container.appendChild(div);
      });

      // --Delegação de eventos para editar e excluir
      container.querySelectorAll('.btn-editar-perfil').forEach(btn => {
        btn.addEventListener('click', (e) => {
          e.stopPropagation();
          Events._abrirModalEditar(parseInt(btn.dataset.id));
        });
      });

      container.querySelectorAll('.btn-excluir-perfil').forEach(btn => {
        btn.addEventListener('click', (e) => {
          e.stopPropagation();
          Events._confirmarExclusao(parseInt(btn.dataset.id), btn.dataset.nome);
        });
      });
    },

    // --Renderizar painel de permissões
    renderPainelPermissoes: (perfil) => {
      const isSistema = perfil.sistema;
      const permAtivas = new Set((perfil.permissoes || []).map(p => p.codigoPermissao));
      _permAlteradas = new Set(permAtivas); // cópia para edição

      // --Atualizar stat card de selecionado
      document.getElementById('statPerfilSelecionado').textContent = perfil.nomePerfil;
      document.getElementById('statPermSelecionado').innerHTML =
        `<i class="bi bi-key"></i> ${permAtivas.size} permissões ativas`;

      // --Atualizar header do painel
      document.getElementById('painelPerfilNome').textContent = perfil.nomePerfil;
      document.getElementById('painelPerfilDesc').textContent =
        perfil.descricaoPerfil || (isSistema ? 'Perfil do sistema — somente leitura' : 'Perfil personalizado');

      // --Mostrar botões do painel
      const btnSalvar  = document.getElementById('btnSalvarPermissoes');
      const btnToggle  = document.getElementById('btnToggleTodos');
      const painelAcoes= document.getElementById('painelAcoes');

      painelAcoes.style.display = 'flex';
      btnSalvar.style.display   = isSistema ? 'none' : 'inline-flex';
      btnToggle.style.display   = 'inline-flex';

      // --Agrupar permissões por módulo
      const grupos = {};
      _permissoes.forEach(p => {
        if (!grupos[p.modulo]) grupos[p.modulo] = [];
        grupos[p.modulo].push(p);
      });

      const body = document.getElementById('painelBody');
      body.innerHTML = '<div class="perm-panel-body" id="matrizPermissoes"></div>';
      const matriz = document.getElementById('matrizPermissoes');

      Object.entries(grupos).sort(([a],[b]) => a.localeCompare(b)).forEach(([modulo, permissoes]) => {
        const icone = _moduloIcone[modulo] || 'bi-grid';
        const ativasNoModulo = permissoes.filter(p => _permAlteradas.has(p.codigoPermissao)).length;

        const grupo = document.createElement('div');
        grupo.className = 'perm-modulo-group';
        grupo.dataset.modulo = modulo;

        grupo.innerHTML = `
          <div class="perm-modulo-header">
            <i class="bi ${icone}"></i>
            <span class="perm-modulo-nome">${modulo}</span>
            <span class="perm-tag" style="font-size:.63rem;">
              ${ativasNoModulo}/${permissoes.length}
            </span>
            ${!isSistema ? `
            <span class="perm-modulo-toggle" data-modulo="${modulo}" title="Marcar/desmarcar tudo">
              <i class="bi bi-check2-all"></i>
            </span>` : ''}
          </div>
          <div class="perm-acoes-grid" id="grid-${modulo}"></div>`;

        const grid = grupo.querySelector(`#grid-${modulo}`);

        permissoes.forEach(perm => {
          const ativo   = _permAlteradas.has(perm.codigoPermissao);
          const iconeAcao = _acaoIcone[perm.acao] || 'bi-circle';
          const label   = _acaoLabel[perm.acao] || perm.acao;

          const toggle = document.createElement('div');
          toggle.className  = `perm-toggle${ativo ? ' ativo' : ''}${isSistema ? ' disabled' : ''}`;
          toggle.dataset.id = perm.codigoPermissao;
          toggle.title      = perm.descricaoPermissao || `${perm.modulo}:${perm.acao}`;

          toggle.innerHTML = `
            <i class="bi ${iconeAcao} perm-toggle-icon"></i>
            <span class="perm-toggle-label">${label}</span>`;

          if (!isSistema) {
            toggle.addEventListener('click', () => UI._togglePermissao(toggle, perm.codigoPermissao, modulo, grupo));
          }

          grid.appendChild(toggle);
        });

        // --Toggle all do módulo
        if (!isSistema) {
          grupo.querySelector('.perm-modulo-toggle')?.addEventListener('click', (e) => {
            e.stopPropagation();
            UI._toggleModulo(modulo, permissoes, grupo);
          });
        }

        matriz.appendChild(grupo);
      });
    },

    // --Toggle individual de permissão
    _togglePermissao: (el, id, modulo, grupoEl) => {
      if (_permAlteradas.has(id)) {
        _permAlteradas.delete(id);
        el.classList.remove('ativo');
      } else {
        _permAlteradas.add(id);
        el.classList.add('ativo');
      }
      UI._atualizarTagModulo(modulo, grupoEl);
    },

    // --Toggle todas as permissões de um módulo
    _toggleModulo: (modulo, permissoes, grupoEl) => {
      const todasAtivas = permissoes.every(p => _permAlteradas.has(p.codigoPermissao));
      permissoes.forEach(p => {
        if (todasAtivas) {
          _permAlteradas.delete(p.codigoPermissao);
        } else {
          _permAlteradas.add(p.codigoPermissao);
        }
      });
      // --Re-render dos toggles do grupo
      grupoEl.querySelectorAll('.perm-toggle').forEach(toggle => {
        const pid = parseInt(toggle.dataset.id);
        toggle.classList.toggle('ativo', _permAlteradas.has(pid));
      });
      UI._atualizarTagModulo(modulo, grupoEl);
    },

    // --Atualizar contador do módulo
    _atualizarTagModulo: (modulo, grupoEl) => {
      const permissoes = _permissoes.filter(p => p.modulo === modulo);
      const ativas = permissoes.filter(p => _permAlteradas.has(p.codigoPermissao)).length;
      const tag = grupoEl.querySelector('.perm-tag');
      if (tag) tag.textContent = `${ativas}/${permissoes.length}`;
    },

    // --Resetar painel para estado vazio
    resetarPainel: () => {
      document.getElementById('painelPerfilNome').textContent = 'Permissões do Perfil';
      document.getElementById('painelPerfilDesc').textContent = 'Selecione um perfil para visualizar e editar as permissões';
      document.getElementById('painelBody').innerHTML = `
        <div class="perm-panel-empty">
          <i class="bi bi-shield-slash"></i>
          Selecione um perfil ao lado para visualizar e gerenciar suas permissões
        </div>`;
      document.getElementById('btnSalvarPermissoes').style.display = 'none';
      document.getElementById('btnToggleTodos').style.display      = 'none';
      document.getElementById('painelAcoes').style.display         = 'none';
      document.getElementById('statPerfilSelecionado').textContent  = 'Nenhum';
      document.getElementById('statPermSelecionado').innerHTML =
        '<i class="bi bi-dash"></i> selecione um perfil';
    },

    // --Preencher modal para edição
    preencherModal: (perfil) => {
      document.getElementById('perfilId').value            = perfil.codigoPerfil;
      document.getElementById('nomePerfil').value          = perfil.nomePerfil;
      document.getElementById('descricaoPerfil').value     = perfil.descricaoPerfil || '';
      document.getElementById('modalPerfilTitulo').innerHTML =
        `<i class="bi bi-shield-check me-2" style="color:var(--mint-dark);"></i> Editar Perfil`;
    },

    // --Limpar modal
    limparModal: () => {
      document.getElementById('perfilId').value         = '';
      document.getElementById('nomePerfil').value       = '';
      document.getElementById('descricaoPerfil').value  = '';
      document.getElementById('modalPerfilTitulo').innerHTML =
        `<i class="bi bi-shield-plus me-2" style="color:var(--mint-dark);"></i> Novo Perfil`;
      ZyloUI.clearAlert('alertModal');
    },

    // --Marcar card selecionado
    marcarSelecionado: (id) => {
      document.querySelectorAll('.perfil-card').forEach(c => {
        c.classList.toggle('selected', parseInt(c.dataset.id) === id);
      });
    },

    // --Filtrar lista por texto e tipo
    filtrarLista: () => {
      const busca  = document.getElementById('campoBusca').value.toLowerCase().trim();
      const filtro = document.getElementById('filtroPerfil').value;

      let lista = [..._perfis];
      if (filtro === 'sistema')       lista = lista.filter(p => p.sistema);
      if (filtro === 'personalizado') lista = lista.filter(p => !p.sistema);
      if (busca) lista = lista.filter(p => p.nomePerfil.toLowerCase().includes(busca));

      UI.renderListaPerfis(lista);
    },
  };

  // ─── Events ────────────────────────────────────────────────────────────────
  const Events = {

    // --Inicializar página
    init: async () => {
      await Events._carregarTudo();
      Events._bindFiltros();
      Events._bindModal();
      Events._bindSalvarPermissoes();
      Events._bindToggleTodos();
    },

    // --Carregar perfis e permissões
    _carregarTudo: async () => {
      try {
        // --Carregar perfis e todas as permissoes em paralelo
        const [perfis, todasPermissoes] = await Promise.all([
          Service.listarTodos(),
          Service.listarTodasPermissoes(),
        ]);

        _perfis     = perfis || [];
        _permissoes = todasPermissoes || [];

        UI.atualizarStats();
        UI.renderListaPerfis(_perfis);
      } catch (err) {
        console.error('Erro ao carregar dados:', err);
        document.getElementById('loadingPerfis').innerHTML = `
          <div class="empty-state">
            <i class="bi bi-exclamation-circle"></i>
            <p>Erro ao carregar perfis</p>
          </div>`;
      }
    },

    // --Selecionar perfil e exibir permissões
    _selecionarPerfil: async (id) => {
      ZyloUI.clearAlert('alertPainel');
      UI.marcarSelecionado(id);

      // --Loading no painel
      document.getElementById('painelBody').innerHTML = `
        <div class="perm-panel-empty">
          <span class="spinner-border spinner-border-sm me-2"></span> Carregando permissões...
        </div>`;

      try {
        const perfil = await Service.buscarComPermissoes(id);
        _perfilSelecionado = perfil;
        UI.renderPainelPermissoes(perfil);
      } catch (err) {
        ZyloUI.showAlert('alertPainel', 'Erro ao carregar permissões do perfil.', 'error');
      }
    },

    // --Filtros de lista
    _bindFiltros: () => {
      document.getElementById('campoBusca').addEventListener('input', UI.filtrarLista);
      document.getElementById('filtroPerfil').addEventListener('change', UI.filtrarLista);
    },

    // --Modal de criar/editar
    _bindModal: () => {
      const modalEl = document.getElementById('modalPerfil');
      const modal   = new bootstrap.Modal(modalEl);

      // --Abrir modal para novo perfil
      document.getElementById('btnNovoPerfil').addEventListener('click', () => {
        UI.limparModal();
        modal.show();
      });

      // --Salvar perfil
      document.getElementById('btnSalvarPerfil').addEventListener('click', async () => {
        await Events._salvarPerfil(modal);
      });

      // --Limpar alert ao fechar modal
      modalEl.addEventListener('hidden.bs.modal', () => ZyloUI.clearAlert('alertModal'));
    },

    // --Abrir modal em modo edição
    _abrirModalEditar: (id) => {
      const perfil = _perfis.find(p => p.codigoPerfil === id);
      if (!perfil) return;
      UI.preencherModal(perfil);
      ZyloUI.clearAlert('alertModal');
      new bootstrap.Modal(document.getElementById('modalPerfil')).show();
    },

    // --Salvar (criar ou editar)
    _salvarPerfil: async (modal) => {
      const id        = document.getElementById('perfilId').value;
      const nome      = document.getElementById('nomePerfil').value.trim();
      const descricao = document.getElementById('descricaoPerfil').value.trim();
      const btn       = document.getElementById('btnSalvarPerfil');

      if (!nome || nome.length < 3) {
        ZyloUI.showAlert('alertModal', 'Nome deve ter no mínimo 3 caracteres.', 'error');
        return;
      }

      ZyloUI.btnLoading(btn, true, 'Salvando...');

      try {
        if (id) {
          // --Editar perfil existente
          const updated = await Service.editar(id, { nomePerfil: nome, descricaoPerfil: descricao });
          _perfis = _perfis.map(p => p.codigoPerfil === parseInt(id) ? { ...p, ...updated } : p);
          // --Atualizar painel se era o selecionado
          if (_perfilSelecionado?.codigoPerfil === parseInt(id)) {
            _perfilSelecionado = { ..._perfilSelecionado, ...updated };
            document.getElementById('painelPerfilNome').textContent = nome;
            document.getElementById('painelPerfilDesc').textContent = descricao || 'Perfil personalizado';
          }
        } else {
          // --Criar novo perfil
          const novo = await Service.criar({ nomePerfil: nome, descricaoPerfil: descricao, permissoesIds: [] });
          _perfis.push(novo);
        }

        UI.atualizarStats();
        UI.renderListaPerfis(_perfis);
        modal.hide();
      } catch (err) {
        ZyloUI.showAlert('alertModal', err.message || 'Erro ao salvar perfil.', 'error');
      } finally {
        ZyloUI.btnLoading(btn, false);
      }
    },

    // --Confirmar exclusão
    _confirmarExclusao: (id, nome) => {
      document.getElementById('nomePerfilExcluir').textContent = nome;
      const modal = new bootstrap.Modal(document.getElementById('modalExcluir'));
      modal.show();

      // --Confirmar botão
      const btnConfirmar = document.getElementById('btnConfirmarExcluir');
      const novoBtn = btnConfirmar.cloneNode(true); // remove listeners anteriores
      btnConfirmar.replaceWith(novoBtn);

      novoBtn.addEventListener('click', async () => {
        ZyloUI.btnLoading(novoBtn, true, 'Excluindo...');
        try {
          await Service.excluir(id);
          _perfis = _perfis.filter(p => p.codigoPerfil !== id);

          // --Resetar painel se era o selecionado
          if (_perfilSelecionado?.codigoPerfil === id) {
            _perfilSelecionado = null;
            UI.resetarPainel();
          }

          UI.atualizarStats();
          UI.renderListaPerfis(_perfis);
          modal.hide();
        } catch (err) {
          ZyloUI.showAlert('alertPainel', err.message || 'Erro ao excluir perfil.', 'error');
          modal.hide();
        } finally {
          ZyloUI.btnLoading(novoBtn, false);
        }
      });
    },

    // --Salvar permissões (sincronizar)
    _bindSalvarPermissoes: () => {
      document.getElementById('btnSalvarPermissoes').addEventListener('click', async () => {
        if (!_perfilSelecionado) return;
        ZyloUI.clearAlert('alertPainel');

        const btn = document.getElementById('btnSalvarPermissoes');
        ZyloUI.btnLoading(btn, true, 'Salvando...');

        try {
          await Service.sincronizarPermissoes(_perfilSelecionado.codigoPerfil, _permAlteradas);

          // --Atualizar o perfil local com novas permissões
          const permissoes = _permissoes.filter(p => _permAlteradas.has(p.codigoPermissao));
          _perfilSelecionado.permissoes = permissoes;
          _perfis = _perfis.map(p =>
            p.codigoPerfil === _perfilSelecionado.codigoPerfil
              ? { ...p, permissoes }
              : p
          );

          UI.atualizarStats();
          UI.filtrarLista();

          ZyloUI.showAlert('alertPainel', 'Permissões atualizadas com sucesso.', 'success');

          // --Atualizar stat card
          document.getElementById('statPermSelecionado').innerHTML =
            `<i class="bi bi-key"></i> ${_permAlteradas.size} permissões ativas`;
        } catch (err) {
          ZyloUI.showAlert('alertPainel', err.message || 'Erro ao salvar permissões.', 'error');
        } finally {
          ZyloUI.btnLoading(btn, false);
        }
      });
    },

    // --Toggle expandir/recolher todos os módulos
    _bindToggleTodos: () => {
      let expandido = true;
      document.getElementById('btnToggleTodos').addEventListener('click', () => {
        expandido = !expandido;
        document.querySelectorAll('.perm-acoes-grid').forEach(grid => {
          grid.style.display = expandido ? 'grid' : 'none';
        });
        const icon = document.querySelector('#btnToggleTodos i');
        if (icon) icon.className = `bi ${expandido ? 'bi-arrows-collapse' : 'bi-arrows-expand'}`;
      });
    },
  };

  return { Service, UI, Events };
})();