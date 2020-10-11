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

import br.com.sysdesc.pesquisa.repository.model.Perfil;
import br.com.sysdesc.pesquisa.repository.model.Usuario;
import lombok.Data;

@Data
@Entity
@Table(name = "tb_permissaoprograma")
@SequenceGenerator(name = "GEN_PERMISSAOPROGRAMA", sequenceName = "GEN_PERMISSAOPROGRAMA", allocationSize = 1)
public class PermissaoPrograma implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_PERMISSAOPROGRAMA")
	@Column(name = "id_permissaoprograma")
	private Long idPermissaoprograma;

	@Column(name = "cd_usuario")
	private Long codigoUsuario;

	@Column(name = "cd_perfil")
	private Long codigoPerfil;

	@Column(name = "cd_programa")
	private Long codigoPrograma;

	@Column(name = "fl_cadastro")
	private Boolean flagCadastro;

	@Column(name = "fl_exclusao")
	private Boolean flagExclusao;

	@Column(name = "fl_leitura")
	private Boolean flagLeitura;

	@ManyToOne
	@JoinColumn(name = "cd_perfil", insertable = false, updatable = false)
	private Perfil perfil;

	@ManyToOne
	@JoinColumn(name = "cd_usuario", insertable = false, updatable = false)
	private Usuario usuario;

	@ManyToOne
	@JoinColumn(name = "cd_programa", insertable = false, updatable = false)
	private Programa programa;

}