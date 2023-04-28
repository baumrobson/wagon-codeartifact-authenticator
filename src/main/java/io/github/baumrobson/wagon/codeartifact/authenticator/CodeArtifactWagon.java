package io.github.baumrobson.wagon.codeartifact.authenticator;

import org.apache.maven.wagon.ConnectionException;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.authentication.AuthenticationInfo;
import org.apache.maven.wagon.providers.http.HttpWagon;
import org.apache.maven.wagon.proxy.ProxyInfoProvider;
import org.apache.maven.wagon.repository.Repository;

public class CodeArtifactWagon extends HttpWagon {

	private AWSCredentialService awsCredentialService = new AWSCredentialService();

	private String awsRegion;
	private String awsAccessKey;
	private String awsAccessKeySecret;
	private String awsDomain;
	private String awsDomainOwner;

	@Override
	public void connect(Repository repository, AuthenticationInfo originAuthenticationInfo,
            ProxyInfoProvider proxyInfoProvider) throws ConnectionException, AuthenticationException
	{
		AuthenticationInfo authenticationInfo = new AuthenticationInfo();
		authenticationInfo.setUserName(originAuthenticationInfo.getUserName());

        if (authenticationInfo.getUserName() == null) {
            if (repository.getUsername() != null) {
                authenticationInfo.setUserName(repository.getUsername());
                if (repository.getPassword() != null && authenticationInfo.getPassword() == null) {
                    authenticationInfo.setPassword(repository.getPassword());
                }
            }
        }

        if (authenticationInfo.getUserName() != null) {
    		String authorizationToken = awsCredentialService.getAuthorizationToken(awsRegion, awsAccessKey, awsAccessKeySecret, awsDomainOwner, awsDomain);
    		authenticationInfo.setPassword(authorizationToken);
        }
        super.connect(repository, authenticationInfo, proxyInfoProvider);
	}

	public String getAwsRegion() {
		return awsRegion;
	}

	public void setAwsRegion(String awsRegion) {
		this.awsRegion = awsRegion;
	}

	public String getAwsAccessKey() {
		return awsAccessKey;
	}

	public void setAwsAccessKey(String awsAccessKey) {
		this.awsAccessKey = awsAccessKey;
	}

	public String getAwsAccessKeySecret() {
		return awsAccessKeySecret;
	}

	public void setAwsAccessKeySecret(String awsAccessKeySecret) {
		this.awsAccessKeySecret = awsAccessKeySecret;
	}

	public String getAwsDomain() {
		return awsDomain;
	}

	public void setAwsDomain(String awsDomain) {
		this.awsDomain = awsDomain;
	}

	public String getAwsDomainOwner() {
		return awsDomainOwner;
	}

	public void setAwsDomainOwner(String awsDomainOwner) {
		this.awsDomainOwner = awsDomainOwner;
	}

}
