package br.com.lar.repository.model;

import java.io.Serializable;
import java.math.BigDecimal;
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
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@Table(name = "tb_veiculo")
@SequenceGenerator(name = "GEN_VEICULO", allocationSize = 1, sequenceName = "GEN_VEICULO")
public class Veiculo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_VEICULO")
	@Column(name = "id_veiculo")
	private Long idVeiculo;

	@ManyToOne
	@JoinColumn(name = "cd_motorista")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Motorista motorista;

	@Column(name = "cd_tipoveiculo")
	private Long tipoVeiculo;

	@Column(name = "tx_placa")
	private String placa;

	@Column(name = "tx_marca")
	private String marca;

	@Column(name = "tx_modelo")
	private String modelo;

	@Column(name = "nr_ano")
	private Long numeroAno;

	@Column(name = "tx_capacidade")
	private BigDecimal capacidade;

	@Column(name = "nr_sincronizacaoversao")
	private Long sincronizacaoVersao;

	@OneToMany(mappedBy = "veiculo")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<FaturamentoDetalhe> faturamentoDetalhes;

}