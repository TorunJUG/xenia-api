package pl.jug.torun.xenia.meetup

import org.apache.http.client.HttpClient
import org.apache.oltu.oauth2.client.OAuthClient
import org.apache.oltu.oauth2.client.URLConnectionClient
import org.apache.oltu.oauth2.client.request.OAuthClientRequest
import org.apache.oltu.oauth2.common.message.types.GrantType
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * Created by mephi_000 on 2014-09-21.
 */
@Component
class TokenRequester {
    @Value('${meetup.code:""}')
    String code

    @Value('${meetup.client_id:""}')
    String clientId

    @Value('${meetup.client_secret:""}')
    String clientSecret
    
    
    public String getAccessToken() {
        OAuthClientRequest request = OAuthClientRequest
                .tokenLocation("https://secure.meetup.com/oauth2/access")
                .setGrantType(GrantType.AUTHORIZATION_CODE)
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRedirectURI("http://localhost:8000/app/messaging-auth")
                .setCode(code)
                .buildQueryMessage();

        OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
        def tokenResponse = oAuthClient.accessToken(request)
        return tokenResponse.accessToken
        
        
    }


}
