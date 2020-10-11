package br.com.lar.util.integracao.cases;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.Window;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import br.com.lar.ui.FrmLogin;
import br.com.lar.util.FrmUtil;
import br.com.sysdesc.pesquisa.repository.model.Usuario;

public class LoginCases {

	private static Usuario usuario;

	public static Usuario autenticacao(FrmLogin frmLogin) throws Exception {

		Future<?> future = Executors.newSingleThreadExecutor().submit(new Runnable() {

			@Override
			public void run() {

				try {
					LoginCases.logarUsuariofalha(frmLogin, "admin", "12345");

					usuario = LoginCases.logarUsuarioSucesso(frmLogin, "admin", "123456");
				} catch (Exception e) {
					fail(e.getMessage());
				}
			}
		});

		frmLogin.setVisible(true);

		future.get();

		assertNotNull(usuario);

		return usuario;
	}

	public static Usuario logarUsuarioSucesso(FrmLogin frmLogin, String usuario, String senha) throws Exception {

		Future<?> future = Executors.newSingleThreadExecutor().submit(setarCamposSucesso(frmLogin, usuario, senha));

		future.get();

		return frmLogin.getUsuario();
	}

	public static void logarUsuariofalha(FrmLogin frmLogin, String usuario, String senha) throws Exception {

		Future<?> future = Executors.newSingleThreadExecutor().submit(setarCamposfalha(frmLogin, usuario, senha));

		frmLogin.setVisible(Boolean.TRUE);

		future.get();
	}

	private static Runnable setarCamposSucesso(FrmLogin frmLogin, String usuario, String senha) {

		return new Runnable() {

			@Override
			public void run() {

				try {

					Thread.sleep(1000);

					getTxUsuario(frmLogin).setText(usuario);
					getTxSenha(frmLogin).setText(senha);

					Thread.sleep(1000);

					getBtLogar(frmLogin).doClick();

				} catch (Exception e) {

					fail(e.getMessage());
				}
			}
		};
	}

	private static Runnable setarCamposfalha(FrmLogin frmLogin, String usuario, String senha) {

		return new Runnable() {

			@Override
			public void run() {

				try {

					Thread.sleep(1000);

					getTxUsuario(frmLogin).setText(usuario);
					getTxSenha(frmLogin).setText(senha);

					Future<?> future = Executors.newSingleThreadExecutor().submit(new Runnable() {

						@Override
						public void run() {

							try {

								Thread.sleep(1000);

								Optional<Window> janela = Arrays.asList(Window.getWindows()).stream()
										.filter(x -> x instanceof JDialog
												&& ((JDialog) x).getTitle().toLowerCase().equals("verificação"))
										.findFirst();

								if (!janela.isPresent()) {
									assertTrue(false);
								}

								janela.get().dispose();

							} catch (Exception e) {

								fail(e.getMessage());
							}
						}
					});

					getBtLogar(frmLogin).doClick();

					future.get();

				} catch (Exception e) {

					fail(e.getMessage());
				}
			}
		};
	}

	private static JButton getBtLogar(FrmLogin frmLogin) throws Exception {

		return (JButton) FrmUtil.getFied(frmLogin, FrmLogin.class, "btLogin");
	}

	private static JTextField getTxUsuario(FrmLogin frmLogin) throws Exception {

		return (JTextField) FrmUtil.getFied(frmLogin, FrmLogin.class, "txLogin");
	}

	private static JPasswordField getTxSenha(FrmLogin frmLogin) throws Exception {

		return (JPasswordField) FrmUtil.getFied(frmLogin, FrmLogin.class, "txSenha");
	}

}
