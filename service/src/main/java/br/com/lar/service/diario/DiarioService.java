package br.com.lar.service.diario;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import br.com.lar.repository.dao.DiarioCabecalhoDAO;
import br.com.lar.repository.dao.OperacaoDAO;
import br.com.lar.repository.model.CaixaCabecalho;
import br.com.lar.repository.model.ContasReceber;
import br.com.lar.repository.model.DiarioCabecalho;
import br.com.lar.repository.model.DiarioDetalhe;
import br.com.lar.repository.model.Faturamento;
import br.com.lar.repository.model.FaturamentoPagamento;
import br.com.lar.repository.model.Historico;
import br.com.lar.repository.model.Operacao;
import br.com.lar.repository.projection.ResumoCaixaMovimentoProjection;
import br.com.sysdesc.util.exception.SysDescException;
import br.com.systrans.util.constants.MensagemConstants;
import br.com.systrans.util.enumeradores.TipoHistoricoOperacaoEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

public class DiarioService {

	private OperacaoDAO operacaoDAO = new OperacaoDAO();
	private DiarioCabecalhoDAO diarioCabecalhoDAO = new DiarioCabecalhoDAO();

	public DiarioCabecalho registrarDiarioContasReceber(ContasReceber contasReceber) {

		List<Long> codigoPagamentos = Arrays.asList(contasReceber.getFormasPagamento().getIdFormaPagamento());

		Map<Long, List<BigDecimal>> parcelas = new HashMap<>();
		parcelas.put(contasReceber.getFormasPagamento().getIdFormaPagamento(), Arrays.asList(contasReceber.getValorParcela()));

		return registrarDiario(contasReceber.getHistorico(), contasReceber.getCaixaCabecalho(), codigoPagamentos, parcelas,
				contasReceber.getValorParcela());
	}

	public DiarioCabecalho registrarDiarioFaturamento(Faturamento faturamento) {

		List<Long> codigoPagamentos = faturamento.getFaturamentoPagamentos().stream()
				.mapToLong(pagamento -> pagamento.getFormasPagamento().getIdFormaPagamento()).distinct().boxed().collect(Collectors.toList());

		Map<Long, List<BigDecimal>> parcelas = faturamento.getFaturamentoPagamentos().stream()
				.collect(Collectors.groupingBy(pagamento -> pagamento.getFormasPagamento().getIdFormaPagamento(),
						Collectors.mapping(FaturamentoPagamento::getValorParcela, Collectors.toList())));

		BigDecimal valorPagamentos = faturamento.getValorBruto().add(faturamento.getValorAcrescimo()).subtract(faturamento.getValorDesconto());

		return registrarDiario(faturamento.getHistorico(), faturamento.getCaixaCabecalho(), codigoPagamentos, parcelas, valorPagamentos);
	}

	public List<ResumoCaixaMovimentoProjection> buscarResumoCaixa(Long codigoCaixaCabecalho) {

		return diarioCabecalhoDAO.buscarResumoCaixa(codigoCaixaCabecalho);
	}

	private DiarioCabecalho registrarDiario(Historico historico, CaixaCabecalho caixaCabecalho, List<Long> codigoPagamentos,
			Map<Long, List<BigDecimal>> parcelasPagamento,
			BigDecimal valorPagamento) {

		List<Operacao> operacoes = operacaoDAO.buscarOperacao(historico.getIdHistorico(), codigoPagamentos);

		Operacao operacaoCredito = operacoes.stream().findFirst()
				.orElseThrow(() -> new SysDescException(MensagemConstants.MENSAGEM_HISTORICO_OPERACAO_NAO_ENCONTRADO));

		DiarioCabecalho diarioCabecalho = new DiarioCabecalho();
		diarioCabecalho.setDataMovimento(new Date());
		diarioCabecalho.setHistorico(historico);
		diarioCabecalho.setDiarioDetalhes(new ArrayList<>());
		diarioCabecalho.setCaixaCabecalho(caixaCabecalho);

		DiarioDetalhe diarioCredito = new DiarioDetalhe();
		diarioCredito.setDiarioCabecalho(diarioCabecalho);
		diarioCredito.setPlanoContas(operacaoCredito.getContaCredora());
		diarioCredito.setTipoSaldo(TipoHistoricoOperacaoEnum.CREDOR.getCodigo());
		diarioCredito.setValorDetalhe(valorPagamento);
		diarioCabecalho.getDiarioDetalhes().add(diarioCredito);

		operacoes.forEach(operacao -> {

			DiarioDetalhe diarioDebito = new DiarioDetalhe();
			diarioDebito.setDiarioCabecalho(diarioCabecalho);
			diarioDebito.setPlanoContas(operacao.getContaDevedora());
			diarioDebito.setTipoSaldo(TipoHistoricoOperacaoEnum.DEVEDOR.getCodigo());
			diarioDebito.setValorDetalhe(somarParcelasPorFormaPagamento(parcelasPagamento, operacao));

			diarioCabecalho.getDiarioDetalhes().add(diarioDebito);

		});

		return diarioCabecalho;
	}

	private BigDecimal somarParcelasPorFormaPagamento(Map<Long, List<BigDecimal>> pagamentos, Operacao operacao) {

		return pagamentos.get(operacao.getFormasPagamento().getIdFormaPagamento()).stream()
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	@Data
	@AllArgsConstructor
	class ParcelaPagamentoVO {

		private Long formaPagamento;

		private BigDecimal valorParcela;
	}

}
