package br.com.lar.repository.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

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
	private ContasPagar contasPagar;

	@Column(name = "cd_contaspagar", insertable = false, updatable = false)
	private Long codigoContasPagar;

	@ManyToOne
	@JoinColumn(name = "cd_veiculo")
	private Veiculo veiculo;

	@ManyToOne
	@JoinColumn(name = "cd_motorista")
	private Motorista motorista;

	@Column(name = "tx_documento")
	private String documento;

	@Column(name = "vl_parcela")
	private BigDecimal valorParcela;

}