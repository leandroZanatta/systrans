package br.com.lar.repository.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

@Data
@Entity
@Table(name = "tb_versao")
@SequenceGenerator(name = "GEN_VERSAO", sequenceName = "GEN_VERSAO", allocationSize = 1)
public class Versao implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id_versao")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_VERSAO")
	private Long idVersao;

	@Column(name = "nr_versao")
	private String numeroVersao;

	@Column(name = "dt_atualizacao")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataAtualizacao;
}