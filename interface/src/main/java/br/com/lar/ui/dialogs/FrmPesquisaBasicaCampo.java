package br.com.lar.ui.dialogs;

import static br.com.sysdesc.util.constants.MensagemConstants.MENSAGEM_INSIRA_DESCRICAO_VALIDA;
import static br.com.sysdesc.util.constants.MensagemConstants.MENSAGEM_INSIRA_TAMANHO_VALIDO;
import static br.com.sysdesc.util.constants.MensagemConstants.MENSAGEM_SELECIONE_FIELD;
import static br.com.sysdesc.util.constants.MensagemConstants.MENSAGEM_SELECIONE_FORMATACAO;
import static br.com.sysdesc.util.resources.Resources.OPTION_VALIDACAO;
import static br.com.sysdesc.util.resources.Resources.translate;

import java.awt.BorderLayout;
import java.util.Arrays;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import br.com.lar.startup.enumeradores.PesquisaEnum;
import br.com.sysdesc.components.JNumericField;
import br.com.sysdesc.pesquisa.repository.model.PesquisaCampo;
import br.com.sysdesc.pesquisa.util.enumeradores.FormatoPesquisaEnum;
import br.com.sysdesc.pesquisa.util.enumeradores.TipoTamanhoEnum;
import br.com.sysdesc.pesquisa.util.formatters.Formatter;
import br.com.sysdesc.pesquisa.util.formatters.impl.LongFormatter;
import br.com.sysdesc.util.classes.ImageUtil;
import br.com.sysdesc.util.classes.LongUtil;
import br.com.sysdesc.util.classes.StringUtil;
import br.com.sysdesc.util.dao.EntityPathUtil;
import br.com.sysdesc.util.enumeradores.TipoFieldEnum;
import br.com.sysdesc.util.exception.SysDescException;
import br.com.sysdesc.util.vo.FieldPesquisaVO;
import net.miginfocom.swing.MigLayout;

public class FrmPesquisaBasicaCampo extends JDialog {

	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();
	private final PesquisaCampo pesquisaCampo;

	private Boolean sucesso = Boolean.FALSE;
	private JLabel lbDescricao;
	private JLabel lbTipoTamanho;
	private JLabel lbTamanho;
	private JLabel lbFormatacao;
	private JLabel lbTipoFormatacao;
	private JLabel lbField;
	private JComboBox<FormatoPesquisaEnum> cbFormatacao;
	private JComboBox<FieldPesquisaVO> cbField;
	private JComboBox<TipoTamanhoEnum> cbTipoTamanho;
	private Formatter componentFormatacao;
	private JTextField txDescricao;
	private JNumericField txTamanho;
	private JPanel panel;
	private JButton btOk;
	private JButton btCancelar;

	public FrmPesquisaBasicaCampo(PesquisaCampo pesquisaCampo) {
		this.pesquisaCampo = pesquisaCampo;

		initComponents();

	}

	private void initComponents() {
		setSize(450, 250);
		setModal(Boolean.TRUE);
		setLocationRelativeTo(null);
		setTitle("CADASTRO DE CAMPOS");

		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[grow][120][]", "[][][][][][][][grow,bottom]"));

		lbField = new JLabel("Field:");
		lbDescricao = new JLabel("Descrição:");
		lbTipoTamanho = new JLabel("Tipo Tamanho:");
		cbField = new JComboBox<FieldPesquisaVO>();
		lbTamanho = new JLabel("Tamanho:");
		txDescricao = new JTextField();
		cbTipoTamanho = new JComboBox<TipoTamanhoEnum>();
		txTamanho = new JNumericField();
		lbFormatacao = new JLabel("Formatação:");
		lbTipoFormatacao = new JLabel("Tipo de formatação:");
		cbFormatacao = new JComboBox<FormatoPesquisaEnum>();
		panel = new JPanel();
		btOk = new JButton("Ok");
		btCancelar = new JButton("Cancelar");
		componentFormatacao = new LongFormatter();

		EntityPathUtil.getAllFieldsFromEntity(PesquisaEnum.forValue(pesquisaCampo.getCodigoPesquisa()).getEntityPath())
				.forEach(cbField::addItem);
		Arrays.asList(TipoTamanhoEnum.values()).forEach(cbTipoTamanho::addItem);

		btCancelar.addActionListener((e) -> dispose());
		cbField.addActionListener((e) -> procesarTipoCampo());
		cbFormatacao.addActionListener((e) -> processarTipoPesquisa());
		btOk.addActionListener((e) -> selecionarCampoPesquisa());

		btCancelar.setIcon(ImageUtil.resize("cancel.png", 16, 16));
		btOk.setIcon(ImageUtil.resize("ok.png", 16, 16));

		cbField.setSelectedIndex(-1);

		contentPanel.add(lbField, "cell 0 0");
		contentPanel.add(lbFormatacao, "cell 0 4");
		contentPanel.add(lbDescricao, "cell 0 2");
		contentPanel.add(lbTipoTamanho, "cell 1 2");
		contentPanel.add(lbTamanho, "cell 2 2");
		contentPanel.add(lbTipoFormatacao, "cell 1 4");
		contentPanel.add(txDescricao, "cell 0 3,growx");
		contentPanel.add(cbField, "cell 0 1 3 1,growx");

		contentPanel.add(txTamanho, "cell 2 3,growx");
		contentPanel.add(cbFormatacao, "cell 0 5,growx");
		contentPanel.add(cbTipoTamanho, "cell 1 3,growx");
		contentPanel.add(componentFormatacao.getComponent(), "cell 1 5 2 1,growx,aligny top");
		contentPanel.add(panel, "cell 0 7 3 1,growx,aligny bottom");

		panel.add(btOk);
		panel.add(btCancelar);

		carregarCampos();
	}

