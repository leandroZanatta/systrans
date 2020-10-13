package br.com.lar.startup.enumeradores;

import java.util.HashMap;
import java.util.Map;

import com.mysema.query.types.path.EntityPathBase;

import br.com.lar.repository.model.QCidade;
import br.com.lar.repository.model.QCliente;
import br.com.lar.repository.model.QContasReceber;
import br.com.lar.repository.model.QEstado;
import br.com.lar.repository.model.QFormasPagamento;
import br.com.lar.repository.model.QFuncionario;
import br.com.lar.repository.model.QGrupo;
import br.com.lar.repository.model.QHistorico;
import br.com.lar.repository.model.QMotorista;
import br.com.lar.repository.model.QVeiculo;
import br.com.sysdesc.pesquisa.repository.model.QPerfil;
import br.com.sysdesc.pesquisa.repository.model.QPesquisa;
import br.com.sysdesc.pesquisa.repository.model.QUsuario;

public enum PesquisaEnum {

	PES_USUARIOS(1L, "Usuários", QUsuario.class),

	PES_PERFIL(2L, "Perfis", QPerfil.class),

	PES_ESTADOS(3L, "Estados", QEstado.class),

	PES_VEICULOS(4L, "Veículos", QVeiculo.class),

	PES_FUNCIONARIOS(6L, "Funcionários", QFuncionario.class),

	PES_GRUPO(7L, "Grupos", QGrupo.class),

	PES_CIDADES(8L, "Cidades", QCidade.class),

	PES_CLIENTES(9L, "Clientes", QCliente.class),

	PES_PESQUISA(10L, "Pesquisa", QPesquisa.class),

	PES_MOTORISTA(13L, "Motoristas", QMotorista.class),

	PES_HISTORICO(11L, "Histórico", QHistorico.class),

	PES_FORMAS_PAGAMENTO(12L, "Formas de Pagamento", QFormasPagamento.class),

	PES_CONTAS_RECEBER(15L, "Contas Ã¡ Receber", QContasReceber.class);

	private static Map<Long, PesquisaEnum> map = new HashMap<>();

	private final Long codigoPesquisa;

	private final String descricaoPesquisa;

	private final Class<? extends EntityPathBase<?>> entityPath;

	static {
		for (PesquisaEnum pesquisaEnum : PesquisaEnum.values()) {
			map.put(pesquisaEnum.codigoPesquisa, pesquisaEnum);
		}
	}

	private PesquisaEnum(Long codigoPesquisa, String descricaoPesquisa, Class<? extends EntityPathBase<?>> entityPath) {
		this.codigoPesquisa = codigoPesquisa;
		this.descricaoPesquisa = descricaoPesquisa;
		this.entityPath = entityPath;
	}

	public static PesquisaEnum forValue(Long value) {
		return map.get(value);
	}

	public Long getCodigoPesquisa() {
		return codigoPesquisa;
	}

	public String getDescricaoPesquisa() {
		return descricaoPesquisa;
	}

	public Class<? extends EntityPathBase<?>> getEntityPath() {
		return entityPath;
	}

	@Override
	public String toString() {
		return descricaoPesquisa;
	}
}
