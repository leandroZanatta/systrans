package br.com.lar.repository.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@Table(name = "tb_vinculoentradacontaspagarveiculo")
@SequenceGenerator(name = "GEN_VINCULOENTRADACONTASPAGARVEICULO", allocationSize = 1, sequenceName = "GEN_VINCULOENTRADACONTASPAGARVEICULO")
public class VinculoEntradaContasPagarVeiculo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_VINCULOENTRADACONTASPAGARVEICULO")
	@Column(name = "id_vinculoentradacontaspagarveiculo")
	private Long idVinculoEntradaContasPagarVeiculo;

	@ManyToOne
	@JoinColumn(name = "cd_faturamentoentradasdetalhe")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private FaturamentoEntradasDetalhe faturamentoEntradasDetalhe;

	@OneToOne
	@JoinColumn(name = "cd_contaspagarveiculo")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private ContasPagarVeiculo contasPagarVeiculo;

}