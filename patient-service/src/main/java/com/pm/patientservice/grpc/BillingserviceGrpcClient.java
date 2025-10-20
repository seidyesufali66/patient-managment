package com.pm.patientservice.grpc;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class BillingserviceGrpcClient {
    private  final BillingServiceGrpc.BillingServiceBlockingStub blockingStub;;
    public BillingserviceGrpcClient(@Value("${billing.service.address:localhost}") String serverAddress,@Value("${billing.service.grpc.port:9001}") int serverPort) {
     log.info("Billing Service Address: {}:{}",serverAddress,serverPort);
      ManagedChannel channel = ManagedChannelBuilder.forAddress(serverAddress, serverPort).usePlaintext().build();
      blockingStub = BillingServiceGrpc.newBlockingStub(channel);
    }
    public BillingResponse createBillingAccount
            (String patientId, String patientName, String patientEmail) {
        log.info("Creating billing account for patientId: {}, patientName: {}, patientEmail: {}", patientId, patientName, patientEmail);
       BillingRequest request = BillingRequest.newBuilder()
               .setPatientID(patientId)
               .setName(patientName)
               .setEmail(patientEmail)
               .build();
       BillingResponse response = blockingStub.createBillingAccount(request);
       log.info("Received billing account creation response: {}", response);
       return response;
    }
}
