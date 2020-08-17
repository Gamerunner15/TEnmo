package com.techelevator.tenmo.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.User;


public class UserService {
	
	private String BASE_URL;
    private RestTemplate template = new RestTemplate();

    public UserService(String url) {
        this.BASE_URL = url;
    }
    
    
    public double getCurrentBalance(AuthenticatedUser currentUser) {
    	
    	HttpEntity<User> entity = this.makeEntity(currentUser);
    	double balance = template.postForObject(BASE_URL + "balance", entity, Double.class);
    	
    	return balance;
    }
	
    public User[] listUsers(){
    	return template.getForObject(BASE_URL + "users", User[].class);
    }
    
    public void sendMoney(Transfer transfer) {
    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	headers.setBearerAuth(transfer.getToken());
    	HttpEntity<Transfer> entity = new HttpEntity<Transfer>(transfer, headers);
    	template.postForObject(BASE_URL + "transfer", entity, Transfer.class);
    }
    
    

    public Transfer[] getTransfers(AuthenticatedUser currentUser) {
    	HttpEntity<User> entity = this.makeEntity(currentUser);
    	Transfer[] transferList = template.postForObject(BASE_URL + "transfer/" + currentUser.getUser().getUsername(), entity, Transfer[].class);
    	
		return transferList;
    	
    }
    
    public void requestTransfer(Transfer transfer) {
    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	headers.setBearerAuth(transfer.getToken());
    	HttpEntity<Transfer> entity = new HttpEntity<Transfer>(transfer, headers);
    	template.postForObject(BASE_URL + "request", entity, Transfer.class);
    }
    
    public Transfer[] getPendingRequests(AuthenticatedUser currentUser) {
    	HttpEntity<User> entity = this.makeEntity(currentUser);
    	Transfer[] transferList = template.postForObject(BASE_URL + "pending/" + currentUser.getUser().getUsername(), entity, Transfer[].class);
    	
		return transferList;
    	
    }
    
    public void updateTransferRequest(Transfer transfer) {
    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	headers.setBearerAuth(transfer.getToken());
    	HttpEntity<Transfer> entity = new HttpEntity<Transfer>(transfer, headers);
    	template.put(BASE_URL + "transfer/" + transfer.getFromUserName(), entity, Transfer.class);
    	
    }
    
    
    
    
    
    
    
    
    public HttpEntity<User> makeEntity(AuthenticatedUser currentUser){
    	User user = new User();
    	user.setId(Integer.valueOf(currentUser.getUser().getId()));
    	user.setUsername(currentUser.getUser().getUsername());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(currentUser.getToken());
		HttpEntity<User> entity = new HttpEntity<User>(user, headers);
		return entity;
	}
    
    
    
}
