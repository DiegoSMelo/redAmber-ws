package br.com.sistema.redAmber.DAO;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import br.com.sistema.redAmber.DAO.generics.DAOGeneric;
import br.com.sistema.redAmber.basicas.Aluno;
import br.com.sistema.redAmber.basicas.BuscaAluno;
import br.com.sistema.redAmber.basicas.Disciplina;
import br.com.sistema.redAmber.basicas.GeralUsuario;
import br.com.sistema.redAmber.basicas.enums.StatusUsuario;
import br.com.sistema.redAmber.exceptions.DAOException;
import br.com.sistema.redAmber.util.Mensagens;

public class DAOAluno extends DAOGeneric<Aluno> implements IDAOAluno {

	public DAOAluno(EntityManager em) {
		super(em);
	}

	@Override
	public Aluno buscarAlunoPorRG(String rg) throws DAOException {

		try {
			TypedQuery<Aluno> result = entityManager.createQuery("SELECT a FROM Aluno a WHERE "
					+ "a.rg = :rg", Aluno.class);
			result.setParameter("rg", rg);
			Aluno aluno = result.getSingleResult();
			return aluno;
		} catch (NoResultException e2) {
			e2.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(Mensagens.m1);
		}
	}

	@Override
	public Aluno buscarAlunoPorLoginSenha(String login, String senha) throws DAOException {

		try {
			TypedQuery<Aluno> result = entityManager.createQuery(
					"SELECT a FROM Aluno a WHERE a.usuario.login = :login AND "
					+ "a.usuario.senha = :senha", Aluno.class);
			result.setParameter("login", login);
			result.setParameter("senha", senha);

			Aluno aluno = result.getSingleResult();

			return aluno;
		} catch (NoResultException e2) {
			e2.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(Mensagens.m1);
		}
	}

	@Override
	public List<Aluno> buscarAlunosPorNomeRG(BuscaAluno bA) throws DAOException {

		String sql = "SELECT a FROM Aluno a WHERE a.status = :status";

		if ((bA.getNome() != null && !bA.getNome().trim().equals("") && !bA.getNome().isEmpty())
				&& (bA.getRg() == null || bA.getRg().trim().equals("") || bA.getRg().isEmpty())) {
			sql += " AND a.nome LIKE :nome";
		}
		if ((bA.getNome() == null || bA.getNome().trim().equals("") || bA.getNome().isEmpty())
				&& (bA.getRg() != null && !bA.getRg().trim().equals("") && !bA.getRg().isEmpty())) {
			sql += " AND a.rg = :rg";
		}
		if ((bA.getNome() != null && !bA.getNome().trim().equals("") && !bA.getNome().isEmpty())
				&& (bA.getRg() != null && !bA.getRg().trim().equals("") && !bA.getRg().isEmpty())) {
			sql += " AND a.nome LIKE :nome AND a.rg = :rg";
		}

		TypedQuery<Aluno> result = entityManager.createQuery(sql, Aluno.class);
		result.setParameter("status", StatusUsuario.ATIVO);

		try {
			if ((bA.getNome() != null && !bA.getNome().trim().equals("") && !bA.getNome().isEmpty())
					&& (bA.getRg() == null || bA.getRg().trim().equals("") || bA.getRg().isEmpty())) {
				result.setParameter("nome", "%" + bA.getNome() + "%");
			}
			if ((bA.getNome() == null || bA.getNome().trim().equals("") || bA.getNome().isEmpty())
					&& (bA.getRg() != null && !bA.getRg().trim().equals("") && !bA.getRg().isEmpty())) {
				result.setParameter("rg", bA.getRg());
			}
			if ((bA.getNome() != null && !bA.getNome().trim().equals("") && !bA.getNome().isEmpty())
					&& (bA.getRg() != null && !bA.getRg().trim().equals("") && !bA.getRg().isEmpty())) {
				result.setParameter("nome", "%" + bA.getNome() + "%");
				result.setParameter("rg", bA.getRg());
			}
			return result.getResultList();
		} catch (NoResultException e2) {
			e2.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(Mensagens.m2);
		}
	}

	@Override
	public GeralUsuario buscarGeralUsuarioPorLoginSenha(String login, String senha) throws DAOException {
		try {
			TypedQuery<GeralUsuario> result = entityManager.createQuery(
					"SELECT g FROM GeralUsuario g WHERE g.usuario.login = :login "
					+ "AND g.usuario.senha = :senha", GeralUsuario.class);
			result.setParameter("login", login);
			result.setParameter("senha", senha);

			GeralUsuario geralUsuario = result.getSingleResult();

			return geralUsuario;
		} catch (NoResultException e2) {
			e2.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(Mensagens.m1);
		}
	}

	@Override
	public List<Disciplina> buscarDisciplinasPorAluno(Long idAluno) {

		try {
			String jpql = "SELECT gd.id.disciplina FROM Grade_Disciplina gd WHERE gd.id.grade IN "
					+ "(SELECT m.grade FROM Matricula m WHERE m.aluno.id = :idAluno)";
			TypedQuery<Disciplina> result = entityManager.createQuery(jpql, Disciplina.class);
			result.setParameter("idAluno", idAluno);
			return result.getResultList();
		} catch (NoResultException e2) {
			e2.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Aluno buscarAlunoPorEmail(String email) {

		try {
			String jpql = "SELECT a FROM Aluno a WHERE a.email = :email";
			TypedQuery<Aluno> result = entityManager.createQuery(jpql, Aluno.class);
			result.setParameter("email", email);
			return result.getSingleResult();
		} catch (NoResultException e2) {
			e2.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}