package br.com.lar.ui;

import static br.com.sysdesc.util.resources.Resources.FRMAPPLICATION_LB_USUARIO;
import static br.com.sysdesc.util.resources.Resources.translate;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.lang.reflect.Constructor;
import java.util.Comparator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.border.EtchedBorder;

import br.com.lar.repository.dao.CaixaDAO;
import br.com.lar.repository.model.PermissaoPrograma;
import br.com.lar.repository.model.Programa;
import br.com.lar.repository.projection.CaixaAbertoProjection;
import br.com.lar.service.main.MainService;
import br.com.lar.startup.enumeradores.ProgramasEnum;
import br.com.lar.thread.AtualizacaoThread;
import br.com.lar.thread.TimerThread;
import br.com.sysdesc.components.AbstractInternalFrame;
import br.com.sysdesc.pesquisa.repository.model.Usuario;
import br.com.sysdesc.util.classes.ImageUtil;
import br.com.sysdesc.util.classes.ListUtil;
import br.com.sysdesc.util.classes.StringUtil;
import br.com.sysdesc.util.exception.SysDescException;
import br.com.sysdesc.util.resources.Resources;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;

@Slf4j
public class FrmApplication extends JFrame {

	private static final String SYS_DESC = "SysTrans - Forcelini Comércio e Tranportes Ltda ME";
	private EtchedBorder border = new EtchedBorder(EtchedBorder.LOWERED);
	private static final long serialVersionUID = 1L;

	private static Usuario usuario;
	private static FrmApplication frmApplication;
	private JLabel lbUsuario;
	private JLabel lbCaixa;
	private JMenuBar menuBar;
	private JDesktopPane desktopPane;
	private JToolBar toolBar;

	public FrmApplication() {

		initComponents();
	}

