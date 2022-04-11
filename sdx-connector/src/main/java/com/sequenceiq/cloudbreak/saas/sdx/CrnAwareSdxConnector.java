package com.sequenceiq.cloudbreak.saas.sdx;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.dyngr.core.AttemptResult;
import com.dyngr.core.AttemptResults;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sequenceiq.cloudbreak.auth.crn.Crn;
import com.sequenceiq.cloudbreak.auth.crn.CrnResourceDescriptor;
import com.sequenceiq.cloudbreak.saas.sdx.polling.PollingResult;
import com.sequenceiq.cloudbreak.saas.sdx.polling.PollingTarget;

@Service
public class CrnAwareSdxConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(CrnAwareSdxConnector.class);

    @Inject
    private Map<CrnResourceDescriptor, SdxService<?>> platformDependentServiceMap;

    public void delete(String sdxCrn, Boolean force) {
        platformDependentServiceMap.get(CrnResourceDescriptor.getByCrnString(sdxCrn)).deleteSdx(sdxCrn, force);
    }

    public AttemptResult<Object> getAttemptResultForDeletion(Set<String> sdxCrns, String environmentName, String environmentCrn) {
        String failedPollingErrorMessageTemplate = "SDX deletion is failed for these: %s";
        switch (calculatePollingTarget(sdxCrns)) {
            case PAAS:
                return getAttemptResultForPolling(platformDependentServiceMap.get(CrnResourceDescriptor.DATALAKE)
                        .getPollingResultForDeletion(environmentName, environmentCrn), failedPollingErrorMessageTemplate);
            case SAAS:
                return getAttemptResultForPolling(platformDependentServiceMap.get(CrnResourceDescriptor.SDX_SAAS_INSTANCE)
                        .getPollingResultForDeletion(environmentName, environmentCrn), failedPollingErrorMessageTemplate);
            case ALL:
            default:
                Map<String, PollingResult> pollingResult = Maps.newHashMap();
                pollingResult.putAll(platformDependentServiceMap.get(CrnResourceDescriptor.DATALAKE)
                        .getPollingResultForDeletion(environmentName, environmentCrn));
                pollingResult.putAll(platformDependentServiceMap.get(CrnResourceDescriptor.SDX_SAAS_INSTANCE)
                        .getPollingResultForDeletion(environmentName, environmentCrn));
                return getAttemptResultForPolling(pollingResult, failedPollingErrorMessageTemplate);
        }
    }

    public Set<String> listSdxCrnsForEveryPlatform(String environmentName, String environmentCrn) {
        Set<String> sdxCrnList = Sets.newHashSet();
        platformDependentServiceMap.values().forEach(sdxService -> sdxCrnList.addAll(sdxService.listSdxCrns(environmentName, environmentCrn)));
        return sdxCrnList;
    }

    private PollingTarget calculatePollingTarget(Set<String> sdxCrns) {
        if (sdxCrns.stream().allMatch(crn -> Crn.ResourceType.INSTANCE.equals(Crn.safeFromString(crn).getResourceType()))) {
            return PollingTarget.SAAS;
        } else if (sdxCrns.stream().allMatch(crn -> Crn.ResourceType.DATALAKE.equals(Crn.safeFromString(crn).getResourceType()))) {
            return PollingTarget.PAAS;
        } else {
            return PollingTarget.ALL;
        }
    }

    private AttemptResult<Object> getAttemptResultForPolling(Map<String, PollingResult> pollingResult, String failedPollingErrorMessageTemplate) {
        if (!pollingResult.isEmpty()) {
            Set<String> failedSdxCrns = pollingResult.entrySet().stream()
                    .filter(entry -> PollingResult.FAILED.equals(entry.getValue()))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet());
            if (!failedSdxCrns.isEmpty()) {
                String errorMessage = String.format(failedPollingErrorMessageTemplate, Joiner.on(",").join(failedSdxCrns));
                LOGGER.info(errorMessage);
                return AttemptResults.breakFor(new IllegalStateException(errorMessage));
            }
            return AttemptResults.justContinue();
        }
        return AttemptResults.finishWith(null);
    }
}
