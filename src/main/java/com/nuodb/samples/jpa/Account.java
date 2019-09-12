package com.nuodb.samples.jpa;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * A simple account entity for demo purposes. For consistency with the other
 * demos, the underlying table is called "Accounts" (plural).
 * <p>
 * To avoid developer errors, we recommend to make tables names either all
 * singular or all plural. Having some of each will trip you up, guaranteed.
 * 
 * @author Paul Chapman
 */
@Entity
@Table(name = "Accounts", schema = "demo")
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id = null;

	public String name;

	public int balance;

	/**
	 * Create an instance
	 * 
	 * @param name    Account owner's name.
	 * @param balance Initial deposit.
	 */
	public Account(String name, int balance) {
		this.name = name;
		this.balance = balance;
	}

	/**
	 * JPA requires a default constructor.
	 */
	protected Account() {
	}

	/**
	 * This should only be used during testing. Otherwise it is set and maintained
	 * by the database.
	 * 
	 * @param id
	 */
	protected void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	/**
	 * Name of account owner.
	 */
	public String getName() {
		return name;
	}

	public int getBalance() {
		return balance;
	}

	public void credit(int amount) {
		this.balance += amount;
	}

	public void debit(int amount) {
		this.balance -= amount;
	}

}
