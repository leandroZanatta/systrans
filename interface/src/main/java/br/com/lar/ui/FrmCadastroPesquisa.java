package br.com.lar.ui;

import static br.com.sysdesc.util.constants.MensagemConstants.MENSAGEM_SELECIONE_APENAS_UM_REGISTRO;
import static br.com.sysdesc.util.constants.MensagemConstants.MENSAGEM_SELECIONE_PESQUISA;
import static br.com.sysdesc.util.enumeradores.TipoPesquisaEnum.NORMAL;
import static br.com.sysdesc.util.resources.Resources.FRMLOGIN_MSG_VERIFICACAO;
import static br.com.sysdesc.util.resources.Resources.FRMPESQUISA_LB_CODIGO;
import static br.com.sysdesc.util.resources.Resources.FRMPESQUISA_LB_DESCRICAO;
import static br.com.sysdesc.util.resources.Resources.FRMPESQUISA_LB_PAGINACAO;
import static br.com.sysdesc.util.resources.Resources.FRMPESQUISA_LB_PESQUISA;
import static br.com.sysdesc.util.resources.Resources.FRMPESQUISA_TITLE;
import static br.com.sysdesc.util.resources.Resources.translate;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import br.com.lar.startup.enumeradores.PesquisaEnum;
import br.com.lar.ui.dialogs.FrmPesquisaBasicaCampo;
import br.com.sysdesc.components.AbstractInternalFrame;
import br.com.sysdesc.components.JNumericField;
import br.com.sysdesc.components.JTextFieldMaiusculo;
import br.com.sysdesc.components.listeners.adapter.ButtonActionListenerAdapter;
import br.com.sysdesc.pesquisa.repository.model.Pesquisa;
import br.com.sysdesc.pesquisa.repository.model.PesquisaCampo;
import br.com.sysdesc.pesquisa.service.impl.PesquisaBasicaService;
import br.com.sysdesc.pesquisa.ui.components.PanelActions;
import br.com.sysdesc.pesquisa.ui.models.ProjectionsTableModel;
import br.com.sysdesc.util.classes.ContadorUtil;
import br.com.sysdesc.util.classes.ImageUtil;

public class FrmCadastroPesquisa extends AbstractInternalFrame {

	private static final long serialVersionUID = 1L;

	private JLabel lblCodigo;
	private JLabel lblPaginacao;
	private JLabel lblPesquisa;
	private JLabel lblDescricao;

	private JPanel painelContent;

	private JTextFieldMaiusculo txDescricao;
	private JNumericField txCodigo;
	private JNumericField txPaginacao;
	private JComboBox<PesquisaEnum> cbPesquisa;
	private JScrollPane scrollPane;
	private JTable tabela;
	private JButton btAdd;
	private JButton btRemove;
	private ProjectionsTableModel projectionsTableModel = new ProjectionsTableModel();

	private PanelActions<Pesquisa> panelActions;

	private PesquisaBasicaService pesquisaBasicaService = new PesquisaBasicaService();

