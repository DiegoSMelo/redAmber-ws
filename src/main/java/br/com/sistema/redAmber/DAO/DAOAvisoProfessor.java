package br.com.sistema.redAmber.DAO;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import br.com.sistema.redAmber.DAO.generics.DAOGeneric;
import br.com.sistema.redAmber.basicas.AvisoProfessor;
import br.com.sistema.redAmber.basicas.BuscaAvisoProfessor;
import br.com.sistema.redAmber.basicas.enums.StatusAvisoProfessor;
import br.com.sistema.redAmber.exceptions.DAOException;
import br.com.sistema.redAmber.util.Mensagens;

public class DAOAvisoProfessor extends DAOGeneric<AvisoProfessor> implements IDAOAvisoProfessor {
	
	public DAOAvisoProfessor(EntityManager em) {
		super(em);
	}
	
	@Override
	public List<AvisoProfessor> consultarPorData(Calendar dataAviso) throws DAOException {
		try {
			TypedQuery<AvisoProfessor> result = entityManager
					.createQuery("SELECT ap FROM AvisoProfessor ap WHERE ap.dataAviso = :dataAviso", 
							AvisoProfessor.class);
			result.setParameter("dataAviso", dataAviso);
			return result.getResultList();
		} catch (NoResultException e2) {
			return null;
		} catch (Exception e) {
			throw new DAOException(Mensagens.m10);
		}
	}
	
	@Override
	public List<AvisoProfessor> consultarPendentes() throws DAOException {
		try {
			TypedQuery<AvisoProfessor> result = entityManager
					.createQuery("SELECT ap FROM AvisoProfessor ap "
							+ "WHERE ap.statusAvisoProfessor = :statusAvisoProfessor", 
							AvisoProfessor.class);
			result.setParameter("statusAvisoProfessor", StatusAvisoProfessor.ENVIADO);
			return result.getResultList();
		} catch (NoResultException e2) {
			return null;
		} catch (Exception e) {
			throw new DAOException(Mensagens.m10);
		}
	}

	@Override
	public List<AvisoProfessor> consultarPorProfessorData(Long idProfessor, 
			Calendar dataAviso) throws DAOException {
		try {
			String sql = "SELECT ap FROM AvisoProfessor ap";
			
			if (idProfessor != null && dataAviso == null) {
				sql += " WHERE ap.professor.id = :idProfessor";
			}
			if (idProfessor == null && dataAviso != null) {
				sql += " WHERE ap.dataAviso = :dataAviso";
			}
			if (idProfessor != null && dataAviso != null) {
				sql += " WHERE ap.professor.id = :idProfessor AND ap.dataAviso = :dataAviso";
			}
			
			TypedQuery<AvisoProfessor> result = entityManager.createQuery(sql, AvisoProfessor.class);
			
			if (idProfessor != null && dataAviso == null) {
				result.setParameter("idProfessor", idProfessor);
			}
			if (idProfessor == null && dataAviso != null) {
				result.setParameter("dataAviso", dataAviso);
			}
			if (idProfessor != null && dataAviso != null) {
				result.setParameter("idProfessor", idProfessor);
				result.setParameter("dataAviso", dataAviso);
			}
			return result.getResultList();
		} catch (NoResultException e2) {
			return null;
		} catch (Exception e) {
			throw new DAOException(Mensagens.m10);
		}
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public Integer consultarQuantidadeDeHoje() {

		List<AvisoProfessor> lista = new ArrayList<AvisoProfessor>();
		Integer quantidade = 0;
		Calendar dataAviso = Calendar.getInstance();
		Date hoje = new Date();
		dataAviso.setTime(hoje);
		int paramDia = hoje.getDay();
		int paramMes = hoje.getMonth();
		int paramAno = hoje.getYear();
		
		try {
			TypedQuery<AvisoProfessor> result = entityManager
					.createQuery("SELECT ap FROM AvisoProfessor ap WHERE "
							+ "ap.statusAvisoProfessor = :statusAvisoProfessor", 
							AvisoProfessor.class);
			result.setParameter("statusAvisoProfessor", StatusAvisoProfessor.ENVIADO);
			lista = result.getResultList();
			
			for (AvisoProfessor aviso : lista) {
				if (aviso.getDataAviso().getTime().getDay() == paramDia && 
						aviso.getDataAviso().getTime().getMonth() == paramMes && 
						aviso.getDataAviso().getTime().getYear() == paramAno) {
					quantidade++;
				}
			}
			return quantidade;
		} catch (NoResultException e2) {
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public List<AvisoProfessor> listarAvisosProfessorPorParametros(BuscaAvisoProfessor consulta) {
		
		try {
			String sql = "SELECT av from AvisoProfessor av";
			
			List<AvisoProfessor> resultado = new ArrayList<AvisoProfessor>();
			List<AvisoProfessor> resposta = new ArrayList<AvisoProfessor>();
			
			if (consulta.getIdProfessor() != null && consulta.getStatus() == null) {
				sql += " WHERE av.professor.id = :idProfessor";
			}
			if (consulta.getIdProfessor() == null && consulta.getStatus() != null) {
				sql += " WHERE av.statusAvisoProfessor = :status";
			}
			if (consulta.getIdProfessor() != null && consulta.getStatus() != null) {
				sql += " WHERE av.professor.id = :idProfessor AND av.statusAvisoProfessor = :status";
			}
			
			TypedQuery<AvisoProfessor> query = entityManager.createQuery(sql, AvisoProfessor.class);
			
			if (consulta.getIdProfessor() != null && consulta.getStatus() == null) {
				query.setParameter("idProfessor", consulta.getIdProfessor());
			}
			if (consulta.getIdProfessor() == null && consulta.getStatus() != null) {
				query.setParameter("status", consulta.getStatus());
			}
			if (consulta.getIdProfessor() != null && consulta.getStatus() != null) {
				query.setParameter("idProfessor", consulta.getIdProfessor());
				query.setParameter("status", consulta.getStatus());
			}
			
			resultado = query.getResultList();
			
			if (consulta.getDataAviso() != null) {
				int dia = consulta.getDataAviso().getDate();
				int mes = consulta.getDataAviso().getMonth();
				int ano = consulta.getDataAviso().getYear();
				
				for (AvisoProfessor aviso : resultado) {
					if (aviso.getDataAviso().getTime().getDate() == dia && 
							aviso.getDataAviso().getTime().getMonth() == mes && 
							aviso.getDataAviso().getTime().getYear() == ano) {
						resposta.add(aviso);
					}
				}
				return resposta;
			}
			return resultado;
		} catch (NoResultException e2) {
			e2.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}