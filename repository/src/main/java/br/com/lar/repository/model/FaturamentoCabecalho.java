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
	private CaixaCabecalho caixaCabecalho;

	@ManyToOne
	@JoinColumn(name = "cd_historico")
	private Historico historico;

	@ManyToOne
	@JoinColumn(name = "cd_cliente")
	private Cliente cliente;

	@Column(name = "dt_movimento")
	@Temporal(TemporalType.DATE)
	private Date dataMovimento;

	@Column(name = "vl_bruto")
	private BigDecimal valorBruto;

	@OneToMany(mappedBy = "faturamentoCabecalho", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<FaturamentoDetalhe> faturamentoDetalhes;

	@OneToMany(mappedBy = "faturamentoCabecalho", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<FaturamentoPagamentos> faturamentoPagamentos;

	@OneToMany(mappedBy = "faturamento")
	private List<VinculoSaida> vinculoSaidas;

	@OneToMany(mappedBy = "faturamento")
	private List<VinculoSaidaCaixa> vinculoSaidaCaixas;

	@OneToMany(mappedBy = "faturamento")
	private List<VinculoSaidaContasReceber> vinculoSaidaContasRecebers;
}