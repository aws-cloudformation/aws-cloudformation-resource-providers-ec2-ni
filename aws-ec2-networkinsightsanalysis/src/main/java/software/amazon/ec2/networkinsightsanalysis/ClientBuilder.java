package software.amazon.ec2.networkinsightsanalysis;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.cloudformation.LambdaWrapper;
import software.amazon.cloudformation.proxy.Logger;

public class ClientBuilder {

    public static Ec2Client getClient(final Logger logger) {
        String regionString = System.getenv("AWS_REGION");
        final Region region = Region.of(regionString);
        logger.log("Building Ec2 client in " + region);

        return Ec2Client.builder()
            .region(region)
            .httpClient(LambdaWrapper.HTTP_CLIENT)
            .build();
    }
}


