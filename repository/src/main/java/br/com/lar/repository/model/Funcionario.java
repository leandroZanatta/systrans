package br.com.lar.repository.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

@Data
@Entity
@Table(name = "tb_funcionario")
@SequenceGenerator(name = "GEN_FUNCIONARIO", sequenceName = "GEN_FUNCIONARIO", allocationSize = 1)
public class Funcionario implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_FUNCIONARIO")
	@Column(name = "id_funcionario")
	private Long idFuncionario;

	@ManyToOne
	@JoinColumn(name = "cd_cliente")
	private Cliente cliente;

	@Column(name = "tx_cargo")
	private String cargo;

	@Column(name = "vl_salario")
	private BigDecimal salario;

	@Column(name = "dt_admissao")
	@Temporal(TemporalType.DATE)
	private Date dataAdmissao;

	@Column(name = "dt_demissao")
	@Temporal(TemporalType.DATE)
	private Date dataDemissao;

}