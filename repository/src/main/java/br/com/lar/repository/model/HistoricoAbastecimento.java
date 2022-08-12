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
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@Table(name = "tb_historicoabastecimento")
@SequenceGenerator(name = "GEN_HISTORICOABASTECIMENTO", allocationSize = 1, sequenceName = "GEN_HISTORICOABASTECIMENTO")
public class HistoricoAbastecimento implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_HISTORICOABASTECIMENTO")
	@Column(name = "id_historicoabastecimento")
	private Long idHistoricoAbastecimento;

	@Column(name = "cd_veiculo", insertable = false, updatable = false)
	private Long codigoVeiculo;

	@ManyToOne
	@JoinColumn(name = "cd_veiculo")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Veiculo veiculo;

	@Column(name = "dt_historico")
	@Temporal(TemporalType.DATE)
	private Date dataHistorico;

	@Column(name = "vl_faturamentomedio")
	private BigDecimal faturamentoMedio;

	@Column(name = "vl_customedio")
	private BigDecimal custoMédio;

	@Column(name = "vl_margemcontribuicao")
	private BigDecimal margemContribuicao;

	@Column(name = "vl_media")
	private BigDecimal mediaVeiculo;

	@Column(name = "vl_custokm")
	private BigDecimal custoKilometro;

}