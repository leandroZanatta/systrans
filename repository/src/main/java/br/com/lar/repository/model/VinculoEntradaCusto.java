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

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tb_vinculoentradacusto")
@SequenceGenerator(name = "GEN_VINCULOENTRADACUSTO", allocationSize = 1, sequenceName = "GEN_VINCULOENTRADACUSTO")
public class VinculoEntradaCusto implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_VINCULOENTRADACUSTO")
	@Column(name = "id_vinculoentradacusto")
	private Long idVinculoEntradaCusto;

	@ManyToOne
	@JoinColumn(name = "cd_faturamentoentradasdetalhe")
	private FaturamentoEntradasDetalhe faturamentoEntradasDetalhe;

	@OneToOne
	@JoinColumn(name = "cd_alocacaocusto")
	private AlocacaoCusto alocacaoCusto;

	@Column(name = "cd_faturamentoentradasdetalhe", insertable = false, updatable = false)
	private Long codigoFaturamentoEntradasDetalhe;

	@Column(name = "cd_alocacaocusto", insertable = false, updatable = false)
	private Long codigoAlocacaoCusto;

}