package br.com.lar.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.toedter.calendar.JDateChooser;

import br.com.lar.repository.model.Funcionario;
import br.com.lar.repository.model.Salario;
import br.com.lar.service.salario.SalarioService;
import br.com.lar.tablemodels.SalarioTableModel;
import br.com.sysdesc.components.JMoneyField;
import br.com.sysdesc.util.classes.BigDecimalUtil;
import net.miginfocom.swing.MigLayout;

public class FrmAumentoSalarial extends JDialog {

	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();
	private JDateChooser dtAlteracao;
	private JMoneyField txSalario;
	private JTable tbSalarios;
	private JPanel panelAcoes;
	private JButton btSalvar;
	private JButton btCancelar;
	private JScrollPane scrollPane;
	private JLabel lbAlteracao;
	private JLabel lbSalario;
	private SalarioTableModel salarioTableModel;
	private Funcionario funcionario;
	private SalarioService salarioService = new SalarioService();

	public FrmAumentoSalarial(Funcionario funcionario) {

		this.funcionario = funcionario;

		setTitle("ALTERAÇÃO SALARIAL");
		setSize(450, 300);
		setModal(true);
		setLocationRelativeTo(null);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		contentPanel.setLayout(new MigLayout("", "[grow][grow]", "[][][][grow][]"));
		panelAcoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		lbAlteracao = new JLabel("Data Alteração:");
		lbSalario = new JLabel("Salário:");

		dtAlteracao = new JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
		txSalario = new JMoneyField();

		btSalvar = new JButton("Salvar");
		btCancelar = new JButton("Cancelar");

		salarioTableModel = new SalarioTableModel(this.getSalarios());
		tbSalarios = new JTable(salarioTableModel);
		scrollPane = new JScrollPane(tbSalarios);

		btCancelar.addActionListener(e -> dispose());
		btSalvar.addActionListener(e -> salvarAumentoSalario());
		dtAlteracao.setDate(new Date());

		contentPanel.add(scrollPane, "cell 0 0 2 1,grow");
		contentPanel.add(lbAlteracao, "cell 0 1");
		contentPanel.add(lbSalario, "cell 1 1");
		contentPanel.add(dtAlteracao, "cell 0 2,growx");
		contentPanel.add(txSalario, "cell 1 2,growx");
		contentPanel.add(panelAcoes, "cell 0 3 2 1,grow");
		panelAcoes.add(btSalvar);
		panelAcoes.add(btCancelar);
	}

	private void salvarAumentoSalario() {

		if (dtAlteracao.getDate() == null) {
			JOptionPane.showMessageDialog(this, "Selecione uma data de alteração de salário");

			return;
		}

		if (BigDecimalUtil.isNullOrZero(txSalario.getValue())) {

			JOptionPane.showMessageDialog(this, "Insira um valor para o aumento salarial");

			return;
		}

		Double maiorSalario = this.salarioTableModel.getRows().stream()
				.mapToDouble(salario -> salario.getValorSalario().doubleValue()).max().orElse(0.0);

		if (BigDecimalUtil.maior(BigDecimal.valueOf(maiorSalario), txSalario.getValue())) {

			JOptionPane.showMessageDialog(this, "Não é possivel reduzir o valor do salário");

			return;
		}

		Salario salario = new Salario();
		salario.setFuncionario(this.funcionario);
		salario.setDataAlteracao(dtAlteracao.getDate());
		salario.setValorSalario(txSalario.getValue());

		this.salarioService.salvar(salario);

		dispose();
	}

	private List<Salario> getSalarios() {

		return salarioService.getSalarios(this.funcionario.getIdFuncionario());
	}

}
