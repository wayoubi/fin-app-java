package ca.concordia.ginacodey.summer19.com6411.project;

public class Request {
	
	private String customerName;
	private int amount;
	private int originalAmount;
	
	
	public Request(String customerNaeme, int amount, int originalAmount) {
		this.setCustomerName(customerNaeme);
		this.setAmount(amount);
		this.setOriginalAmount(originalAmount);
	}
	
	public String getCustomerName() {
		return customerName;
	}

	private void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public int getAmount() {
		return amount;
	}

	private void setAmount(int amount) {
		this.amount = amount;
	}
	
	public int getOriginalAmount() {
		return originalAmount;
	}

	private void setOriginalAmount(int originalAmount) {
		this.originalAmount = originalAmount;
	}

}
