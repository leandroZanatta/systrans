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
@Table(name = "tb_diariodetalhe")
@SequenceGenerator(name = "GEN_DIARIODETALHE", sequenceName = "GEN_DIARIODETALHE", allocationSize = 1)
public class DiarioDetalhe implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_DIARIODETALHE")
	@Column(name = "id_diariodetalhe")
	private Long idDiarioDetalhe;

	@ManyToOne
	@JoinColumn(name = "cd_diariocabecalho")
	private DiarioCabecalho diarioCabecalho;

	@ManyToOne
	@JoinColumn(name = "cd_planocontas")
	private PlanoContas planoContas;

	@Column(name = "vl_detalhe")
	private BigDecimal valorDetalhe;

}