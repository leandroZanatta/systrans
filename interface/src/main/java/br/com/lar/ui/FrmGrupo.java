package br.com.lar.ui;

import java.awt.Color;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import com.google.gson.Gson;

import br.com.lar.repository.model.Grupo;
import br.com.lar.service.grupo.GrupoService;
import br.com.lar.startup.enumeradores.PesquisaEnum;
import br.com.sysdesc.components.AbstractInternalFrame;
import br.com.sysdesc.components.JTextFieldId;
import br.com.sysdesc.components.JTextFieldMaiusculo;
import br.com.sysdesc.components.listeners.adapter.ButtonActionListenerAdapter;
import br.com.sysdesc.pesquisa.ui.components.PanelActions;
import br.com.sysdesc.util.vo.CampoClientesHabilitadosVO;
import net.miginfocom.swing.MigLayout;

public class FrmGrupo extends AbstractInternalFrame {

	private static final long serialVersionUID = 1L;

	private JPanel painelContent;

	private JLabel lblCodigo;
	private JLabel lblDescricao;

	private JTextFieldId txCodigo;
	private JTextFieldMaiusculo txDescricao;

	private JCheckBox chckbxRg;
	private JCheckBox chckbxDataNascimento;
	private JCheckBox chckbxNomePai;
	private JCheckBox chckbxNomeMe;
	private JCheckBox chckbxCidade;
	private JCheckBox chckbxEndereo;
	private JCheckBox chckbxNmero;
	private JCheckBox chckbxNaturalidade;
	private JCheckBox chckbxBairro;
	private JCheckBox chckbxCep;
	private JCheckBox chckbxCelular;
	private JCheckBox chckbxEmail;
	private JCheckBox chckbxEstadoCivil;
	private JCheckBox chckbxEscolaridade;
	private JCheckBox chckbxSexo;
	private JCheckBox chckbxRaacor;
	private JCheckBox chckbxReligio;

	private GrupoService grupoService = new GrupoService();
	private PanelActions<Grupo> panelActions;

	public FrmGrupo(Long permissaoPrograma, Long codigoUsuario) {
		super(permissaoPrograma, codigoUsuario);

		initComponents();
	}

	private void initComponents() {

		setSize(670, 300);
		setClosable(Boolean.TRUE);
		setTitle("CADASTRO DE GRUPOS");

		painelContent = new JPanel();

		lblCodigo = new JLabel("Código:");
		lblDescricao = new JLabel("Descrição");

		txCodigo = new JTextFieldId();
		txDescricao = new JTextFieldMaiusculo();

		JPanel panelPermissoes = new JPanel();

		panelPermissoes.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null),
				"Campos obrigatórios de Clientes", TitledBorder.CENTER, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panelPermissoes.setLayout(new MigLayout("", "[grow][grow][grow][grow][grow][grow]", "[][][]"));
		painelContent.setLayout(new MigLayout("", "[grow]", "[][][][][109.00][42.00,grow]"));

		painelContent.add(lblCodigo, "cell 0 0");
		painelContent.add(txCodigo, "cell 0 1,width 50:100:100");
		painelContent.add(lblDescricao, "cell 0 2");
		painelContent.add(txDescricao, "cell 0 3,growx");
		painelContent.add(panelPermissoes, "cell 0 4,grow");
		chckbxRg = new JCheckBox("Rg");
		panelPermissoes.add(chckbxRg, "cell 0 0");
		chckbxDataNascimento = new JCheckBox("Data Nascimento");
		panelPermissoes.add(chckbxDataNascimento, "cell 1 0");
		chckbxEndereo = new JCheckBox("Endereço");
		panelPermissoes.add(chckbxEndereo, "cell 2 0");
		chckbxCep = new JCheckBox("CEP");
		panelPermissoes.add(chckbxCep, "cell 3 0");
		chckbxEstadoCivil = new JCheckBox("Estado Civil");
		panelPermissoes.add(chckbxEstadoCivil, "cell 4 0");
		chckbxEscolaridade = new JCheckBox("Escolaridade");
		panelPermissoes.add(chckbxEscolaridade, "cell 5 0");
		chckbxNomeMe = new JCheckBox("Nome Mãe");
		panelPermissoes.add(chckbxNomeMe, "cell 0 1");
		chckbxNaturalidade = new JCheckBox("Naturalidade");
		panelPermissoes.add(chckbxNaturalidade, "cell 1 1");
		chckbxNmero = new JCheckBox("Número");
		panelPermissoes.add(chckbxNmero, "cell 2 1");
		chckbxEmail = new JCheckBox("Email");
		panelPermissoes.add(chckbxEmail, "cell 3 1");
		chckbxSexo = new JCheckBox("Sexo");
		panelPermissoes.add(chckbxSexo, "cell 4 1");
		chckbxRaacor = new JCheckBox("Raça/Cor");
		panelPermissoes.add(chckbxRaacor, "cell 5 1");
		chckbxNomePai = new JCheckBox("Nome Pai");
		panelPermissoes.add(chckbxNomePai, "cell 0 2");
		chckbxCidade = new JCheckBox("Cidade");
		panelPermissoes.add(chckbxCidade, "cell 1 2");
		chckbxBairro = new JCheckBox("Bairro");
		panelPermissoes.add(chckbxBairro, "cell 2 2");
		chckbxCelular = new JCheckBox("Celular");
		panelPermissoes.add(chckbxCelular, "cell 3 2");
		chckbxReligio = new JCheckBox("Religião");
		panelPermissoes.add(chckbxReligio, "cell 4 2");
		getContentPane().add(painelContent);

