package br.com.lar.ui;

import static br.com.lar.ui.util.CampoObrigatorioUtil.formatarCampoObrigatorio;
import static br.com.sysdesc.util.resources.Resources.FRMCLIENTE_LBL_CPF_CNPJ;
import static br.com.sysdesc.util.resources.Resources.FRMCLIENTE_LBL_DATA_DE_NASCIMENTO;
import static br.com.sysdesc.util.resources.Resources.FRMCLIENTE_LBL_INSCRICAO_ESTADUAL;
import static br.com.sysdesc.util.resources.Resources.FRMCLIENTE_LBL_RAZAO_SOCIAL;
import static br.com.sysdesc.util.resources.Resources.FRMCLIENTE_TITLE;
import static br.com.sysdesc.util.resources.Resources.translate;

import java.awt.FlowLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.ParseException;
import java.util.Arrays;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.text.MaskFormatter;

import com.google.gson.Gson;
import com.toedter.calendar.JDateChooser;

import br.com.lar.repository.model.Cidade;
import br.com.lar.repository.model.Cliente;
import br.com.lar.repository.model.Estado;
import br.com.lar.repository.model.Grupo;
import br.com.lar.service.cidade.CidadeService;
import br.com.lar.service.cliente.ClienteService;
import br.com.lar.service.estado.EstadoService;
import br.com.lar.service.grupo.GrupoService;
import br.com.lar.startup.enumeradores.PesquisaEnum;
import br.com.sysdesc.components.AbstractInternalFrame;
import br.com.sysdesc.components.JTextFieldId;
import br.com.sysdesc.components.JTextFieldMaiusculo;
import br.com.sysdesc.pesquisa.ui.components.CampoPesquisa;
import br.com.sysdesc.pesquisa.ui.components.PanelActions;
import br.com.sysdesc.util.classes.CNPJUtil;
import br.com.sysdesc.util.classes.CPFUtil;
import br.com.sysdesc.util.classes.IfNull;
import br.com.sysdesc.util.enumeradores.EscolaridadeEnum;
import br.com.sysdesc.util.enumeradores.EstadoCivilEnum;
import br.com.sysdesc.util.enumeradores.RacaCorEnum;
import br.com.sysdesc.util.enumeradores.ReligiaoEnum;
import br.com.sysdesc.util.enumeradores.SexoEnum;
import br.com.sysdesc.util.enumeradores.TipoClienteEnum;
import br.com.sysdesc.util.enumeradores.TipoStatusEnum;
import br.com.sysdesc.util.vo.CampoClientesHabilitadosVO;
import net.miginfocom.swing.MigLayout;

public class FrmCliente extends AbstractInternalFrame {

	private static final long serialVersionUID = 1L;

	private MaskFormatter mascaraCPF;
	private MaskFormatter mascaraCNPJ;

	private JLabel lbCpfcnpj;
	private JLabel lbNome;
	private JLabel lbDataNascimento;
	private JLabel lbRg;

	private JTextFieldId txCodigo;

	private JTextField txEmail;

	private JTextFieldMaiusculo txNome;
	private JTextFieldMaiusculo txIncricaoEstadual;
	private JTextFieldMaiusculo txEndereco;
	private JTextFieldMaiusculo txBairro;
	private JTextFieldMaiusculo txNumero;
	private JTextFieldMaiusculo txNomePai;
	private JTextFieldMaiusculo txNomeMae;

	private JDateChooser txDataDeNascimento;
	private JComboBox<Estado> cbEstado;
	private JComboBox<Cidade> cbCidade;
	private JComboBox<EstadoCivilEnum> cbEstadoCivil;
	private JComboBox<TipoStatusEnum> cbSituacao;
	private JComboBox<SexoEnum> cbSexo;
	private JComboBox<RacaCorEnum> cbRacaCor;
	private JComboBox<EscolaridadeEnum> cbEscolaridade;
	private JComboBox<ReligiaoEnum> cbReligiao;

	private JFormattedTextField txCelular;
	private JFormattedTextField txCep;
	private JFormattedTextField txCgc;

