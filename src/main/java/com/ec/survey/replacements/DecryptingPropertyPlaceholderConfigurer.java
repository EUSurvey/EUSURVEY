package com.ec.survey.replacements;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;
import org.jasypt.properties.PropertyValueEncryptionUtils;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.io.IOException;
import java.security.Security;
import java.util.Properties;

/**
 * This class is for decrypting properties
 *
 * Is replaces EncryptablePropertySourcesPlaceholderConfigurer from jasypt-spring4
 * https://github.com/jasypt/jasypt/blob/master/jasypt-spring4/src/main/java/org/jasypt/spring4/properties/EncryptablePropertySourcesPlaceholderConfigurer.java
 *
 * It uses the password stored in the 'CAS_PBE_PASSWORD' ENV variable
 * Encrypted properties look like this:
 *  secretpassword=ENC(<BASE64 ENCODED+ENCRYPTED VALUE>)
 *  eg
 *  secretpassword=ENC(7w7ySGxc2Ez6NAD8PriKVTMjOiMRe5WtaCaKZCipNUc=)
 *
 * To encrypt a property call /eusurvey/administration/encrypt/{input}/{password}
 * and insert the value after the hash '#' into the properties
 */
public class DecryptingPropertyPlaceholderConfigurer extends PropertySourcesPlaceholderConfigurer {

    private final StandardPBEStringEncryptor encryptor;

    private boolean didConvert = false;

    public DecryptingPropertyPlaceholderConfigurer(){
        EnvironmentStringPBEConfig config = new EnvironmentStringPBEConfig();
        config.setAlgorithm("PBEWITHSHA256AND256BITAES-CBC-BC");
        config.setPasswordEnvName("CAS_PBE_PASSWORD");
        if (Security.getProvider("BC") == null){
            Security.addProvider(new BouncyCastleProvider());
        }
        config.setProviderName("BC");

        this.encryptor = new StandardPBEStringEncryptor();
        this.encryptor.setConfig(config);
    }

    @Override
    protected void convertProperties(Properties props) {
        if (!didConvert) {
            super.convertProperties(props);
            didConvert = true;
        }
    }

    @Override
    protected String convertPropertyValue(String originalValue) {
        //Checks whether the properties looks like ENC(.+)
        if (!PropertyValueEncryptionUtils.isEncryptedValue(originalValue)){
            return originalValue;
        }
        return PropertyValueEncryptionUtils.decrypt(originalValue, encryptor);
    }

    @Override
    protected Properties mergeProperties() throws IOException {
        Properties mergedProperties = super.mergeProperties();
        convertProperties(mergedProperties);
        return mergedProperties;
    }
}
