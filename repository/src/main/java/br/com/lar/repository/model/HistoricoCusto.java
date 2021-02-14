package br.com.lar.repository.model;

import java.io.Serializable;
import java.util.List;

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

import lombok.Data;
import lombok.ToString;

@Data
@Entity
@Table(name = "tb_historicocusto")
@SequenceGenerator(name = "GEN_HISTORICOCUSTO", allocationSize = 1, sequenceName = "GEN_HISTORICOCUSTO")
public class HistoricoCusto implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_HISTORICOCUSTO")
	@Column(name = "id_historicocusto")
	private Long idHistoricoCusto;

	@ManyToOne
	@JoinColumn(name = "cd_historico")
	private Historico historico;

	@Column(name = "cd_historico", insertable = false, updatable = false)
	private Long codigoHistorico;

	@Column(name = "fl_tipocusto")
	private String flagTipoCusto;

	@Column(name = "nr_mesesalocacao")
	private Long mesesAlocacao;

	@OneToMany(mappedBy = "historicoCusto")
	@ToString.Exclude
	private List<AlocacaoCusto> alocacaoCustos;
}