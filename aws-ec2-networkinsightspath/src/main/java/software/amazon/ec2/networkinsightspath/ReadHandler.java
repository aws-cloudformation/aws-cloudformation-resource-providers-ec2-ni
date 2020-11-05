package software.amazon.ec2.networkinsightspath;

import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeNetworkInsightsPathsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeNetworkInsightsPathsResponse;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class ReadHandler extends BaseHandlerStd {

    @Override
    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(final AmazonWebServicesClientProxy proxy,
                                                                          final ResourceHandlerRequest<ResourceModel>
                                                                              resourceHandlerRequest,
                                                                          final Ec2Client client,
                                                                          final Logger logger) {
        final ResourceModel model = resourceHandlerRequest.getDesiredResourceState();
        final DescribeNetworkInsightsPathsRequest describeRequest = Translator.translateToReadRequest(model);
        DescribeNetworkInsightsPathsResponse describeResponse;
        try {
            describeResponse = proxy.injectCredentialsAndInvokeV2(describeRequest,
                request -> {
                    logger.log("DescribePathsRequest: " + request);
                    final DescribeNetworkInsightsPathsResponse response = client.describeNetworkInsightsPaths(request);
                    logger.log("DescribePathsResponse: " + response);

                    return response;
                });
            logger.log(String.format("%s read succeeded", model.getPrimaryIdentifier()));

            return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModel(Translator.translateFromReadResponse(describeResponse))
                .status(OperationStatus.SUCCESS)
                .build();
        } catch (Ec2Exception e) {
            logger.log("Exception: " + e);

            return ProgressEvent.defaultFailureHandler(e,
                    Translator.getHandlerError(e.awsErrorDetails().errorCode()));
        }
    }
}
