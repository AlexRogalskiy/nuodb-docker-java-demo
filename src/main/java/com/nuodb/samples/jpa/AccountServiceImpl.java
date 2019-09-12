package com.nuodb.samples.jpa;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Manages saving and retrieving accounts.
 * <p>
 * In this simple example, this class does nothing but call through to an
 * {@link AccountRepository}. However in a real application this class would
 * contain your business logic. It is tempting to merge the service and
 * repository class into a single class, but in the long run you will need both.
 * This is a classic example of "separation of concerns" - isolating business
 * logic from data persistence.
 * <p>
 * Note that this class is transactional because {@link AccountService} is
 * annotated with Spring's {@link Transactional} annotation. You could use the
 * {@code javax.transaction.Transactional} if you prefer, Spring supports both.
 * 
 * @author Paul Chapman
 */
@Service
public class AccountServiceImpl implements AccountService {

	private AccountRepository accountRepository;

	@Autowired
	public AccountServiceImpl(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	@Override
	public long totalAccounts() {
		// YOUR BUSINESS LOGIC HERE
		return accountRepository.count();
	}
	
	@Override
	public void save(List<Account> accounts) {
		// YOUR BUSINESS LOGIC HERE
		accountRepository.save(accounts);
	}

	@Override
	public Iterable<Account> findAll() {
		// YOUR BUSINESS LOGIC HERE
		return accountRepository.findAll();
	}

	@Override
	public List<Account> find(String match) {
		// YOUR BUSINESS LOGIC HERE
		return accountRepository.findByNameLike(match);
	}

}
