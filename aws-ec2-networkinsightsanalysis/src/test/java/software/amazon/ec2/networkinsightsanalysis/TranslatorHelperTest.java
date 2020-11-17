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
                        .aclRule(software.amazon.awssdk.services.ec2.model.AnalysisAclRule.builder().ruleNumber(1).build())
                        .component(software.amazon.awssdk.services.ec2.model.AnalysisComponent.builder().id("comp").build())
                        .destinationVpc(software.amazon.awssdk.services.ec2.model.AnalysisComponent.builder().id("dest").build())
                        .inboundHeader(software.amazon.awssdk.services.ec2.model.AnalysisPacketHeader.builder().destinationAddresses(ImmutableList.of("1.2.3.4")).build())
                        .outboundHeader(software.amazon.awssdk.services.ec2.model.AnalysisPacketHeader.builder().destinationAddresses(ImmutableList.of("5.6.7.8")).build())
                        .routeTableRoute(software.amazon.awssdk.services.ec2.model.AnalysisRouteTableRoute.builder().instanceId("i-1").build())
                        .securityGroupRule(software.amazon.awssdk.services.ec2.model.AnalysisSecurityGroupRule.builder().cidr("cidr").build())
                        .sourceVpc(software.amazon.awssdk.services.ec2.model.AnalysisComponent.builder().id("src").build())
                        .subnet(software.amazon.awssdk.services.ec2.model.AnalysisComponent.builder().id("subnet").build())
                        .vpc(software.amazon.awssdk.services.ec2.model.AnalysisComponent.builder().id("vpc").build())
                        .build();

        PathComponent expected = PathComponent.builder()
                .aclRule(AnalysisAclRule.builder().ruleNumber(1).build())
                .component(AnalysisComponent.builder().id("comp").build())
                .destinationVpc(AnalysisComponent.builder().id("dest").build())
                .inboundHeader(AnalysisPacketHeader.builder().destinationAddresses(ImmutableList.of("1.2.3.4")).build())
                .outboundHeader(AnalysisPacketHeader.builder().destinationAddresses(ImmutableList.of("5.6.7.8")).build())
                .routeTableRoute(AnalysisRouteTableRoute.builder().instanceId("i-1").build())
                .securityGroupRule(AnalysisSecurityGroupRule.builder().cidr("cidr").build())
                .sourceVpc(AnalysisComponent.builder().id("src").build())
                .subnet(AnalysisComponent.builder().id("subnet").build())
                .vpc(AnalysisComponent.builder().id("vpc").build())
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
                        .acl(software.amazon.awssdk.services.ec2.model.AnalysisComponent.builder().id("acl-id").build())
                        .aclRule(software.amazon.awssdk.services.ec2.model.AnalysisAclRule.builder().ruleNumber(1).build())
                        .address("1.2.3.4")
                        .addresses(ImmutableList.of("1.2.3.4", "4.3.2.1"))
                        .attachedTo(
                                software.amazon.awssdk.services.ec2.model.AnalysisComponent.builder().id("attached-id").build())
                        .availabilityZones(ImmutableList.of("us-test-1a", "us-test-2b"))
                        .cidrs(ImmutableList.of("cidr1", "cidr2"))
                        .classicLoadBalancerListener(
                                software.amazon.awssdk.services.ec2.model.AnalysisLoadBalancerListener.builder()
                                        .loadBalancerPort(2).build())
                        .component(software.amazon.awssdk.services.ec2.model.AnalysisComponent.builder().id("comp-id").build())
                        .customerGateway(
                                software.amazon.awssdk.services.ec2.model.AnalysisComponent.builder().id("cgw-id").build())
                        .destination(
                                software.amazon.awssdk.services.ec2.model.AnalysisComponent.builder().id("dest-id").build())
                        .destinationVpc(
                                software.amazon.awssdk.services.ec2.model.AnalysisComponent.builder().id("dest-vpc").build())
                        .direction("direction")
                        .elasticLoadBalancerListener(
                                software.amazon.awssdk.services.ec2.model.AnalysisComponent.builder().id("elb-listener")
                                        .build())
                        .explanationCode("explanation-code")
                        .ingressRouteTable(
                                software.amazon.awssdk.services.ec2.model.AnalysisComponent.builder().id("rtb-id").build())
                        .internetGateway(
                                software.amazon.awssdk.services.ec2.model.AnalysisComponent.builder().id("igw-id").build())
                        .loadBalancerArn("elb-arn")
                        .loadBalancerListenerPort(3)
                        .loadBalancerTarget(
                                software.amazon.awssdk.services.ec2.model.AnalysisLoadBalancerTarget.builder().port(4).build())
                        .loadBalancerTargetGroup(
                                software.amazon.awssdk.services.ec2.model.AnalysisComponent.builder().id("lb-target-group-1")
                                        .build())
                        .loadBalancerTargetGroups(ImmutableList.of(
                                software.amazon.awssdk.services.ec2.model.AnalysisComponent.builder().id("lb-target-group-1")
                                        .build(),
                                software.amazon.awssdk.services.ec2.model.AnalysisComponent.builder().id("lb-target-group-2")
                                        .build()))
                        .loadBalancerTargetPort(5)
                        .missingComponent("missing-comp")
                        .natGateway(software.amazon.awssdk.services.ec2.model.AnalysisComponent.builder().id("ngw-id").build())
                        .networkInterface(software.amazon.awssdk.services.ec2.model.AnalysisComponent.builder().id("eni-id")
                                .build())
                        .packetField("packet-field")
                        .port(6)
                        .portRanges(ImmutableList.of(
                                software.amazon.awssdk.services.ec2.model.PortRange.builder().from(7).build(),
                                software.amazon.awssdk.services.ec2.model.PortRange.builder().from(8).build()))
                        .prefixList(software.amazon.awssdk.services.ec2.model.AnalysisComponent.builder().id("prefix-list-id")
                                .build())
                        .protocols(ImmutableList.of(Protocol.UDP, Protocol.TCP))
                        .routeTable(software.amazon.awssdk.services.ec2.model.AnalysisComponent.builder().id("rtb-id").build())
                        .routeTableRoute(software.amazon.awssdk.services.ec2.model.AnalysisRouteTableRoute.builder()
                                .origin("origin").build())
                        .securityGroup(software.amazon.awssdk.services.ec2.model.AnalysisComponent.builder().id("sg-1").build())
                        .securityGroupRule(software.amazon.awssdk.services.ec2.model.AnalysisSecurityGroupRule.builder()
                                .cidr("cidr").build())
                        .securityGroups(ImmutableList.of(
                                software.amazon.awssdk.services.ec2.model.AnalysisComponent.builder().id("sg-1").build(),
                                software.amazon.awssdk.services.ec2.model.AnalysisComponent.builder().id("sg-2").build()))
                        .sourceVpc(software.amazon.awssdk.services.ec2.model.AnalysisComponent.builder().id("src-vpc").build())
                        .state("state")
                        .subnet(software.amazon.awssdk.services.ec2.model.AnalysisComponent.builder().id("subnet-id").build())
                        .subnetRouteTable(software.amazon.awssdk.services.ec2.model.AnalysisComponent.builder().id("subnet-rtb")
                                .build())
                        .vpc(software.amazon.awssdk.services.ec2.model.AnalysisComponent.builder().id("vpc-id").build())
                        .vpcEndpoint(software.amazon.awssdk.services.ec2.model.AnalysisComponent.builder().id("vpc-endpoint")
                                .build())
                        .vpcPeeringConnection(software.amazon.awssdk.services.ec2.model.AnalysisComponent.builder().id("pcx-id")
                                .build())
                        .vpnConnection(software.amazon.awssdk.services.ec2.model.AnalysisComponent.builder().id("vpnc-id")
                                .build())
                        .vpnGateway(software.amazon.awssdk.services.ec2.model.AnalysisComponent.builder().id("vgw-id").build())
                        .build();

        Explanation expected = Explanation.builder()
                .acl(AnalysisComponent.builder().id("acl-id").build())
                .aclRule(AnalysisAclRule.builder().ruleNumber(1).build())
                .address("1.2.3.4")
                .addresses(ImmutableList.of("1.2.3.4", "4.3.2.1"))
                .attachedTo(AnalysisComponent.builder().id("attached-id").build())
                .availabilityZones(ImmutableList.of("us-test-1a", "us-test-2b"))
                .cidrs(ImmutableList.of("cidr1", "cidr2"))
                .classicLoadBalancerListener(AnalysisLoadBalancerListener.builder().loadBalancerPort(2).build())
                .component(AnalysisComponent.builder().id("comp-id").build())
                .customerGateway(AnalysisComponent.builder().id("cgw-id").build())
                .destination(AnalysisComponent.builder().id("dest-id").build())
                .destinationVpc(AnalysisComponent.builder().id("dest-vpc").build())
                .direction("direction")
                .elasticLoadBalancerListener(AnalysisComponent.builder().id("elb-listener").build())
                .explanationCode("explanation-code")
                .ingressRouteTable(AnalysisComponent.builder().id("rtb-id").build())
                .internetGateway(AnalysisComponent.builder().id("igw-id").build())
                .loadBalancerArn("elb-arn")
                .loadBalancerListenerPort(3)
                .loadBalancerTarget(AnalysisLoadBalancerTarget.builder().port(4).build())
                .loadBalancerTargetGroup(AnalysisComponent.builder().id("lb-target-group-1").build())
                .loadBalancerTargetGroups(ImmutableList.of(AnalysisComponent.builder().id("lb-target-group-1").build(),
                        AnalysisComponent.builder().id("lb-target-group-2").build()))
                .loadBalancerTargetPort(5)
                .missingComponent("missing-comp")
                .natGateway(AnalysisComponent.builder().id("ngw-id").build())
                .networkInterface(AnalysisComponent.builder().id("eni-id").build())
                .packetField("packet-field")
                .port(6)
                .portRanges(ImmutableList.of(PortRange.builder().from(7).build(),
                        PortRange.builder().from(8).build()))
                .prefixList(AnalysisComponent.builder().id("prefix-list-id").build())
                .protocols(ImmutableList.of(Protocol.UDP.toString(), Protocol.TCP.toString()))
                .routeTable(AnalysisComponent.builder().id("rtb-id").build())
                .routeTableRoute(AnalysisRouteTableRoute.builder().origin("origin").build())
                .securityGroup(AnalysisComponent.builder().id("sg-1").build())
                .securityGroupRule(AnalysisSecurityGroupRule.builder().cidr("cidr").build())
                .securityGroups(ImmutableList.of(AnalysisComponent.builder().id("sg-1").build(),
                        AnalysisComponent.builder().id("sg-2").build()))
                .sourceVpc(AnalysisComponent.builder().id("src-vpc").build())
                .state("state")
                .subnet(AnalysisComponent.builder().id("subnet-id").build())
                .subnetRouteTable(AnalysisComponent.builder().id("subnet-rtb").build())
                .vpc(AnalysisComponent.builder().id("vpc-id").build())
                .vpcEndpoint(AnalysisComponent.builder().id("vpc-endpoint").build())
                .vpcPeeringConnection(AnalysisComponent.builder().id("pcx-id").build())
                .vpnConnection(AnalysisComponent.builder().id("vpnc-id").build())
                .vpnGateway(AnalysisComponent.builder().id("vgw-id").build())
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
        software.amazon.awssdk.services.ec2.model.AnalysisAclRule input =
                software.amazon.awssdk.services.ec2.model.AnalysisAclRule.builder()
                        .build();

        AnalysisAclRule expected = AnalysisAclRule.builder()
                .build();

        AnalysisAclRule actual = TranslatorHelper.translateAclRule(input);

        assertEquals(expected, actual);
    }

    @Test
    public void translateAclRuleGivenEmptyExpectSuccess() {
        software.amazon.awssdk.services.ec2.model.AnalysisAclRule input =
                software.amazon.awssdk.services.ec2.model.AnalysisAclRule.builder().build();

        AnalysisAclRule expected = AnalysisAclRule.builder().build();

        AnalysisAclRule actual = TranslatorHelper.translateAclRule(input);

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
        software.amazon.awssdk.services.ec2.model.AnalysisComponent input =
                software.amazon.awssdk.services.ec2.model.AnalysisComponent.builder()
                        .id("id")
                        .arn("arn")
                        .build();

        AnalysisComponent expected = AnalysisComponent.builder()
                .id("id")
                .arn("arn")
                .build();

        AnalysisComponent actual = TranslatorHelper.translateComponent(input);

        assertEquals(expected, actual);
    }

    @Test
    public void translateComponentGivenEmptyExpectSuccess() {
        software.amazon.awssdk.services.ec2.model.AnalysisComponent input =
                software.amazon.awssdk.services.ec2.model.AnalysisComponent.builder().build();

        AnalysisComponent expected = AnalysisComponent.builder().build();

        AnalysisComponent actual = TranslatorHelper.translateComponent(input);

        assertEquals(expected, actual);
    }

    @Test
    public void translateComponentGivenNullExpectNull() {
        assertEquals(null, TranslatorHelper.translateComponent(null));
    }

    @Test
    public void translateHeaderExpectSuccess() {
        software.amazon.awssdk.services.ec2.model.AnalysisPacketHeader input =
                software.amazon.awssdk.services.ec2.model.AnalysisPacketHeader.builder()
                        .protocol(Protocol.UDP)
                        .destinationAddresses(ImmutableList.of("1.2.3.4"))
                        .destinationPortRanges(ImmutableList.of(
                                software.amazon.awssdk.services.ec2.model.PortRange.builder().from(0).build()))
                        .sourceAddresses(ImmutableList.of("5.6.7.8"))
                        .sourcePortRanges(ImmutableList.of(
                                software.amazon.awssdk.services.ec2.model.PortRange.builder().from(1).build()))
                        .build();

        AnalysisPacketHeader expected = AnalysisPacketHeader.builder()
                .protocol("udp")
                .destinationAddresses(ImmutableList.of("1.2.3.4"))
                .destinationPortRanges(ImmutableList.of(PortRange.builder().from(0).build()))
                .sourceAddresses(ImmutableList.of("5.6.7.8"))
                .sourcePortRanges(ImmutableList.of(PortRange.builder().from(1).build()))
                .build();

        AnalysisPacketHeader actual = TranslatorHelper.translateHeader(input);

        assertEquals(expected, actual);
    }

    @Test
    public void translateHeaderGivenEmptyExpectSuccess() {
        software.amazon.awssdk.services.ec2.model.AnalysisPacketHeader input =
                software.amazon.awssdk.services.ec2.model.AnalysisPacketHeader.builder().build();

        AnalysisPacketHeader expected = AnalysisPacketHeader.builder().build();

        AnalysisPacketHeader actual = TranslatorHelper.translateHeader(input);

        assertEquals(expected, actual);
    }

    @Test
    public void translateHeaderGivenNullExpectNull() {
        assertEquals(null, TranslatorHelper.translateHeader(null));
    }

    @Test
    public void translateRouteTableRouteExpectSuccess() {
        software.amazon.awssdk.services.ec2.model.AnalysisRouteTableRoute input =
                software.amazon.awssdk.services.ec2.model.AnalysisRouteTableRoute.builder()
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

        AnalysisRouteTableRoute expected = AnalysisRouteTableRoute.builder()
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

        AnalysisRouteTableRoute actual = TranslatorHelper.translateRouteTableRoute(input);

        assertEquals(expected, actual);
    }

    @Test
    public void translateRouteTableRouteGivenEmptyExpectSuccess() {
        software.amazon.awssdk.services.ec2.model.AnalysisRouteTableRoute input =
                software.amazon.awssdk.services.ec2.model.AnalysisRouteTableRoute.builder().build();

        AnalysisRouteTableRoute expected = AnalysisRouteTableRoute.builder().build();

        AnalysisRouteTableRoute actual = TranslatorHelper.translateRouteTableRoute(input);

        assertEquals(expected, actual);
    }

    @Test
    public void translateRouteTableRouteGivenNullExpectNull() {
        assertEquals(null, TranslatorHelper.translateRouteTableRoute(null));
    }

    @Test
    public void translateSecurityGroupRuleExpectSuccess() {
        software.amazon.awssdk.services.ec2.model.AnalysisSecurityGroupRule input =
                software.amazon.awssdk.services.ec2.model.AnalysisSecurityGroupRule.builder()
                        .cidr("cidr")
                        .direction("direction")
                        .portRange(software.amazon.awssdk.services.ec2.model.PortRange.builder().from(0).build())
                        .prefixListId("prefix")
                        .protocol(Protocol.UDP)
                        .securityGroupId("sg-1")
                        .build();

        AnalysisSecurityGroupRule expected = AnalysisSecurityGroupRule.builder()
                .cidr("cidr")
                .direction("direction")
                .portRange(PortRange.builder().from(0).build())
                .prefixListId("prefix")
                .protocol("udp")
                .securityGroupId("sg-1")
                .build();

        AnalysisSecurityGroupRule actual = TranslatorHelper.translateSecurityGroupRule(input);

        assertEquals(expected, actual);
    }

    @Test
    public void translateSecurityGroupRuleGivenEmptyExpectSuccess() {
        software.amazon.awssdk.services.ec2.model.AnalysisSecurityGroupRule input =
                software.amazon.awssdk.services.ec2.model.AnalysisSecurityGroupRule.builder().build();

        AnalysisSecurityGroupRule expected = AnalysisSecurityGroupRule.builder().build();

        AnalysisSecurityGroupRule actual = TranslatorHelper.translateSecurityGroupRule(input);

        assertEquals(expected, actual);
    }

    @Test
    public void translateSecurityGroupRuleGivenNullExpectNull() {
        assertEquals(null, TranslatorHelper.translateSecurityGroupRule(null));
    }

    @Test
    public void translateLoadBalancerListenerExpectSuccess() {
        software.amazon.awssdk.services.ec2.model.AnalysisLoadBalancerListener input =
                software.amazon.awssdk.services.ec2.model.AnalysisLoadBalancerListener.builder()
                        .loadBalancerPort(8443)
                        .instancePort(8000)
                        .build();

        AnalysisLoadBalancerListener expected = AnalysisLoadBalancerListener.builder()
                .loadBalancerPort(8443)
                .instancePort(8000)
                .build();

        AnalysisLoadBalancerListener actual = TranslatorHelper.translateLoadBalancerListener(input);

        assertEquals(expected, actual);
    }

    @Test
    public void translateLoadBalancerListenerGivenEmptyExpectSuccess() {
        software.amazon.awssdk.services.ec2.model.AnalysisLoadBalancerListener input =
                software.amazon.awssdk.services.ec2.model.AnalysisLoadBalancerListener.builder().build();

        AnalysisLoadBalancerListener expected = AnalysisLoadBalancerListener.builder().build();

        AnalysisLoadBalancerListener actual = TranslatorHelper.translateLoadBalancerListener(input);

        assertEquals(expected, actual);
    }

    @Test
    public void translateLoadBalancerListenerGivenNullExpectNull() {
        assertEquals(null, TranslatorHelper.translateLoadBalancerListener(null));
    }

    @Test
    public void translateLoadBalancerTargetExpectSuccess() {
        software.amazon.awssdk.services.ec2.model.AnalysisLoadBalancerTarget input =
                software.amazon.awssdk.services.ec2.model.AnalysisLoadBalancerTarget.builder()
                        .address("address")
                        .availabilityZone("az1")
                        .instance(software.amazon.awssdk.services.ec2.model.AnalysisComponent.builder().id("i-1").build())
                        .port(8443)
                        .build();

        AnalysisLoadBalancerTarget expected = AnalysisLoadBalancerTarget.builder()
                .address("address")
                .availabilityZone("az1")
                .instance(AnalysisComponent.builder().id("i-1").build())
                .port(8443)
                .build();

        AnalysisLoadBalancerTarget actual = TranslatorHelper.translateLoadBalancerTarget(input);

        assertEquals(expected, actual);
    }

    @Test
    public void translateLoadBalancerTargetGivenEmptyExpectSuccess() {
        software.amazon.awssdk.services.ec2.model.AnalysisLoadBalancerTarget input =
                software.amazon.awssdk.services.ec2.model.AnalysisLoadBalancerTarget.builder().build();

        AnalysisLoadBalancerTarget expected = AnalysisLoadBalancerTarget.builder().build();

        AnalysisLoadBalancerTarget actual = TranslatorHelper.translateLoadBalancerTarget(input);

        assertEquals(expected, actual);
    }

    @Test
    public void translateLoadBalancerTargetGivenNullExpectNull() {
        assertEquals(null, TranslatorHelper.translateLoadBalancerTarget(null));
    }

    @Test
    public void translateComponentsExpectSuccess() {
        List<software.amazon.awssdk.services.ec2.model.AnalysisComponent> input = ImmutableList.of(
                software.amazon.awssdk.services.ec2.model.AnalysisComponent.builder().id("1").build(),
                software.amazon.awssdk.services.ec2.model.AnalysisComponent.builder().id("2").build()
        );

        List<AnalysisComponent> expected = ImmutableList.of(
                AnalysisComponent.builder().id("1").build(),
                AnalysisComponent.builder().id("2").build()
        );

        List<AnalysisComponent> actual = TranslatorHelper.translateComponents(input);

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
