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
@Table(name = "tb_vinculoentrada")
@SequenceGenerator(name = "GEN_VINCULOENTRADA", allocationSize = 1, sequenceName = "GEN_VINCULOENTRADA")
public class VinculoEntrada implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_VINCULOENTRADA")
	@Column(name = "id_vinculoentrada")
	private Long idVinculoEntrada;

	@ManyToOne
	@JoinColumn(name = "cd_faturamentoentrada")
	private FaturamentoEntradasCabecalho faturamentoEntrada;

	@OneToOne
	@JoinColumn(name = "cd_diariocabecalho")
	private DiarioCabecalho diarioCabecalho;

}