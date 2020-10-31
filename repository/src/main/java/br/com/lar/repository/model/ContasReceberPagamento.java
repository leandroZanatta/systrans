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
@Table(name = "tb_contasreceberpagamento")
@SequenceGenerator(name = "GEN_CONTASRECEBERPAGAMENTO", sequenceName = "GEN_CONTASRECEBERPAGAMENTO", allocationSize = 1)
public class ContasReceberPagamento implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id_contasreceberpagamento")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_CONTASRECEBERPAGAMENTO")
	private Long idContasReceberPagamento;

	@ManyToOne
	@JoinColumn(name = "cd_caixacabecalho")
	private CaixaCabecalho caixaCabecalho;

	@ManyToOne
	@JoinColumn(name = "cd_contasreceber")
	private ContasReceber contasReceber;

	@ManyToOne
	@JoinColumn(name = "cd_formaspagamento")
	private FormasPagamento formasPagamento;

	@ManyToOne
	@JoinColumn(name = "cd_historico")
	private Historico historico;

	@Column(name = "dt_pagamento")
	@Temporal(TemporalType.DATE)
	private Date dataPagamento;

	@Column(name = "vl_desconto")
	private BigDecimal valorDesconto;

	@Column(name = "vl_acrescimo")
	private BigDecimal valorAcrescimo;

	@Column(name = "vl_juros")
	private BigDecimal valorJuros;

	@Column(name = "vl_parcela")
	private BigDecimal valorPago;

}