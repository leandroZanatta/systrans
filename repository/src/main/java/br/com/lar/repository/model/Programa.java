package br.com.lar.repository.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "tb_programa")
public class Programa implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id_programa")
	private Long idPrograma;

	@Column(name = "cd_programa", insertable = false, updatable = false)
	private Long codigoModulo;

	@Column(name = "cd_aplicativo")
	private Long codigoAplicativo;

	@Column(name = "nr_ordem")
	private Long ordem;

	@ManyToOne
	@JoinColumn(name = "cd_programa")
	private Programa programaPai;

	@Column(name = "tx_descricao")
	private String descricao;

	@Column(name = "tx_icone")
	private String icone;

	@Column(name = "fl_acessorapido")
	private boolean flagAcessoRapido;

	@OneToMany(mappedBy = "programaPai", fetch = FetchType.EAGER)
	private List<Programa> programas;

	@OneToMany(mappedBy = "programa")
	private List<PermissaoPrograma> permissaoProgramas;

}