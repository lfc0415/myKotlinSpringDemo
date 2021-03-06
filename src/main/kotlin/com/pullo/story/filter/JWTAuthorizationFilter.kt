package com.pullo.story.filter

import com.pullo.story.util.JwtTokenUtils
import com.pullo.story.util.JwtTokenUtils.getUserRole
import com.pullo.story.util.JwtTokenUtils.getUsername
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthorizationFilter(authenticationManager: AuthenticationManager) :
    BasicAuthenticationFilter(authenticationManager) {
    @Throws(IOException::class, ServletException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain
    ) {
        val tokenHeader = request.getHeader(JwtTokenUtils.TOKEN_HEADER)
        // 如果请求头中没有Authorization信息则直接放行了
        if (tokenHeader == null || !tokenHeader.startsWith(JwtTokenUtils.TOKEN_PREFIX)) {
            chain.doFilter(request, response)
            return
        }
        // 如果请求头中有token，则进行解析，并且设置认证信息
        SecurityContextHolder.getContext().authentication = getAuthentication(tokenHeader)
        super.doFilterInternal(request, response, chain)
    }

    /**
     * 这里从token中获取用户信息并新建一个token
     */
    private fun getAuthentication(tokenHeader: String): UsernamePasswordAuthenticationToken? {
        val token = tokenHeader.replace(JwtTokenUtils.TOKEN_PREFIX, "")
        val username = getUsername(token)
        val role = getUserRole(token)
        return UsernamePasswordAuthenticationToken(username, null, setOf(SimpleGrantedAuthority(role)))
    }
}
