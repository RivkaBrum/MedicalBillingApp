import java.time.*;
import java.util.ArrayList;
import java.io.Serializable;

public class Patient implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private int patientId;
	private String password;
	private LocalDate dateOfBirth;
	private String SSNumber;
	private String address;
	private String phoneNumber;
	private InsuranceCompanies insuranceOnFile;

	private ArrayList < Case > cases = new ArrayList < Case > ();

	// gonna increment this number for each new patient and set it to their patient
	// id this way each patient has a unique patiney
	private static int patientIdValue = 1000;

	// constructor
	public Patient(String name, LocalDate dob, String SSNumber, String password) {
		if (SSNumber.length() != 9) {
			throw new IllegalArgumentException("Social Security Number Must be 9 digits");
		}
		this.name = name;
		// increment the patient id number trackerientIdValue
		patientIdValue++;
		// set the patient id to be this new incremented number this way
		this.patientId = patientIdValue;
		this.dateOfBirth = dob;
		this.SSNumber = SSNumber;
		this.password = password;
	}
	public void setPassword(String s) {
		this.password = s;
	}
	public boolean passwordsMatch(String s) {
		return s.equals(password);
	}
	public InsuranceCompanies getInsuranceOnFile() {
		return insuranceOnFile;
	}

	public void setInsuranceOnFile(InsuranceCompanies insuranceOnFile) {
		this.insuranceOnFile = insuranceOnFile;
	}

	public static void incrementPatientId() {
		patientIdValue++;

	}

	public int getPatientId() {
		return patientId;
	}

	public String getName() {
		return name;
	}

	public String getPhoneNumber() {
		return this.phoneNumber;
	}

	public void setPhoneNumber(String s) {
		this.phoneNumber = s;
	}

	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	public String getSSNumber() {
		return SSNumber.substring(0, 3) + "-" + SSNumber.substring(3, 5) + "-" + SSNumber.substring(5);
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public boolean isBelow18() {
		LocalDate today = LocalDate.now();
		Period timeInBetween = Period.between(this.dateOfBirth, today);

		return timeInBetween.getYears() < 18;
	}

	public boolean isAbove65() {
		LocalDate today = LocalDate.now();
		Period timeInBetween = Period.between(this.dateOfBirth, today);

		return timeInBetween.getYears() >= 65;
	}

	public int getAge() {
		LocalDate today = LocalDate.now();
		Period age = Period.between(this.dateOfBirth, today);

		return age.getYears();
	}

	public void addCase(Case c) {
		this.cases.add(c);
	}

	// return the requested case
	public Case getCase(int i) {
		// returns deep copy
		return new Case(this.cases.get(i));
	}

	public ArrayList < Case > getDeepCopyOfCases() {
		ArrayList < Case > list = new ArrayList < > ();
		for (int i = 0; i < cases.size(); i++) {
			list.add(new Case(this.cases.get(i)));
		}
		return list;
	}

	public double getTotalBalance() {
		double total = 0;
		for (int i = 0; i < cases.size(); i++) {
			total += cases.get(i).getBalance();
		}
		return total;
	}

	public double getTotalCoPay() {
		double total = 0;
		for (int i = 0; i < cases.size(); i++) {
			total += cases.get(i).getCopay();
		}
		return total;
	}

	public void payBill(double amount) {

		double amountPaying = 0; // amount that is being paid per bill/case
		for (int i = 0; i < cases.size(); i++) {
			if (amount != 0) { // if the amount isn't 0
				if (cases.get(i).getBalance() < amount) {
					//if amount left to pay is greater than this cases bill
					//it will set the amount paying and pay this case and adjust the amount left to pay
					amountPaying = cases.get(i).getBalance();
					cases.get(i).payBill(amountPaying);
					amount -= amountPaying;
				} else {
					amountPaying = amount;
					cases.get(i).payBill(amountPaying);
					amount = 0;
				}
			} else { // if the amount reaches zero than the method ends
				return;
			}
		}
	}
}