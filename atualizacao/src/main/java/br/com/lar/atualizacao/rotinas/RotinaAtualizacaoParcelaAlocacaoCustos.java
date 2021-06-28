package br.com.lar.atualizacao.rotinas;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import br.com.systrans.util.RateioUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RotinaAtualizacaoParcelaAlocacaoCustos {

	private Connection connection;

	public RotinaAtualizacaoParcelaAlocacaoCustos(Connection connection) {

		this.connection = connection;
	}

	public void start() {

		try {
			List<FaturamentoParcelas> faturamentoParcelas = buscarParcelasAlocacao();

			Map<Integer, List<FaturamentoParcelas>> mapaParcelas = faturamentoParcelas.stream()
					.collect(Collectors.groupingBy(FaturamentoParcelas::getCodigoDetalhe));

			connection.setAutoCommit(false);

			for (Entry<Integer, List<FaturamentoParcelas>> entry : mapaParcelas.entrySet()) {

				BigDecimal valorLiquido = entry.getValue().get(0).getValorLiquido();

				if (entry.getValue().size() == 1) {

					entry.getValue().get(0).setValorParcela(valorLiquido);

				} else {

					BigDecimal valorParcela = valorLiquido.divide(BigDecimal.valueOf(entry.getValue().size()), 2, RoundingMode.HALF_EVEN);

					entry.getValue().forEach(parcela -> parcela.setValorParcela(valorParcela));

					RateioUtil.efetuarRateio(entry.getValue(), FaturamentoParcelas::getValorParcela, FaturamentoParcelas::setValorParcela,
							valorLiquido);
				}

				for (FaturamentoParcelas parcela : entry.getValue()) {
					runUpdate(parcela);
				}
			}

			connection.commit();

		} catch (SQLException e) {

			log.error("não foi possivel efetuar update das parcelas", e);
		}
	}

	private void runUpdate(FaturamentoParcelas parcela) throws SQLException {

		if (!parcela.getValorParcelaAnterior().equals(parcela.getValorParcela())) {

			log.info("Identificado valor parcela diferente: anterior:{} novo: {}", parcela.getValorParcelaAnterior(), parcela.getValorParcela());

			String sql = "UPDATE TB_ALOCACAOCUSTO SET VL_PARCELA=? WHERE ID_ALOCACAOCUSTO=?";

			try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

				preparedStatement.setBigDecimal(1, parcela.getValorParcela());
				preparedStatement.setInt(2, parcela.getCodigoAlocacao());

				preparedStatement.executeUpdate();
			}
		}
	}

	private List<FaturamentoParcelas> buscarParcelasAlocacao() throws SQLException {
		log.info("Buscando Parcelas de alocação de custos");

		List<FaturamentoParcelas> faturamentoParcelas = new ArrayList<>();

		String sql = "SELECT\r\n" + "	d.ID_FATURAMENTOENTRADASDETALHE,\r\n" + "	v.CD_ALOCACAOCUSTO,\r\n"
				+ "	d.VL_BRUTO-d.VL_DESCONTO + d.VL_ACRESCIMO AS vl_liquido,\r\n" + "	a.VL_PARCELA\r\n" + "FROM\r\n"
				+ "	TB_FATURAMENTOENTRADASDETALHE d\r\n" + "INNER JOIN TB_VINCULOENTRADACUSTO v ON\r\n"
				+ "	d.ID_FATURAMENTOENTRADASDETALHE = v.CD_FATURAMENTOENTRADASDETALHE\r\n" + "INNER JOIN TB_ALOCACAOCUSTO a ON\r\n"
				+ "	v.CD_ALOCACAOCUSTO=a.ID_ALOCACAOCUSTO";

		try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {

				faturamentoParcelas.add(new FaturamentoParcelas(rs.getInt("ID_FATURAMENTOENTRADASDETALHE"), rs.getInt("CD_ALOCACAOCUSTO"),
						rs.getBigDecimal("VL_LIQUIDO"), rs.getBigDecimal("VL_PARCELA"), BigDecimal.ZERO));
			}
		}

		return faturamentoParcelas;

	}

}

@Data
@AllArgsConstructor
class FaturamentoParcelas {

	private Integer codigoDetalhe;
	private Integer codigoAlocacao;
	private BigDecimal valorLiquido;
	private BigDecimal valorParcelaAnterior;
	private BigDecimal valorParcela;

}
