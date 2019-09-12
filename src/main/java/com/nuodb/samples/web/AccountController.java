package com.nuodb.samples.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.nuodb.samples.jpa.Account;
import com.nuodb.samples.jpa.AccountService;

@RestController
class AccountController {
	private AccountService accountService;

	public AccountController(AccountService accountService) {
		this.accountService = accountService;
	}

	@GetMapping("/accounts")
	public Iterable<Account> allAccounts() {
		return accountService.findAll();
	}

	@GetMapping("/accounts/search/{match}")
	public List<Account> search(@PathVariable("match") String match) {
		return accountService.find(match);
	}
}
