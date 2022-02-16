package br.com.lar.ui.buttonactions;

import java.awt.event.KeyEvent;

import br.com.sysdesc.pesquisa.components.buttonactions.ButtonAction;

public class ButtonActionConfigurarServidor extends ButtonAction {

	private static final long serialVersionUID = 1L;

	public ButtonActionConfigurarServidor() {

		super("mapConfigurarServidor", KeyEvent.VK_K, KeyEvent.VK_CONTROL, "password.png", "Configurar Servidor");
	}

	@Override
	public void saveEvent() {
		setEnabled(Boolean.TRUE);
	}

	@Override
	public void editEvent() {
		setEnabled(Boolean.TRUE);
	}

	@Override
	public void newEvent() {
		setEnabled(Boolean.TRUE);
	}

	@Override
	public void searchEvent() {
		setEnabled(Boolean.TRUE);
	}

	@Override
	public void startEvent() {
		setEnabled(Boolean.TRUE);
	}

}
