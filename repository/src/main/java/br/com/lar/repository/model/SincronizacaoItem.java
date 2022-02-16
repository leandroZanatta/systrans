package br.com.lar.repository.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "tb_sincronizacaoitem")
@SequenceGenerator(name = "GEN_SINCRONIZACAOITEM", allocationSize = 1, sequenceName = "GEN_SINCRONIZACAOITEM")
public class SincronizacaoItem implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_SINCRONIZACAOITEM")
	@Column(name = "id_sincronizacaoitem")
	private Long idSincronizacao;

	@Column(name = "cd_tabela")
	private Long codigoTabela;

	@Column(name = "nr_sincronizacaoversao")
	private Long sincronizacaoVersao;
}