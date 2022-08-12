package br.com.lar.service.abastecimento;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import br.com.lar.repository.dao.AbastecimentoVeiculoDAO;
import br.com.lar.repository.model.AbastecimentoMediaVeiculo;
import br.com.lar.repository.model.AbastecimentoVeiculo;
import br.com.lar.repository.model.FaturamentoEntradaPagamentos;
import br.com.lar.repository.model.FaturamentoEntradasCabecalho;
import br.com.lar.repository.model.FaturamentoEntradasDetalhe;
import br.com.lar.service.caixa.CaixaCabecalhoService;
import br.com.lar.service.faturamento.FaturamentoEntradaService;
import br.com.sysdesc.pesquisa.service.impl.AbstractPesquisableServiceImpl;
import br.com.sysdesc.util.classes.BigDecimalUtil;
import br.com.sysdesc.util.classes.IfNull;
import br.com.sysdesc.util.classes.LongUtil;
import br.com.sysdesc.util.exception.SysDescException;
import br.com.systrans.util.constants.MensagemConstants;

public class AbastecimentoVeiculoService extends AbstractPesquisableServiceImpl<AbastecimentoVeiculo> {

	private AbastecimentoVeiculoDAO abastecimentoVeiculoDAO;
	private FaturamentoEntradaService faturamentoEntradaService = new FaturamentoEntradaService();
	private CaixaCabecalhoService caixaCabecalhoService = new CaixaCabecalhoService();

	public AbastecimentoVeiculoService() {
		this(new AbastecimentoVeiculoDAO());
	}

	public AbastecimentoVeiculoService(AbastecimentoVeiculoDAO abastecimentoVeiculoDAO) {
		super(abastecimentoVeiculoDAO, AbastecimentoVeiculo::getIdAbastecimentoVeiculo);

		this.abastecimentoVeiculoDAO = abastecimentoVeiculoDAO;
	}

	public Long buscarUltimaKilometragemVeiculo(Long codigoVeiculo) {

		return IfNull.get(abastecimentoVeiculoDAO.buscarUltimaKilometragemVeiculo(codigoVeiculo), 0L);
	}

	@Override
	public void validar(AbastecimentoVeiculo objetoPersistir) {

		if (objetoPersistir.getConfiguracaoAbastecimento() == null) {
			throw new SysDescException(MensagemConstants.SELECIONE_CONFIGURACAO_ABASTECIMENTO);
		}

		if (LongUtil.isNullOrZero(objetoPersistir.getKilometroInicial())) {
			throw new SysDescException(MensagemConstants.INSIRA_KILOMETRAGEM_INICIAL);
		}

		if (LongUtil.isNullOrZero(objetoPersistir.getKilometroFinal())) {
			throw new SysDescException(MensagemConstants.INSIRA_KILOMETRAGEM_FINAL);
		}

		if (LongUtil.maior(objetoPersistir.getKilometroInicial(), objetoPersistir.getKilometroFinal())) {

			throw new SysDescException(MensagemConstants.KILOMETRAGEM_FINAL_MENOR_INICIAL);
		}

		if (BigDecimalUtil.isNullOrZero(objetoPersistir.getLitrosAbastecidos())) {

			throw new SysDescException(MensagemConstants.INSIRA_LITROS_ABASTECIDOS);
		}

		if (BigDecimalUtil.isNullOrZero(objetoPersistir.getValorAbastecimento())) {

			throw new SysDescException(MensagemConstants.INSIRA_VALOR_ABASTECIDO);
		}

	}

	@Override
	public void salvar(AbastecimentoVeiculo objetoPersistir) {

		this.gerarFaturamentoEntrada(abastecimentoVeiculoDAO.getEntityManager(), objetoPersistir);
	}

