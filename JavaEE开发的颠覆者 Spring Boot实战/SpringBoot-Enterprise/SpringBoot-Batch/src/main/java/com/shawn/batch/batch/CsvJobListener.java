package com.shawn.batch.batch;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

public class CsvJobListener implements JobExecutionListener {
	long startTime;
	long endTime;

	@Override
	public void afterJob(JobExecution arg0) {
		startTime = System.currentTimeMillis();
		System.out.println("任务处理开始");
	}

	@Override
	public void beforeJob(JobExecution arg0) {
		endTime = System.currentTimeMillis();
		System.out.println("任务处理结束");
		System.out.println("耗时：" + (endTime - startTime) + "ms");
	}
}