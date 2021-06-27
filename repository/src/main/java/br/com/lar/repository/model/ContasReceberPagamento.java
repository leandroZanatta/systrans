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
@Table(name = "tb_contasreceberpagamento")
@SequenceGenerator(name = "GEN_CONTASRECEBERPAGAMENTO", sequenceName = "GEN_CONTASRECEBERPAGAMENTO", allocationSize = 1)
public class ContasReceberPagamento implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id_contasreceberpagamento")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_CONTASRECEBERPAGAMENTO")
	private Long idContasReceberPagamento;

	@ManyToOne
	@JoinColumn(name = "cd_caixacabecalho")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private CaixaCabecalho caixaCabecalho;

	@ManyToOne
	@JoinColumn(name = "cd_contasreceber")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private ContasReceber contasReceber;

	@ManyToOne
	@JoinColumn(name = "cd_formaspagamento")
	@ToString.Exclude
	private FormasPagamento formasPagamento;

	@ManyToOne
	@JoinColumn(name = "cd_historico")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Historico historico;

	@Column(name = "dt_movimento")
	@Temporal(TemporalType.DATE)
	private Date dataMovimento;

	@Column(name = "dt_cadastro")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataCadastro;

	@Column(name = "dt_manutencao")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataManutencao;

	@Column(name = "vl_desconto")
	private BigDecimal valorDesconto;

	@Column(name = "vl_acrescimo")
	private BigDecimal valorAcrescimo;

	@Column(name = "vl_juros")
	private BigDecimal valorJuros;

	@Column(name = "vl_parcela")
	private BigDecimal valorPago;

}