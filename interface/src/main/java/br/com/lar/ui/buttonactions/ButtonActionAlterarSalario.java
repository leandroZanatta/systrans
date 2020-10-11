package br.com.lar.ui.buttonactions;

import java.awt.event.KeyEvent;

import br.com.sysdesc.pesquisa.components.buttonactions.ButtonAction;

public class ButtonActionAlterarSalario extends ButtonAction {

	private static final long serialVersionUID = 1L;

	public ButtonActionAlterarSalario() {

		super("mapAlterarSalario", KeyEvent.VK_L, KeyEvent.VK_CONTROL, "money.png", "Aumento Salarial");
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
