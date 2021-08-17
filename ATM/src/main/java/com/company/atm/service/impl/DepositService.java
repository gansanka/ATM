package com.company.atm.service.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.company.atm.ProcessException;
import com.company.atm.model.Bill;
import com.company.atm.service.BankingService;

public class DepositService implements BankingService {

	@Override
	public String operationCode() {
		return getServiceMapping("deposit");
	}

	@Override
	public void process(Map<String, Integer> process, Map<String, Bill> orignalBill) throws ProcessException {
		validateDeposit(process);
		process.entrySet().stream().forEach(item -> {
			Bill bill = orignalBill.get(item.getKey());
			bill.setCurrencyCount(bill.getCurrencyCount() + item.getValue());
			bill.setCurrencyTotal(bill.getCurrencyValue() * bill.getCurrencyCount());
		});

		StringBuilder sb = new StringBuilder("Balance: ");
		AtomicInteger total = new AtomicInteger();

		List<Bill> values = orignalBill.values().stream().collect(Collectors.toList());

		Collections.sort(values, Comparator.comparing(Bill::getCurrencyValue).reversed());
		values.forEach(item -> {
			sb.append(item.getCurrencyValue() + "s=").append(item.getCurrencyCount() + ",");
			total.addAndGet(item.getCurrencyTotal());
		});
		sb.append("Total=" + total);
		System.out.println(sb.toString());
	}

	void validateDeposit(Map<String, Integer> process) throws ProcessException {
		if (process.values().stream().filter(item -> item == 0).collect(Collectors.toList()).size() == process.size()) {
			throw new ProcessException("Deposit amount cannot be zero");
		}
		if (process.values().stream().filter(item -> item < 0).collect(Collectors.toList()).size() > 0) {
			throw new ProcessException("Incorrect deposit amount");
		}
	}

}
