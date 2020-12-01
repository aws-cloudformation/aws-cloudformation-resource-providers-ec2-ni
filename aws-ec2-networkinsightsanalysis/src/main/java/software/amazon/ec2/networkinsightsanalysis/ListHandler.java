package software.amazon.ec2.networkinsightsanalysis;

import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeNetworkInsightsAnalysesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeNetworkInsightsAnalysesResponse;
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
                                                                       final CallbackContext callbackContext,
                                                                       final Logger logger) {
        final String nextToken = resourceHandlerRequest.getNextToken();
        logger.log("Trying to describe analyses with next token: " + nextToken);
        DescribeNetworkInsightsAnalysesRequest describeAnalysesRequest = Translator.translateToListRequest(nextToken);
        try {
            final DescribeNetworkInsightsAnalysesResponse response = proxy.injectCredentialsAndInvokeV2(describeAnalysesRequest,
                    request -> {
                        logger.log("DescribeAnalysesRequest: " + request);
                        final DescribeNetworkInsightsAnalysesResponse describeResponse = client.describeNetworkInsightsAnalyses(request);
                        logger.log("DescribeAnalysesResponse: " + describeResponse);

                        return describeResponse;
                    });
            logger.log("Analyses listed successfully");

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
