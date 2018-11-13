package chapter14;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 模式匹配
 * 
 * @author Loops
 *
 */
public class PatternMatching {

	public static void main(String[] args) {
		Expr e = new BinOp("+", new Number(5), new Number(0));
		Expr match = simplify(e);
		System.out.println(match);// 打印输出5
	}

	/* 使用模式匹配简化表达式 */
	private static Expr simplify(Expr e) {
		/* 处理BinOp表达式 */
		TriFunction<String, Expr, Expr, Expr> binopcase = (opname, left, right) -> {
			/* 处理加法 */
			if ("+".equals(opname)) {
				if (left instanceof Number && ((Number) left).val == 0) {
					return right;
				}
				if (right instanceof Number && ((Number) right).val == 0) {
					return left;
				}
			}
			/* 处理乘法 */
			if ("*".equals(opname)) {
				if (left instanceof Number && ((Number) left).val == 1) {
					return right;
				}
				if (right instanceof Number && ((Number) right).val == 1) {
					return left;
				}
			}
			return new BinOp(opname, left, right);
		};
		/* 处理Number对象 */
		Function<Integer, Expr> numcase = val -> new Number(val);
		/* 如果用户提供的Expr无法识别时进行的默认处理机制 */
		Supplier<Expr> defaultcase = () -> new Number(0);
		/* 进行模式匹配 */
		return patternMatchExpr(e, binopcase, numcase, defaultcase);
	}

	public static class Expr {

	}

	public static class Number extends Expr {
		int val;

		public Number(int val) {
			this.val = val;
		}

		@Override
		public String toString() {
			return "" + val;
		}
	}

	public static class BinOp extends Expr {
		String opname;
		Expr left, right;

		public BinOp(String opname, Expr left, Expr right) {
			this.opname = opname;
			this.left = left;
			this.right = right;
		}

		@Override
		public String toString() {
			return "(" + left + " " + opname + " " + right + ")";
		}
	}

	public static <T> T myIf(boolean b, Supplier<T> truecase, Supplier<T> falsecase) {
		return b ? truecase.get() : falsecase.get();
	}

	public static interface TriFunction<S, T, U, R> {
		R apply(S s, T t, U u);
	}

	public static <T> T patternMatchExpr(Expr e, TriFunction<String, Expr, Expr, T> binopcase,
			Function<Integer, T> numcase, Supplier<T> defaultcase) {
		return (e instanceof BinOp) ? binopcase.apply(((BinOp) e).opname, ((BinOp) e).left, ((BinOp) e).right)
				: (e instanceof Number) ? numcase.apply(((Number) e).val) : defaultcase.get();
	}
}