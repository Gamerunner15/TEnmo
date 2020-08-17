package com.techelevator.tenmo.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.techelevator.tenmo.dao.JDBCTransferDAO;
import com.techelevator.tenmo.dao.TransferDAO;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;

@PreAuthorize("isAuthenticated()")
@RestController
public class TransferController {
	
	private TransferDAO transferDAO;
	
	
	public TransferController(TransferDAO transferDAO) {
		this.transferDAO = transferDAO;
	}
	
	@RequestMapping(path = "/balance", method = RequestMethod.POST)
	public double getUserBalance(@RequestBody User user) {
		
		return transferDAO.getBalance(user.getUsername());
		
	}
	
	@RequestMapping(path = "/transfer", method = RequestMethod.POST)
	public void transferFunds(@RequestBody Transfer transfer) {
		transferDAO.transferFunds(transfer);
	}
	
	
	@RequestMapping(path = "/transfer/{username}", method = RequestMethod.POST)
	public List<Transfer> getTransferLog(@RequestBody User user) {
		return transferDAO.viewAllTransfers(user);	
	}
	
	@RequestMapping(path = "/request", method = RequestMethod.POST)
	public void requestTransfer(@RequestBody Transfer transfer) {
		transferDAO.requestTransfer(transfer);
	}
	
	
	@RequestMapping(path = "/pending/{username}", method = RequestMethod.POST)
	public List<Transfer> getPendingRequests(@RequestBody User user){
		return transferDAO.getPendingTransfers(user);
	}
	
	@RequestMapping(path = "/transfer/{username}", method = RequestMethod.PUT)
	public void updateTransferRequest(@RequestBody Transfer transfer) {
		transferDAO.updateTransferRequest(transfer);
	}
	
	

}
