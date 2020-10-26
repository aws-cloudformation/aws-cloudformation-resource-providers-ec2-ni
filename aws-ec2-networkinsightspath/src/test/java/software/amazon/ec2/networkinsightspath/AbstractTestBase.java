package software.amazon.ec2.networkinsightspath;

import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
import software.amazon.cloudformation.proxy.Credentials;
import software.amazon.cloudformation.proxy.LoggerProxy;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class AbstractTestBase {
    protected static final Credentials MOCK_CREDENTIALS;
    protected static final LoggerProxy logger;

    static {
        MOCK_CREDENTIALS = new Credentials("accessKey", "secretKey", "token");
        logger = new LoggerProxy();
    }

    ResourceHandlerRequest<ResourceModel> arrangeResourceHandlerRequest(ResourceModel resourceModel) {
        return ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(resourceModel)
            .build();
    }

    AwsServiceException arrangeException(String errorCode) {
        return Ec2Exception.builder()
            .awsErrorDetails(AwsErrorDetails.builder()
                .errorCode(errorCode)
                .errorMessage("some message")
                .build())
            .build();
    }
}
