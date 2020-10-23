package br.com.lar.service.contasreceber;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import br.com.lar.repository.model.ContasReceber;
import br.com.lar.repository.model.Faturamento;
import br.com.lar.repository.model.FaturamentoPagamento;
import br.com.sysdesc.util.enumeradores.TipoStatusEnum;

public class FaturamentoContasReceber {

	public List<ContasReceber> registrarContasReceber(Faturamento faturamento) {

		List<ContasReceber> contasRecebers = new ArrayList<>();

		List<FaturamentoPagamento> faturamentoAPrazo = faturamento.getFaturamentoPagamentos().stream()
				.filter(pagamento -> pagamento.getFormasPagamento().isFlagPermitePagamentoPrazo())
				.collect(Collectors.toList());

		faturamentoAPrazo.forEach(pagamento -> {

			ContasReceber contasReceber = new ContasReceber();
			contasReceber.setBaixado(false);
			contasReceber.setCliente(faturamento.getCliente());
			contasReceber.setCodigoStatus(TipoStatusEnum.ATIVO.getCodigo());
			contasReceber.setDataCadastro(new Date());
			contasReceber.setDataManutencao(new Date());
			contasReceber.setDataMovimento(pagamento.getDataLancamento());
			contasReceber.setDataVencimento(pagamento.getDataVencimento());
			contasReceber.setDocumento(faturamento.getNumeroDocumento());
			contasReceber.setFormasPagamento(pagamento.getFormasPagamento());
			contasReceber.setHistorico(faturamento.getHistorico());

			if (faturamento.getFaturamentoTransporte() != null) {
				contasReceber.setMotorista(faturamento.getFaturamentoTransporte().getMotorista());
				contasReceber.setVeiculo(faturamento.getFaturamentoTransporte().getVeiculo());
			}
			contasReceber.setValorAcrescimo(BigDecimal.ZERO);
			contasReceber.setValorDesconto(BigDecimal.ZERO);
			contasReceber.setValorJuros(BigDecimal.ZERO);
			contasReceber.setValorPago(BigDecimal.ZERO);
			contasReceber.setValorParcela(pagamento.getValorParcela());

			contasRecebers.add(contasReceber);
		});

		return contasRecebers;
	}

}
