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

@Data
@Entity
@Table(name = "tb_documentoentrada")
@SequenceGenerator(name = "GEN_DOCUMENTOENTRADA", allocationSize = 1, sequenceName = "GEN_DOCUMENTOENTRADA")
public class DocumentoEntrada implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_DOCUMENTOENTRADA")
	@Column(name = "id_documentoentrada")
	private Long idDocumentoEntrada;

	@ManyToOne
	@JoinColumn(name = "cd_faturamentoentrada")
	private FaturamentoEntrada faturamentoEntrada;

	@Column(name = "tx_local")
	private String local;

}