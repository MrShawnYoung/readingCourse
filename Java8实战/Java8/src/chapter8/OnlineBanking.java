package chapter8;

/**
 * 模板方法
 * 
 * @author Loops
 *
 */
public abstract class OnlineBanking {
	static private class Customer {
	}

	static private class Database {
		static Customer getCustomerWithId(int id) {
			return new Customer();
		}
	}

	public void processCustomer(int id) {
		Customer c = Database.getCustomerWithId(id);
		makeCustomerHappy(c);
	}

	abstract void makeCustomerHappy(Customer c);
}