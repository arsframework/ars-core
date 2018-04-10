package ars.invoke.request;

import java.util.Set;
import java.util.Map;
import java.util.Date;
import java.util.HashMap;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.security.Key;
import java.io.Serializable;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.ExpiredJwtException;

import ars.util.Dates;
import ars.util.Formable;

/**
 * JWT令牌实现
 *
 * @author wuyongqiang
 */
public class Token implements Map<String, Object>, Formable, Serializable {
    /**
     * 令牌唯一标识符
     */
    public static final String CODE = "code";

    /**
     * 令牌过期时间标识符
     */
    public static final String TIMEOUT = "timeout";

    private static final long serialVersionUID = 1L;

    /**
     * 令牌秘钥
     */
    private static final byte[] SECRET = DatatypeConverter.parseBase64Binary("!@#json$%^web&*(token)");

    private String code; // 令牌标识
    private int timeout; // 过期时间（秒）
    private boolean valid; // 是否有效
    private Map<String, Object> attributes;

    public Token(String code) {
        if (code == null) {
            throw new IllegalArgumentException("Code must not be null");
        }
        this.code = code;
        this.attributes = new HashMap<String, Object>(0);
    }

    public Token(String code, int timeout) {
        if (code == null) {
            throw new IllegalArgumentException("Code must not be null");
        }
        if (timeout < 1) {
            throw new IllegalArgumentException("Timeout must not be less than 1, got " + timeout);
        }
        this.code = code;
        this.timeout = timeout;
        this.attributes = new HashMap<String, Object>(0);
    }

    public Token(String code, int timeout, Map<String, Object> attributes) {
        if (code == null) {
            throw new IllegalArgumentException("Code must not be null");
        }
        if (timeout < 1) {
            throw new IllegalArgumentException("Timeout must not be less than 1, got " + timeout);
        }
        if (attributes == null) {
            throw new IllegalArgumentException("Attributes must not be null");
        }
        this.code = code;
        this.valid = true;
        this.timeout = timeout;
        this.attributes = attributes;
    }

    /**
     * 解析令牌
     *
     * @param code 令牌标识
     * @return 令牌对象
     */
    public static Token parse(String code) {
        if (code == null) {
            throw new IllegalArgumentException("Code must not be null");
        }
        Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(code).getBody();
        int timeout = (int) (claims.getExpiration().getTime() - claims.getIssuedAt().getTime());
        return new Token(code, timeout, claims);
    }

    /**
     * 构建令牌
     *
     * @param issuer   颁发者
     * @param audience 颁发目标
     * @param timeout  过期时间（秒）
     * @return 令牌对象
     */
    public static Token build(String issuer, String audience, int timeout) {
        return build(issuer, audience, timeout, Collections.<String, Object>emptyMap());
    }

    /**
     * 构建令牌
     *
     * @param issuer     颁发者
     * @param audience   颁发目标
     * @param timeout    过期时间（秒）
     * @param attributes 令牌附加属性
     * @return 令牌对象
     */
    public static Token build(String issuer, String audience, int timeout, Map<String, Object> attributes) {
        if (issuer == null) {
            throw new IllegalArgumentException("Issuer must not be null");
        }
        if (audience == null) {
            throw new IllegalArgumentException("Audience must not be null");
        }
        if (timeout < 1) {
            throw new IllegalArgumentException("Timeout must not be less than 1, got " + timeout);
        }
        if (attributes == null) {
            throw new IllegalArgumentException("Attributes must not be null");
        }
        Date now = new Date();
        Date expiration = Dates.differ(now, Calendar.SECOND, timeout);
        Map<String, Object> _attributes = new HashMap<String, Object>(4 + attributes.size());
        _attributes.put(Claims.ISSUER, issuer);
        _attributes.put(Claims.AUDIENCE, audience);
        _attributes.put(Claims.ISSUED_AT, now.getTime());
        _attributes.put(Claims.EXPIRATION, expiration.getTime());
        JwtBuilder builder = Jwts.builder().setIssuer(issuer).setAudience(audience).setIssuedAt(now)
            .setExpiration(expiration);
        for (Entry<String, Object> entry : attributes.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (Claims.ID.equals(key) || Claims.ISSUER.equals(key) || Claims.AUDIENCE.equals(key)
                || Claims.SUBJECT.equals(key) || Claims.NOT_BEFORE.equals(key) || Claims.ISSUED_AT.equals(key)
                || Claims.EXPIRATION.equals(key)) {
                throw new RuntimeException("Invalid attribute key:" + key);
            }
            builder.claim(key, value);
            _attributes.put(key, value);
        }
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        Key key = new SecretKeySpec(SECRET, signatureAlgorithm.getJcaName());
        return new Token(builder.signWith(signatureAlgorithm, key).compact(), timeout, _attributes);
    }

    /**
     * 判断令牌是否有效
     *
     * @return true/false
     */
    public boolean isValid() {
        return this.valid;
    }

    /**
     * 令牌有效性验证
     *
     * @throws TokenInvalidException 令牌无效异常
     */
    public void validate() throws TokenInvalidException {
        if (!this.valid) {
            try {
                this.attributes = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(this.code).getBody();
            } catch (ExpiredJwtException e) {
                throw new TokenInvalidException("error.token.expired");
            } catch (Exception e) {
                throw new TokenInvalidException(e.getMessage());
            }
            this.valid = true;
        }
    }

    /**
     * 获取令牌标识
     *
     * @return 令牌标识
     */
    public String getCode() {
        return this.code;
    }

    /**
     * 获取令牌过期时间（秒）
     *
     * @return 过期时间
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * 获取令牌颁发者
     *
     * @return 颁发者
     */
    public String getIssuer() {
        return (String) this.attributes.get(Claims.ISSUER);
    }

    /**
     * 获取令牌颁发目标
     *
     * @return 颁发目标
     */
    public String getAudience() {
        return (String) this.attributes.get(Claims.AUDIENCE);
    }

    /**
     * 获取令牌颁发时间
     *
     * @return 颁发时间
     */
    public Date getReleased() {
        Integer timestamp = (Integer) this.attributes.get(Claims.ISSUED_AT);
        return timestamp == null ? null : new Date(timestamp);
    }

    /**
     * 获取令牌到期时间
     *
     * @return 到期时间
     */
    public Date getExpiration() {
        Integer timestamp = (Integer) this.attributes.get(Claims.EXPIRATION);
        return timestamp == null ? null : new Date(timestamp);
    }

    @Override
    public int size() {
        return this.attributes.size();
    }

    @Override
    public boolean isEmpty() {
        return this.attributes.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.attributes.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.attributes.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return this.attributes.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        return this.attributes.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return this.attributes.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> map) {
        this.attributes.putAll(map);
    }

    @Override
    public void clear() {
        this.attributes.clear();
    }

    @Override
    public Set<String> keySet() {
        return this.attributes.keySet();
    }

    @Override
    public Collection<Object> values() {
        return this.attributes.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return this.attributes.entrySet();
    }

    @Override
    public Map<String, Object> format() {
        Map<String, Object> values = new HashMap<String, Object>(2);
        values.put(CODE, this.code);
        values.put(TIMEOUT, this.timeout);
        return values;
    }

    @Override
    public String toString() {
        return this.code;
    }

}
