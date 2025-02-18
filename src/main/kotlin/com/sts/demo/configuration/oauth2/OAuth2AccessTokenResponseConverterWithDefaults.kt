package com.sts.demo.configuration.oauth2

import org.springframework.core.convert.converter.Converter
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames

// Used for linkedInAccessTokenResponseClient
class OAuth2AccessTokenResponseConverterWithDefaults : Converter<Map<String, Any>, OAuth2AccessTokenResponse> {

    private val tokenResponseParameterNames: Set<String> = setOf(
        OAuth2ParameterNames.ACCESS_TOKEN,
        OAuth2ParameterNames.TOKEN_TYPE,
        OAuth2ParameterNames.EXPIRES_IN,
        OAuth2ParameterNames.REFRESH_TOKEN,
        OAuth2ParameterNames.SCOPE
    )

    private var defaultAccessTokenType: OAuth2AccessToken.TokenType = OAuth2AccessToken.TokenType.BEARER

    override fun convert(tokenResponseParameters: Map<String, Any>): OAuth2AccessTokenResponse {
        val accessToken = tokenResponseParameters[OAuth2ParameterNames.ACCESS_TOKEN] as? String ?: ""

        var accessTokenType = defaultAccessTokenType
        if (OAuth2AccessToken.TokenType.BEARER.value.equals(
                tokenResponseParameters[OAuth2ParameterNames.TOKEN_TYPE] as? String, ignoreCase = true
            )
        ) {
            accessTokenType = OAuth2AccessToken.TokenType.BEARER
        }

        var expiresIn: Long = 0
        if (tokenResponseParameters.containsKey(OAuth2ParameterNames.EXPIRES_IN)) {
            try {
                expiresIn = (tokenResponseParameters[OAuth2ParameterNames.EXPIRES_IN] as? Int)?.toLong() ?: 0
            } catch (ex: NumberFormatException) { }
        }

        var scopes: Set<String> = emptySet()
        tokenResponseParameters[OAuth2ParameterNames.SCOPE]?.let {
            scopes = (it as? String)?.split(" ")?.toSet() ?: emptySet()
        }

        val additionalParameters = tokenResponseParameters.filter {
            !tokenResponseParameterNames.contains(it.key)
        }

        return OAuth2AccessTokenResponse.withToken(accessToken)
            .tokenType(accessTokenType)
            .expiresIn(expiresIn)
            .scopes(scopes)
            .additionalParameters(additionalParameters)
            .build()
    }
}
