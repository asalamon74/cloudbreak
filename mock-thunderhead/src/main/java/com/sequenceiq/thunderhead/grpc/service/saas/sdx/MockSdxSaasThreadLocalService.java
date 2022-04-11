package com.sequenceiq.thunderhead.grpc.service.saas.sdx;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

@Service
public class MockSdxSaasThreadLocalService {

    private static final ThreadLocal<Pair<String, String>> SAAS_THREAD_LOCAL = new ThreadLocal<>();

    public void setSaasInstance(String envCrn, String sdxInstanceCrn) {
        if (SAAS_THREAD_LOCAL.get() != null) {
            SAAS_THREAD_LOCAL.remove();
        }
        SAAS_THREAD_LOCAL.set(Pair.of(envCrn, sdxInstanceCrn));
    }

    public String getEnvironmentCrn() {
        if (SAAS_THREAD_LOCAL.get() == null) {
            return null;
        } else {
            return SAAS_THREAD_LOCAL.get().getKey();
        }
    }

    public String getSdxCrn() {
        if (SAAS_THREAD_LOCAL.get() == null) {
            return null;
        } else {
            return SAAS_THREAD_LOCAL.get().getValue();
        }
    }

    public void remove() {
        SAAS_THREAD_LOCAL.remove();
    }
}
