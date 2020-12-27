package br.com.lar.ui;

import java.util.Arrays;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.mysema.query.BooleanBuilder;

import br.com.lar.repository.model.FormasPagamento;
import br.com.lar.repository.model.Historico;
import br.com.lar.repository.model.ParametroOperacaoFinanceira;
import br.com.lar.repository.model.PlanoContas;
import br.com.lar.service.formaspagamento.FormasPagamentoService;
import br.com.lar.service.historico.HistoricoService;
import br.com.lar.service.operacao.ParametroOperacaoFinanceiraService;
import br.com.lar.service.planocontas.PlanoContasService;
import br.com.lar.startup.enumeradores.PesquisaEnum;
import br.com.sysdesc.components.AbstractInternalFrame;
import br.com.sysdesc.components.JTextFieldId;
import br.com.sysdesc.pesquisa.ui.components.CampoPesquisa;
import br.com.sysdesc.pesquisa.ui.components.PanelActions;
import br.com.sysdesc.util.exception.SysDescException;
import br.com.systrans.util.constants.MensagemConstants;
import br.com.systrans.util.enumeradores.TipoContaEnum;
import br.com.systrans.util.enumeradores.TipoHistoricoOperacaoEnum;
import net.miginfocom.swing.MigLayout;

public class FrmParametroOperacaoFinanceira extends AbstractInternalFrame {

	private static final String TEMPLATE_PESQUISA = "%d - %s";

	private static final long serialVersionUID = 1L;

	private JPanel painelContent;
	private JTextFieldId txCodigo;
	private CampoPesquisa<Historico> pesquisaHistorico;
	private CampoPesquisa<PlanoContas> pesquisaContaDevedora;
	private HistoricoService historicoService = new HistoricoService();
	private FormasPagamentoService formasPagamentoService = new FormasPagamentoService();
	private PlanoContasService planoContasService = new PlanoContasService();
	private ParametroOperacaoFinanceiraService parametroOperacaoFinanceiraService = new ParametroOperacaoFinanceiraService();
	private CampoPesquisa<FormasPagamento> pesquisaFormasPagamento;
	private final TipoHistoricoOperacaoEnum historico;
	private CampoPesquisa<PlanoContas> pesquisaContaCredito;
	private JComboBox<TipoContaEnum> cbTipoConta;
	private PanelActions<ParametroOperacaoFinanceira> panelActions;

	public FrmParametroOperacaoFinanceira(Long permissaoPrograma, Long codigoUsuario, TipoHistoricoOperacaoEnum historico) {
		super(permissaoPrograma, codigoUsuario);
		this.historico = historico;

		initComponents();
	}

	private void initComponents() {

		setSize(500, 360);
		setClosable(Boolean.TRUE);
		setTitle("CADASTRO DE OPERAÇÕES FINANCEIRAS");

		painelContent = new JPanel();
		JLabel lbCodigo = new JLabel("Código:");
		JLabel lbHistorico = new JLabel("Histórico:");
		JLabel lbContaCredora = new JLabel("Conta Débito:");
		JLabel lblContaResultado = new JLabel("Conta Crédito:");
		JLabel lblTipoConta = new JLabel("Tipo Conta:");
		JLabel lblFormaDePagamento = new JLabel("Forma de Pagamento:");

		txCodigo = new JTextFieldId();
		cbTipoConta = new JComboBox<>();

		pesquisaHistorico = new CampoPesquisa<Historico>(historicoService, PesquisaEnum.PES_HISTORICOOPERACOES.getCodigoPesquisa(),
				getCodigoUsuario()) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(Historico objeto) {

				return String.format(TEMPLATE_PESQUISA, objeto.getIdHistorico(), objeto.getDescricao());
			}

			@Override
			public BooleanBuilder getPreFilter() {

				if (getTipoConta().equals(TipoContaEnum.DESCONTOS)) {

					return historico.equals(TipoHistoricoOperacaoEnum.DEVEDOR) ? historicoService.getHistoricosCredores()
							: historicoService.getHistoricosDevedores();
				}

				return historico.equals(TipoHistoricoOperacaoEnum.DEVEDOR) ? historicoService.getHistoricosDevedores()
						: historicoService.getHistoricosCredores();
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

		pesquisaContaCredito = new CampoPesquisa<PlanoContas>(planoContasService, PesquisaEnum.PES_PLANOCONTAS.getCodigoPesquisa(),
				getCodigoUsuario()) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(PlanoContas objeto) {
				return String.format(TEMPLATE_PESQUISA, objeto.getIdPlanoContas(), objeto.getDescricao());
			}

			@Override
			public BooleanBuilder getPreFilter() {

				if (getTipoConta().equals(TipoContaEnum.DESCONTOS)) {

					return historico.equals(TipoHistoricoOperacaoEnum.DEVEDOR) ? planoContasService.getContasCredoras()
							: planoContasService.getContasBalanco();
				}

				return historico.equals(TipoHistoricoOperacaoEnum.DEVEDOR) ? planoContasService.getContasBalanco()
						: planoContasService.getContasCredoras();
			}
		};

		pesquisaContaDevedora = new CampoPesquisa<PlanoContas>(planoContasService, PesquisaEnum.PES_PLANOCONTAS.getCodigoPesquisa(),
				getCodigoUsuario()) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(PlanoContas objeto) {
				return String.format(TEMPLATE_PESQUISA, objeto.getIdPlanoContas(), objeto.getDescricao());
			}

			@Override
			public BooleanBuilder getPreFilter() {

				if (getTipoConta().equals(TipoContaEnum.DESCONTOS)) {

					return historico.equals(TipoHistoricoOperacaoEnum.CREDOR) ? planoContasService.getContasCredoras()
							: planoContasService.getContasBalanco();
				}

				return historico.equals(TipoHistoricoOperacaoEnum.DEVEDOR) ? planoContasService.getContasDevedoras()
						: planoContasService.getContasBalanco();
			}
		};

