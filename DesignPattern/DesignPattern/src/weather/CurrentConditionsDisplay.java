package weather;

import java.util.Observable;
import java.util.Observer;

/**
 * 最新状态类
 * 
 * @author 杨弢
 * 
 */
public class CurrentConditionsDisplay implements Observer, DisplayElement {
	Observable observable;
	private float temperature;
	private float humidity;

	public CurrentConditionsDisplay(Observable observable) {
		this.observable = observable;
		observable.addObserver(this);
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof WeatherData) {
			WeatherData weatherData = (WeatherData) o;
			this.temperature = weatherData.getTemperature();
			this.humidity = weatherData.getHumidity();
			display();
		}
	}

	public void display() {
		System.out.println("Current conditions:" + temperature
				+ "F degrees and " + humidity + "% humidity");
	}
}