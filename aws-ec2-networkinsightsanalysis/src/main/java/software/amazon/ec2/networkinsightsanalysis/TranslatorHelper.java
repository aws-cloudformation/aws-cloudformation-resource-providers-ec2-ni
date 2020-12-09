package software.amazon.ec2.networkinsightsanalysis;

import java.util.List;
import java.util.stream.Collectors;

public class TranslatorHelper {

  public static PathComponent translatePathComponent(
          software.amazon.awssdk.services.ec2.model.PathComponent pathComponent) {
    if (pathComponent == null) {
      return null;
    }
    return software.amazon.ec2.networkinsightsanalysis.PathComponent.builder()
            .sequenceNumber(pathComponent.sequenceNumber())
            .aclRule(translateAclRule(pathComponent.aclRule()))
            .component(translateComponent(pathComponent.component()))
            .destinationVpc(translateComponent(pathComponent.destinationVpc()))
            .outboundHeader(translateHeader(pathComponent.outboundHeader()))
            .inboundHeader(translateHeader(pathComponent.inboundHeader()))
            .routeTableRoute(translateRouteTableRoute(pathComponent.routeTableRoute()))
            .securityGroupRule(translateSecurityGroupRule(pathComponent.securityGroupRule()))
            .sourceVpc(translateComponent(pathComponent.sourceVpc()))
            .subnet(translateComponent(pathComponent.subnet()))
            .vpc(translateComponent(pathComponent.vpc()))
            .build();
  }

  public static Explanation translateExplanation(
          software.amazon.awssdk.services.ec2.model.Explanation explanation) {
    if (explanation == null) {
      return null;
    }
    return Explanation.builder()
            .acl(translateComponent(explanation.acl()))
            .aclRule(translateAclRule(explanation.aclRule()))
            .address(explanation.address())
            .addresses(translateStringList(explanation.addresses()))
            .attachedTo(translateComponent(explanation.attachedTo()))
            .availabilityZones(translateStringList(explanation.availabilityZones()))
            .cidrs(translateStringList(explanation.cidrs()))
            .classicLoadBalancerListener(translateLoadBalancerListener(explanation.classicLoadBalancerListener()))
            .component(translateComponent(explanation.component()))
            .customerGateway(translateComponent(explanation.customerGateway()))
            .destination(translateComponent(explanation.destination()))
            .destinationVpc(translateComponent(explanation.destinationVpc()))
            .direction(explanation.direction())
            .elasticLoadBalancerListener(translateComponent(explanation.elasticLoadBalancerListener()))
            .explanationCode(explanation.explanationCode())
            .ingressRouteTable(translateComponent(explanation.ingressRouteTable()))
            .internetGateway(translateComponent(explanation.internetGateway()))
            .loadBalancerArn(explanation.loadBalancerArn())
            .loadBalancerListenerPort(explanation.loadBalancerListenerPort())
            .loadBalancerTarget(translateLoadBalancerTarget(explanation.loadBalancerTarget()))
            .loadBalancerTargetGroup(translateComponent(explanation.loadBalancerTargetGroup()))
            .loadBalancerTargetGroups(translateComponents(explanation.loadBalancerTargetGroups()))
            .loadBalancerTargetPort(explanation.loadBalancerTargetPort())
            .loadBalancerListenerPort(explanation.loadBalancerListenerPort())
            .missingComponent(explanation.missingComponent())
            .natGateway(translateComponent(explanation.natGateway()))
            .networkInterface(translateComponent(explanation.networkInterface()))
            .packetField(explanation.packetField())
            .port(explanation.port())
            .portRanges(translatePortRanges(explanation.portRanges()))
            .prefixList(translateComponent(explanation.prefixList()))
            .protocols(translateStringList(explanation.protocols()))
            .securityGroup(translateComponent(explanation.securityGroup()))
            .securityGroupRule(translateSecurityGroupRule(explanation.securityGroupRule()))
            .securityGroups(translateComponents(explanation.securityGroups()))
            .sourceVpc(translateComponent(explanation.sourceVpc()))
            .state(explanation.state())
            .subnet(translateComponent(explanation.subnet()))
            .subnetRouteTable(translateComponent(explanation.subnetRouteTable()))
            .routeTable(translateComponent(explanation.routeTable()))
            .routeTableRoute(translateRouteTableRoute(explanation.routeTableRoute()))
            .vpc(translateComponent(explanation.vpc()))
            .vpcEndpoint(translateComponent(explanation.vpcEndpoint()))
            .vpcPeeringConnection(translateComponent(explanation.vpcPeeringConnection()))
            .vpnConnection(translateComponent(explanation.vpnConnection()))
            .vpnGateway(translateComponent(explanation.vpnGateway()))
            .build();
  }

  public static AlternatePathHint translateAlternatePathHint(
          software.amazon.awssdk.services.ec2.model.AlternatePathHint alternatePathHint) {
    if (alternatePathHint == null) {
      return null;
    }
    return AlternatePathHint.builder()
            .componentArn(alternatePathHint.componentArn())
            .componentId(alternatePathHint.componentId())
            .build();
  }

