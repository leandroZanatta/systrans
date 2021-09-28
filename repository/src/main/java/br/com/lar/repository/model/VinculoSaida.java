package br.com.lar.repository.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@Table(name = "tb_vinculosaida")
@SequenceGenerator(name = "GEN_VINCULOSAIDA", allocationSize = 1, sequenceName = "GEN_VINCULOSAIDA")
public class VinculoSaida implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_VINCULOSAIDA")
	@Column(name = "id_vinculosaida")
	private Long idVinculoSaida;

	@ManyToOne
	@JoinColumn(name = "cd_faturamento")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private FaturamentoCabecalho faturamento;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "cd_diariocabecalho")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private DiarioCabecalho diarioCabecalho;

}