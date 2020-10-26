package software.amazon.ec2.networkinsightspath;

import software.amazon.awssdk.services.ec2.model.CreateNetworkInsightsPathRequest;
import software.amazon.awssdk.services.ec2.model.CreateNetworkInsightsPathResponse;
import software.amazon.awssdk.services.ec2.model.NetworkInsightsPath;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    public static String arrangeDestinationIp() {
        return arrangeId("dest-ip");
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

    public static ResourceModel arrangeResourceModel() {
        return ResourceModel.builder()
            .protocol(arrangeProtocol())
            .source(arrangeSource())
            .description(arrangeDestination())
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

    public static CreateNetworkInsightsPathRequest arrangeCreatePathRequest(ResourceHandlerRequest<ResourceModel> request) {
        final ResourceModel model = request.getDesiredResourceState();
        final Map<String, String> tags = request.getDesiredResourceTags();

        final CreateNetworkInsightsPathRequest.Builder builder = CreateNetworkInsightsPathRequest.builder()
            .sourceIp(model.getSourceIp())
            .destinationIp(model.getDestinationIp())
            .source(model.getSource())
            .destination(model.getDestination())
            .protocol(model.getProtocol())
            .destinationPort(model.getDestinationPort())
            .name(model.getName())
            .description(model.getDescription());
        if (tags != null && !tags.isEmpty()) {
            builder.tagSpecifications(getTagSpecification(tags));
        }

        return builder.build();
    }
}
