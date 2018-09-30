package concurrentmode.matrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.RecursiveTask;
import org.jmatrices.dbl.Matrix;
import org.jmatrices.dbl.operator.MatrixOperator;

/**
 * 矩阵
 * 
 * @author 杨弢
 * 
 */
public class MatrixMulTask extends RecursiveTask<Matrix> {
	Matrix m1;
	Matrix m2;
	String pos;

	public MatrixMulTask(Matrix m1, Matrix m2, String pos) {
		this.m1 = m1;
		this.m2 = m2;
		this.pos = pos;
	}

	@Override
	protected Matrix compute() {
		// System.out.println(Thread.currentThread().getId() + ":" +
		// Thread.currentThread().getName() + "is start");
		if (m1.rows() <= PMatrixMul.granularity || m2.cols() <= PMatrixMul.granularity) {
			Matrix mRe = MatrixOperator.multiply(m1, m2);
			return mRe;
		} else {
			// 如果不是，那么继续分割矩阵
			int rows;
			rows = m1.rows();
			// 左乘的矩阵横向分割
			Matrix m11 = m1.getSubMatrix(1, 1, rows / 2, m1.cols());
			Matrix m12 = m1.getSubMatrix(rows / 2 + 1, 1, m1.rows(), m1.cols());
			// 右乘矩阵纵向分割
			Matrix m21 = m2.getSubMatrix(1, 1, m2.rows(), m2.cols() / 2);
			Matrix m22 = m2.getSubMatrix(1, m2.cols() / 2 + 1, m2.rows(), m2.cols());
			ArrayList<MatrixMulTask> subTasks = new ArrayList<MatrixMulTask>();
			MatrixMulTask tmp = null;
			tmp = new MatrixMulTask(m11, m21, "m1");
			subTasks.add(tmp);
			tmp = new MatrixMulTask(m11, m22, "m2");
			subTasks.add(tmp);
			tmp = new MatrixMulTask(m12, m21, "m3");
			subTasks.add(tmp);
			tmp = new MatrixMulTask(m12, m22, "m4");
			subTasks.add(tmp);
			for (MatrixMulTask t : subTasks) {
				t.fork();
			}
			Map<String, Matrix> matrixMap = new HashMap<String, Matrix>();
			for (MatrixMulTask t : subTasks) {
				matrixMap.put(t.pos, t.join());
			}
			Matrix tmp1 = MatrixOperator.horizontalConcatenation(matrixMap.get("m1"), matrixMap.get("m2"));
			Matrix tmp2 = MatrixOperator.horizontalConcatenation(matrixMap.get("m3"), matrixMap.get("m4"));
			Matrix reM = MatrixOperator.verticalConcatenation(tmp1, tmp2);
			return reM;
		}
	}
}
