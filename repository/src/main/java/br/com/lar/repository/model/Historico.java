package br.com.lar.repository.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "tb_historico")
@SequenceGenerator(name = "GEN_HISTORICO", allocationSize = 1, sequenceName = "GEN_HISTORICO")
public class Historico implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_HISTORICO")
	@Column(name = "id_historico")
	private Long idHistorico;

	@Column(name = "cd_tipohistorico")
	private Long tipoHistorico;

	@Column(name = "tx_descricao")
	private String descricao;

	@OneToMany(mappedBy = "historico")
	private List<Operacao> operacoes;

	@OneToMany(mappedBy = "historico")
	private List<Faturamento> faturamentos;

	@OneToMany(mappedBy = "historico")
	private List<HistoricoCusto> historicoCustos;

}