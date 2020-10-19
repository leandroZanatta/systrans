package br.com.lar.repository.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tb_faturamentotransporte")
public class FaturamentoTransporte implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Long codigoFaturamento;

	@OneToOne
	@MapsId
	@JoinColumn(name = "cd_faturamento")
	private Faturamento faturamento;

	@ManyToOne
	@JoinColumn(name = "cd_veiculo")
	private Veiculo veiculo;

	@ManyToOne
	@JoinColumn(name = "cd_motorista")
	private Motorista motorista;

	@Column(name = "nr_odometroinicial")
	private Long odometroInicial;

	@Column(name = "nr_odometrofinal")
	private Long odometroFinal;
}