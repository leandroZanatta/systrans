package br.com.systrans.util.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClienteDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private long id;
	private String nome;
	private String cgc;
	private long situacao;
	private Long versao;

}
