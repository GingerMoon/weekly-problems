package class_2023_01_2_week;

import java.util.HashMap;

// 给定一个正整数 x，我们将会写出一个形如 x (op1) x (op2) x (op3) x ... 的表达式
// 其中每个运算符 op1，op2，… 可以是加、减、乘、除之一
// 例如，对于 x = 3，我们可以写出表达式 3 * 3 / 3 + 3 - 3，该式的值为3
// 在写这样的表达式时，我们需要遵守下面的惯例：
// 除运算符（/）返回有理数
// 任何地方都没有括号
// 我们使用通常的操作顺序：乘法和除法发生在加法和减法之前
// 不允许使用一元否定运算符（-）。例如，“x - x” 是一个有效的表达
// 因为它只使用减法，但是 “-x + x” 不是，因为它使用了否定运算符
// 我们希望编写一个能使表达式等于给定的目标值 target 且运算符最少的表达式
// 返回所用运算符的最少数量
// 测试链接 : https://leetcode.cn/problems/least-operators-to-express-number/
public class Code03_LeastOperatorsToExpressNumber {

	public static int leastOpsExpressTarget(int x, int target) {
		return dp(0, target, x, new HashMap<>()) - 1;
	}

	// i : 当前来到了x的i次方
	// target : 认为target只能由x的i次方，或者更高次方来解决，不能使用更低次方！
	// 返回在这样的情况下，target最少能由几个运算符搞定！
	// (3, 1001231) -> 返回值！
	// dp.get(3) -> {1001231 对应的value}
	public static int dp(int i, int target, int x, HashMap<Integer, HashMap<Integer, Integer>> dp) {
		if (dp.containsKey(i) && dp.get(i).containsKey(target)) {
			return dp.get(i).get(target);
		}

		int ans = 0;
		if (target > 0 && i < 39) {
			if (target == 1) {
				ans = cost(i);
			} else {
				// 我们把课上没有讲清楚的地方这里写一下
				// 比如 x = 5, target = 73
				// 首先来到5的0次方，要搞定73
				// 73 % 5 = 3，知道余数是3
				// 这个余数3 需要 / 1，得到3，因为此时的余数只能由若干个5的0次方解决
				// 所以73要么是70 + 3 * 1，解决的
				// 或者73是75 - (5 - 3) * 1，解决的
				// 前者代价是搞定3个5的0次方的代价 + 后续搞定70的代价
				// 后者代价是搞定2个5的0次方的代价 + 后续搞定75的代价
				// 如果选择70 + 3 * 1的路线，3 * 1的代价：3 * 搞定5的0次方的代价
				// 后续是70的代价，继续
				// 此时来到5的1次方，要搞定70
				// 70 % 25 = 20，知道余数是20
				// 这个余数20 需要 / 5，得到4
				// 因为此时的余数只能由若干个5的1次方解决
				// 所以70要么是50 + 4 * 5的1次方，解决的
				// 或者70是75 - (5 - 4) * 5的1次方，解决的
				// 前者代价是搞定4个5的1次方的代价 + 后续搞定50的代价
				// 后者代价是搞定1个5的1次方的代价 + 后续搞定75的代价
				// 如果选择50 + 4 * 5的1次方 的路线，后续是50的代价，继续
				// 此时来到5的2次方，要搞定50
				// 50 % 125 = 50，知道余数是50
				// 这个余数50 需要 / 5的2次方，得到2，
				// 因为此时的余数只能由若干个5的2次方解决
				// 所以50要么是0 + 2 * 5的2次方，解决的
				// 或者50是125 - (5 - 2) * 5的2次方，解决的
				// 我们课上讲的是这个思路
				// 上面的思路怎么方便的实现，就是下面的code
				// 我们来解释一下，大家可以把下面的过程，和上面的分析过程对比一下
				// 会发现等效
				// 比如，i = 0, x = 5, target = 73
				// 表示当前来到5的0次方，x是5(固定的), target是73
				// 73 % 5 = 3，知道余数是3
				// 所以73要么是70 + 3 * 5的0次方，解决的
				// 或者73是75 - (5 - 3) * 5的0次方，解决的
				// 3 * 5的0次方 代价就是 : mod * cost(0)
				// (5 - 3) * 5的0次方 代价就是 : (x - mod) * cost(0)
				// 假设我们选择70 + 3 * 5的0次方的路线
				// 73 / 5 = 14，也就是代码中的div，解决70的后续过程就是
				// i = 1, x = 5, target = 14
				// 表示当前来到5的1次方，x是5(固定的), target是14(其实是原来的70，除过5了)
				// 14 % 5 = 4，我们得到了4，这和上面的过程一样
				// 所以原来的70要么是50 + 4 * 5的1次方，解决的
				// 或者原来的70是75 - (5 - 4) * 5的1次方，解决的
				// 这里变成：
				// 现在的14要么是10(因为除了5，其实代表原来的70) + 4 * 5的1次方
				// 现在的14要么是15(因为除了5，其实代表原来的75) - 1 * 5的1次方
				// 14 / 5 = 2，也就是代码中的div，
				// 14已经是除以5之后的结果了，再除以5得到了2，
				// 其实这个2，代表了原来的50，因为除了两次5
				// 所以，i = 2, x = 5, target = 2
				// 其实代表来到5的2次方，搞定50的代价
				// 所以，i = 2, x = 5, target = 3
				// 其实代表来到5的2次方，搞定75的代价(因为3同样是2次除以5之后的结果)
				// 也就是说，每一步的代价，其实都是算对了的
				// 后续依然如此，但是代码这样处理可以写的非常少
				int div = target / x;
				int mod = target % x;
				int p1 = mod * cost(i) + dp(i + 1, div, x, dp);
				int p2 = (x - mod) * cost(i) + dp(i + 1, div + 1, x, dp);
				ans = Math.min(p1, p2);
			}
		}
		if (!dp.containsKey(i)) {
			dp.put(i, new HashMap<>());
		}
		dp.get(i).put(target, ans);
		return ans;
	}

	// 得到x的i次方这个数字
	// 需要几个运算符，默认前面再加个'+'或'-'
	public static int cost(int i) {
		return i == 0 ? 2 : i;
	}

}
