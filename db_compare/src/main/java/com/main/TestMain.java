package com.main;

import com.core.Processor;

public class TestMain {
	public static void main(String[] args)throws Exception {
		Processor.process("src", null);
		System.out.println("done");
	}
}
