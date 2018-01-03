package bestmodel.adapter;

import bestmodel.view.BPMObserver;
import bestmodel.view.BeatObserver;

/**
 * 心脏模型接口
 * 
 * @author 杨弢
 * 
 */
public interface HeartModelInterface {
	int getHeartRate();

	void registerObserver(BeatObserver o);

	void removeObserver(BeatObserver o);

	void registerObserver(BPMObserver o);

	void removeObserver(BPMObserver o);
}
