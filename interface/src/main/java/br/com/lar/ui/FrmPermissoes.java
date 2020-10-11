package br.com.lar.ui;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.jdesktop.swingx.JXTreeTable;

import br.com.lar.repository.model.PermissaoPrograma;
import br.com.lar.repository.model.Programa;
import br.com.lar.service.login.LoginService;
import br.com.lar.service.permissoes.PermissoesPesquisaService;
import br.com.lar.service.permissoes.PermissoesProgramaService;
import br.com.lar.service.programa.ProgramaService;
import br.com.lar.startup.enumeradores.PesquisaEnum;
import br.com.lar.tablemodels.MenusTreeTableModel;
import br.com.lar.tablemodels.PesquisaTreeTableModel;
import br.com.sysdesc.components.AbstractInternalFrame;
import br.com.sysdesc.components.listeners.ChangeListener;
import br.com.sysdesc.components.renders.CheckBoxRenderer;
import br.com.sysdesc.pesquisa.repository.model.Perfil;
import br.com.sysdesc.pesquisa.repository.model.PermissaoPesquisa;
import br.com.sysdesc.pesquisa.repository.model.Pesquisa;
import br.com.sysdesc.pesquisa.repository.model.Usuario;
import br.com.sysdesc.pesquisa.service.impl.PesquisaBasicaService;
import br.com.sysdesc.pesquisa.service.perfil.PerfilService;
import br.com.sysdesc.pesquisa.ui.components.CampoPesquisa;
import br.com.sysdesc.util.classes.ImageUtil;
import br.com.sysdesc.util.classes.ListUtil;
import br.com.sysdesc.util.constants.MensagemConstants;
import br.com.sysdesc.util.resources.Resources;
import br.com.sysdesc.util.vo.PermissaoPesquisaVO;
import br.com.sysdesc.util.vo.PermissaoProgramaVO;

public class FrmPermissoes extends AbstractInternalFrame {

    private CampoPesquisa<Usuario> campoPesquisaUsuario;
    private CampoPesquisa<Perfil> campoPesquisaPerfil;

    private JXTreeTable treeTableMenus;
    private MenusTreeTableModel permissaoTreeTableModel;
    private JXTreeTable treeTablePesquisa;
    private PesquisaTreeTableModel pesquisaTreeTableModel;
    private LoginService loginService = new LoginService();
    private PerfilService perfilService = new PerfilService();
    private ProgramaService programaService = new ProgramaService();
    private PesquisaBasicaService pesquisaService = new PesquisaBasicaService();
    private PermissoesProgramaService permissoesProgramaService = new PermissoesProgramaService();
    private PermissoesPesquisaService permissoesPesquisaService = new PermissoesPesquisaService();

    private Map<Long, PermissaoProgramaVO> mapMenu = new HashMap<>();
    private PermissaoPesquisaVO permissaoPesquisaVO;

    private List<PermissaoPrograma> permissoesPrograma;
    private List<PermissaoPesquisa> permissoesPesquisa;

    private static final long serialVersionUID = 1L;

    public FrmPermissoes(Long permissaoPrograma, Long codigoUsuario) {
        super(permissaoPrograma, codigoUsuario);

        initComponents();
    }

