package java0.conc0303;

import com.sun.xml.internal.ws.util.CompletedFuture;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * 本周作业：（必做）思考有多少种方式，在main函数启动一个新线程或线程池，
 * 异步运行一个方法，拿到这个方法的返回值后，退出主线程？
 * 写出你的方法，越多越好，提交到github。
 *
 * 一个简单的代码参考：
 */
public class Homework03 {
    
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        
        long start=System.currentTimeMillis();
        // 在这里创建一个线程或线程池，
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        // 异步执行 下面方法
        // 方法1
        //Future<Integer> future = executorService.submit(new CallableTask());
        //int result = future.get();

        // 方法2
        //Future<?> future = executorService.submit(new RunnableTask(), Integer.class);
        //int result = sum(); //这是得到的返回值

        // 方法3
        //FutureTask<Integer> futureTask = new FutureTask<>(new Callable<Integer>() {
        //    @Override
        //    public Integer call() throws Exception {
        //        return sum();
        //    }
        //});
        //executorService.submit(futureTask);
        //int result = futureTask.get();

        // 方法4
        //FutureTask<Integer> futureTask = new FutureTask<>(new Callable<Integer>() {
        //    @Override
        //    public Integer call() throws Exception {
        //        return sum();
        //    }
        //});
        //new Thread(futureTask).start();
        //Integer result = futureTask.get();

        // 方法5
        CompletableFuture<Integer> cf = CompletableFuture.supplyAsync(Homework03::sum);
        Object result = cf.get();


        // 确保  拿到result 并输出
        System.out.println("异步计算结果为："+result);
         
        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
        
        // 然后退出main线程
        executorService.shutdown();
    }

    static class CallableTask implements Callable {
        @Override
        public Object call() throws Exception {
            return sum();
        }
    }

    static class RunnableTask implements Runnable {
        @Override
        public void run() {
            sum();
        }
    }
    
    private static int sum() {
        return fibo(36);
    }
    
    private static int fibo(int a) {
        if ( a < 2) 
            return 1;
        return fibo(a-1) + fibo(a-2);
    }
}
