package br.com.lar.repository.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.com.sysdesc.pesquisa.repository.model.Usuario;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@Table(name = "tb_abastecimentoveiculo")
@SequenceGenerator(name = "GEN_ABASTECIMENTOVEICULO", allocationSize = 1, sequenceName = "GEN_ABASTECIMENTOVEICULO")
public class AbastecimentoVeiculo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_ABASTECIMENTOVEICULO")
	@Column(name = "id_abastecimentoveiculo")
	private Long idAbastecimentoVeiculo;

	@Column(name = "cd_configuracaoabastecimento", insertable = false, updatable = false)
	private Long codigoConfiguracaoAbastecimento;

	@ManyToOne
	@JoinColumn(name = "cd_cliente")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Cliente cliente;

	@ManyToOne
	@JoinColumn(name = "cd_configuracaoabastecimento")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private ConfiguracaoAbastecimento configuracaoAbastecimento;

	@ManyToOne
	@JoinColumn(name = "cd_usuario")
	private Usuario usuario;

	@Column(name = "tx_documento")
	private String numeroDocumento;

	@Column(name = "vl_kminicial")
	private Long kilometroInicial;

	@Column(name = "vl_kmfinal")
	private Long kilometroFinal;

	@Column(name = "vl_abastecimento")
	private BigDecimal valorAbastecimento;

	@Column(name = "vl_litros")
	private BigDecimal litrosAbastecidos;

	@Column(name = "fl_abastecimentoparcial")
	private Boolean abastecimentoParcial;

	@OneToMany(mappedBy = "abastecimentoVeiculo", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<AbastecimentoPagamento> abastecimentoPagamentos;

}