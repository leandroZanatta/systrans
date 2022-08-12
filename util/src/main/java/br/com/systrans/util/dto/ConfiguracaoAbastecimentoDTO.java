package br.com.systrans.util.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfiguracaoAbastecimentoDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long idConfiguracaoAbastecimento;
	private Long codigoVeiculo;
	private String historico;
	private Long codigoFormaPagamento;
	private Long sincronizacaoVersao;
}
