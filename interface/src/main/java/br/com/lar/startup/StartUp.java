package br.com.lar.startup;

import br.com.lar.ui.FrmApplication;
import br.com.sysdesc.util.classes.LookAndFeelUtil;
import br.com.sysdesc.util.dao.Conexao;

public class StartUp {

	public static void main(String[] args) throws Exception {

		LookAndFeelUtil.configureLayout();

		Conexao.buildEntityManager();

		FrmApplication.getInstance();
	}
}
