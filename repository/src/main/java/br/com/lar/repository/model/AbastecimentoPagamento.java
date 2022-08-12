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
@Table(name = "tb_abastecimentopagamento")
@SequenceGenerator(name = "GEN_ABASTECIMENTOPAGAMENTO", allocationSize = 1, sequenceName = "GEN_ABASTECIMENTOPAGAMENTO")
public class AbastecimentoPagamento implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_ABASTECIMENTOPAGAMENTO")
	@Column(name = "id_abastecimentopagamento")
	private Long idAbastecimentoPagamento;

	@ManyToOne
	@JoinColumn(name = "cd_abastecimentoveiculo")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private AbastecimentoVeiculo abastecimentoVeiculo;

	@Column(name = "dt_lancamento")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataLancamento;

	@Column(name = "dt_vencimento")
	@Temporal(TemporalType.DATE)
	private Date dataVencimento;

	@Column(name = "nr_parcela")
	private Long numeroParcela;

	@Column(name = "vl_parcela")
	private BigDecimal valorParcela;

}