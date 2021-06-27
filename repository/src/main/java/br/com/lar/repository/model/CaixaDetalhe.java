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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@Table(name = "tb_caixadetalhe")
@SequenceGenerator(name = "GEN_CAIXADETALHE", sequenceName = "GEN_CAIXADETALHE", allocationSize = 1)
public class CaixaDetalhe implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_CAIXADETALHE")
	@Column(name = "id_caixadetalhe")
	private Long idCaixaDetalhe;

	@Column(name = "cd_caixacabecalho", insertable = false, updatable = false)
	private Long codigoCaixaCabecalho;

	@ManyToOne
	@JoinColumn(name = "cd_caixacabecalho")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private CaixaCabecalho caixaCabecalho;

	@ManyToOne
	@JoinColumn(name = "cd_planocontas")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private PlanoContas planoContas;

	@Column(name = "cd_planocontas", insertable = false, updatable = false)
	private Long codigoPlanoContas;

	@Column(name = "dt_movimento")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataMovimento;

	@Column(name = "nr_tiposaldo")
	private Long tipoSaldo;

	@Column(name = "vl_detalhe")
	private BigDecimal valorDetalhe;

	@OneToOne(mappedBy = "caixaDetalhe")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private VinculoEntradaCaixa vinculoEntradaCaixa;

	@OneToOne(mappedBy = "caixaDetalhe")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private VinculoSaidaCaixa vinculoSaidaCaixa;

}