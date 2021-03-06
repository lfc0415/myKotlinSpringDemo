package com.pullo.story.util

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.util.Date
import java.util.HashMap

object JwtTokenUtils {
    const val TOKEN_HEADER = "Authorization"
    const val TOKEN_PREFIX = "Bearer "
    private const val SECRET = "amars0ft"
    private const val ISS = "pullo"
    // 角色的key
    private const val ROLE_CLAIMS = "rol"
    // 过期时间是3600秒，既是1个小时
    private const val EXPIRATION = 3600L
    // 选择了记住我之后的过期时间为7天
    private const val EXPIRATION_REMEMBER = 604800L

    // 创建token
    fun createToken(username: String?, role: String, isRememberMe: Boolean): String {
        val expiration = if (isRememberMe) EXPIRATION_REMEMBER else EXPIRATION
        val map = HashMap<String, Any>()
        map[ROLE_CLAIMS] = role
        return Jwts.builder()
            .signWith(SignatureAlgorithm.HS512, SECRET)
            .setClaims(map)
            .setIssuer(ISS)
            .setSubject(username)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + expiration * 1000))
            .compact()
    }

    /**
     * 从token中获取用户名
      */
    fun getUsername(token: String): String {
        return getTokenBody(token).subject
    }

    /**
     * 获取用户角色
      */
    fun getUserRole(token: String): String {
        return getTokenBody(token)[ROLE_CLAIMS] as String
    }

    // 是否已过期
    fun isExpiration(token: String): Boolean {
        return getTokenBody(token).expiration.before(Date())
    }

    private fun getTokenBody(token: String): Claims {
        return Jwts.parser()
            .setSigningKey(SECRET)
            .parseClaimsJws(token)
            .body
    }
}
