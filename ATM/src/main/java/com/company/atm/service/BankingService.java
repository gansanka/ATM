package com.company.atm.service;

import java.util.Map;
import java.util.stream.Collectors;

import com.company.atm.ProcessException;
import com.company.atm.model.Bill;
import com.company.atm.utility.Utility;

public interface BankingService {

	void process(Map<String, Integer> process, Map<String, Bill> orignalBill) throws ProcessException;

	public String operationCode();

	default String getServiceMapping(String serviceType) {
		String serviceName = Utility.getResources().stringPropertyNames().stream()
				.filter(item -> item.contains("services")).collect(Collectors.toList()).stream()
				.filter(item -> item.contains(serviceType)).findFirst().get();
		return Utility.getResources().getProperty(serviceName);

	}

}
