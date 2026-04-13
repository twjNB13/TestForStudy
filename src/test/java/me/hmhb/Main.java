package me.hmhb;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Main {

    public volatile static int a = 1;

    public static void main(String[] args) throws InterruptedException {

        System.out.println(new ArrayList<Integer>() instanceof List<Integer>);
    }

    interface A {
        void test();
    }
    
    class B implements A {

        @Override
        public void test() {
            System.out.println("test");
        }
    }
}
