import java.io.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import java.time.DateTimeException;

public class Main {
	static Scanner input = new Scanner(System.in);
	//objects needed for the music
	static AudioInputStream audioInputStream;
	static Clip clip;

	// arraylist that will hold the patients
	static ArrayList < Patient > patients = loadPatientsFromFile();

	public static void main(String[] args) {
		try {

			music();

			int response, amountOfUnsucessfulTries = 0;
			do {
				System.out.print("For patient: enter 1 \nInsurance rep: enter 2 ");
				response = input.nextInt();
				amountOfUnsucessfulTries++;

				// clear the buffer
				input.nextLine();

			} while (response != 1 && response != 2 && amountOfUnsucessfulTries != 5);
			if (amountOfUnsucessfulTries == 5) {
				System.out.println("Too many attempts. Please try again later. ");
				System.exit(1);
			}

			// if its a patient, continue with the code for the patient to access the portal
			if (response == 1) {

				int amountOfTimesEnteredInvalidPatient = 0;

				while (true) {

					// setting the current patient to null, it will be set to an actual patient
					// object later on
					Patient currPatient = null;

					// set the answer to a, will be changed when the user inputs an answer
					char answer = 'a';

					// loop for input validation
					while ((answer != 'Y') && (answer != 'N') && (answer != 'E')) {
						System.out.println("Are you already in our System? enter yes/no, or press e to exit");
						// do we need to clear the buffer?
						answer = input.nextLine().toUpperCase().charAt(0);
					}

					// if they are already in the system
					if (answer == 'Y') {
						int id = getPatientId();

						// search them based on their patient id and set them to the current patient

						// loop thru the patient arraylist and find the patient that matches the patient
						// id they entered
						for (int i = 0; i < patients.size(); i++) {
							if (id == patients.get(i).getPatientId()) {
								currPatient = patients.get(i);
							}
						}

						// if they aren't in the system. tell them and restart the whole loop
						if (currPatient == null) {
							System.out.println("You are not in our system");
							amountOfTimesEnteredInvalidPatient++;

							// if they entered an invalid patient too many times don't let them try again
							if (amountOfTimesEnteredInvalidPatient < 5) {
								System.out.println("Error! Please try again.");
								continue;
							} else {

								System.out.println("You entered an invalid patient too many times");
								System.exit(1);

							}
						}

						for (int timesTriedEnteringPassword = 0; timesTriedEnteringPassword <= 5; timesTriedEnteringPassword++) {
							if (currPatient.passwordsMatch(getPassword(id))) {
								System.out.println("Correct password");
								break;
							} else {
								if (timesTriedEnteringPassword == 5) {
									System.out.println("You failed too many times. exiting program");
									System.exit(1);
								} else {
									System.out.println("Incorrect. try again");
								}
							}
						}

						// print out the name that correspond with this patient

						System.out.println("Hello " + currPatient.getName() + " patient ID: " +
								currPatient.getPatientId() + "\nWelcome to the patient portal!");

						// if they are not in the system yet
					} else if (answer == 'N') {
						// Patient info

						// set the currpatient to a new patient
						currPatient = new Patient(getName(), getDOB(), getSSNumber(), setPassword());

						// if another patient shares a social with this patient, exit the program
						for (int i = 0; i < patients.size(); i++) {
							if (patients.get(i).getSSNumber().equals(currPatient.getSSNumber())) {
								System.out.println(
										"another patient already has this ss number.\nexiting program for security reasons.\ncontact the help desk");
								System.exit(1);
							}
						}
						currPatient.setAddress(getAddress());
						currPatient.setPhoneNumber(getPhoneNumber());
						setInsuranceOnFile(currPatient);
						patients.add(currPatient);

						// we have a new patient in the list, so make sure our updated patient list is
						// saved to the file
						saveUpdatedInfoToFile();

						// tell them thier patient id
						System.out.println("Your patient id is " + currPatient.getPatientId());

						// if they chose to exit
					} else if (answer == 'E') {
						System.exit(0);
					}

					// go to the main menu to do want the patient wants to do
					executeMainMenu(currPatient);

				}
			}

			// if they are not a patient, but an insurance rep
			else {
				int repID;
				// only allow us to see all patients
				int cggID = 23, yoID = 34, akID = 65, rbID = 45;
				do {
					System.out.print("Please enter your insurance ID: ");
					repID = input.nextInt();
				} while (repID != 23 && repID != 34 && repID != 65 && repID != 45);
				System.out.print("WELCOME ");
				if (repID == 23) {
					System.out.print("CHAYA GITTY ");
				} else if (repID == 34) {
					System.out.print("YOCHEVED ");
				} else if (repID == 65) {
					System.out.print("AVIGAIL ");
				} else {
					System.out.print("RIVKA ");
				}
				System.out.print("TO THE INSURANCE PORTAL\n");
				int inputId;
				Patient currPatientRep = null;
				do {
					System.out.println("Please enter the patient's ID you would like to view: ");
					inputId = input.nextInt();
				} while (!hasPatient(inputId));
				for (int i = 0; i < patients.size(); i++) {
					if (inputId == patients.get(i).getPatientId()) {
						currPatientRep = patients.get(i);
					}
				}
				// clear buffer
				input.nextLine();
				executeMainMenu(currPatientRep);
			}
		} catch (RuntimeException e) {
			// clearing buffer
			input.nextLine();
			System.err.println("UH OH SOMETHING WENT WRONG. RESTARTING PROGRAM\n");
			clip.close();
			main(args);

		}
	}

