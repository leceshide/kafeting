package com.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
/**方法来源与网络，在实际使用中发现有错误*/
public class DFA {
	public static final DFA dao = new DFA();
	public static final String[] arr = { "淫", "法轮功","做爱","共产党", "激情", "性交" };
	private Node rootNode = new Node('R');
	private int a = 0;
	private StringBuilder strWord = new StringBuilder();
	public void searchWord(String content) {
		char[] chars = content.toCharArray();
		Node node = rootNode;
		while (a < chars.length) {
			node = findNode(node, chars[a]);
			if (node == null) {
				node = rootNode;
				strWord.append(chars[a]);
			} else {
				strWord.append("*");
			}
			a++;
		}
	}

	public void createTree(String[] arr) {
		for (String str : arr) {
			char[] chars = str.toCharArray();
			if (chars.length > 0)
				insertNode(rootNode, chars, 0);
		}
	}

	private void insertNode(Node node, char[] cs, int index) {
		Node n = findNode(node, cs[index]);
		if (n == null) {
			n = new Node(cs[index]);
			node.nodes.add(n);
		}

		if (index == (cs.length - 1))
			n.flag = 1;

		index++;
		if (index < cs.length)
			insertNode(n, cs, index);
	}

	private Node findNode(Node node, char c) {
		List<Node> nodes = node.nodes;
		Node rn = null;
		for (Node n : nodes) {
			if (n.c == c) {
				rn = n;
				break;
			}
		}
		return rn;
	}

	private static class Node {
		public char c;
		public int flag; // 1：表示终结，0：延续 这里只替换成*所以用不着
		public List<Node> nodes = new ArrayList<Node>();

		public Node(char c) {
			this.c = c;
			this.flag = 0;
		}

		public Node(char c, int flag) {
			this.c = c;
			this.flag = flag;
		}
	}

	public String replaceAllWord(String[] arr, String content) {
		char conCharArry[] = content.toCharArray();
		// 这里key为每个敏感词的第一个字符，里面放着第一个字符相同的敏感词list集合
		Map<Character, List<String>> word = new HashMap<Character, List<String>>();

		// 遍历数组生成敏感词map对象
		for (String str : arr) {
			char key = str.charAt(0);
			List<String> list = word.get(key);
			if (list == null) {
				list = new ArrayList<String>();
				list.add(str);
				word.put(key, list);
			} else {
				list.add(str);
			}
		}

		// 对内容每一个字符进行遍历，如果当前字符为敏感词的首字符则进行下面行为否则continue本次操作
		for (int i = 0; i < conCharArry.length; i++) {
			List<String> list = word.get(conCharArry[i]);

			if (list == null) {
				continue;
			}

			for (String str : list) {
				char words[] = str.toCharArray();
				// 对是否匹配一个完整的敏感词进行标志，如果匹配敏感词过程中有一个字符不符则标注为false
				boolean mark = true;
				for (int j = 0; j < words.length; j++) {
					if (words[j] != conCharArry[j + i]) {
						mark = false;
						break;
					}
				}

				// 把敏感词逐个替换成*
				if (mark) {
					for (int j = 0; j < words.length; j++) {
						conCharArry[i++] = '*';
					}
				}
			}
		}

		return new String(conCharArry);

	}
	
	
	
