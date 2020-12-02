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

@Data
@Entity
@Table(name = "tb_vinculoentradacontaspagar")
@SequenceGenerator(name = "GEN_VINCULOENTRADACONTASPAGAR", allocationSize = 1, sequenceName = "GEN_VINCULOENTRADACONTASPAGAR")
public class VinculoEntradaContasPagar implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_VINCULOENTRADACONTASPAGAR")
	@Column(name = "id_vinculoentradacontaspagar")
	private Long idVinculoEntradaContasPagar;

	@ManyToOne
	@JoinColumn(name = "cd_faturamentoentrada")
	private FaturamentoEntrada faturamentoEntrada;

	@OneToOne
	@JoinColumn(name = "cd_contaspagar")
	private ContasPagar contasPagar;

}