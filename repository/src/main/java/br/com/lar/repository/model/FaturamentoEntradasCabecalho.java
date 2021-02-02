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
@Table(name = "tb_faturamentoentradascabecalho")
@SequenceGenerator(name = "GEN_FATURAMENTOENTRADASCABECALHO", allocationSize = 1, sequenceName = "GEN_FATURAMENTOENTRADASCABECALHO")
public class FaturamentoEntradasCabecalho implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_FATURAMENTOENTRADASCABECALHO")
	@Column(name = "id_faturamentoentradascabecalho")
	private Long idFaturamentoEntradasCabecalho;

	@ManyToOne
	@JoinColumn(name = "cd_caixacabecalho")
	private CaixaCabecalho caixaCabecalho;

	@ManyToOne
	@JoinColumn(name = "cd_cliente")
	private Cliente cliente;

	@ManyToOne
	@JoinColumn(name = "cd_historico")
	private Historico historico;

	@ManyToOne
	@JoinColumn(name = "cd_centrocusto")
	private CentroCusto centroCusto;

	@Column(name = "dt_movimento")
	@Temporal(TemporalType.DATE)
	private Date dataMovimento;

	@Column(name = "vl_bruto")
	private BigDecimal valorBruto;

	@Column(name = "cd_historico", insertable = false, updatable = false)
	private Long codigoHistorico;

	@Column(name = "cd_cliente", insertable = false, updatable = false)
	private Long codigoCliente;

	@Column(name = "cd_centrocusto", insertable = false, updatable = false)
	private Long codigoCentroCusto;

	@OneToMany(mappedBy = "faturamentoEntradasCabecalho", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<FaturamentoEntradaPagamentos> faturamentoEntradaPagamentos;

	@OneToMany(mappedBy = "faturamentoEntradasCabecalho", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<FaturamentoEntradasDetalhe> faturamentoEntradasDetalhes;

	@OneToMany(mappedBy = "faturamentoEntrada")
	private List<DocumentoEntrada> documentoEntradas = new ArrayList<>();

	@OneToMany(mappedBy = "faturamentoEntrada")
	private List<VinculoEntrada> vinculoEntradas;

	@OneToMany(mappedBy = "faturamentoEntrada")
	private List<VinculoEntradaCaixa> vinculoEntradaCaixas;

	@OneToMany(mappedBy = "faturamentoEntrada")
	private List<VinculoEntradaContasPagar> vinculoEntradaContasPagars;

}