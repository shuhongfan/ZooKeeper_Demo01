package com.shf.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class CuratorWatcherTest {
    private CuratorFramework client;
    /**
     * 建立连接
     */
    @Before
    public void testConnect(){
//        1. 第一种方式
        ExponentialBackoffRetry retry = new ExponentialBackoffRetry(3000, 10);
//        CuratorFramework client = CuratorFrameworkFactory.newClient("127.0.0.1:2181",
//                60 * 1000, 1000, retry);
//        client.start();

//        2. 第二种方式
        client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .sessionTimeoutMs(60 * 1000)
                .connectionTimeoutMs(15 * 1000)
                .retryPolicy(retry)
                .namespace("shf")
                .build();
        client.start();
    }

    @After
    public void close(){
        if (client != null){
            client.close();
        }
    }

    @Test
    public void testNodeCache() throws Exception {
//        1. 创建NodeCache对象
        NodeCache nodeCache = new NodeCache(client, "/app1");

//        2. 注册监听
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                System.out.println("节点变化了");
            }
        });

//        3.开启监听
        nodeCache.start(true);

        while (true){

        }
    }

    @Test
    public void testPathChildrenCache(){
//        1.创建监听对象
        PathChildrenCache pathChildrenCache = new PathChildrenCache(client, "/app2", true);
//        2.绑定监听器

    }
}
