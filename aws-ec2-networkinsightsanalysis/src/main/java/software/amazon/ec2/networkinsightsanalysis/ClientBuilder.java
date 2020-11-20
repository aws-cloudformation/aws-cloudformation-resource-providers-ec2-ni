package software.amazon.ec2.networkinsightsanalysis;

import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.cloudformation.LambdaWrapper;

import java.net.URI;

public class ClientBuilder {

    public static Ec2Client getClient() {
        return Ec2Client.builder()
            .httpClient(LambdaWrapper.HTTP_CLIENT)
            .build();
    }
}

