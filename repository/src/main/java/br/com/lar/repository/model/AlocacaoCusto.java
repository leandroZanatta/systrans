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
@Table(name = "tb_alocacaocusto")
@SequenceGenerator(name = "GEN_ALOCACAOCUSTO", allocationSize = 1, sequenceName = "GEN_ALOCACAOCUSTO")
public class AlocacaoCusto implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_ALOCACAOCUSTO")
	@Column(name = "id_alocacaocusto")
	private Long idAlocacaoCusto;

	@ManyToOne
	@JoinColumn(name = "cd_historicocusto")
	private HistoricoCusto historicoCusto;

	@ManyToOne
	@JoinColumn(name = "cd_centrocusto")
	private CentroCusto centroCusto;

	@ManyToOne
	@JoinColumn(name = "cd_veiculo")
	private Veiculo veiculo;

	@ManyToOne
	@JoinColumn(name = "cd_motorista")
	private Motorista motorista;

	@Column(name = "fl_tipocusto")
	private String flagTipoCusto;

	@Column(name = "dt_periodo")
	@Temporal(TemporalType.DATE)
	private Date periodo;

	@Column(name = "nr_parcela")
	private Long numeroParcela;

	@Column(name = "vl_parcela")
	private BigDecimal valorParcela;
}