	public FrmCadastroPesquisa(Long permissaoPrograma, Long codigoUsuario) {
		super(permissaoPrograma, codigoUsuario);

		setSize(800, 400);
		setClosable(Boolean.TRUE);
		setTitle(translate(FRMPESQUISA_TITLE));

		painelContent = new JPanel();

		projectionsTableModel = new ProjectionsTableModel();
		painelContent.setLayout(null);

		lblCodigo = new JLabel(translate(FRMPESQUISA_LB_CODIGO));
		lblDescricao = new JLabel(translate(FRMPESQUISA_LB_DESCRICAO));
		lblPesquisa = new JLabel(translate(FRMPESQUISA_LB_PESQUISA));
		lblPaginacao = new JLabel(translate(FRMPESQUISA_LB_PAGINACAO));

		txPaginacao = new JNumericField();
		txCodigo = new JNumericField();
		txDescricao = new JTextFieldMaiusculo();
		cbPesquisa = new JComboBox<>();
		btAdd = new JButton("");
		btRemove = new JButton("");
		tabela = new JTable(projectionsTableModel);
		scrollPane = new JScrollPane(tabela);

		lblCodigo.setBounds(10, 10, 50, 17);
		txPaginacao.setBounds(687, 80, 87, 20);
		txCodigo.setBounds(10, 30, 100, 20);
		lblDescricao.setBounds(10, 61, 350, 17);
		lblPesquisa.setBounds(375, 61, 300, 17);
		txDescricao.setBounds(10, 80, 350, 20);
		lblPaginacao.setBounds(687, 60, 87, 17);
		cbPesquisa.setBounds(375, 80, 300, 20);
		btAdd.setBounds(737, 199, 40, 23);
		btRemove.setBounds(737, 233, 40, 23);
		scrollPane.setBounds(10, 112, 725, 212);

		Arrays.asList(PesquisaEnum.values()).forEach(cbPesquisa::addItem);

		btAdd.setIcon(ImageUtil.resize("add.png", 15, 15));
		btRemove.setIcon(ImageUtil.resize("minus.png", 15, 15));

		painelContent.add(lblCodigo);
		painelContent.add(txCodigo);
		painelContent.add(lblDescricao);
		painelContent.add(lblPesquisa);
		painelContent.add(txDescricao);
		painelContent.add(lblPaginacao);
		painelContent.add(cbPesquisa);
		painelContent.add(txPaginacao);
		painelContent.add(btAdd);
		painelContent.add(btRemove);

		painelContent.add(scrollPane);

		tabela.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent mouseEvent) {

				JTable table = (JTable) mouseEvent.getSource();

				Point point = mouseEvent.getPoint();

				int row = table.rowAtPoint(point);

				if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {

					abrirPesquisaCampo(projectionsTableModel.getRow(row));
				}
			}
		});

		btRemove.addActionListener((l) -> removerPesquisaCampo());

		btAdd.addActionListener((l) -> abrirPesquisaCampo());

		getContentPane().add(painelContent);

		panelActions = new PanelActions<Pesquisa>(this, pesquisaBasicaService,
				PesquisaEnum.PES_PESQUISA.getCodigoPesquisa()) {

			private static final long serialVersionUID = 1L;

			@Override
			public void carregarObjeto(Pesquisa objeto) {
				txCodigo.setValue(objeto.getIdPesquisa());
				txDescricao.setText(objeto.getDescricao());
				txPaginacao.setValue(objeto.getPaginacao());
				cbPesquisa.setSelectedItem(PesquisaEnum.forValue(objeto.getCodigoPesquisa()));

				projectionsTableModel.setRows(new ArrayList<>(objeto.getPesquisaCampos()));
			}

			@Override
			public boolean preencherObjeto(Pesquisa objetoPesquisa) {

				objetoPesquisa.setIdPesquisa(txCodigo.getValue());
				objetoPesquisa.setDescricao(txDescricao.getText());
				objetoPesquisa.setPaginacao(txPaginacao.getValue());

				if (cbPesquisa.getSelectedIndex() >= 0) {

					objetoPesquisa.setCodigoPesquisa(((PesquisaEnum) cbPesquisa.getSelectedItem()).getCodigoPesquisa());

				}

				objetoPesquisa.setTipo(NORMAL.getCodigo());

				List<PesquisaCampo> pesquisaCampos = projectionsTableModel.getRows();

				ContadorUtil contadorUtil = new ContadorUtil();

				pesquisaCampos.forEach(pesquisaCampo -> {

					pesquisaCampo.setPesquisa(objetoPesquisa);

					pesquisaCampo.setFlagOrdem(contadorUtil.next());
				});

				objetoPesquisa.setPesquisaCampos(pesquisaCampos);

				return true;
			}
		};
		panelActions.setBounds(10, 325, 764, 38);

		panelActions.addSaveListener(objeto -> txCodigo.setValue(objeto.getIdPesquisa()));

		panelActions.addButtonListener(new ButtonActionListenerAdapter() {

			@Override
			public void newEvent() {
				txPaginacao.setValue(20L);
			}

		});

		painelContent.add(panelActions);

	}

	private void removerPesquisaCampo() {

		if (tabela.getSelectedRow() < 0) {
			JOptionPane.showMessageDialog(null, translate(MENSAGEM_SELECIONE_APENAS_UM_REGISTRO));

			return;
		}

		projectionsTableModel.remove(tabela.getSelectedRow());
	}

	private void abrirPesquisaCampo() {

		abrirPesquisaCampo(null);
	}

	private void abrirPesquisaCampo(PesquisaCampo pesquisaCampoEdicao) {

		if (cbPesquisa.getSelectedIndex() < 0) {

			JOptionPane.showMessageDialog(null, translate(MENSAGEM_SELECIONE_PESQUISA),
					translate(FRMLOGIN_MSG_VERIFICACAO), JOptionPane.WARNING_MESSAGE);

			return;
		}

		PesquisaCampo pesquisaCampo = new PesquisaCampo();
		pesquisaCampo.setCodigoPesquisa(((PesquisaEnum) cbPesquisa.getSelectedItem()).getCodigoPesquisa());

		if (pesquisaCampoEdicao != null) {
			pesquisaCampo = pesquisaCampoEdicao;
		}

		FrmPesquisaBasicaCampo frmPesquisaBasicaCampo = new FrmPesquisaBasicaCampo(pesquisaCampo);

		frmPesquisaBasicaCampo.setVisible(Boolean.TRUE);

		if (frmPesquisaBasicaCampo.getSucesso()) {
			projectionsTableModel.addProjection(frmPesquisaBasicaCampo.getPesquisaCampo());
		}
	}

}
