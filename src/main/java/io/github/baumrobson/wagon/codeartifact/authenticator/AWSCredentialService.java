package io.github.baumrobson.wagon.codeartifact.authenticator;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.codeartifact.AWSCodeArtifactClient;
import com.amazonaws.services.codeartifact.model.GetAuthorizationTokenRequest;
import com.amazonaws.services.codeartifact.model.GetAuthorizationTokenResult;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.amazonaws.services.securitytoken.model.GetSessionTokenRequest;

public class AWSCredentialService implements AWSCredentialsProvider {

	private Regions region;
	private String accessKey;
	private String accessKeySecret;
	private AWSCredentials credentials;

	/**
	 * @return a {@link BasicSessionCredentials} instance with a valid authenticated session token
	 */
	public String getAuthorizationToken(String regionName, String accessKey, String accessKeySecret, String domainOwner, String domain) {
		this.region = Regions.fromName(regionName);
		this.accessKey = accessKey;
		this.accessKeySecret = accessKeySecret;

		GetAuthorizationTokenRequest request = new GetAuthorizationTokenRequest();
		request.withDomainOwner(domainOwner)
			.withDomain(domain);

		GetAuthorizationTokenResult result = AWSCodeArtifactClient.builder()
				.withRegion(this.region)
				.withCredentials(this)
				.build()
				.getAuthorizationToken(request);
		return result.getAuthorizationToken();
	}

	public AWSCredentials getCredentials() {
		if (this.credentials == null) {
			refresh();
		}
		return this.credentials;
	}

	public void refresh() {
		AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(
				new BasicAWSCredentials(this.accessKey, this.accessKeySecret));

		AWSSecurityTokenService stsClient = AWSSecurityTokenServiceClientBuilder.standard().withRegion(this.region)

				.withCredentials(credentialsProvider).build();

		// Start a new session for managing a service instance's bucket
		GetSessionTokenRequest getSessionTokenRequest = new GetSessionTokenRequest().withDurationSeconds(43200);

		// Get the session token for the service instance's bucket
		Credentials sessionCredentials = stsClient.getSessionToken(getSessionTokenRequest).getCredentials();

		this.credentials = new BasicSessionCredentials(sessionCredentials.getAccessKeyId(), sessionCredentials.getSecretAccessKey(),
				sessionCredentials.getSessionToken());
	}
}
