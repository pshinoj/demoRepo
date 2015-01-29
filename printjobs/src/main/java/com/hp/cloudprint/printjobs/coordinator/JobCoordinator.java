package com.hp.cloudprint.printjobs.coordinator;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by prabhash on 8/14/2014.
 */
public class JobCoordinator {

    private CuratorFramework curator;
    private String localIPAddress;
    private static final String SLASH = "/";
    private List<String> deviceWatcherList;

    public JobCoordinator() {
        initZookeeper();
    }

    private void initZookeeper() {
        deviceWatcherList = new ArrayList<String>();
        try {
            this.localIPAddress = Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            PropertiesConfiguration zkConfig = new PropertiesConfiguration("zk.properties");
            String connectString = zkConfig.getString("zkConnect");
            Integer retryCount = zkConfig.getInt("zkRetryCount");
            Integer retryInterval = zkConfig.getInt("zkRetryInterval");

            this.curator = CuratorFrameworkFactory.builder().namespace("print-job-0s1").connectString(connectString)
                    .retryPolicy(new RetryNTimes(retryCount, retryInterval)).build();
            this.curator.start();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void addDeviceJobInfo(String deviceId, JobInfo jobInfo) {
        // The final path to create is "/print-job-0s1/jobId"
        String path = createNodeIfNeeded(jobInfo.getJobId().toString(), "", null);
        // The final path to create is "/print-job-0s1/deviceId/flowType"
        String devPath = createNodeIfNeeded(deviceId, "", null);
        String flowPath = createNodeIfNeeded(jobInfo.getFlowType().toString(), devPath, null);
        if (!deviceWatcherList.contains(deviceId)) {
            addDataWatcher(path, new DeviceJobFlowWatcher());
            deviceWatcherList.add(deviceId);
        }
        // Create host node under device
        try {
            //path = createNodeIfNeeded(localIPAddress, path, null);
            //path = createNodeIfNeeded(jobInfo.getJobId().toString(), path, CreateMode.EPHEMERAL_SEQUENTIAL);
            final byte[] jobData = buildJobInfoJson(jobInfo);
            this.curator.setData().forPath(path, jobData);
            addDataWatcher(path, new JobStatusWatcher());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeDeviceJobInfo(String deviceId, String jobId) {
        String hostPath =SLASH+deviceId+SLASH+localIPAddress;
        String path = hostPath+SLASH+jobId;
        try {
            Stat stat = curator.checkExists().forPath(path);
            if (stat != null) {
                curator.delete().forPath(path);
                // If no more child for this node, delete the node itself
                List<String> children = curator.getChildren().forPath(hostPath);
                if (children == null || children.size() == 0) {
                    curator.delete().forPath(hostPath);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private byte[] buildJobInfoJson(JobInfo jobInfo) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsBytes(jobInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String createNodeIfNeeded(String nodeName, String parent, CreateMode mode) {
        String path = parent + SLASH + nodeName;
        Stat stat = null;
        try {
            stat = this.curator.checkExists().forPath(path);
            if (stat == null) {
                if (mode == null) {
                    path = this.curator.create().creatingParentsIfNeeded().forPath(path);
                } else {
                    path = this.curator.create().creatingParentsIfNeeded().withMode(mode).forPath(path);
                }

            }
        } catch (Exception e) {
            path = null;
            e.printStackTrace();
        }
        return path;
    }

    private void addDataWatcher(String path, Watcher watcher) {
        try {
            this.curator.getData().usingWatcher(watcher).forPath(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
