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
@Table(name = "tb_vinculoabastecimento")
@SequenceGenerator(name = "GEN_VINCULOABASTECIMENTO", allocationSize = 1, sequenceName = "GEN_VINCULOABASTECIMENTO")
public class VinculoAbastecimento implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_VINCULOABASTECIMENTO")
	@Column(name = "id_vinculoabastecimento")
	private Long idVinculoAbastecimento;

	@ManyToOne
	@JoinColumn(name = "cd_faturamentoentrada")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private FaturamentoEntradasCabecalho faturamentoEntrada;

	@OneToOne
	@JoinColumn(name = "cd_abastecimentoveiculo")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private AbastecimentoVeiculo abastecimentoVeiculo;

}