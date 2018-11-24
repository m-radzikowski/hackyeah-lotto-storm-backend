package repository;

import javax.ejb.Stateless;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class EntityManagerProducer {

	@PersistenceContext(unitName = "LottoPU")
	private EntityManager entityManager;

	@Produces
	public EntityManager produceLottoEM() {
		return entityManager;
	}
}
