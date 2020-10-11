package br.com.lar.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.Component;
import java.awt.Container;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import br.com.lar.repository.model.PermissaoPrograma;
import br.com.lar.startup.enumeradores.ProgramasEnum;
import br.com.lar.ui.FrmApplication;
import br.com.sysdesc.components.JTextFieldId;
import br.com.sysdesc.components.JTextFieldMaiusculo;
import br.com.sysdesc.pesquisa.components.buttonactions.AbstractButtonAction;
import br.com.sysdesc.pesquisa.repository.model.Usuario;
import br.com.sysdesc.pesquisa.ui.components.PanelActions;

public class FrmUtil {

	public static JInternalFrame openInstance(FrmApplication frmApplication, List<PermissaoPrograma> permissaoProgramas,
			ProgramasEnum programa) throws Exception {

		Optional<PermissaoPrograma> optional = permissaoProgramas.stream()
				.filter(x -> x.getCodigoPrograma().equals(programa.getCodigo())).findFirst();

		assertTrue(optional.isPresent());

		Class<?>[] parameters = { Class.class, PermissaoPrograma.class };

		Method metodo = frmApplication.getClass().getDeclaredMethod("getSingleInstance", parameters);

		metodo.setAccessible(true);

		Object[] objeto = { programa.getInternalFrame(), optional.get() };

		metodo.invoke(frmApplication, objeto);

		return frmApplication.getDesktopPane().getAllFrames()[0];
	}

	public static void setarUsuario(FrmApplication frmApplication, Usuario usuario) throws Exception {

		Field field = frmApplication.getClass().getDeclaredField("usuario");

		field.setAccessible(true);

		field.set(frmApplication, usuario);

	}

	public static void executeVoidMethod(Object object, String method, Class<?>[] parameters, Object[] valores)
			throws Exception {

		Method metodo = object.getClass().getDeclaredMethod(method, parameters);

		metodo.setAccessible(true);

		metodo.invoke(object, valores);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getFied(Object frm, Class<?> clazz, String fieldStr) throws Exception {

		Field field = clazz.getDeclaredField(fieldStr);

		field.setAccessible(true);

		return (T) field.get(frm);
	}

	public static void novoRegistro(Object object, Class<?> clazz) throws Exception {

		PanelActions<?> panelActions = FrmUtil.getFied(object, clazz, "panelActions");

		((JButton) FrmUtil.getFied(panelActions, PanelActions.class, "btNovo")).doClick();
	}

	public static void salvarRegistro(Object object, Class<?> clazz) throws Exception {
		PanelActions<?> panelActions = FrmUtil.getFied(object, clazz, "panelActions");

		((JButton) FrmUtil.getFied(panelActions, PanelActions.class, "btSalvar")).doClick();
	}

	public static void assertLabels(Container container, Class<?> clazz, String... labels) throws Exception {

		List<String> labelsComponent = new ArrayList<>();

		findLabels(labelsComponent, container);

		List<String> labelsAdicionados = labelsComponent.stream().filter(x -> !Arrays.asList(labels).contains(x))
				.collect(Collectors.toList());

		List<String> labelsNaoEncontrados = Arrays.asList(labels).stream().filter(x -> !labelsComponent.contains(x))
				.collect(Collectors.toList());

		if (labelsAdicionados.size() > 0 || labelsNaoEncontrados.size() > 0) {

			fail(String.format("Formulário: %s \n Labels não encontrados %s \n Labels Adicionados: %s",
					container.getClass().getName(), labelsNaoEncontrados.toString(), labelsAdicionados.toString()));
		}

	}

	private static void findLabels(List<String> labelsComponent, Container object) {

		for (Component component : object.getComponents()) {

			if (!(component instanceof JLabel) && component instanceof Container) {
				findLabels(labelsComponent, (Container) component);
			}

			if (component instanceof JLabel) {
				labelsComponent.add(((JLabel) component).getText());
			}
		}
	}

	public static void assertTextFieldId(Object object, Class<?> clazz, int quantidade) throws Exception {
		PanelActions<?> panelActions = FrmUtil.getFied(object, clazz, "panelActions");

		Map<Class<? extends Component>, List<Component>> map = getFied(panelActions, AbstractButtonAction.class,
				"camposTela");

		assertEquals(quantidade, map.get(JTextFieldId.class).size());
	}

	public static void assertTextFieldMaiusculo(Object object, Class<?> clazz, int quantidade) throws Exception {
		PanelActions<?> panelActions = FrmUtil.getFied(object, clazz, "panelActions");

		Map<Class<? extends Component>, List<Component>> map = getFied(panelActions, AbstractButtonAction.class,
				"camposTela");

		Long count = map.get(JTextField.class).stream().filter(x -> x instanceof JTextFieldMaiusculo).count();

		assertEquals(quantidade, count.intValue());
	}

}
