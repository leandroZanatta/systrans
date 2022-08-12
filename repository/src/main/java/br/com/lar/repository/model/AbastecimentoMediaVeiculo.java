package br.com.lar.repository.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@Table(name = "tb_abastecimentomediaveiculo")
@SequenceGenerator(name = "GEN_ABASTECIMENTOMEDIAVEICULO", allocationSize = 1, sequenceName = "GEN_ABASTECIMENTOMEDIAVEICULO")
public class AbastecimentoMediaVeiculo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_ABASTECIMENTOMEDIAVEICULO")
	@Column(name = "id_abastecimentomediaveiculo")
	private Long idAbastecimentoMediaVeiculo;

	@ManyToOne
	@JoinColumn(name = "cd_veiculo")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Veiculo veiculo;

	@Column(name = "dt_calculo")
	@Temporal(TemporalType.DATE)
	private Date dataCalculo;

	@Column(name = "vl_quantidadekm")
	private Long quantidadeKilometros;

	@Column(name = "vl_litros")
	private BigDecimal litrosAbastecidos;

	@Column(name = "vl_abastecimento")
	private BigDecimal valorAbastecimento;

	@Column(name = "vl_kmporlitro")
	private BigDecimal kilometrosPorLitro;

	@Column(name = "vl_rsporkm")
	private BigDecimal reaisPorKilometro;

	@Column(name = "nr_sincronizacaoversao")
	private Long sincronizacaoVersao;

}