package ca.concordia.ginacodey.summer19.com6411.project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Money implements Mediator, Runnable {
	
	private HashMap<String, Bank> banks;
	private HashMap<String, Customer> customers;
	private BlockingQueue<Request> requesstsQueue;
	private BlockingQueue<Response> responsesQueue;
	private ThreadGroup customersGroup;
	private ThreadGroup banksGroup;

	public Money() {
		this.setBanks(new HashMap<String, Bank>());
		this.setCustomers(new HashMap<String, Customer>());
		this.setRequesstsQueue(new LinkedBlockingQueue<Request>());
		this.setResponesQueue(new LinkedBlockingQueue<Response>());
		this.setCustomersGroup(new ThreadGroup("Customers"));
		this.setBanksGroup(new ThreadGroup("Banks"));
	}
	
	public static void main(String[] args) {
		
		Money money = new Money();
		Thread thread = new Thread(money);
		thread.start();
		
		System.out.println("** Customers and loan objectives **");	
		Money.parseCustomers("Customers.txt").stream().forEach(customer -> 
			{
				System.out.println(customer.getName() + ":" + customer.getRequiredAmount() + ".");
				money.getCustomers().put(customer.getName(), customer); 
				customer.setMedaitor(money);
				new Thread(money.getCustomersGroup(), customer, customer.getName()).start();
			} 
		);
		
		System.out.println("** Banks and financial resources **");
		Money.parseBnks("Banks.txt").stream().forEach(bank -> 
			{
				System.out.println(bank.getName() + ":" + bank.getAvailableFunds() + ".");
				money.getBanks().put(bank.getName(), bank); 
				bank.setMedaitor(money);
				new Thread(money.getBanksGroup(), bank, bank.getName()).start();
			} 
		);
		
		System.out.println("\"Starting ....\"");
	}
	
	public void run() {
		boolean exit = false;
		while(!exit) {
			try {
				Thread.sleep(3000);
				if(this.getBanksGroup().activeCount() == 0 && this.getRequesstsQueue().size() == 0 && this.getResponsesQueue().size() == 0) {
					exit = true;
					this.getCustomers().values().stream().forEach(customer -> 
						{ 
							if(customer.terminate() != 0 && customer.getGrantedAmount() != customer.getRequiredAmount()) {
								System.out.println(customer.getName() + " was only able to borrow "+customer.getGrantedAmount()+" dollar(s). Boo Hoo!");
							}
						}
					);
				}
				if(this.getCustomersGroup().activeCount() == 0 && this.getRequesstsQueue().size() == 0 && this.getResponsesQueue().size() == 0) {
					exit = true;
					this.getBanks().values().stream().forEach(bank -> 
						{
							if(bank.terminate() != 0) {
								System.out.println(bank.getName() + " has "+bank.getAvailableFunds()+" dollar(s) remaining.");
							}
						}
					);
				}
				Request request = this.getRequesstsQueue().poll();
				if(request != null) {
					this.processLoanRequesst(request);
				}
				Response response = this.getResponsesQueue().poll();
				if(response != null) {
					this.informCustomer(response);
				}
			} catch (InterruptedException exception) {
				
			}
		}
	}
	
	
	public static List<Customer> parseCustomers(String path) {
		List<Customer> customers = new ArrayList<Customer>();
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			String line = br.readLine();
			while(line != null) {
				String name = line.substring(line.indexOf("{")+1, line.indexOf(",")).trim();
				String amount = line.substring(line.indexOf(",")+1, line.indexOf("}")).trim();
				customers.add(new Customer(name,Integer.parseInt(amount)));
				line = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return customers;
	}
	
	public static List<Bank> parseBnks(String path) {
		List<Bank> banks = new ArrayList<Bank>();
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			String line = br.readLine();
			while(line != null) {
				String name = line.substring(line.indexOf("{")+1, line.indexOf(",")).trim();
				String amount = line.substring(line.indexOf(",")+1, line.indexOf("}")).trim();
				banks.add(new Bank(name,Integer.parseInt(amount)));
				line = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return banks;
	}
	
	private void informCustomer(Response response) {
		try {	
			this.getCustomers().get(response.getCustomerName()).call(response);
			if(response.isPartial()) {
				System.out.println(response.getBankName() + " denies a loan of "+ response.getRequiredAmount() +" dollar(s) from " + response.getCustomerName());
			}
			System.out.println(response.getBankName() + " approves a loan of "+ response.getApprovedAmount() +" dollar(s) from " + response.getCustomerName());
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	
	private void processLoanRequesst(Request request) {
		if(request.getAmount() == 0) {
			System.out.println(request.getCustomerName() + " has reached the objective of "+ request.getOriginalAmount() +" dollar(s) . Woo Hoo! ");
			return;
		}
		if(this.getBanks().size()==0) {
			return;
		}
		Random random = new Random();
		Object[] values = this.getBanks().values().toArray();
		Bank bank = (Bank) values[random.nextInt(values.length)];
		try {
			bank.getPocessingQueue().put(request);
			System.out.println(request.getCustomerName() + " requests a loan of "+ request.getAmount()+" dollar(s) " + bank.getName());
		} catch (InterruptedException interruptedException) {
			interruptedException.printStackTrace();
		}
	}
	
	@Override
	public void respondToLoanRequest(Response response) {
		try {
			this.getResponsesQueue().put(response);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	
	@Override
	public void putLoanRequest(Request request) {
		try {
			this.getRequesstsQueue().put(request);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	
	@Override
	public void unregisterBank(String bankName) {
		this.getBanks().remove(bankName);
		System.out.println(bankName + " has 0 dollar(s) remaining.");
	}
	
	@Override
	public void registerBank(Bank bank) {
		this.getBanks().put(bank.getName(), bank);
	}
	
	public HashMap<String, Bank> getBanks() {
		return banks;
	}
	
	private void setBanks(HashMap<String, Bank> banks) {
		this.banks = banks;
	}
	
	private HashMap<String, Customer> getCustomers() {
		return customers;
	}

	private void setCustomers(HashMap<String, Customer> customers) {
		this.customers = customers;
	}
	
	private BlockingQueue<Request> getRequesstsQueue() {
		return this.requesstsQueue;
	}

	private void setRequesstsQueue(BlockingQueue<Request> loanRequesstsQueue) {
		this.requesstsQueue = loanRequesstsQueue;
	}
	
	private BlockingQueue<Response> getResponsesQueue() {
		return this.responsesQueue;
	}

	private void setResponesQueue(BlockingQueue<Response> responsesQueue) {
		this.responsesQueue = responsesQueue;
	}
	
	private ThreadGroup getCustomersGroup() {
		return customersGroup;
	}

	private void setCustomersGroup(ThreadGroup customersGroup) {
		this.customersGroup = customersGroup;
	}

	private ThreadGroup getBanksGroup() {
		return banksGroup;
	}

	private void setBanksGroup(ThreadGroup banksGroup) {
		this.banksGroup = banksGroup;
	}
}