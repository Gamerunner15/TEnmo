package com.techelevator.tenmo;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Scanner;

import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.InsufficientFundsException;
import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.User;
import com.techelevator.tenmo.models.UserCredentials;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.AuthenticationServiceException;
import com.techelevator.tenmo.services.UserService;
import com.techelevator.view.ConsoleService;

public class App {
	private DecimalFormat decimalFormat = new DecimalFormat("##.00");
	private static final String API_BASE_URL = "http://localhost:8080/";

	private static final String MENU_OPTION_EXIT = "Exit";
	private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
	private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
	private static final String[] LOGIN_MENU_OPTIONS = { LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN,
			MENU_OPTION_EXIT };
	private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
	private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
	private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
	private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS,
			MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS,
			MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };

	private AuthenticatedUser currentUser;
	private ConsoleService console;
	private AuthenticationService authenticationService;
	private UserService userService;
	private Scanner userScanner = new Scanner(System.in);

	public static void main(String[] args) {
		App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL),
				new UserService(API_BASE_URL));
		app.run();
	}

	public App(ConsoleService console, AuthenticationService authenticationService, UserService userService) {
		this.console = console;
		this.authenticationService = authenticationService;
		this.userService = userService;
	}

	public void run() {
		System.out.println("*********************");
		System.out.println("* Welcome to TEnmo! *");
		System.out.println("*********************");

		registerAndLogin();
		mainMenu();
	}

	private void mainMenu() {
		while (true) {
			String choice = (String) console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			if (MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
				viewCurrentBalance();
			} else if (MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
				viewTransferHistory();
			} else if (MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
				viewPendingRequests();
			} else if (MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
				sendBucks();
			} else if (MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
				requestBucks();
			} else if (MAIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else {
				// the only other option on the main menu is to exit
				exitProgram();
			}
		}
	}

	private void viewCurrentBalance() {
		double balance = userService.getCurrentBalance(currentUser);
		System.out.println("Current Balance: " + balance);
	}

	private void viewTransferHistory() {
		Transfer[] transferList = userService.getTransfers(currentUser);
		System.out.println("---------------------------------------------");
		System.out.println("Transfers");
		System.out.println(String.format("%-8s %-30s %-8s", "ID", "From --> To", "Amount"));
		System.out.println("---------------------------------------------");
		for (Transfer transfer : transferList) {
			System.out.println(String.format("%-8d %-30s %-8s", transfer.getTransferId(),
					transfer.getFromUserName() + " --> " + transfer.getToUserName(),
					"$" + decimalFormat.format(transfer.getAmount())));
		}
		System.out.println("Please enter transfer ID to view details (0 to cancel): ");
		boolean valid = false;
		int response = 0;
		try {
			response = Integer.parseInt(userScanner.nextLine());
			if (response == 0) {
				throw new NumberFormatException();
			}
			for (Transfer t : transferList) {
				if (t.getTransferId() == response) {
					valid = true;
				}
			}
			if (!valid) {
				throw new Exception();
			}

		} catch (NumberFormatException e) {
			valid = false;
			System.out.println("Operation cancelled.");
			return;
		} catch (Exception e) {
			valid = false;
			System.out.println("Please enter valid ID.");
			return;
		}
		System.out.println("---------------------------------------------");
		System.out.println("Transfer Details");
		System.out.println("---------------------------------------------");
		for (Transfer t : transferList) {
			if (t.getTransferId() == response) {
				System.out.println("ID: " + t.getTransferId());
				System.out.println("From: " + t.getFromUserName());
				System.out.println("To: " + t.getToUserName());
				System.out.println("Type: " + t.getTransferTypeName());
				System.out.println("Status: " + t.getTransferStatusName());
				System.out.println("Amount: " + "$" + decimalFormat.format(t.getAmount()));
			}

		}

	}

	private void viewPendingRequests() {
		Transfer[] transferList = userService.getPendingRequests(currentUser);
		System.out.println("---------------------------------------------");
		System.out.println("Pending Transfers");
		System.out.println(String.format("%-8s %-30s %-8s", "ID", "To", "Amount"));
		System.out.println("---------------------------------------------");
		for (Transfer transfer : transferList) {
			System.out.println(String.format("%-8d %-30s %-8s", transfer.getTransferId(), transfer.getToUserName(),
					"$" + decimalFormat.format(transfer.getAmount())));
		}
		System.out.println("Please enter transfer ID to Approve/Deny (0 to cancel): ");
		boolean valid = false;
		Transfer chosenTransfer = new Transfer();
		int response = 0;
		try {
			response = Integer.parseInt(userScanner.nextLine());
			if (response == 0) {
				throw new NumberFormatException();
			}
			for (Transfer t : transferList) {
				if (t.getTransferId() == response) {
					valid = true;
					chosenTransfer = t;
				}
			}
			if (!valid) {
				throw new Exception();
			}

		} catch (NumberFormatException e) {
			valid = false;
			System.out.println("Operation cancelled.");
			return;
		} catch (Exception e) {
			valid = false;
			System.out.println("Please enter valid ID.");
			return;
		}

		System.out.println("---------------------------------------------");
		System.out.println(String.format("%-8s %-30s %-8s", chosenTransfer.getTransferId(),
				chosenTransfer.getToUserName(), chosenTransfer.getAmount()));
		System.out.println("---------------------------------------------");
		System.out.println("1) Approve");
		System.out.println("2) Deny");
		System.out.println("0) Neither");
		System.out.print("Please choose an option >>");
		int approveResponse;
		try {
			approveResponse = Integer.parseInt(userScanner.nextLine());
		} catch (Exception e) {
			System.out.println("Invalid Response.");
			return;
		}
		if (approveResponse == 1) {
			chosenTransfer.setTransferStatus(2);
			chosenTransfer.setToken(currentUser.getToken());
			userService.updateTransferRequest(chosenTransfer);
		} else if (approveResponse == 2) {
			chosenTransfer.setTransferStatus(3);
			chosenTransfer.setToken(currentUser.getToken());
			userService.updateTransferRequest(chosenTransfer);
		} else {
			System.out.println("Invalid response.");
		}

	}

	private void sendBucks() {
		System.out.println("Please select a user to send to:");
		String username = this.getChosenUsername();

		Transfer transfer = new Transfer();

		boolean isValid = false;
		double amount = 0;
		while (!isValid) {
			System.out.println("How much money do you want to send?");
			try {
				amount = Double.parseDouble(userScanner.nextLine());

				if (userService.getCurrentBalance(currentUser) < amount) {
					throw new InsufficientFundsException();
				}
				isValid = true;
			} catch (InsufficientFundsException ex) {
				System.out.println("Insufficient Funds.");
				isValid = false;
			} catch (Exception e) {
				System.out.println("Not a valid amount.");
				isValid = false;
			}
		}

		// Setting the transfer properties
		transfer.setToUserName(username);
		transfer.setAmount(amount);
		transfer.setFromUserName(currentUser.getUser().getUsername());
		transfer.setToken(currentUser.getToken());

		userService.sendMoney(transfer);

	}

	private void requestBucks() {
		System.out.println("Please select a user to request from:");
		Transfer transfer = new Transfer();
		String username = this.getChosenUsername();
		System.out.println("How much money would you like to request?");
		double amount;
		try {
			amount = Double.parseDouble(userScanner.nextLine());
		} catch (Exception e) {
			System.out.println("Invalid response.");
			return;
		}
		transfer.setAmount(amount);
		transfer.setFromUserName(username);
		transfer.setToUserName((currentUser.getUser().getUsername()));
		transfer.setTransferType(1);
		transfer.setTransferStatus(1);
		transfer.setToken(currentUser.getToken());
		userService.requestTransfer(transfer);

	}

	private void exitProgram() {
		System.out.println("Goodbye!");
		System.exit(0);
	}

	private void registerAndLogin() {
		while (!isAuthenticated()) {
			String choice = (String) console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
			if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
				register();
			} else {
				// the only other option on the login menu is to exit
				exitProgram();
			}
		}
	}

	private boolean isAuthenticated() {
		return currentUser != null;
	}

	private void register() {
		System.out.println("Please register a new user account");
		boolean isRegistered = false;
		while (!isRegistered) // will keep looping until user is registered
		{
			UserCredentials credentials = collectUserCredentials();
			try {
				authenticationService.register(credentials);
				isRegistered = true;
				System.out.println("Registration successful. You can now login.");
			} catch (AuthenticationServiceException e) {
				System.out.println("REGISTRATION ERROR: " + e.getMessage());
				System.out.println("Please attempt to register again.");
			}
		}
	}

	private void login() {
		System.out.println("Please log in");
		currentUser = null;
		while (currentUser == null) // will keep looping until user is logged in
		{
			UserCredentials credentials = collectUserCredentials();
			try {
				currentUser = authenticationService.login(credentials);
			} catch (AuthenticationServiceException e) {
				System.out.println("LOGIN ERROR: " + e.getMessage());
				System.out.println("Please attempt to login again.");
			}
		}
	}

	private UserCredentials collectUserCredentials() {
		String username = console.getUserInput("Username");
		String password = console.getUserInput("Password");
		return new UserCredentials(username, password);
	}

	public String getChosenUsername() {
		boolean valid = false;
		String username = "";
		int response = 0;
		while (!valid) {
			// Getting User List
			User[] userList = userService.listUsers();
			for (int i = 0; i < userList.length; i++) {
				if (!(userList[i].getUsername().equals(currentUser.getUser().getUsername()))) {
					System.out.println(userList[i].getId() + ") " + userList[i].getUsername());
				}
			}

			try {
				response = Integer.parseInt(userScanner.nextLine());

				for (User user : userList) {
					if (user.getId() == response) {
						valid = true;
						username = user.getUsername();
					}
					if (currentUser.getUser().getId() == response) {
						valid = false;
					}
				}
				if (!valid) {
					throw new Exception();
				}

			} catch (Exception e) {
				System.out.println("Invalid Response.");
				valid = false;
			}
		}
		return username;
	}

}
