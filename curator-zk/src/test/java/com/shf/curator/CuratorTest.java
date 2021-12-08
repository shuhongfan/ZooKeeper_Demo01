package com.shf.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class CuratorTest {
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

    /**
     * 创建节点  create
     * 1. 基本创建
     * 2. 创建节点 带有数据
     * 3. 设置节点的类型
     * 4. 创建多级节点
     */
    @Test
    public void testCreate() throws Exception {
//        1.基本创建
        String path = client.create().forPath("/app2","hehe".getBytes());
        System.out.println(path);
    }

    @Test
    public void testCreate3() throws Exception {
        String path = client.create().withMode(CreateMode.EPHEMERAL).forPath("/app3");
        System.out.println(path);
    }

    @Test
    public void testCreate4() throws Exception {
        String path = client.create().creatingParentsIfNeeded().forPath("/app4/p1");
        System.out.println(path);
    }

    /**
     * 查询数据
     * @throws Exception
     */
    @Test
    public void testGet() throws Exception {
//        查询数据get  getData
        byte[] bytes = client.getData().forPath("/app1");
        System.out.println(new String(bytes));
    }

    @Test
    public void testGet2() throws Exception {
//        查询子节点  ls   getChildren
        List<String> path = client.getChildren().forPath("/app4");
        System.out.println(path);
    }

    @Test
    public void testGet3() throws Exception {
//        查询节点的状态信息
        Stat status = new Stat();
        byte[] path = client.getData().storingStatIn(status).forPath("/app1");
        System.out.println(new String(path));
    }

//    修改数据
    @Test
    public void testSet() throws Exception {
        client.setData().forPath("/app1", "shf".getBytes());
    }

//    根据版本修改
    @Test
    public void testSetForVersion() throws Exception {
        Stat status = new Stat();
        byte[] path = client.getData().storingStatIn(status).forPath("/app1");
        int version = status.getVersion();
        client.setData().withVersion(version).forPath("/app1","haha".getBytes());
    }

    @Test
    public void testDelete() throws Exception {
//        删除带有子节点的节点
        client.delete().deletingChildrenIfNeeded().forPath("/app4");
    }

    @Test
    public void testDelete3() throws Exception {
        client.delete().guaranteed().inBackground(new BackgroundCallback() {
            @Override
            public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                System.out.println("我被删除了");
            }
        }).forPath("/app3");
    }
}