	private CampoPesquisa<Grupo> pesquisaGrupo;
	private CampoPesquisa<Cidade> pesquisaNaturalidade;
	private JPanel panelTipoPessoa;
	private ButtonGroup buttonGroup;
	private JRadioButton rbFisca;
	private JRadioButton rbJurdica;

	private PanelActions<Cliente> painelDeBotoes;

	private ClienteService clienteService = new ClienteService();
	private EstadoService estadoService = new EstadoService();
	private CidadeService cidadeService = new CidadeService();
	private GrupoService grupoService = new GrupoService();

	public FrmCliente(Long permissaoPrograma, Long codigoUsuario) throws ParseException {

		super(permissaoPrograma, codigoUsuario);

		initComponents();
	}

	private void initComponents() throws ParseException {

		setTitle(translate(FRMCLIENTE_TITLE));
		setSize(623, 545);
		setClosable(Boolean.TRUE);
		getContentPane().setLayout(new MigLayout("", "[80.00][141.00][][][99.00][76.00][]", "[][][][][][][][][][][][][][][][][][][][][grow]"));

		MaskFormatter mascaraCep = new MaskFormatter("#####-###");
		MaskFormatter mascaraCelular = new MaskFormatter("(##) #####-####");
		mascaraCNPJ = new MaskFormatter("##.###.###/####-##");
		mascaraCPF = new MaskFormatter("###.###.###-##");

		mascaraCelular.setPlaceholderCharacter('_');
		mascaraCNPJ.setPlaceholderCharacter('_');
		mascaraCep.setPlaceholderCharacter('_');
		mascaraCPF.setPlaceholderCharacter('_');

		JLabel lbCodigo = new JLabel("Código:");
		lbNome = new JLabel(translate(FRMCLIENTE_LBL_RAZAO_SOCIAL));
		lbCpfcnpj = new JLabel(translate(FRMCLIENTE_LBL_CPF_CNPJ));
		lbRg = new JLabel(translate(FRMCLIENTE_LBL_INSCRICAO_ESTADUAL));
		lbDataNascimento = new JLabel(translate(FRMCLIENTE_LBL_DATA_DE_NASCIMENTO));
		JLabel lbEstado = new JLabel("Estado:");
		JLabel lbCidade = new JLabel("Cidade:");
		JLabel lbEndereco = new JLabel("Endereço:");
		JLabel lbNumero = new JLabel("Número:");
		JLabel lbCelular = new JLabel("Celular:");
		JLabel lbEmail = new JLabel("Email:");
		JLabel lbSituacao = new JLabel("Situação:");
		JLabel lbGrupo = new JLabel("Grupo:");
		JLabel lbNomePai = new JLabel("Nome do Pai:");
		JLabel lbNomeMae = new JLabel("Nome da Mãe:");
		JLabel lbRacacor = new JLabel("Raça/Cor:");
		JLabel lbReligiao = new JLabel("Religião:");
		JLabel lbNaturalidade = new JLabel("Naturalidade:");
		JLabel lbBairro = new JLabel("Bairro:");
		JLabel lbCep = new JLabel("CEP:");
		JLabel lbEstadoCivil = new JLabel("Estado Civil:");
		JLabel lbEscolaridade = new JLabel("Escolaridade:");
		JLabel lbSexo = new JLabel("Sexo:");

		txCodigo = new JTextFieldId();

		txEmail = new JTextField();

		txNomePai = new JTextFieldMaiusculo();
		txNomeMae = new JTextFieldMaiusculo();
		txBairro = new JTextFieldMaiusculo();
		txNome = new JTextFieldMaiusculo();
		txEndereco = new JTextFieldMaiusculo();
		txIncricaoEstadual = new JTextFieldMaiusculo(30);
		txNumero = new JTextFieldMaiusculo(5);

		txCep = new JFormattedTextField();
		txCelular = new JFormattedTextField();
		txCgc = new JFormattedTextField();

		cbEstadoCivil = new JComboBox<>();
		cbEscolaridade = new JComboBox<>();
		cbSexo = new JComboBox<>();
		cbEstado = new JComboBox<>();
		cbSituacao = new JComboBox<>();
		cbCidade = new JComboBox<>();
		cbRacaCor = new JComboBox<>();
		cbReligiao = new JComboBox<>();

		panelTipoPessoa = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

		txDataDeNascimento = new JDateChooser("dd/MM/yyyy", "##/##/#####", '_');

		buttonGroup = new ButtonGroup();
		rbFisca = new JRadioButton("Pessoa Física");
		rbJurdica = new JRadioButton("Pessoa Jurídica");

		pesquisaGrupo = new CampoPesquisa<Grupo>(grupoService, PesquisaEnum.PES_GRUPO.getCodigoPesquisa(), getCodigoUsuario()) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(Grupo objeto) {

				return String.format("%d - %s", objeto.getIdGrupo(), objeto.getDescricao());

			}
		};

