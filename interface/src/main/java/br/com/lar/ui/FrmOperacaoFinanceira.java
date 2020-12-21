package br.com.lar.ui;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.mysema.query.BooleanBuilder;

import br.com.lar.repository.model.FormasPagamento;
import br.com.lar.repository.model.Historico;
import br.com.lar.repository.model.OperacaoFinanceiro;
import br.com.lar.repository.model.PlanoContas;
import br.com.lar.service.formaspagamento.FormasPagamentoService;
import br.com.lar.service.historico.HistoricoService;
import br.com.lar.service.operacao.OperacaoFinanceiroService;
import br.com.lar.service.planocontas.PlanoContasService;
import br.com.lar.startup.enumeradores.PesquisaEnum;
import br.com.sysdesc.components.AbstractInternalFrame;
import br.com.sysdesc.components.JTextFieldId;
import br.com.sysdesc.pesquisa.ui.components.CampoPesquisa;
import br.com.sysdesc.pesquisa.ui.components.PanelActions;
import br.com.sysdesc.util.exception.SysDescException;
import br.com.sysdesc.util.resources.Resources;
import br.com.systrans.util.constants.MensagemConstants;
import br.com.systrans.util.enumeradores.TipoHistoricoOperacaoEnum;
import net.miginfocom.swing.MigLayout;

public class FrmOperacaoFinanceira extends AbstractInternalFrame {

	private static final String TEMPLATE_PESQUISA = "%d - %s";

	private static final long serialVersionUID = 1L;

	private JPanel painelContent;
	private PanelActions<OperacaoFinanceiro> painelBotoes;
	private JLabel lbCodigo;
	private JLabel lblFormaDePagamento;
	private JLabel lbHistorico;
	private JLabel lbContaCredora;
	private JTextFieldId txCodigo;
	private CampoPesquisa<Historico> pesquisaHistorico;
	private CampoPesquisa<PlanoContas> pesquisaHistoricoCredor;
	private HistoricoService historicoService = new HistoricoService();
	private FormasPagamentoService formasPagamentoService = new FormasPagamentoService();
	private PlanoContasService planoContasService = new PlanoContasService();
	private OperacaoFinanceiroService operacaoFinanceiroService = new OperacaoFinanceiroService();
	private JLabel lblFormaDePagamento_1;
	private CampoPesquisa<FormasPagamento> pesquisaFormasPagamento;
	private final TipoHistoricoOperacaoEnum historico;
	private CampoPesquisa<PlanoContas> pesquisaContaDevedora;

	public FrmOperacaoFinanceira(Long permissaoPrograma, Long codigoUsuario, TipoHistoricoOperacaoEnum historico) {
		super(permissaoPrograma, codigoUsuario);
		this.historico = historico;

		initComponents();
	}

	private void initComponents() {

		setSize(500, 320);
		setClosable(Boolean.TRUE);
		setTitle("CADASTRO DE OPERAÇÕES FINANCEIRAS");

		painelContent = new JPanel();
		lbCodigo = new JLabel("Código:");
		lbHistorico = new JLabel("Histórico:");
		lbContaCredora = new JLabel("Conta:");
		lblFormaDePagamento = new JLabel("Plano de Contas:");
		txCodigo = new JTextFieldId();

		pesquisaHistorico = new CampoPesquisa<Historico>(historicoService, PesquisaEnum.PES_HISTORICOOPERACOES.getCodigoPesquisa(),
				getCodigoUsuario(), historico.equals(TipoHistoricoOperacaoEnum.CREDOR) ? historicoService.getHistoricosCredores()
						: historicoService.getHistoricosDevedores()) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(Historico objeto) {
				return String.format(TEMPLATE_PESQUISA, objeto.getIdHistorico(), objeto.getDescricao());
			}
		};

