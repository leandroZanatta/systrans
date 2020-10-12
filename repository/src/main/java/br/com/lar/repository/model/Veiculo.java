package br.com.lar.repository.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

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

	@Column(name = "cd_tipoveiculo")
	private Long tipoVeiculo;

	@Column(name = "tx_placa")
	private String placa;

	@Column(name = "tx_capacidade")
	private BigDecimal capacidade;

}