		pesquisaNaturalidade = new CampoPesquisa<Cidade>(cidadeService, PesquisaEnum.PES_CIDADES.getCodigoPesquisa(), getCodigoUsuario()) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(Cidade objeto) {
				return String.format("%d - %s", objeto.getIdCidade(), objeto.getDescricao());
			}
		};

		cbEstado.addActionListener(e -> carregarCidades());
		rbFisca.addActionListener(e -> selecionouPessoaFisica());
		rbJurdica.addActionListener(e -> selecionouPessoaJuridica());
		pesquisaGrupo.addChangeListener((value) -> formatarcamposObrigatorios(value));

		rbFisca.setSelected(Boolean.TRUE);

		mascaraCep.install(txCep);
		mascaraCelular.install(txCelular);

		estadoService.listarEstados().forEach(cbEstado::addItem);
		Arrays.asList(TipoStatusEnum.values()).forEach(cbSituacao::addItem);
		Arrays.asList(SexoEnum.values()).forEach(cbSexo::addItem);
		Arrays.asList(EstadoCivilEnum.values()).forEach(cbEstadoCivil::addItem);
		Arrays.asList(RacaCorEnum.values()).forEach(cbRacaCor::addItem);
		Arrays.asList(EscolaridadeEnum.values()).forEach(cbEscolaridade::addItem);
		Arrays.asList(ReligiaoEnum.values()).forEach(cbReligiao::addItem);

		txCgc.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent e) {

				boolean documentoValido;

				String documento;

				if (rbFisca.isSelected()) {
					documento = "CPF";
					documentoValido = CPFUtil.isCPFValido(txCgc.getText());
				} else {
					documento = "CNPJ";
					documentoValido = CNPJUtil.isCNPJValido(txCgc.getText());
				}

				if (!documentoValido) {
					JOptionPane.showMessageDialog(null, "O " + documento + " informado é inválido.");

					txCgc.setText("");

					return;
				}

				Cliente cliente = clienteService.buscarClientePorCpf(txCgc.getText(), txCodigo.getValue());

				if (cliente != null) {

					Integer confirmacao = JOptionPane.showConfirmDialog(null,
							"O " + documento + " informado já está cadastrado.\n Deseja Carrega-lo?", "Verificação", JOptionPane.YES_NO_OPTION);

					if (confirmacao == JOptionPane.YES_OPTION) {

						painelDeBotoes.carregarObjetoPesquisado(cliente);

						return;
					}

					txCgc.setText("");
				}

			}
		});

		panelTipoPessoa.add(rbJurdica);
		panelTipoPessoa.add(rbFisca);
		buttonGroup.add(rbJurdica);
		buttonGroup.add(rbFisca);

		getContentPane().add(lbCodigo, "cell 0 0");
		getContentPane().add(lbGrupo, "cell 1 0 3 1");
		getContentPane().add(txCodigo, "cell 0 1,growx");
		getContentPane().add(pesquisaGrupo, "cell 1 1 4 1,growx");
		getContentPane().add(panelTipoPessoa, "cell 5 1 2 1,grow");
		getContentPane().add(txIncricaoEstadual, "flowy,cell 2 3 4 1,growx");
		getContentPane().add(txDataDeNascimento, "cell 6 3,growx");
		getContentPane().add(lbNome, "cell 0 4 7 1");
		getContentPane().add(txNome, "cell 0 5 7 1,growx");
		getContentPane().add(lbCpfcnpj, "cell 0 2 2 1");
		getContentPane().add(lbRg, "cell 2 2 4 1");
		getContentPane().add(lbDataNascimento, "cell 6 2");
		getContentPane().add(txCgc, "cell 0 3 2 1,growx");
		getContentPane().add(lbNomePai, "cell 0 6 3 1");
		getContentPane().add(lbNomeMae, "cell 3 6 4 1");
		getContentPane().add(txNomePai, "cell 0 7 3 1,growx");
		getContentPane().add(txNomeMae, "cell 3 7 4 1,growx");
		getContentPane().add(lbEstado, "cell 0 8 3 1");
		getContentPane().add(cbCidade, "cell 3 9 4 1,growx");
		getContentPane().add(lbCidade, "cell 3 8 4 1");
		getContentPane().add(cbEstado, "cell 0 9 3 1,growx");
		getContentPane().add(lbEndereco, "cell 0 10 6 1");
		getContentPane().add(lbNumero, "cell 6 10");
		getContentPane().add(txEndereco, "cell 0 11 6 1,growx");
		getContentPane().add(txNumero, "cell 6 11,growx");
		getContentPane().add(lbNaturalidade, "cell 0 12");
		getContentPane().add(lbBairro, "cell 4 12 2 1");
		getContentPane().add(lbCep, "cell 6 12");
		getContentPane().add(pesquisaNaturalidade, "cell 0 13 4 1,growx");
		getContentPane().add(txBairro, "cell 4 13 2 1,growx");
		getContentPane().add(txCep, "cell 6 13,growx");
		getContentPane().add(lbCelular, "cell 0 14 2 1");
		getContentPane().add(lbEmail, "cell 2 14 5 1");
		getContentPane().add(txCelular, "cell 0 15 2 1,growx");
		getContentPane().add(txEmail, "cell 2 15 5 1,growx");
		getContentPane().add(lbEstadoCivil, "cell 0 16 2 1");
		getContentPane().add(lbEscolaridade, "cell 2 16 4 1");
		getContentPane().add(lbSexo, "cell 6 16");
		getContentPane().add(cbEstadoCivil, "cell 0 17 2 1,growx");
		getContentPane().add(cbEscolaridade, "cell 2 17 4 1,growx");
		getContentPane().add(cbSexo, "cell 6 17,growx");
		getContentPane().add(lbRacacor, "cell 0 18");
		getContentPane().add(lbReligiao, "cell 2 18");
		getContentPane().add(lbSituacao, "cell 6 18");
		getContentPane().add(cbRacaCor, "cell 0 19 2 1,growx");
		getContentPane().add(cbReligiao, "cell 2 19 4 1,growx");
		getContentPane().add(cbSituacao, "cell 6 19,growx");

		painelDeBotoes = new PanelActions<Cliente>(this, clienteService, PesquisaEnum.PES_CLIENTES.getCodigoPesquisa()) {

			private static final long serialVersionUID = 1L;

			@Override
			public void carregarObjeto(Cliente objeto) {
				txCodigo.setValue(objeto.getIdCliente());
				pesquisaGrupo.setValue(objeto.getGrupo());

				if (objeto.getFlagTipoCliente().equals(TipoClienteEnum.PESSOA_FISICA.getCodigo())) {
					rbFisca.setSelected(Boolean.TRUE);
					selecionouPessoaFisica();
				} else {
					rbJurdica.setSelected(Boolean.TRUE);
					selecionouPessoaJuridica();
				}

				txCgc.setText(objeto.getCgc());
				txNome.setText(objeto.getNome());
				txIncricaoEstadual.setText(objeto.getRgie());
				txDataDeNascimento.setDate(objeto.getDatadenascimento());
				cbEstado.setSelectedItem(objeto.getCidade().getEstado());
				cbCidade.setSelectedItem(objeto.getCidade());
				pesquisaNaturalidade.setValue(objeto.getNaturalidade());
				txEndereco.setText(objeto.getEndereco());
				txNumero.setText(objeto.getNumero());
				txBairro.setText(objeto.getBairro());
				txCep.setText(objeto.getCep());
				txCelular.setText(objeto.getTelefone());
				txEmail.setText(objeto.getEmail());
				txNomePai.setText(objeto.getNomePai());
				txNomeMae.setText(objeto.getNomeMae());
				cbEstadoCivil.setSelectedItem(EstadoCivilEnum.findByCodigo(objeto.getEstadocivil()));
				cbSexo.setSelectedItem(SexoEnum.findByCodigo(objeto.getSexo()));
				cbSituacao.setSelectedItem(TipoStatusEnum.findByCodigo(objeto.getSituacao()));
				cbReligiao.setSelectedItem(ReligiaoEnum.findByCodigo(objeto.getReligiao()));
				cbRacaCor.setSelectedItem(RacaCorEnum.findByCodigo(objeto.getNumeroCor()));
				cbEscolaridade.setSelectedItem(EscolaridadeEnum.findByCodigo(objeto.getEscolaridade()));

			}

			@Override
			public boolean preencherObjeto(Cliente objetoPesquisa) {

				objetoPesquisa.setGrupo(pesquisaGrupo.getObjetoPesquisado());
				objetoPesquisa.setCgc(txCgc.getText());
				objetoPesquisa.setNome(txNome.getText());
				objetoPesquisa.setDatadenascimento(txDataDeNascimento.getDate());
				objetoPesquisa.setNomePai(IfNull.getStringEmpty(txNomePai.getText()));
				objetoPesquisa.setNomeMae(IfNull.getStringEmpty(txNomeMae.getText()));
				objetoPesquisa.setRgie(IfNull.getStringEmpty(txIncricaoEstadual.getText()));
				objetoPesquisa.setEndereco(IfNull.getStringEmpty(txEndereco.getText()));
				objetoPesquisa.setNumero(IfNull.getStringEmpty(txNumero.getText()));
				objetoPesquisa.setBairro(IfNull.getStringEmpty(txBairro.getText()));
				objetoPesquisa.setEmail(IfNull.getStringEmpty(txEmail.getText()));
				objetoPesquisa.setCep(IfNull.getStringChar(txCep.getText(), "_"));
				objetoPesquisa.setTelefone(IfNull.getStringChar(txCelular.getText(), "_"));
				objetoPesquisa.setNaturalidade(pesquisaNaturalidade.getObjetoPesquisado());
				objetoPesquisa.setCidade(null);
				objetoPesquisa.setEstadocivil(null);
				objetoPesquisa.setSexo(null);
				objetoPesquisa.setSituacao(null);
				objetoPesquisa.setEscolaridade(null);
				objetoPesquisa.setNumeroCor(null);
				objetoPesquisa.setReligiao(null);

				if (rbFisca.isSelected()) {
					objetoPesquisa.setFlagTipoCliente(TipoClienteEnum.PESSOA_FISICA.getCodigo());
				} else if (rbJurdica.isSelected()) {
					objetoPesquisa.setFlagTipoCliente(TipoClienteEnum.PESSOA_JURIDICA.getCodigo());
				}

				if (cbCidade.getSelectedIndex() >= 0) {
					objetoPesquisa.setCidade((Cidade) cbCidade.getSelectedItem());
				}

				if (cbEstadoCivil.getSelectedIndex() >= 0) {
					objetoPesquisa.setEstadocivil(((EstadoCivilEnum) cbEstadoCivil.getSelectedItem()).getCodigo());
				}

				if (cbSexo.getSelectedIndex() >= 0) {
					objetoPesquisa.setSexo(((SexoEnum) cbSexo.getSelectedItem()).getCodigo());
				}

				if (cbSituacao.getSelectedIndex() >= 0) {
					objetoPesquisa.setSituacao(((TipoStatusEnum) cbSituacao.getSelectedItem()).getCodigo());
				}

				if (cbReligiao.getSelectedIndex() >= 0) {
					objetoPesquisa.setReligiao(((ReligiaoEnum) cbReligiao.getSelectedItem()).getCodigo());
				}

				if (cbRacaCor.getSelectedIndex() >= 0) {
					objetoPesquisa.setNumeroCor(((RacaCorEnum) cbRacaCor.getSelectedItem()).getCodigo());
				}

				if (cbEscolaridade.getSelectedIndex() >= 0) {
					objetoPesquisa.setEscolaridade(((EscolaridadeEnum) cbEscolaridade.getSelectedItem()).getCodigo());
				}

				return true;
			}
		};

		painelDeBotoes.addSaveListener(cliente -> txCodigo.setValue(cliente.getIdCliente()));

		getContentPane().add(painelDeBotoes, "cell 0 20 7 1,growx,aligny bottom");

		selecionouPessoaFisica();
	}

	protected void formatarcamposObrigatorios(Grupo grupo) {

		CampoClientesHabilitadosVO campoCliente = getCamposHabilitados(grupo);

		formatarCampoObrigatorio(pesquisaGrupo, campoCliente.isGrupo());
		formatarCampoObrigatorio(txCgc, campoCliente.isCgc());
		formatarCampoObrigatorio(txIncricaoEstadual, campoCliente.isRg());
		formatarCampoObrigatorio(txDataDeNascimento, campoCliente.isDataNascimento());
		formatarCampoObrigatorio(txNome, campoCliente.isNome());
		formatarCampoObrigatorio(txNomePai, campoCliente.isNomePai());
		formatarCampoObrigatorio(txNomeMae, campoCliente.isNomeMae());
		formatarCampoObrigatorio(cbEstado, campoCliente.isCidade());
		formatarCampoObrigatorio(cbCidade, campoCliente.isCidade());
		formatarCampoObrigatorio(txEndereco, campoCliente.isEndereco());
		formatarCampoObrigatorio(txNumero, campoCliente.isNumero());
		formatarCampoObrigatorio(pesquisaNaturalidade, campoCliente.isNaturalidade());
		formatarCampoObrigatorio(txBairro, campoCliente.isBairro());
		formatarCampoObrigatorio(txCep, campoCliente.isCep());
		formatarCampoObrigatorio(txCelular, campoCliente.isCelular());
		formatarCampoObrigatorio(txEmail, campoCliente.isEmail());
		formatarCampoObrigatorio(cbEstadoCivil, campoCliente.isEstadoCivil());
		formatarCampoObrigatorio(cbEscolaridade, campoCliente.isEscolaridade());
		formatarCampoObrigatorio(cbSexo, campoCliente.isSexo());
		formatarCampoObrigatorio(cbRacaCor, campoCliente.isRaca());
		formatarCampoObrigatorio(cbReligiao, campoCliente.isReligiao());
		formatarCampoObrigatorio(cbSituacao, campoCliente.isSituacao());

	}

	private CampoClientesHabilitadosVO getCamposHabilitados(Grupo grupo) {

		if (grupo != null) {
			return new Gson().fromJson(grupo.getConfiguracao(), CampoClientesHabilitadosVO.class);
		}

		return new CampoClientesHabilitadosVO();
	}

	private void selecionouPessoaJuridica() {

		lbCpfcnpj.setText("CNPJ:");
		lbNome.setText("Razão Social:");
		lbDataNascimento.setText("Data de Fundação:");
		lbRg.setText("Inscrição Estadual:");
		cbEstadoCivil.setEnabled(false);
		cbEscolaridade.setEnabled(false);
		cbRacaCor.setEnabled(false);
		cbReligiao.setEnabled(false);

		cbSexo.setEnabled(false);
		mascaraCPF.uninstall();
		mascaraCNPJ.install(txCgc);

	}

	private void selecionouPessoaFisica() {
		lbCpfcnpj.setText("CPF:");
		lbNome.setText("Nome:");
		lbDataNascimento.setText("Data de Nascimento:");
		lbRg.setText("RG:");
		cbEstadoCivil.setEnabled(painelDeBotoes.isEditable());
		cbSexo.setEnabled(painelDeBotoes.isEditable());
		cbEscolaridade.setEnabled(painelDeBotoes.isEditable());
		cbRacaCor.setEnabled(painelDeBotoes.isEditable());
		cbReligiao.setEnabled(painelDeBotoes.isEditable());

		mascaraCNPJ.uninstall();
		mascaraCPF.install(txCgc);

	}

	private void carregarCidades() {

		cbCidade.removeAllItems();

		if (cbEstado.getSelectedIndex() >= 0) {
			cidadeService.buscarCidadesPorEstado(((Estado) cbEstado.getSelectedItem()).getIdEstado()).forEach(cbCidade::addItem);
		}

	}

}
