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
@Table(name = "tb_salario")
@SequenceGenerator(name = "GEN_SALARIO", sequenceName = "GEN_SALARIO", allocationSize = 1)
public class Salario implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_SALARIO")
	@Column(name = "id_salario")
	private Long idSalario;

	@ManyToOne
	@JoinColumn(name = "cd_funcionario")
	private Funcionario funcionario;

	@Column(name = "dt_alteracao")
	@Temporal(TemporalType.DATE)
	private Date dataAlteracao;

	@Column(name = "vl_salario")
	private BigDecimal valorSalario;

}