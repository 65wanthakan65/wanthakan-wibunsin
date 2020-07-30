/*
 * Copyright 2016 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.challenge.healthcheck.linecorp.login.infra.line.api.v2;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.function.Function;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import retrofit2.Call;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.challenge.healthcheck.linecorp.login.infra.http.Client;
import com.challenge.healthcheck.linecorp.login.infra.line.api.v2.response.AccessToken;
import com.challenge.healthcheck.linecorp.login.infra.line.api.v2.response.IdToken;
import com.challenge.healthcheck.linecorp.login.infra.line.api.v2.response.Verify;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.quickreply.QuickReply;
import com.linecorp.bot.model.response.BotApiResponse;
import com.auth0.jwt.exceptions.JWTDecodeException;

/**
 * <p>LINE v2 API Access</p>
 */
@Component
public class LineAPIService {

    private static final String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";
    private static final String CLIENT_CREDENTIALS = "client_credentials";
    private static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";
    private static final String BEARER = "Bearer ";
    

    @Value("${linecorp.platform.channel.channelId}")
    private String channelId;
    @Value("${linecorp.platform.channel.channelSecret}")
    private String channelSecret;
    @Value("${linecorp.platform.channel.callbackUrl}")
    private String callbackUrl;
    @Value("${linecorp.platform.channel.channelAccessToken}")
    private String channelAccessToken;
    @Value("${linecorp.platform.channel.userId}")
    private String userId;
    

    public AccessToken accessToken(String code) {
        return getClient(t -> t.accessToken(
                GRANT_TYPE_AUTHORIZATION_CODE,
                channelId,
                channelSecret,
                callbackUrl,
                code));
    }
    
    public AccessToken accessToken() {
        return getClient(t -> t.accessToken(
        		CLIENT_CREDENTIALS,
                channelId,
                channelSecret));
    }

    public AccessToken refreshToken(final AccessToken accessToken) {
        return getClient(t -> t.refreshToken(
                GRANT_TYPE_REFRESH_TOKEN,
                accessToken.refresh_token,
                channelId,
                channelSecret));
    }

    public Verify verify(final AccessToken accessToken) {
        return getClient(t -> t.verify(
                accessToken.access_token));
    }

    public void revoke(final AccessToken accessToken) {
        getClient(t -> t.revoke(
                accessToken.access_token,
                channelId,
                channelSecret));
    }

    public IdToken idToken(String id_token) {
        try {
            DecodedJWT jwt = JWT.decode(id_token);
            return new IdToken(
                    jwt.getClaim("iss").asString(),
                    jwt.getClaim("sub").asString(),
                    jwt.getClaim("aud").asString(),
                    jwt.getClaim("ext").asLong(),
                    jwt.getClaim("iat").asLong(),
                    jwt.getClaim("nonce").asString(),
                    jwt.getClaim("name").asString(),
                    jwt.getClaim("picture").asString());
        } catch (JWTDecodeException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void sendMsg(String msg) throws Exception{
        try {
        	final LineMessagingClient client = LineMessagingClient
        	        .builder(channelAccessToken)
        	        .build();

        	final TextMessage textMessage = new TextMessage(msg);
        	final PushMessage pushMessage = new PushMessage(userId,textMessage);

        	final BotApiResponse botApiResponse;
        	try {
        	    botApiResponse = client.pushMessage(pushMessage).get();
        	} catch (InterruptedException ex) {
        	    ex.printStackTrace();
        	    return;
        	}catch (ExecutionException  ex) {
        		 ex.printStackTrace();
         	    return;
			}
//        	System.out.println(botApiResponse);
        } catch (JWTDecodeException e) {
            throw new RuntimeException(e);
        }
    }


    public String getLineWebLoginUrl(String state, String nonce, List<String> scopes) {
        final String encodedCallbackUrl;
        final String scope = String.join("%20", scopes);

        try {
            encodedCallbackUrl = URLEncoder.encode(callbackUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        return "https://access.line.me/oauth2/v2.1/authorize?response_type=code"
                + "&client_id=" + channelId
                + "&redirect_uri=" + encodedCallbackUrl
                + "&state=" + state
                + "&scope=" + scope
                + "&nonce=" + nonce;
    }

    public boolean verifyIdToken(String id_token, String nonce) {
        try {
            JWT.require(
                Algorithm.HMAC256(channelSecret))
                .withIssuer("https://access.line.me")
                .withAudience(channelId)
                .withClaim("nonce", nonce)
                .acceptLeeway(60) // add 60 seconds leeway to handle clock skew between client and server sides.
                .build()
                .verify(id_token);
            return true;
        } catch (UnsupportedEncodingException e) {
            //UTF-8 encoding not supported
            return false;
        } catch (JWTVerificationException e) {
            //Invalid signature/claims
            return false;
        }
    }

    private <R> R getClient(final Function<LineAPI, Call<R>> function) {
        return Client.getClient("https://api.line.me/", LineAPI.class, function);
    }

}