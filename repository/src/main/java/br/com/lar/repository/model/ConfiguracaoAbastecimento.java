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
@Table(name = "tb_configuracaoabastecimento")
@SequenceGenerator(name = "GEN_CONFIGURACAOABASTECIMENTO", allocationSize = 1, sequenceName = "GEN_CONFIGURACAOABASTECIMENTO")
public class ConfiguracaoAbastecimento implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_CONFIGURACAOABASTECIMENTO")
	@Column(name = "id_configuracaoabastecimento")
	private Long idConfiguracaoAbastecimento;

	@Column(name = "cd_veiculo", insertable = false, updatable = false)
	private Long codigoVeiculo;

	@Column(name = "cd_operacao", insertable = false, updatable = false)
	private Long codigoOperacao;

	@Column(name = "cd_centrocusto", insertable = false, updatable = false)
	private Long codigoCentroCusto;

	@ManyToOne
	@JoinColumn(name = "cd_veiculo")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Veiculo veiculo;

	@ManyToOne
	@JoinColumn(name = "cd_operacao")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Operacao operacao;

	@ManyToOne
	@JoinColumn(name = "cd_centrocusto")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private CentroCusto centroCusto;

	@Column(name = "nr_sincronizacaoversao")
	private Long sincronizacaoVersao;

}