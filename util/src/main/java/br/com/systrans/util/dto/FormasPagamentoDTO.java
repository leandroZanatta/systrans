package br.com.systrans.util.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FormasPagamentoDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private long id;
	private String descricao;
	private Long versao;
}
