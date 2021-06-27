package br.com.lar.repository.model;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@Table(name = "tb_motorista")
@SequenceGenerator(name = "GEN_MOTORISTA", sequenceName = "GEN_MOTORISTA", allocationSize = 1)
public class Motorista implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_MOTORISTA")
	@Column(name = "id_motorista")
	private Long idMotorista;

	@ManyToOne
	@JoinColumn(name = "cd_funcionario")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Funcionario funcionario;

	@Column(name = "tx_cnh")
	private String cnh;

	@Column(name = "nr_documento")
	private Long numeroDocumento;

	@Column(name = "dt_vencimento")
	@Temporal(TemporalType.DATE)
	private Date dataVencimento;

	@OneToMany(mappedBy = "motorista")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<Veiculo> veiculos;

	@OneToMany(mappedBy = "motorista")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<FaturamentoDetalhe> faturamentoDetalhes;
}