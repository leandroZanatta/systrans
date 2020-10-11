package br.com.lar.ui.buttonactions;

import java.awt.event.KeyEvent;

import br.com.sysdesc.pesquisa.components.buttonactions.ButtonAction;

public class ButtonActionAlterarPermissoes extends ButtonAction {

	private static final long serialVersionUID = 1L;

	public ButtonActionAlterarPermissoes() {

		super("mapAlterarPermissoes", KeyEvent.VK_A, KeyEvent.VK_CONTROL, "password.png", "Alterar Permissoes");
	}

	@Override
	public void saveEvent() {
		setEnabled(Boolean.TRUE);
	}

	@Override
	public void editEvent() {
		setEnabled(Boolean.FALSE);
	}

	@Override
	public void newEvent() {
		setEnabled(Boolean.FALSE);
	}

	@Override
	public void searchEvent() {
		setEnabled(Boolean.TRUE);
	}

	@Override
	public void startEvent() {
		setEnabled(Boolean.FALSE);
	}

}
