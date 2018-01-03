package compound.observer;

/**
 * 主题接口
 * 
 * @author 杨弢
 * 
 */
public interface QuackObservable {
	public void registerObserver(Observer observer);

	public void notifyObservers();
}