package software.amazon.ec2.networkinsightsanalysis;

import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
import software.amazon.cloudformation.proxy.Credentials;
import software.amazon.cloudformation.proxy.LoggerProxy;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.function.Supplier;

public class AbstractTestBase {

  protected static final LoggerProxy logger;

  public Credentials credentials = new Credentials("accessKeyId", "secretAccessKey", "sessionToken");;
  public Supplier<Long> remainingTimeSupplier = () -> 2L;

  static {
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
