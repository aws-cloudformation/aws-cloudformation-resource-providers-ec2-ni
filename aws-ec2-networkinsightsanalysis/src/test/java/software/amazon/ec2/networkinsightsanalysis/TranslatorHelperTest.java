package software.amazon.ec2.networkinsightsanalysis;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.ec2.model.Protocol;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TranslatorHelperTest {


    @Test
    public void translatePathComponentExpectSuccess() {
        software.amazon.awssdk.services.ec2.model.PathComponent input =
                software.amazon.awssdk.services.ec2.model.PathComponent.builder()
                        .aclRule(software.amazon.awssdk.services.ec2.model.AclRule.builder().ruleNumber(1).build())
                        .component(software.amazon.awssdk.services.ec2.model.Component.builder().id("comp").build())
                        .destinationVpc(software.amazon.awssdk.services.ec2.model.Component.builder().id("dest").build())
                        .inboundHeader(software.amazon.awssdk.services.ec2.model.Header.builder().destinationAddresses(ImmutableList.of("1.2.3.4")).build())
                        .outboundHeader(software.amazon.awssdk.services.ec2.model.Header.builder().destinationAddresses(ImmutableList.of("5.6.7.8")).build())
                        .routeTableRoute(software.amazon.awssdk.services.ec2.model.RouteTableRoute.builder().instanceId("i-1").build())
                        .securityGroupRule(software.amazon.awssdk.services.ec2.model.SecurityGroupRule.builder().cidr("cidr").build())
                        .sourceVpc(software.amazon.awssdk.services.ec2.model.Component.builder().id("src").build())
                        .subnet(software.amazon.awssdk.services.ec2.model.Component.builder().id("subnet").build())
                        .vpc(software.amazon.awssdk.services.ec2.model.Component.builder().id("vpc").build())
                        .build();

        PathComponent expected = PathComponent.builder()
                .aclRule(AclRule.builder().ruleNumber(1).build())
                .component(Component.builder().id("comp").build())
                .destinationVpc(Component.builder().id("dest").build())
                .inboundHeader(Header.builder().destinationAddresses(ImmutableList.of("1.2.3.4")).build())
                .outboundHeader(Header.builder().destinationAddresses(ImmutableList.of("5.6.7.8")).build())
                .routeTableRoute(RouteTableRoute.builder().instanceId("i-1").build())
                .securityGroupRule(SecurityGroupRule.builder().cidr("cidr").build())
                .sourceVpc(Component.builder().id("src").build())
                .subnet(Component.builder().id("subnet").build())
                .vpc(Component.builder().id("vpc").build())
                .build();

        PathComponent actual = TranslatorHelper.translatePathComponent(input);

        assertEquals(expected, actual);
    }

    @Test
    public void translatePathComponentGivenEmptyExpectSuccess() {
        software.amazon.awssdk.services.ec2.model.PathComponent input =
                software.amazon.awssdk.services.ec2.model.PathComponent.builder().build();

        PathComponent expected = PathComponent.builder().build();

        PathComponent actual = TranslatorHelper.translatePathComponent(input);

        assertEquals(expected, actual);
    }

    @Test
    public void translatePathComponentGivenNullExpectNull() {
        assertEquals(null, TranslatorHelper.translatePathComponent(null));
    }

    @Test
    public void translateExplanationExpectSuccess() {
        software.amazon.awssdk.services.ec2.model.Explanation input =
                software.amazon.awssdk.services.ec2.model.Explanation.builder()
                        .acl(software.amazon.awssdk.services.ec2.model.Component.builder().id("acl-id").build())
                        .aclRule(software.amazon.awssdk.services.ec2.model.AclRule.builder().ruleNumber(1).build())
                        .address("1.2.3.4")
                        .addresses(ImmutableList.of("1.2.3.4", "4.3.2.1"))
                        .attachedTo(
                                software.amazon.awssdk.services.ec2.model.Component.builder().id("attached-id").build())
                        .availabilityZones(ImmutableList.of("us-test-1a", "us-test-2b"))
                        .cidrs(ImmutableList.of("cidr1", "cidr2"))
                        .classicLoadBalancerListener(
                                software.amazon.awssdk.services.ec2.model.LoadBalancerListener.builder()
                                        .loadBalancerPort(2).build())
                        .component(software.amazon.awssdk.services.ec2.model.Component.builder().id("comp-id").build())
                        .customerGateway(
                                software.amazon.awssdk.services.ec2.model.Component.builder().id("cgw-id").build())
                        .destination(
                                software.amazon.awssdk.services.ec2.model.Component.builder().id("dest-id").build())
                        .destinationVpc(
                                software.amazon.awssdk.services.ec2.model.Component.builder().id("dest-vpc").build())
                        .direction("direction")
                        .elasticLoadBalancerListener(
                                software.amazon.awssdk.services.ec2.model.Component.builder().id("elb-listener")
                                        .build())
                        .explanationCode("explanation-code")
                        .ingressRouteTable(
                                software.amazon.awssdk.services.ec2.model.Component.builder().id("rtb-id").build())
                        .internetGateway(
                                software.amazon.awssdk.services.ec2.model.Component.builder().id("igw-id").build())
                        .loadBalancerArn("elb-arn")
                        .loadBalancerListenerPort(3)
                        .loadBalancerTarget(
                                software.amazon.awssdk.services.ec2.model.LoadBalancerTarget.builder().port(4).build())
                        .loadBalancerTargetGroup(
                                software.amazon.awssdk.services.ec2.model.Component.builder().id("lb-target-group-1")
                                        .build())
                        .loadBalancerTargetGroups(ImmutableList.of(
                                software.amazon.awssdk.services.ec2.model.Component.builder().id("lb-target-group-1")
                                        .build(),
                                software.amazon.awssdk.services.ec2.model.Component.builder().id("lb-target-group-2")
                                        .build()))
                        .loadBalancerTargetPort(5)
                        .missingComponent("missing-comp")
                        .natGateway(software.amazon.awssdk.services.ec2.model.Component.builder().id("ngw-id").build())
                        .networkInterface(software.amazon.awssdk.services.ec2.model.Component.builder().id("eni-id")
                                .build())
                        .packetField("packet-field")
                        .port(6)
                        .portRanges(ImmutableList.of(
                                software.amazon.awssdk.services.ec2.model.PortRange.builder().from(7).build(),
                                software.amazon.awssdk.services.ec2.model.PortRange.builder().from(8).build()))
                        .prefixList(software.amazon.awssdk.services.ec2.model.Component.builder().id("prefix-list-id")
                                .build())
                        .protocols(ImmutableList.of(Protocol.UDP, Protocol.TCP))
                        .routeTable(software.amazon.awssdk.services.ec2.model.Component.builder().id("rtb-id").build())
                        .routeTableRoute(software.amazon.awssdk.services.ec2.model.RouteTableRoute.builder()
                                .origin("origin").build())
                        .securityGroup(software.amazon.awssdk.services.ec2.model.Component.builder().id("sg-1").build())
                        .securityGroupRule(software.amazon.awssdk.services.ec2.model.SecurityGroupRule.builder()
                                .cidr("cidr").build())
                        .securityGroups(ImmutableList.of(
                                software.amazon.awssdk.services.ec2.model.Component.builder().id("sg-1").build(),
                                software.amazon.awssdk.services.ec2.model.Component.builder().id("sg-2").build()))
                        .sourceVpc(software.amazon.awssdk.services.ec2.model.Component.builder().id("src-vpc").build())
                        .state("state")
                        .subnet(software.amazon.awssdk.services.ec2.model.Component.builder().id("subnet-id").build())
                        .subnetRouteTable(software.amazon.awssdk.services.ec2.model.Component.builder().id("subnet-rtb")
                                .build())
                        .vpc(software.amazon.awssdk.services.ec2.model.Component.builder().id("vpc-id").build())
                        .vpcEndpoint(software.amazon.awssdk.services.ec2.model.Component.builder().id("vpc-endpoint")
                                .build())
                        .vpcPeeringConnection(software.amazon.awssdk.services.ec2.model.Component.builder().id("pcx-id")
                                .build())
                        .vpnConnection(software.amazon.awssdk.services.ec2.model.Component.builder().id("vpnc-id")
                                .build())
                        .vpnGateway(software.amazon.awssdk.services.ec2.model.Component.builder().id("vgw-id").build())
                        .build();

        Explanation expected = Explanation.builder()
                .acl(Component.builder().id("acl-id").build())
                .aclRule(AclRule.builder().ruleNumber(1).build())
                .address("1.2.3.4")
                .addresses(ImmutableList.of("1.2.3.4", "4.3.2.1"))
                .attachedTo(Component.builder().id("attached-id").build())
                .availabilityZones(ImmutableList.of("us-test-1a", "us-test-2b"))
                .cidrs(ImmutableList.of("cidr1", "cidr2"))
                .classicLoadBalancerListener(LoadBalancerListener.builder().loadBalancerPort(2).build())
                .component(Component.builder().id("comp-id").build())
                .customerGateway(Component.builder().id("cgw-id").build())
                .destination(Component.builder().id("dest-id").build())
                .destinationVpc(Component.builder().id("dest-vpc").build())
                .direction("direction")
                .elasticLoadBalancerListener(Component.builder().id("elb-listener").build())
                .explanationCode("explanation-code")
                .ingressRouteTable(Component.builder().id("rtb-id").build())
                .internetGateway(Component.builder().id("igw-id").build())
                .loadBalancerArn("elb-arn")
                .loadBalancerListenerPort(3)
                .loadBalancerTarget(LoadBalancerTarget.builder().port(4).build())
                .loadBalancerTargetGroup(Component.builder().id("lb-target-group-1").build())
                .loadBalancerTargetGroups(ImmutableList.of(Component.builder().id("lb-target-group-1").build(),
                        Component.builder().id("lb-target-group-2").build()))
                .loadBalancerTargetPort(5)
                .missingComponent("missing-comp")
                .natGateway(Component.builder().id("ngw-id").build())
                .networkInterface(Component.builder().id("eni-id").build())
                .packetField("packet-field")
                .port(6)
                .portRanges(ImmutableList.of(PortRange.builder().from(7).build(),
                        PortRange.builder().from(8).build()))
                .prefixList(Component.builder().id("prefix-list-id").build())
                .protocols(ImmutableList.of(Protocol.UDP.toString(), Protocol.TCP.toString()))
                .routeTable(Component.builder().id("rtb-id").build())
                .routeTableRoute(RouteTableRoute.builder().origin("origin").build())
                .securityGroup(Component.builder().id("sg-1").build())
                .securityGroupRule(SecurityGroupRule.builder().cidr("cidr").build())
                .securityGroups(ImmutableList.of(Component.builder().id("sg-1").build(),
                        Component.builder().id("sg-2").build()))
                .sourceVpc(Component.builder().id("src-vpc").build())
                .state("state")
                .subnet(Component.builder().id("subnet-id").build())
                .subnetRouteTable(Component.builder().id("subnet-rtb").build())
                .vpc(Component.builder().id("vpc-id").build())
                .vpcEndpoint(Component.builder().id("vpc-endpoint").build())
                .vpcPeeringConnection(Component.builder().id("pcx-id").build())
                .vpnConnection(Component.builder().id("vpnc-id").build())
                .vpnGateway(Component.builder().id("vgw-id").build())
                .build();

        Explanation actual = TranslatorHelper.translateExplanation(input);

        assertEquals(expected, actual);
    }

    @Test
    public void translateExplanationGivenEmptyExpectSuccess() {
        software.amazon.awssdk.services.ec2.model.Explanation input =
                software.amazon.awssdk.services.ec2.model.Explanation.builder().build();

        Explanation expected = Explanation.builder().build();

        Explanation actual = TranslatorHelper.translateExplanation(input);

        assertEquals(expected, actual);
    }

    @Test
    public void translateExplanationGivenNullExpectNull() {
        assertEquals(null, TranslatorHelper.translateExplanation(null));
    }

    @Test
    public void translateAlternatePathHintExpectSuccess() {
        software.amazon.awssdk.services.ec2.model.AlternatePathHint alternatePathHint =
                software.amazon.awssdk.services.ec2.model.AlternatePathHint.builder()
                        .componentArn("component-arn")
                        .componentId("component-id")
                        .build();

        AlternatePathHint expected = AlternatePathHint.builder()
                .componentArn("component-arn")
                .componentId("component-id")
                .build();

        AlternatePathHint actual = TranslatorHelper.translateAlternatePathHint(alternatePathHint);

        assertEquals(expected, actual);
    }

    @Test
    public void translateAlternatePathHintGivenEmptyExpectSuccess() {
        software.amazon.awssdk.services.ec2.model.AlternatePathHint alternatePathHint =
                software.amazon.awssdk.services.ec2.model.AlternatePathHint.builder().build();

        AlternatePathHint expected = AlternatePathHint.builder().build();

        AlternatePathHint actual = TranslatorHelper.translateAlternatePathHint(alternatePathHint);

        assertEquals(expected, actual);
    }

    @Test
    public void translateAlternatePathHintGivenNullExpectNull() {
        assertEquals(null, TranslatorHelper.translateAlternatePathHint(null));
    }

    @Test
    public void translateStringListExpectSuccess() {
        List<String> input = ImmutableList.of("1", "2");
        List<String> expected = ImmutableList.of("1", "2");
        List<String> actual = TranslatorHelper.translateStringList(input);

        assertEquals(expected, actual);
    }

    @Test
    public void translateStringListGivenEmptyExpectNull() {
        assertEquals(null, TranslatorHelper.translateStringList(ImmutableList.of()));
    }

    @Test
    public void translateStringListGivenNullExpectNull() {
        assertEquals(null, TranslatorHelper.translateStringList(null));
    }

    @Test
    public void translateAclRuleExpectSuccess() {
        software.amazon.awssdk.services.ec2.model.AclRule input =
                software.amazon.awssdk.services.ec2.model.AclRule.builder()
                        .build();

        AclRule expected = AclRule.builder()
                .build();

        AclRule actual = TranslatorHelper.translateAclRule(input);

        assertEquals(expected, actual);
    }

    @Test
    public void translateAclRuleGivenEmptyExpectSuccess() {
        software.amazon.awssdk.services.ec2.model.AclRule input =
                software.amazon.awssdk.services.ec2.model.AclRule.builder().build();

        AclRule expected = AclRule.builder().build();

        AclRule actual = TranslatorHelper.translateAclRule(input);

        assertEquals(expected, actual);
    }

    @Test
    public void translateAclRuleGivenNullExpectNull() {
        assertEquals(null, TranslatorHelper.translateAclRule(null));
    }

    @Test
    public void translatePortRangeExpectSuccess() {
        software.amazon.awssdk.services.ec2.model.PortRange input =
                software.amazon.awssdk.services.ec2.model.PortRange.builder()
                        .from(0)
                        .to(1)
                        .build();


        PortRange expected = PortRange.builder()
                .from(0)
                .to(1)
                .build();

        PortRange actual = TranslatorHelper.translatePortRange(input);

        assertEquals(expected, actual);
    }

    @Test
    public void translatePortRangeGivenEmptyExpectSuccess() {
        software.amazon.awssdk.services.ec2.model.PortRange input =
                software.amazon.awssdk.services.ec2.model.PortRange.builder().build();

        PortRange expected = PortRange.builder().build();

        PortRange actual = TranslatorHelper.translatePortRange(input);

        assertEquals(expected, actual);
    }

    @Test
    public void translatePortRangeGivenNullExpectNull() {
        assertEquals(null, TranslatorHelper.translatePortRange(null));
    }

    @Test
    public void translateComponentExpectSuccess() {
        software.amazon.awssdk.services.ec2.model.Component input =
                software.amazon.awssdk.services.ec2.model.Component.builder()
                        .id("id")
                        .arn("arn")
                        .build();

        Component expected = Component.builder()
                .id("id")
                .arn("arn")
                .build();

        Component actual = TranslatorHelper.translateComponent(input);

        assertEquals(expected, actual);
    }

    @Test
    public void translateComponentGivenEmptyExpectSuccess() {
        software.amazon.awssdk.services.ec2.model.Component input =
                software.amazon.awssdk.services.ec2.model.Component.builder().build();

        Component expected = Component.builder().build();

        Component actual = TranslatorHelper.translateComponent(input);

        assertEquals(expected, actual);
    }

    @Test
    public void translateComponentGivenNullExpectNull() {
        assertEquals(null, TranslatorHelper.translateComponent(null));
    }

    @Test
    public void translateHeaderExpectSuccess() {
        software.amazon.awssdk.services.ec2.model.Header input =
                software.amazon.awssdk.services.ec2.model.Header.builder()
                        .protocol(Protocol.UDP)
                        .destinationAddresses(ImmutableList.of("1.2.3.4"))
                        .destinationPortRanges(ImmutableList.of(
                                software.amazon.awssdk.services.ec2.model.PortRange.builder().from(0).build()))
                        .sourceAddresses(ImmutableList.of("5.6.7.8"))
                        .sourcePortRanges(ImmutableList.of(
                                software.amazon.awssdk.services.ec2.model.PortRange.builder().from(1).build()))
                        .build();

        Header expected = Header.builder()
                .protocol("udp")
                .destinationAddresses(ImmutableList.of("1.2.3.4"))
                .destinationPortRanges(ImmutableList.of(PortRange.builder().from(0).build()))
                .sourceAddresses(ImmutableList.of("5.6.7.8"))
                .sourcePortRanges(ImmutableList.of(PortRange.builder().from(1).build()))
                .build();

        Header actual = TranslatorHelper.translateHeader(input);

        assertEquals(expected, actual);
    }

    @Test
    public void translateHeaderGivenEmptyExpectSuccess() {
        software.amazon.awssdk.services.ec2.model.Header input =
                software.amazon.awssdk.services.ec2.model.Header.builder().build();

        Header expected = Header.builder().build();

        Header actual = TranslatorHelper.translateHeader(input);

        assertEquals(expected, actual);
    }

    @Test
    public void translateHeaderGivenNullExpectNull() {
        assertEquals(null, TranslatorHelper.translateHeader(null));
    }

    @Test
    public void translateRouteTableRouteExpectSuccess() {
        software.amazon.awssdk.services.ec2.model.RouteTableRoute input =
                software.amazon.awssdk.services.ec2.model.RouteTableRoute.builder()
                        .destinationCidr("cidr")
                        .destinationPrefixListId("prefix")
                        .egressOnlyInternetGatewayId("igw-1")
                        .gatewayId("igw-2")
                        .instanceId("i-1")
                        .natGatewayId("ngw-1")
                        .networkInterfaceId("eni-1")
                        .transitGatewayId("tgw-1")
                        .vpcPeeringConnectionId("vpcx-1")
                        .origin("origin")
                        .build();

        RouteTableRoute expected = RouteTableRoute.builder()
                .destinationCidr("cidr")
                .destinationPrefixListId("prefix")
                .egressOnlyInternetGatewayId("igw-1")
                .gatewayId("igw-2")
                .instanceId("i-1")
                .natGatewayId("ngw-1")
                .networkInterfaceId("eni-1")
                .transitGatewayId("tgw-1")
                .vpcPeeringConnectionId("vpcx-1")
                .origin("origin")
                .build();

        RouteTableRoute actual = TranslatorHelper.translateRouteTableRoute(input);

        assertEquals(expected, actual);
    }

    @Test
    public void translateRouteTableRouteGivenEmptyExpectSuccess() {
        software.amazon.awssdk.services.ec2.model.RouteTableRoute input =
                software.amazon.awssdk.services.ec2.model.RouteTableRoute.builder().build();

        RouteTableRoute expected = RouteTableRoute.builder().build();

        RouteTableRoute actual = TranslatorHelper.translateRouteTableRoute(input);

        assertEquals(expected, actual);
    }

    @Test
    public void translateRouteTableRouteGivenNullExpectNull() {
        assertEquals(null, TranslatorHelper.translateRouteTableRoute(null));
    }

    @Test
    public void translateSecurityGroupRuleExpectSuccess() {
        software.amazon.awssdk.services.ec2.model.SecurityGroupRule input =
                software.amazon.awssdk.services.ec2.model.SecurityGroupRule.builder()
                        .cidr("cidr")
                        .direction("direction")
                        .portRange(software.amazon.awssdk.services.ec2.model.PortRange.builder().from(0).build())
                        .prefixListId("prefix")
                        .protocol(Protocol.UDP)
                        .securityGroupId("sg-1")
                        .build();

        SecurityGroupRule expected = SecurityGroupRule.builder()
                .cidr("cidr")
                .direction("direction")
                .portRange(PortRange.builder().from(0).build())
                .prefixListId("prefix")
                .protocol("udp")
                .securityGroupId("sg-1")
                .build();

        SecurityGroupRule actual = TranslatorHelper.translateSecurityGroupRule(input);

        assertEquals(expected, actual);
    }

    @Test
    public void translateSecurityGroupRuleGivenEmptyExpectSuccess() {
        software.amazon.awssdk.services.ec2.model.SecurityGroupRule input =
                software.amazon.awssdk.services.ec2.model.SecurityGroupRule.builder().build();

        SecurityGroupRule expected = SecurityGroupRule.builder().build();

        SecurityGroupRule actual = TranslatorHelper.translateSecurityGroupRule(input);

        assertEquals(expected, actual);
    }

    @Test
    public void translateSecurityGroupRuleGivenNullExpectNull() {
        assertEquals(null, TranslatorHelper.translateSecurityGroupRule(null));
    }

    @Test
    public void translateLoadBalancerListenerExpectSuccess() {
        software.amazon.awssdk.services.ec2.model.LoadBalancerListener input =
                software.amazon.awssdk.services.ec2.model.LoadBalancerListener.builder()
                        .loadBalancerPort(8443)
                        .instancePort(8000)
                        .build();

        LoadBalancerListener expected = LoadBalancerListener.builder()
                .loadBalancerPort(8443)
                .instancePort(8000)
                .build();

        LoadBalancerListener actual = TranslatorHelper.translateLoadBalancerListener(input);

        assertEquals(expected, actual);
    }

    @Test
    public void translateLoadBalancerListenerGivenEmptyExpectSuccess() {
        software.amazon.awssdk.services.ec2.model.LoadBalancerListener input =
                software.amazon.awssdk.services.ec2.model.LoadBalancerListener.builder().build();

        LoadBalancerListener expected = LoadBalancerListener.builder().build();

        LoadBalancerListener actual = TranslatorHelper.translateLoadBalancerListener(input);

        assertEquals(expected, actual);
    }

    @Test
    public void translateLoadBalancerListenerGivenNullExpectNull() {
        assertEquals(null, TranslatorHelper.translateLoadBalancerListener(null));
    }

    @Test
    public void translateLoadBalancerTargetExpectSuccess() {
        software.amazon.awssdk.services.ec2.model.LoadBalancerTarget input =
                software.amazon.awssdk.services.ec2.model.LoadBalancerTarget.builder()
                        .address("address")
                        .availabilityZone("az1")
                        .instance(software.amazon.awssdk.services.ec2.model.Component.builder().id("i-1").build())
                        .port(8443)
                        .build();

        LoadBalancerTarget expected = LoadBalancerTarget.builder()
                .address("address")
                .availabilityZone("az1")
                .instance(Component.builder().id("i-1").build())
                .port(8443)
                .build();

        LoadBalancerTarget actual = TranslatorHelper.translateLoadBalancerTarget(input);

        assertEquals(expected, actual);
    }

    @Test
    public void translateLoadBalancerTargetGivenEmptyExpectSuccess() {
        software.amazon.awssdk.services.ec2.model.LoadBalancerTarget input =
                software.amazon.awssdk.services.ec2.model.LoadBalancerTarget.builder().build();

        LoadBalancerTarget expected = LoadBalancerTarget.builder().build();

        LoadBalancerTarget actual = TranslatorHelper.translateLoadBalancerTarget(input);

        assertEquals(expected, actual);
    }

    @Test
    public void translateLoadBalancerTargetGivenNullExpectNull() {
        assertEquals(null, TranslatorHelper.translateLoadBalancerTarget(null));
    }

    @Test
    public void translateComponentsExpectSuccess() {
        List<software.amazon.awssdk.services.ec2.model.Component> input = ImmutableList.of(
                software.amazon.awssdk.services.ec2.model.Component.builder().id("1").build(),
                software.amazon.awssdk.services.ec2.model.Component.builder().id("2").build()
        );

        List<Component> expected = ImmutableList.of(
                Component.builder().id("1").build(),
                Component.builder().id("2").build()
        );

        List<Component> actual = TranslatorHelper.translateComponents(input);

        assertEquals(expected, actual);
    }

    @Test
    public void translateComponentsGivenEmptyExpectNull() {
        assertEquals(null, TranslatorHelper.translateComponents(ImmutableList.of()));
    }

    @Test
    public void translateComponentsGivenNullExpectNull() {
        assertEquals(null, TranslatorHelper.translateComponents(null));
    }

    @Test
    public void translatePortRangesExpectSuccess() {
        List<software.amazon.awssdk.services.ec2.model.PortRange> input = ImmutableList.of(
                software.amazon.awssdk.services.ec2.model.PortRange.builder().from(0).build(),
                software.amazon.awssdk.services.ec2.model.PortRange.builder().from(1).build()
        );

        List<PortRange> expected = ImmutableList.of(
                PortRange.builder().from(0).build(),
                PortRange.builder().from(1).build()
        );

        List<PortRange> actual = TranslatorHelper.translatePortRanges(input);

        assertEquals(expected, actual);
    }

    @Test
    public void translatePortRangesGivenEmptyExpectNull() {
        assertEquals(null, TranslatorHelper.translatePortRanges(ImmutableList.of()));

    }

    @Test
    public void translatePortRangesGivenNullExpectNull() {
        assertEquals(null, TranslatorHelper.translatePortRanges(null));
    }
}
