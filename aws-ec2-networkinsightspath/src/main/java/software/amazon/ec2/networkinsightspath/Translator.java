package software.amazon.ec2.networkinsightspath;

import software.amazon.awssdk.services.ec2.model.CreateNetworkInsightsPathRequest;
import software.amazon.awssdk.services.ec2.model.CreateTagsRequest;
import software.amazon.awssdk.services.ec2.model.DeleteNetworkInsightsPathRequest;
import software.amazon.awssdk.services.ec2.model.DeleteTagsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeNetworkInsightsPathsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeNetworkInsightsPathsResponse;
import software.amazon.awssdk.services.ec2.model.NetworkInsightsPath;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static software.amazon.ec2.networkinsightspath.TagUtils.convertTags;
import static software.amazon.ec2.networkinsightspath.TagUtils.getTagSpecification;

/**
 * This class is a centralized placeholder for
 * - api request construction
 * - object translation to/from aws sdk
 * - resource model construction for read/list handlers
 */

public class Translator {

    static CreateNetworkInsightsPathRequest translateToCreateRequest(final ResourceHandlerRequest<ResourceModel> request) {
        final ResourceModel createPathModel = request.getDesiredResourceState();
        Map<String, String> desiredTags = request.getDesiredResourceTags();
        final CreateNetworkInsightsPathRequest.Builder createPathRequestBuilder = CreateNetworkInsightsPathRequest.builder()
            .sourceIp(createPathModel.getSourceIp())
            .destinationIp(createPathModel.getDestinationIp())
            .source(createPathModel.getSource())
            .destination(createPathModel.getDestination())
            .protocol(createPathModel.getProtocol())
            .destinationPort(createPathModel.getDestinationPort())
            .name(createPathModel.getName())
            .description(createPathModel.getDescription());
        if (desiredTags != null && !desiredTags.isEmpty()) {
            createPathRequestBuilder.tagSpecifications(getTagSpecification(desiredTags));
        }

        return createPathRequestBuilder.build();
    }

    static DescribeNetworkInsightsPathsRequest translateToReadRequest(final ResourceModel model) {
        return DescribeNetworkInsightsPathsRequest.builder()
            .networkInsightsPathIds(model.getNetworkInsightsPathId())
            .build();
    }

    static ResourceModel translateFromReadResponse(final DescribeNetworkInsightsPathsResponse response) {
        return Translator.translateFromPathToModel(response.networkInsightsPaths().get(0));
    }

    static DeleteNetworkInsightsPathRequest translateToDeleteRequest(final ResourceModel model) {
        return DeleteNetworkInsightsPathRequest.builder()
            .networkInsightsPathId(model.getNetworkInsightsPathId())
            .build();
    }

    static DescribeNetworkInsightsPathsRequest translateToListRequest(final String nextToken) {
        return DescribeNetworkInsightsPathsRequest.builder()
            .nextToken(nextToken)
            .build();
    }

    static List<ResourceModel> translateFromListRequest(final DescribeNetworkInsightsPathsResponse response) {
        return response.networkInsightsPaths()
            .stream()
            .map(Translator::translateFromPathToModel)
            .collect(Collectors.toList());
    }

    static CreateTagsRequest translateToCreateTagsRequest(List<Tag> tags, String pathId) {
        return CreateTagsRequest.builder()
            .resources(pathId)
            .tags(tags)
            .build();
    }

    static DeleteTagsRequest translateToDeleteTagsRequest(List<Tag> tags, String pathId) {
        return DeleteTagsRequest.builder()
            .resources(pathId)
            .tags(tags)
            .build();
    }

    //TODO: Add exceptions related to adding and deleting tags
    static HandlerErrorCode getHandlerError(final String errorCode) {
        switch (errorCode) {
            case "NetworkInsightsSource.NotFound":
            case "NetworkInsightsDestination.NotFound":
            case "MissingParameterException":
            case "InvalidParameterCombination":
            case "InvalidParameterValue":
            case "TagPolicyViolation":
            case "IdempotentParameterMismatch":
            case "AnalysisExistsForNetworkInsightsPath":
                return HandlerErrorCode.InvalidRequest;
            case "NetworkInsightsPath.LimitExceeded":
                //  https://docs.aws.amazon.com/AWSEC2/latest/APIReference/query-api-troubleshooting.html
            case "RequestLimitExceeded":
                // thrown by EC2 Xino: https://w.amazon.com/index.php/EC2/Throttling/Customer_Support_Guide#How%20would%20a%20customer%20know%20he%20is%20being%20throttled?
            case "Client.RequestLimitExceeded":
                return HandlerErrorCode.Throttling;
            case "InvalidNetworkInsightsPathId.NotFound":
                return HandlerErrorCode.NotFound;
            case "UnauthorizedOperation":
            case "Client.UnauthorizedOperation":
                return HandlerErrorCode.AccessDenied;
            case "Unavailable":
            case "InternalError":
                return HandlerErrorCode.ServiceInternalError;
            default:
                return HandlerErrorCode.GeneralServiceException;
        }
    }

    private static ResourceModel translateFromPathToModel(NetworkInsightsPath path) {
        final ResourceModel.ResourceModelBuilder builder = ResourceModel.builder()
            .networkInsightsPathId(path.networkInsightsPathId())
            .networkInsightsPathArn(path.networkInsightsPathArn())
            .createdDate(path.createdDate().toString())
            .sourceIp(path.sourceIp())
            .destinationIp(path.destinationIp())
            .source(path.source())
            .destination(path.destination())
            .protocol(path.protocolAsString())
            .destinationPort(path.destinationPort())
            .name(path.name())
            .description(path.description());
        if (path.tags() != null && !path.tags().isEmpty()) {
            builder.tags(convertTags(path.tags()));
        } else {
            builder.tags(null);
        }
        return builder.build();
    }
}