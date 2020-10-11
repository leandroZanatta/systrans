package br.com.lar.repository.model;

import java.io.Serializable;
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
@Table(name = "tb_estado")
@SequenceGenerator(name = "GEN_ESTADO", allocationSize = 1, sequenceName = "GEN_ESTADO")
public class Estado implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_ESTADO")
	@Column(name = "id_estado")
	private Long idEstado;

	@Column(name = "tx_descricao")
	private String descricao;

	@Column(name = "tx_uf")
	private String uf;

	@OneToMany(mappedBy = "estado")
	private List<Cidade> cidades;

	@Override
	public String toString() {
		return this.descricao;
	}

}