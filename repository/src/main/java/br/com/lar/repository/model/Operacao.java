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
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@Table(name = "tb_operacao")
@SequenceGenerator(name = "GEN_OPERACAO", allocationSize = 1, sequenceName = "GEN_OPERACAO")
public class Operacao implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_OPERACAO")
	@Column(name = "id_operacao")
	private Long idOperacao;

	@ManyToOne
	@JoinColumn(name = "cd_historico")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Historico historico;

	@ManyToOne
	@JoinColumn(name = "cd_formapagamento")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private FormasPagamento formasPagamento;

	@ManyToOne
	@JoinColumn(name = "cd_contacredora")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private PlanoContas contaCredora;

	@Column(name = "tx_descricao")
	private String descricao;

	@ManyToOne
	@JoinColumn(name = "cd_contadevedora")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private PlanoContas contaDevedora;

	@Column(name = "cd_contacredora", insertable = false, updatable = false)
	private Long codigoContaCredora;

	@Column(name = "cd_contadevedora", insertable = false, updatable = false)
	private Long codigoContaDevedora;

	@Column(name = "cd_formapagamento", insertable = false, updatable = false)
	private Long codigoFormaPagamento;

	@Column(name = "cd_historico", insertable = false, updatable = false)
	private Long codigoHistorico;
}