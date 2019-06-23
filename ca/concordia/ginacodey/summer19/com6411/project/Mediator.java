package ca.concordia.ginacodey.summer19.com6411.project;

public interface Mediator {
	public void putLoanRequest(Request loanRequest);
	public void respondToLoanRequest(Response loanResponse);
	public void unregisterBank(String bankName);
	public void registerBank(Bank bank);
}
