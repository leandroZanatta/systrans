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
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@Table(name = "tb_centrocusto")
@SequenceGenerator(name = "GEN_CENTROCUSTO", allocationSize = 1, sequenceName = "GEN_CENTROCUSTO")
public class CentroCusto implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_CENTROCUSTO")
	@Column(name = "id_centrocusto")
	private Long idCentroCusto;

	@Column(name = "tx_descricao")
	private String descricao;

	@OneToMany(mappedBy = "centroCusto")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<AlocacaoCusto> alocacaoCustos;
}