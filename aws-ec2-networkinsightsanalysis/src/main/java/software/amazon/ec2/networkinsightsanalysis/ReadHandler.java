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

public class ReadHandler extends BaseHandlerStd {

    @Override
    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(final AmazonWebServicesClientProxy proxy,
                                                                          final ResourceHandlerRequest<ResourceModel>
                                                                                  resourceHandlerRequest,
                                                                          final Ec2Client client,
                                                                          final CallbackContext callbackContext,
                                                                          final Logger logger) {
        final ResourceModel model = resourceHandlerRequest.getDesiredResourceState();
        final DescribeNetworkInsightsAnalysesRequest describeRequest = Translator.translateToReadRequest(model);
        DescribeNetworkInsightsAnalysesResponse describeResponse;
        try {
            describeResponse = proxy.injectCredentialsAndInvokeV2(describeRequest,
                request -> {
                    logger.log("DescribeAnalysesRequest: " + request);
                    final DescribeNetworkInsightsAnalysesResponse response =
                            client.describeNetworkInsightsAnalyses(request);
                    logger.log("DescribeAnalysesResponse: " + response);

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