    private void initComponents() {

        setSize(650, 580);
        setClosable(Boolean.TRUE);
        setTitle("Cadastro de Permissoes");

        PermissaoProgramaVO permissaoProgramaVO = new PermissaoProgramaVO();
        permissaoProgramaVO.setDescricao("TODOS OS PROGRAMAS");
        permissaoProgramaVO.setIdPrograma(0L);
        mapMenu.put(0L, permissaoProgramaVO);

        permissaoPesquisaVO = new PermissaoPesquisaVO();
        permissaoPesquisaVO.setDescricao("TODAS AS PESQUISAS");
        permissaoPesquisaVO.setCodigoPesquisa(0L);
        permissaoPesquisaVO.setIdPesquisa(0L);

        this.montarPermissoesMenu(permissaoProgramaVO, programaService.buscarRootProgramas());
        this.montarPermissoesPesquisa(pesquisaService.buscarRootPesquisas());

        JLabel lblUsurio = new JLabel("Usuário:");
        JLabel lblPerfil = new JLabel("Perfil:");
        JPanel panel1 = new JPanel();

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        JPanel panel = new JPanel();
        JPanel panel2 = new JPanel();

        JButton btnSalvar = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");

        permissaoTreeTableModel = new MenusTreeTableModel(permissaoProgramaVO);
        pesquisaTreeTableModel = new PesquisaTreeTableModel(permissaoPesquisaVO);

        treeTableMenus = new JXTreeTable(permissaoTreeTableModel);
        treeTablePesquisa = new JXTreeTable(pesquisaTreeTableModel);

        campoPesquisaUsuario = new CampoPesquisa<Usuario>(loginService, PesquisaEnum.PES_USUARIOS.getCodigoPesquisa(), getCodigoUsuario()) {

            private static final long serialVersionUID = 1L;

            @Override
            public String formatarValorCampo(Usuario objeto) {
                return String.format("%s - %s", objeto.getIdUsuario(), objeto.getNomeUsuario());
            }
        };

        campoPesquisaPerfil = new CampoPesquisa<Perfil>(perfilService, PesquisaEnum.PES_PERFIL.getCodigoPesquisa(), getCodigoUsuario()) {

            private static final long serialVersionUID = 1L;

            @Override
            public String formatarValorCampo(Perfil objeto) {
                return String.format("%s - %s", objeto.getIdPerfil(), objeto.getDescricao());
            }
        };

        campoPesquisaPerfil.addChangeListener(new ChangeListener<Perfil>() {

            @Override
            public void valueChanged(Perfil value) {

                campoPesquisaUsuario.limpar();

                zerarPermissoes();

                if (value != null) {

                    permissoesPrograma = permissoesProgramaService.buscarPorPerfil(value.getIdPerfil());
                    permissoesPesquisa = permissoesPesquisaService.buscarPorPerfil(value.getIdPerfil());

                    atualizarPermissoesPrograma();

                    atualizarPermissoesPesquisa();

                }
            }
        });

        campoPesquisaUsuario.addChangeListener(new ChangeListener<Usuario>() {

            @Override
            public void valueChanged(Usuario value) {

                campoPesquisaPerfil.limpar();

                zerarPermissoes();

                if (value != null) {

                    permissoesPrograma = permissoesProgramaService.buscarPorUsuario(value.getIdUsuario());
                    permissoesPesquisa = permissoesPesquisaService.buscarPorUsuario(value.getIdUsuario());

                    atualizarPermissoesPrograma();

                    atualizarPermissoesPesquisa();
                }
            }

        });

        campoPesquisaUsuario.setBounds(10, 25, 615, 25);
        campoPesquisaPerfil.setBounds(10, 70, 615, 25);
        lblUsurio.setBounds(10, 10, 60, 14);
        lblPerfil.setBounds(10, 55, 46, 14);
        panel1.setBounds(7, 515, 620, 33);
        tabbedPane.setBounds(10, 106, 615, 405);

        btnCancelar.addActionListener((e) -> dispose());
        btnSalvar.addActionListener((e) -> salvarPermissoesPrograma());

        btnCancelar.setIcon(ImageUtil.resize("cancel.png", 16, 16));
        btnSalvar.setIcon(ImageUtil.resize("ok.png", 16, 16));

        getContentPane().setLayout(null);

        getContentPane().add(lblUsurio);
        getContentPane().add(campoPesquisaUsuario);
        getContentPane().add(lblPerfil);
        getContentPane().add(campoPesquisaPerfil);
        getContentPane().add(panel1);
        getContentPane().add(tabbedPane);
        panel1.add(btnSalvar);
        panel1.add(btnCancelar);

        tabbedPane.addTab("Programas", null, panel, null);
        tabbedPane.addTab("Pesquisas", null, panel2, null);

        panel.setLayout(new BorderLayout(0, 0));
        panel2.setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPaneMenu = new JScrollPane();
        JScrollPane scrollPanePesquisa = new JScrollPane();

        panel.add(scrollPaneMenu);
        panel2.add(scrollPanePesquisa);

        treeTableMenus.setRootVisible(true);
        treeTablePesquisa.setRootVisible(true);

        treeTableMenus.getColumnModel().getColumn(0).setPreferredWidth(550);
        treeTableMenus.getColumnModel().getColumn(1).setPreferredWidth(80);
        treeTableMenus.getColumnModel().getColumn(2).setPreferredWidth(80);
        treeTableMenus.getColumnModel().getColumn(3).setPreferredWidth(80);

        treeTablePesquisa.getColumnModel().getColumn(0).setPreferredWidth(550);
        treeTablePesquisa.getColumnModel().getColumn(1).setPreferredWidth(80);

        new CheckBoxRenderer(treeTableMenus, 1);
        new CheckBoxRenderer(treeTableMenus, 2);
        new CheckBoxRenderer(treeTableMenus, 3);

        new CheckBoxRenderer(treeTablePesquisa, 1);

        scrollPaneMenu.setViewportView(treeTableMenus);
        scrollPanePesquisa.setViewportView(treeTablePesquisa);

    }

