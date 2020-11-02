package software.amazon.ec2.networkinsightsanalysis;

import java.util.List;
import java.util.stream.Collectors;

public class TagUtils {

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

}
