package com.brunodunbar.grpc.sandbox;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.Test;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static org.junit.Assert.*;

/**
 * Created by bruno on 23/04/17.
 */
public class GRpcWrapperTest {


    private RandomString randomString = new RandomString(255);
    private Gson gson = new Gson();
    private Random random = new Random();
    private Type requestListType = new TypeToken<List<ExtracaoRequest>>() {
    }.getType();

    @Test
    public void testSize() throws IOException {
        int totalJsonSize = 0;
        int totalRpcSize = 0;

        int totalJsonWriteTime = 0;
        int totalRpcWriteTime = 0;
        
        int totalJsonReadTime = 0;
        int totalRpcReadTime = 0;

        for (int i = 0; i < 100000; i++) {
            List<ExtracaoRequest> requests = createRandom();

            Result rpcResult = testRpc(requests);
            Result jsonResult = testJson(requests);
            
            totalJsonSize += jsonResult.getSize();
            totalJsonWriteTime += jsonResult.getWriteTime();
            totalJsonReadTime += jsonResult.getReadTime();
            
            totalRpcSize += rpcResult.getSize();
            totalRpcWriteTime += rpcResult.getWriteTime();
            totalRpcReadTime += rpcResult.getReadTime();
            
        }
        
        System.out.println("Json Write Time: " + totalJsonWriteTime+ ", RPC Write Time: " + totalRpcWriteTime);
        System.out.println("Json Read Time: " + totalJsonReadTime+ ", RPC Read Time: " + totalRpcReadTime);
        System.out.println("Json Size: " + totalJsonSize + ", RPC Size: " + totalRpcSize);
    }


    private Result testRpc(List<ExtracaoRequest> requests) throws IOException {
        long startTime = System.currentTimeMillis();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream);

        Services.ExtracaoRequests.Builder extracaoRequestsBuilder = Services.ExtracaoRequests.newBuilder();

        for (ExtracaoRequest extracaoRequest : requests) {
            extracaoRequestsBuilder.addRequests(GRpcWrapper.wrap(extracaoRequest));
        }

        Services.ExtracaoRequests extracaoRequests = extracaoRequestsBuilder.build();
        extracaoRequests.writeTo(gzipOutputStream);

        gzipOutputStream.finish();
        long writeTime = System.currentTimeMillis() - startTime;

        startTime = System.currentTimeMillis();

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);

        List<ExtracaoRequest> parsedRequests = Services.ExtracaoRequests.parseFrom(gzipInputStream)
                .getRequestsList().stream().map(GRpcWrapper::unwrap)
                .collect(Collectors.toList());

        assertEquals(requests.size(), parsedRequests.size());

        long readTime = System.currentTimeMillis() - startTime;

        return new Result(writeTime, readTime, outputStream.toByteArray().length);
    }

    private Result testJson(List<ExtracaoRequest> requests) throws IOException {
        long startTime = System.currentTimeMillis();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream);

        gson.toJson(requests, new PrintStream(gzipOutputStream));
        gzipOutputStream.finish();

        long writeTime = System.currentTimeMillis() - startTime;


        startTime = System.currentTimeMillis();

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);

        List<ExtracaoRequest> parsed = gson.fromJson
                (new InputStreamReader(gzipInputStream), requestListType);

        assertEquals(requests.size(), parsed.size());

        long readTime = System.currentTimeMillis() - startTime;

        return new Result(writeTime, readTime, outputStream.toByteArray().length);
    }

    private static class Result {
        long writeTime;
        long readTime;
        int size;

        public Result(long writeTime, long readTime, int size) {
            this.writeTime = writeTime;
            this.readTime = readTime;
            this.size = size;
        }

        public long getWriteTime() {
            return writeTime;
        }

        public long getReadTime() {
            return readTime;
        }

        public int getSize() {
            return size;
        }
    }

    private List<ExtracaoRequest> createRandom() {

        List<ExtracaoRequest> requests = new ArrayList<>();
        int max = random.nextInt(20);
        for (int i = 0; i < max; i++) {
            requests.add(new ExtracaoRequest.Builder().id((long) i).addValues(createRandomValues()).build());
        }
        return requests;
    }

    private List<Object> createRandomValues() {
        List<Object> objects = new ArrayList<>();
        int max = random.nextInt(20);
        for (int i = 0; i < max; i++) {
            objects.add(createRandomValue());
        }
        return objects;
    }

    private Object createRandomValue() {

        Random random = new Random();
        int value = random.nextInt(Services.ValueType.DOUBLE_VALUE);
        switch (Services.ValueType.forNumber(value)) {
            case NULL:
                return null;
            case STRING:
                return randomString.nextString();
            case INTEGER:
                return random.nextInt();
            case LONG:
                return random.nextLong();
            case FLOAT:
                return random.nextFloat();
            case DOUBLE:
                return random.nextDouble();
        }

        return null;
    }


}