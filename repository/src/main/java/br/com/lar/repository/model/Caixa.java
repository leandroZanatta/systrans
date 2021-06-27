package br.com.lar.repository.model;

import java.io.Serializable;
import java.util.List;

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
@Table(name = "tb_caixa")
@SequenceGenerator(name = "GEN_CAIXA", sequenceName = "GEN_CAIXA", allocationSize = 1)
public class Caixa implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_CAIXA")
	@Column(name = "id_caixa")
	private Long idCaixa;

	@ManyToOne
	@JoinColumn(name = "cd_usuario")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Usuario usuario;

	@Column(name = "tx_descricao")
	private String descricao;

	@Column(name = "cd_usuario", insertable = false, updatable = false)
	private Long codigoUsuario;

	@OneToMany(mappedBy = "caixa")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<CaixaCabecalho> caixaCabecalhos;

}