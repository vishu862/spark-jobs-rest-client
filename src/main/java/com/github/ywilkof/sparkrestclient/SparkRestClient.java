package com.github.ywilkof.sparkrestclient;

import com.github.ywilkof.sparkrestclient.interfaces.JobStatusRequestSpecification;
import com.github.ywilkof.sparkrestclient.interfaces.JobSubmitRequestSpecification;
import com.github.ywilkof.sparkrestclient.interfaces.KillJobRequestSpecification;
import com.github.ywilkof.sparkrestclient.interfaces.RequestSpecification;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;

import java.util.*;

/**
 * Created by yonatan on 08.10.15.
 */
@Setter(AccessLevel.PACKAGE)
@Getter(AccessLevel.PUBLIC)
public class SparkRestClient implements RequestSpecification {

    SparkRestClient() {}

    private SparkVersion sparkVersion;

    private Integer masterPort;

    private String masterHost;

    private Boolean eventLogEnabled;

    private Boolean supervise;

    private ClusterMode clusterMode;

    private Map<String,String> environmentVariables;

    private static final String DEPLOY_MODE_CLUSTER = "cluster";

    private HttpClient client;

    String getMasterUrl() {
        return masterHost + ":" + masterPort;
    }

    public static SparkRestClientBuilder builder() {
        return new SparkRestClientBuilder();
    }

    @Override
    public JobSubmitRequestSpecification prepareJobSubmit() {
        return null;
    }

    @Override
    public KillJobRequestSpecification killJob() {
        return new KillJobRequestSpecificationImpl(this);
    }

    @Override
    public JobStatusRequestSpecification checkJobStatus() {
        return new JobStatusRequestSpecificationImpl(this);
    }

    public static class SparkRestClientBuilder {
        private SparkVersion sparkVersion = SparkVersion.V1_5_0;
        private Integer masterPort = 6066;
        private String masterHost;
        private Boolean eventLogEnabled = Boolean.FALSE;
        private Boolean supervise = Boolean.FALSE;
        private ClusterMode clusterMode = ClusterMode.spark;

        private Map<String,String> environmentVariables = Collections.emptyMap();

        private HttpClient client = HttpClientBuilder.create()
                .setConnectionManager(new BasicHttpClientConnectionManager())
                .build();


        private SparkRestClientBuilder() {
        }

        public SparkRestClientBuilder sparkVersion(SparkVersion sparkVersion) {
            this.sparkVersion = sparkVersion;
            return this;
        }

        public SparkRestClientBuilder masterPort(Integer masterPort) {
            this.masterPort = masterPort;
            return this;
        }

        public SparkRestClientBuilder masterHost(String masterHost) {
            this.masterHost = masterHost;
            return this;
        }

        public SparkRestClientBuilder eventLogEnabled(Boolean eventLogEnabled) {
            this.eventLogEnabled = eventLogEnabled;
            return this;
        }

        public SparkRestClientBuilder supervise(Boolean supervise) {
            this.supervise = supervise;
            return this;
        }

        public SparkRestClientBuilder clusterMode(ClusterMode clusterMode) {
            this.clusterMode = clusterMode;
            return this;
        }

        public SparkRestClientBuilder environmentVariables(Map<String, String> environmentVariables) {
            this.environmentVariables = environmentVariables;
            return this;
        }

        public SparkRestClientBuilder httpClient(HttpClient httpClient) {
            this.client = httpClient;
            return this;
        }

        public SparkRestClient build() {
            if (masterHost == null ||
                    masterPort == null) {
                throw new IllegalArgumentException("master host and port must be set.");
            }
            if (client == null) {
                throw new IllegalArgumentException("http client cannot be null.");
            }
            SparkRestClient sparkRestClient = new SparkRestClient();
            sparkRestClient.setSparkVersion(sparkVersion);
            sparkRestClient.setMasterPort(masterPort);
            sparkRestClient.setMasterHost(masterHost);
            sparkRestClient.setEventLogEnabled(eventLogEnabled);
            sparkRestClient.setSupervise(supervise);
            sparkRestClient.setEnvironmentVariables(environmentVariables);
            sparkRestClient.setClient(client);
            sparkRestClient.setClusterMode(clusterMode);
            return sparkRestClient;
        }
    }
}
