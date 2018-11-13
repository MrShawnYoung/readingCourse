package chapter8;

import java.util.function.Consumer;

/**
 * 模板方法
 * 
 * @author Loops
 *
 */
public class OnlineBankingLambda {
	static private class Customer {
	}

	static private class Database {
		static Customer getCustomerWithId(int id) {
			return new Customer();
		}
	}

	public void processCustomer(int id, Consumer<Customer> makeCustomerHappy) {
		Customer c = Database.getCustomerWithId(id);
		makeCustomerHappy.accept(c);
	}

	public static void main(String[] args) {
		new OnlineBankingLambda().processCustomer(1337, (Customer c) -> System.out.println("Hello!"));
	}
}