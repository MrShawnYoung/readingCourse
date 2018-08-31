package chapter2;

/**
 * VM Args: -Xss2M(这时候不妨设置大些)
 * 创建线程导致内存溢出异常
 */
public class JavaVMStackOOM {

	private void dontStop() {
		while (true) {

		}
	}

	public void stackLeakByThread() {
		while (true) {
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					dontStop();
				}
			});
			thread.start();
		}
	}

	public static void main(String[] args) {
		JavaVMStackOOM oom = new JavaVMStackOOM();
		oom.stackLeakByThread();
	}
}