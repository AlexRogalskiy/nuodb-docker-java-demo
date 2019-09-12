package com.nuodb.samples.jpa;

import java.util.List;

/**
 * An interface for storing and retrieving accounts, typically from a persistent
 * data store such as NuoDB.
 * 
 * @author Paul Chapman
 */
public interface AccountRepository {
	
	public long count();
	
	public void save(List<Account> accounts);

	public Iterable<Account> findAll();

	public List<Account> findByNameLike(String match);
}
