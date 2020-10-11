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
@Table(name = "tb_grupo")
@SequenceGenerator(name = "GEN_GRUPO", allocationSize = 1, sequenceName = "GEN_GRUPO")
public class Grupo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_GRUPO")
	@Column(name = "id_grupo")
	private Long idGrupo;

	@Column(name = "tx_descricao")
	private String descricao;

	@Column(name = "tx_configuracao")
	private String configuracao;

	@OneToMany(mappedBy = "grupo")
	private List<Cliente> clientes;

}