    private void montarPermissoesPesquisa(List<Pesquisa> pesquisas) {

        Map<Long, List<Pesquisa>> mapaPesquisas = pesquisas.stream().collect(Collectors.groupingBy(Pesquisa::getCodigoPesquisa));

        for (Entry<Long, List<Pesquisa>> pesquisa : mapaPesquisas.entrySet()) {

            PermissaoPesquisaVO permissaoPesquisaVO = new PermissaoPesquisaVO();
            permissaoPesquisaVO.setIdPesquisa(0L);
            permissaoPesquisaVO.setCodigoPesquisa(pesquisa.getKey());
            permissaoPesquisaVO.setDescricao(PesquisaEnum.forValue(pesquisa.getKey()).getDescricaoPesquisa());

            pesquisa.getValue().forEach(item -> {

                PermissaoPesquisaVO pesquisaItem = new PermissaoPesquisaVO();
                pesquisaItem.setCodigoPesquisa(pesquisa.getKey());
                pesquisaItem.setIdPesquisa(item.getIdPesquisa());
                pesquisaItem.setDescricao(item.getDescricao());

                permissaoPesquisaVO.addChild(pesquisaItem);
            });

            this.permissaoPesquisaVO.addChild(permissaoPesquisaVO);
        }

    }

    private void salvarPermissoesPrograma() {

        if (salvarPermissoes()) {

            JOptionPane.showMessageDialog(null, Resources.translate(MensagemConstants.MENSAGENS_PERMISSOES_SALVAS));

            dispose();
        }
    }

    private boolean salvarPermissoes() {

        if (campoPesquisaPerfil.getObjetoPesquisado() != null) {

            return salvarPermissoesPerfil();

        }

        if (campoPesquisaUsuario.getObjetoPesquisado() != null) {

            return salvarPermissoesUsuario();
        }

        JOptionPane.showMessageDialog(this, "Nenhum usuário ou perfil selecionado");

        return false;
    }

