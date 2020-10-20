package br.com.lar.ui;

import java.util.Arrays;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import br.com.lar.repository.model.Historico;
import br.com.lar.service.historico.HistoricoService;
import br.com.lar.startup.enumeradores.PesquisaEnum;
import br.com.lar.startup.enumeradores.TipoHistoricoOperacaoEnum;
import br.com.sysdesc.components.AbstractInternalFrame;
import br.com.sysdesc.components.JTextFieldId;
import br.com.sysdesc.components.JTextFieldMaiusculo;
import br.com.sysdesc.pesquisa.ui.components.PanelActions;
import net.miginfocom.swing.MigLayout;

public class FrmCadastroOperacoes extends AbstractInternalFrame {

	private static final long serialVersionUID = 1L;

	private JPanel painelContent;
	private JTextFieldId txCodigo;
	private JLabel lblCodigo;
	private JLabel lbTipoHistorico;
	private JLabel lbDescricao;
	private JTextFieldMaiusculo txDescricao;
	private PanelActions<Historico> panelActions;
	private HistoricoService historicoService = new HistoricoService();
	private JComboBox<TipoHistoricoOperacaoEnum> cbTipoHistorico;

	public FrmCadastroOperacoes(Long permissaoPrograma, Long codigoUsuario) {
		super(permissaoPrograma, codigoUsuario);

		initComponents();
	}

	private void initComponents() {

		setSize(450, 230);
		setClosable(Boolean.TRUE);
		setTitle("CADASTRO DE OPERAÇÕES");

		painelContent = new JPanel();
		txCodigo = new JTextFieldId();
		lblCodigo = new JLabel("Código:");
		lbTipoHistorico = new JLabel("Tipo de Historico:");
		lbDescricao = new JLabel("Descrição:");
		txDescricao = new JTextFieldMaiusculo();
		cbTipoHistorico = new JComboBox<>();

		painelContent.setLayout(new MigLayout("", "[grow]", "[][][][][][][grow]"));
		getContentPane().add(painelContent);
		painelContent.add(lblCodigo, "cell 0 0");
		painelContent.add(txCodigo, "cell 0 1,width 50:100:100");
		painelContent.add(lbDescricao, "cell 0 2");
		painelContent.add(txDescricao, "cell 0 3,growx");
		painelContent.add(lbTipoHistorico, "cell 0 4,growx");
		painelContent.add(cbTipoHistorico, "cell 0 5,growx");

		Arrays.asList(TipoHistoricoOperacaoEnum.values()).forEach(cbTipoHistorico::addItem);

		panelActions = new PanelActions<Historico>(this, historicoService,
				PesquisaEnum.PES_OPERACOES.getCodigoPesquisa()) {

			private static final long serialVersionUID = 1L;

			@Override
			public void carregarObjeto(Historico objeto) {
				txCodigo.setValue(objeto.getIdHistorico());
				cbTipoHistorico.setSelectedItem(TipoHistoricoOperacaoEnum.forValue(objeto.getTipoHistorico()));
				txDescricao.setText(objeto.getDescricao());
			}

			@Override
			public boolean preencherObjeto(Historico objetoPesquisa) {

				objetoPesquisa.setIdHistorico(txCodigo.getValue());
				objetoPesquisa.setTipoHistorico(null);
				objetoPesquisa.setDescricao(txDescricao.getText());

				if (cbTipoHistorico.getSelectedIndex() >= 0) {

					objetoPesquisa.setTipoHistorico(
							((TipoHistoricoOperacaoEnum) cbTipoHistorico.getSelectedItem()).getCodigo());
				}

				return true;
			}

		};

		panelActions.addSaveListener(objeto -> txCodigo.setValue(objeto.getIdHistorico()));

		painelContent.add(panelActions, "cell 0 6,growx,aligny bottom");
	}

}
