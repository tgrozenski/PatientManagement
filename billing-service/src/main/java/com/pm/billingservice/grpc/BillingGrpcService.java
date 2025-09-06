package com.pm.billingservice.grpc;

import billing.BillingResponse;
import billing.BillingRequest;
import billing.BillingServiceGrpc.BillingServiceImplBase;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class BillingGrpcService extends BillingServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(BillingGrpcService.class);

    @Override
    public void createBillingAccount(BillingRequest billingRequest,
                                     StreamObserver<BillingResponse> responseObserver) {
        log.info("CreatingAccount request received {}", billingRequest.toString());

        //Business logic would go here, save to database, perform calculations, ect.
        BillingResponse billingResponse = BillingResponse.newBuilder()
                .setAccountId("1234")
                .setStatus("Active")
                .build();

        responseObserver.onNext(billingResponse);
        responseObserver.onCompleted();
    }
}
