package com.myproject.authenticationcore.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class KeyUtils {

    @Value("${access-token.private}")
    private Resource accessTokenPrivateKeyPath;

    @Value("${access-token.public}")
    private Resource accessTokenPublicKeyPath;

    @Value("${refresh-token.private}")
    private Resource refreshTokenPrivateKeyPath;

    @Value("${refresh-token.public}")
    private Resource refreshTokenPublicKeyPath;

    private KeyPair _accessTokenKeyPair;
    private KeyPair _refreshTokenKeyPair;

    private KeyPair getAccessTokenKeyPair() throws IOException {
        if (Objects.isNull(_accessTokenKeyPair)) {
            _accessTokenKeyPair = getKeyPair(accessTokenPublicKeyPath, accessTokenPrivateKeyPath);
        }
        return _accessTokenKeyPair;
    }

    private KeyPair getRefreshTokenKeyPair() throws IOException {
        if (Objects.isNull(_refreshTokenKeyPair)) {
            _refreshTokenKeyPair = getKeyPair(refreshTokenPublicKeyPath, refreshTokenPrivateKeyPath);
        }
        return _refreshTokenKeyPair;
    }

    private KeyPair getKeyPair(Resource publicKeyPath, Resource privateKeyPath) throws IOException {
        KeyPair keyPair;
        File publicKeyFile = publicKeyPath.getFile();
        File privateKeyFile = privateKeyPath.getFile();

        if (publicKeyFile.exists() && privateKeyFile.exists()) {
            //log.info("loading keys from file: {}, {}", publicKeyPath, privateKeyPath); 
            try {
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");

                byte[] publicKeyBytes = Files.readAllBytes(Paths.get(publicKeyPath.getURI()));
                String publicKeyContent = new String(publicKeyBytes)
                        .replaceAll("\\n", "")
                        .replaceAll("\\r", "")
                        .replace("-----BEGIN PUBLIC KEY-----", "")
                        .replace("-----END PUBLIC KEY-----", "");

                X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyContent));
                PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

                byte[] privateKeyBytes = Files.readAllBytes(Paths.get(privateKeyPath.getURI()));
                String privateKeyContent = new String(privateKeyBytes)
                        .replaceAll("\\n", "")
                        .replaceAll("\\r", "")
                        .replace("-----BEGIN PRIVATE KEY-----", "")
                        .replace("-----END PRIVATE KEY-----", "");

                PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyContent));
                PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

                keyPair = new KeyPair(publicKey, privateKey);
                return keyPair;
            } catch (NoSuchAlgorithmException | IOException | InvalidKeySpecException e) {
                throw new RuntimeException(e);
            }
        } else {
//            if (Arrays.stream(environment.getActiveProfiles()).anyMatch(s -> s.equals("prod"))) {
//                throw new RuntimeException("public and private keys don't exist");
//            }
        }

        File directory = new File("access-refresh-token-keys");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        try {
            //log.info("Generating new public and private keys: {}, {}", publicKeyPath, privateKeyPath); 
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
            try (FileOutputStream fos = new FileOutputStream(publicKeyPath.getFile())) {
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyPair.getPublic().getEncoded());
                fos.write(keySpec.getEncoded());
            }

            try (FileOutputStream fos = new FileOutputStream(privateKeyPath.getFile())) {
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyPair.getPrivate().getEncoded());
                fos.write(keySpec.getEncoded());
            }
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }

        return keyPair;
    }


    public RSAPublicKey getAccessTokenPublicKey() throws IOException {
        return (RSAPublicKey) getAccessTokenKeyPair().getPublic();
    }


    public RSAPrivateKey getAccessTokenPrivateKey() throws IOException {
        return (RSAPrivateKey) getAccessTokenKeyPair().getPrivate();
    }


    public RSAPublicKey getRefreshTokenPublicKey() throws IOException {
        return (RSAPublicKey) getRefreshTokenKeyPair().getPublic();
    }


    public RSAPrivateKey getRefreshTokenPrivateKey() throws IOException {
        return (RSAPrivateKey) getRefreshTokenKeyPair().getPrivate();
    }


}