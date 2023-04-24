package class_2023_04_4_week;

// 你有一个凸的 n 边形，其每个顶点都有一个整数值。给定一个整数数组 values ，
// 其中 values[i] 是第 i 个顶点的值（即 顺时针顺序 ）。
// 假设将多边形 剖分 为 n - 2 个三角形。
// 对于每个三角形，该三角形的值是顶点标记的乘积，
// 三角剖分的分数是进行三角剖分后所有 n - 2 个三角形的值之和。
// 返回 多边形进行三角剖分后可以得到的最低分 。
// 测试链接 : https://leetcode.cn/problems/minimum-score-triangulation-of-polygon/
public class Code04_MinScoreTriangulationPolygon {

	public static int minScoreTriangulation(int[] values) {
		int[][] dp = new int[values.length][values.length];
		for (int i = 0; i < values.length; i++) {
			for (int j = 0; j < values.length; j++) {
				dp[i][j] = -1;
			}
		}
		return f(values, 0, values.length - 1, dp);
	}

	// values[i...j]范围上这些点，要分解成多个三角形
	// 三角形一个端点是values[i]，另一个端点是values[j]
	// 那么第三个点在i+1....j-1之间选
	// 比如选了m点 : i......m.......j
	// 当前获得的分数为values[i] * values[m] * values[j]
	// 接下来，i.....m去分解三角形、m.....j去分解三角形
	public static int f(int[] values, int i, int j, int[][] dp) {
		if (i >= j - 1) {
			// 不够三个点，不会有得分
			return 0;
		}
		if (dp[i][j] != -1) {
			// 缓存的答案
			// 如果命中直接返回
			// 看体系学习班，动态规划的章节
			return dp[i][j];
		}
		int ans = Integer.MAX_VALUE;
		for (int m = i + 1; m < j; m++) {
			ans = Math.min(ans, f(values, i, m, dp) + f(values, m, j, dp) + values[i] * values[m] * values[j]);
		}
		dp[i][j] = ans;
		return ans;
	}

}
