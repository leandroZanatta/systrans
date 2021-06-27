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
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@Table(name = "tb_diariocabecalho")
@SequenceGenerator(name = "GEN_DIARIOCABECALHO", sequenceName = "GEN_DIARIOCABECALHO", allocationSize = 1)
public class DiarioCabecalho implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_DIARIOCABECALHO")
	@Column(name = "id_diariocabecalho")
	private Long idDiarioCabecalho;

	@ManyToOne
	@JoinColumn(name = "cd_caixacabecalho")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private CaixaCabecalho caixaCabecalho;

	@Column(name = "dt_movimento")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataMovimento;

	@ManyToOne
	@JoinColumn(name = "cd_historico")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Historico historico;

	@OneToMany(mappedBy = "diarioCabecalho", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<DiarioDetalhe> diarioDetalhes;

	@Column(name = "cd_caixacabecalho", insertable = false, updatable = false)
	private Long codigoCaixaCabecalho;

	@Column(name = "cd_historico", insertable = false, updatable = false)
	private Long codigoHistorico;

	@OneToMany(mappedBy = "diarioCabecalho")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<VinculoEntrada> vinculoSaidas;

	@OneToMany(mappedBy = "diarioCabecalho")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<VinculoSaida> vinculoEntradas;

	@OneToOne(mappedBy = "diarioCabecalho")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private VinculoEntrada vinculoEntrada;

	@OneToOne(mappedBy = "diarioCabecalho")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private VinculoSaida vinculoSaida;

}