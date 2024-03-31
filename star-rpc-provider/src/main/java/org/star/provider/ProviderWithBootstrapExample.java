package org.star.provider;

import org.star.common.service.UserService;
import org.star.starpc.bootstrap.ProviderBootstrap;
import org.star.starpc.model.ServiceRegisterInfo;

import java.util.ArrayList;
import java.util.List;

public class ProviderWithBootstrapExample {
    public static void main(String[] args) {
        List<ServiceRegisterInfo<?>> serviceRegisterInfos = new ArrayList<>();
        ServiceRegisterInfo serviceRegisterInfo = new ServiceRegisterInfo(
                UserService.class.getName(), UserServiceImpl.class);
        serviceRegisterInfos.add(serviceRegisterInfo);

        ProviderBootstrap.init(serviceRegisterInfos);
    }
}
