package aop;

public class TestBean {
	private String str = "testStr";

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}

	public void test() {
		System.out.println("test");
	}
}
