package com.techelevator.tenmo.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;

@Component
public class JDBCTransferDAO implements TransferDAO {

	private JdbcTemplate template;

	public JDBCTransferDAO(JdbcTemplate template) {
		this.template = template;
	}

	public void transferFunds(Transfer transfer) {
		// Subtracting From payment-maker
		String subtract = "UPDATE accounts " + "SET balance = (accounts.balance - ?) "
				+ "WHERE user_id = (SELECT user_id FROM users WHERE username = ?)";
		template.update(subtract, transfer.getAmount(), transfer.getFromUserName());

		String add = "UPDATE accounts SET balance = (accounts.balance + ?) "
				+ "WHERE user_id = (SELECT user_id FROM users WHERE username = ?)";
		template.update(add, transfer.getAmount(), transfer.getToUserName());

		String log = "INSERT INTO transfers (transfer_type_id, transfer_status_id, "
				+ "account_from, account_to, amount) "
				+ "VALUES (2, 2, (SELECT user_id FROM users WHERE username = ?), "
				+ "(SELECT user_id FROM users WHERE username = ?), ?);";
		template.update(log, transfer.getFromUserName(), transfer.getToUserName(), transfer.getAmount());
	}

	@Override
	public double getBalance(String username) {
		double balance = 0;
		String sql = "SELECT balance FROM accounts " + "JOIN users ON users.user_id = accounts.user_id "
				+ "WHERE users.username = ?";
		SqlRowSet row = template.queryForRowSet(sql, username);
		if (row.next()) {
			balance = row.getDouble("balance");
		}
		return balance;
	}

	
	public void requestTransfer(Transfer transfer) {
		
		String log = "INSERT INTO transfers (transfer_type_id, transfer_status_id, "
				+ "account_from, account_to, amount) "
				+ "VALUES (1, 1, (SELECT user_id FROM users WHERE username = ?), "
				+ "(SELECT user_id FROM users WHERE username = ?), ?);";
		template.update(log, transfer.getFromUserName(), transfer.getToUserName(), transfer.getAmount());
		
		
	}
	
	public List<Transfer> getPendingTransfers(User user){
		String sql = "SELECT * FROM transfers WHERE account_from = (SELECT user_id FROM users WHERE username = ?) "
				+ "AND transfer_status_id = 1";
		
		SqlRowSet row = template.queryForRowSet(sql, user.getUsername());
		List<Transfer> transferList = new ArrayList<Transfer>();
		while (row.next()) {
			
			Transfer transfer = new Transfer();
			transfer.setAmount(row.getDouble("amount"));
			transfer.setFromUser(row.getInt("account_from"));
			transfer.setToUser(row.getInt("account_to"));
			transfer.setTransferId(row.getInt("transfer_id"));
			transfer.setTransferStatus(row.getInt("transfer_status_id"));
			transfer.setTransferType(row.getInt("transfer_type_id"));
			
			String sql2 = "SELECT username FROM users WHERE user_id = ?";  
			
			SqlRowSet row2 = template.queryForRowSet(sql2, transfer.getToUser());
			row2.next();
			transfer.setToUserName(row2.getString("username"));
			
			SqlRowSet row3 = template.queryForRowSet(sql2, transfer.getFromUser());
			row3.next();
			transfer.setFromUserName(row3.getString("username"));
			
			transferList.add(transfer);
		}
		return transferList;
	}
	
	
	public void updateTransferRequest(Transfer transfer) {
		String sql = "UPDATE transfers " + 
				"SET transfer_status_id = ? " + 
				"WHERE transfer_id = ?;";
		template.update(sql, transfer.getTransferStatus(), transfer.getTransferId());
		
		if(transfer.getTransferStatus() == 2) {
			String subtract = "UPDATE accounts " + "SET balance = (accounts.balance - ?) "
					+ "WHERE user_id = (SELECT user_id FROM users WHERE username = ?)";
			template.update(subtract, transfer.getAmount(), transfer.getFromUserName());

			String add = "UPDATE accounts SET balance = (accounts.balance + ?) "
					+ "WHERE user_id = (SELECT user_id FROM users WHERE username = ?)";
			template.update(add, transfer.getAmount(), transfer.getToUserName());
		}
		
		
	}
	
	
	

	@Override
	public List<Transfer> viewAllTransfers(User user) {
		
		String sql = "SELECT * FROM transfers WHERE account_from = ? OR account_to = ?";
		
		SqlRowSet row = template.queryForRowSet(sql, user.getId(), user.getId());
		
		List<Transfer> transferList = new ArrayList<Transfer>();
	
				while (row.next()) {
					
					Transfer transfer = new Transfer();
					transfer.setAmount(row.getDouble("amount"));
					transfer.setFromUser(row.getInt("account_from"));
					transfer.setToUser(row.getInt("account_to"));
					transfer.setTransferId(row.getInt("transfer_id"));
					transfer.setTransferStatus(row.getInt("transfer_status_id"));
					transfer.setTransferType(row.getInt("transfer_type_id"));
					
					
					String sql2 = "SELECT username FROM users WHERE user_id = ?";  
					
					SqlRowSet row2 = template.queryForRowSet(sql2, transfer.getToUser());
					row2.next();
					transfer.setToUserName(row2.getString("username"));
					
					SqlRowSet row3 = template.queryForRowSet(sql2, transfer.getFromUser());
					row3.next();
					transfer.setFromUserName(row3.getString("username"));
					
					transferList.add(transfer);
					
			
				}
		
		return transferList;
	}

	@Override
	public String getTransferDetails(int id) {
		// TODO Auto-generated method stub
		return null;
	}

}
