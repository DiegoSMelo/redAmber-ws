package br.com.sistema.redAmber.ws;

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

import br.com.sistema.redAmber.basicas.Aluno;
import br.com.sistema.redAmber.basicas.Grade;
import br.com.sistema.redAmber.basicas.Matricula;
import br.com.sistema.redAmber.basicas.http.AlunoHTTP;
import br.com.sistema.redAmber.basicas.http.MatriculaHTTP;
import br.com.sistema.redAmber.rn.RNGrade;
import br.com.sistema.redAmber.rn.RNMatricula;
import br.com.sistema.redAmber.util.Datas;

@Path("/matriculaws")
public class MatriculaWS {
	
	private RNMatricula rnMatricula;
	private RNGrade rnGrade;
	private Gson gson;
	
	public MatriculaWS() {
		
		this.rnMatricula = new RNMatricula();
		this.rnGrade = new RNGrade();
		this.gson = new Gson();
	}

	@POST
	@Path("salvar")
	@Consumes("application/json")
	@Produces("text/plain")
	public String salvarMatricula(String jsonMatricula){
	
		Matricula matricula = new Matricula();
		Aluno aluno = new Aluno();
		MatriculaHTTP matriculaHTTP = this.gson.fromJson(jsonMatricula, MatriculaHTTP.class);
		AlunoHTTP alunoHTTP = matriculaHTTP.getAluno();
		
		Calendar dataNascimento = Datas.converterDateToCalendar(new Date(Long.parseLong(alunoHTTP.getDataNascimento())));
		
		aluno.setId(alunoHTTP.getId());
		aluno.setNome(alunoHTTP.getNome());
		aluno.setDataNascimento(dataNascimento);
		aluno.setRg(alunoHTTP.getRg());
		aluno.setEmail(alunoHTTP.getEmail());
		aluno.setTelefone(alunoHTTP.getTelefone());
		aluno.setUsuario(alunoHTTP.getUsuario());
		aluno.setStatus(alunoHTTP.getStatus());
		
		Calendar dataMatricula = Datas.converterDateToCalendar(new Date(Long.parseLong(matriculaHTTP.getDataMatricula())));
		
		matricula.setId(matriculaHTTP.getId());
		matricula.setCodigoMatricula(matriculaHTTP.getCodigoMatricula());
		matricula.setDataMatricula(dataMatricula);
		matricula.setAluno(aluno);
		matricula.setEntrada(matriculaHTTP.getEntrada());
		matricula.setStatus(matriculaHTTP.getStatus());
		
		Long id = null;
		try {
			id = matriculaHTTP.getGrade().getId();
		} catch (Exception e) {
			id = null;
		}
		if (id != null) {
			Grade grade = rnGrade.buscarGradePorId(id);
			matricula.setGrade(grade);
		}
		
		this.rnMatricula.salvar(matricula);
		
		return "Matrícula salva com sucesso.";
	}
	
	@GET
	@Path("aluno/{id}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
	public String listarMatriculasPorIdAluno(@PathParam("id") String id){
		
		List<Matricula> lista =  this.rnMatricula.listarMatriculasPorIdAluno(Long.parseLong(id));
		return this.gson.toJson(lista);
	}
	
	@GET
	@Path("buscar-por-codigo/{codigo}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
	public String buscarMatriculaPorCodigoMatricula(@PathParam("codigo") String codigo) {
		
		Matricula matricula = this.rnMatricula.buscarMatriculaPorCodigoMatricula(codigo);
		return this.gson.toJson(matricula);
	}
	
	@GET
	@Path("buscar-por-id/{id}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
	public String buscarMatriculaPorId(@PathParam("id") String id) {
		
		Matricula matricula = this.rnMatricula.buscarMatriculaPorId(Long.parseLong(id));
		return this.gson.toJson(matricula);
	}
}