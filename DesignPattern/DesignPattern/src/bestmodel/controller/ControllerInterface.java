package bestmodel.controller;

/**
 * 控制器接口
 * 
 * @author 杨弢
 * 
 */
public interface ControllerInterface {
	public void start();

	public void stop();

	public void increaseBPM();

	public void decreaseBPM();

	public void setBPM(int bpm);
}