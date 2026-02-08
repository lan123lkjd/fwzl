package com.rental.utils;

import java.util.*;

/**
 * Trie字典树 - 敏感词过滤
 */
public class TrieUtil {

    private final TrieNode root;
    private static final String REPLACEMENT = "***";

    // 默认敏感词列表
    private static final List<String> DEFAULT_SENSITIVE_WORDS = Arrays.asList(
            "傻逼", "操你", "草泥马", "妈的", "去死", "垃圾", "废物",
            "白痴", "蠢货", "混蛋", "王八蛋", "滚蛋", "狗屎");

    public TrieUtil() {
        this.root = new TrieNode();
        // 初始化默认敏感词
        for (String word : DEFAULT_SENSITIVE_WORDS) {
            addWord(word);
        }
    }

    public TrieUtil(List<String> sensitiveWords) {
        this.root = new TrieNode();
        for (String word : sensitiveWords) {
            addWord(word);
        }
    }

    /**
     * 添加敏感词
     */
    public void addWord(String word) {
        if (word == null || word.isEmpty()) {
            return;
        }
        TrieNode current = root;
        for (char c : word.toCharArray()) {
            current.children.computeIfAbsent(c, k -> new TrieNode());
            current = current.children.get(c);
        }
        current.isEnd = true;
    }

    /**
     * 过滤敏感词
     */
    public String filter(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        StringBuilder result = new StringBuilder();
        int i = 0;

        while (i < text.length()) {
            int matchLength = findSensitiveWord(text, i);
            if (matchLength > 0) {
                result.append(REPLACEMENT);
                i += matchLength;
            } else {
                result.append(text.charAt(i));
                i++;
            }
        }

        return result.toString();
    }

    /**
     * 检查是否包含敏感词
     */
    public boolean containsSensitiveWord(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }

        for (int i = 0; i < text.length(); i++) {
            if (findSensitiveWord(text, i) > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取所有敏感词
     */
    public List<String> findAllSensitiveWords(String text) {
        List<String> words = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return words;
        }

        int i = 0;
        while (i < text.length()) {
            int matchLength = findSensitiveWord(text, i);
            if (matchLength > 0) {
                words.add(text.substring(i, i + matchLength));
                i += matchLength;
            } else {
                i++;
            }
        }
        return words;
    }

    /**
     * 从指定位置查找敏感词，返回匹配长度
     */
    private int findSensitiveWord(String text, int start) {
        TrieNode current = root;
        int matchLength = 0;
        int lastMatchLength = 0;

        for (int i = start; i < text.length(); i++) {
            char c = text.charAt(i);
            TrieNode child = current.children.get(c);

            if (child == null) {
                break;
            }

            matchLength++;
            current = child;

            if (current.isEnd) {
                lastMatchLength = matchLength;
            }
        }

        return lastMatchLength;
    }

    /**
     * Trie节点
     */
    private static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isEnd = false;
    }
}
