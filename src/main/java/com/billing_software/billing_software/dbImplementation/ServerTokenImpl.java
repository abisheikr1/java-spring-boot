package com.billing_software.billing_software.dbImplementation;

import org.springframework.stereotype.Service;

import com.billing_software.billing_software.dbInterface.ServerTokenInterface;
import com.billing_software.billing_software.models.authentication.ServerToken;
import com.billing_software.billing_software.repositories.ServerTokenRepository;

@Service
public class ServerTokenImpl implements ServerTokenInterface {

    private ServerTokenRepository serverTokenRepository;

    public ServerTokenImpl(ServerTokenRepository serverTokenRepository) {
        this.serverTokenRepository = serverTokenRepository;
    }

    @Override
    public String create(ServerToken serverTokenData) {
        serverTokenRepository.save(serverTokenData).subscribe();
        return serverTokenData.getId();
    }

    @Override
    public ServerToken get(String serverTokenId) {
        return serverTokenRepository.findById(serverTokenId).blockOptional().orElse(null);
    }

    @Override
    public boolean isTokenPresent(String id) {
        return serverTokenRepository.existsById(id).block();
    }

    @Override
    public void delete(String serverTokenId) {
        serverTokenRepository.deleteById(serverTokenId).subscribe();
    }

}
