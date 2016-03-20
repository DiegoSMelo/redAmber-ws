package br.com.sistema.redAmber.DAO;

import br.com.sistema.redAmber.DAO.generics.IDAOGeneric;
import br.com.sistema.redAmber.basicas.Professor;
import br.com.sistema.redAmber.exceptions.DAOException;

public interface IDAOProfessor extends IDAOGeneric<Professor>{

	public Professor buscarProfessorPorRg(String rg);
	public Professor buscarProfessorPorLoginSenha(String login, String senha) throws DAOException;

}