	public static String setPassword() {
		System.out.println("Enter a password");
		String password = input.nextLine();
		System.out.println("Enter again to confirm");
		if (password.equals(input.nextLine())) {
			System.out.println("Passwords match. Success");

			return password;
		} else {
			System.out.println("Passwords dont match. try again");
			return setPassword();
		}
	}

	public static String getPassword(int patientId) {
		System.out.println("Enter your passoword. Just press enter if you forgot it");
		String userInput = input.nextLine();
		if (userInput.equals("")) {
			recoverPassword(patientId);
			System.out.println("Enter your password.");
			userInput = input.nextLine();
		}
		return userInput;

	}

	public static void recoverPassword(int patientId) {
		int numberOfTries = 5;
		for (int i = 0; i < numberOfTries; i++) {
			String SSNumber = getSSNumber();
			SSNumber = SSNumber.substring(0, 3) + "-" + SSNumber.substring(3, 5) + "-" + SSNumber.substring(5);

			for (int j = 0; j < patients.size(); j++) {
				if (patients.get(j).getPatientId() == patientId) {
					if (patients.get(j).getSSNumber().equals(SSNumber)) {
						patients.get(j).setPassword(setPassword());
						saveUpdatedInfoToFile();

						return;
					} else {
						System.out.println("Social Security Number doesnt match patient id. try again");
					}
				}

			}
		}
		System.out.println("You reached the maximum amount of tries. exiting program.");
		System.exit(1);
	}

	public static void recoverId() {
		int numberOfTries = 5;
		for (int i = 0; i < numberOfTries; i++) {
			String SSNumber = getSSNumber();
			SSNumber = SSNumber.substring(0, 3) + "-" + SSNumber.substring(3, 5) + "-" + SSNumber.substring(5);

			for (int j = 0; j < patients.size(); j++) {
				if (SSNumber.equals(patients.get(j).getSSNumber())) {
					System.out.println("Your patient id is " + patients.get(j).getPatientId());
					return;
				}
			}
			System.out.println("Your patient id wasnt found. try again");
		}
		System.out.println("You reached the maximum amount of tries. exiting program.");
		System.exit(1);
	}

	public static void executeMainMenu(Patient currPatient) {
		// now that we have a patient that we are working with, lets see what to do
		while (true) {

			// user chooses a choice from the menu
			int choice = menu();

			switch (choice) {
				case 1:
					displayPatientInfo(currPatient);
					break;

				// if they chose 2, they want to edit their info
				case 2:
					editPatientInfo(currPatient);
					break;

				case 3:
					// display what they owe
					displayWhatIsOwed(currPatient);
					break;
				case 4:
					// if they chose 4, they are adding a new case to their file
					addCase(currPatient);
					break;

				case 5:
					payBill(currPatient);
					break;
				case 6:
					System.out.println("Thank you for using our app! Have an amazing day");
					System.exit(0);
			}
		}

	}

