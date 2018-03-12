package com.abc.springBootHelpingComponents.Controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;

//@Controller
@RestController
//@RequestMapping("/welcome")
public class WelcomeController {

    @RequestMapping("/hello")
    public String hello(){
        ExecutorService executor = Executors.newFixedThreadPool(10);
        String[] hostList = { "https://crunchify.com", "http://yahoo.com",
                "http://ip.jsontest.com/?callback=showMyIP",
                "http://www.ebay.com", "http://google.com",
                "http://www.example.co", "https://paypal.com",
                "http://bing.com/", "http://techcrunch.com/",
                "http://mashable.com/", "http://thenextweb.com/",
                "http://wordpress.com/", "http://wordpress.org/",
                "http://example.com/", "http://sjsu.edu/",
                "http://ebay.co.uk/", "http://google.co.uk/",
                "http://www.wikipedia.org/",
                "http://en.wikipedia.org/wiki/Main_Page" };


     Set<Future<Map>>set =new HashSet<Future<Map>>();
        for (int i = 0; i < hostList.length; i++) {

            String url = hostList[i];
            Callable worker = new AsynchronousHttpClient(url);
            set.add(executor.submit(worker));
        }
        executor.shutdown();
        // Wait until all threads are finish
        for(Future future :set){
            try {
                System.out.println(future.get());
            } catch (CancellationException e ){
                e.printStackTrace();
            } catch (ExecutionException e){
                e.printStackTrace();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        System.out.println("\nFinished all threads");


        return "Hello";
    }

    public static class AsynchronousHttpClient implements Callable {
        private final String url;

        private ConcurrentMap<String,Object> response=new ConcurrentHashMap<String,Object>();
        StringBuffer stringBuffer =new StringBuffer();
        AsynchronousHttpClient(String url) {
            this.url = url;
        }

//
        @Override
        public Map<String,Object> call() {

            String result = "";
            String content = "";
            try {
                URL siteURL = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) siteURL
                        .openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                BufferedReader in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null){
                    stringBuffer.append(inputLine);
                }

                in.close();

            } catch (Exception e) {
                result = "->Red<-\t";
            }
//            JSONObject json = new JSONObject(stringBuffer);
//            String xml = XML.toString(json);
//            response.put(url,xml);
            response.put(url,stringBuffer);
           // System.out.println(url + "\t\tStatus:" + result);
            return response;
        }


    }
}
