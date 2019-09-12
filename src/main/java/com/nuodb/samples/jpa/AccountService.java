package com.nuodb.samples.jpa;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

/**
 * An interface for managing accounts.
 * 
 * @author Paul Chapman
 */
@Transactional
public interface AccountService {

	public long totalAccounts();

	public void save(List<Account> accounts);

	public Iterable<Account> findAll();

	public List<Account> find(String match);
}
