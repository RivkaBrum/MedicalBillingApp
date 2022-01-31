import java.io.Serializable;

public class Bill implements Serializable {

	private static final long serialVersionUID = 1L;

	private double coPay; // what the patient has to pay
	private double insurancePaid; // what insurance covers
	private Treatment treatmentType;
	private InsuranceCompanies insuranceType;
	private double totalTreatmentPrice;
	private Patient patient;

	public Bill(InsuranceCompanies insurance, Treatment treatment, Patient p) {
		this.patient = p;
		this.insuranceType = insurance;
		this.treatmentType = treatment;
		this.totalTreatmentPrice = treatmentPrices();
		this.coPay = patientsTotalBill();
		this.insurancePaid = totalTreatmentPrice - coPay;

	}

	//copy construcotr
	public Bill(Bill b) {
		this.coPay = b.coPay;
		this.insurancePaid = b.insurancePaid;
		this.treatmentType = b.treatmentType;
		this.insuranceType = b.insuranceType;
		this.totalTreatmentPrice = b.totalTreatmentPrice;
		this.patient = b.patient;
	}

	public double patientsTotalBill() {
		double price = 0;
		treatmentPrices(); // making sure the treatment price is set
		if (patient.isBelow18() || patient.isAbove65()) { //switched to OR
			if (insuranceType == insuranceType.MEDICAID || insuranceType == insuranceType.UNITEDHEALTHCARE) {
				price = 0.00; // Medicaid plans, they cover in full no matter what treatment
			} else if (insuranceType == insuranceType.AETNA || insuranceType == insuranceType.MOLINA) {
				// they cover 90 percent of the service
				price = this.totalTreatmentPrice * .1;
			} else if (insuranceType == insuranceType.ANTHEM) {
				// Worst insurance company ever!!!!!
				// making them cover only 20 percent
				price = this.totalTreatmentPrice * .8;
			} else {
				// CIGNA and EMBLEMHEALTH only cover 60 percent of the bill
				price = this.totalTreatmentPrice * .6;
			}
			return price;
		} else {
			if (insuranceType == insuranceType.MEDICAID || insuranceType == insuranceType.UNITEDHEALTHCARE) {
				price = 0.00; // Medicaid plans, they cover in full no matter what treatment
			} else if (insuranceType == insuranceType.AETNA || insuranceType == insuranceType.MOLINA) {
				// they cover 80 percent of the service
				price = this.totalTreatmentPrice * .2;
			} else if (insuranceType == insuranceType.ANTHEM) {
				// Worst insurance company ever!!!!!
				// making them cover only 10 percent
				price = this.totalTreatmentPrice * .9;
			} else {
				// CIGNA and EMBLEMHEALTH only cover 50 percent of the bill
				price = this.totalTreatmentPrice * .5;
			}
			return price;
		}

	}

	public double treatmentPrices() {
		if (treatmentType == treatmentType.CT_SCAN) {
			this.totalTreatmentPrice = 550.00;
		} else if (treatmentType == treatmentType.ECHOCARDIOGRAM) {
			this.totalTreatmentPrice = 2500.00;
		} else if (treatmentType == treatmentType.HEART_BYPASS_SURGERY) {
			this.totalTreatmentPrice = 50200.00;
		} else if (treatmentType == treatmentType.HIP_REPLACEMENT_SURGERY) {
			this.totalTreatmentPrice = 23203.50;
		} else if (treatmentType == treatmentType.MRI) {
			this.totalTreatmentPrice = 2611.00;
		} else if (treatmentType == treatmentType.UPPER_ENDOSCOPY) {
			this.totalTreatmentPrice = 1150;
		} else {
			this.totalTreatmentPrice = 400;
		}
		return this.totalTreatmentPrice;
	}

	public double getCoPay() {
		return coPay;
	}

	public void setCoPay(int coPay) {
		this.coPay = coPay;
	}

	public double getInsurancePaid() {
		return insurancePaid;
	}

	public void setInsurancePaid(double insurancePaid) {
		this.insurancePaid = insurancePaid;
	}

	public double getTotalTreatmentPrice() {
		return this.totalTreatmentPrice;
	}

}