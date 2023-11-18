package com.xienng.rpc.server.business;

import com.xienng.rpc.contract.IDemoService;

/**
 * Hello world!
 *
 */
public class DemoServiceImpl implements  IDemoService
{

	public int sum(int a, int b) {
		return a+b;
	}
    
}
