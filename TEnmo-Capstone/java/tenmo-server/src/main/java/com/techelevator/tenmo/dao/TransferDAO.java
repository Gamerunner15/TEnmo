package com.techelevator.tenmo.dao;

import java.util.List;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;

public interface TransferDAO {
	
	public void transferFunds(Transfer transfer);
	
	public double getBalance(String username);
	
	public void updateTransferRequest(Transfer transfer);
	
	public List<Transfer> viewAllTransfers(User user);
	
	public String getTransferDetails(int id);
	
	public void requestTransfer(Transfer transfer);
	
	public List<Transfer> getPendingTransfers(User user);
	
	
	
	

}
