package com.sts.demo.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtRequestFilter(
	private val userDetailsService: UserDetailsService,
	private val jwtTokenUtil: JwtTokenUtil
) : OncePerRequestFilter() {
	override fun doFilterInternal(
		request: HttpServletRequest,
		response: HttpServletResponse,
		filterChain: FilterChain
	) {
		if (!request.servletPath.startsWith("/api")) {
			filterChain.doFilter(request, response)
			return
		}

		val authorizationHeader = request.getHeader("Authorization")

		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			val jwtToken = authorizationHeader.substring(7)
			try {
				val username = jwtTokenUtil.getUsernameFromToken(jwtToken)
				if (SecurityContextHolder.getContext().authentication == null) {
					if (jwtTokenUtil.isOAuth2Token(jwtToken)) {
						val authorities = jwtTokenUtil.getAuthoritiesFromToken(jwtToken)
						val authentication = UsernamePasswordAuthenticationToken(
							username, null, authorities
						)
						authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
						SecurityContextHolder.getContext().authentication = authentication
					} else {
						val userDetails = userDetailsService.loadUserByUsername(username)
						if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
							val authenticationToken = UsernamePasswordAuthenticationToken(
								userDetails, null, userDetails.authorities
							)
							authenticationToken.details = WebAuthenticationDetailsSource().buildDetails(request)
							SecurityContextHolder.getContext().authentication = authenticationToken
						}
					}
				}

			} catch (e: Exception) {
				println("JWT Token is invalid $e")
			}
		}

		filterChain.doFilter(request, response)
	}
}