	private void gerarFaturamentoEntrada(EntityManager em, AbastecimentoVeiculo objetoPersistir) {

		caixaCabecalhoService.validarExistenciaCaixaAberto(objetoPersistir.getUsuario());

		Date dataMovimento = new Date();

		FaturamentoEntradasCabecalho faturamentoEntradasCabecalho = new FaturamentoEntradasCabecalho();
		faturamentoEntradasCabecalho.setHistorico(objetoPersistir.getConfiguracaoAbastecimento().getOperacao().getHistorico());
		faturamentoEntradasCabecalho.setValorBruto(objetoPersistir.getValorAbastecimento());
		faturamentoEntradasCabecalho.setDataMovimento(dataMovimento);
		faturamentoEntradasCabecalho.setCentroCusto(objetoPersistir.getConfiguracaoAbastecimento().getCentroCusto());
		faturamentoEntradasCabecalho.setCaixaCabecalho(caixaCabecalhoService.obterCaixaDoDia(objetoPersistir.getUsuario()));
		faturamentoEntradasCabecalho.setCliente(objetoPersistir.getCliente());

		faturamentoEntradasCabecalho.setFaturamentoEntradaPagamentos(new ArrayList<>());

		objetoPersistir.getAbastecimentoPagamentos().forEach(abastecimentoPagamento -> {

			FaturamentoEntradaPagamentos faturamentoEntradaPagamentos = new FaturamentoEntradaPagamentos();
			faturamentoEntradaPagamentos.setDataLancamento(abastecimentoPagamento.getDataLancamento());
			faturamentoEntradaPagamentos.setDataVencimento(abastecimentoPagamento.getDataVencimento());
			faturamentoEntradaPagamentos.setValorParcela(abastecimentoPagamento.getValorParcela());
			faturamentoEntradaPagamentos.setNumeroParcela(abastecimentoPagamento.getNumeroParcela());
			faturamentoEntradaPagamentos.setFormasPagamento(objetoPersistir.getConfiguracaoAbastecimento().getOperacao().getFormasPagamento());

			faturamentoEntradaPagamentos.setFaturamentoEntradasCabecalho(faturamentoEntradasCabecalho);

			faturamentoEntradasCabecalho.getFaturamentoEntradaPagamentos().add(faturamentoEntradaPagamentos);
		});

		FaturamentoEntradasDetalhe faturamentoEntradasDetalhe = new FaturamentoEntradasDetalhe();
		faturamentoEntradasDetalhe.setVeiculo(objetoPersistir.getConfiguracaoAbastecimento().getVeiculo());
		faturamentoEntradasDetalhe.setFaturamentoEntradasCabecalho(faturamentoEntradasCabecalho);
		faturamentoEntradasDetalhe.setMotorista(objetoPersistir.getConfiguracaoAbastecimento().getVeiculo().getMotorista());
		faturamentoEntradasDetalhe.setNumeroDocumento(objetoPersistir.getNumeroDocumento());
		faturamentoEntradasDetalhe.setValorBruto(objetoPersistir.getValorAbastecimento());
		faturamentoEntradasDetalhe.setValorAcrescimo(BigDecimal.ZERO);
		faturamentoEntradasDetalhe.setValorDesconto(BigDecimal.ZERO);

		faturamentoEntradasCabecalho.setFaturamentoEntradasDetalhes(Arrays.asList(faturamentoEntradasDetalhe));

		faturamentoEntradaService.validar(faturamentoEntradasCabecalho);

		AbastecimentoMediaVeiculo mediaAbastecimento = calcularConsumo(objetoPersistir);

		em.getTransaction().begin();

		faturamentoEntradaService.integrarFaturamento(em, faturamentoEntradasCabecalho);

		if (mediaAbastecimento != null) {

			em.persist(mediaAbastecimento);
		}

		em.persist(objetoPersistir);

		em.getTransaction().commit();
	}

	private AbastecimentoMediaVeiculo calcularConsumo(AbastecimentoVeiculo objetoPersistir) {

		if (objetoPersistir.getAbastecimentoParcial()) {

			return null;
		}

		List<AbastecimentoVeiculo> abastecimentosAnteriores = abastecimentoVeiculoDAO
				.buscarAbastecimentosUltimoTanqueCompleto(objetoPersistir.getConfiguracaoAbastecimento().getVeiculo().getIdVeiculo());

		if (abastecimentosAnteriores == null) {

			return null;
		}

		abastecimentosAnteriores.add(objetoPersistir);

		BigDecimal litrosAbastecidos = BigDecimal.ZERO;
		BigDecimal valorAbastecido = BigDecimal.ZERO;
		Long kmRodados = 0L;

		for (AbastecimentoVeiculo abastecimento : abastecimentosAnteriores) {

			litrosAbastecidos = litrosAbastecidos.add(abastecimento.getLitrosAbastecidos());
			valorAbastecido = valorAbastecido.add(abastecimento.getValorAbastecimento());
			kmRodados = kmRodados + (abastecimento.getKilometroFinal() - abastecimento.getKilometroInicial());
		}

		AbastecimentoMediaVeiculo abastecimentoMediaVeiculo = new AbastecimentoMediaVeiculo();
		abastecimentoMediaVeiculo.setVeiculo(objetoPersistir.getConfiguracaoAbastecimento().getVeiculo());
		abastecimentoMediaVeiculo.setDataCalculo(new Date());
		abastecimentoMediaVeiculo.setLitrosAbastecidos(litrosAbastecidos);
		abastecimentoMediaVeiculo.setValorAbastecimento(valorAbastecido);
		abastecimentoMediaVeiculo.setQuantidadeKilometros(kmRodados);
		abastecimentoMediaVeiculo.setKilometrosPorLitro(BigDecimal.valueOf(kmRodados).divide(litrosAbastecidos, 8, RoundingMode.HALF_EVEN));
		abastecimentoMediaVeiculo.setReaisPorKilometro(valorAbastecido.divide(BigDecimal.valueOf(kmRodados), 8, RoundingMode.HALF_EVEN));

		return abastecimentoMediaVeiculo;
	}
}
