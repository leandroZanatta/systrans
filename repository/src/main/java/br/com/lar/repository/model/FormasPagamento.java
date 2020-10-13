package br.com.lar.repository.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "tb_formapagamento")
@SequenceGenerator(name = "GEN_FORMAPAGAMENTO", allocationSize = 1, sequenceName = "GEN_FORMAPAGAMENTO")
public class FormasPagamento implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_FORMAPAGAMENTO")
	@Column(name = "id_formapagamento")
	private Long idFormaPagamento;

	@Column(name = "tx_descricao")
	private String descricao;

	@Column(name = "nr_formapagamento")
	private Long codigoFormaPagamento;

	@Column(name = "fl_permitetroco")
	private boolean flagPermiteTroco;

	@Column(name = "fl_usatef")
	private boolean flagUsaTEF;

	@Column(name = "fl_permitepagamentoprazo")
	private boolean flagPermitePagamentoPrazo;

	@Column(name = "nr_maximoparcelas")
	private Long numeroMaximoParcelas;

	@Column(name = "nr_valorminimopagamento")
	private BigDecimal valorMinimoParcelas;

	@Column(name = "nr_diasparapagamento")
	private Long numeroDiasPagamento;

	@OneToMany(mappedBy = "formasPagamento")
	private List<ContasReceber> contasReceber;

}