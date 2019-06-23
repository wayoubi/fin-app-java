package ca.concordia.ginacodey.summer19.com6411.project;

import java.util.Random;

public class Customer implements Runnable, Processable {

	public static final int SLEEP_PERIOD = 3000;
	public static final int MAX_LOAN_AMOUNT = 50;

	private String name;
	private int requiredAmount;
	private int grantedAmount;
    private Mediator mediator;
    private int numberOfRequests = 0;
    private boolean stop;


	public Customer(String name, int requiredAmount) {
		this.setName(name);
		this.setRequiredAmount(requiredAmount);
		this.setGrantedAmount(0);
		this.setStop(false);
	}
	
	public void run() {
		while(!this.isStop()) {
			try {
				Thread.sleep(SLEEP_PERIOD);
				if(numberOfRequests>0) {
					continue;
				}
				Random random = new Random();	
				int pendingAmount = this.getRequiredAmount() - this.getGrantedAmount();
				Request request = new Request(this.getName(), (pendingAmount < MAX_LOAN_AMOUNT)? pendingAmount : random.nextInt(MAX_LOAN_AMOUNT)+1, this.getRequiredAmount());
				this.getMediator().putLoanRequest(request);
				numberOfRequests++;
				if(pendingAmount == 0)
					break;
			} catch (InterruptedException interruptedException) {
				interruptedException.printStackTrace();
			}
		}
	}
	
	public  void call(Response loanResponse) {
		numberOfRequests--;
		int pendingAmount = this.getRequiredAmount() - this.getGrantedAmount();
		if(pendingAmount > 0 && this.getGrantedAmount() + loanResponse.getApprovedAmount() <= this.getRequiredAmount()) {
			this.setGrantedAmount(this.getGrantedAmount() + loanResponse.getApprovedAmount());
		}		
	}
	
	public int terminate() {
		this.setStop(true);
		return this.getGrantedAmount();
	}
	
	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getRequiredAmount() {
		return requiredAmount;
	}

	public void setRequiredAmount(int requiredAmount) {
		this.requiredAmount = requiredAmount;
	}

	public int getGrantedAmount() {
		return grantedAmount;
	}

	public void setGrantedAmount(int grantedAmount) {
		this.grantedAmount = grantedAmount;
	}

	@Override
	public void setMedaitor(Mediator mediator) {
		this.mediator = mediator;		
	}
	
	@Override
	public Mediator getMediator() {
		return mediator;
	}
	
	public boolean isStop() {
		return stop;
	}

	private void setStop(boolean stop) {
		this.stop = stop;
	}
}
