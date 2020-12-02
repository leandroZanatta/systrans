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
@Table(name = "tb_vinculoentradacaixa")
@SequenceGenerator(name = "GEN_VINCULOENTRADACAIXA", allocationSize = 1, sequenceName = "GEN_VINCULOENTRADACAIXA")
public class VinculoEntradaCaixa implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_VINCULOENTRADACAIXA")
	@Column(name = "id_vinculoentradacaixa")
	private Long idVinculoEntradaCaixa;

	@ManyToOne
	@JoinColumn(name = "cd_faturamentoentrada")
	private FaturamentoEntrada faturamentoEntrada;

	@OneToOne
	@JoinColumn(name = "cd_caixadetalhe")
	private CaixaDetalhe caixaDetalhe;

}