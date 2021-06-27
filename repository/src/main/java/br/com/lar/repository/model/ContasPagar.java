package br.com.lar.repository.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
@Table(name = "tb_contaspagar")
@SequenceGenerator(name = "GEN_CONTASPAGAR", sequenceName = "GEN_CONTASPAGAR", allocationSize = 1)
public class ContasPagar implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id_contaspagar")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_CONTASPAGAR")
	private Long idContasPagar;

	@ManyToOne
	@JoinColumn(name = "cd_caixacabecalho")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private CaixaCabecalho caixaCabecalho;

	@ManyToOne
	@JoinColumn(name = "cd_diarioDetalhe")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private DiarioDetalhe diarioDetalhe;

	@ManyToOne
	@JoinColumn(name = "cd_cliente")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Cliente cliente;

	@ManyToOne
	@JoinColumn(name = "cd_formaspagamento")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private FormasPagamento formasPagamento;

	@ManyToOne
	@JoinColumn(name = "cd_historico")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Historico historico;

	@Column(name = "dt_movimento")
	@Temporal(TemporalType.DATE)
	private Date dataMovimento;

	@Column(name = "dt_vencimento")
	@Temporal(TemporalType.DATE)
	private Date dataVencimento;

	@Column(name = "vl_parcela")
	private BigDecimal valorParcela;

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

	@Column(name = "fl_baixado")
	private boolean baixado;

	@Column(name = "cd_status")
	private Long codigoStatus;

	@Column(name = "cd_caixacabecalho", insertable = false, updatable = false)
	private Long codigoCaixaCabecalho;

	@Column(name = "cd_cliente", insertable = false, updatable = false)
	private Long codigoCliente;

	@Column(name = "cd_formaspagamento", insertable = false, updatable = false)
	private Long codigoFormaPagamento;

	@Column(name = "cd_historico", insertable = false, updatable = false)
	private Long codigoHistorico;

	@OneToOne(mappedBy = "contasPagar")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private VinculoEntradaContasPagar vinculoEntradaContasPagar;

	@OneToMany(mappedBy = "contasPagar")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<ContasPagarPagamento> contasPagarPagamentos;

	@OneToMany(mappedBy = "contasPagar", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<ContasPagarVeiculo> contasPagarVeiculos = new ArrayList<>();

}