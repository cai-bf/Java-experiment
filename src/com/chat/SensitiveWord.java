package com.chat;

import java.io.*;
import java.util.*;
import java.util.stream.StreamSupport;

public class SensitiveWord {
    private String dict_path;
    private Set<String> words = new HashSet<>();
    private static HashMap wordMap;

    public SensitiveWord(String path){
        dict_path = path;
        init();
    }

    private void init(){
        File file = new File(dict_path);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (IOException e){
            e.printStackTrace();
        }
        String word;
        try {
            while ((word = reader.readLine()) != null){
                word = word.trim();
                words.add(word);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        initwordMap();
    }

    private void initwordMap(){
        // 初始化敏感词容器
        wordMap = new HashMap(words.size());
        String key;
        Map now;
        Map<String, String> newMap;

        Iterator<String> iterator = words.iterator();
        while (iterator.hasNext()){
            key = iterator.next();
            now = wordMap;
            now.put("isEnd", "0");
            for (int i=0; i<key.length(); i++){
                // 获取关键字
                char keyChar = key.charAt(i);
                // 获取容器中该关键字
                Object map = now.get(keyChar);
                // 存在则进入下一个循环
                if (map != null){
                    now = (Map)map;
                } else {
                    // 不存在则新建map, 置isEnd＝０,并存进敏感词容器
                    newMap = new HashMap<>();
                    newMap.put("isEnd", "0");
                    now.put(keyChar, newMap);
                    now = newMap;
                }
                if (i == key.length()-1){
                    // the last
                    now.put("isEnd", "1");
                }
            }
        }
    }

    /**
     * 检查是否存在关键词并替换
     * 尚未完全完善，某些情况下会出现识别错误的情况
     */
    public String filter(StringBuilder str) {
        StringBuilder temp = new StringBuilder(str);
        Map now = wordMap;
        // System.out.println(str.charAt(2));
            for (int j = 0; j < str.length(); j++) {
                char word = str.charAt(j);
                Object map = now.get(word);
                if (map != null) {
                    now = (Map) map;
                    // 替换关键词
                    str.replace(j, j + 1, "*");
                    System.out.println(str);
                    if (now.get("isEnd").equals("1")) {
                        now = wordMap;
                        temp = new StringBuilder(str);
                    }
                } else {
                    // if语句用于避免关键词接在不完整关键词后导致的错误判断
                    if (str.charAt(j-1) == '*')
                        j--;
                    str = new StringBuilder(temp);
                    now = wordMap;

                }
                if (j == str.length()-1 && map!=null && !now.get("isEnd").equals("1")) {
                    str = new StringBuilder(temp);
                }
        }
        return str.toString();
    }

    public static void main(String[] args){
        SensitiveWord sensitiveWord = new SensitiveWord("./Words.txt");
        Scanner scanner = new Scanner(System.in);
        String result;
        String message;
        while (!(message = scanner.nextLine()).equals("")){
            StringBuilder t = new StringBuilder(message);
            long start = System.currentTimeMillis();
            result =  sensitiveWord.filter(t);
            long end = System.currentTimeMillis();
            System.out.println(result + (end - start));
        }
    }
}
