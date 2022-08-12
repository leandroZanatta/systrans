package br.com.lar.ui;

import static br.com.lar.startup.enumeradores.PesquisaEnum.PES_CONFIGURACAO_ABASTECIMENTO;

import javax.swing.JLabel;

import br.com.lar.repository.model.CentroCusto;
import br.com.lar.repository.model.ConfiguracaoAbastecimento;
import br.com.lar.repository.model.Operacao;
import br.com.lar.repository.model.Veiculo;
import br.com.lar.service.abastecimento.ConfiguracaoAbastecimentoService;
import br.com.lar.service.centrocusto.CentroCustoService;
import br.com.lar.service.operacao.OperacaoService;
import br.com.lar.service.veiculo.VeiculoService;
import br.com.lar.startup.enumeradores.PesquisaEnum;
import br.com.sysdesc.components.AbstractInternalFrame;
import br.com.sysdesc.components.JTextFieldId;
import br.com.sysdesc.pesquisa.ui.components.CampoPesquisa;
import br.com.sysdesc.pesquisa.ui.components.PanelActions;
import net.miginfocom.swing.MigLayout;

public class FrmConfigurarAbastecimentos extends AbstractInternalFrame {

	private static final long serialVersionUID = 1L;
	private JLabel lbCodigo;
	private JTextFieldId txCodigo;

	private PanelActions<ConfiguracaoAbastecimento> panelActions;
	private ConfiguracaoAbastecimentoService configuracaoAbastecimentoService = new ConfiguracaoAbastecimentoService();
	private VeiculoService veiculoService = new VeiculoService();
	private OperacaoService operacaoService = new OperacaoService();
	private CentroCustoService centroCustoService = new CentroCustoService();

	private CampoPesquisa<Veiculo> pesquisaVeiculo;
	private CampoPesquisa<Operacao> pesquisaOperacao;
	private CampoPesquisa<CentroCusto> pesquisaCentroCusto;

	private JLabel lbVeiculo;
	private JLabel lbOperacao;
	private JLabel lbCentroCusto;

	public FrmConfigurarAbastecimentos(Long codigoPrograma, Long codigoUsuario) {
		super(codigoPrograma, codigoUsuario);

		initComponents();
	}

	private void initComponents() {

		setSize(450, 280);
		setClosable(Boolean.TRUE);
		setTitle("Configurar Integração de Abastecimentos");

		getContentPane().setLayout(new MigLayout("", "[66.00][grow]", "[][][][][][][][][grow]"));

		lbCodigo = new JLabel("Código: ");
		lbVeiculo = new JLabel("Veículo:");
		lbOperacao = new JLabel("Operação:");
		lbCentroCusto = new JLabel("Centro de Custos:");

		txCodigo = new JTextFieldId();

		pesquisaVeiculo = new CampoPesquisa<Veiculo>(veiculoService, PesquisaEnum.PES_VEICULOS.getCodigoPesquisa(), getCodigoUsuario()) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(Veiculo objeto) {

				return String.format("%d - %s", objeto.getIdVeiculo(), objeto.getPlaca());
			}
		};

		pesquisaOperacao = new CampoPesquisa<Operacao>(operacaoService, PesquisaEnum.PES_OPERACOES.getCodigoPesquisa(), getCodigoUsuario()) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(Operacao objeto) {

				return String.format("%d - %s", objeto.getIdOperacao(), objeto.getDescricao());
			}
		};

		pesquisaCentroCusto = new CampoPesquisa<CentroCusto>(centroCustoService, PesquisaEnum.PES_CENTRO_CUSTO.getCodigoPesquisa(),
				getCodigoUsuario()) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(CentroCusto objeto) {

				return String.format("%d - %s", objeto.getIdCentroCusto(), objeto.getDescricao());
			}
		};

		getContentPane().add(lbCodigo, "cell 0 0");
		getContentPane().add(txCodigo, "cell 0 1,growx");
		getContentPane().add(pesquisaVeiculo, "cell 0 3 2 1,grow");
		getContentPane().add(pesquisaOperacao, "cell 0 5 2 1,grow");
		getContentPane().add(pesquisaCentroCusto, "cell 0 7 2 1,grow");
		getContentPane().add(lbVeiculo, "cell 0 2,alignx left");
		getContentPane().add(lbOperacao, "cell 0 4");
		getContentPane().add(lbCentroCusto, "cell 0 6");

		panelActions = new PanelActions<ConfiguracaoAbastecimento>(this, configuracaoAbastecimentoService,
				PES_CONFIGURACAO_ABASTECIMENTO.getCodigoPesquisa()) {

			private static final long serialVersionUID = 1L;

			@Override
			public void carregarObjeto(ConfiguracaoAbastecimento objeto) {
				txCodigo.setValue(objeto.getIdConfiguracaoAbastecimento());
				pesquisaVeiculo.setValue(objeto.getVeiculo());
				pesquisaOperacao.setValue(objeto.getOperacao());
				pesquisaCentroCusto.setValue(objeto.getCentroCusto());
			}

			@Override
			public boolean preencherObjeto(ConfiguracaoAbastecimento objetoPesquisa) {
				objetoPesquisa.setIdConfiguracaoAbastecimento(txCodigo.getValue());
				objetoPesquisa.setVeiculo(pesquisaVeiculo.getObjetoPesquisado());
				objetoPesquisa.setOperacao(pesquisaOperacao.getObjetoPesquisado());
				objetoPesquisa.setCentroCusto(pesquisaCentroCusto.getObjetoPesquisado());

				return true;
			}
		};

		panelActions.addSaveListener(cidade -> txCodigo.setValue(cidade.getIdConfiguracaoAbastecimento()));

		getContentPane().add(panelActions, "cell 0 8 2 1,grow");
	}

}
