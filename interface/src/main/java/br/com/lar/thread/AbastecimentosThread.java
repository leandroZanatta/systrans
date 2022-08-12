package br.com.lar.thread;

import static br.com.sysdesc.util.resources.Resources.APPLICATION_ABASTECIMENTOS;
import static br.com.sysdesc.util.resources.Resources.translate;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JLabel;
import javax.swing.JPanel;

import br.com.lar.service.sincronizacao.SincronizacaoService;
import br.com.sysdesc.util.classes.StringUtil;
import br.com.systrans.util.sincronizacao.ServerEndpoints;

public class AbastecimentosThread extends Thread {

	private static final String URLABASTECIMENTOS = translate(APPLICATION_ABASTECIMENTOS);
	private SincronizacaoService sincronizacaoService = new SincronizacaoService();
	private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

	private JLabel lbServidor;

	public AbastecimentosThread(JPanel contentServidor) {
		lbServidor = new JLabel("");

		contentServidor.add(lbServidor);
	}

	@Override
	public void run() {

		scheduledExecutorService.scheduleWithFixedDelay(this::atualizarAbastecimentos, 0, 1, TimeUnit.MINUTES);
	}

	private void atualizarAbastecimentos() {

		if (StringUtil.isNullOrEmpty(URLABASTECIMENTOS)) {

			lbServidor.setText("Servidor Não configurado");

			return;
		}

		if (ServerEndpoints.getinstance() == null) {

			ServerEndpoints.createInstance(URLABASTECIMENTOS);
		}

		lbServidor.setText(sincronizacaoService.servidorAtivo() ? "Ativo" : "Inativo");

		sincronizacaoService.iniciarSincronizacao();
	}
}
