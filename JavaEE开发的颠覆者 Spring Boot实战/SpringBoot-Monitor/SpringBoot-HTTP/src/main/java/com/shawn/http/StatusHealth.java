package com.shawn.http;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
/* 实现接口重写health()方法 */
public class StatusHealth implements HealthIndicator {
	@Autowired
	StatusService statusService;

	@Override
	public Health health() {
		String status = statusService.getStatus();
		if (status == null || !status.equals("running")) {
			/* 当status的值为非running时构造失败 */
			return Health.down().withDetail("Error", "Not Running").build();
		}
		/* 其余成功 */
		return Health.up().build();
	}
}