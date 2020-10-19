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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

@Data
@Entity
@Table(name = "tb_faturamento")
@SequenceGenerator(name = "GEN_FATURAMENTO", allocationSize = 1, sequenceName = "GEN_FATURAMENTO")
public class Faturamento implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_FATURAMENTO")
	@Column(name = "id_faturamento")
	private Long idFaturamento;

	@ManyToOne
	@JoinColumn(name = "cd_historico")
	private Historico historico;

	@ManyToOne
	@JoinColumn(name = "cd_cliente")
	private Cliente cliente;

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

	@OneToOne(mappedBy = "faturamento", cascade = CascadeType.ALL, orphanRemoval = true, optional = true)
	private FaturamentoTransporte faturamentoTransporte;

	@OneToMany(mappedBy = "faturamento", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<FaturamentoPagamento> faturamentoPagamentos;

}