package software.amazon.ec2.networkinsightsanalysis;

import software.amazon.cloudformation.proxy.StdCallbackContext;

@lombok.Getter
@lombok.Setter
@lombok.ToString
@lombok.EqualsAndHashCode(callSuper = true)
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class CallbackContext extends StdCallbackContext {
    private boolean actionStarted;
}
