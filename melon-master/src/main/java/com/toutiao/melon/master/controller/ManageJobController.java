package com.toutiao.melon.master.controller;

import com.toutiao.melon.master.job.ComputationGraph;
import com.toutiao.melon.master.job.JobLoader;
import com.toutiao.melon.master.service.JarFileService;
import com.toutiao.melon.master.service.ZooKeeperService;
import com.toutiao.melon.rpc.ManageJobGrpc.ManageJobImplBase;
import com.toutiao.melon.rpc.ManageJobRequest;
import com.toutiao.melon.rpc.ManageJobRequestMetadata;
import com.toutiao.melon.rpc.ManageJobRequestMetadata.RequestType;
import com.toutiao.melon.rpc.ManageJobResponse;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Singleton
public class ManageJobController extends ManageJobImplBase {
    private static final Logger log = LoggerFactory.getLogger(ManageJobController.class);

    @Inject
    private JarFileService jarService;

    @Inject
    private ZooKeeperService zkService;

    @Override
    public StreamObserver<ManageJobRequest> manageJob(
            StreamObserver<ManageJobResponse> responseObserver) {
        return new StreamObserver<ManageJobRequest>() {

            RequestType requestType = RequestType.UNRECOGNIZED;
            String jobName = null;
            private OutputStream jarFileOutputStream;
            private String message;

            @Override
            public void onNext(ManageJobRequest value) {
                if (value.hasMetadata()) {
                    if (requestType == RequestType.UNRECOGNIZED) {
                        ManageJobRequestMetadata metadata = value.getMetadata();
                        requestType = metadata.getRequestType();
                        jobName = metadata.getJobName();
                        validateJobName();
                        if (message == null) {
                            jarFileOutputStream = getJarFileOutputStream();
                        }
                    }
                } else {
                    if (jarFileOutputStream != null) {
                        try {
                            jarFileOutputStream.write(value.getJarBytes().toByteArray());
                        } catch (IOException e) {
                            log.error(e.toString());
                            if (message == null) {
                                message = "Fail to write jar file: " + e.toString();
                            }
                        }
                    } else if (message == null) {
                        message = "Internal error, unexpected jar file";
                    }
                }
            }

            @Override
            public void onError(Throwable t) {
                log.error(t.toString());
                if (message == null) {
                    message = "Network error: " + t.toString();
                }
                closeJarFileOutputStream();
            }

            @Override
            public void onCompleted() {
                closeJarFileOutputStream();
                if (message == null) {
                    processRequest();
                }

                ManageJobResponse resp = ManageJobResponse.newBuilder()
                        .setMessage(message).build();
                responseObserver.onNext(resp);
                responseObserver.onCompleted();
            }

            private void closeJarFileOutputStream() {
                if (jarFileOutputStream != null) {
                    try {
                        jarFileOutputStream.close();
                    } catch (IOException e) {
                        log.error(e.toString());
                        if (message == null) {
                            message = "Fail to write jar file: " + e.toString();
                        }
                    }
                }
            }

            private void validateJobName() {
                if (!RequestType.QUERY_RUNNING_JOB.equals(requestType)
                        && !jobName.matches("[a-zA-Z0-9]+")) {
                    message = "Only alphanumeric characters allowed for jobName";
                }
            }

            private OutputStream getJarFileOutputStream() {
                if (requestType == RequestType.START_JOB) {
                    try {
                        return jarService.getOutputStream(jobName);
                    } catch (IOException e) {
                        if (message == null) {
                            message = "Fail to write jar file: " + e.toString();
                        }
                    }
                }
                return null;
            }

            private String formatRunningJobs(Map<String, String> runningJobs) {
                StringBuilder builder = new StringBuilder("Running jobs:\n");
                boolean hasJob = false;
                for (Map.Entry<String, String> e : runningJobs.entrySet()) {
                    if ("run".equals(e.getValue())) {
                        builder.append("  ");
                        builder.append(e.getKey());
                        builder.append("\n");
                        hasJob = true;
                    }
                }
                if (!hasJob) {
                    builder.append("  <none>\n");
                }
                return builder.toString();
            }

            private void processRequest() {
                synchronized (ManageJobController.class) {
                    switch (requestType) {
                        case START_JOB:
                            if (jobName.length() > Byte.MAX_VALUE) {
                                message = "Length of job name shouldn't be more than 127";
                            }

                            if (zkService.jobExists(jobName)) {
                                message = "Job exists";
                                break;
                            }

                            try {
                                URL jarLocalUrl = jarService.getJarFileUrl(jobName);
                                ComputationGraph computationGraph =
                                        new JobLoader().load(jobName, jarLocalUrl);
                                zkService.startJob(jobName, computationGraph);
                            } catch (Throwable e) {
                                message = "Unable to start job: " + e.toString();
                                break;
                            }

                            message = "Success";
                            break;
                        case STOP_JOB:
                            try {
                                zkService.stopJob(jobName);
                                jarService.deleteJarFile(jobName);
                            } catch (Throwable t) {
                                message = "Failed to stop job: " + t.toString();
                                break;
                            }

                            message = "Success";
                            break;
                        case QUERY_RUNNING_JOB:
                            message = formatRunningJobs(zkService.getRunningJobs());
                            break;
                        default:
                            log.error("Not Support.");
                            System.exit(-1);
                    }
                }
            }
        };
    }
}
