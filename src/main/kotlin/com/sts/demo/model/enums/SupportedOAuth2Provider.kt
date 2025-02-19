package com.sts.demo.model.enums

enum class SupportedOAuth2Provider(val providerName: String) {
	GOOGLE("google"),
	GITHUB("github"),
	FACEBOOK("facebook"),
	MICROSOFT("microsoft");

	companion object {
		private val enumMap: Map<String, SupportedOAuth2Provider> =
			entries.associateBy(SupportedOAuth2Provider::providerName)

		fun fromProviderName(providerName: String): SupportedOAuth2Provider {
			return enumMap[providerName] ?: throw IllegalArgumentException("Unsupported OAuth2 provider: $providerName")
		}
	}
}