		panelActions = new PanelActions<Grupo>(this, grupoService, PesquisaEnum.PES_GRUPO.getCodigoPesquisa()) {

			private static final long serialVersionUID = 1L;

			@Override
			public void carregarObjeto(Grupo objeto) {

				txCodigo.setValue(objeto.getIdGrupo());
				txDescricao.setText(objeto.getDescricao());
				preencherCamposObrigatorios(
						new Gson().fromJson(objeto.getConfiguracao(), CampoClientesHabilitadosVO.class));
			}

			@Override
			public boolean preencherObjeto(Grupo objetoPesquisa) {

				objetoPesquisa.setIdGrupo(txCodigo.getValue());
				objetoPesquisa.setDescricao(txDescricao.getText());
				objetoPesquisa.setConfiguracao(new Gson().toJson(preencherObjeto()));

				return true;
			}

			private CampoClientesHabilitadosVO preencherObjeto() {

				CampoClientesHabilitadosVO campoClientes = new CampoClientesHabilitadosVO();

				campoClientes.setRg(chckbxRg.isSelected());
				campoClientes.setDataNascimento(chckbxDataNascimento.isSelected());
				campoClientes.setNomePai(chckbxNomePai.isSelected());
				campoClientes.setNomeMae(chckbxNomeMe.isSelected());
				campoClientes.setCidade(chckbxCidade.isSelected());
				campoClientes.setEndereco(chckbxEndereo.isSelected());
				campoClientes.setNumero(chckbxNmero.isSelected());
				campoClientes.setNaturalidade(chckbxNaturalidade.isSelected());
				campoClientes.setBairro(chckbxBairro.isSelected());
				campoClientes.setCep(chckbxCep.isSelected());
				campoClientes.setCelular(chckbxCelular.isSelected());
				campoClientes.setEmail(chckbxEmail.isSelected());
				campoClientes.setEstadoCivil(chckbxEstadoCivil.isSelected());
				campoClientes.setEscolaridade(chckbxEscolaridade.isSelected());
				campoClientes.setSexo(chckbxSexo.isSelected());
				campoClientes.setRaca(chckbxRaacor.isSelected());
				campoClientes.setReligiao(chckbxReligio.isSelected());

				return campoClientes;
			}
		};

		panelActions.addSaveListener(objeto -> txCodigo.setValue(objeto.getIdGrupo()));
		panelActions.addButtonListener(new ButtonActionListenerAdapter() {

			@Override
			public void newEvent() {
				preencherCamposObrigatorios(new CampoClientesHabilitadosVO());
			}

		});
		painelContent.add(panelActions, "cell 0 5,growx,aligny bottom");
	}

	private void preencherCamposObrigatorios(CampoClientesHabilitadosVO campoClientes) {

		chckbxRg.setSelected(campoClientes.isRg());
		chckbxDataNascimento.setSelected(campoClientes.isDataNascimento());
		chckbxNomePai.setSelected(campoClientes.isNomePai());
		chckbxNomeMe.setSelected(campoClientes.isNomeMae());
		chckbxCidade.setSelected(campoClientes.isCidade());
		chckbxEndereo.setSelected(campoClientes.isEndereco());
		chckbxNmero.setSelected(campoClientes.isNumero());
		chckbxNaturalidade.setSelected(campoClientes.isNaturalidade());
		chckbxBairro.setSelected(campoClientes.isBairro());
		chckbxCep.setSelected(campoClientes.isCep());
		chckbxCelular.setSelected(campoClientes.isCelular());
		chckbxEmail.setSelected(campoClientes.isEmail());
		chckbxEstadoCivil.setSelected(campoClientes.isEstadoCivil());
		chckbxEscolaridade.setSelected(campoClientes.isEscolaridade());
		chckbxSexo.setSelected(campoClientes.isSexo());
		chckbxRaacor.setSelected(campoClientes.isRaca());
		chckbxReligio.setSelected(campoClientes.isReligiao());

	}

}
