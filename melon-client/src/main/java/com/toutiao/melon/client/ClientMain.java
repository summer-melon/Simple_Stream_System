package com.toutiao.melon.client;

import com.toutiao.melon.rpc.ManageJobRequest;
import com.toutiao.melon.rpc.ManageJobRequestMetadata.RequestType;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientMain {

    private static final Logger log = LoggerFactory.getLogger(ClientMain.class);

    public static void main(String[] args) throws Exception {
        checkUsage(args);

        String master = args[1] + ":6000";
        RequestType requestType = RequestType.UNRECOGNIZED;
        InputStream inputStream = null;
        String jobName = null;
        switch (args[0]) {
            case "start":
                checkArguments(args, 4);
                requestType = RequestType.START_JOB;
                // 获取提交作业jar包
                inputStream = new FileInputStream(new File(args[2]));
                jobName = args[3];
                break;
            case "stop":
                checkArguments(args, 3);
                requestType = RequestType.STOP_JOB;
                jobName = args[2];
                break;
            case "list":
                checkArguments(args, 2);
                requestType = RequestType.QUERY_RUNNING_JOB;
                break;
            default:
                log.error("Not Support.");
        }
        communicateWithMaster(master, requestType, inputStream, jobName);
    }

    private static void communicateWithMaster(String master,
                                       RequestType requestType,
                                       InputStream inputStream,
                                       String jobName) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder.forTarget(master)
                .usePlaintext()
                .build();
        try {
            ClientManager client = new ClientManager(channel);
            log.info("{}", client.manageJob(requestType, jobName, inputStream));
        } catch (IOException | InterruptedException e) {
            log.error("Failed while performing request: {}", e.toString());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }

    }

    private static void checkUsage(String[] args) {
        if (args.length == 0 || !args[0].matches("start|stop|list")) {
            System.err.println("Usage: ");
            System.err.println("  start <master_ip> <jar_file_name> <job_name>");
            System.err.println("  stop <master_ip> <job_name>");
            System.err.println("  list <master_ip>");
            System.exit(-1);
        }
    }

    private static void checkArguments(String[] args, int num) {
        if (args.length < num) {
            System.err.println("Lack arguments");
            System.exit(-1);
        }
    }
}
