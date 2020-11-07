package br.com.lar.repository.model;

import java.io.Serializable;
import java.math.BigDecimal;
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
@Table(name = "tb_faturamentoentrada")
@SequenceGenerator(name = "GEN_FATURAMENTOENTRADA", allocationSize = 1, sequenceName = "GEN_FATURAMENTOENTRADA")
public class FaturamentoEntrada implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_FATURAMENTOENTRADA")
	@Column(name = "id_faturamentoentrada")
	private Long idFaturamentoEntrada;

	@ManyToOne
	@JoinColumn(name = "cd_caixacabecalho")
	private CaixaCabecalho caixaCabecalho;

	@ManyToOne
	@JoinColumn(name = "cd_historico")
	private Historico historico;

	@ManyToOne
	@JoinColumn(name = "cd_cliente")
	private Cliente cliente;

	@ManyToOne
	@JoinColumn(name = "cd_veiculo")
	private Veiculo veiculo;

	@ManyToOne
	@JoinColumn(name = "cd_motorista")
	private Motorista motorista;

	@ManyToOne
	@JoinColumn(name = "cd_centrocusto")
	private CentroCusto centroCusto;

	@Column(name = "nr_documento")
	private String numeroDocumento;

	@Column(name = "dt_movimento")
	@Temporal(TemporalType.DATE)
	private Date dataMovimento;

	@Column(name = "vl_bruto")
	private BigDecimal valorBruto;

	@Column(name = "vl_acrescimo")
	private BigDecimal valorAcrescimo;

	@Column(name = "vl_desconto")
	private BigDecimal valorDesconto;

	@OneToMany(mappedBy = "faturamentoEntrada", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<FaturamentoEntradaPagamento> faturamentoEntradaPagamentos;

}