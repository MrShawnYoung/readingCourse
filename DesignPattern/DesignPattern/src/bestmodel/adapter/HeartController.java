package bestmodel.adapter;

import bestmodel.adapter.impl.HeartAdapter;
import bestmodel.controller.ControllerInterface;
import bestmodel.view.impl.DJView;

/**
 * 心脏控制器
 * 
 * @author 杨弢
 * 
 */
public class HeartController implements ControllerInterface {
	HeartModelInterface model;
	DJView view;

	public HeartController(HeartModelInterface model) {
		this.model = model;
		view = new DJView(this, new HeartAdapter(model));
		view.createView();
		view.createControls();
		view.disableStopMenuItem();
		view.disableStartMenuItem();
	}

	@Override
	public void start() {

	}

	@Override
	public void stop() {

	}

	@Override
	public void increaseBPM() {

	}

	@Override
	public void decreaseBPM() {

	}

	@Override
	public void setBPM(int bpm) {

	}
}