	private void carregarCampos() {

		if (!StringUtil.isNullOrEmpty(pesquisaCampo.getCampo())) {

			Optional<FieldPesquisaVO> fieldPesquisaVO = EntityPathUtil
					.getAllFieldsFromEntity(PesquisaEnum.forValue(pesquisaCampo.getCodigoPesquisa()).getEntityPath())
					.stream().filter(x -> x.getName().equals(pesquisaCampo.getCampo())).findFirst();

			if (fieldPesquisaVO.isPresent()) {
				cbField.setSelectedItem(fieldPesquisaVO.get());
			}
		}

		if (!StringUtil.isNullOrEmpty(pesquisaCampo.getDescricao())) {
			txDescricao.setText(pesquisaCampo.getDescricao());
		}

		if (pesquisaCampo.getFlagTipoTamanho() != null) {
			cbTipoTamanho.setSelectedItem(TipoTamanhoEnum.tipoTamanhoForCodigo(pesquisaCampo.getFlagTipoTamanho()));
		}

		if (pesquisaCampo.getNumeroTamanho() != null) {
			txTamanho.setValue(pesquisaCampo.getNumeroTamanho());
		}

		if (pesquisaCampo.getFlagFormatacao() != null) {

			cbFormatacao.setSelectedItem(FormatoPesquisaEnum.formatoForCodigo(pesquisaCampo.getFlagFormatacao()));

			processarTipoPesquisa();
		}
	}

	private void selecionarCampoPesquisa() {

		try {

			validar();

			TipoFieldEnum tipoField = getTipoDado();

			pesquisaCampo.setCampo(((FieldPesquisaVO) cbField.getSelectedItem()).getName());
			pesquisaCampo.setDescricao(txDescricao.getText());
			pesquisaCampo.setFlagTipoDado(tipoField.getCodigo());
			pesquisaCampo.setFlagTipoTamanho(((TipoTamanhoEnum) cbTipoTamanho.getSelectedItem()).getCodigo());
			pesquisaCampo.setNumeroTamanho(txTamanho.getValue());
			pesquisaCampo.setFlagFormatacao(((FormatoPesquisaEnum) cbFormatacao.getSelectedItem()).getCodigo());
			pesquisaCampo.setFormato(componentFormatacao.getFormatterKey());

			sucesso = Boolean.TRUE;

			dispose();

		} catch (SysDescException e) {
			JOptionPane.showMessageDialog(null, e.getMensagem(), translate(OPTION_VALIDACAO),
					JOptionPane.WARNING_MESSAGE);
		}
	}

	private void validar() {

		if (cbField.getSelectedIndex() < 0) {

			throw new SysDescException(MENSAGEM_SELECIONE_FIELD);
		}

		if (StringUtil.isNullOrEmpty(txDescricao.getText())) {
			throw new SysDescException(MENSAGEM_INSIRA_DESCRICAO_VALIDA);
		}

		if (LongUtil.isNullOrZero(txTamanho.getValue())) {
			throw new SysDescException(MENSAGEM_INSIRA_TAMANHO_VALIDO);
		}

		if (cbFormatacao.getSelectedIndex() < 0) {

			throw new SysDescException(MENSAGEM_SELECIONE_FORMATACAO);
		}

	}

	private void processarTipoPesquisa() {

		contentPanel.remove(componentFormatacao.getComponent());

		if (cbFormatacao.getSelectedIndex() < 0) {

			componentFormatacao = FormatoPesquisaEnum.DEFAULT.getFormatter();

		} else {

			FormatoPesquisaEnum tipoPesquisaEnum = (FormatoPesquisaEnum) cbFormatacao.getSelectedItem();

			componentFormatacao = tipoPesquisaEnum.getFormatter();

		}

		contentPanel.add(componentFormatacao.getComponent(), "cell 1 5 2 1,growx,aligny top");

		contentPanel.revalidate();

		contentPanel.repaint();
	}

	private void procesarTipoCampo() {

		cbFormatacao.removeAllItems();

		if (cbField.getSelectedIndex() >= 0) {

			TipoFieldEnum tipoField = getTipoDado();

			FormatoPesquisaEnum.tipoTamanhoForTipoPesquisa(tipoField).forEach(cbFormatacao::addItem);
		}

	}

	private TipoFieldEnum getTipoDado() {
		FieldPesquisaVO fieldPesquisaVO = (FieldPesquisaVO) cbField.getSelectedItem();

		TipoFieldEnum tipoField = TipoFieldEnum.getFromPath(fieldPesquisaVO);
		return tipoField;
	}

	public Boolean getSucesso() {
		return sucesso;
	}

	public PesquisaCampo getPesquisaCampo() {
		return pesquisaCampo;
	}

}
