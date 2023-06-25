package class_2023_07_1_week;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

// 测试链接 : https://leetcode.cn/problems/find-critical-and-pseudo-critical-edges-in-minimum-spanning-tree/
public class Code04_FindCriticalAndPseudoCriticalEdges {

	public static int MAXN = 101;

	public static int MAXM = 201;

	// 记录
	public static int[] record = new int[MAXM];

	// 并查集相关
	public static int[] father = new int[MAXN];
	public static int[] size = new int[MAXN];
	public static int[] help = new int[MAXN];
	public static int sets = 0;

	// 边相关
	public static int[][] edges = new int[MAXM][4];
	public static int m;

	// 找桥相关
	public static int[] dfn = new int[MAXN];
	public static int[] low = new int[MAXN];
	public static int cnt;

	public static List<List<Integer>> findCriticalAndPseudoCriticalEdges(int n, int[][] e) {
		buildUnoinSet(n);
		m = e.length;
		buildEdges(e);
		Arrays.fill(record, 0, m, -1);
		List<Integer> real = new ArrayList<>();
		List<Integer> pseudo = new ArrayList<>();
		int teamStart = 0;
		while (sets != 1) {
			int teamEnd = teamStart;
			while (teamEnd + 1 < m && edges[teamEnd + 1][3] == edges[teamStart][3]) {
				teamEnd++;
			}
			bridge(teamStart, teamEnd);
			for (int i = teamStart; i <= teamEnd; i++) {
				int ei = edges[i][0];
				if (record[ei] == 0) {
					real.add(ei);
				} else if (record[ei] == 1) {
					pseudo.add(ei);
				}
				union(edges[i][1], edges[i][2]);
			}
			teamStart = teamEnd + 1;
		}
		List<List<Integer>> ans = new ArrayList<>();
		ans.add(real);
		ans.add(pseudo);
		return ans;
	}

	// 并查集初始化
	public static void buildUnoinSet(int n) {
		for (int i = 0; i < n; i++) {
			father[i] = i;
			size[i] = 1;
		}
		sets = n;
	}

	// 并查集向上找代表节点
	public static int find(int i) {
		int r = 0;
		while (i != father[i]) {
			help[r++] = i;
			i = father[i];
		}
		while (r > 0) {
			father[help[--r]] = i;
		}
		return i;
	}

	// 并查集合并集合
	public static void union(int i, int j) {
		int fi = find(i);
		int fj = find(j);
		if (fi != fj) {
			if (size[fi] >= size[fj]) {
				father[fj] = fi;
				size[fi] += size[fj];
			} else {
				father[fi] = fj;
				size[fj] += size[fi];
			}
			sets--;
		}
	}

	public static void buildEdges(int[][] e) {
		for (int i = 0; i < m; i++) {
			edges[i][0] = i;
			edges[i][1] = e[i][0];
			edges[i][2] = e[i][1];
			edges[i][3] = e[i][2];
		}
		Arrays.sort(edges, 0, m, (a, b) -> a[3] - b[3]);
	}

	public static void bridge(int start, int end) {
		int n = 0;
		HashMap<Integer, Integer> id = new HashMap<>();
		for (int i = start; i <= end; i++) {
			int a = find(edges[i][1]);
			int b = find(edges[i][2]);
			if (!id.containsKey(a)) {
				id.put(a, n++);
			}
			if (!id.containsKey(b)) {
				id.put(b, n++);
			}
		}
		List<List<int[]>> graph = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			graph.add(new ArrayList<>());
		}
		for (int i = start; i <= end; i++) {
			int index = edges[i][0];
			int a = id.get(find(edges[i][1]));
			int b = id.get(find(edges[i][2]));
			if (a != b) {
				record[index] = 1;
				graph.get(a).add(new int[] { index, b });
				graph.get(b).add(new int[] { index, a });
			} else {
				record[index] = 2;
			}
		}
		criticalConnections(n, graph);
		// 处理重复连接
		// 什么是重复连接？不是自己指向自己，那叫自环
		// 重复连接指的是:
		// 集合a到集合b有一条边，边的序号是p
		// 于是，a的邻接表里有(p,b)，b的邻接表里有(p,a)
		// 集合a到集合b又有一条边，边的序号是t
		// 于是，a的邻接表里有(t,b)，b的邻接表里有(t,a)
		// 那么p和t都是重复链接，因为单独删掉p或者t，不会影响联通性
		// 而这种重复链接，在求桥的模版中是不支持的
		// 也就是说求桥的模版中，默认没有重复链接，才能去用模版
		// 所以这里要单独判断，如果有重复链接被设置成了桥，要把它改成伪关键边状态
		for (int i = 0; i < n; i++) {
			List<int[]> nexts = graph.get(i);
			nexts.sort((a, b) -> a[1] - b[1]);
			for (int j = 1; j < nexts.size(); j++) {
				if (nexts.get(j)[1] == nexts.get(j - 1)[1]) {
					record[nexts.get(j)[0]] = 1;
					record[nexts.get(j - 1)[0]] = 1;
				}
			}
		}
	}

	public static void criticalConnections(int n, List<List<int[]>> graph) {
		Arrays.fill(dfn, 0, n, 0);
		Arrays.fill(low, 0, n, 0);
		cnt = 0;
		for (int i = 0; i < n; i++) {
			if (dfn[i] == 0) {
				tarjan(i, i, -1, -1, graph);
			}
		}
	}

	public static void tarjan(int start, int cur, int father, int ei, List<List<int[]>> graph) {
		dfn[cur] = low[cur] = ++cnt;
		for (int[] edge : graph.get(cur)) {
			int nei = edge[0];
			int nni = edge[1];
			if (nni != father) {
				if (dfn[nni] == 0) {
					tarjan(start, nni, cur, nei, graph);
					low[cur] = Math.min(low[cur], low[nni]);
				} else {
					low[cur] = Math.min(low[cur], dfn[nni]);
				}
			}
		}
		if (low[cur] == dfn[cur] && cur != start) {
			record[ei] = 0;
		}
	}

}