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
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@Table(name = "tb_abastecimento")
@SequenceGenerator(name = "GEN_ABASTECIMENTO", allocationSize = 1, sequenceName = "GEN_ABASTECIMENTO")
public class Abastecimento implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_ABASTECIMENTO")
	@Column(name = "id_abastecimento")
	private Long idAbastecimento;

	@Column(name = "cd_veiculo", insertable = false, updatable = false)
	private Long codigoVeiculo;

	@ManyToOne
	@JoinColumn(name = "cd_veiculo")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Veiculo veiculo;

	@Column(name = "vl_faturamentomedio")
	private BigDecimal faturamentoMedio;

	@Column(name = "vl_customedio")
	private BigDecimal custoMédio;

	@Column(name = "vl_margemcontribuicao")
	private BigDecimal margemContribuicao;

	@Column(name = "vl_media")
	private BigDecimal mediaVeiculo;

	@Column(name = "vl_custokm")
	private BigDecimal custoKilometro;

	@Column(name = "nr_sincronizacaoversao")
	private Long sincronizacaoVersao;

}