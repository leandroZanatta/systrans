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

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tb_contaspagarpagamento")
@SequenceGenerator(name = "GEN_CONTASPAGARPAGAMENTO", sequenceName = "GEN_CONTASPAGARPAGAMENTO", allocationSize = 1)
public class ContasPagarPagamento implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id_contaspagarpagamento")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_CONTASPAGARPAGAMENTO")
	private Long idContasPagarPagamento;

	@ManyToOne
	@JoinColumn(name = "cd_contaspagar")
	private ContasPagar contasPagar;

	@ManyToOne
	@JoinColumn(name = "cd_caixacabecalho")
	private CaixaCabecalho caixaCabecalho;

	@ManyToOne
	@JoinColumn(name = "cd_formaspagamento")
	private FormasPagamento formasPagamento;

	@ManyToOne
	@JoinColumn(name = "cd_historico")
	private Historico historico;

	@Column(name = "dt_movimento")
	@Temporal(TemporalType.DATE)
	private Date dataMovimento;

	@Column(name = "vl_desconto")
	private BigDecimal valorDesconto;

	@Column(name = "vl_acrescimo")
	private BigDecimal valorAcrescimo;

	@Column(name = "vl_juros")
	private BigDecimal valorJuros;

	@Column(name = "vl_pago")
	private BigDecimal valorPago;

	@Column(name = "dt_cadastro")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataCadastro;

	@Column(name = "dt_manutencao")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataManutencao;

}