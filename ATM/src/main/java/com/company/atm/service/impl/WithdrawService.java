package com.company.atm.service.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.company.atm.ProcessException;
import com.company.atm.model.Bill;
import com.company.atm.service.BankingService;
import com.company.atm.utility.Utility;

public class WithdrawService implements BankingService {

	@Override
	public String operationCode() {
		return getServiceMapping("withdraw");
	}

	@Override
	public void process(Map<String, Integer> process, Map<String, Bill> orignalBill) throws ProcessException {
		int amount = process.values().stream().reduce(Integer::sum).get();
		validateDeposit(amount, orignalBill);
		Map<String, Bill> dispensedBills = new HashMap<>();
		String[] notes = Utility.getResources().getProperty("bills").split(",");
		for (String item : notes) {
			int curr = Integer.valueOf(item);
			// System.out.println("Amount : " + amount);
			Bill billPresent = orignalBill.get(item);
			int currAmtAvailable = billPresent.getCurrencyValue() * billPresent.getCurrencyCount();

			// System.out.println("currAmtAvailable : " + currAmtAvailable + ",amount :" +
			// amount + ",curr :" + curr);
			int currNeeded = amount / curr;
			Bill bill = new Bill();
			bill.setCurrencyValue(curr);
			if (currNeeded > billPresent.getCurrencyCount()) {
				amount = amount - currAmtAvailable;
				bill.setCurrencyCount(billPresent.getCurrencyCount());
			} else {
				bill.setCurrencyCount(currNeeded);
				amount = amount % curr;
			}
			bill.setCurrencyTotal(bill.getCurrencyValue() * bill.getCurrencyCount());
			dispensedBills.put(item, bill);

		}

		if (amount == 0) {
			StringBuilder sb = new StringBuilder("Dispensed: ");
			List<Bill> dispensedBillList = dispensedBills.values().stream().collect(Collectors.toList());
			Collections.sort(dispensedBillList, Comparator.comparing(Bill::getCurrencyValue).reversed());

			dispensedBillList.stream().forEach(item -> {
				if (item.getCurrencyCount() > 0) {
					sb.append(item.getCurrencyValue() + "s=").append(item.getCurrencyCount() + ",");
					Bill deductBill = orignalBill.values().stream()
							.filter(orgBill -> orgBill.getCurrencyValue() == item.getCurrencyValue()).findFirst().get();
					deductBill.setCurrencyCount(deductBill.getCurrencyCount() - item.getCurrencyCount());
					deductBill.setCurrencyTotal(deductBill.getCurrencyValue() * deductBill.getCurrencyCount());
				}
			});

			StringBuilder balance = new StringBuilder("Balance: ");
			List<Bill> values = orignalBill.values().stream().collect(Collectors.toList());
			AtomicInteger total = new AtomicInteger();

			Collections.sort(values, Comparator.comparing(Bill::getCurrencyValue).reversed());

			values.forEach(item -> {
				balance.append(item.getCurrencyValue() + "s=").append(item.getCurrencyCount() + ",");
				total.addAndGet(item.getCurrencyTotal());
			});

			System.out.println(sb.toString());
			balance.append("Total=" + total);
			System.out.println(balance.toString());
		} else {
			System.out.println("Requested withdraw amount is not dispensable");
		}

	}

	void validateDeposit(int amount, Map<String, Bill> orignalBill) throws ProcessException {
		if (amount <= 0 || orignalBill.values().stream().map(item -> item.getCurrencyTotal())
				.collect(Collectors.toList()).stream().reduce(Integer::sum).get() < amount) {
			throw new ProcessException("Incorrect or insufficient funds");
		}
	}

}
