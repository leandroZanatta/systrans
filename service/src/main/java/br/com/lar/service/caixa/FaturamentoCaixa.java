package br.com.lar.service.caixa;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import br.com.lar.repository.dao.OperacaoDAO;
import br.com.lar.repository.model.CaixaCabecalho;
import br.com.lar.repository.model.CaixaDetalhe;
import br.com.lar.repository.model.ContasPagar;
import br.com.lar.repository.model.ContasReceber;
import br.com.lar.repository.model.Faturamento;
import br.com.lar.repository.model.FaturamentoPagamento;
import br.com.lar.repository.model.Historico;
import br.com.lar.repository.model.Operacao;
import lombok.AllArgsConstructor;
import lombok.Data;

public class FaturamentoCaixa {

	private OperacaoDAO operacaoDAO = new OperacaoDAO();

	public List<CaixaDetalhe> registrarCaixaContasReceber(ContasReceber contasReceber) {

		List<Long> codigoPagamentos = Arrays.asList(contasReceber.getFormasPagamento().getIdFormaPagamento());

		Map<Long, List<BigDecimal>> parcelas = new HashMap<>();
		parcelas.put(contasReceber.getFormasPagamento().getIdFormaPagamento(), Arrays.asList(contasReceber.getValorParcela()));

		return registrarCaixa(contasReceber.getHistorico(), codigoPagamentos, contasReceber.getCaixaCabecalho(), parcelas);
	}

	public List<CaixaDetalhe> registrarCaixaContasPagar(ContasPagar contasPagar) {
		List<Long> codigoPagamentos = Arrays.asList(contasPagar.getFormasPagamento().getIdFormaPagamento());

		Map<Long, List<BigDecimal>> parcelas = new HashMap<>();
		parcelas.put(contasPagar.getFormasPagamento().getIdFormaPagamento(), Arrays.asList(contasPagar.getValorParcela()));

		return registrarCaixa(contasPagar.getHistorico(), codigoPagamentos, contasPagar.getCaixaCabecalho(), parcelas);

	}

	public List<CaixaDetalhe> registrarCaixaFaturamento(Faturamento faturamento) {

		List<Long> codigoPagamentos = faturamento.getFaturamentoPagamentos().stream()
				.mapToLong(pagamento -> pagamento.getFormasPagamento().getIdFormaPagamento()).distinct().boxed().collect(Collectors.toList());

		Map<Long, List<BigDecimal>> parcelas = faturamento.getFaturamentoPagamentos().stream()
				.collect(Collectors.groupingBy(pagamento -> pagamento.getFormasPagamento().getIdFormaPagamento(),
						Collectors.mapping(FaturamentoPagamento::getValorParcela, Collectors.toList())));

		return registrarCaixa(faturamento.getHistorico(), codigoPagamentos, faturamento.getCaixaCabecalho(), parcelas);
	}

	public List<CaixaDetalhe> registrarCaixa(Historico historico, List<Long> codigoPagamentos, CaixaCabecalho caixaCabecalho,
			Map<Long, List<BigDecimal>> parcelasPagamento) {

		List<CaixaDetalhe> caixaDetalhes = new ArrayList<>();

		List<Operacao> operacoes = operacaoDAO.buscarOperacao(historico.getIdHistorico(), codigoPagamentos);

		operacoes.forEach(operacao -> {

			CaixaDetalhe caixaDetalhe = new CaixaDetalhe();
			caixaDetalhe.setCaixaCabecalho(caixaCabecalho);
			caixaDetalhe.setDataMovimento(new Date());
			caixaDetalhe.setTipoSaldo(operacao.getHistorico().getTipoHistorico());
			caixaDetalhe.setPlanoContas(operacao.getContaDevedora());
			caixaDetalhe.setValorDetalhe(somarParcelasPorFormaPagamento(parcelasPagamento, operacao));

			caixaDetalhes.add(caixaDetalhe);
		});

		return caixaDetalhes;
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
