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
@Table(name = "tb_faturamentopagamentos")
@SequenceGenerator(name = "GEN_FATURAMENTOPAGAMENTOS", allocationSize = 1, sequenceName = "GEN_FATURAMENTOPAGAMENTOS")
public class FaturamentoPagamentos implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_FATURAMENTOPAGAMENTOS")
	@Column(name = "id_faturamentopagamentos")
	private Long idFaturamentoPagamentos;

	@ManyToOne
	@JoinColumn(name = "cd_faturamentocabecalho")
	private FaturamentoCabecalho faturamentoCabecalho;

	@ManyToOne
	@JoinColumn(name = "cd_formapagamento")
	private FormasPagamento formasPagamento;

	@Column(name = "dt_lancamento")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataLancamento;

	@Column(name = "dt_vencimento")
	@Temporal(TemporalType.DATE)
	private Date dataVencimento;

	@Column(name = "nr_parcela")
	private Long numeroParcela;

	@Column(name = "vl_parcela")
	private BigDecimal valorParcela;
}