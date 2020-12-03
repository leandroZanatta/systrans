package br.com.lar.repository.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "tb_faturamentoentradasdetalhe")
@SequenceGenerator(name = "GEN_FATURAMENTOENTRADASDETALHE", allocationSize = 1, sequenceName = "GEN_FATURAMENTOENTRADASDETALHE")
public class FaturamentoEntradasDetalhe implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_FATURAMENTOENTRADASDETALHE")
	@Column(name = "id_faturamentoentradasdetalhe")
	private Long idFaturamentoEntradasDetalhe;

	@ManyToOne
	@JoinColumn(name = "cd_faturamentoentradascabecalho")
	private FaturamentoEntradasCabecalho faturamentoEntradasCabecalho;

	@ManyToOne
	@JoinColumn(name = "cd_veiculo")
	private Veiculo veiculo;

	@ManyToOne
	@JoinColumn(name = "cd_motorista")
	private Motorista motorista;

	@Column(name = "nr_documento")
	private String numeroDocumento;

	@Column(name = "vl_bruto")
	private BigDecimal valorBruto;

	@Column(name = "vl_acrescimo")
	private BigDecimal valorAcrescimo;

	@Column(name = "vl_desconto")
	private BigDecimal valorDesconto;

}