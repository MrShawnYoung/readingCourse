package compound.observer;

/**
 * 观察者通知接口
 * 
 * @author 杨弢
 * 
 */
public interface Observer {
	public void update(QuackObservable duck);
}