  public static List<String> translateStringList(List<String> list) {
    if (list == null || list.isEmpty()) {
      return null;
    }
    return list;
  }

  static AnalysisAclRule translateAclRule(software.amazon.awssdk.services.ec2.model.AnalysisAclRule aclRule) {
    if (aclRule == null) {
      return null;
    }
    return AnalysisAclRule.builder()
            .cidr(aclRule.cidr())
            .egress(aclRule.egress())
            .portRange(translatePortRange(aclRule.portRange()))
            .protocol(aclRule.protocol())
            .ruleAction(aclRule.ruleAction())
            .ruleNumber(aclRule.ruleNumber())
            .build();
  }

  static PortRange translatePortRange(software.amazon.awssdk.services.ec2.model.PortRange portRange) {
    if (portRange == null) {
      return null;
    }
    return PortRange.builder()
            .from(portRange.from())
            .to(portRange.to())
            .build();
  }

  static AnalysisComponent translateComponent(software.amazon.awssdk.services.ec2.model.AnalysisComponent component) {
    if (component == null) {
      return null;
    }
    return AnalysisComponent.builder()
            .arn(component.arn())
            .id(component.id())
            .build();
  }

  static AnalysisPacketHeader translateHeader(software.amazon.awssdk.services.ec2.model.AnalysisPacketHeader header) {
    if (header == null) {
      return null;
    }
    return AnalysisPacketHeader.builder()
            .destinationAddresses(translateStringList(header.destinationAddresses()))
            .destinationPortRanges(translatePortRanges(header.destinationPortRanges()))
            .protocol(header.protocol())
            .sourceAddresses(translateStringList(header.sourceAddresses()))
            .sourcePortRanges(translatePortRanges(header.sourcePortRanges()))
            .build();
  }

  static AnalysisRouteTableRoute translateRouteTableRoute(
          software.amazon.awssdk.services.ec2.model.AnalysisRouteTableRoute routeTableRoute) {
    if (routeTableRoute == null) {
      return null;
    }
    return AnalysisRouteTableRoute.builder()
            .destinationCidr(routeTableRoute.destinationCidr())
            .destinationPrefixListId(routeTableRoute.destinationPrefixListId())
            .egressOnlyInternetGatewayId(routeTableRoute.egressOnlyInternetGatewayId())
            .gatewayId(routeTableRoute.gatewayId())
            .instanceId(routeTableRoute.instanceId())
            .natGatewayId(routeTableRoute.natGatewayId())
            .networkInterfaceId(routeTableRoute.networkInterfaceId())
            .origin(routeTableRoute.origin())
            .transitGatewayId(routeTableRoute.transitGatewayId())
            .vpcPeeringConnectionId(routeTableRoute.vpcPeeringConnectionId())
            .build();
  }

  static AnalysisSecurityGroupRule translateSecurityGroupRule(
          software.amazon.awssdk.services.ec2.model.AnalysisSecurityGroupRule securityGroupRule) {
    if (securityGroupRule == null) {
      return null;
    }
    return AnalysisSecurityGroupRule.builder()
            .cidr(securityGroupRule.cidr())
            .direction(securityGroupRule.direction())
            .portRange(translatePortRange(securityGroupRule.portRange()))
            .prefixListId(securityGroupRule.prefixListId())
            .protocol(securityGroupRule.protocol())
            .securityGroupId(securityGroupRule.securityGroupId())
            .build();
  }

  static AnalysisLoadBalancerListener translateLoadBalancerListener(
          software.amazon.awssdk.services.ec2.model.AnalysisLoadBalancerListener loadBalancerListener) {
    if (loadBalancerListener == null) {
      return null;
    }
    return AnalysisLoadBalancerListener.builder()
            .instancePort(loadBalancerListener.instancePort())
            .loadBalancerPort(loadBalancerListener.loadBalancerPort())
            .build();
  }

  static AnalysisLoadBalancerTarget translateLoadBalancerTarget(
          software.amazon.awssdk.services.ec2.model.AnalysisLoadBalancerTarget loadBalancerTarget) {
    if (loadBalancerTarget == null) {
      return null;
    }
    return AnalysisLoadBalancerTarget.builder()
            .address(loadBalancerTarget.address())
            .availabilityZone(loadBalancerTarget.availabilityZone())
            .instance(translateComponent(loadBalancerTarget.instance()))
            .port(loadBalancerTarget.port())
            .build();
  }

  static List<AnalysisComponent> translateComponents(
          List<software.amazon.awssdk.services.ec2.model.AnalysisComponent> components) {
    if (components == null || components.isEmpty()) {
      return null;
    }
    return components.stream().map(TranslatorHelper::translateComponent).collect(Collectors.toList());
  }

  static List<PortRange> translatePortRanges(
          List<software.amazon.awssdk.services.ec2.model.PortRange> portRanges) {
    if (portRanges == null || portRanges.isEmpty()) {
      return null;
    }
    return portRanges.stream().map(TranslatorHelper::translatePortRange).collect(Collectors.toList());
  }

}
