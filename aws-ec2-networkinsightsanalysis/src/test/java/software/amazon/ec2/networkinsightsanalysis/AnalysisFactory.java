package software.amazon.ec2.networkinsightsanalysis;

import com.google.common.collect.ImmutableList;
import software.amazon.awssdk.services.ec2.model.StartNetworkInsightsAnalysisRequest;
import software.amazon.awssdk.services.ec2.model.StartNetworkInsightsAnalysisResponse;
import software.amazon.awssdk.services.ec2.model.DeleteNetworkInsightsAnalysisRequest;
import software.amazon.awssdk.services.ec2.model.DescribeNetworkInsightsAnalysesRequest;
import software.amazon.awssdk.services.ec2.model.NetworkInsightsAnalysis;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class AnalysisFactory {

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

    public static String arrangeAnalysisId() {
        return arrangeId("nia");
    }

    public static String arrangePathId() {
        return arrangeId("nip");
    }

    public static ResourceModel arrangeResourceModel() {
        return ResourceModel.builder().networkInsightsPathId(arrangePathId()).build();
    }

    public static ResourceModel arrangeResourceModel(final String analysisId) {
        return ResourceModel.builder()
            .networkInsightsAnalysisId(analysisId)
            .build();
    }

}
