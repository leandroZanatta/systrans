package br.com.systrans.util.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VeiculoDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private long id;
	private String placa;
	private Long versao;
}
