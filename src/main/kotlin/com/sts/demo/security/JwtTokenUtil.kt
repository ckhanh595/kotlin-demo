package com.sts.demo.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.security.Key
import java.util.Date

@Component
class JwtTokenUtil {

	@Value("\${jwt.secret}")
	private lateinit var secret: String

	@Value("\${jwt.expiration}")
	private var expiration: Long = 0

	private fun getSigningKey(): Key {
		val keyBytes = secret.toByteArray()
		return Keys.hmacShaKeyFor(keyBytes)
	}

	fun generateToken(userDetails: UserDetails): String {
		val claims = hashMapOf(
			"sub" to userDetails.username,
			"roles" to userDetails.authorities.joinToString { it.authority }
		)

		return createToken(claims, userDetails.username)
	}

	fun generateOAuth2Token(username: String, authorities: Collection<GrantedAuthority>): String {
		val claims: Map<String, Any> = hashMapOf(
			"sub" to username,
			"role" to authorities.joinToString(",") { it.authority },
			"oauth2" to true
		)
		return createToken(claims, username)
	}

	private fun createToken(claims: Map<String, Any>, username: String?): String {
		return Jwts.builder()
			.setClaims(claims)
			.setSubject(username)
			.setIssuedAt(Date(System.currentTimeMillis()))
			.setExpiration(Date(System.currentTimeMillis() + expiration * 1000))
			.signWith(getSigningKey(), SignatureAlgorithm.HS256)
			.compact()
	}

	fun getUsernameFromToken(token: String): String {
		return getClaimFromToken(token) { it.subject }
	}

	fun getExpirationDateFromToken(token: String): Date {
		return getClaimFromToken(token) { it.expiration }
	}

	private fun <T> getClaimFromToken(token: String, claimResolver: (Claims) -> T): T {
		val claims = getAllClaimsFromToken(token)
		return claimResolver(claims)
	}

	private fun getAllClaimsFromToken(token: String): Claims {
		return Jwts.parserBuilder()
			.setSigningKey(getSigningKey())
			.build()
			.parseClaimsJws(token)
			.body
	}

	fun validateToken(token: String, userDetails: UserDetails): Boolean {
		val username = getUsernameFromToken(token)
		return username == userDetails.username && !isTokenExpired(token)
	}

	private fun isTokenExpired(token: String): Boolean {
		val expiration = getExpirationDateFromToken(token)
		return expiration.before(Date())
	}

	fun isOAuth2Token(token: String): Boolean {
		return try {
			val claims = getAllClaimsFromToken(token)
			claims.containsKey("oauth2") && claims["oauth2"] as Boolean
		} catch (e: Exception) {
			false
		}
	}

	fun getAuthoritiesFromToken(token: String): Collection<GrantedAuthority> {
		val claims = getAllClaimsFromToken(token)
		val roleString = claims["role"] as String? ?: return emptyList()

		return roleString.split(",")
			.filter { it.isNotBlank() }
			.map { SimpleGrantedAuthority(it.trim()) }
	}

}