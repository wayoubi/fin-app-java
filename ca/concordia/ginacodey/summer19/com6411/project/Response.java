package ca.concordia.ginacodey.summer19.com6411.project;

public class Response {
	
	private String bankName;
	private int approvedAmount;
	private String customerName;
	
	private int requiredAmount;
	
	
	public Response(String bankName, int approvedAmount, String customerName, int requiredAmount) {
		this.setBankName(bankName);
		this.setApprovedAmount(approvedAmount);
		this.setRequiredAmount(requiredAmount);
		this.setCustomerName(customerName);
		
	}
	
	public boolean isPartial() {
		return (this.getApprovedAmount() == this.getRequiredAmount() ? false : true);
	}
	
	public String getBankName() {
		return bankName;
	}

	public void setBankName(String customerName) {
		this.bankName = customerName;
	}

	public int getApprovedAmount() {
		return approvedAmount;
	}

	public void setApprovedAmount(int approvedAmount) {
		this.approvedAmount = approvedAmount;
	}
	
	public int getRequiredAmount() {
		return requiredAmount;
	}

	private void setRequiredAmount(int requiredAmount) {
		this.requiredAmount = requiredAmount;
	}
	
	public String getCustomerName() {
		return customerName;
	}

	private void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
}
