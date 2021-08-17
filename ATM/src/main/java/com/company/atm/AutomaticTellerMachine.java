package com.company.atm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.company.atm.model.Bill;
import com.company.atm.service.BankingService;
import com.company.atm.service.impl.DepositService;
import com.company.atm.service.impl.WithdrawService;
import com.company.atm.utility.Utility;

public class AutomaticTellerMachine {

	static List<BankingService> services = List.of((new DepositService()), (new WithdrawService()));

	static Map<String, Bill> bills = new HashMap<>();

	public static void main(String[] args) {

		initializeBills();
		try (Scanner scanner = new Scanner(System.in)) {
			while (true) {
				System.out.println("1. Deposit 2. Withdraw");
				int process = scanner.nextInt();
				switch (process) {
				case 1:
					String[] notes = Utility.getResources().getProperty("bills").split(",");
					Map<String, Integer> deposit = new HashMap<>();
					Arrays.stream(notes).forEach(item -> {
						System.out.println("Enter the denomination of " + item);
						deposit.put(item, scanner.nextInt());
					});
					try {
						services.stream().filter(item -> String.valueOf(process).equals(item.operationCode()))
								.findFirst().get().process(deposit, bills);
					} catch (ProcessException pe) {
						System.out.println(pe.getLocalizedMessage());
					}
					break;
				case 2:
					System.out.println("Enter the amount to be withdrawn");
					Map<String, Integer> withdraw = new HashMap<>();
					int amount = scanner.nextInt();
					withdraw.put("amount", amount);
					try {
						services.stream().filter(item -> String.valueOf(process).equals(item.operationCode()))
								.findFirst().get().process(withdraw, bills);
					} catch (ProcessException pe) {
						System.out.println(pe.getLocalizedMessage());
					}
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	static void initializeBills() {
		String[] notes = Utility.getResources().getProperty("bills").split(",");
		Arrays.stream(notes).forEach(item -> {
			Bill bill = new Bill();
			bill.setCurrencyValue(Integer.parseInt(item));
			bills.put(item, bill);
		});
	}
}