	private void initComponents() {

		JPanel contentPane = new JPanel();
		menuBar = new JMenuBar();
		desktopPane = new JDesktopPane();
		toolBar = new JToolBar();
		JPanel panel = new JPanel();

		contentPane.setLayout(new BorderLayout(0, 0));

		contentPane.add(panel, BorderLayout.SOUTH);
		contentPane.add(desktopPane, BorderLayout.CENTER);
		contentPane.add(toolBar, BorderLayout.NORTH);
		panel.setLayout(new MigLayout("", "[][grow][][]", "[20px]"));

		JPanel panel_1 = new JPanel();
		JPanel panel_3 = new JPanel();
		JPanel panel_2 = new JPanel();
		JPanel panel_4 = new JPanel();

		lbUsuario = new JLabel();
		lbCaixa = new JLabel("");
		JLabel lbHorario = new JLabel();

		configurarBorda(panel_1);
		configurarBorda(panel_2);
		configurarBorda(panel_3);
		configurarBorda(panel_4);

		panel.add(panel_1, "flowx,cell 0 0,grow");
		panel.add(panel_2, "cell 2 0,alignx center,growy");
		panel.add(panel_4, "cell 3 0,alignx right,growy");
		panel.add(panel_3, "cell 1 0,grow");

		panel_3.add(lbCaixa);
		panel_1.add(lbUsuario);
		panel_4.add(lbHorario);

		TimerThread timerThread = new TimerThread(lbHorario);
		AtualizacaoThread atualizacaoThread = new AtualizacaoThread(panel_2);

		timerThread.start();
		atualizacaoThread.start();

		addWindowListener(new java.awt.event.WindowAdapter() {

			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {

				int option = JOptionPane.showConfirmDialog(null, "Deseja realmente fechar a aplicação?",
						"Fechar Aplicação", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

				if (option == JOptionPane.YES_OPTION) {

					System.exit(0);
				}
			}
		});

		setJMenuBar(menuBar);
		setTitle(SYS_DESC);
		setSize(1024, 600);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setExtendedState(MAXIMIZED_BOTH);
		setLocationRelativeTo(null);
		setContentPane(contentPane);
	}

	private void configurarBorda(JPanel panel) {

		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setVgap(0);
		panel.setBorder(border);
	}

	private void getSingleInstance(Class<? extends AbstractInternalFrame> frame, PermissaoPrograma permissaoPrograma) {

		try {

			Constructor<? extends AbstractInternalFrame> constructor = frame.getConstructor(Long.class, Long.class);

			AbstractInternalFrame internalFrame = constructor.newInstance(permissaoPrograma.getCodigoPrograma(),
					usuario.getIdUsuario());

			this.posicionarFrame(internalFrame, permissaoPrograma.getPrograma().getIcone());

		} catch (Exception e) {

			String erroMsg = String.format("Erro ao abrir programa %s",
					Resources.translate(permissaoPrograma.getPrograma().getDescricao()));

			if (e.getCause() instanceof SysDescException) {

				erroMsg = ((SysDescException) e.getCause()).getMensagem();
			}

			log.error(erroMsg, e);

			JOptionPane.showMessageDialog(this, erroMsg);
		}

	}

	public void posicionarFrame(JInternalFrame internalFrame, String icon) {

		if (!StringUtil.isNullOrEmpty(icon)) {

			internalFrame.setFrameIcon(ImageUtil.resize(icon, 18, 18));
		}

		desktopPane.add(internalFrame);

		Dimension desktopSize = desktopPane.getSize();
		Dimension jInternalFrameSize = internalFrame.getSize();

		internalFrame.setLocation((desktopSize.width - jInternalFrameSize.width) / 2,
				(desktopSize.height - jInternalFrameSize.height) / 2);

		internalFrame.setVisible(Boolean.TRUE);
	}

	public static FrmApplication getInstance() {

		if (frmApplication == null) {

			frmApplication = new FrmApplication();

			frmApplication.setVisible(Boolean.TRUE);

			FrmLogin frmLogin = new FrmLogin(frmApplication);

			frmLogin.setVisible(Boolean.TRUE);

			usuario = frmLogin.getUsuario();

			frmApplication.createMenus();

			frmApplication.setarLabels();

		}

		return frmApplication;
	}

	private void setarLabels() {

		String formattedUser = String.format(" %s - %s", usuario.getIdUsuario(), usuario.getNomeUsuario());

		lbUsuario.setText(translate(FRMAPPLICATION_LB_USUARIO) + formattedUser);

		setarLabelCaixa();
	}

	public void setarLabelCaixa() {

		String descricaoCaixa = "";

		CaixaAbertoProjection caixaProjection = new CaixaDAO().obterCaixaAberto(usuario.getIdUsuario());

		if (caixaProjection != null) {

			descricaoCaixa = String.format("%s - %s", caixaProjection.getDescricao(),
					caixaProjection.isCaixaAberto() ? "ABERTO" : "FECHADO");
		}

		lbCaixa.setText(descricaoCaixa);

	}

	private void createMenus() {

		MainService mainService = new MainService();

		List<Programa> permissoes = mainService.buscarPermissaoUsuario(usuario.getIdUsuario());

		permissoes.sort(Comparator.comparing(Programa::getOrdem));

		permissoes.forEach(menu -> {

			if (!ListUtil.isNullOrEmpty(menu.getProgramas())) {

				JMenu menuToolbar = new JMenu(translate(menu.getDescricao()));

				menuBar.add(menuToolbar);

				menu.getProgramas().sort(Comparator.comparing(Programa::getOrdem));

				menu.getProgramas().forEach(programa -> createSubMenus(menuToolbar, programa));
			}

		});

		menuBar.repaint();
		toolBar.repaint();
	}

	private void createSubMenus(JMenu menuToolbar, Programa menu) {

		if (isValidMenu(menu)) {
			if (!ListUtil.isNullOrEmpty(menu.getProgramas())) {

				JMenu submenu = new JMenu(translate(menu.getDescricao()));

				menuToolbar.add(submenu);

				menu.getProgramas().sort(Comparator.comparing(Programa::getOrdem));

				if (!StringUtil.isNullOrEmpty(menu.getIcone())) {
					submenu.setIcon(ImageUtil.resize(menu.getIcone(), 25, 25));
				}

				menu.getProgramas().forEach(programa -> createSubMenus(submenu, programa));

				return;
			}

			JMenuItem menuitem = new JMenuItem(translate(menu.getDescricao()));

			ProgramasEnum programa = ProgramasEnum.findByCodigo(menu.getIdPrograma());

			if (programa != null) {
				menuitem.addActionListener(
						e -> getSingleInstance(programa.getInternalFrame(), menu.getPermissaoProgramas().get(0)));
			}

			if (!StringUtil.isNullOrEmpty(menu.getIcone())) {

				menuitem.setIcon(ImageUtil.resize(menu.getIcone(), 20, 20));

				if (menu.isFlagAcessoRapido()) {

					JButton botao = new JButton(ImageUtil.resize(menu.getIcone(), 30, 30));

					botao.setToolTipText(translate(menu.getDescricao()));

					botao.addActionListener(
							e -> getSingleInstance(programa.getInternalFrame(), menu.getPermissaoProgramas().get(0)));

					toolBar.add(botao);
				}
			}

			menuToolbar.add(menuitem);
		}
	}

	private boolean isValidMenu(Programa menu) {

		return menu.getCodigoAplicativo() == null || menu.getCodigoAplicativo().equals(1L);
	}

	public static Usuario getUsuario() {
		return usuario;
	}

	public JDesktopPane getDesktopPane() {
		return desktopPane;
	}
}
