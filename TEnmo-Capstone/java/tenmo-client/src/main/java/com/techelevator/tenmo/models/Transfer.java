package com.techelevator.tenmo.models;

public class Transfer {
	
	private int fromUser;
	private int toUser;
	private double amount;
	private String token;
	private int transferType;
	private int transferId;
	private int transferStatus;
	private String fromUserName;
	private String toUserName;
	
	
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Transfer() {
		
	}

	public int getFromUser() {
		return fromUser;
	}

	public void setFromUser(int fromUser) {
		this.fromUser = fromUser;
	}

	public int getToUser() {
		return toUser;
	}

	public void setToUser(int toUser) {
		this.toUser = toUser;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public int getTransferType() {
		return transferType;
	}

	public void setTransferType(int transferType) {
		this.transferType = transferType;
	}

	public int getTransferId() {
		return transferId;
	}

	public void setTransferId(int transferId) {
		this.transferId = transferId;
	}

	public int getTransferStatus() {
		return transferStatus;
	}

	public void setTransferStatus(int transferStatus) {
		this.transferStatus = transferStatus;
	}
	
	
	
	public String getFromUserName() {
		return fromUserName;
	}

	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}

	public String getToUserName() {
		return toUserName;
	}

	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}

	public String getTransferTypeName() {
		if (transferType == 1) {
			return "Request";
		}
		else {
			return "Send";
		}
	}
	
	public String getTransferStatusName() {
		if (transferStatus == 1) {
			return "Pending";
		}
		else if (transferStatus == 2) {
			return "Approved";
		}
		else {
			return "Rejected";
		}
	}
	

}