		Arrays.asList(TipoContaEnum.values()).forEach(cbTipoConta::addItem);
		cbTipoConta.setSelectedIndex(-1);

		painelContent.setLayout(new MigLayout("", "[grow]", "[][][][][][][][][][][][][grow]"));
		getContentPane().add(painelContent);

		painelContent.add(lbCodigo, "cell 0 0");
		painelContent.add(lblTipoConta, "cell 0 2");
		painelContent.add(lblFormaDePagamento, "cell 0 6");
		painelContent.add(lblContaResultado, "cell 0 8");
		painelContent.add(lbContaCredora, "cell 0 10");
		painelContent.add(lbHistorico, "cell 0 4");

		painelContent.add(cbTipoConta, "cell 0 3,growx");
		painelContent.add(pesquisaFormasPagamento, "cell 0 7,grow");
		painelContent.add(pesquisaContaCredito, "cell 0 9,grow");
		painelContent.add(txCodigo, "cell 0 1,,width 50:100:100");
		painelContent.add(pesquisaHistorico, "cell 0 5,growx");
		painelContent.add(pesquisaContaDevedora, "cell 0 11,growx");

		panelActions = new PanelActions<ParametroOperacaoFinanceira>(this, parametroOperacaoFinanceiraService,
				PesquisaEnum.PES_PARAMETRO_OPERACAO_FINANCEIRA.getCodigoPesquisa()) {

			private static final long serialVersionUID = 1L;

			@Override
			public void carregarObjeto(ParametroOperacaoFinanceira objeto) {

				txCodigo.setValue(objeto.getIdParametroOperacaoFinanceiro());
				cbTipoConta.setSelectedItem(TipoContaEnum.findByCodigo(objeto.getCodigoTipoConta()));
				pesquisaHistorico.setValue(objeto.getHistorico());
				pesquisaFormasPagamento.setValue(objeto.getFormasPagamento());
				pesquisaContaCredito.setValue(objeto.getContaCredora());
				pesquisaContaDevedora.setValue(objeto.getContaDevedora());

			}

			@Override
			public boolean preencherObjeto(ParametroOperacaoFinanceira objetoPesquisa) {

				objetoPesquisa.setCodigoTipoFinanceiro(historico.getCodigo());
				objetoPesquisa.setCodigoTipoConta(getTipoConta().getCodigo());
				objetoPesquisa.setHistorico(pesquisaHistorico.getObjetoPesquisado());
				objetoPesquisa.setFormasPagamento(pesquisaFormasPagamento.getObjetoPesquisado());
				objetoPesquisa.setContaCredora(pesquisaContaCredito.getObjetoPesquisado());
				objetoPesquisa.setContaDevedora(pesquisaContaDevedora.getObjetoPesquisado());

				return true;
			}
		};

		panelActions.setPreFilter(parametroOperacaoFinanceiraService.filtrarTipoConta(historico.getCodigo()));
		panelActions.addSaveListener(objeto -> txCodigo.setValue(objeto.getIdParametroOperacaoFinanceiro()));
		painelContent.add(panelActions, "cell 0 12,grow");

	}

	private TipoContaEnum getTipoConta() {

		if (cbTipoConta.getSelectedIndex() < 0) {

			throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_TIPO_CONTA);
		}

		return (TipoContaEnum) cbTipoConta.getSelectedItem();
	}

}
