package br.com.lar.ui.util;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JFormattedTextField;

import com.toedter.calendar.JDateChooser;

import br.com.sysdesc.pesquisa.ui.components.CampoPesquisa;
import br.com.sysdesc.pesquisa.ui.components.CampoPesquisaMultiSelect;

public class CampoObrigatorioUtil {

	private CampoObrigatorioUtil() {
	}

	public static void formatarCampoObrigatorio(Component component, boolean obrigatorio) {

		Color corCampo = getColor(obrigatorio);

		if (component instanceof CampoPesquisa<?>) {

			((CampoPesquisa<?>) component).setBackgroundColor(corCampo);
		} else if (component instanceof CampoPesquisaMultiSelect<?>) {

			((CampoPesquisaMultiSelect<?>) component).setBackgroundColor(corCampo);
		} else if (component instanceof JDateChooser) {

			((JFormattedTextField) ((JDateChooser) component).getDateEditor()).setBackground(corCampo);
		} else {

			component.setBackground(corCampo);
		}

	}

	private static Color getColor(boolean obrigatorio) {

		return obrigatorio ? new Color(255, 230, 230) : new Color(223, 255, 223);
	}
}
