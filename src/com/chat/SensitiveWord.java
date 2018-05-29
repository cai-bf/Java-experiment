package com.chat;

import java.io.*;
import java.util.*;

public class SensitiveWord {
    private String dict_path;
    private Set<String> words;
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

    public String filter(StringBuffer str){
        
    }
}
