package br.com.lar.service.veiculo;

import br.com.lar.repository.dao.VeiculoDAO;
import br.com.lar.repository.model.Veiculo;
import br.com.sysdesc.pesquisa.service.impl.AbstractPesquisableServiceImpl;
import br.com.sysdesc.util.classes.BigDecimalUtil;
import br.com.sysdesc.util.classes.LongUtil;
import br.com.sysdesc.util.classes.StringUtil;
import br.com.sysdesc.util.constants.MensagemConstants;
import br.com.sysdesc.util.exception.SysDescException;

public class VeiculoService extends AbstractPesquisableServiceImpl<Veiculo> {

	private VeiculoDAO veiculoDAO;

	public VeiculoService() {
		this(new VeiculoDAO());
	}

	public VeiculoService(VeiculoDAO veiculoDAO) {
		super(veiculoDAO, Veiculo::getIdVeiculo);

		this.veiculoDAO = veiculoDAO;
	}

	@Override
	public void validar(Veiculo objetoPersistir) {

		if (LongUtil.isNullOrZero(objetoPersistir.getTipoVeiculo())) {

			throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_ESTADO);
		}

		if (StringUtil.isNullOrEmpty(objetoPersistir.getPlaca())) {

			throw new SysDescException(MensagemConstants.MENSAGEM_INSIRA_DESCRICAO_VALIDA);
		}

		if (BigDecimalUtil.isNullOrZero(objetoPersistir.getCapacidade())) {

			throw new SysDescException(MensagemConstants.MENSAGEM_INSIRA_DESCRICAO_VALIDA);
		}
	}

}
