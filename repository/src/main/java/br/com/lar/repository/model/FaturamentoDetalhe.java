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
@Table(name = "tb_faturamentodetalhe")
@SequenceGenerator(name = "GEN_FATURAMENTODETALHE", allocationSize = 1, sequenceName = "GEN_FATURAMENTODETALHE")
public class FaturamentoDetalhe implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_FATURAMENTODETALHE")
	@Column(name = "id_faturamentodetalhe")
	private Long idFaturamentoDetalhe;

	@ManyToOne
	@JoinColumn(name = "cd_faturamentocabecalho")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private FaturamentoCabecalho faturamentoCabecalho;

	@Column(name = "cd_faturamentocabecalho", insertable = false, updatable = false)
	private Long codigoFaturamentoCabecalho;

	@Column(name = "cd_veiculo", insertable = false, updatable = false)
	private Long codigoVeiculo;

	@ManyToOne
	@JoinColumn(name = "cd_veiculo")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Veiculo veiculo;

	@ManyToOne
	@JoinColumn(name = "cd_motorista")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Motorista motorista;

	@Column(name = "nr_documento")
	private String numeroDocumento;

	@Column(name = "vl_bruto")
	private BigDecimal valorBruto;

	@Column(name = "vl_acrescimo")
	private BigDecimal valorAcrescimo;

	@Column(name = "vl_desconto")
	private BigDecimal valorDesconto;
}