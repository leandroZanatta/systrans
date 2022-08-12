package br.com.systrans.util.sincronizacao;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;

public class ServerEndpoints {

	private static ServerEndpoints instance;

	private final WebTarget webTarget;
	public Invocation.Builder servidorAtivo;
	public Invocation.Builder obterVersoes;
	public Invocation.Builder usuarios;
	public Invocation.Builder configuracaoAbastecimento;
	public Invocation.Builder clientes;
	public Invocation.Builder abastecimentoMediaVeiculo;
	public Invocation.Builder formasPagamento;
	public Invocation.Builder veiculos;

	public ServerEndpoints(String urlServidor) {
		this.webTarget = ClientBuilder.newClient().target(urlServidor);
		this.servidorAtivo = webTarget.path("api/v1/servidor-ativo").request(MediaType.APPLICATION_JSON);
		this.obterVersoes = webTarget.path("api/v1/sincronizacao/all").request(MediaType.APPLICATION_JSON);
		this.usuarios = webTarget.path("api/v1/usuario").request(MediaType.APPLICATION_JSON);
		this.clientes = webTarget.path("api/v1/clientes").request(MediaType.APPLICATION_JSON);
		this.formasPagamento = webTarget.path("api/v1/formas-pagamento").request(MediaType.APPLICATION_JSON);
		this.veiculos = webTarget.path("api/v1/veiculos").request(MediaType.APPLICATION_JSON);
		this.configuracaoAbastecimento = webTarget.path("api/v1/configuracao-abastecimento").request(MediaType.APPLICATION_JSON);
		this.abastecimentoMediaVeiculo = webTarget.path("api/v1/abastecimento-media-veiculo").request(MediaType.APPLICATION_JSON);
	}

	public static void createInstance(String url) {

		instance = new ServerEndpoints(url);
	}

	public static ServerEndpoints getinstance() {

		return instance;
	}
}
