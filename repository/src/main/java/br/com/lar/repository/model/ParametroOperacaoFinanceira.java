package br.com.lar.repository.model;

import java.io.Serializable;

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
@Table(name = "tb_parametrooperacaofinanceira")
@SequenceGenerator(name = "GEN_PARAMETROOPERACAOFINANCEIRA", allocationSize = 1, sequenceName = "GEN_PARAMETROOPERACAOFINANCEIRA")
public class ParametroOperacaoFinanceira implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_PARAMETROOPERACAOFINANCEIRA")
	@Column(name = "id_parametrooperacaofinanceira")
	private Long idParametroOperacaoFinanceiro;

	@Column(name = "cd_tipofinanceiro")
	private Long codigoTipoFinanceiro;

	@Column(name = "cd_tipoconta")
	private Long codigoTipoConta;

	@ManyToOne
	@JoinColumn(name = "cd_historico")
	private Historico historico;

	@ManyToOne
	@JoinColumn(name = "cd_formaspagamento")
	private FormasPagamento formasPagamento;

	@ManyToOne
	@JoinColumn(name = "cd_contacredora")
	private PlanoContas contaCredora;

	@ManyToOne
	@JoinColumn(name = "cd_contadevedora")
	private PlanoContas contaDevedora;

	@Column(name = "cd_contacredora", insertable = false, updatable = false)
	private Long codigoContaCredora;

	@Column(name = "cd_contadevedora", insertable = false, updatable = false)
	private Long codigoContaDevedora;

	@Column(name = "cd_formaspagamento", insertable = false, updatable = false)
	private Long codigoPagamento;

	@Column(name = "cd_historico", insertable = false, updatable = false)
	private Long codigoHistorico;
}