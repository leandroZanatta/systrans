package br.com.lar.service.faturamento;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.swing.JOptionPane;

import br.com.lar.repository.dao.FaturamentoEntradaCabecalhoDAO;
import br.com.lar.repository.dao.HistoricoCustoDAO;
import br.com.lar.repository.model.AlocacaoCusto;
import br.com.lar.repository.model.CaixaCabecalho;
import br.com.lar.repository.model.CaixaSaldo;
import br.com.lar.repository.model.Cliente;
import br.com.lar.repository.model.ContasPagar;
import br.com.lar.repository.model.ContasPagarVeiculo;
import br.com.lar.repository.model.FaturamentoEntradasCabecalho;
import br.com.lar.repository.model.FaturamentoEntradasDetalhe;
import br.com.lar.repository.model.VinculoEntradaContasPagar;
import br.com.lar.service.caixa.CaixaCabecalhoService;
import br.com.lar.service.caixa.ResumoCaixaService;
import br.com.sysdesc.pesquisa.service.impl.AbstractPesquisableServiceImpl;
import br.com.sysdesc.util.classes.ListUtil;
import br.com.sysdesc.util.exception.SysDescException;
import br.com.systrans.util.constants.MensagemConstants;
import br.com.systrans.util.vo.FechamentoCaixaVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ManutencaoFaturamentoEntradaService extends AbstractPesquisableServiceImpl<FaturamentoEntradasCabecalho> {

	private ResumoCaixaService resumoCaixaService = new ResumoCaixaService();
	private CaixaCabecalhoService caixaCabecalhoService = new CaixaCabecalhoService();
	private FaturamentoEntradaCabecalhoDAO faturamentoEntradaDAO;
	private HistoricoCustoDAO historicoCustoDAO = new HistoricoCustoDAO();
	private FaturamentoEntradaService faturamentoEntradaService = new FaturamentoEntradaService();

	public ManutencaoFaturamentoEntradaService() {
		this(new FaturamentoEntradaCabecalhoDAO());
	}

	public ManutencaoFaturamentoEntradaService(FaturamentoEntradaCabecalhoDAO faturamentoEntradaDAO) {
		super(faturamentoEntradaDAO, FaturamentoEntradasCabecalho::getIdFaturamentoEntradasCabecalho);

		this.faturamentoEntradaDAO = faturamentoEntradaDAO;
	}

	@Override
	public void validar(FaturamentoEntradasCabecalho objetoPersistir) {

		if (objetoPersistir.getCentroCusto() == null) {

			throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_CENTRO_CUSTO);
		}

		if (objetoPersistir.getCliente() == null) {

			throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_FORNECEDOR);
		}

		if (ListUtil.isNullOrEmpty(objetoPersistir.getFaturamentoEntradaPagamentos())) {

			throw new SysDescException(MensagemConstants.MENSAGEM_INSIRA_PAGAMENTOS);
		}

		if (objetoPersistir.getDataMovimento() == null) {
			throw new SysDescException(MensagemConstants.MENSAGEM_DATA_MOVIMENTO_INVALIDA);
		}

		if (!historicoCustoDAO.existeHistorico(objetoPersistir.getHistorico())) {

			throw new SysDescException(MensagemConstants.MENSAGEM_HISTORICO_CUSTO_NAO_CONFIGURADO, objetoPersistir.getHistorico().getDescricao());
		}

	}

	@Override
	public void salvar(FaturamentoEntradasCabecalho objetoPersistir) {

		EntityManager entityManager = faturamentoEntradaDAO.getEntityManager();

		try {

			entityManager.getTransaction().begin();

			if (!objetoPersistir.getVinculoEntradaContasPagars().isEmpty()) {

				atualizarContasPagarCliente(objetoPersistir, entityManager);
			}

			objetoPersistir.getFaturamentoEntradasDetalhes().forEach(detalhe -> {

				atualizarDetalhes(detalhe, entityManager);
			});

			entityManager.merge(objetoPersistir);

			entityManager.getTransaction().commit();

		} catch (Exception e) {

			log.error("Erro ao editar Entradas de faturamento", e);

			entityManager.getTransaction().rollback();
		}

	}

	private void atualizarDetalhes(FaturamentoEntradasDetalhe detalhe, EntityManager entityManager) {

		atualizarVinculosContasPagar(detalhe, entityManager);
		atualizarVinculosCustos(detalhe, entityManager);

		entityManager.merge(detalhe);
	}

	private void atualizarVinculosCustos(FaturamentoEntradasDetalhe detalhe, EntityManager entityManager) {

		if (!detalhe.getVinculoEntradaCustos().isEmpty() && detalhe.getVeiculo() != null) {

			List<AlocacaoCusto> alocacaoCustos = detalhe.getVinculoEntradaCustos().stream().map(vinculo -> vinculo.getAlocacaoCusto())
					.filter(alocacao -> !detalhe.getVeiculo().equals(alocacao.getVeiculo())).collect(Collectors.toList());

			if (!alocacaoCustos.isEmpty()) {

				alocacaoCustos.forEach(alocacao -> {

					alocacao.setVeiculo(detalhe.getVeiculo());
					alocacao.setCodigoVeiculo(detalhe.getVeiculo().getIdVeiculo());

					entityManager.merge(alocacao);
				});

			}
		} else if (!detalhe.getVinculoEntradaCustos().isEmpty() && detalhe.getVeiculo() == null) {

			List<AlocacaoCusto> alocacaoCustos = detalhe.getVinculoEntradaCustos().stream().map(vinculo -> vinculo.getAlocacaoCusto())
					.filter(alocacao -> alocacao.getVeiculo() != null).collect(Collectors.toList());

			alocacaoCustos.forEach(alocacao -> {

				alocacao.setVeiculo(null);
				alocacao.setCodigoVeiculo(null);

				entityManager.merge(alocacao);
			});
		}

	}

	private void atualizarVinculosContasPagar(FaturamentoEntradasDetalhe detalhe, EntityManager entityManager) {

		if (detalhe.getVinculoEntradaContasPagarVeiculos().isEmpty() && detalhe.getVeiculo() != null) {

			List<ContasPagar> contasPagar = detalhe.getFaturamentoEntradasCabecalho().getVinculoEntradaContasPagars().stream()
					.map(VinculoEntradaContasPagar::getContasPagar).collect(Collectors.toList());

			faturamentoEntradaService.criarVinculosContasPagarVeiculo(contasPagar, detalhe, detalhe.getVeiculo());

			contasPagar.forEach(contaPagar -> {

				contaPagar.getContasPagarVeiculos().forEach(contaVeiculo -> {

					entityManager.persist(contaVeiculo);
				});
			});

			detalhe.getVinculoEntradaContasPagarVeiculos().forEach(entityManager::persist);
		} else if (!detalhe.getVinculoEntradaContasPagarVeiculos().isEmpty() && detalhe.getVeiculo() != null) {

			detalhe.getVinculoEntradaContasPagarVeiculos().forEach(vinculo -> {
				vinculo.getContasPagarVeiculo().setVeiculo(detalhe.getVeiculo());
				vinculo.getContasPagarVeiculo().setCodigoVeiculo(detalhe.getVeiculo().getIdVeiculo());

				entityManager.merge(vinculo.getContasPagarVeiculo());
			});
		} else if (!detalhe.getVinculoEntradaContasPagarVeiculos().isEmpty() && detalhe.getVeiculo() == null) {

			List<ContasPagar> contasPagar = detalhe.getFaturamentoEntradasCabecalho().getVinculoEntradaContasPagars().stream()
					.map(VinculoEntradaContasPagar::getContasPagar).collect(Collectors.toList());

			detalhe.getVinculoEntradaContasPagarVeiculos().forEach(vinculo -> {

				contasPagar.forEach(contaPagar -> {

					Optional<ContasPagarVeiculo> optional = contaPagar.getContasPagarVeiculos().stream().filter(
							veiculoConta -> veiculoConta.getIdContasPagarVeiculo().equals(vinculo.getContasPagarVeiculo().getIdContasPagarVeiculo()))
							.findFirst();

					if (optional.isPresent()) {

						contaPagar.getContasPagarVeiculos().remove(optional.get());

						entityManager.merge(contaPagar);
					}

				});
			});
		}

	}

	private void atualizarContasPagarCliente(FaturamentoEntradasCabecalho objetoPersistir, EntityManager entityManager) {

		Cliente clienteReferenciado = objetoPersistir.getCliente();

		objetoPersistir.getVinculoEntradaContasPagars().forEach(vinculoContasPagar -> {

			ContasPagar contasPagar = vinculoContasPagar.getContasPagar();

			if (!contasPagar.getCliente().getIdCliente().equals(clienteReferenciado.getIdCliente())) {

				contasPagar.setCliente(clienteReferenciado);
				contasPagar.setCodigoCliente(clienteReferenciado.getIdCliente());

				entityManager.merge(contasPagar);
			}
		});

	}

	public void excluirFaturamento(FaturamentoEntradasCabecalho faturamentoCabecalho) {

		if (faturamentoCabecalho.getCaixaCabecalho().getDataFechamento() == null
				|| JOptionPane.showConfirmDialog(null, "O CAIXA DA EXCLUSÃO JÁ FOI FECHADO.\nDESEJA RECALCULAR OS SALDOS DE CAIXA?",
						"VERIFICAÇÃO DE CAIXA", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

			EntityManager entityManager = faturamentoEntradaDAO.getEntityManager();

			try {
				entityManager.getTransaction().begin();

				FaturamentoEntradasCabecalho mergedEntity = entityManager.merge(faturamentoCabecalho);

				if (!mergedEntity.getVinculoEntradas().isEmpty()) {

					mergedEntity.getVinculoEntradas().forEach(vinculoEntrada -> {
						vinculoEntrada.getDiarioCabecalho().getDiarioDetalhes().forEach(entityManager::remove);

						entityManager.remove(vinculoEntrada.getDiarioCabecalho());
					});

					mergedEntity.getVinculoEntradas().forEach(entityManager::remove);
				}

				if (!mergedEntity.getVinculoEntradaCaixas().isEmpty()) {

					mergedEntity.getVinculoEntradaCaixas().forEach(vinculoEntradaCaixa -> {
						entityManager.remove(vinculoEntradaCaixa.getCaixaDetalhe());
					});

					mergedEntity.getVinculoEntradaCaixas().forEach(entityManager::remove);
				}

				if (!mergedEntity.getVinculoEntradaContasPagars().isEmpty()) {

					mergedEntity.getVinculoEntradaContasPagars().forEach(vinculoEntradaCaixa -> {

						vinculoEntradaCaixa.getContasPagar().getContasPagarPagamentos().forEach(entityManager::remove);
						vinculoEntradaCaixa.getContasPagar().getContasPagarVeiculos().forEach(entityManager::remove);

						entityManager.remove(vinculoEntradaCaixa.getContasPagar());
					});

					mergedEntity.getVinculoEntradaContasPagars().forEach(entityManager::remove);
				}

				mergedEntity.getFaturamentoEntradasDetalhes().forEach(detalhes -> {

					detalhes.getVinculoEntradaCustos().forEach(entityManager::remove);

					detalhes.getVinculoEntradaContasPagarVeiculos().forEach(vinculoContaPagar -> {
						entityManager.remove(vinculoContaPagar.getContasPagarVeiculo());
					});

					entityManager.remove(detalhes);
				});

				mergedEntity.getFaturamentoEntradaPagamentos().forEach(entityManager::remove);
				mergedEntity.getDocumentoEntradas().forEach(entityManager::remove);

				entityManager.remove(mergedEntity);

				CaixaCabecalho caixaCabecalho = faturamentoCabecalho.getCaixaCabecalho();

				while (caixaCabecalho != null && caixaCabecalho.getDataFechamento() != null) {

					FechamentoCaixaVO fechamentoCaixaVO = caixaCabecalhoService.buscarResumoFechamentoCaixa(caixaCabecalho);
					CaixaSaldo caixaSaldo = caixaCabecalho.getCaixaSaldo();

					BigDecimal novoSaldo = fechamentoCaixaVO.getSaldoAtual().add(fechamentoCaixaVO.getValorPagamentos())
							.add(fechamentoCaixaVO.getValorDinheiro());

					caixaSaldo.setValorSaldo(fechamentoCaixaVO.getValorPagamentos().add(fechamentoCaixaVO.getValorDinheiro()));
					caixaSaldo.setValorSaldoAcumulado(novoSaldo);

					entityManager.merge(caixaSaldo);

					caixaCabecalho = caixaCabecalhoService.obterProximoCaixa(caixaCabecalho);
				}

				entityManager.getTransaction().commit();

			} catch (Exception e) {
				e.printStackTrace();
				entityManager.getTransaction().rollback();
			}
		}

	}
}