    private Boolean salvarPermissoesUsuario() {

        List<PermissaoPrograma> permissoes = new ArrayList<>();

        Usuario usuario = campoPesquisaUsuario.getObjetoPesquisado();

        mapMenu.values().stream().filter(x -> !x.getIdPrograma().equals(0L)).forEach(programa -> {

            Optional<PermissaoPrograma> optional = permissoesPrograma.stream().filter(x -> x.getCodigoPrograma().equals(programa.getIdPrograma()))
                    .findFirst();

            PermissaoPrograma permissao = getPermissaoPrograma(optional, programa.getIdPrograma());

            permissao.setCodigoUsuario(usuario.getIdUsuario());
            permissao.setFlagCadastro(programa.getFlagCadastro());
            permissao.setFlagExclusao(programa.getFlagExclusao());
            permissao.setFlagLeitura(programa.getFlagLeitura());

            permissoes.add(permissao);
        });

        permissoesProgramaService.salvarTodos(permissoes);

        List<PermissaoPesquisa> permissoesPesquisaSalvar = new ArrayList<>();
        List<PermissaoPesquisaVO> semPermissao = new ArrayList<>();

        permissaoPesquisaVO.getChilds().forEach(classe -> classe.getChilds().stream().forEach(permissao -> {

            if (!permissao.getFlagLeitura()) {

                semPermissao.add(permissao);
            }

            Optional<PermissaoPesquisa> optional = this.permissoesPesquisa.stream()
                    .filter(x -> x.getCodigoPesquisa().equals(permissao.getIdPesquisa())).findFirst();

            if (!optional.isPresent()) {

                PermissaoPesquisa permissaoPesquisaClasse = new PermissaoPesquisa();

                permissaoPesquisaClasse.setCodigoUsuario(usuario.getIdUsuario());
                permissaoPesquisaClasse.setCodigoPesquisa(permissao.getIdPesquisa());

                permissoesPesquisaSalvar.add(permissaoPesquisaClasse);
            }
        }));

        List<Long> idsSemPermissao = semPermissao.stream().mapToLong(p -> p.getIdPesquisa()).boxed().collect(Collectors.toList());

        List<PermissaoPesquisa> permissoesExcluir = permissoesPesquisa.stream().filter(x -> idsSemPermissao.contains(x.getCodigoPesquisa()))
                .collect(Collectors.toList());

        if (!ListUtil.isNullOrEmpty(permissoesPesquisaSalvar)) {

            permissoesPesquisaSalvar.forEach(objeto -> {
                permissoesPesquisaService.salvar(objeto);
            });

        }

        if (!ListUtil.isNullOrEmpty(permissoesExcluir)) {

            permissoesPesquisaService.excluir(permissoesExcluir);
        }

        return Boolean.TRUE;
    }

    private PermissaoPrograma getPermissaoPrograma(Optional<PermissaoPrograma> optional, Long programa) {

        if (optional.isPresent()) {
            return optional.get();
        }

        PermissaoPrograma permissaoPrograma = new PermissaoPrograma();
        permissaoPrograma.setCodigoPrograma(programa);

        return permissaoPrograma;
    }

    private Boolean salvarPermissoesPerfil() {
        List<PermissaoPrograma> permissoes = new ArrayList<>();

        Perfil perfil = campoPesquisaPerfil.getObjetoPesquisado();

        List<PermissaoPrograma> permissoesPerfil = permissoesProgramaService.buscarPorPerfil(perfil.getIdPerfil());

        List<PermissaoPesquisa> permissoesPesquisa = permissoesPesquisaService.buscarPorPerfil(perfil.getIdPerfil());

        mapMenu.values().stream().filter(x -> !x.getIdPrograma().equals(0L)).forEach(programa -> {

            Optional<PermissaoPrograma> optional = permissoesPerfil.stream().filter(x -> x.getCodigoPrograma().equals(programa.getIdPrograma()))
                    .findFirst();

            PermissaoPrograma permissao = getPermissaoPrograma(optional, programa.getIdPrograma());

            permissao.setCodigoPerfil(perfil.getIdPerfil());
            permissao.setFlagCadastro(programa.getFlagCadastro());
            permissao.setFlagExclusao(programa.getFlagExclusao());
            permissao.setFlagLeitura(programa.getFlagLeitura());

            permissoes.add(permissao);
        });

        permissoesProgramaService.salvarTodos(permissoes);

        List<PermissaoPesquisa> permissoesPesquisaSalvar = new ArrayList<>();
        List<Long> idsComPermissao = new ArrayList<>();

        permissaoPesquisaVO.getChilds().forEach(classe -> classe.getChilds().stream().filter(x -> x.getFlagLeitura()).forEach(permissao -> {

            if (permissao.getFlagLeitura()) {

                idsComPermissao.add(permissao.getCodigoPesquisa());
            }

            Optional<PermissaoPesquisa> optional = permissoesPesquisa.stream().filter(x -> x.getCodigoPesquisa().equals(permissao.getIdPesquisa()))
                    .findFirst();

            if (!optional.isPresent()) {

                PermissaoPesquisa permissaoPesquisaClasse = new PermissaoPesquisa();

                permissaoPesquisaClasse.setCodigoPerfil(perfil.getIdPerfil());
                permissaoPesquisaClasse.setCodigoPesquisa(permissao.getIdPesquisa());

                permissoesPesquisaSalvar.add(permissaoPesquisaClasse);
            }
        }));

        List<PermissaoPesquisa> permissoesExcluir = permissoesPesquisa.stream().filter(x -> !idsComPermissao.contains(x.getCodigoPesquisa()))
                .collect(Collectors.toList());

        if (!ListUtil.isNullOrEmpty(permissoesPesquisaSalvar)) {

            permissoesPesquisaSalvar.forEach(objeto -> {
                permissoesPesquisaService.salvar(objeto);
            });

        }

        if (!ListUtil.isNullOrEmpty(permissoesExcluir)) {

            permissoesPesquisaService.excluir(permissoesExcluir);
        }

        return Boolean.TRUE;
    }

