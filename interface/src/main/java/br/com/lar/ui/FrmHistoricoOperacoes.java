package br.com.lar.ui;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.mysema.query.BooleanBuilder;

import br.com.lar.repository.model.FormasPagamento;
import br.com.lar.repository.model.Historico;
import br.com.lar.repository.model.Operacao;
import br.com.lar.repository.model.PlanoContas;
import br.com.lar.service.formaspagamento.FormasPagamentoService;
import br.com.lar.service.historico.HistoricoService;
import br.com.lar.service.operacao.OperacaoService;
import br.com.lar.service.planocontas.PlanoContasService;
import br.com.lar.startup.enumeradores.PesquisaEnum;
import br.com.lar.startup.enumeradores.TipoHistoricoOperacaoEnum;
import br.com.sysdesc.components.AbstractInternalFrame;
import br.com.sysdesc.components.JTextFieldId;
import br.com.sysdesc.components.JTextFieldMaiusculo;
import br.com.sysdesc.pesquisa.ui.components.CampoPesquisa;
import br.com.sysdesc.pesquisa.ui.components.PanelActions;
import br.com.sysdesc.util.constants.MensagemConstants;
import br.com.sysdesc.util.exception.SysDescException;
import br.com.sysdesc.util.resources.Resources;
import net.miginfocom.swing.MigLayout;

public class FrmHistoricoOperacoes extends AbstractInternalFrame {

	private static final String TEMPLATE_PESQUISA = "%d - %s";

	private static final long serialVersionUID = 1L;

	private JPanel painelContent;
	private PanelActions<Operacao> painelBotoes;
	private JLabel lbCodigo;
	private JLabel lblFormaDePagamento;
	private JLabel lblOperao;
	private JLabel lbContaCredora;
	private JLabel lbContaDevedora;
	private JLabel lblDescrio;
	private JTextFieldId txCodigo;
	private CampoPesquisa<Historico> pesquisaHistorico;
	private CampoPesquisa<FormasPagamento> pesquisaFormasPagamento;
	private CampoPesquisa<PlanoContas> pesquisaHistoricoCredor;
	private CampoPesquisa<PlanoContas> pesquisaHistoricoDevedor;
	private HistoricoService historicoService = new HistoricoService();
	private FormasPagamentoService formasPagamentoService = new FormasPagamentoService();
	private PlanoContasService planoContasService = new PlanoContasService();
	private OperacaoService operacaoService = new OperacaoService();
	private JTextFieldMaiusculo txDescricao;

	public FrmHistoricoOperacoes(Long permissaoPrograma, Long codigoUsuario) {
		super(permissaoPrograma, codigoUsuario);

		initComponents();
	}

	private void initComponents() {

		setSize(500, 360);
		setClosable(Boolean.TRUE);
		setTitle("CADASTRO DE HISTORICO DE OPERAÇÕES");

		painelContent = new JPanel();
		lbCodigo = new JLabel("Código:");
		lblOperao = new JLabel("Operação:");
		lbContaCredora = new JLabel("Conta Credora:");
		lblFormaDePagamento = new JLabel("Forma de Pagamento:");
		lbContaDevedora = new JLabel("Conta Devedora:");
		lblDescrio = new JLabel("Descrição:");
		txDescricao = new JTextFieldMaiusculo();
		txCodigo = new JTextFieldId();

		pesquisaHistorico = new CampoPesquisa<Historico>(historicoService, PesquisaEnum.PES_HISTORICOOPERACOES.getCodigoPesquisa(),
				getCodigoUsuario()) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(Historico objeto) {
				return String.format(TEMPLATE_PESQUISA, objeto.getIdHistorico(), objeto.getDescricao());
			}
		};

		pesquisaFormasPagamento = new CampoPesquisa<FormasPagamento>(formasPagamentoService, PesquisaEnum.PES_FORMAS_PAGAMENTO.getCodigoPesquisa(),
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
					return planoContasService.getContasDevedoras();
				default:
					throw new SysDescException(MensagemConstants.MENSAGEM_HISTORICO_OPERACAO_NAO_ENCONTRADO);

				}

			}
		};
		pesquisaHistoricoDevedor = new CampoPesquisa<PlanoContas>(planoContasService, PesquisaEnum.PES_PLANOCONTAS.getCodigoPesquisa(),
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

				if (pesquisaFormasPagamento.getObjetoPesquisado() == null) {

					throw new SysDescException(Resources.translate(MensagemConstants.MENSAGEM_SELECIONE_FORMA_PAGAMAMENTO));
				}

				return planoContasService.getContasBalanco();
			}
		};

		painelContent.setLayout(new MigLayout("", "[grow]", "[][][][][][][][][][][][][grow]"));
		getContentPane().add(painelContent);

		painelContent.add(lbCodigo, "cell 0 0");
		painelContent.add(lblFormaDePagamento, "cell 0 6");
		painelContent.add(lblDescrio, "cell 0 2");
		painelContent.add(lbContaCredora, "cell 0 8");
		painelContent.add(lbContaDevedora, "cell 0 10");
		painelContent.add(lblOperao, "cell 0 4");
		painelContent.add(txCodigo, "cell 0 1,,width 50:100:100");
		painelContent.add(txDescricao, "cell 0 3,growx");
		painelContent.add(pesquisaHistorico, "cell 0 5,growx");
		painelContent.add(pesquisaFormasPagamento, "cell 0 7,growx");
		painelContent.add(pesquisaHistoricoCredor, "cell 0 9,growx");
		painelContent.add(pesquisaHistoricoDevedor, "cell 0 11,growx");

		painelBotoes = new PanelActions<Operacao>(this, operacaoService, PesquisaEnum.PES_OPERACOES.getCodigoPesquisa()) {

			private static final long serialVersionUID = 1L;

			@Override
			public void carregarObjeto(Operacao objeto) {
				txCodigo.setValue(objeto.getIdOperacao());
				pesquisaHistorico.setValue(objeto.getHistorico());
				pesquisaFormasPagamento.setValue(objeto.getFormasPagamento());
				txDescricao.setText(objeto.getDescricao());
				pesquisaHistoricoCredor.setValue(objeto.getContaCredora());
				pesquisaHistoricoDevedor.setValue(objeto.getContaDevedora());
			}

			@Override
			public boolean preencherObjeto(Operacao objetoPesquisa) {
				objetoPesquisa.setIdOperacao(txCodigo.getValue());
				objetoPesquisa.setFormasPagamento(pesquisaFormasPagamento.getObjetoPesquisado());
				objetoPesquisa.setHistorico(pesquisaHistorico.getObjetoPesquisado());
				objetoPesquisa.setDescricao(txDescricao.getText());
				objetoPesquisa.setContaCredora(pesquisaHistoricoCredor.getObjetoPesquisado());
				objetoPesquisa.setContaDevedora(pesquisaHistoricoDevedor.getObjetoPesquisado());

				return true;
			}
		};

		painelBotoes.addSaveListener(cidade -> txCodigo.setValue(cidade.getIdOperacao()));

		painelContent.add(painelBotoes, "cell 0 12,growx");

	}

}
