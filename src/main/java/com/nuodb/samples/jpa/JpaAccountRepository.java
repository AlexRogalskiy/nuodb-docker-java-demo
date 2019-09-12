package com.nuodb.samples.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

/**
 * Handles storing and retrieving accounts from NuoDB using JPA.
 * 
 * @author Paul Chapman
 */
@Repository
public class JpaAccountRepository implements AccountRepository {

	public static final String SELECT_ACCOUNTS = "SELECT a FROM Account a";

	public static final String SELECT_NUM_ACCOUNTS = "SELECT count(*) FROM Account a";

	private EntityManager entityManager;

	/**
	 * Pass in the EntityManager to use. In fact the entity-manager is a Spring
	 * generated proxy - every time it is used it resolves to the entity-manager
	 * that is current for the current transaction. (By default, every thread gets a
	 * new entity-manager for every transaction it runs).
	 * <p>
	 * Per the JPA spec, Spring automatically invokes this method due to the
	 * {@link PersistenceContext} annotation (works like Spring's {@code @Autowired}
	 * but cannot be used with a constructor).
	 * <p>
	 * The JPA spec assumes that the {@code @PersistenceContext} method is invoked
	 * on a request-scoped bean, getting the current entity-manager at the time.
	 * Spring beans, like this one, are typically singletons, so Spring supports
	 * {@code @PersistenceContext} but passes a singleton proxy, which resolves
	 * every time it is used.
	 *
	 * @param entityManager Entity manager proxy.
	 */
	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public long count() {
		TypedQuery<Long> q = //
				entityManager.createQuery(SELECT_NUM_ACCOUNTS, Long.class);
		return q.getSingleResult();

	}

	@Override
	public void save(List<Account> accounts) {
		for (Account account : accounts)
			entityManager.persist(account);
	}

	@Override
	public Iterable<Account> findAll() {
		TypedQuery<Account> q = //
				entityManager.createQuery(SELECT_ACCOUNTS, Account.class);
		return q.getResultList();
	}

	@Override
	public List<Account> findByNameLike(String match) {
		match = ("%" + match + '%').toUpperCase();

		TypedQuery<Account> q = //
				entityManager.createQuery(SELECT_ACCOUNTS + " WHERE upper(name) LIKE ?1", //
						Account.class);
		q.setParameter(1, match);
		return q.getResultList();
	}

}