		pesquisaFormasPagamento = new CampoPesquisa<FormasPagamento>(formasPagamentoService,
				PesquisaEnum.PES_FORMAS_PAGAMENTO.getCodigoPesquisa(),
				getCodigoUsuario()) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(FormasPagamento objeto) {
				return String.format(TEMPLATE_PESQUISA, objeto.getIdFormaPagamento(), objeto.getDescricao());
			}
		};

		pesquisaHistoricoCredor = new CampoPesquisa<PlanoContas>(planoContasService, PesquisaEnum.PES_PLANOCONTAS.getCodigoPesquisa(),
				getCodigoUsuario()) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(PlanoContas objeto) {
				return String.format(TEMPLATE_PESQUISA, objeto.getIdPlanoContas(), objeto.getDescricao());
			}

			@Override
			public BooleanBuilder getPreFilter() {

				if (pesquisaHistorico.getObjetoPesquisado() == null) {

					throw new SysDescException(Resources.translate(MensagemConstants.MENSAGEM_SELECIONE_HISTORICO));
				}

				switch (TipoHistoricoOperacaoEnum.forValue(pesquisaHistorico.getObjetoPesquisado().getTipoHistorico())) {
				case CREDOR:
					return planoContasService.getContasCredoras();
				case DEVEDOR:
					return planoContasService.getContasBalanco();
				default:
					throw new SysDescException(MensagemConstants.MENSAGEM_HISTORICO_OPERACAO_NAO_ENCONTRADO);

				}

			}
		};

		painelContent.setLayout(new MigLayout("", "[grow]", "[][][][][][][][][][][grow]"));
		getContentPane().add(painelContent);

		painelContent.add(lbCodigo, "cell 0 0");
		painelContent.add(lblFormaDePagamento, "cell 0 4");

		pesquisaContaDevedora = new CampoPesquisa<PlanoContas>(planoContasService, PesquisaEnum.PES_PLANOCONTAS.getCodigoPesquisa(),
				getCodigoUsuario()) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(PlanoContas objeto) {
				return String.format(TEMPLATE_PESQUISA, objeto.getIdPlanoContas(), objeto.getDescricao());
			}

			@Override
			public BooleanBuilder getPreFilter() {
				if (pesquisaHistorico.getObjetoPesquisado() == null) {

					throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_HISTORICO);
				}

				switch (historico) {
				case CREDOR:
					return planoContasService.getContasBalanco();
				case DEVEDOR:
					return planoContasService.getContasDevedoras();
				default:
					throw new SysDescException(MensagemConstants.MENSAGEM_HISTORICO_OPERACAO_NAO_ENCONTRADO);

				}

			}
		};
		painelContent.add(pesquisaContaDevedora, "cell 0 5,grow");

		lblFormaDePagamento_1 = new JLabel("Forma de Pagamento:");
		painelContent.add(lblFormaDePagamento_1, "cell 0 6");

		painelContent.add(pesquisaFormasPagamento, "cell 0 7,grow");
		painelContent.add(lbContaCredora, "cell 0 8");
		painelContent.add(lbHistorico, "cell 0 2");
		painelContent.add(txCodigo, "cell 0 1,,width 50:100:100");
		painelContent.add(pesquisaHistorico, "cell 0 3,growx");
		painelContent.add(pesquisaHistoricoCredor, "cell 0 9,growx");

		painelBotoes = new PanelActions<OperacaoFinanceiro>(this, operacaoFinanceiroService,
				PesquisaEnum.PES_HISTORICOOPERACOES.getCodigoPesquisa()) {

			private static final long serialVersionUID = 1L;

			@Override
			public void carregarObjeto(OperacaoFinanceiro objeto) {
				txCodigo.setValue(objeto.getIdOperacaoFinanceiro());
				pesquisaHistorico.setValue(objeto.getHistorico());
				pesquisaFormasPagamento.setValue(objeto.getFormasPagamento());
				pesquisaHistoricoCredor.setValue(objeto.getContaCredora());
				pesquisaContaDevedora.setValue(objeto.getContaDevedora());
			}

			@Override
			public boolean preencherObjeto(OperacaoFinanceiro objetoPesquisa) {
				objetoPesquisa.setIdOperacaoFinanceiro(txCodigo.getValue());
				objetoPesquisa.setHistorico(pesquisaHistorico.getObjetoPesquisado());
				objetoPesquisa.setContaCredora(pesquisaHistoricoCredor.getObjetoPesquisado());
				objetoPesquisa.setContaDevedora(pesquisaContaDevedora.getObjetoPesquisado());
				objetoPesquisa.setFormasPagamento(pesquisaFormasPagamento.getObjetoPesquisado());

				return true;
			}
		};

		painelBotoes.addSaveListener(cidade -> txCodigo.setValue(cidade.getIdOperacaoFinanceiro()));

		painelContent.add(painelBotoes, "cell 0 10,growx");

	}

}
