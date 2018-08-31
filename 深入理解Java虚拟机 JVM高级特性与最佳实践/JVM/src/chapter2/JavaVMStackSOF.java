package chapter2;

/**
 * VM Args: -Xss128k
 * 虚拟机栈和本地方法OOM测试
 */
public class JavaVMStackSOF {

	private int stackLength = 1;

	public void stackLeak() {
		stackLength++;
		stackLeak();
	}

	public static void main(String[] args) {
		JavaVMStackSOF oom = new JavaVMStackSOF();
		try {
			oom.stackLeak();
		} catch (Throwable e) {
			System.out.println("stack legnth:" + oom.stackLength);
		}
	}
}