package commands
import org.crsh.cli.Command
import org.crsh.cli.Usage
import org.crsh.command.InvocationContext
class hello {
	@Usage("Say Hello")	//该命令用途
	@Command	//注解当前是一个CRaSH命令
	def main(InvocationContext context) {

		def bootVersion = context.attributes['spring.boot.version'];	//获取Spring Boot版本
		def springVersion = context.attributes['spring.version']	//获取Spring版本

		return "Hello,your Spring Boot version is "+bootVersion +",your Spring Framework version is "+springVersion

	}
}