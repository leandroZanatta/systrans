package br.com.lar.ui;

import javax.swing.JLabel;
import javax.swing.JPanel;

import br.com.lar.repository.model.CentroCusto;
import br.com.lar.service.centrocusto.CentroCustoService;
import br.com.lar.startup.enumeradores.PesquisaEnum;
import br.com.sysdesc.components.AbstractInternalFrame;
import br.com.sysdesc.components.JTextFieldId;
import br.com.sysdesc.components.JTextFieldMaiusculo;
import br.com.sysdesc.pesquisa.ui.components.PanelActions;
import net.miginfocom.swing.MigLayout;

public class FrmCentroCusto extends AbstractInternalFrame {

	private static final long serialVersionUID = 1L;

	private JPanel painelContent;

	private JLabel lblCodigo;
	private JLabel lblDescricao;

	private JTextFieldId txCodigo;
	private JTextFieldMaiusculo txDescricao;

	private PanelActions<CentroCusto> panelActions;
	private CentroCustoService centroCustoService = new CentroCustoService();

	public FrmCentroCusto(Long permissaoPrograma, Long codigoUsuario) {
		super(permissaoPrograma, codigoUsuario);

		initComponents();
	}

	private void initComponents() {

		setSize(450, 179);
		setClosable(Boolean.TRUE);
		setTitle("CADASTRO DE CENTROS DE CUSTO");

		painelContent = new JPanel();

		lblCodigo = new JLabel("Código:");
		lblDescricao = new JLabel("Descrição:");

		txCodigo = new JTextFieldId();
		txDescricao = new JTextFieldMaiusculo();

		painelContent.setLayout(new MigLayout("", "[grow]", "[][][][][grow]"));
		getContentPane().add(painelContent);

		painelContent.add(lblCodigo, "cell 0 0");
		painelContent.add(txCodigo, "cell 0 1,width 50:100:100");
		painelContent.add(lblDescricao, "cell 0 2");
		painelContent.add(txDescricao, "cell 0 3,growx");

		panelActions = new PanelActions<CentroCusto>(this, centroCustoService, PesquisaEnum.PES_CENTRO_CUSTO.getCodigoPesquisa()) {

			private static final long serialVersionUID = 1L;

			@Override
			public void carregarObjeto(CentroCusto objeto) {
				txCodigo.setValue(objeto.getIdCentroCusto());
				txDescricao.setText(objeto.getDescricao());
			}

			@Override
			public boolean preencherObjeto(CentroCusto objetoPesquisa) {

				objetoPesquisa.setIdCentroCusto(txCodigo.getValue());
				objetoPesquisa.setDescricao(txDescricao.getText());

				return true;
			}
		};

		panelActions.addSaveListener((objeto) -> txCodigo.setValue(objeto.getIdCentroCusto()));

		painelContent.add(panelActions, "cell 0 4,growx,aligny bottom");
	}

}
