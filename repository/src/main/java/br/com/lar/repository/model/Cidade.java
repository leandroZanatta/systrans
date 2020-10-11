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

import lombok.Data;

@Data
@Entity
@Table(name = "tb_cidade")
@SequenceGenerator(name = "GEN_CIDADE", allocationSize = 1, sequenceName = "GEN_CIDADE")
public class Cidade implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_CIDADE")
	@Column(name = "id_cidade")
	private Long idCidade;

	@Column(name = "tx_descricao")
	private String descricao;

	@Column(name = "cd_estado", insertable = false, updatable = false)
	private Long codigoEstado;

	@ManyToOne
	@JoinColumn(name = "cd_estado")
	private Estado estado;

	@OneToMany(mappedBy = "cidade")
	private List<Cliente> clientes;

	@OneToMany(mappedBy = "naturalidade")
	private List<Cliente> naturalidades;

	@Override
	public String toString() {
		return this.descricao;
	}
}