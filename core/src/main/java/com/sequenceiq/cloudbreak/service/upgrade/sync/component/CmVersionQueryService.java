package com.sequenceiq.cloudbreak.service.upgrade.sync.component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.sequenceiq.cloudbreak.common.exception.CloudbreakServiceException;
import com.sequenceiq.cloudbreak.common.model.PackageInfo;
import com.sequenceiq.cloudbreak.domain.stack.Stack;
import com.sequenceiq.cloudbreak.orchestrator.exception.CloudbreakOrchestratorFailedException;
import com.sequenceiq.cloudbreak.orchestrator.host.HostOrchestrator;
import com.sequenceiq.cloudbreak.orchestrator.model.GatewayConfig;
import com.sequenceiq.cloudbreak.service.GatewayConfigService;
import com.sequenceiq.cloudbreak.service.cluster.Package;
import com.sequenceiq.cloudbreak.service.cluster.PackageName;

@Component
@ConfigurationProperties(prefix = "cb.instance")
public class CmVersionQueryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CmVersionQueryService.class);
    private static final String CM_PREFIX = "cm-";

    private List<Package> packages;

    @Inject
    private HostOrchestrator hostOrchestrator;

    @Inject
    private GatewayConfigService gatewayConfigService;

    public List<Package> getPackages() {
        return packages;
    }

    public void setPackages(List<Package> packages) {
        this.packages = packages;
    }

    /**
     * Will query all active parcels (CDH and non-CDH as well) from the CM server. Received format:
     * CDH -> 7.2.7-1.cdh7.2.7.p7.12569826
     * <p>
     * TODO: the call is blocking for some time. Check if it is possible to block for shorter period of time.
     *
     * @param stack The stack, to get the coordinates of the CM to query
     * @return List of parcels found in the CM
     */
    Map<String, List<PackageInfo>> queryCmPackageInfo(Stack stack) throws CloudbreakOrchestratorFailedException {
        GatewayConfig gatewayConfig = gatewayConfigService.getPrimaryGatewayConfig(stack);
        Map<String, Optional<String>> packageMap = packages.stream()
                .filter(aPackage -> aPackage.getName().startsWith(CM_PREFIX))
                .map(Package::getPkg)
                .collect(Collectors.toMap(PackageName::getName, packageName -> Optional.ofNullable(packageName.getPattern())));
        Map<String, Map<String, String>> packageVersionsFromAllHosts = hostOrchestrator.getPackageVersionsFromAllHosts(gatewayConfig, packageMap);
        Map<String, List<PackageInfo>> fullPackageVersionsFromAllHosts = hostOrchestrator.getFullPackageVersionsFromAllHosts(gatewayConfig, packageMap);
        LOGGER.debug("Reading CM package info, found packages: " + fullPackageVersionsFromAllHosts);
        return fullPackageVersionsFromAllHosts;
    }

    void checkCmPackageInfoConsistency(Map<String, List<PackageInfo>> cmPackageVersionsFromAllHosts) {
        Multimap<String, PackageInfo> pkgVersionsMMap = HashMultimap.create();
        cmPackageVersionsFromAllHosts.values()
                .forEach(packageInfoList -> packageInfoList.forEach(
                        packageInfo -> pkgVersionsMMap.put(packageInfo.getName(), packageInfo)));
        // at least one package has different versions
        if (pkgVersionsMMap.keySet().size() < pkgVersionsMMap.size()) {
            StringBuilder errorMessageBuilder = new StringBuilder("Error during sync! The following package(s) has multiple versions present on the machines. ");
            pkgVersionsMMap.asMap()
                    .entrySet()
                    .stream()
                    .filter(packageInfos -> packageInfos.getValue().size() > 1)
                    .forEach(packageInfos -> errorMessageBuilder
                            .append("Package: ")
                            .append(packageInfos.getValue()));
            String error = errorMessageBuilder.toString();
            LOGGER.warn(error);
            throw new CloudbreakServiceException(error);
        }
        // CM server and agent are on different version
        Stream<PackageInfo> distinctPackageInfos = pkgVersionsMMap.values().stream().distinct();
        if (distinctPackageInfos.count() > 1) {
            String error = "Error during sync! CM server and agent has different versions. "
                    + distinctPackageInfos.map(PackageInfo::getPackageNameAndFullVersion)
                    .collect(Collectors.joining(", "));
            LOGGER.warn(error);
            throw new CloudbreakServiceException(error);
        }
    }

    void chooseCmVersion(Map<String, List<PackageInfo>> cmPackageVersionsFromAllHosts) {
        return cmPackageVersionsFromAllHosts.values().forEach(packageInfoList -> p);
    }

}
