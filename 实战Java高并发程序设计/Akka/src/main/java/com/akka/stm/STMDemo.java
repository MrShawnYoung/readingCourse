package com.akka.stm;

import static akka.pattern.Patterns.ask;
import java.util.concurrent.TimeUnit;

import scala.concurrent.Await;

import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.transactor.Coordinated;
import akka.util.Timeout;

/**
 * 软件事务内存
 * 
 * @author 杨弢
 * 
 */
public class STMDemo {
	public static ActorRef company = null;
	public static ActorRef employee = null;

	public static void main(String[] args) throws Exception {
		final ActorSystem system = ActorSystem.create("transactionDemo", ConfigFactory.load("samplehello.conf"));
		company = system.actorOf(Props.create(CompanyActor.class), "company");
		employee = system.actorOf(Props.create(EmployeeActor.class), "employee");

		Timeout timeout = new Timeout(1, TimeUnit.SECONDS);
		// 尝试19次汇款，每次递增
		for (int i = 1; i < 20; i++) {
			// 新建一个协调者，并且发送给company
			company.tell(new Coordinated(i, timeout), ActorRef.noSender());
			Thread.sleep(200);
			// 询问当前余额
			Integer companyCount = (Integer) Await.result(ask(company, "GetCount", timeout), timeout.duration());
			Integer employeeCount = (Integer) Await.result(ask(employee, "GetCount", timeout), timeout.duration());
			// 输出
			System.out.println("company count=" + companyCount);
			System.out.println("employee count=" + employeeCount);
			System.out.println("=================");
		}
	}
}