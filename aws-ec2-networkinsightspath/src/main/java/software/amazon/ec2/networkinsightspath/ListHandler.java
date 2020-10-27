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

public class ListHandler extends BaseHandlerStd {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(final AmazonWebServicesClientProxy proxy,
                                                                       final ResourceHandlerRequest<ResourceModel>
                                                                           resourceHandlerRequest,
                                                                       final Ec2Client client,
                                                                       final Logger logger) {
        final String nextToken = resourceHandlerRequest.getNextToken();
        logger.log("Trying to describe paths with next token: " + nextToken);
        DescribeNetworkInsightsPathsRequest describePathsRequest = Translator.translateToListRequest(nextToken);
        try {
            final DescribeNetworkInsightsPathsResponse response = proxy.injectCredentialsAndInvokeV2(describePathsRequest,
                request -> {
                    logger.log("DescribePathsRequest: " + describePathsRequest);
                    final DescribeNetworkInsightsPathsResponse describeResponse = client.describeNetworkInsightsPaths(describePathsRequest);
                    logger.log("DescribePathsResponse: " + describeResponse);

                    return describeResponse;
                });
            logger.log("Paths listed successfully");

            return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .status(OperationStatus.SUCCESS)
                .resourceModels(Translator.translateFromListRequest(response))
                .nextToken(response.nextToken())
                .build();
        } catch (Ec2Exception e) {
            logger.log("Exception: " + e);

            return ProgressEvent.defaultFailureHandler(e,
                Translator.getHandlerError(e.awsErrorDetails().errorCode()));
        }
    }
}

