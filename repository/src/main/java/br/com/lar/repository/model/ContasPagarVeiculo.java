package br.com.lar.repository.model;

import java.io.Serializable;
import java.math.BigDecimal;
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

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@Table(name = "tb_contaspagarveiculo")
@SequenceGenerator(name = "GEN_CONTASPAGARVEICULO", sequenceName = "GEN_CONTASPAGARVEICULO", allocationSize = 1)
public class ContasPagarVeiculo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id_contaspagarveiculo")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_CONTASPAGARVEICULO")
	private Long idContasPagarVeiculo;

	@ManyToOne
	@JoinColumn(name = "cd_contaspagar")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private ContasPagar contasPagar;

	@Column(name = "cd_contaspagar", insertable = false, updatable = false)
	private Long codigoContasPagar;

	@Column(name = "cd_veiculo", insertable = false, updatable = false)
	private Long codigoVeiculo;

	@ManyToOne
	@JoinColumn(name = "cd_veiculo")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Veiculo veiculo;

	@ManyToOne
	@JoinColumn(name = "cd_motorista")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Motorista motorista;

	@Column(name = "tx_documento")
	private String documento;

	@Column(name = "vl_parcela")
	private BigDecimal valorParcela;

	@OneToMany(mappedBy = "contasPagarVeiculo", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<VinculoEntradaContasPagarVeiculo> vinculoEntradaContasPagarVeiculos;

}