package com.pm.billingservice.grpc;

import billing.BillingResponse;
import billing.BillingServiceGrpc.BillingServiceImplBase;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@GrpcService
public class BillingGrpcService  extends BillingServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(BillingGrpcService.class);
    @Override
    public void createBillingAccount( billing.BillingRequest request,
                                     StreamObserver<billing.BillingResponse> responseObserver) {
        log.info("Create Billing Account request received {}", request.toString());
        BillingResponse response = BillingResponse.newBuilder()
                .setAccountID("12345")
                .setStatus("ACCOUNT ACTIVE")
                .build();
        try {
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error occurred while creating billing account: {}", e.getMessage());
            responseObserver.onError(e);
        }
    }
}