	public String replaceWord(String content) {
		char conCharArry[] = content.toCharArray();
		// 这里key为每个敏感词的第一个字符，里面放着第一个字符相同的敏感词list集合
		Map<Character, List<String>> word = new HashMap<Character, List<String>>();

		// 遍历数组生成敏感词map对象
		for (String str : arr) {
			char key = str.charAt(0);
			List<String> list = word.get(key);
			if (list == null) {
				list = new ArrayList<String>();
				list.add(str);
				word.put(key, list);
			} else {
				list.add(str);
			}
		}

		// 对内容每一个字符进行遍历，如果当前字符为敏感词的首字符则进行下面行为否则continue本次操作
		for (int i = 0; i < conCharArry.length; i++) {
			List<String> list = word.get(conCharArry[i]);

			if (list == null) {
				continue;
			}

			for (String str : list) {
				char words[] = str.toCharArray();
				// 对是否匹配一个完整的敏感词进行标志，如果匹配敏感词过程中有一个字符不符则标注为false
				boolean mark = true;
				for (int j = 0; j < words.length; j++) {
					if(j + i <=  conCharArry.length){//加判断，放在报错--------先应急
						if (words[j] != conCharArry[j + i]) {/*此处报错-=------------------*/
							mark = false;
							break;
						}
					}
				}

				// 把敏感词逐个替换成*
				if (mark) {
					for (int j = 0; j < words.length; j++) {
						conCharArry[i++] = '*';
					}
				}
			}
		}
		return new String(conCharArry);
	}

	public static Properties loadPropertyFile(String fullFile) {
		String webRootPath = null;
		if (null == fullFile || fullFile.equals(""))
			throw new IllegalArgumentException(
					"Properties file path can not be null : " + fullFile);
		webRootPath = DFA.class.getClassLoader().getResource("").getPath();
		webRootPath = new File(webRootPath).getParent();
		InputStream inputStream = null;
		Properties p = null;
		try {
			inputStream = new FileInputStream(new File(webRootPath
					+ File.separator + fullFile));
			p = new Properties();
			p.load(inputStream);
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("Properties file not found: "
					+ fullFile);
		} catch (IOException e) {
			throw new IllegalArgumentException(
					"Properties file can not be loading: " + fullFile);
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return p;
	}

	public String replaceWordStr(String content) {
		char conCharArry[] = content.toCharArray();
		StringBuffer sb = new StringBuffer();

		// 这里key为每个敏感词的第一个字符，里面放着第一个字符相同的敏感词list集合
		Map<Character, List<String>> word = new HashMap<Character, List<String>>();
		Properties properties = loadPropertyFile("words.properties");
		// 遍历数组生成敏感词map对象
		for (Entry entry : properties.entrySet()) {
			String keyWrod = entry.getKey().toString();
			char key = "".equals(keyWrod) ? ' ' : keyWrod.charAt(0);
			List<String> list = word.get(key);

			if (list == null) {
				list = new ArrayList<String>();
				list.add(keyWrod);
				word.put(key, list);
			} else {
				list.add(keyWrod);
			}
		}

		// 对内容每一个字符进行遍历，如果当前字符为敏感词的首字符则进行下面行为否则continue本次操作
		for (int i = 0; i < conCharArry.length; i++) {
			List<String> list = word.get(conCharArry[i]);

			if (list == null) {
				sb.append(conCharArry[i]);
				continue;
			}

			for (String str : list) {
				char words[] = str.toCharArray();
				// 对是否匹配一个完整的敏感词进行标志，如果匹配敏感词过程中有一个字符不符则标注为false
				boolean mark = true;
				for (int j = 0; j < words.length; j++) {
					if (words[j] != conCharArry[j + i]) {
						mark = false;
						break;
					}
				}
				// 把敏感词逐个替换
				if (mark) {
					sb.append(properties.get(str));
					for (int j = 1; j < words.length; j++) {
						i++;
					}
				} else {
					sb.append(conCharArry[i]);
				}
			}
		}

		return sb.toString();

	}

//	public static void main(String[] args) {
//		//String[] arr = { "tmd", "小姐","权", "DA" };
//		String content = "tmd ITeye文章版权属于作者，受法律保护 Da 小姐";
//		long start = System.currentTimeMillis();
//		for (int i = 0; i < 10000; i++) {
//			DFA dfa = new DFA();
//			//dfa.createTree(arr);
//			//dfa.searchWord(content);
//			//dfa.replaceWordStr(content);
//			System.out.println(dfa.replaceWord(content));
//			//System.out.println(dfa.replaceAllWord(arr, content));
//		}
//		long end = System.currentTimeMillis();
//		System.out.println(end - start);
//	}

}
