package com.xienng.rpc.client;

import com.xienng.rpc.contract.IDemoService;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {

		JDKDynamicService<IDemoService> proxy = new JDKDynamicService<IDemoService>();
		proxy.setClass(IDemoService.class);
		IDemoService service = proxy.get();
		System.out.println("result" + service.sum(1, 2));
	}
}
