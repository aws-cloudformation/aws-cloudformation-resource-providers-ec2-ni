package software.amazon.ec2.networkinsightsanalysis;

import com.google.common.collect.Streams;
import software.amazon.awssdk.services.ec2.model.TagSpecification;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TagUtils {

    public static TagSpecification getTagSpecification(Map<String, String> desiredTags) {
        return TagSpecification.builder()
                // TODO: Update ResourceType when the tagging is supported: https://sim.amazon.com/issues/34189e31-4d41-40d8-b195-6e5d542bc301
//            .resourceType(ResourceType.DHCP_OPTIONS)
                .resourceType("network-insights-analysis")
                .tags(desiredTags.keySet().stream()
                        .map(key -> software.amazon.awssdk.services.ec2.model.Tag.builder()
                                .key(key)
                                .value(desiredTags.get(key))
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    public static List<Tag> convertTags(List<software.amazon.awssdk.services.ec2.model.Tag> tags) {
        if (tags == null || tags.isEmpty()) {
            return null;
        } else {
            return tags.stream()
                    .map(tag -> software.amazon.ec2.networkinsightsanalysis.Tag.builder()
                            .key(tag.key())
                            .value(tag.value())
                            .build())
                    .collect(Collectors.toList());
        }
    }

    public static List<software.amazon.awssdk.services.ec2.model.Tag> getTagsToAdd(Map<String, String> tags,
                                                                                   List<String> tagsToUpdate,
                                                                                   List<String> tagsToAdd) {
        return Streams.concat(tagsToAdd.stream(), tagsToUpdate.stream())
                .map(tag -> software.amazon.awssdk.services.ec2.model.Tag.builder()
                        .key(tag)
                        .value(tags.get(tag))
                        .build())
                .collect(Collectors.toList());
    }

    public static List<software.amazon.awssdk.services.ec2.model.Tag> getTagsToDelete(Map<String, String> tags,
                                                                                      List<String> tagsToDelete) {
        return tagsToDelete.stream()
                .map(tag -> software.amazon.awssdk.services.ec2.model.Tag.builder()
                        .key(tag)
                        .value(tags.get(tag))
                        .build())
                .collect(Collectors.toList());
    }
}