	public static int getPatientId() {

		System.out.println("Enter your patient id\nIf you forgot your patient id, enter forgot");
		String typedInId = input.nextLine();

		// if they typed in a string started with f it means they want to recover
		if (typedInId.toLowerCase().charAt(0) == 'f') {

			// recover their id
			recoverId();

			// have them enter their patient id now that they recovered it
			System.out.println("\nEnter your patient id");
			typedInId = input.nextLine();
		}

		return Integer.parseInt(typedInId);
	}

	public static void addCase(Patient currPatient) {

		// figure out the insurance
		InsuranceCompanies insuranceUsing;

		// if there's an insurance on file, give the option to use it
		if (currPatient.getInsuranceOnFile() != null) {

			String choice;
			do {
				System.out.println("1. Use Insurance on file\n2. Use a different Insurance");
				choice = input.nextLine();
			} while (!choice.equals("1") && !choice.equals("2"));

			// if using insurance on file
			if (choice.equals("1")) {
				insuranceUsing = currPatient.getInsuranceOnFile();
				// if not using insurance on file
			} else {
				insuranceUsing = getInsurance();

			}

			// if there's no insurance on file, ask what insurance they want to use
		} else {
			insuranceUsing = getInsurance();
		}

		// calling method to get the treatment type
		Treatment treatment = getTreatmentName();

		// make a new case out of this information
		Case c = new Case(treatment, insuranceUsing, currPatient);

		// tell them what the copay is
		System.out.println("For this case, you owe " + String.format("$%.2f", c.getBalance()) + " as a copay");
		// adding a new case to this patient
		currPatient.addCase(c);
		saveUpdatedInfoToFile();
	}

	public static void payBill(Patient currPatient) {

		// if balance is zero there is no need to pay any bill
		if (currPatient.getTotalBalance() <= .001) {
			System.out.println("There is no balance on the account :)");
			return;
		}

		System.out.println("How much would you like to pay? ");
		double amountPaying = input.nextDouble();
		while (amountPaying <= 0 || (amountPaying > currPatient.getTotalBalance())) {
			System.out.println("Enter a valid amount:");
			amountPaying = input.nextDouble();
		}
		// pay bill now

		// clear the buffer
		input.nextLine();
		currPatient.payBill(amountPaying);
		saveUpdatedInfoToFile();

	}

	public static void setInsuranceOnFile(Patient currPatient) {
		System.out.println("Would you like to keep the insurance on file? (y/n)");
		char ans = input.nextLine().toLowerCase().charAt(0);
		if (ans == 'y') {
			currPatient.setInsuranceOnFile(getInsurance());

		}
	}

	/**
	 * this method basically overwrites everything in the file with the current
	 * state of the patients arraylist, essentially "saving" any changes to any
	 * patients
	 */
	public static void saveUpdatedInfoToFile() {

		try (ObjectOutputStream objectOut = new ObjectOutputStream(new FileOutputStream("PatientInfo.txt"));) {

			for (int i = 0; i < patients.size(); i++) {
				objectOut.writeObject(patients.get(i));
			}
		} catch (Exception e) {}

	}

	/**
	 * this method edits the patient information
	 *
	 * @param p the current patient that we are editing the info of
	 */
	public static void editPatientInfo(Patient p) {
		String entry;
		do {
			System.out.println("Enter (1-3)\n1 for Address \n2 for Phone number\n3 to change insurance on file");

			entry = input.nextLine();

		} while (!entry.equals("1") && !entry.equals("2") && !entry.equals("3"));

		int choice = Integer.parseInt(entry);

		switch (choice) {
			case 1:
				p.setAddress(getAddress());
				saveUpdatedInfoToFile();
				break;

			case 2:
				p.setPhoneNumber(getPhoneNumber());
				saveUpdatedInfoToFile();
				break;
			case 3:
				p.setInsuranceOnFile(getInsurance());
				saveUpdatedInfoToFile();
				break;

		}

	}

	/**
	 * displays the personal info of the patient
	 *
	 * @param p the patient that we are displaying the info of
	 */
	public static void displayPatientInfo(Patient p) {
		System.out.println("Name: " + p.getName() + "\nSocial Security Number: " + p.getSSNumber() + "\nDOB: " +
				p.getDateOfBirth() + "\nAge: " + p.getAge() + "\nPhone Number: " + p.getPhoneNumber() + "\nAddress: " +
				p.getAddress() + "\nInsurance: " +
				(p.getInsuranceOnFile() == null ? "unknown" : p.getInsuranceOnFile().toString()));
	}

