import java.io.Serializable;

public class Case implements Serializable {
	private static final long serialVersionUID = 1L;

	private Treatment treatment;
	private InsuranceCompanies insurance;
	private Bill bill;
	private double balance;
	private Patient patient;

	public Case(Treatment t, InsuranceCompanies i, Patient p) {
		this.patient = p;
		this.treatment = t;
		this.insurance = i;
		this.bill = new Bill(this.insurance, this.treatment, this.patient);
		this.balance = this.bill.patientsTotalBill();
	}
	public Case(Case c) {
		this.patient = c.patient;
		this.treatment = c.treatment;
		this.insurance = c.insurance;
		this.bill = c.bill;
		this.balance = c.balance;
	}

	public double howMuchWasPaidAlreadyOutOfPocket() {
		return bill.getTotalTreatmentPrice() - balance;
	}

	public Bill getDeepCopyOfTheBill() {
		return new Bill(this.bill);

	}
	public Treatment getTreatment() {
		return treatment;
	}

	public void setTreatment(Treatment treatment) {
		this.treatment = treatment;
	}

	public InsuranceCompanies getInsurance() {
		return insurance;
	}

	public void setInsurance(InsuranceCompanies insurance) {
		this.insurance = insurance;
	}

	public double getBalance() {
		return balance;
	}
	public double getCopay() {
		return bill.getCoPay();
	}
	public void payBill(double amount) {
		this.balance -= amount;
	}
}