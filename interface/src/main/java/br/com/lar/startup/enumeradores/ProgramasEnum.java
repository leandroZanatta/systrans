package br.com.lar.startup.enumeradores;

import java.util.HashMap;
import java.util.Map;

import br.com.lar.ui.FrmAberturaCaixa;
import br.com.lar.ui.FrmCadastroOperacoes;
import br.com.lar.ui.FrmCadastroPesquisa;
import br.com.lar.ui.FrmCaixa;
import br.com.lar.ui.FrmCentroCusto;
import br.com.lar.ui.FrmCidade;
import br.com.lar.ui.FrmCliente;
import br.com.lar.ui.FrmConsultarContasPagar;
import br.com.lar.ui.FrmConsultarContasReceber;
import br.com.lar.ui.FrmEstado;
import br.com.lar.ui.FrmFechamentoCaixa;
import br.com.lar.ui.FrmFormasPagamento;
import br.com.lar.ui.FrmFuncionario;
import br.com.lar.ui.FrmGrupo;
import br.com.lar.ui.FrmHistoricoCusto;
import br.com.lar.ui.FrmHistoricoOperacoes;
import br.com.lar.ui.FrmLancamentoEntradas;
import br.com.lar.ui.FrmLancamentoSaidas;
import br.com.lar.ui.FrmMotorista;
import br.com.lar.ui.FrmPerfil;
import br.com.lar.ui.FrmPermissoes;
import br.com.lar.ui.FrmPlanoContas;
import br.com.lar.ui.FrmResumoCaixa;
import br.com.lar.ui.FrmUsuario;
import br.com.lar.ui.FrmVeiculo;
import br.com.lar.ui.relatorios.FrmRelatorioContasPagar;
import br.com.lar.ui.relatorios.FrmRelatorioContasReceber;
import br.com.lar.ui.relatorios.FrmRelatorioDiario;
import br.com.sysdesc.components.AbstractInternalFrame;

public enum ProgramasEnum {

	CADASTRO_PERFIS(2L, FrmPerfil.class),

	CADASTRO_ESTADOS(3L, FrmEstado.class),

	CADASTRO_CIDADE(4L, FrmCidade.class),

	CADASTRO_CLIENTES(5L, FrmCliente.class),

	CADASTRO_PESQUISA(8L, FrmCadastroPesquisa.class),

	CADASTRO_USUARIOS(9L, FrmUsuario.class),

	CADASTRO_PERMISSOES(10L, FrmPermissoes.class),

	CADASTRO_VEICULOS(11L, FrmVeiculo.class),

	CADASTRO_MOTORISTAS(12L, FrmMotorista.class),

	CADASTRO_FUNCIONARIOS(13L, FrmFuncionario.class),

	CADASTRO_GRUPOS(14L, FrmGrupo.class),

	CADASTRO_FORMAS_PAGAMENTO(19L, FrmFormasPagamento.class),

	CADASTRO_PLANO_CONTAS(23L, FrmPlanoContas.class),

	HISTORICO_OPERACOES(30L, FrmCadastroOperacoes.class),

	PAGAMENTO_OPERACOES(31L, FrmHistoricoOperacoes.class),

	HISTORICO_CUSTOS(40L, FrmHistoricoCusto.class),

	LANCAMENTOS_ENTRADAS(41L, FrmLancamentoEntradas.class),

	LANCAMENTOS_SAIDAS(42L, FrmLancamentoSaidas.class),

	ABERTURA_CAIXA(25L, FrmAberturaCaixa.class),

	CONSULTA_CONTAS_RECEBER(27L, FrmConsultarContasReceber.class),

	FECHAMENTO_CAIXA(33L, FrmFechamentoCaixa.class),

	RESUMO_CAIXA(34L, FrmResumoCaixa.class),

	CADASTRO_CAIXA(35L, FrmCaixa.class),

	CONSULTA_CONTAS_PAGAR(38L, FrmConsultarContasPagar.class),

	CADASTRO_CENTRO_CUSTOS(39L, FrmCentroCusto.class),

	RELATORIO_DIARIO(45L, FrmRelatorioDiario.class),

	RELATORIO_DESPEZAS(47L, FrmRelatorioDiario.class),

	RELATORIO_RECEITAS(51L, FrmRelatorioDiario.class),

	RELATORIO_CONTAS_PAGAR(49L, FrmRelatorioContasPagar.class),

	RELATORIO_CONTAS_RECEBER(50L, FrmRelatorioContasReceber.class);

	private static Map<Long, ProgramasEnum> mapa = new HashMap<>();

	static {

		for (ProgramasEnum programa : ProgramasEnum.values()) {
			mapa.put(programa.getCodigo(), programa);
		}
	}

	private final Long codigo;

	private final Class<? extends AbstractInternalFrame> internalFrame;

	ProgramasEnum(Long codigo, Class<? extends AbstractInternalFrame> internalFrame) {
		this.codigo = codigo;
		this.internalFrame = internalFrame;
	}

	public Long getCodigo() {
		return codigo;
	}

	public Class<? extends AbstractInternalFrame> getInternalFrame() {
		return internalFrame;
	}

	public static ProgramasEnum findByCodigo(Long codigoPrograma) {
		return mapa.get(codigoPrograma);
	}
}