	/**
	 * prints out the main menu and gets the user to input a selection
	 *
	 * @param input keyboard object for user input
	 * @return user's selection
	 */
	public static int menu() {
		String ans;
		do {
			System.out.println("\nMain Menu: \n1. View Patients Information\n" + "2. Edit Patients Information\n" +
					"3. View Balance\n" + "4. Add a new charge\n5. Pay Bill\n" + "6. Exit");
			ans = input.nextLine();
		} while (!ans.equals("1") && !ans.equals("2") && !ans.equals("3") && !ans.equals("4") && !ans.equals("5") &&
				!ans.equals("6"));

		return Integer.parseInt(ans);
	}

	/**
	 * this method reads in patients from the file and returns them in an arraylist
	 *
	 * @return the arraylist of patients
	 */
	public static ArrayList < Patient > loadPatientsFromFile() {

		ArrayList < Patient > p = new ArrayList < > ();

		// read from the file and put all the patients into the arraylist
		try (ObjectInputStream objectIn = new ObjectInputStream(new FileInputStream("PatientInfo.txt"));) {

			while (true) {
				p.add((Patient) objectIn.readObject());
				// for every patient we are reading, we need to increment the counter that gives
				// out patient ids becuase we dont want duplicate ids
				Patient.incrementPatientId();
			}
		} catch (Exception e) {
			// an excpetion will be thrown when there are no more objects left to read, and
			// when that happens ...

			// put the objects back into the emptied out file
			try (ObjectOutputStream objectOut = new ObjectOutputStream(new FileOutputStream("PatientInfo.txt"));) {

				// all the patients were read from the file and removed from the file when it
				// was read and so they're no longer in it,
				// so put them back in
				for (int i = 0; i < p.size(); i++) {
					objectOut.writeObject(p.get(i));
				}
			} catch (Exception ex) {

			}

			// FINALLY, RETURN THE ARRAYLIST
			return p;
		}

	}

	/**
	 * gets a name from the user
	 *
	 * @param input scanner object
	 * @return the name they typed in
	 */
	public static String getName() {

		System.out.println("Please enter your full name:");
		String name = input.nextLine();
		name = name.substring(0, 1).toUpperCase() + name.substring(1);
		return name;

	}

	/**
	 * gets a phone number
	 *
	 * @param input scanner object
	 * @return the phone number in a string
	 */

	public static String getPhoneNumber() {
		System.out.println("What is your Phone number? (only enter the numbers without spaces)");
		while (!input.hasNext("\\d{10}")) {
			System.out.print("That's not a valid phone number. Enter it again: ");
			input.nextLine();
		}
		String phoneNumber = input.next();
		phoneNumber = phoneNumber.substring(0, 3) + "-" + phoneNumber.substring(3, 6) + "-" + phoneNumber.substring(6);
		// clearing the buffer
		input.nextLine();
		return phoneNumber;

	}

	/**
	 * gets an address that the user inputs
	 */
	public static String getAddress() {
		System.out.println("Enter the first line of your address:");
		String address = input.nextLine();
		return address;
	}

	/**
	 * get the date of birth
	 *
	 * @return the date of birth in a localDate object
	 */
	public static LocalDate getDOB() {
		System.out.println("What is your Date of birth? (MM/dd/yyyy)");
		while (!input.hasNext("([0-9]{2})/([0-9]{2})/([0-9]){4}")) {
			System.out.print("That's not a valid date. Enter the date again: ");
			input.nextLine();
		}

		String dateOfBirth = input.next();

		int month = Integer.parseInt(dateOfBirth.substring(0, 2));
		int date = Integer.parseInt(dateOfBirth.substring(3, 5));
		int year = Integer.parseInt(dateOfBirth.substring(6, 10));

		LocalDate dob;
		try {
			dob = LocalDate.of(year, month, date);

			// if they input an invalid date, call this method again (recursion)
		} catch (DateTimeException e) {
			System.out.println("That date was not valid");
			return getDOB();
		}

		// if the year they inputed is before 1920, or the date is later than now, that
		// cant be so call this method again and return that
		if (dob.compareTo(LocalDate.now()) > 0 || year < 1900) {
			System.out
					.println("The date entered is not valid. Either the year didn't occur yet or can't be that old :)");
			return getDOB();
		}
		// clearing the buffer
		input.nextLine();
		return dob;
	}

