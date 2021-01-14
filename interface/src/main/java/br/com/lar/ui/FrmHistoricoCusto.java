package br.com.lar.ui;

import java.util.Arrays;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import br.com.lar.repository.model.Historico;
import br.com.lar.repository.model.HistoricoCusto;
import br.com.lar.service.historico.HistoricoService;
import br.com.lar.service.historicocusto.HistoricoCustoService;
import br.com.lar.startup.enumeradores.PesquisaEnum;
import br.com.sysdesc.components.AbstractInternalFrame;
import br.com.sysdesc.components.JNumericField;
import br.com.sysdesc.components.JTextFieldId;
import br.com.sysdesc.pesquisa.ui.components.CampoPesquisa;
import br.com.sysdesc.pesquisa.ui.components.PanelActions;
import br.com.systrans.util.enumeradores.TipoAlocacaoEnum;
import net.miginfocom.swing.MigLayout;

public class FrmHistoricoCusto extends AbstractInternalFrame {

	private static final long serialVersionUID = 1L;

	private JPanel painelContent;

	private JLabel lblCodigo;
	private JLabel lbHistorico;

	private JTextFieldId txCodigo;
	private CampoPesquisa<Historico> pesquisaHistorico;

	private PanelActions<HistoricoCusto> panelActions;
	private HistoricoCustoService historicoCustoService = new HistoricoCustoService();
	private HistoricoService historicoService = new HistoricoService();
	private JLabel lblTipoDeAlocao;
	private JLabel lblMesesAlocao;
	private JComboBox<TipoAlocacaoEnum> cbTipoAlocacao;
	private JNumericField txMesesAlocacao;

	public FrmHistoricoCusto(Long permissaoPrograma, Long codigoUsuario) {
		super(permissaoPrograma, codigoUsuario);

		initComponents();
	}

	private void initComponents() {

		setSize(450, 220);
		setClosable(Boolean.TRUE);
		setTitle("CADASTRO DE HISTÓRICO DE CUSTOS");

		painelContent = new JPanel();

		lblCodigo = new JLabel("Código:");
		lbHistorico = new JLabel("Histórico:");
		txCodigo = new JTextFieldId();
		lblTipoDeAlocao = new JLabel("Tipo de Alocação:");
		lblMesesAlocao = new JLabel("Meses Alocação:");
		cbTipoAlocacao = new JComboBox<>();
		txMesesAlocacao = new JNumericField(3);
		pesquisaHistorico = new CampoPesquisa<Historico>(historicoService, PesquisaEnum.PES_HISTORICOOPERACOES.getCodigoPesquisa(),
				getCodigoUsuario(), historicoService.getHistoricosDevedores()) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(Historico objeto) {
				return String.format("%d - %s", objeto.getIdHistorico(), objeto.getDescricao());
			}
		};

		Arrays.asList(TipoAlocacaoEnum.values()).forEach(cbTipoAlocacao::addItem);

		painelContent.setLayout(new MigLayout("", "[grow][100px]", "[][][][][][][grow]"));
		getContentPane().add(painelContent);

		painelContent.add(lblCodigo, "cell 0 0");
		painelContent.add(txCodigo, "cell 0 1,width 50:100:100");
		painelContent.add(lbHistorico, "cell 0 2 2 1");
		painelContent.add(pesquisaHistorico, "cell 0 3 2 1,growx");
		painelContent.add(lblTipoDeAlocao, "cell 0 4");
		painelContent.add(lblMesesAlocao, "cell 1 4");
		painelContent.add(cbTipoAlocacao, "cell 0 5,growx");
		painelContent.add(txMesesAlocacao, "cell 1 5,growx");

		panelActions = new PanelActions<HistoricoCusto>(this, historicoCustoService, PesquisaEnum.PES_HISTORICO_CUSTO.getCodigoPesquisa()) {

			private static final long serialVersionUID = 1L;

			@Override
			public void carregarObjeto(HistoricoCusto objeto) {
				txCodigo.setValue(objeto.getIdHistoricoCusto());
				pesquisaHistorico.setValue(objeto.getHistorico());
				cbTipoAlocacao.setSelectedItem(TipoAlocacaoEnum.forValue(objeto.getFlagTipoCusto()));
				txMesesAlocacao.setValue(objeto.getMesesAlocacao());
			}

			@Override
			public boolean preencherObjeto(HistoricoCusto objetoPesquisa) {

				objetoPesquisa.setIdHistoricoCusto(txCodigo.getValue());
				objetoPesquisa.setHistorico(pesquisaHistorico.getObjetoPesquisado());
				objetoPesquisa.setFlagTipoCusto(null);

				if (cbTipoAlocacao.getSelectedIndex() >= 0) {
					objetoPesquisa.setFlagTipoCusto(((TipoAlocacaoEnum) cbTipoAlocacao.getSelectedItem()).getCodigo());
				}

				objetoPesquisa.setMesesAlocacao(txMesesAlocacao.getValue());

				return true;
			}
		};

		panelActions.addSaveListener(objeto -> txCodigo.setValue(objeto.getIdHistoricoCusto()));
		painelContent.add(panelActions, "cell 0 6 2 1,growx,aligny bottom");

	}

}
