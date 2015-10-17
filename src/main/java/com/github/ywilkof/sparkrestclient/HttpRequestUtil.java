package com.github.ywilkof.sparkrestclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.BasicResponseHandler;

import java.io.IOException;

public class HttpRequestUtil {

    static <T extends SparkResponse>  T executeHttpMethodAndGetResponse(HttpClient client, HttpRequestBase httpRequest, Class<T> responseClass) throws FailedSparkRequestException {
        T response;
        try {
            final String stringResponse = client.execute(httpRequest, new BasicResponseHandler());
            if (stringResponse != null) {
                response = (T) MapperWrapper.MAPPER.readValue(stringResponse, responseClass);
            } else {
                throw new FailedSparkRequestException("Received empty string response");
            }
        } catch (InvalidFormatException e) {
            throw new FailedSparkRequestException("Spark server responded with different values than expected.");
        } catch (IOException e) {
            throw new FailedSparkRequestException(e);
        } finally {
            httpRequest.releaseConnection();
        }

        if (response == null || !response.getSuccess()) {
            throw new FailedSparkRequestException("Spark master failed executing the request.");
        }

        return response;
    }
}
