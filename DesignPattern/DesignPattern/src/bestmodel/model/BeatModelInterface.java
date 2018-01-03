package bestmodel.model;

import bestmodel.view.BPMObserver;
import bestmodel.view.BeatObserver;

/**
 * 模型接口
 * 
 * @author 杨弢
 * 
 */
public interface BeatModelInterface {
	void initialize();

	void on();

	void off();

	void setBPM(int bpm);

	int getBPM();

	void registerObserver(BeatObserver o);

	void removeObserver(BeatObserver o);

	void registerObserver(BPMObserver o);

	void removeObserver(BPMObserver o);
}
