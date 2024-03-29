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
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@Table(name = "tb_faturamentocabecalho")
@SequenceGenerator(name = "GEN_FATURAMENTOCABECALHO", allocationSize = 1, sequenceName = "GEN_FATURAMENTOCABECALHO")
public class FaturamentoCabecalho implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_FATURAMENTOCABECALHO")
	@Column(name = "id_faturamentocabecalho")
	private Long idFaturamentoCabecalho;

	@ManyToOne
	@JoinColumn(name = "cd_caixacabecalho")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private CaixaCabecalho caixaCabecalho;

	@ManyToOne
	@JoinColumn(name = "cd_historico")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Historico historico;

	@ManyToOne
	@JoinColumn(name = "cd_cliente")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Cliente cliente;

	@Column(name = "dt_movimento")
	@Temporal(TemporalType.DATE)
	private Date dataMovimento;

	@Column(name = "vl_bruto")
	private BigDecimal valorBruto;

	@OneToMany(mappedBy = "faturamentoCabecalho", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<FaturamentoDetalhe> faturamentoDetalhes = new ArrayList<>();

	@OneToMany(mappedBy = "faturamentoCabecalho", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<FaturamentoPagamentos> faturamentoPagamentos = new ArrayList<>();

	@OneToMany(mappedBy = "faturamento", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<VinculoSaida> vinculoSaidas = new ArrayList<>();

	@OneToMany(mappedBy = "faturamento", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<VinculoSaidaCaixa> vinculoSaidaCaixas = new ArrayList<>();

	@OneToMany(mappedBy = "faturamento", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<VinculoSaidaContasReceber> vinculoSaidaContasRecebers = new ArrayList<>();

	@Column(name = "cd_historico", insertable = false, updatable = false)
	private Long codigoHistorico;

	@Column(name = "cd_cliente", insertable = false, updatable = false)
	private Long codigoCliente;

}