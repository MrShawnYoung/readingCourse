package chapter13;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 函数式编程实战
 * 
 * @author Loops
 *
 */
public class SubsetsMain {

	public static void main(String[] args) {
		List<List<Integer>> subs = subsets(Arrays.asList(1, 4, 9));
		subs.forEach(System.out::println);
	}

	public static List<List<Integer>> subsets(List<Integer> list) {
		/* 如果输入为空，它就只包含一个子集，既空列表自身 */
		if (list.isEmpty()) {
			List<List<Integer>> ans = new ArrayList<>();
			ans.add(Collections.emptyList());
			return ans;
		}
		Integer first = list.get(0);
		List<Integer> rest = list.subList(1, list.size());
		/* 否则就取出一个元素first，找出剩余部分的所有子集，并将其赋予subans。subans构成了结果的另外一半 */
		List<List<Integer>> subans = subsets(rest);
		/* 答案的另一半是subans2 ，它包含了subans中的所有列表，但是经过调整，在每个列表的第一个元素之前添加了first */
		List<List<Integer>> subans2 = insertAll(first, subans);
		/* 将两个子答案整合在一起就完成了任务，简单吗？ */
		return concat(subans, subans2);
	}

	public static List<List<Integer>> insertAll(Integer first, List<List<Integer>> lists) {
		List<List<Integer>> result = new ArrayList<>();
		for (List<Integer> l : lists) {
			/* 复制列表，从而使你有机会对其进行添加操作。即使底层是可变的，你也不应该复制底层的结构（不过Integer底层是不可变的） */
			List<Integer> copyList = new ArrayList<>();
			copyList.add(first);
			copyList.addAll(l);
			result.add(copyList);
		}
		return result;
	}

	public static List<List<Integer>> concat(List<List<Integer>> a, List<List<Integer>> b) {
		List<List<Integer>> r = new ArrayList<>(a);
		r.addAll(b);
		return r;
	}
}