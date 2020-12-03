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
@Table(name = "tb_contasreceberveiculo")
@SequenceGenerator(name = "GEN_CONTASRECEBERVEICULO", sequenceName = "GEN_CONTASRECEBERVEICULO", allocationSize = 1)
public class ContasReceberVeiculo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id_contasreceberveiculo")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_CONTASRECEBERVEICULO")
	private Long idContasReceberVeiculo;

	@ManyToOne
	@JoinColumn(name = "cd_contasreceber")
	private ContasReceber contasReceber;

	@Column(name = "cd_contasreceber", insertable = false, updatable = false)
	private Long codigoContasReceber;

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