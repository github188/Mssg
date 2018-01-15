package com.fable.framework.proxy.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Sets;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * .
 *
 * @author stormning 2017/9/21
 * @since 1.3.0
 */
@Slf4j
public class PortUtils {

    private static Cache<Integer, Long> PORTS_TO_BE_TAKEN
            = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS).build();

    @SneakyThrows
    private static int randomPort() {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(0);
            socket.setReuseAddress(false);
            int localPort = socket.getLocalPort();
            if (PORTS_TO_BE_TAKEN.getIfPresent(localPort) != null) {
                return randomPort();
            }
            return localPort;
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }

    public synchronized static void usePorts(int... ports) {
        for (int port : ports) {
            PORTS_TO_BE_TAKEN.invalidate(port);
        }
    }

    /**
     * @param size
     * @return ports
     */
    public synchronized static int[] randomPorts(int size) {
        int startPort = randomPort();
        if (loopTest(startPort, size)) {
            int ports[] = new int[size];
            for (int i = 0; i < size; i++) {
                int port = startPort + i;
                PORTS_TO_BE_TAKEN.put(port, System.currentTimeMillis());
                ports[i] = port;
            }
            return ports;
        }
        return randomPorts(size);
    }

    private static boolean loopTest(int startPort, int size) {
        if (startPort % 2 == 1) {
            return false;
        }
        for (int i = 1; i < size; i++) {
            if (isPortInUse(startPort + i)) {
                return false;
            }
        }
        return true;
    }

    @SneakyThrows
    public static boolean isPortInUse(int port) {
        if (PORTS_TO_BE_TAKEN.getIfPresent(port) != null) {
            return true;
        }
        ServerSocket socket = null;
        boolean inUse = false;
        try {
            socket = new ServerSocket(port);
        } catch (IOException e) {
            inUse = true;
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
        return inUse;
    }

    public static void main(String[] args) throws InterruptedException {
//        int[] ports = PortUtils.randomPorts(3);
//        System.out.println(Arrays.toString(ports));
//        System.out.println(PortUtils.PORTS_TO_BE_TAKEN.asMap());
//        System.out.println(PortUtils.isPortInUse(ports[1]));
//        PortUtils.usePorts(ports[1]);
//        System.out.println(PortUtils.PORTS_TO_BE_TAKEN.asMap());
//        System.out.println(PortUtils.isPortInUse(ports[1]));
//        Executors.newSingleThreadScheduledExecutor().schedule(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println(PortUtils.isPortInUse(ports[2]));
//            }
//        }, 5, TimeUnit.SECONDS);
//
//        Executors.newSingleThreadScheduledExecutor().schedule(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println(PortUtils.isPortInUse(ports[2]));
//            }
//        }, 11, TimeUnit.SECONDS);


        Set<Integer> all = Sets.newHashSet();

        CountDownLatch latch = new CountDownLatch(1000);
        for (int i = 0; i < 1000; i++) {
            new Thread(() -> {
                int[] ports = PortUtils.randomPorts(2);
                all.add(ports[0]);
                all.add(ports[1]);
                System.out.println(PORTS_TO_BE_TAKEN.size());
                latch.countDown();
            }).start();
        }
        latch.await();
        System.out.println(all.size());
    }
}
