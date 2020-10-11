package br.com.lar.ui;

import static br.com.sysdesc.util.resources.Resources.FRMESTADO_LB_CODIGO;
import static br.com.sysdesc.util.resources.Resources.FRMESTADO_LB_DESCRICAO;
import static br.com.sysdesc.util.resources.Resources.FRMESTADO_LB_UF;
import static br.com.sysdesc.util.resources.Resources.FRMESTADO_TITLE;
import static br.com.sysdesc.util.resources.Resources.translate;

import javax.swing.JLabel;
import javax.swing.JPanel;

import br.com.lar.repository.model.Estado;
import br.com.lar.service.estado.EstadoService;
import br.com.lar.startup.enumeradores.PesquisaEnum;
import br.com.sysdesc.components.AbstractInternalFrame;
import br.com.sysdesc.components.JTextFieldId;
import br.com.sysdesc.components.JTextFieldMaiusculo;
import br.com.sysdesc.pesquisa.ui.components.PanelActions;
import net.miginfocom.swing.MigLayout;

public class FrmEstado extends AbstractInternalFrame {

	private static final long serialVersionUID = 1L;

	private JPanel painelContent;
	private JTextFieldId txCodigo;
	private JLabel lblCodigo;
	private JLabel lblDescricao;
	private JLabel lblUF;
	private JTextFieldMaiusculo txDescricao;
	private JTextFieldMaiusculo txUF;
	private PanelActions<Estado> panelActions;
	private EstadoService estadoService = new EstadoService();

	public FrmEstado(Long permissaoPrograma, Long codigoUsuario) {
		super(permissaoPrograma, codigoUsuario);

		initComponents();
	}

	private void initComponents() {

		setSize(450, 230);
		setClosable(Boolean.TRUE);
		setTitle(translate(FRMESTADO_TITLE));

		painelContent = new JPanel();
		txCodigo = new JTextFieldId();
		lblCodigo = new JLabel(translate(FRMESTADO_LB_CODIGO));
		lblDescricao = new JLabel(translate(FRMESTADO_LB_DESCRICAO));
		txDescricao = new JTextFieldMaiusculo();
		lblUF = new JLabel(translate(FRMESTADO_LB_UF));
		txUF = new JTextFieldMaiusculo();

		painelContent.setLayout(new MigLayout("", "[grow]", "[][][][][][][grow]"));
		getContentPane().add(painelContent);
		painelContent.add(lblCodigo, "cell 0 0");
		painelContent.add(txCodigo, "cell 0 1,width 50:100:100");

		painelContent.add(lblDescricao, "cell 0 2");
		painelContent.add(txDescricao, "cell 0 3,growx");
		painelContent.add(lblUF, "cell 0 4,growx");
		painelContent.add(txUF, "cell 0 5,width 50:100:100");

		panelActions = new PanelActions<Estado>(this, estadoService, PesquisaEnum.PES_ESTADOS.getCodigoPesquisa()) {

			private static final long serialVersionUID = 1L;

			@Override
			public void carregarObjeto(Estado objeto) {
				txCodigo.setValue(objeto.getIdEstado());
				txDescricao.setText(objeto.getDescricao());
				txUF.setText(objeto.getUf());
			}

			@Override
			public boolean preencherObjeto(Estado objetoPesquisa) {

				objetoPesquisa.setIdEstado(txCodigo.getValue());
				objetoPesquisa.setDescricao(txDescricao.getText());
				objetoPesquisa.setUf(txUF.getText());

				return true;
			}

		};

		panelActions.addSaveListener((objeto) -> txCodigo.setValue(objeto.getIdEstado()));

		painelContent.add(panelActions, "cell 0 6,growx,aligny bottom");
	}

}
