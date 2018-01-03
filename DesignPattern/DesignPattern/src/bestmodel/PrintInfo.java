package bestmodel;

import bestmodel.adapter.HeartController;
import bestmodel.adapter.impl.HeartModel;
import bestmodel.controller.ControllerInterface;
import bestmodel.controller.impl.BeatController;
import bestmodel.model.BeatModelInterface;
import bestmodel.model.impl.BeatModel;

public class PrintInfo {

	public static void main(String[] args) {
		// BeatModelInterface model = new BeatModel();
		// ControllerInterface controller = new BeatController(model);

		HeartModel heartModel = new HeartModel();
		ControllerInterface model = new HeartController(heartModel);
	}
}