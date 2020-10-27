package software.amazon.ec2.networkinsightspath;

import com.google.common.collect.ImmutableList;
import software.amazon.awssdk.services.ec2.model.CreateNetworkInsightsPathRequest;
import software.amazon.awssdk.services.ec2.model.CreateNetworkInsightsPathResponse;
import software.amazon.awssdk.services.ec2.model.DeleteNetworkInsightsPathRequest;
import software.amazon.awssdk.services.ec2.model.DescribeNetworkInsightsPathsRequest;
import software.amazon.awssdk.services.ec2.model.NetworkInsightsPath;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static software.amazon.ec2.networkinsightspath.TagUtils.getTagSpecification;

public class PathFactory {

    private static Map<String, Integer> idMap = new HashMap<>();

    public static int increment(int id) {
        return (id + 1) % 10000;
    }

    public static int nextId(String prefix) {
        return idMap.compute(prefix, (k, v) -> increment(Optional.ofNullable(v).orElse(0)));
    }

    public static String arrangeId(String prefix) {
        return prefix + "-" + nextId(prefix);
    }

    public static String arrangePathId() {
        return arrangeId("nip");
    }

    public static String arrangeNextToken() {
        return arrangeId("nextToken");
    }

    public static String arrangeKey() {
        return arrangeId("key");
    }

    public static String arrangeValue() {
        return arrangeId("value");
    }

    public static String arrangePathArn() {
        return arrangeId("arn");
    }

    public static Instant arrangeCreatedDate() {
        return Instant.MAX;
    }

    public static String arrangeSource() {
        return arrangeId("src");
    }

    public static String arrangeDestination() {
        return arrangeId("dest");
    }

    public static String arrangeSourceIp() {
        return arrangeId("source-ip");
    }

    public static String arrangeDestinationIp() {
        return arrangeId("dest-ip");
    }

    public static Integer arrangeDestinationPort() {
        return 1234;
    }

    public static String arrangeName() {
        return "name";
    }

    public static String arrangeDescription() {
        return "description";
    }

    public static String arrangeProtocol() {
        return "TCP";
    }

    public static software.amazon.ec2.networkinsightspath.Tag arrangeTag() {
        return software.amazon.ec2.networkinsightspath.Tag.builder()
            .key(arrangeKey())
            .value(arrangeValue())
            .build();
    }

    public static  software.amazon.awssdk.services.ec2.model.Tag arrangeEc2Tag() {
        return software.amazon.awssdk.services.ec2.model.Tag.builder()
            .key(arrangeKey())
            .value(arrangeValue())
            .build();
    }

    public static ResourceModel arrangeResourceModel() {
        return ResourceModel.builder()
            .protocol(arrangeProtocol())
            .source(arrangeSource())
            .destination(arrangeDestination())
            .build();
    }

    public static ResourceModel arrangeFullResourceModel() {
        return ResourceModel.builder()
            .networkInsightsPathId(arrangePathId())
            .networkInsightsPathArn(arrangePathArn())
            .createdDate(arrangeCreatedDate().toString())
            .protocol(arrangeProtocol())
            .source(arrangeSource())
            .destination(arrangeDestination())
            .sourceIp(arrangeSourceIp())
            .destinationIp(arrangeDestinationIp())
            .destinationPort(arrangeDestinationPort())
            .name(arrangeName())
            .description(arrangeDescription())
            .tags(ImmutableList.of(arrangeTag()))
            .build();
    }

    public static ResourceModel arrangeResourceModel(final String pathId,
                                                     final String pathArn,
                                                     final String createdDate) {
        return ResourceModel.builder()
            .networkInsightsPathId(pathId)
            .networkInsightsPathArn(pathArn)
            .createdDate(createdDate)
            .build();
    }

    public static ResourceModel arrangeResourceModel(final String pathId) {
        return ResourceModel.builder()
            .networkInsightsPathId(pathId)
            .build();
    }

    public static CreateNetworkInsightsPathResponse arrangeCreatePathResponse() {
        return CreateNetworkInsightsPathResponse.builder()
            .networkInsightsPath(arrangePath())
            .build();
    }

    public static NetworkInsightsPath arrangePath() {
        return NetworkInsightsPath.builder()
            .networkInsightsPathId(arrangePathId())
            .networkInsightsPathArn(arrangePathArn())
            .createdDate(arrangeCreatedDate())
            .build();
    }

    public static DeleteNetworkInsightsPathRequest arrangeDeletePathRequest(final String pathId) {
        return DeleteNetworkInsightsPathRequest.builder()
            .networkInsightsPathId(pathId)
            .build();
    }

    public static CreateNetworkInsightsPathRequest arrangeCreatePathRequest(ResourceHandlerRequest<ResourceModel> request) {
        final ResourceModel createPathModel = request.getDesiredResourceState();
        final Map<String, String> desiredTags = request.getDesiredResourceTags();

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

    public static DescribeNetworkInsightsPathsRequest arrangeDescribePathsRequest(ResourceHandlerRequest<ResourceModel> request) {
        final ResourceModel model = request.getDesiredResourceState();

        return DescribeNetworkInsightsPathsRequest.builder()
            .networkInsightsPathIds(model.getNetworkInsightsPathId())
            .build();
    }

    public static NetworkInsightsPath arrangeNetworkInsightsPath(ResourceModel model) {
        final NetworkInsightsPath.Builder builder = NetworkInsightsPath.builder()
            .networkInsightsPathId(model.getNetworkInsightsPathId())
            .networkInsightsPathArn(model.getNetworkInsightsPathArn())
            .createdDate(Instant.parse(model.getCreatedDate()))
            .sourceIp(model.getSourceIp())
            .destinationIp(model.getDestinationIp())
            .source(model.getSource())
            .destination(model.getDestination())
            .protocol(model.getProtocol())
            .destinationPort(model.getDestinationPort())
            .name(model.getName())
            .description(model.getDescription());
        if (model.getTags() != null) {
            builder.tags(model.getTags().stream().map(tag -> Tag.builder()
                .key(tag.getKey())
                .value(tag.getValue())
                .build()).collect(Collectors.toList()));
        }
        return builder.build();
    }
}
