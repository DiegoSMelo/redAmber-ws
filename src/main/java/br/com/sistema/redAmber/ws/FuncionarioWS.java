package br.com.sistema.redAmber.ws;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import br.com.sistema.redAmber.basicas.BuscaFuncionario;
import br.com.sistema.redAmber.basicas.Funcionario;
import br.com.sistema.redAmber.basicas.Usuario;
import br.com.sistema.redAmber.basicas.enums.TipoFuncionario;
import br.com.sistema.redAmber.basicas.enums.TipoUsuario;
import br.com.sistema.redAmber.basicas.http.FuncionarioHTTP;
import br.com.sistema.redAmber.basicas.http.LoginHTTP;
import br.com.sistema.redAmber.exceptions.DAOException;
import br.com.sistema.redAmber.exceptions.EmailException;
import br.com.sistema.redAmber.exceptions.RNException;
import br.com.sistema.redAmber.rn.RNFuncionario;
import br.com.sistema.redAmber.util.Datas;

@Path("funcionariows")
public class FuncionarioWS {

	private RNFuncionario rnFuncionario;
	private Gson gson;

	public FuncionarioWS() {
		this.gson = new Gson();
		this.rnFuncionario = new RNFuncionario();
		try {
			this.rnFuncionario.inserirAdmin();
		} catch (RNException e) {
			e.printStackTrace();
		} catch (EmailException e) {
			e.printStackTrace();
		} catch (DAOException e) {
			e.printStackTrace();
		}
	}

	@POST
	@Path("salvar")
	@Consumes("application/json")
	@Produces("text/plain")
	public String salvarFuncionario(String jsonFuncionario) {
		try {
			Funcionario funcionario = new Funcionario();
			FuncionarioHTTP funcionarioHTTP = this.gson.fromJson(jsonFuncionario, FuncionarioHTTP.class);
			Usuario usuario = new Usuario();
			usuario = funcionarioHTTP.getUsuario();
			
			if (usuario != null) {
				funcionario.setUsuario(usuario);
			}
			
			Calendar dataNascimento = Datas
					.converterDateToCalendar(new Date(Long.parseLong(funcionarioHTTP.getDataNascimento())));

			funcionario.setId(funcionarioHTTP.getId());
			funcionario.setDataNascimento(dataNascimento);
			funcionario.setEmail(funcionarioHTTP.getEmail());
			funcionario.setNome(funcionarioHTTP.getNome());
			funcionario.setRg(funcionarioHTTP.getRg());
			funcionario.setStatus(funcionarioHTTP.getStatus());
			funcionario.setTelefone(funcionarioHTTP.getTelefone());
			funcionario.setTipo(TipoUsuario.FUNCIONARIO);

			if (funcionarioHTTP.getTipoFuncionario().equals("C")) {
				funcionario.setTipoFuncionario(TipoFuncionario.C);
			} else {
				if (funcionarioHTTP.getTipoFuncionario().equals("S")) {
					funcionario.setTipoFuncionario(TipoFuncionario.S);
				}
			}
			this.rnFuncionario.salvar(funcionario);
			return "Funcionário salvo com sucesso";
		} catch (RNException e) {
			e.printStackTrace();
			return "Data de nascimento futura";
		} catch (EmailException e) {
			e.printStackTrace();
			return "Email duplicado";
		} catch (DAOException e) {
			return "Error";
		}
	}

	@GET
	@Path("buscar-por-rg/{rg}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
	public String buscaFuncionarioPorRG(@PathParam("rg") String rg) {
		try {
			return this.gson.toJson(this.rnFuncionario.buscarFuncionarioPorRG(rg));
		} catch (DAOException e) {
			return null;
		}
	}

	@GET
	@Path("funcionario/{id}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
	public String buscaFuncionarioPorId(@PathParam("id") String id) {
		return this.gson.toJson(this.rnFuncionario.buscarPorId(Long.parseLong(id)));
	}

	@GET
	@Path("listar")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
	public String listaFuncionarios() {
		List<Funcionario> listaFuncionarios = this.rnFuncionario.listarTodosFuncionarios();
		return this.gson.toJson(listaFuncionarios);
	}

	// -----------------------------------------------------------------------------------------

	@GET
	@Path("funcionario/por-login-senha/{login}/{senha}")
	@Produces("application/json")
	public String buscarFuncionarioPorLoginSenha(@PathParam("login") String login, @PathParam("senha") String senha) {
		try {
			LoginHTTP loginHTTP = new LoginHTTP(login, senha);
			Funcionario funcionario = this.rnFuncionario.buscarFuncionarioPorLoginSenha(loginHTTP.getLogin(),
					loginHTTP.getSenha());

			return this.gson.toJson(funcionario);
		} catch (JsonSyntaxException e) {
			return "Error";
		} catch (DAOException e) {
			return "Error";
		}
	}

	@POST
	@Path("login/funcionario")
	@Consumes("application/json")
	@Produces("application/json")
	public String buscarFuncionarioPorLoginSenha(String jsonLogin) {
		try {
			LoginHTTP loginHTTP = this.gson.fromJson(jsonLogin, LoginHTTP.class);
			Funcionario funcionario = this.rnFuncionario.buscarFuncionarioPorLoginSenha(loginHTTP.getLogin(),
					loginHTTP.getSenha());

			return this.gson.toJson(funcionario);
		} catch (JsonSyntaxException e) {
			return "Error";
		} catch (DAOException e) {
			return "Error";
		}
	}

	@GET
	@Path("funcionario/por-login/{login}")
	@Produces("application/json")
	public String buscarFuncionarioPorLogin(@PathParam("login") String login) {
		try {
			LoginHTTP loginHTTP = new LoginHTTP();
			loginHTTP.setLogin(login);

			Funcionario funcionario = this.rnFuncionario.buscarFuncionarioPorLogin(loginHTTP.getLogin());
			return this.gson.toJson(funcionario);
		} catch (JsonSyntaxException e) {
			return "Error";
		} catch (DAOException e) {
			return "Error";
		}
	}

	@POST
	@Path("buscar-por-nome-rg")
	@Consumes("application/json")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
	public String buscarFuncionarioPorNomeRG(String buscaFuncionario) {
		BuscaFuncionario consulta = new BuscaFuncionario();
		consulta = this.gson.fromJson(buscaFuncionario, BuscaFuncionario.class);
		List<Funcionario> listaFuncionarios = new ArrayList<Funcionario>();
		try {
			listaFuncionarios = this.rnFuncionario.buscarFuncionariosPorNomeRG(consulta);
		} catch (DAOException e) {
			e.printStackTrace();
		}
		return this.gson.toJson(listaFuncionarios);
	}
}