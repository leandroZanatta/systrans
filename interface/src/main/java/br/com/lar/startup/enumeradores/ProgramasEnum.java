package br.com.lar.startup.enumeradores;

import java.util.HashMap;
import java.util.Map;

import br.com.lar.ui.FrmCadastroPesquisa;
import br.com.lar.ui.FrmCidade;
import br.com.lar.ui.FrmCliente;
import br.com.lar.ui.FrmConsultarContasReceber;
import br.com.lar.ui.FrmEstado;
import br.com.lar.ui.FrmFormasPagamento;
import br.com.lar.ui.FrmFuncionario;
import br.com.lar.ui.FrmGerarContasReceber;
import br.com.lar.ui.FrmGrupo;
import br.com.lar.ui.FrmHistorico;
import br.com.lar.ui.FrmMotorista;
import br.com.lar.ui.FrmPerfil;
import br.com.lar.ui.FrmPermissoes;
import br.com.lar.ui.FrmUsuario;
import br.com.lar.ui.FrmVeiculo;
import br.com.sysdesc.components.AbstractInternalFrame;

public enum ProgramasEnum {

	CADASTRO_PERFIS(2L, FrmPerfil.class),

	CADASTRO_ESTADOS(3L, FrmEstado.class),

	CADASTRO_CIDADE(4L, FrmCidade.class),

	CADASTRO_CLIENTES(5L, FrmCliente.class),

	CADASTRO_PESQUISA(8L, FrmCadastroPesquisa.class),

	CADASTRO_USUARIOS(9L, FrmUsuario.class),

	CADASTRO_PERMISSOES(10L, FrmPermissoes.class),

	CADASTRO_VEICULOS(11L, FrmVeiculo.class),

	CADASTRO_MOTORISTAS(12L, FrmMotorista.class),

	CADASTRO_FUNCIONARIOS(13L, FrmFuncionario.class),

	CADASTRO_GRUPOS(14L, FrmGrupo.class),

	CADASTRO_FORMAS_PAGAMENTO(19L, FrmFormasPagamento.class),

	CADASTRO_HISTORICOS(20L, FrmHistorico.class),

	CADASTRO_CONTAS_RECEBER(26L, FrmGerarContasReceber.class),

	CONSULTA_CONTAS_RECEBER(27L, FrmConsultarContasReceber.class);

	private static Map<Long, ProgramasEnum> mapa = new HashMap<>();

	static {

		for (ProgramasEnum programa : ProgramasEnum.values()) {
			mapa.put(programa.getCodigo(), programa);
		}
	}

	private final Long codigo;

	private final Class<? extends AbstractInternalFrame> internalFrame;

	ProgramasEnum(Long codigo, Class<? extends AbstractInternalFrame> internalFrame) {
		this.codigo = codigo;
		this.internalFrame = internalFrame;
	}

	public Long getCodigo() {
		return codigo;
	}

	public Class<? extends AbstractInternalFrame> getInternalFrame() {
		return internalFrame;
	}

	public static ProgramasEnum findByCodigo(Long codigoPrograma) {
		return mapa.get(codigoPrograma);
	}
}
