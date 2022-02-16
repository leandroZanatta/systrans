package br.com.lar.service.sincronizacao;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import br.com.lar.repository.dao.FormasPagamentoDAO;
import br.com.lar.repository.model.FormasPagamento;
import br.com.systrans.util.dto.FormasPagamentoDTO;
import br.com.systrans.util.dto.SincronizacaoTabelaDTO;
import br.com.systrans.util.sincronizacao.ServerEndpoints;
import jakarta.ws.rs.client.Entity;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SincronizacaoFormasPagamento implements Callable<Boolean> {

	private Long versaoLocal;
	private Long versaoRemota;
	private FormasPagamentoDAO formasPagamentoDAO = new FormasPagamentoDAO();

	public SincronizacaoFormasPagamento(List<SincronizacaoTabelaDTO> local, List<SincronizacaoTabelaDTO> remota) {

		this.versaoLocal = local.stream().filter(sincronizacao -> sincronizacao.getCodigoTabela().equals(3L))
				.map(SincronizacaoTabelaDTO::getVersaoSincronizacao).findFirst().orElse(0L);
		this.versaoRemota = remota.stream().filter(sincronizacao -> sincronizacao.getCodigoTabela().equals(3L))
				.map(SincronizacaoTabelaDTO::getVersaoSincronizacao).findFirst().orElse(0L);
	}

	@Override
	public Boolean call() throws Exception {

		while (this.versaoRemota < this.versaoLocal) {

			try {

				List<FormasPagamento> formasPagamentoSincronizar = formasPagamentoDAO.obterPorVersao(versaoRemota, versaoLocal, 500L);

				Boolean retornoServer = ServerEndpoints.getinstance().formasPagamento
						.post(Entity
								.json(formasPagamentoSincronizar.stream()
										.map(formaPagamento -> new FormasPagamentoDTO(formaPagamento.getIdFormaPagamento(),
												formaPagamento.getDescricao(), formaPagamento.getSincronizacaoVersao()))
										.collect(Collectors.toList())),
								Boolean.class);

				if (retornoServer) {

					this.versaoRemota = formasPagamentoSincronizar.stream().mapToLong(FormasPagamento::getSincronizacaoVersao).max().getAsLong();
				}

			} catch (Exception e) {

				log.warn("Ocorreu um erro ao efetuar sincronização de formas de pagamento", e);
			}
		}

		return true;
	}

}