    private void atualizarPermissoesPesquisa() {

        permissaoPesquisaVO.getChilds().forEach(classe -> {

            classe.getChilds().forEach(pesquisa -> {

                boolean permissaoEncontrada = permissoesPesquisa.stream().anyMatch(x -> pesquisa.getIdPesquisa().equals(x.getCodigoPesquisa()));

                pesquisa.setFlagLeitura(permissaoEncontrada);

            });

            boolean permissaoClasse = classe.getChilds().stream().anyMatch(x -> x.getFlagLeitura());

            classe.setFlagLeitura(permissaoClasse);
        });

        boolean permissaoGeral = permissaoPesquisaVO.getChilds().stream().anyMatch(x -> x.getFlagLeitura());
        permissaoPesquisaVO.setFlagLeitura(permissaoGeral);
    }

    protected void atualizarPermissoesPrograma() {

        permissoesPrograma.forEach(permissao -> {

            if (mapMenu.containsKey(permissao.getCodigoPrograma())) {

                PermissaoProgramaVO permissaoProgramaVO = mapMenu.get(permissao.getCodigoPrograma());
                permissaoProgramaVO.setFlagCadastro(permissao.getFlagCadastro());
                permissaoProgramaVO.setFlagExclusao(permissao.getFlagExclusao());
                permissaoProgramaVO.setFlagLeitura(permissao.getFlagLeitura());
            }
        });

        PermissaoProgramaVO root = mapMenu.get(0L);

        root.setFlagCadastro(mapMenu.values().stream().filter(x -> !x.getIdPrograma().equals(0L)).allMatch(x -> x.getFlagCadastro()));
        root.setFlagExclusao(mapMenu.values().stream().filter(x -> !x.getIdPrograma().equals(0L)).allMatch(x -> x.getFlagExclusao()));
        root.setFlagLeitura(mapMenu.values().stream().filter(x -> !x.getIdPrograma().equals(0L)).allMatch(x -> x.getFlagLeitura()));

        treeTableMenus.updateUI();
    }

    private void zerarPermissoes() {

        mapMenu.values().forEach(permissao -> {
            permissao.setFlagLeitura(Boolean.FALSE);
            permissao.setFlagCadastro(Boolean.FALSE);
            permissao.setFlagExclusao(Boolean.FALSE);
        });

        permissaoPesquisaVO.setFlagLeitura(Boolean.FALSE);

        permissaoPesquisaVO.getChilds().forEach(pesquisa -> {
            pesquisa.setFlagLeitura(Boolean.FALSE);
            pesquisa.getChilds().forEach(classe -> classe.setFlagLeitura(Boolean.FALSE));
        });
    }

    private void montarPermissoesMenu(PermissaoProgramaVO root, List<Programa> rootProgramas) {

        for (Programa programa : rootProgramas) {

            PermissaoProgramaVO permissaoProgramaVO = new PermissaoProgramaVO();
            permissaoProgramaVO.setIdPrograma(programa.getIdPrograma());
            permissaoProgramaVO.setDescricao(Resources.translate(programa.getDescricao()));

            List<Programa> submenus = programaService.buscarSubMenus(programa.getIdPrograma());

            if (!submenus.isEmpty()) {
                montarPermissoesMenu(permissaoProgramaVO, submenus);
            }

            mapMenu.put(permissaoProgramaVO.getIdPrograma(), permissaoProgramaVO);

            root.addChild(permissaoProgramaVO);
        }

    }
}
