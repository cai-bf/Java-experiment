package com.iotest;

import java.io.*;
import java.util.*;

public class QueryLastName {
    private Map<String, Integer> name = new TreeMap<>();

    public QueryLastName(String apath){
        readFile(apath);
    }
    private void add(String aname){
        if (name.containsKey(aname)){
            name.put(aname, name.get(aname)+1);
        }else{
            name.put(aname, 1);
        }
    }
    private void readFile(String apath){
        File file = new File(apath);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempstring = null;
            while ((tempstring = reader.readLine()) != null) {
                tempstring = tempstring.substring(0, 1);
                add(tempstring);
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        finally {
            if (reader != null)
                try {
                    reader.close();
                }catch (IOException e){

                }
        }
    }

    public void getResult() {
        Set<String> str = name.keySet();
        for (String firstname : str){
            System.out.println(firstname + " : " + name.get(firstname) + "人");
        }
    }

    public static void main(String[] args){
        QueryLastName atest = new QueryLastName("./Names.txt");
        atest.getResult();

    }
}
