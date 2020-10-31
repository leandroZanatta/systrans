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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

@Data
@Entity
@Table(name = "tb_contasreceber")
@SequenceGenerator(name = "GEN_CONTASRECEBER", sequenceName = "GEN_CONTASRECEBER", allocationSize = 1)
public class ContasReceber implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id_contasreceber")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_CONTASRECEBER")
	private Long idContasReceber;

	@ManyToOne
	@JoinColumn(name = "cd_caixacabecalho")
	private CaixaCabecalho caixaCabecalho;

	@Column(name = "cd_caixacabecalho", insertable = false, updatable = false)
	private Long codigoCaixaCabecalho;

	@Column(name = "cd_cliente", insertable = false, updatable = false)
	private Long codigoCliente;

	@ManyToOne
	@JoinColumn(name = "cd_cliente")
	private Cliente cliente;

	@Column(name = "cd_formaspagamento", insertable = false, updatable = false)
	private Long codigoFormaPagamento;

	@ManyToOne
	@JoinColumn(name = "cd_formaspagamento")
	private FormasPagamento formasPagamento;

	@ManyToOne
	@JoinColumn(name = "cd_veiculo")
	private Veiculo veiculo;

	@ManyToOne
	@JoinColumn(name = "cd_motorista")
	private Motorista motorista;

	@ManyToOne
	@JoinColumn(name = "cd_historico")
	private Historico historico;

	@Column(name = "tx_documento")
	private String documento;

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

	@OneToMany(mappedBy = "contasReceber", cascade = CascadeType.ALL)
	private List<ContasReceberPagamento> contasReceberPagamentos = new ArrayList<>();

}