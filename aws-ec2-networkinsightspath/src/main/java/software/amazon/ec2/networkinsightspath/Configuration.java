package software.amazon.ec2.networkinsightspath;

import software.amazon.awssdk.utils.CollectionUtils;

import java.util.Map;
import java.util.stream.Collectors;

class Configuration extends BaseConfiguration {

    public Configuration() {
        super("aws-ec2-networkinsightspath.json");
    }

    // need to override this method so getDesiredResourceTags method on ResourceHandlerRequest returns both the
    // stack tags and resource tags combined
    @Override
    public Map<String, String> resourceDefinedTags(final ResourceModel resourceModel) {
        if (CollectionUtils.isNullOrEmpty(resourceModel.getTags()))
            return null;

        return resourceModel.getTags().stream()
            .collect(Collectors.toMap(Tag::getKey, Tag::getValue));
    }
}
