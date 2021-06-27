package br.com.lar.repository.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@Table(name = "tb_vinculosaidacontasreceber")
@SequenceGenerator(name = "GEN_VINCULOSAIDACONTASRECEBER", allocationSize = 1, sequenceName = "GEN_VINCULOSAIDACONTASRECEBER")
public class VinculoSaidaContasReceber implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_VINCULOSAIDACONTASRECEBER")
	@Column(name = "id_vinculosaidacontasreceber")
	private Long idVinculoSaidaContasReceber;

	@ManyToOne
	@JoinColumn(name = "cd_faturamento")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private FaturamentoCabecalho faturamento;

	@OneToOne
	@JoinColumn(name = "cd_contasreceber")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private ContasReceber contasReceber;

}