	/**
	 * gets the ssn
	 *
	 * @return the ssn in a string
	 */
	public static String getSSNumber() {
		System.out.println("Please enter you SSN: (9 digits)");
		String SSNumber = input.next();
		while (SSNumber.length() != 9) {
			System.out.println("Must be only 9 digits. Enter a VALID SSN number:");
			SSNumber = input.next();
		}

		// clearing the buffer
		input.nextLine();
		return SSNumber;
	}

	/**
	 * displays each of the patients bills, what its for, etc.
	 *
	 * @param p the current patient
	 */
	public static void displayWhatIsOwed(Patient p) {
		double totalOwed = 0;
		ArrayList < Case > cases = p.getDeepCopyOfCases();
		for (int i = 0; i < cases.size(); i++) {
			System.out.printf(
					"For case #%d (%s), the total was $%,.2f, the insurance (%s) paid $%,.2f, %n the copay was $%.2f and the amount that still needs to%n be paid is $%.2f%n%n",
					(i + 1), cases.get(i).getTreatment(), cases.get(i).getDeepCopyOfTheBill().getTotalTreatmentPrice(),
					cases.get(i).getInsurance(), cases.get(i).getDeepCopyOfTheBill().getInsurancePaid(),
					cases.get(i).getDeepCopyOfTheBill().getCoPay(), cases.get(i).getBalance());

			totalOwed += cases.get(i).getBalance();
		}

		System.out.printf("%nThe total amount owed is $%.2f%n", totalOwed);
	}

	public static InsuranceCompanies getInsurance() {
		int numInsurance;
		InsuranceCompanies company = null; // will change in the switch statements
		System.out.println("1) Medicaid\n2) UnitedHealth\n3) EmblemHealth\n4) Cigna\n5) Aetna\n6) Molina\n7) Anthem");
		do {
			System.out.print("Please enter the number of your insurance (1-7): ");
			numInsurance = input.nextInt();
		} while (numInsurance < 0 || numInsurance > 7);
		switch (numInsurance) {
			case 1:
				company = InsuranceCompanies.MEDICAID;
				break;
			case 2:
				company = InsuranceCompanies.UNITEDHEALTHCARE;
				break;
			case 3:
				company = InsuranceCompanies.EMBLEMHEALTH;
				break;
			case 4:
				company = InsuranceCompanies.CIGNA;
				break;
			case 5:
				company = InsuranceCompanies.AETNA;
				break;
			case 6:
				company = InsuranceCompanies.MOLINA;
				break;
			case 7:
				company = InsuranceCompanies.ANTHEM;
				break;
		}

		// clearing the buffer
		input.nextLine();
		return company;

	}

	public static Treatment getTreatmentName() {
		int treatmentNum;
		Treatment treatment = null; // for now
		do {
			System.out.println("Enter the number of your treatment: ");
			System.out.println("1.CT Scan \n2.Echocardiogram \n3.Bypass Surgery \n4.Hip Replacment" +
					"\n5.MRI \n6.Upper Endoscopy \n7.Xray");
			treatmentNum = input.nextInt();
		} while (treatmentNum < 0 || treatmentNum > 7);
		switch (treatmentNum) {
			case 1:
				treatment = Treatment.CT_SCAN;
				break;
			case 2:
				treatment = Treatment.ECHOCARDIOGRAM;
				break;
			case 3:
				treatment = Treatment.HEART_BYPASS_SURGERY;
				break;
			case 4:
				treatment = Treatment.HIP_REPLACEMENT_SURGERY;
				break;
			case 5:
				treatment = Treatment.MRI;
				break;
			case 6:
				treatment = Treatment.UPPER_ENDOSCOPY;
				break;
			case 7:
				treatment = Treatment.XRAY;
				break;
		}

		// clearing the buffer
		input.nextLine();
		return treatment;

	}

	public static boolean hasPatient(int id) {
		for (int i = 0; i < patients.size(); i++) {
			if (id == patients.get(i).getPatientId()) {
				return true;
			}
		}
		return false;
	}

	public static void music() {
		try {

			audioInputStream = AudioSystem.getAudioInputStream(new File("music.wav"));
			clip = AudioSystem.getClip();
			clip.open(audioInputStream);
			clip.start();
			clip.loop(clip.LOOP_CONTINUOUSLY);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}