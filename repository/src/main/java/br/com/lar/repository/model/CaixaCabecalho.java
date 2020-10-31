package br.com.lar.repository.model;

import java.io.Serializable;
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
@Table(name = "tb_caixacabecalho")
@SequenceGenerator(name = "GEN_CAIXACABECALHO", sequenceName = "GEN_CAIXACABECALHO", allocationSize = 1)
public class CaixaCabecalho implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_CAIXACABECALHO")
	@Column(name = "id_caixacabecalho")
	private Long idCaixaCabecalho;

	@ManyToOne
	@JoinColumn(name = "cd_caixa")
	private Caixa caixa;

	@OneToOne(mappedBy = "caixaCabecalho", optional = true, cascade = CascadeType.ALL)
	private CaixaSaldo caixaSaldo;

	@Column(name = "cd_caixa", insertable = false, updatable = false)
	private Long codigoCaixa;

	@Column(name = "dt_movimento")
	@Temporal(TemporalType.DATE)
	private Date dataMovimento;

	@Column(name = "dt_abertura")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataAbertura;

	@Column(name = "dt_fechamento")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataFechamento;

	@OneToMany(mappedBy = "caixaCabecalho")
	private List<CaixaDetalhe> caixaDetalhes;

}