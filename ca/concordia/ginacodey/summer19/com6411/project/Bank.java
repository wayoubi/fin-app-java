package ca.concordia.ginacodey.summer19.com6411.project;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Bank implements Runnable, Processable {

	
	private BlockingQueue<Request> pocessingQueue;
	
	private String name;
	private int availableFunds;
	private Mediator mediator;
	private boolean stop;

	public Bank(String name, int availableFunds) {
		this.setName(name);
		this.setAvailableFunds(availableFunds);
		this.setPocessingQueue(new LinkedBlockingQueue<Request>());
		this.setStop(false);
	}
	
	public void run() {
		this.mediator.registerBank(this);
		while(this.getAvailableFunds() > 0 && !this.isStop()) {
			try {
				Request loanRequest = this.getPocessingQueue().take();
				if(loanRequest.getAmount()==0) {
					continue;
				}
				Response loanResponse = this.process(loanRequest);
				this.getMediator().respondToLoanRequest(loanResponse);
			} catch (InterruptedException interruptedException) {
				interruptedException.printStackTrace();
			}
		}
		if(!this.isStop()) {
			this.getMediator().unregisterBank(this.getName());
		}
		
	}
	
	public int terminate() {
		this.setStop(true);
		try {
			this.getPocessingQueue().put(new Request("terminate", 0, 0));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return this.getAvailableFunds();
	}
	
	private Response process(Request loanRequest) {
		Response loanResponse = null;
		if(this.getAvailableFunds()>=loanRequest.getAmount()) {
			this.setAvailableFunds(this.getAvailableFunds() - loanRequest.getAmount());
			loanResponse = new Response(this.getName(), loanRequest.getAmount(), loanRequest.getCustomerName(), loanRequest.getAmount());
		} else {
			loanResponse = new Response(this.getName(), this.getAvailableFunds(), loanRequest.getCustomerName(), loanRequest.getAmount());
			this.setAvailableFunds(0);
		}
		return loanResponse;
	}

	@Override
	public String getName() {
		return name;
	}

	private void setName(String name) {
		this.name = name;
	}

	public int getAvailableFunds() {
		return availableFunds;
	}

	private void setAvailableFunds(int availableFunds) {
		this.availableFunds = availableFunds;
	}
	
	@Override
	public Mediator getMediator() {
		return this.mediator;
	}
	
	@Override
	public void setMedaitor(Mediator mediator) {
		this.mediator = mediator;		
	}
	
	public BlockingQueue<Request> getPocessingQueue() {
		return pocessingQueue;
	}

	private void setPocessingQueue(BlockingQueue<Request> pocessingQueue) {
		this.pocessingQueue = pocessingQueue;
	}
	
	private boolean isStop() {
		return stop;
	}

	private void setStop(boolean stop) {
		this.stop = stop;
	}

}
