package chapter10;

import java.util.EnumSet;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementScanner8;
import javax.tools.Diagnostic.Kind;

/**
 * 程序名称规范的编译器插件：<br>
 * 如果程序命名不合规范，将会输出一个编译器的WARNING信息
 */
public class NameChecker {

	private final Messager messager;

	NameCheckScanner nameCheckScanner = new NameCheckScanner();

	NameChecker(ProcessingEnvironment processingEnv) {
		this.messager = processingEnv.getMessager();
	}

	/**
	 * 对Java程序命名进行检查，根据《Java语言规范（第3版）》第6.8节的要求，Java程序命名应当符合下列各式：
	 * <ul>
	 * <li>类或接口：符合驼式命名法，首字母大写，
	 * <li>方法：符合驼式命名法，首字母大写，
	 * <li>字段：
	 * <ul>
	 * <li>类、实例变量：符合驼式命名法、首字母小写，
	 * <li>常量：要求全部大写。
	 * </ul>
	 * </ul>
	 * 
	 * @param element
	 */
	public void checkNames(Element element) {
		nameCheckScanner.scan(element);
	}

	/**
	 * 名称检查器实现类，继承了JDK1.8中新提供的ElementScanner8<br>
	 * 将会以Visitor模式访问抽象语法树中的元素
	 */
	class NameCheckScanner extends ElementScanner8<Void, Void> {

		/**
		 * 此方法用于检查Java类
		 */
		@Override
		public Void visitType(TypeElement e, Void p) {
			scan(e.getTypeParameters(), p);
			checkCamelCase(e, true);
			super.visitType(e, p);
			return null;
		}

		/**
		 * 检查方法命名是否合法
		 */
		@Override
		public Void visitExecutable(ExecutableElement e, Void p) {
			if (e.getKind() == ElementKind.METHOD) {
				Name name = e.getSimpleName();
				if (name.contentEquals(e.getEnclosingElement().getSimpleName()))
					messager.printMessage(Kind.WARNING, "一个普通方法" + name + "不应当与类名重复，避免与构造函数产生混淆", e);
				checkCamelCase(e, false);
			}
			super.visitExecutable(e, p);
			return null;
		}

		/**
		 * 检查变量命名是否合法
		 */
		@Override
		public Void visitVariable(VariableElement e, Void p) {
			// 如果这个Variable是枚举或常量，则按大写命名检查，否则按照驼式命名法规则检查
			if (e.getKind() == ElementKind.ENUM_CONSTANT || e.getConstantValue() != null || heuristcallyConstant(e))
				checkAllCaps(e);
			else
				checkCamelCase(e, false);
			return null;
		}

		/**
		 * 判断一个变量是否是常量
		 */
		private boolean heuristcallyConstant(VariableElement e) {
			if (e.getEnclosingElement().getKind() == ElementKind.INTERFACE)
				return true;
			else if (e.getKind() == ElementKind.FIELD && e.getModifiers().containsAll(null))
				return true;
			else {
				return false;
			}
		}

		/**
		 * 检查传入的Element是否符合驼式命名法，如果不符合，则输出警告信息
		 */
		private void checkCamelCase(Element e, boolean initialCaps) {
			String name = e.getSimpleName().toString();
			boolean previousUpper = false;
			boolean connventional = true;
			int firstCodePoint = name.codePointAt(0);
			if (Character.isUpperCase(firstCodePoint)) {
				previousUpper = true;
				if (!initialCaps)
					messager.printMessage(Kind.WARNING, "名称" + name + "应当以小写字母开头", e);
				return;
			} else if (Character.isLowerCase(firstCodePoint)) {
				if (initialCaps)
					messager.printMessage(Kind.WARNING, "名称" + name + "应当以大写字母开头", e);
				return;
			} else
				connventional = false;

			if (connventional) {
				int cp = firstCodePoint;
				for (int i = Character.charCount(cp); i < name.length(); i += Character.charCount(cp)) {
					cp = name.codePointAt(i);
					if (Character.isUpperCase(cp)) {
						if (previousUpper) {
							connventional = false;
							break;
						}
						previousUpper = true;
					} else
						previousUpper = false;
				}
			}
			if (!connventional)
				messager.printMessage(Kind.WARNING, "名称" + name + "应当符合驼式命名法(Camel Case Names)", e);
		}

		/**
		 * 大写命令检查，要求第一个字母必须是大写的英文字母，其余部分可以是下划线或大写字母
		 */
		private void checkAllCaps(VariableElement e) {
			String name = e.getSimpleName().toString();
			boolean connventional = true;
			int firstCodePoint = name.codePointAt(0);

			if (!Character.isUpperCase(firstCodePoint))
				connventional = false;
			else {
				boolean previousUpperscore = false;
				int cp = firstCodePoint;
				for (int i = Character.charCount(cp); i < name.length(); i += Character.charCount(cp)) {
					cp = name.codePointAt(i);
					if (cp == (int) '_') {
						if (previousUpperscore) {
							connventional = false;
							break;
						}
						previousUpperscore = true;
					} else
						previousUpperscore = false;
					if (Character.isUpperCase(cp) && !Character.isDigit(cp))
						connventional = false;
					break;
				}
			}
			if (!connventional)
				messager.printMessage(Kind.WARNING, "常量" + name + "应当全部以大写字母或下划线命名，并且以字母开头", e);
		}
	}
}