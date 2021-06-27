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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@Table(name = "tb_caixasaldo")
@SequenceGenerator(name = "GEN_CAIXASALDO", sequenceName = "GEN_CAIXASALDO", allocationSize = 1)
public class CaixaSaldo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_CAIXASALDO")
	@Column(name = "id_caixasaldo")
	private Long idCaixaSaldo;

	@Column(name = "dt_movimento")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataMovimento;

	@OneToOne
	@JoinColumn(name = "cd_caixacabecalho")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private CaixaCabecalho caixaCabecalho;

	@Column(name = "cd_caixacabecalho", insertable = false, updatable = false)
	private Long codigoCaixaCabecalho;

	@Column(name = "vl_saldo")
	private BigDecimal valorSaldo;

	@Column(name = "vl_saldoacumulado")
	private BigDecimal valorSaldoAcumulado;

}